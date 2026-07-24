# Adaptor 适配器模式

> 适配器模式是 AntFlow 的核心设计精髓,通过 4 个核心适配器接口把业务变化点与引擎执行完全解耦。本章深入剖析适配器的设计原理、注册机制、查找算法与扩展方式。

## 设计哲学

AntFlow 把 Activiti 工作流引擎的复杂性封装在底层,通过适配器把所有业务变化点抽象为可插拔的接口:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 380" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr8" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 业务变化点 -->
  <rect x="20" y="20" width="200" height="320" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="120" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">业务变化点</text>
  <text x="40" y="76" font-size="11" fill="#78350f">• 审批操作类型(30+种)</text>
  <text x="40" y="96" font-size="11" fill="#78350f">• 审批人来源(15种)</text>
  <text x="40" y="116" font-size="11" fill="#78350f">• 条件类型(10+种)</text>
  <text x="40" y="136" font-size="11" fill="#78350f">• 消息通道(邮件/短信/钉钉…)</text>
  <text x="40" y="156" font-size="11" fill="#78350f">• 表单类型(DIY/LF/外部)</text>
  <text x="40" y="176" font-size="11" fill="#78350f">• 节点类型(12种)</text>
  <text x="40" y="196" font-size="11" fill="#78350f">• 三方接入(SaaS)</text>

  <!-- 适配器层 -->
  <rect x="260" y="20" width="400" height="320" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="460" y="44" text-anchor="middle" font-size="14" font-weight="700" fill="#155e2f">Adaptor 适配器层(解耦核心)</text>

  <rect x="280" y="64" width="170" height="60" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="365" y="88" text-anchor="middle" font-size="11" font-weight="700" fill="#155e2f">ProcessOperationAdaptor</text>
  <text x="365" y="106" text-anchor="middle" font-size="9" fill="#14532d">30+ 审批操作实现</text>
  <text x="365" y="118" text-anchor="middle" font-size="9" fill="#14532d">按 operationType 路由</text>

  <rect x="470" y="64" width="170" height="60" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="555" y="88" text-anchor="middle" font-size="11" font-weight="700" fill="#155e2f">PersonnelAdaptor</text>
  <text x="555" y="106" text-anchor="middle" font-size="9" fill="#14532d">15 审批人来源实现</text>
  <text x="555" y="118" text-anchor="middle" font-size="9" fill="#14532d">按 nodeProperty 路由</text>

  <rect x="280" y="140" width="170" height="60" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="365" y="164" text-anchor="middle" font-size="11" font-weight="700" fill="#155e2f">ConditionJudge</text>
  <text x="365" y="182" text-anchor="middle" font-size="9" fill="#14532d">10+ 条件评估实现</text>
  <text x="365" y="194" text-anchor="middle" font-size="9" fill="#14532d">按 conditionType 路由</text>

  <rect x="470" y="140" width="170" height="60" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="555" y="164" text-anchor="middle" font-size="11" font-weight="700" fill="#155e2f">MessageSendAdaptor</text>
  <text x="555" y="182" text-anchor="middle" font-size="9" fill="#14532d">3 通道实现(可扩展)</text>
  <text x="555" y="194" text-anchor="middle" font-size="9" fill="#14532d">按 sendType 路由</text>

  <rect x="280" y="216" width="170" height="60" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="365" y="240" text-anchor="middle" font-size="11" font-weight="700" fill="#155e2f">FormOperationAdaptor</text>
  <text x="365" y="258" text-anchor="middle" font-size="9" fill="#14532d">DIY/LF/外部表单实现</text>
  <text x="365" y="270" text-anchor="middle" font-size="9" fill="#14532d">按 formCode 路由</text>

  <rect x="470" y="216" width="170" height="60" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="555" y="240" text-anchor="middle" font-size="11" font-weight="700" fill="#155e2f">BpmnNodeAdaptor</text>
  <text x="555" y="258" text-anchor="middle" font-size="9" fill="#14532d">12 节点类型实现</text>
  <text x="555" y="270" text-anchor="middle" font-size="9" fill="#14532d">按 nodeType 路由</text>

  <rect x="280" y="292" width="360" height="40" rx="6" fill="#fef9c3" stroke="#a16207"/>
  <text x="460" y="316" text-anchor="middle" font-size="11" font-weight="700" fill="#713f12">统一注册:AdaptorService + Spring @Component + isSupportBusinessObject</text>

  <!-- 引擎稳定层 -->
  <rect x="700" y="20" width="200" height="320" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="800" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">引擎稳定层</text>
  <text x="720" y="76" font-size="11" fill="#1e3a8a">• Activiti 5.23 引擎</text>
  <text x="720" y="96" font-size="11" fill="#1e3a8a">• MyBatis-Plus 持久层</text>
  <text x="720" y="116" font-size="11" fill="#1e3a8a">• AOP 拦截器</text>
  <text x="720" y="136" font-size="11" fill="#1e3a8a">• 工厂层(FormFactory)</text>
  <text x="720" y="156" font-size="11" fill="#1e3a8a">• 业务服务层</text>
  <text x="720" y="176" font-size="11" fill="#1e3a8a">• REST Controller</text>
  <text x="720" y="196" font-size="11" fill="#1e3a8a">• Activiti 表</text>
  <text x="720" y="216" font-size="11" fill="#1e3a8a">• AntFlow 业务表</text>
  <text x="720" y="252" font-size="11" font-weight="700" fill="#dc2626">稳定层不感知</text>
  <text x="720" y="270" font-size="11" font-weight="700" fill="#dc2626">业务变化</text>

  <!-- 箭头 -->
  <line x1="220" y1="180" x2="260" y2="180" stroke="#475569" stroke-width="2" marker-end="url(#arr8)"/>
  <line x1="660" y1="180" x2="700" y2="180" stroke="#475569" stroke-width="2" marker-end="url(#arr8)"/>
</svg>

## 4 大核心适配器

### 1. ProcessOperationAdaptor — 操作适配器

每个 `operationType` 对应一个实现,处理一种审批操作。

[adp/processoperation/](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processoperation/) 目录下 30+ 个实现类:

```java
public interface ProcessOperationAdaptor {
    /** 执行具体操作 */
    void doProcessButton(BusinessDataVo vo);

    /** 返回对应的操作类型枚举 */
    Enum<?> getOperationType();
}
```

完整列表见 [审批操作](/workflow-run/approve#按钮体系总览) 章节。

### 2. PersonnelAdaptor — 审批人适配器

把设计期配置的审批人规则解析为运行期实际 assignee。[adp/personneladp/](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/personneladp/) 目录下 16 个实现类。

### 3. ConditionJudge — 条件评估器

评估节点条件是否满足。[adp/conditionfilter/](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/conditionfilter/) 目录下 10+ 个实现类。

### 4. MessageSendAdaptor — 消息发送适配器

负责实际的消息发送。[adp/processnotice/](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processnotice/) 目录下 3 个实现类(邮件/短信/App推送)。

## 注册机制:AdaptorService

所有适配器实现统一的 [AdaptorService](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/interf/AdaptorService.java) 接口,通过枚举注册:

```java
public interface AdaptorService {
    /** 子类实现,声明支持哪些枚举 */
    void setSupportBusinessObjects();

    /** 注册到 SUPPORTED_BUSINESS ConcurrentHashMap,key 为 类全名+marker */
    default void addSupportBusinessObjects(Enum<?>... businessObjects) {
        for (Enum<?> bo : businessObjects) {
            SUPPORTED_BUSINESS.put(this.getClass().getName() + bo.name(), bo);
        }
    }

    /** 通过 == 匹配(枚举单例成立) */
    default boolean isSupportBusinessObject(Enum<?> businessObject) {
        return SUPPORTED_BUSINESS.containsValue(businessObject);
    }
}
```

### 注册流程

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 280" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr9" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 1. Spring 启动 -->
  <rect x="20" y="20" width="200" height="60" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="120" y="44" text-anchor="middle" font-size="12" font-weight="700" fill="#1e40af">Spring 启动</text>
  <text x="120" y="62" text-anchor="middle" font-size="10" fill="#1e3a8a">扫描 @Component Bean</text>

  <!-- 2. 适配器初始化 -->
  <rect x="260" y="20" width="240" height="60" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="380" y="44" text-anchor="middle" font-size="12" font-weight="700" fill="#155e2f">@PostConstruct</text>
  <text x="380" y="62" text-anchor="middle" font-size="10" fill="#14532d">调 setSupportBusinessObjects()</text>

  <!-- 3. 注册到 Map -->
  <rect x="540" y="20" width="240" height="60" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="660" y="44" text-anchor="middle" font-size="12" font-weight="700" fill="#92400e">SUPPORTED_BUSINESS</text>
  <text x="660" y="62" text-anchor="middle" font-size="10" fill="#78350f">ConcurrentHashMap&lt;String, Enum&gt;</text>

  <!-- 4. 运行期查找 -->
  <rect x="20" y="120" width="200" height="60" rx="8" fill="#e0e7ff" stroke="#4f46e5"/>
  <text x="120" y="144" text-anchor="middle" font-size="12" font-weight="700" fill="#3730a3">运行期查找</text>
  <text x="120" y="162" text-anchor="middle" font-size="10" fill="#312e81">TagParser.parseTag(enum)</text>

  <!-- 5. SpringBeanUtils.getBeans -->
  <rect x="260" y="120" width="240" height="60" rx="8" fill="#fce7f3" stroke="#db2777"/>
  <text x="380" y="144" text-anchor="middle" font-size="12" font-weight="700" fill="#9d174d">SpringBeanUtils.getBeans</text>
  <text x="380" y="162" text-anchor="middle" font-size="10" fill="#831843">遍历所有 Adaptor Bean</text>

  <!-- 6. isSupportBusinessObject -->
  <rect x="540" y="120" width="240" height="60" rx="8" fill="#fee2e2" stroke="#dc2626"/>
  <text x="660" y="144" text-anchor="middle" font-size="12" font-weight="700" fill="#991b1b">isSupportBusinessObject</text>
  <text x="660" y="162" text-anchor="middle" font-size="10" fill="#7f1d1d">== 匹配返回对应 Adaptor</text>

  <!-- 7. 返回实例 -->
  <rect x="20" y="220" width="760" height="40" rx="6" fill="#1e293b"/>
  <text x="400" y="244" text-anchor="middle" font-size="12" font-weight="700" fill="#fff">返回 Adaptor 实例 → 调 doProcessButton / getAssigneeList / judge / sendMessage</text>

  <!-- 箭头 -->
  <line x1="220" y1="50" x2="260" y2="50" stroke="#475569" stroke-width="1.5" marker-end="url(#arr9)"/>
  <line x1="500" y1="50" x2="540" y2="50" stroke="#475569" stroke-width="1.5" marker-end="url(#arr9)"/>
  <line x1="220" y1="150" x2="260" y2="150" stroke="#475569" stroke-width="1.5" marker-end="url(#arr9)"/>
  <line x1="500" y1="150" x2="540" y2="150" stroke="#475569" stroke-width="1.5" marker-end="url(#arr9)"/>
  <line x1="660" y1="180" x2="400" y2="220" stroke="#475569" stroke-width="1.5" marker-end="url(#arr9)" stroke-dasharray="4 3"/>
</svg>

## 关键 TagParser 实现

[PersonnelTagParser.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/tagparser/PersonnelTagParser.java):

```java
public class PersonnelTagParser {
    public AbstractBpmnPersonnelAdaptor parseTag(PersonnelEnum data) {
        // 扫描所有 AbstractBpmnPersonnelAdaptor 子类 Bean
        Collection<AbstractBpmnPersonnelAdaptor> beans =
                SpringBeanUtils.getBeans(AbstractBpmnPersonnelAdaptor.class);
        for (AbstractBpmnPersonnelAdaptor bean : beans) {
            if (bean.isSupportBusinessObject(data)) {
                return bean;   // 找到匹配的适配器
            }
        }
        return null;
    }
}
```

### IAdaptorFactory 接口

[IAdaptorFactory.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/factory/IAdaptorFactory.java) 通过注解声明适配器查找:

```java
public interface IAdaptorFactory {
    @SpfService(tagParser = PersonnelTagParser.class)
    AbstractBpmnPersonnelAdaptor getPersonnelAdaptor(NodePropertyEnum nodePropertyEnum);

    @AutoParse
    ProcessNoticeAdaptor getProcessNoticeAdaptor(MessageSendTypeEnum messageSendTypeEnum);

    @SpfService(tagParser = OperationTagParser.class)
    ProcessOperationAdaptor getProcessOperation(ProcessOperationEnum operationEnum);
    // ... 其他适配器查找方法
}
```

`@SpfService` / `@AutoParse` 由框架通过 Javassist 动态代理实现,运行期生成空实现并拦截调用,转发到对应 TagParser。

## FormFactory:表单适配器工厂

[FormFactory](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/factory/FormFactory.java) 是最重要的工厂,负责按 formCode 找到对应的 `FormOperationAdaptor`:

```java
public class FormFactory {

    /** 按 formCode 查找 FormAdaptor */
    public FormOperationAdaptor getFormAdaptor(BusinessDataVo vo) {
        String formCode = vo.getFormCode();
        // 通过 @ActivitiServiceAnno(svcName=formCode) 注解查找
        return SpringBeanUtils.getBeanByAnnotation(
                ActivitiServiceAnno.class,
                anno -> anno.svcName().equals(formCode));
    }

    /** 反序列化前端 Map 为 BusinessDataVo(核心解耦点) */
    public BusinessDataVo dataFormConversion(Map<String, Object> params, String formCode) {
        FormOperationAdaptor adaptor = getFormAdaptor(formCode);
        Class<? extends BusinessDataVo> voClass = adaptor.getVoClass();
        BusinessDataVo vo = JSON.parseObject(JSON.toJSONString(params), voClass);
        vo.setFormCode(formCode);
        return vo;
    }
}
```

## 适配器调用链示例:发起流程

完整调用链展示 4 个适配器如何协同工作:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 380" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr10" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 1. Controller -->
  <rect x="20" y="20" width="200" height="50" rx="6" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="120" y="40" text-anchor="middle" font-size="11" font-weight="700" fill="#1e40af">BpmnConfController</text>
  <text x="120" y="58" text-anchor="middle" font-size="9" fill="#1e3a8a">buttonsOperation(values, formCode)</text>

  <!-- 2. FormFactory -->
  <rect x="260" y="20" width="220" height="50" rx="6" fill="#dcfce7" stroke="#16a34a"/>
  <text x="370" y="40" text-anchor="middle" font-size="11" font-weight="700" fill="#155e2f">FormFactory</text>
  <text x="370" y="58" text-anchor="middle" font-size="9" fill="#14532d">dataFormConversion → BusinessDataVo</text>

  <!-- 3. AdaptorFactory -->
  <rect x="520" y="20" width="200" height="50" rx="6" fill="#fef3c7" stroke="#d97706"/>
  <text x="620" y="40" text-anchor="middle" font-size="11" font-weight="700" fill="#92400e">AdaptorFactory</text>
  <text x="620" y="58" text-anchor="middle" font-size="9" fill="#78350f">getProcessOperation(SUBMIT)</text>

  <!-- 4. SubmitProcessImpl -->
  <rect x="760" y="20" width="140" height="50" rx="6" fill="#fee2e2" stroke="#dc2626"/>
  <text x="830" y="40" text-anchor="middle" font-size="11" font-weight="700" fill="#991b1b">SubmitProcessImpl</text>
  <text x="830" y="58" text-anchor="middle" font-size="9" fill="#7f1d1d">doProcessButton(vo)</text>

  <!-- 5. FormAdaptor.submitData -->
  <rect x="20" y="100" width="220" height="50" rx="6" fill="#e0e7ff" stroke="#4f46e5"/>
  <text x="130" y="120" text-anchor="middle" font-size="11" font-weight="700" fill="#3730a3">FormAdaptor.submitData</text>
  <text x="130" y="138" text-anchor="middle" font-size="9" fill="#312e81">保存业务表单数据</text>

  <!-- 6. BpmnConfBizServiceImpl.startProcess -->
  <rect x="260" y="100" width="220" height="50" rx="6" fill="#fce7f3" stroke="#db2777"/>
  <text x="370" y="120" text-anchor="middle" font-size="11" font-weight="700" fill="#9d174d">BpmnConfBizServiceImpl</text>
  <text x="370" y="138" text-anchor="middle" font-size="9" fill="#831843">startProcess</text>

  <!-- 7. PersonnelAdaptor -->
  <rect x="520" y="100" width="200" height="50" rx="6" fill="#fef9c3" stroke="#a16207"/>
  <text x="620" y="120" text-anchor="middle" font-size="11" font-weight="700" fill="#713f12">PersonnelAdaptor</text>
  <text x="620" y="138" text-anchor="middle" font-size="9" fill="#422006">getAssigneeList(每个节点)</text>

  <!-- 8. ConditionJudge -->
  <rect x="760" y="100" width="140" height="50" rx="6" fill="#cffafe" stroke="#0891b2"/>
  <text x="830" y="120" text-anchor="middle" font-size="11" font-weight="700" fill="#155e75">ConditionJudge</text>
  <text x="830" y="138" text-anchor="middle" font-size="9" fill="#083344">judge(条件节点)</text>

  <!-- 9. Activiti 启动 -->
  <rect x="260" y="180" width="220" height="50" rx="6" fill="#f1f5f9" stroke="#475569"/>
  <text x="370" y="200" text-anchor="middle" font-size="11" font-weight="700" fill="#1e293b">BpmnCreateBpmnAndStartImpl</text>
  <text x="370" y="218" text-anchor="middle" font-size="9" fill="#475569">Activiti 启动流程实例</text>

  <!-- 10. ProcessorFactory -->
  <rect x="520" y="180" width="380" height="50" rx="6" fill="#fef3c7" stroke="#d97706"/>
  <text x="710" y="200" text-anchor="middle" font-size="11" font-weight="700" fill="#92400e">ProcessorFactory.executePostProcessors</text>
  <text x="710" y="218" text-anchor="middle" font-size="9" fill="#78350f">触发后处理器</text>

  <!-- 11. MessageSendAdaptor -->
  <rect x="520" y="260" width="380" height="50" rx="6" fill="#dcfce7" stroke="#16a34a"/>
  <text x="710" y="280" text-anchor="middle" font-size="11" font-weight="700" fill="#155e2f">MessageSendAdaptor</text>
  <text x="710" y="298" text-anchor="middle" font-size="9" fill="#14532d">@Async 异步发送邮件/短信/App 推送</text>

  <!-- 12. 数据库 -->
  <rect x="20" y="260" width="460" height="50" rx="6" fill="#fef9c3" stroke="#a16207"/>
  <text x="250" y="280" text-anchor="middle" font-size="11" font-weight="700" fill="#713f12">数据库:t_bpm_variable / bpm_business_process / bpm_verify_info / ACT_RU_*</text>
  <text x="250" y="298" text-anchor="middle" font-size="9" fill="#422006">写入流程实例、变量、审批历史、Activiti 任务</text>

  <!-- 13. 完成 -->
  <rect x="20" y="330" width="880" height="40" rx="6" fill="#1e293b"/>
  <text x="460" y="354" text-anchor="middle" font-size="12" font-weight="700" fill="#fff">流程实例创建完成,首个审批任务就绪</text>

  <!-- 箭头 -->
  <line x1="220" y1="45" x2="260" y2="45" stroke="#475569" stroke-width="1" marker-end="url(#arr10)"/>
  <line x1="480" y1="45" x2="520" y2="45" stroke="#475569" stroke-width="1" marker-end="url(#arr10)"/>
  <line x1="720" y1="45" x2="760" y2="45" stroke="#475569" stroke-width="1" marker-end="url(#arr10)"/>
  <line x1="830" y1="70" x2="130" y2="100" stroke="#475569" stroke-width="1" marker-end="url(#arr10)" stroke-dasharray="3 2"/>
  <line x1="240" y1="125" x2="260" y2="125" stroke="#475569" stroke-width="1" marker-end="url(#arr10)"/>
  <line x1="480" y1="125" x2="520" y2="125" stroke="#475569" stroke-width="1" marker-end="url(#arr10)"/>
  <line x1="720" y1="125" x2="760" y2="125" stroke="#475569" stroke-width="1" marker-end="url(#arr10)"/>
  <line x1="370" y1="150" x2="370" y2="180" stroke="#475569" stroke-width="1" marker-end="url(#arr10)"/>
  <line x1="480" y1="205" x2="520" y2="205" stroke="#475569" stroke-width="1" marker-end="url(#arr10)"/>
  <line x1="710" y1="230" x2="710" y2="260" stroke="#475569" stroke-width="1" marker-end="url(#arr10)" stroke-dasharray="3 2"/>
  <line x1="370" y1="230" x2="250" y2="260" stroke="#475569" stroke-width="1" marker-end="url(#arr10)" stroke-dasharray="3 2"/>
  <line x1="250" y1="310" x2="250" y2="330" stroke="#475569" stroke-width="1" marker-end="url(#arr10)"/>
  <line x1="710" y1="310" x2="460" y2="330" stroke="#475569" stroke-width="1" marker-end="url(#arr10)"/>
</svg>

## 扩展示例:新增 ProcessOperationAdaptor

以"暂停流程"操作为例,4 步完成扩展:

### 步骤 1:扩展枚举

```java
// 在 ProcessOperationEnum 中新增
PAUSE(39, "暂停流程", false);
```

### 步骤 2:实现适配器

```java
@Component
public class PauseProcessImpl implements ProcessOperationAdaptor {

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        // 1. 更新流程状态为"已暂停"
        bpmBusinessProcessService.updateProcessState(vo.getProcessNumber(), 8); // 8=PAUSED

        // 2. 暂停 Activiti 流程实例
        runtimeService.suspendProcessInstanceById(vo.getProcessInstanceId());

        // 3. 写审批历史
        bpmVerifyInfoService.add(BpmVerifyInfo.builder()
                .processNumber(vo.getProcessNumber())
                .verifyUserId(SecurityUtils.getLogInEmpId())
                .verifyStatus(PROCESS_PAUSE_TYPE)
                .verifyDesc(vo.getApprovalComment())
                .build());
    }

    @Override
    public Enum<?> getOperationType() {
        return ProcessOperationEnum.PAUSE;
    }
}
```

### 步骤 3:前端按钮配置

在 [utils/antflow/const.js](file:///d:/projects/jimuoffice/antflow-vue/src/utils/antflow/const.js) 添加:

```javascript
export const approvalButtonConf = {
  // ... 现有按钮
  39: { label: '暂停', type: 'warning', icon: 'VideoPause' },
};
```

### 步骤 4:节点配置开启按钮

在流程设计器中,于节点的 `buttonSignConf.operationTypes` 数组追加 `39`,前端按钮即出现。

> 无需修改任何工厂代码,Spring 启动时自动扫描 `@Component` 注册到 `SUPPORTED_BUSINESS`。

## 设计优势

| 优势 | 说明 |
|---|---|
| **开闭原则** | 新增操作/审批人来源/条件类型只需新增实现类,无需修改工厂 |
| **单一职责** | 每个适配器只处理一种业务,代码内聚 |
| **可测试性** | 适配器是无状态 Bean,易于单元测试 |
| **可扩展性** | 业务方自定义适配器无需修改 antflow 源码 |
| **运行期发现** | 通过 Spring 组件扫描,无需配置文件 |
| **O(1) 查找** | ConcurrentHashMap 缓存,查找性能极高 |

## 小结

- 4 大核心适配器(ProcessOperationAdaptor/PersonnelAdaptor/ConditionJudge/MessageSendAdaptor)+ 2 个辅助适配器(FormOperationAdaptor/BpmnNodeAdaptor)覆盖全部业务变化点
- 统一通过 `AdaptorService` 接口 + 枚举注册机制接入,新增 `@Component` Bean 即自动注册
- TagParser 通过 `SpringBeanUtils.getBeans` + `isSupportBusinessObject` 动态查找
- `FormFactory.dataFormConversion` 是核心解耦点,屏蔽 LF/DIY/外部表单差异
- 扩展只需 4 步:扩枚举 → 实现适配器 → 前端配置 → 节点开启按钮

下一节 [虚拟节点系统](/dev-guide/vnode-system) 介绍 AntFlow 自研节点类型如何在运行期转换为 Activiti 原生节点。
