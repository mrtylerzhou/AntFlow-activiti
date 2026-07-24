# 虚拟节点系统

> AntFlow 全网首创的「虚拟节点(VNode)模式」:在设计期支持 12 种丰富的节点类型(发起人、审批人、条件、抄送、自动节点、条件审批、条件抄送等),但在运行期统一转换为 Activiti 原生支持的审批人节点(nodeType=4)。本章详解虚拟节点的转换机制与运行期处理逻辑。

## 设计动机

Activiti 原生只支持有限的节点类型:start、userTask、serviceTask、gateway 等。AntFlow 需要支持中国式办公场景下的丰富节点类型,但不希望改造 Activiti 内核。虚拟节点模式解决了这个矛盾:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 320" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr11" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 设计期:12 种节点 -->
  <rect x="20" y="20" width="430" height="280" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="235" y="44" text-anchor="middle" font-size="14" font-weight="700" fill="#1e40af">设计期:12 种节点类型</text>
  <text x="235" y="62" text-anchor="middle" font-size="11" fill="#1e3a8a">t_bpmn_node.node_type(NodeTypeEnum)</text>

  <rect x="40" y="80" width="180" height="36" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="130" y="103" text-anchor="middle" font-size="10" fill="#1e293b">1 发起人 START</text>

  <rect x="240" y="80" width="180" height="36" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="330" y="103" text-anchor="middle" font-size="10" fill="#1e293b">4 审批人 APPROVER</text>

  <rect x="40" y="124" width="180" height="36" rx="4" fill="#fef3c7" stroke="#d97706"/>
  <text x="130" y="147" text-anchor="middle" font-size="10" fill="#92400e">2 网关 GATEWAY</text>

  <rect x="240" y="124" width="180" height="36" rx="4" fill="#fef3c7" stroke="#d97706"/>
  <text x="330" y="147" text-anchor="middle" font-size="10" fill="#92400e">3 条件 CONDITIONS</text>

  <rect x="40" y="168" width="180" height="36" rx="4" fill="#dcfce7" stroke="#16a34a"/>
  <text x="130" y="191" text-anchor="middle" font-size="10" fill="#155e2f">6 抄送V1 COPY</text>

  <rect x="240" y="168" width="180" height="36" rx="4" fill="#dcfce7" stroke="#16a34a"/>
  <text x="330" y="191" text-anchor="middle" font-size="10" fill="#155e2f">8 抄送V2 COPY_V2</text>

  <rect x="40" y="212" width="180" height="36" rx="4" fill="#fce7f3" stroke="#db2777"/>
  <text x="130" y="235" text-anchor="middle" font-size="10" fill="#9d174d">9 自动节点 AUTO_NODE</text>

  <rect x="240" y="212" width="180" height="36" rx="4" fill="#fce7f3" stroke="#db2777"/>
  <text x="330" y="235" text-anchor="middle" font-size="10" fill="#9d174d">12 条件审批 CONDITION_APPROVE</text>

  <rect x="40" y="256" width="390" height="36" rx="4" fill="#fee2e2" stroke="#dc2626"/>
  <text x="235" y="279" text-anchor="middle" font-size="10" fill="#991b1b">13 条件抄送 CONDITION_COPY · 5 接入方条件 · 7 并行网关</text>

  <!-- 转换箭头 -->
  <line x1="450" y1="160" x2="490" y2="160" stroke="#475569" stroke-width="3" marker-end="url(#arr11)"/>
  <text x="470" y="148" text-anchor="middle" font-size="10" font-weight="700" fill="#475569">运行期</text>
  <text x="470" y="180" text-anchor="middle" font-size="10" font-weight="700" fill="#475569">转换</text>

  <!-- 运行期:统一审批人节点 -->
  <rect x="490" y="20" width="410" height="280" rx="8" fill="#fef9c3" stroke="#a16207"/>
  <text x="695" y="44" text-anchor="middle" font-size="14" font-weight="700" fill="#713f12">运行期:统一审批人节点</text>
  <text x="695" y="62" text-anchor="middle" font-size="11" fill="#422006">Activiti userTask(nodeType=4)</text>

  <rect x="520" y="100" width="100" height="60" rx="6" fill="#fff" stroke="#a16207"/>
  <text x="570" y="124" text-anchor="middle" font-size="11" font-weight="700" fill="#713f12">userTask</text>
  <text x="570" y="142" text-anchor="middle" font-size="9" fill="#422006">assignee=张三</text>

  <line x1="620" y1="130" x2="660" y2="130" stroke="#475569" stroke-width="2" marker-end="url(#arr11)"/>

  <rect x="660" y="100" width="100" height="60" rx="6" fill="#fff" stroke="#a16207"/>
  <text x="710" y="124" text-anchor="middle" font-size="11" font-weight="700" fill="#713f12">userTask</text>
  <text x="710" y="142" text-anchor="middle" font-size="9" fill="#422006">assignee=李四</text>

  <line x1="760" y1="130" x2="800" y2="130" stroke="#475569" stroke-width="2" marker-end="url(#arr11)"/>

  <rect x="800" y="100" width="80" height="60" rx="6" fill="#fff" stroke="#a16207"/>
  <text x="840" y="124" text-anchor="middle" font-size="11" font-weight="700" fill="#713f12">endEvent</text>

  <text x="695" y="220" text-anchor="middle" font-size="12" font-weight="700" fill="#713f12">所有节点统一为 userTask</text>
  <text x="695" y="240" text-anchor="middle" font-size="10" fill="#422006">条件节点 → exclusiveGateway + userTask</text>
  <text x="695" y="258" text-anchor="middle" font-size="10" fill="#422006">抄送节点 → userTask(CC_NODE虚拟assignee=-3)</text>
  <text x="695" y="276" text-anchor="middle" font-size="10" fill="#422006">自动节点 → userTask(条件满足自动完成)</text>
</svg>

## 节点类型枚举

[NodeTypeEnum](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/constant/enums/NodeTypeEnum.java):

| Code | 枚举名 | 中文名 | hasPropertyTable | 运行期处理 |
|:---:|---|---|:---:|---|
| 1 | NODE_TYPE_START | 发起人 | 0 | 转换为 userTask,assignee=发起人 |
| 2 | NODE_TYPE_GATEWAY | 网关 | 0 | 转换为 exclusiveGateway 容器 |
| 3 | NODE_TYPE_CONDITIONS | 条件 | 1 | 转换为 exclusiveGateway 分支 |
| 4 | NODE_TYPE_APPROVER | 审批人 | 0 | 原生 userTask |
| 5 | NODE_TYPE_OUT_SIDE_CONDITIONS | 接入方条件 | 1 | 同条件节点,来源三方系统 |
| 6 | NODE_TYPE_COPY | 抄送V1 | 1 | userTask + CC_NODE(-3) 虚拟assignee |
| 7 | NODE_TYPE_PARALLEL_GATEWAY | 并行网关 | 0 | parallelGateway 容器 |
| 8 | NODE_TYPE_COPY_V2 | 抄送V2 | 0 | 同 V1,支持运行时动态 |
| 9 | NODE_TYPE_AUTO_NODE | 自动节点 | 0 | userTask,条件满足自动 complete |
| 12 | NODE_TYPE_CONDITION_APPROVE | 条件审批 | 0 | userTask,条件满足自动 complete,否则人工审批 |
| 13 | NODE_TYPE_CONDITION_COPY | 条件抄送 | 0 | userTask + CC_NODE,条件满足才抄送 |

## 节点适配器:BpmnNodeAdaptor

每种节点类型对应一个 `BpmnNodeAdaptor` 实现,负责设计期配置持久化与运行期节点处理。位于 [adp/bpmnnodeadp/](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/bpmnnodeadp/):

```java
public abstract class BpmnNodeAdaptor {
    /** 节点配置持久化到 t_bpmn_node */
    public abstract void editBpmnNode(BpmnNodeVo nodeVo);

    /** 把 t_bpmn_node 转换为运行期 BpmnNodeVo */
    public abstract void formatToBpmnNodeVo(BpmnNodeVo nodeVo);

    /** 返回支持的节点类型 */
    public abstract Enum<?> getSupportNodeType();
}
```

主要实现类:

| 实现类 | 节点类型 | 职责 |
|---|---|---|
| `NodePropertyApproverAdp` | 4 审批人 | 处理审批人节点配置 |
| `NodePropertyRoleAdp` | 4 审批人(角色) | 角色审批人特殊处理 |
| `NodePropertyPersonnelAdp` | 4 审批人(指定人员) | 指定人员特殊处理 |
| `NodePropertyCustomizeAdp` | 4 审批人(发起人自选) | 自选审批人特殊处理 |
| `BpmnNodeAutoAdaptor` | 9 自动节点 | 自动节点配置 |
| `BpmnNodeConditionApproveAdaptor` | 12 条件审批 | 条件审批配置 |
| `BpmnNodeConditionCopyAdaptor` | 13 条件抄送 | 条件抄送配置 |
| `BpmnNodeCopyAdaptor` | 6/8 抄送 | 抄送节点配置 |
| `BpmnNodeGatewayAdaptor` | 2/3 网关/条件 | 条件分支配置 |
| `BpmnNodeStartAdaptor` | 1 发起人 | 发起人节点配置 |
| `BpmnNodeParallelAdaptor` | 7 并行网关 | 并行分支配置 |

## 运行期节点转换

[BpmnConfBizServiceImpl.startProcess](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/BpmnConfBizServiceImpl.java) 是节点转换的入口:

```java
public void startProcess(Long bpmnCode, BpmnStartConditionsVo startConditionsVo) {
    // 1. 读取流程定义 + 节点列表
    BpmnConfVo confVo = getBpmnConfVo(bpmnCode);
    List<BpmnNode> nodes = confVo.getNodes();

    // 2. 通过 BpmnNodeAdaptor 把每个节点配置转为运行期 VO
    List<BpmnNodeVo> nodeVos = nodes.stream()
            .map(node -> {
                BpmnNodeAdaptor adaptor = adaptorFactory.getBpmnNodeAdaptor(
                        NodeTypeEnum.getEnumByCode(node.getNodeType()));
                BpmnNodeVo nodeVo = convertToVo(node);
                adaptor.formatToBpmnNodeVo(nodeVo);
                return nodeVo;
            })
            .collect(Collectors.toList());

    // 3. 通过 PersonnelAdaptor 解析每个节点的实际审批人
    for (BpmnNodeVo nodeVo : nodeVos) {
        if (needResolveAssignee(nodeVo)) {
            PersonnelAdaptor pAdaptor = adaptorFactory.getPersonnelAdaptor(
                    NodePropertyEnum.getEnumByCode(nodeVo.getNodeProperty()));
            List<String> assignees = pAdaptor.getAssigneeList(
                    startConditionsVo.getVariables(), nodeVo);
            nodeVo.setAssignees(assignees);
        }
    }

    // 4. 通过 ConditionServiceImpl 评估条件节点,决定后续分支
    List<BpmnNodeVo> activeNodes = conditionService.filterNodesByCondition(nodeVos, startConditionsVo);

    // 5. BpmnDeduplicationFormatImpl 执行去重
    deduplicationFormat.format(activeNodes, confVo.getDeduplicationType());

    // 6. BpmnNodeFormatImpl 生成 Activiti 元素列表
    List<BpmnElement> elements = bpmnNodeFormatImpl.format(activeNodes);

    // 7. BpmnInsertVariablesImpl 写入 t_bpm_variable
    bpmnInsertVariablesImpl.insertVariables(confVo, startConditionsVo);

    // 8. BpmnCreateBpmnAndStartImpl 创建并启动 Activiti 实例
    bpmnCreateBpmnAndStartImpl.createBpmnAndStart(confVo, elements, startConditionsVo);
}
```

## 元素生成:BpmnNodeFormatImpl

[BpmnNodeFormatImpl](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/BpmnNodeFormatImpl.java) 把虚拟节点转换为 Activiti 元素:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 280" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr12" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 虚拟节点输入 -->
  <rect x="20" y="20" width="220" height="240" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="130" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">虚拟节点(BpmnNodeVo)</text>

  <rect x="40" y="60" width="180" height="36" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="130" y="82" text-anchor="middle" font-size="10" fill="#1e293b">发起人(nodeType=1)</text>

  <rect x="40" y="104" width="180" height="36" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="130" y="126" text-anchor="middle" font-size="10" fill="#1e293b">审批人(nodeType=4)</text>

  <rect x="40" y="148" width="180" height="36" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="130" y="170" text-anchor="middle" font-size="10" fill="#1e293b">条件分支(nodeType=2/3)</text>

  <rect x="40" y="192" width="180" height="36" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="130" y="214" text-anchor="middle" font-size="10" fill="#1e293b">自动/条件审批/抄送</text>

  <!-- BpmnNodeFormatImpl -->
  <rect x="280" y="100" width="220" height="80" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="390" y="124" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">BpmnNodeFormatImpl</text>
  <text x="390" y="142" text-anchor="middle" font-size="10" fill="#78350f">format(activeNodes)</text>
  <text x="390" y="158" text-anchor="middle" font-size="10" fill="#78350f">递归转换为元素列表</text>

  <!-- Activiti 元素输出 -->
  <rect x="540" y="20" width="360" height="240" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="720" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#155e2f">Activiti 元素(BpmnElement)</text>

  <rect x="560" y="60" width="120" height="36" rx="4" fill="#fff" stroke="#16a34a"/>
  <text x="620" y="82" text-anchor="middle" font-size="10" fill="#1e293b">startEvent</text>

  <rect x="700" y="60" width="180" height="36" rx="4" fill="#fff" stroke="#16a34a"/>
  <text x="790" y="82" text-anchor="middle" font-size="10" fill="#1e293b">userTask(assignee=发起人)</text>

  <rect x="560" y="104" width="120" height="36" rx="4" fill="#fff" stroke="#16a34a"/>
  <text x="620" y="126" text-anchor="middle" font-size="10" fill="#1e293b">exclusiveGateway</text>

  <rect x="700" y="104" width="180" height="36" rx="4" fill="#fff" stroke="#16a34a"/>
  <text x="790" y="126" text-anchor="middle" font-size="10" fill="#1e293b">sequenceFlow(condition)</text>

  <rect x="560" y="148" width="320" height="36" rx="4" fill="#fff" stroke="#16a34a"/>
  <text x="720" y="170" text-anchor="middle" font-size="10" fill="#1e293b">userTask(assignee=审批人)× N</text>

  <rect x="560" y="192" width="120" height="36" rx="4" fill="#fff" stroke="#16a34a"/>
  <text x="620" y="214" text-anchor="middle" font-size="10" fill="#1e293b">userTask(CC_NODE)</text>

  <rect x="700" y="192" width="180" height="36" rx="4" fill="#fff" stroke="#16a34a"/>
  <text x="790" y="214" text-anchor="middle" font-size="10" fill="#1e293b">endEvent</text>

  <!-- 箭头 -->
  <line x1="240" y1="140" x2="280" y2="140" stroke="#475569" stroke-width="2" marker-end="url(#arr12)"/>
  <line x1="500" y1="140" x2="540" y2="140" stroke="#475569" stroke-width="2" marker-end="url(#arr12)"/>
</svg>

## 特殊节点处理

### 抄送节点(nodeType=6/8)

抄送节点转换为 userTask,assignee 为虚拟用户 `CC_NODE`(-3)。流程流转到此节点时:

1. `BpmProcessForward` 表插入抄送记录
2. `BpmnNodeAdaptor` 自动 complete 任务,流程继续流转
3. 抄送对象通过 `bpm_process_forward` 表查询(type=9)

```java
// 抄送节点的 BpmnNodeAdaptor 处理
public class BpmnNodeCopyAdaptor extends BpmnNodeAdaptor {
    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo nodeVo) {
        // 设置虚拟 assignee
        nodeVo.setAssignees(Collections.singletonList("CC_NODE"));
        nodeVo.setAutoComplete(true);  // 自动完成
    }
}
```

### 自动节点(nodeType=9)

自动节点配置 `autoNodeConf` 含条件表达式,运行期:

1. 流程流转到此节点时,`AbstractFormOperationAdaptor.automaticCondition(vo)` 评估条件
2. 条件满足:`processComplete(task)` 自动完成,流程继续
3. 条件不满足:抛出异常或转人工(具体由 `automaticAction` 实现)

### 条件审批(nodeType=12)

根据项目 memory 中的硬约束:

- 条件审批节点必须保留真实审批人在 `nodeApproveList`,**不使用虚拟 approver(-3)**
- 条件审批节点**不得**加入 `NONE_OPERATIONAL_NODES`,以支持任务撤回
- 条件结果为 false/null 时,条件审批节点**不得**自动完成,等待人工审批

### 条件抄送(nodeType=13)

根据项目 memory:

- 条件抄送节点始终使用 `CC_NODE` 虚拟 assignee 自动完成,**无论条件结果**
- 条件结果为 false/null 时,条件抄送节点**不得**写 `BpmProcessForward` 记录

## 节点 Labels 系统

除了节点类型,AntFlow 还通过 **节点 Labels** 标识特殊运行期行为。Labels 存储在 `NodeExtraInfoDTO.nodeLabelVOS`:

| Label | 含义 | 运行期行为 |
|---|---|---|
| `af_syslabel_appoint_next_node_approver` | 指定下一节点审批人 | 节点追加「指定下一节点审批人」按钮 |
| `dynamic_condition_node` | 动态条件节点 | 表单修改时触发 `BpmnProcessMigrationService.migrateAndJumpToCurrent` 迁移 |
| `condition_approve_node` | 条件审批节点 | 条件满足自动完成 |
| `condition_copy_node` | 条件抄送节点 | 条件满足才抄送 |

Labels 通过 `NodeLabelConstants` 定义,前端 [formatdisplay_data.js](file:///d:/projects/jimuoffice/antflow-vue/src/utils/antflow/formatdisplay_data.js) 处理 nodeType=12/13 的特殊回显。

## 流程迁移:动态条件变更

当用户在审批过程中修改了影响条件分支的表单字段,需要触发流程迁移:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 240" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr13" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 用户审批 -->
  <rect x="20" y="20" width="160" height="60" rx="6" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="100" y="44" text-anchor="middle" font-size="11" font-weight="700" fill="#1e40af">用户审批</text>
  <text x="100" y="62" text-anchor="middle" font-size="10" fill="#1e3a8a">修改条件字段</text>

  <!-- 检测变更 -->
  <rect x="220" y="20" width="200" height="60" rx="6" fill="#fef3c7" stroke="#d97706"/>
  <text x="320" y="44" text-anchor="middle" font-size="11" font-weight="700" fill="#92400e">isDynamicConditionChanged</text>
  <text x="320" y="62" text-anchor="middle" font-size="10" fill="#78350f">检测表单字段变更</text>

  <!-- 迁移服务 -->
  <rect x="460" y="20" width="220" height="60" rx="6" fill="#dcfce7" stroke="#16a34a"/>
  <text x="570" y="44" text-anchor="middle" font-size="11" font-weight="700" fill="#155e2f">BpmnProcessMigrationService</text>
  <text x="570" y="62" text-anchor="middle" font-size="10" fill="#14532d">migrateAndJumpToCurrent</text>

  <!-- 写入迁移记录 -->
  <rect x="720" y="20" width="180" height="60" rx="6" fill="#fee2e2" stroke="#dc2626"/>
  <text x="810" y="44" text-anchor="middle" font-size="11" font-weight="700" fill="#991b1b">t_bpm_dynamic_condition_choosen</text>
  <text x="810" y="62" text-anchor="middle" font-size="10" fill="#7f1d1d">记录迁移选择</text>

  <!-- 流程实例迁移 -->
  <rect x="20" y="120" width="880" height="100" rx="8" fill="#f8fafc" stroke="#94a3b8"/>
  <text x="40" y="144" font-size="12" font-weight="700" fill="#1e293b">Activiti 流程实例迁移</text>
  <text x="40" y="166" font-size="11" fill="#475569">① 取当前流程定义 + 重新评估所有条件节点</text>
  <text x="40" y="186" font-size="11" fill="#475569">② 删除原后续节点的 ACT_RU_TASK</text>
  <text x="40" y="206" font-size="11" fill="#475569">③ 按新分支创建后续节点的 ACT_RU_TASK,assignee 重新解析</text>

  <!-- 箭头 -->
  <line x1="180" y1="50" x2="220" y2="50" stroke="#475569" stroke-width="1.5" marker-end="url(#arr13)"/>
  <line x1="420" y1="50" x2="460" y2="50" stroke="#475569" stroke-width="1.5" marker-end="url(#arr13)"/>
  <line x1="680" y1="50" x2="720" y2="50" stroke="#475569" stroke-width="1.5" marker-end="url(#arr13)"/>
  <line x1="570" y1="80" x2="460" y2="120" stroke="#475569" stroke-width="1.5" marker-end="url(#arr13)" stroke-dasharray="3 2"/>
</svg>

## 小结

- 虚拟节点模式让 AntFlow 在不改造 Activiti 内核的前提下支持 12 种丰富节点类型
- 运行期通过 `BpmnNodeAdaptor.formatToBpmnNodeVo` + `BpmnNodeFormatImpl.format` 把虚拟节点统一转换为 Activiti userTask
- 抄送节点使用 `CC_NODE`(-3)虚拟 assignee + 自动完成机制
- 自动节点/条件审批节点通过条件评估决定是否自动 complete
- 节点 Labels 系统标识特殊运行期行为(指定下一节点审批人、动态条件迁移等)
- 表单修改触发 `BpmnProcessMigrationService` 动态迁移流程实例

下一节 [流程流转控制](/dev-guide/flow-control) 介绍 Activiti 任务完成、节点跳转、流程迁移等底层控制机制。
