# BPMN 模型构建管线

> AntFlow 不直接写 BPMN XML，而是用一套 **JSON-first 的 VO 模型** 描述流程，再通过「格式化 → 元素化 → 引擎化」三步管线转换为 Activiti 能执行的 BPMN 模型。本章完整解析这条转换链。

## 一、管线全景图

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 520" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="a" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <text x="460" y="24" text-anchor="middle" font-size="15" font-weight="700" fill="#1e293b">BPMN 模型构建三段管线</text>

  <!-- 阶段1 -->
  <rect x="20" y="45" width="280" height="110" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="160" y="70" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">阶段 1: 格式化</text>
  <rect x="35" y="80" width="250" height="30" rx="4" fill="#fff" stroke="#93c5fd"/>
  <text x="160" y="100" text-anchor="middle" font-size="10" fill="#1e293b">BpmnPersonnelFormatImpl</text>
  <rect x="35" y="114" width="250" height="30" rx="4" fill="#fff" stroke="#93c5fd"/>
  <text x="160" y="134" text-anchor="middle" font-size="10" fill="#1e293b">BpmnRemoveFormat (去重/跳过/条件过滤)</text>
  <text x="160" y="155" text-anchor="middle" font-size="9" fill="#1e40af">输入: List&lt;BpmnNodeVo&gt; → 输出: 格式化后的 List&lt;BpmnNodeVo&gt;</text>

  <!-- 阶段2 -->
  <rect x="320" y="45" width="280" height="110" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="460" y="70" text-anchor="middle" font-size="13" font-weight="700" fill="#14532d">阶段 2: 元素化</text>
  <rect x="335" y="80" width="250" height="30" rx="4" fill="#fff" stroke="#86efac"/>
  <text x="460" y="100" text-anchor="middle" font-size="10" fill="#1e293b">BpmnNodeFormatImpl</text>
  <rect x="335" y="114" width="250" height="30" rx="4" fill="#fff" stroke="#86efac"/>
  <text x="460" y="134" text-anchor="middle" font-size="10" fill="#1e293b">BpmnElementAdaptor × 17</text>
  <text x="460" y="155" text-anchor="middle" font-size="9" fill="#14532d">输入: List&lt;BpmnNodeVo&gt; → 输出: List&lt;BpmnConfCommonElementVo&gt;</text>

  <!-- 阶段3 -->
  <rect x="620" y="45" width="280" height="110" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="760" y="70" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">阶段 3: 引擎化</text>
  <rect x="635" y="80" width="250" height="30" rx="4" fill="#fff" stroke="#fbbf24"/>
  <text x="760" y="100" text-anchor="middle" font-size="10" fill="#1e293b">BpmnAddFlowElementAdaptor × 11</text>
  <rect x="635" y="114" width="250" height="30" rx="4" fill="#fff" stroke="#fbbf24"/>
  <text x="760" y="134" text-anchor="middle" font-size="10" fill="#1e293b">BpmnBuildUtils → Activiti BpmnModel</text>
  <text x="760" y="155" text-anchor="middle" font-size="9" fill="#92400e">输入: List&lt;BpmnConfCommonElementVo&gt; → 输出: BpmnModel</text>

  <!-- 连线 -->
  <line x1="300" y1="100" x2="320" y2="100" stroke="#475569" stroke-width="2" marker-end="url(#a)"/>
  <line x1="600" y1="100" x2="620" y2="100" stroke="#475569" stroke-width="2" marker-end="url(#a)"/>

  <!-- 详细流程 -->
  <text x="460" y="180" text-anchor="middle" font-size="13" font-weight="700" fill="#1e293b">详细数据转换链</text>

  <rect x="20" y="200" width="880" height="300" rx="8" fill="#f8fafc" stroke="#94a3b8"/>

  <!-- 每行 -->
  <rect x="40" y="215" width="170" height="35" rx="4" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="125" y="237" text-anchor="middle" font-size="10" fill="#1e40af">t_bpmn_node (DB)</text>

  <rect x="240" y="215" width="170" height="35" rx="4" fill="#dcfce7" stroke="#16a34a"/>
  <text x="325" y="237" text-anchor="middle" font-size="10" fill="#14532d">BpmnNodeVo.list</text>

  <rect x="440" y="215" width="170" height="35" rx="4" fill="#fef3c7" stroke="#d97706"/>
  <text x="525" y="237" text-anchor="middle" font-size="10" fill="#92400e">BpmnConfCommonElementVo.list</text>

  <rect x="640" y="215" width="170" height="35" rx="4" fill="#fce7f3" stroke="#db2777"/>
  <text x="725" y="237" text-anchor="middle" font-size="10" fill="#9d174d">Activiti BpmnModel</text>

  <!-- 箭头 -->
  <line x1="210" y1="232" x2="240" y2="232" stroke="#475569" stroke-width="1.5" marker-end="url(#a)"/>
  <line x1="410" y1="232" x2="440" y2="232" stroke="#475569" stroke-width="1.5" marker-end="url(#a)"/>
  <line x1="610" y1="232" x2="640" y2="232" stroke="#475569" stroke-width="1.5" marker-end="url(#a)"/>

  <text x="420" y="225" text-anchor="middle" font-size="8" fill="#64748b">format</text>
  <text x="625" y="225" text-anchor="middle" font-size="8" fill="#64748b">toElements</text>
  <text x="740" y="225" text-anchor="middle" font-size="8" fill="#9d174d">buildModel</text>

  <!-- 中间件 -->
  <rect x="40" y="270" width="170" height="25" rx="3" fill="#e0e7ff" stroke="#6366f1"/>
  <text x="125" y="287" text-anchor="middle" font-size="9" fill="#4338ca">JsonConfUtil.parseNodeConfig</text>

  <rect x="240" y="270" width="170" height="25" rx="3" fill="#e0e7ff" stroke="#6366f1"/>
  <text x="325" y="287" text-anchor="middle" font-size="9" fill="#4338ca">IAdaptorFactory.getPersonnelAdaptor</text>

  <rect x="440" y="270" width="170" height="25" rx="3" fill="#e0e7ff" stroke="#6366f1"/>
  <text x="525" y="287" text-anchor="middle" font-size="9" fill="#4338ca">BpmnElementUtils.getSingleElement</text>

  <rect x="640" y="270" width="170" height="25" rx="3" fill="#e0e7ff" stroke="#6366f1"/>
  <text x="725" y="287" text-anchor="middle" font-size="9" fill="#4338ca">BpmnBuildUtils.createUserTask</text>

  <!-- 节点类型映射 -->
  <text x="40" y="325" font-size="10" font-weight="600" fill="#1e293b">节点类型映射:</text>

  <g transform="translate(40, 330)">
    <rect x="0" y="0" width="110" height="55" rx="4" fill="#e0e7ff" stroke="#6366f1"/>
    <text x="55" y="18" text-anchor="middle" font-size="9" fill="#4338ca">nodeType=4(审批人)</text>
    <text x="55" y="33" text-anchor="middle" font-size="9" fill="#4338ca">→ BpmnNodePropertyEnum</text>
    <text x="55" y="48" text-anchor="middle" font-size="9" fill="#4338ca">→ ElementPropertyEnum</text>
  </g>

  <g transform="translate(160, 330)">
    <rect x="0" y="0" width="110" height="55" rx="4" fill="#dcfce7" stroke="#16a34a"/>
    <text x="55" y="18" text-anchor="middle" font-size="9" fill="#14532d">nodeType=2(条件)</text>
    <text x="55" y="33" text-anchor="middle" font-size="9" fill="#14532d">→ ExclusiveGateway</text>
    <text x="55" y="48" text-anchor="middle" font-size="9" fill="#14532d">→ ConditionJudge</text>
  </g>

  <g transform="translate(280, 330)">
    <rect x="0" y="0" width="110" height="55" rx="4" fill="#fef3c7" stroke="#d97706"/>
    <text x="55" y="18" text-anchor="middle" font-size="9" fill="#92400e">nodeType=7(并行)</text>
    <text x="55" y="33" text-anchor="middle" font-size="9" fill="#92400e">→ ParallelGateway</text>
    <text x="55" y="48" text-anchor="middle" font-size="9" fill="#92400e">→ 多分支子链</text>
  </g>

  <g transform="translate(400, 330)">
    <rect x="0" y="0" width="110" height="55" rx="4" fill="#fce7f3" stroke="#db2777"/>
    <text x="55" y="18" text-anchor="middle" font-size="9" fill="#9d174d">nodeType=9(自动)</text>
    <text x="55" y="33" text-anchor="middle" font-size="9" fill="#9d174d">→ nodeType→4</text>
    <text x="55" y="48" text-anchor="middle" font-size="9" fill="#9d174d">+ autoNodeConf</text>
  </g>

  <g transform="translate(520, 330)">
    <rect x="0" y="0" width="130" height="55" rx="4" fill="#e0e7ff" stroke="#6366f1"/>
    <text x="65" y="18" text-anchor="middle" font-size="9" fill="#4338ca">nodeType=6/8(抄送)</text>
    <text x="65" y="33" text-anchor="middle" font-size="9" fill="#4338ca">→ 虚拟用户 -3</text>
    <text x="65" y="48" text-anchor="middle" font-size="9" fill="#4338ca">+ copyNode label</text>
  </g>

  <g transform="translate(660, 330)">
    <rect x="0" y="0" width="220" height="55" rx="4" fill="#10b981" stroke="#059669" fill-opacity="0.15"/>
    <text x="110" y="18" text-anchor="middle" font-size="9" fill="#047857">ElementPropertyEnum 路由表</text>
    <text x="110" y="33" text-anchor="middle" font-size="9" fill="#047857">Single→BpmnAddFlowElementSingleAdp</text>
    <text x="110" y="48" text-anchor="middle" font-size="9" fill="#047857">MultSign→BpmnAddFlowElementMultSignAdp</text>
  </g>

  <text x="460" y="518" text-anchor="middle" font-size="9" fill="#94a3b8">antflow-common: BpmnNodeFormatImpl + BpmnElementUtils · antflow-engine: ProcessModelServiceImpl + BpmnBuildUtils</text>
</svg>

---

## 二、阶段 1: 格式化（Personnel + Remove）

### 2.1 BpmnPersonnelFormatImpl — 审批人解析

从数据库查出 `BpmnNodeVo` 列表后，第一个处理就是**为每个节点解析实际的审批人**。

```java
// antflow-common/.../formatter/BpmnPersonnelFormatImpl.java
@Component
public class BpmnPersonnelFormatImpl implements BpmnPersonnelFormat {

    public void formatPersonnelsConf(BpmnConfVo bpmnConfVo,
                                     BpmnStartConditionsVo startConditionsVo) {

        List<BpmnNodeVo> nodes = bpmnConfVo.getNodes();
        for (BpmnNodeVo nodeVo : nodes) {
            if (nodeVo.getNodeType() == NodeTypeEnum.NODE_TYPE_APPROVER.getCode()) {

                // 1. 通过 SPI 获取审批人适配器
                AbstractBpmnPersonnelAdaptor adaptor = adaptorFactory
                    .getPersonnelAdaptor(NodePropertyEnum.getByCode(nodeVo.getNodeProperty()));

                // 2. 调用适配器解析审批人
                adaptor.setNodeParams(nodeVo, startConditionsVo, paramTypeEnum,
                    nextId, mapPreNodes, setAddNodes);

                // 3. 结果写入 BpmnNodeVo.params.assigneeList
                //    每个 assignee 包含: assignee(用户ID), assigneeName, elementName
            }
        }
    }
}
```

**关键点**：此阶段调用 `IAdaptorFactory.getPersonnelAdaptor()`，触发 PersonnelTagParser 的翻译 → SpringBeanUtils.getBean → 返回对应的审核人适配器。

### 2.2 BpmnRemoveFormat — 节点过滤

格式化完成后，根据业务规则**移除不应执行的节点**：

| 过滤器 | 职责 |
|---|---|
| `BpmnRemoveFormatImpl` | 通用条件过滤（动态条件分支中不满足的路径） |
| `BpmnRemoveDeduplicationFormatImpl` | 去重过滤（前向去重/后向去重中已出现过的审批人） |
| `BpmnRemoveSkipFormatImpl` | 跳过标记（`skippedAssignees` label） |

源码路径：
- `antflow-common/.../formatter/BpmnPersonnelFormatImpl.java`
- `antflow-common/.../formatter/BpmnRemoveFormatImpl.java`

---

## 三、阶段 2: 元素化（Node → Element）

### 3.1 BpmnNodeFormatImpl — 主编排器

这是管线中的核心组件，将 `List<BpmnNodeVo>` 转换为 `List<BpmnConfCommonElementVo>`：

```java
// antflow-common/.../adaptor/bpmnelementadp/BpmnNodeFormatImpl.java
@Component
public class BpmnNodeFormatImpl {

    public List<BpmnConfCommonElementVo> getBpmnConfCommonElementVoList(
            BpmnConfCommonVo bpmnConfCommonVo,
            List<BpmnNodeVo> nodes,
            BpmnStartConditionsVo startConditionsVo) {

        List<BpmnConfCommonElementVo> elements = Lists.newArrayList();

        // 1. 添加 Start Event
        elements.add(BpmnElementUtils.getStartEventElement(startElementId));

        // 2. 遍历节点，为每个节点调用对应的 BpmnElementAdaptor
        for (BpmnNodeVo node : nodes) {
            AbstractBpmnElementAdaptor adaptor = getBpmnElementAdaptor(node.getNodeProperty());
            List<BpmnConfCommonElementVo> nodeElements = adaptor.getElementVo(...);
            elements.addAll(nodeElements);
        }

        // 3. 处理 SequenceFlow 连线
        // 4. 添加 End Event
        elements.add(BpmnElementUtils.getEndEventElement(endElementId));

        // 5. 处理并行网关（Parallel Gateway）的嵌套子链
        // 6. 处理加签子元素（Sign-Up sub-elements）
        // 7. 设置按钮和标签
        return elements;
    }
}
```

### 3.2 BpmnElementAdaptor — 17 个实现

每个 `NodePropertyEnum` 对应一个 ElementAdaptor：

| Adaptor | 枚举 | 功能 |
|---|---|---|
| `NodePropertyPersonnelElmAdp` | PERSONNEL(5) | 指定人员节点 |
| `NodePropertyRoleElmAdp` | ROLE(4) | 指定角色节点 |
| `NodePropertyDirectLeaderElmAdp` | DIRECT_LEADER(13) | 直属领导节点 |
| `NodePropertyLevelElmAdp` | LEVEL(3) | 按级别层层审批 |
| `BpmnNodePropertyHrbpAdp` | HRBP(6) | HRBP 审批 |
| `BpmnNodePropertyStartUserElmAdp` | START_USER(12) | 发起人自选 |
| `BpmnNodePropertyLoopAdp` | LOOP(2) | 循环审批 |
| `BpmnNodePropertyCustomizeElmAdp` | CUSTOMIZE(7) | 自定义规则 |
| ... | ... | ... |

每个 Adaptor 的职责：将 `BpmnNodeVo` 转换为 1~N 个 `BpmnConfCommonElementVo`。

### 3.3 BpmnElementUtils — 元素工厂

所有 ElementAdaptor 最终都通过 `BpmnElementUtils` 的静态工厂方法构造 Element：

```java
// antflow-common/.../util/BpmnElementUtils.java
BpmnConfCommonElementVo getSingleElement(...)           // 单人审批
BpmnConfCommonElementVo getMultiplayerSignElement(...)  // 会签
BpmnConfCommonElementVo getMultiplayerOrSignElement(...)// 或签
BpmnConfCommonElementVo getMultiplayerSignInOrderElement(...) // 顺序会签
BpmnConfCommonElementVo getSignUpElement(...)           // 加签子元素
BpmnConfCommonElementVo getSequenceFlow(...)            // 连线
BpmnConfCommonElementVo getParallelGateWayElement(...)  // 并行网关
BpmnConfCommonElementVo getStartEventElement(...)       // 开始事件
BpmnConfCommonElementVo getEndEventElement(...)         // 结束事件
```

---

## 四、阶段 3: 引擎化（Element → BpmnModel）

### 4.1 ProcessModelServiceImpl — 终端组装器

```java
// antflow-engine/.../service/impl/ProcessModelServiceImpl.java
@Service
public class ProcessModelServiceImpl {

    public BpmnModel buildBpmnModel(BpmnConfCommonVo bpmnConfCommonVo) {
        List<BpmnConfCommonElementVo> elements = bpmnConfCommonVo.getElements();

        BpmnModel bpmnModel = new BpmnModel();
        Process process = new Process();
        process.setId(bpmnConfCommonVo.getProcessNum());

        // 遍历 Element，调用对应的 AddFlowElementAdaptor
        for (BpmnConfCommonElementVo element : elements) {
            ElementPropertyEnum property = element.getElementProperty();

            // 根据 ElementPropertyEnum 查找对应的 AddFlowElementAdaptor
            BpmnAddFlowElementAdaptor flowAdp = getAddFlowElementAdaptor(property);

            // 将 BpmnConfCommonElementVo 转为 Activiti FlowElement
            flowAdp.addFlowElement(element, process, startParamMap, startConditions);
        }

        // 运行 BPMN 自动布局
        SimpleBpmnFlowDesigner.design(bpmnModel);

        return bpmnModel;
    }
}
```

### 4.2 BpmnAddFlowElementAdaptor — 11 个实现

由 `ElementPropertyEnum` 的静态路由表决定：

| ElementPropertyEnum (code) | AddFlowElementAdaptor | 生成的 Activiti 元素 |
|---|---|---|
| SINGLE(1) | `BpmnAddFlowElementSingleAdp` | `UserTask` |
| MULTIPLAYER_SIGN(2) | `BpmnAddFlowElementMultSignAdp` | `UserTask` + `multiInstanceLoopCharacteristics` (全部同意) |
| MULTIPLAYER_ORSIGN(3) | `BpmnAddFlowElementMultOrSignAdp` | `UserTask` + multiInstance (一人同意) |
| MULTIPLAYER_SIGN_IN_ORDER(21) | `BpmnAddFlowElementSignInOrderAdp` | 多个串联 `UserTask` |
| LOOP(8) | `BpmnAddFlowElementLoopAdp` | 循环 `UserTask` |
| SIGN_UP_SERIAL(9) | `BpmnAddFlowElementSignUpSerialAdp` | 串行加签 `UserTask` 链 |
| SIGN_UP_PARALLEL(10) | `BpmnAddFlowElementMultSignAdp` | 并行加签 |
| SIGN_UP_PARALLEL_OR(11) | `BpmnAddFlowElementMultOrSignAdp` | 或签式加签 |
| EXCLUSIVE_GATEWAY(4) | (内置处理) | `ExclusiveGateway` |
| PARALLEL_GATEWAY(5) | (内置处理) | `ParallelGateway` |
| SEQUENCE_FLOW(6) | (内置处理) | `SequenceFlow` |

### 4.3 BpmnBuildUtils — Activiti 原语工厂

最终一步，将 `BpmnConfCommonElementVo` 中的数据填充到 Activiti 的 `BpmnModel` 对象：

```java
// antflow-common/.../util/BpmnBuildUtils.java
public class BpmnBuildUtils {
    // 自定义命名空间，用于在 BPMN 中嵌入 AntFlow 设计期节点 ID
    public static final String ANTFLOW_NAMESPACE = "http://antflow.org";

    // 创建基础 UserTask
    public static UserTask createUserTask(String id, String name,
                                           String assignee, String documentation);

    // 创建会签 UserTask（带 multiInstanceLoopCharacteristics）
    public static UserTask createSignUserTask(String id, String name,
            String assigneeList, String elementVariable, String loopCardinality);

    // 创建或签 UserTask
    public static UserTask createOrSignUserTask(...);

    // 创建顺序会签 UserTask（带 LoopCharacteristics + completionCondition）
    public static UserTask createLoopUserTask(...);

    // 设置 TaskListener（delegateExpression → bpmnTaskListener）
    public static void setTaskListener(UserTask userTask);

    // 设置 AntFlow 自定义扩展属性
    public static void setNodeIdAttribute(UserTask userTask, String nodeId);
}
```

`setNodeIdAttribute` 是管线的最后一步，它将 AntFlow 设计期的 `nodeId` 写入 Activiti 的 `UserTask` 作为 BPMN 扩展属性：

```xml
<userTask id="userTask_X" name="经理审批">
  <extensionElements>
    <antflow:nodeId>10001</antflow:nodeId>
  </extensionElements>
</userTask>
```

这让运行时可以通过 `nodeId` 回查数据库中的节点配置（审批人、条件、按钮等），无需在每次任务完成时都 JOIN 一次 `t_bpmn_node`。

---

## 五、完整调用入口

流程设计保存时，调用链如下：

```
前端 POST /bpmnConf/edit
  ↓
BpmnConfController.edit(bpmnConfVo)
  ↓
BpmnConfBizServiceImpl.edit(bpmnConfVo)
  ↓
BpmnPersonnelFormatImpl.formatPersonnelsConf()
  → 为每个节点解析实际审批人
  ↓
BpmnRemoveFormatImpl.removeBpmnConf()
  → 过滤不应执行的节点
  ↓
BpmnNodeFormatImpl.getBpmnConfCommonElementVoList()
  → 转换为 BpmnConfCommonElementVo 列表
  ↓
ProcessModelServiceImpl.buildBpmnModel()
  → BpmnAddFlowElementAdaptor → BpmnBuildUtils → BpmnModel
  → SimpleBpmnFlowDesigner.design() (BFS 自动布局)
  ↓
保存 BpmnModel 的 BPMN XML 到数据库
```

---

## 六、ElementPropertyEnum — 中央路由表

`ElementPropertyEnum` 是整个管线的中央路由表，将每个元素属性映射到具体的 `BpmnAddFlowElementAdaptor` 和 `BpmnInsertVariableSubs`：

```java
// antflow-common/.../constant/enus/ElementPropertyEnum.java
ELEMENT_PROPERTY_SINGLE(1, BpmnAddFlowElementSingleAdp.class,
    BpmnInsertVariableSubsMultiplayerSignAdp.class),
ELEMENT_PROPERTY_MULTIPLAYER_SIGN(2, BpmnAddFlowElementMultSignAdp.class,
    BpmnInsertVariableSubsMultiplayerSignAdp.class),
ELEMENT_PROPERTY_MULTIPLAYER_ORSIGN(3, BpmnAddFlowElementMultOrSignAdp.class,
    BpmnInsertVariableSubsMultiplayerOrSignAdp.class),
// ...
```

`ProcessModelServiceImpl` 通过 `ElementPropertyEnum.getByCode(code)` 找到对应的 adpClass，通过反射调用其 `addFlowElement()` 方法。这使得新增一个元素类型只需：
1. 添加新的 `ElementPropertyEnum` 值
2. 实现对应的 `BpmnAddFlowElementAdaptor`
3. 在 `BpmnBuildUtils` 中添加对应的工厂方法

---

## 下一步

- [虚拟节点系统](/dev-guide/vnode-system) — 理解为什么需要这条管线
- [适配器 SPI 体系全解](/dev-guide/spi-architecture) — 理解管线中的 SPI 调度
- [数据库设计](/dev-guide/db-design) — 理解 JSON 配置的存储结构
