# 适配器 SPI 体系全解

> AntFlow 的核心可扩展性基于一套**双派发 SPI 体系**：接口 + 注解 + TagParser + javassist 动态代理。本章从源码层面完整解析这套体系的设计原理、注册机制和调度流程。

## 一、设计目标

AntFlow 需要支持**无限扩展**业务变化点（审批人来源、条件类型、通知渠道等），但又不能每次都修改核心引擎代码。解决方案是一套支持「接口定义 → 注解声明 → 编译期生成代理」的 SPI 体系。

核心思想：
- **编译期绑定**而非运行期反射，性能接近硬编码
- **新增实现即生效**，Spring 自动扫描 + TagParser 自动路由
- **双派发机制**区分「按枚举分派」和「按规则匹配」两种模式

---

## 二、核心组件

### 2.1 IAdaptorFactory — 中央适配器工厂

所有适配器的入口接口，定义了 7 个获取方法：

```java
// antflow-engine/.../factory/IAdaptorFactory.java
public interface IAdaptorFactory {

    @SpfService(tagParser = ActivitiTagParser.class)
    FormOperationAdaptor getActivitiService(BusinessDataVo dataVo);

    @SpfService(tagParser = PersonnelTagParser.class)
    AbstractBpmnPersonnelAdaptor getPersonnelAdaptor(NodePropertyEnum nodePropertyEnum);

    @SpfService(tagParser = OrderedSignTagParser.class)
    AbstractOrderedSignNodeAdp getOrderedSignNodeAdp(SignTypeEnum signTypeEnum);

    @SpfService(tagParser = FormOperationTagParser.class)
    BpmnPersonnelFormat getBpmnPersonnelFormat(boolean isOutSideProcess);

    @SpfService(tagParser = BpmnNodeAdaptorTagParser.class)
    BpmnNodeAdaptor getBpmnNodeAdaptor(BpmnNodeAdpConfEnum adpConfEnum);

    @AutoParse
    AbstractBpmnElementAdaptor getBpmnElementAdaptor(NodePropertyEnum nodePropertyEnum);

    @AutoParse
    ProcessNoticeAdaptor getProcessNoticeAdaptor(MessageSendTypeEnum type);

    @AutoParse
    ConditionJudge getConditionJudge(String conditionTypeCode);
}
```

源码路径：`antflow-engine/src/main/java/org/openoa/engine/factory/IAdaptorFactory.java`

### 2.2 两种注解：@SpfService vs @AutoParse

| 特性 | @SpfService | @AutoParse |
|---|---|---|
| **分派方式** | TagParser 将参数 **翻译为 bean 名称** | 遍历所有实现类的 `isSupportBusinessObject(Enum)` |
| **代理生成** | javassist 字节码生成 | javassist 字节码生成 |
| **性能** | O(1) Map 查找 | O(n) 遍历实现类 |
| **适用场景** | 参数可直接映射为 bean 名的场景 | 参数需要逐个询问适配器是否支持 |
| **例子** | `NodePropertyEnum.ROLE` → `NodePropertyRoleAdp` | 遍历所有 `AbstractBpmnElementAdaptor` 找支持当前枚举的 |

### 2.3 TagParser — 参数翻译器

`TagParser<TBean, TParam>` 接口只定义了一个方法：

```java
// antflow-engine/.../factory/TagParser.java
public interface TagParser<TBean, TParam> {
    TBean parseTag(TParam data);
}
```

**六个内置实现：**

| TagParser | 输入参数 | 输出 | 逻辑 |
|---|---|---|---|
| `ActivitiTagParser` | `BusinessDataVo` | bean 名称 | 从 `vo.getFormCode()` 拿到 `FormOperationAdaptor` 的 Spring bean 名 |
| `PersonnelTagParser` | `NodePropertyEnum` | bean 名称 | 枚举 code → `PersonnelEnum` → bean 名映射 |
| `OrderedSignTagParser` | `SignTypeEnum` | bean 名称 | 会签类型 → 对应实现 |
| `FormOperationTagParser` | `boolean` | bean 名称 | 内部/外部流程 → 不同实现 |
| `BpmnNodeAdaptorTagParser` | `BpmnNodeAdpConfEnum` | bean 名称 | 节点适配器配置枚举 → bean 名 |
| `BpmnElementAdaptorTagParser` | — | — | 已注释掉，改走 @AutoParse |

以 `PersonnelTagParser` 为例：

```java
// antflow-engine/.../service/tagparser/PersonnelTagParser.java
public class PersonnelTagParser implements TagParser<String, NodePropertyEnum> {

    @Override
    public String parseTag(NodePropertyEnum nodePropertyEnum) {
        // NodePropertyEnum.ROLE → PersonnelEnum.ROLE → beanName: "NodePropertyRoleAdp"
        PersonnelEnum personnelEnum = PersonnelEnum.getPersonnelEnumByCode(
            nodePropertyEnum.getCode());
        // 返回 Spring bean 名称，代理层直接用 applicationContext.getBean(beanName)
        return personnelEnum.getAdaptorBeanName();
    }
}
```

每个 `PersonnelEnum` 枚举值都预先配好了对应的 bean 名：

```java
ROLE(4, "指定角色", "NodePropertyRoleAdp"),
PERSONNEL(5, "指定人员", "NodePropertyPersonnelAdp"),
DIRECT_LEADER(13, "直属领导", "NodePropertyDirectLeaderAdp"),
// ...
```

源码路径：
- `antflow-engine/.../factory/TagParser.java`
- `antflow-engine/.../service/tagparser/PersonnelTagParser.java`
- `antflow-base/.../constant/enums/PersonnelEnum.java`

---

## 三、javassist 动态代理生成

### 3.1 AdaptorFactoryProxy — @SpfService 代理生成器

这是整个 SPI 体系的技术核心——在 Spring 启动时用 javassist 字节码生成一个 `IAdaptorFactory` 的实现类，**每个方法编译为直接调用 TagParser + getBean 的字节码**。

```java
// antflow-engine/.../factory/AdaptorFactoryProxy.java
public class AdaptorFactoryProxy implements FactoryBean<IAdaptorFactory> {

    @Override
    public IAdaptorFactory getObject() throws Exception {
        ClassPool pool = ClassPool.getDefault();

        // 生成类：public class AdaptorFactoryProxy$Impl implements IAdaptorFactory
        CtClass cc = pool.makeClass("AdaptorFactoryProxy$Impl");
        cc.setInterfaces(new CtClass[]{pool.get(IAdaptorFactory.class.getName())});

        for (Method method : IAdaptorFactory.class.getDeclaredMethods()) {
            SpfService spfService = method.getAnnotation(SpfService.class);
            if (spfService == null) continue;

            // 为每个 @SpfService 方法生成字节码
            Class<? extends TagParser> parserClass = spfService.tagParser();
            String parserName = parserClass.getName();

            String methodBody =
                "{\n" +
                "    " + parserName + " parser = new " + parserName + "();\n" +
                "    Object beanOrName = parser.parseTag($1);\n" +     // ① TagParser 翻译
                "    if (beanOrName instanceof String) {\n" +
                "        return (" + returnType + ") SpringBeanUtils.getBean((String) beanOrName);\n" +  // ② getBean
                "    }\n" +
                "    return (" + returnType + ") beanOrName;\n" +
                "}";

            CtMethod ctMethod = CtNewMethod.make(methodBody, cc);
            cc.addMethod(ctMethod);
        }

        return (IAdaptorFactory) cc.toClass().newInstance();
    }
}
```

**执行流程：**

```
调用 adaptorFactory.getPersonnelAdaptor(NodePropertyEnum.ROLE)
  ↓
[字节码执行，无反射]
  new PersonnelTagParser()
  .parseTag(ROLE)
  → 返回 "NodePropertyRoleAdp" 字符串
  ↓
  SpringBeanUtils.getBean("NodePropertyRoleAdp")
  → 返回 NodePropertyRoleAdp 实例
```

源码路径：`antflow-engine/src/main/java/org/openoa/engine/factory/AdaptorFactoryProxy.java`

### 3.2 AutoParseProxyFactory — @AutoParse 代理生成器

`@AutoParse` 方法不走 TagParser，而是用 `AdaptorService.isSupportBusinessObject()` 模式：

```java
// antflow-engine/.../factory/AutoParseProxyFactory.java（伪代码）
public Object invoke(Object proxy, Method method, Object[] args) {
    if (!method.isAnnotationPresent(AutoParse.class)) return null;

    // 获取返回类型的所有 Spring bean
    Class<?> returnType = method.getReturnType();
    Map<String, ?> beans = SpringBeanUtils.getBeansOfType(returnType);

    for (Object bean : beans.values()) {
        if (bean instanceof AdaptorService) {
            AdaptorService adaptor = (AdaptorService) bean;
            // 逐个询问适配器是否支持当前参数
            if (adaptor.isSupportBusinessObject((Enum<?>) args[0])) {
                return bean;
            }
        }
    }
    return null;
}
```

执行流程：

```
调用 adaptorFactory.getConditionJudge("LF_STRING")
  ↓
[遍历所有 ConditionJudge 的 Spring bean]
  LFStringConditionJudge.isSupportBusinessObject(LF_STRING)?
  → 返回 true → 返回该实例
```

---

## 四、AdaptorService — 适配器自注册

每个适配器实现类都实现 `AdaptorService` 接口：

```java
// antflow-base/.../interf/AdaptorService.java
public interface AdaptorService {
    void setSupportBusinessObjects();
    default void addSupportBusinessObjects(Enum<?>... businessObjects) {
        // 把支持的枚举值注册到 Map<Enum, AdaptorService>
    }
    default boolean isSupportBusinessObject(Enum<?> businessObject) {
        // 判断当前适配器是否支持该枚举
    }
}
```

适配器在 Spring `@PostConstruct` 阶段自我注册：

```java
@Component
public class NodePropertyRoleAdp implements BpmnNodeAdaptor, AdaptorService {

    @PostConstruct
    public void init() {
        // 声明自己支持的 NodePropertyEnum 值
        addSupportBusinessObjects(NodePropertyEnum.ROLE);
    }

    @Override
    public boolean isSupportBusinessObject(Enum<?> businessObject) {
        return businessObject == NodePropertyEnum.ROLE;
    }
}
```

---

## 五、完整调度链路

以「流程发起 → 解析审批人」为例，展示整个 SPI 体系的运作：

```
1. 前端提交后 → BpmnConfController
     ↓
2. ProcessApprovalServiceImpl.buttonsOperation()
     ↓
3. FormFactory.getFormAdaptor(formCode)
     ↓ 通过 ActivitiTagParser 翻译 formCode → bean 名
     ↓ 代理层用 SpringBeanUtils.getBean(beanName)
     ↓ 返回对应的 FormOperationAdaptor 实现
     ↓
4. FormOperationAdaptor.launchParameters()  // 获取启动参数
     ↓
5. SubmitProcessImpl.doProcessButton()
     内部调用：
     ↓
6. BpmnPersonnelFormatImpl.formatPersonnelsConf()
     遍历每个节点，调用：
     ↓
7. IAdaptorFactory.getPersonnelAdaptor(node.getNodeProperty())
     ↓ PersonnelTagParser 翻译 NodePropertyEnum → bean 名
     ↓ 代理层 getBean → AbstractBpmnPersonnelAdaptor 实现
     ↓
8. AbstractBpmnPersonnelAdaptor.setNodeParams()
     调用：
     ↓
9. BpmnPersonnelProviderService.getAssigneeList()
     ↓ 具体的 Provider 实现（如指定人员/角色/直属领导）
     ↓ 返回 List<BpmnNodeParamsAssigneeVo>
     ↓
10. 结果写入 BpmnNodeVo.params.assigneeList
      → 后续由 BpmnNodeFormatImpl 转换为 Activiti BPMN 元素
```

---

## 六、三层分派架构总结

| 层级 | 组件 | 职责 | 技术 |
|---|---|---|---|
| **Layer 1: 接口定义** | `IAdaptorFactory` | 声明所有可获取的适配器类型 | 接口 + `@SpfService`/`@AutoParse` |
| **Layer 2: 参数翻译** | 6 个 `TagParser` | 将枚举/业务参数翻译为 bean 名称 | `parseTag(TParam)` 策略 |
| **Layer 3: 代理执行** | `AdaptorFactoryProxy` / `AutoParseProxyFactory` | javassist 生成字节码，O(1) 或 O(n) 查找 | 字节码生成 + `SpringBeanUtils.getBean` |

**关键设计决策：**

1. **编译期代理 vs 运行期反射**：javassist 在 Spring 启动时一次性生成字节码，运行时不走反射，性能等同于直接调用
2. **双派发策略**：参数可直接映射时用 O(1) 的 TagParser（适用于确定性的枚举映射）；参数不确定时用 O(n) 的 `@AutoParse`（适用于未知数量的扩展）
3. **Spring 原生整合**：所有适配器都是标准 Spring Bean，通过 `@Component` + `@PostConstruct` 自注册，零侵入

---

## 七、扩展新适配器

扩展一个全新的适配器类型只需 3 步：

### Step 1: 在 IAdaptorFactory 中声明获取方法

```java
// 在 IAdaptorFactory 接口中新增
@AutoParse  // 或 @SpfService(tagParser = MyNewTagParser.class)
MyNewAdaptor getMyNewAdaptor(MyNewEnum param);
```

### Step 2: 实现适配器和 TagParser（如果用 @SpfService）

```java
@Component
public class MyNewAdaptorImpl implements MyNewAdaptor, AdaptorService {
    @PostConstruct
    public void init() {
        addSupportBusinessObjects(MyNewEnum.TYPE_A, MyNewEnum.TYPE_B);
    }
}

@Component
public class MyNewTagParser implements TagParser<String, MyNewEnum> {
    @Override
    public String parseTag(MyNewEnum param) {
        return param.getBeanName(); // 或自定义逻辑
    }
}
```

### Step 3: 重新构建（代理在编译期生成，无需修改运行的代理代码）

`AdaptorFactoryProxy` 会在每次应用启动时重新扫描 `IAdaptorFactory` 的所有方法并生成字节码，**新增方法自动生效**——无需手动修改代理工厂代码。

---

## 下一步

- [Adaptor 适配器模式](/dev-guide/adaptor-pattern) — 理解 4 大核心适配器的设计理念
- [DIY 回调与事件机制](/dev-guide/diy-callback-events) — FormOperationAdaptor 完整生命周期
- [BPMN 模型构建管线](/dev-guide/bpmn-pipeline) — 从 Node VO 到 Activiti BPMN Model
