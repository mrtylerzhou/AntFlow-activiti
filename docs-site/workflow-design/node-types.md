# 节点类型详解

> AntFlow 通过 12 种节点类型组合,表达从简单的串行审批到复杂的并行会签、条件路由、自动审批等各种工作流场景。

## 节点类型总览

AntFlow 的节点类型定义在 [NodeTypeEnum.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/constant/enums/NodeTypeEnum.java):

| Code | 枚举名 | 中文名 | hasPropertyTable | 说明 |
|---|---|---|:---:|---|
| 1 | NODE_TYPE_START | 发起人 | 0 | 流程起点,配置发起人权限 |
| 2 | NODE_TYPE_GATEWAY | 网关 | 0 | 条件分支容器 |
| 3 | NODE_TYPE_CONDITIONS | 条件 | 1 | 网关下的条件分支 |
| 4 | NODE_TYPE_APPROVER | 审批人 | 0 | 核心审批节点 |
| 5 | NODE_TYPE_OUT_SIDE_CONDITIONS | 接入方条件 | 1 | 三方系统的条件节点 |
| 6 | NODE_TYPE_COPY | 抄送V1 | 1 | 旧版抄送节点 |
| 7 | NODE_TYPE_PARALLEL_GATEWAY | 并行网关 | 0 | 并行分支容器 |
| 8 | NODE_TYPE_COPY_V2 | 抄送V2 | 0 | 新版抄送,支持运行时动态 |
| 9 | NODE_TYPE_AUTO_NODE | 自动节点 | 0 | 条件满足自动通过 |
| 12 | NODE_TYPE_CONDITION_APPROVE | 条件审批 | 0 | 条件满足自动通过,否则人工审批 |
| 13 | NODE_TYPE_CONDITION_COPY | 条件抄送 | 0 | 条件满足才抄送 |

::: warning 编号不连续
节点类型编号不连续(跳过 0、10、11)。10 和 11 在前端 [addNode.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/addNode.vue) 中存在,但它们是**组合节点**(一次性生成两个节点),后端 NodeTypeEnum 中没有对应枚举。
:::

`hasPropertyTable=1` 表示该节点类型拥有独立的属性表(条件节点、抄送V1、接入方条件)。其余节点配置统一存入 `t_bpmn_node.node_config_json` 字段。

## 节点类型分类

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 880 420" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <text x="440" y="24" text-anchor="middle" font-size="16" font-weight="700" fill="#1e293b">AntFlow 节点类型分类</text>

  <!-- 容器节点 -->
  <rect x="20" y="50" width="260" height="160" rx="10" fill="#eef2ff" stroke="#6366f1" stroke-width="1.5"/>
  <text x="150" y="75" text-anchor="middle" font-size="13" font-weight="700" fill="#4338ca">容器节点(网关)</text>
  <rect x="40" y="90" width="220" height="32" rx="5" fill="#fff" stroke="#6366f1"/>
  <text x="150" y="110" text-anchor="middle" font-size="11" fill="#1e293b">nodeType=2 条件网关</text>
  <rect x="40" y="130" width="220" height="32" rx="5" fill="#fff" stroke="#6366f1"/>
  <text x="150" y="150" text-anchor="middle" font-size="11" fill="#1e293b">nodeType=7 并行网关</text>
  <rect x="40" y="170" width="220" height="32" rx="5" fill="#fff" stroke="#6366f1"/>
  <text x="150" y="190" text-anchor="middle" font-size="11" fill="#1e293b">nodeType=3 条件分支</text>

  <!-- 审批类节点 -->
  <rect x="300" y="50" width="260" height="160" rx="10" fill="#fef3c7" stroke="#d97706" stroke-width="1.5"/>
  <text x="430" y="75" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">审批类节点</text>
  <rect x="320" y="90" width="220" height="32" rx="5" fill="#fff" stroke="#d97706"/>
  <text x="430" y="110" text-anchor="middle" font-size="11" fill="#1e293b">nodeType=1 发起人</text>
  <rect x="320" y="130" width="220" height="32" rx="5" fill="#fff" stroke="#d97706"/>
  <text x="430" y="150" text-anchor="middle" font-size="11" fill="#1e293b">nodeType=4 审批人</text>
  <rect x="320" y="170" width="220" height="32" rx="5" fill="#fff" stroke="#d97706"/>
  <text x="430" y="190" text-anchor="middle" font-size="11" fill="#1e293b">nodeType=12 条件审批</text>

  <!-- 抄送类节点 -->
  <rect x="580" y="50" width="280" height="160" rx="10" fill="#dbeafe" stroke="#3b82f6" stroke-width="1.5"/>
  <text x="720" y="75" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">抄送类节点</text>
  <rect x="600" y="90" width="240" height="32" rx="5" fill="#fff" stroke="#3b82f6"/>
  <text x="720" y="110" text-anchor="middle" font-size="11" fill="#1e293b">nodeType=6 抄送V1(旧版)</text>
  <rect x="600" y="130" width="240" height="32" rx="5" fill="#fff" stroke="#3b82f6"/>
  <text x="720" y="150" text-anchor="middle" font-size="11" fill="#1e293b">nodeType=8 抄送V2</text>
  <rect x="600" y="170" width="240" height="32" rx="5" fill="#fff" stroke="#3b82f6"/>
  <text x="720" y="190" text-anchor="middle" font-size="11" fill="#1e293b">nodeType=13 条件抄送</text>

  <!-- 自动类节点 -->
  <rect x="160" y="240" width="260" height="160" rx="10" fill="#dcfce7" stroke="#16a34a" stroke-width="1.5"/>
  <text x="290" y="265" text-anchor="middle" font-size="13" font-weight="700" fill="#166534">自动类节点</text>
  <rect x="180" y="280" width="220" height="32" rx="5" fill="#fff" stroke="#16a34a"/>
  <text x="290" y="300" text-anchor="middle" font-size="11" fill="#1e293b">nodeType=9 自动节点</text>
  <rect x="180" y="320" width="220" height="32" rx="5" fill="#fff" stroke="#16a34a"/>
  <text x="290" y="340" text-anchor="middle" font-size="11" fill="#1e293b">nodeType=12 条件审批</text>
  <rect x="180" y="360" width="220" height="32" rx="5" fill="#fff" stroke="#16a34a"/>
  <text x="290" y="380" text-anchor="middle" font-size="11" fill="#1e293b">nodeType=13 条件抄送</text>

  <!-- 三方接入 -->
  <rect x="460" y="240" width="260" height="160" rx="10" fill="#fce7f3" stroke="#db2777" stroke-width="1.5"/>
  <text x="590" y="265" text-anchor="middle" font-size="13" font-weight="700" fill="#9d174d">三方接入节点</text>
  <rect x="480" y="280" width="220" height="32" rx="5" fill="#fff" stroke="#db2777"/>
  <text x="590" y="300" text-anchor="middle" font-size="11" fill="#1e293b">nodeType=5 接入方条件</text>
  <rect x="480" y="320" width="220" height="50" rx="5" fill="#fff" stroke="#db2777"/>
  <text x="590" y="340" text-anchor="middle" font-size="11" fill="#1e293b">由外部系统通过 Open API</text>
  <text x="590" y="358" text-anchor="middle" font-size="11" fill="#1e293b">提供审批人和条件</text>
</svg>

## 设计期 vs 运行期

AntFlow 的一个关键设计是:**设计期保留语义化 nodeType,运行期统一转换为 nodeType=4(审批人节点)**,通过标签区分。

[NodeUtil.nodeSpecialProcess](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/util/NodeUtil.java) 处理转换:

| 设计期 nodeType | 运行期处理 | 标签/标记 | 虚拟审批人 |
|---|---|---|---|
| 8(抄送V2) | 转 4 | `isCarbonCopyNode=true` | `CC_NODE` |
| 9(自动节点) | 转 4 | `isAutomaticNode=true` | `AUTO_NODE_SKIP(-3)` |
| 12(条件审批) | 转 4 | `isConditionApproveNode=true` | **保留真实审批人** |
| 13(条件抄送) | 转 4 | `isConditionCopyNode=true` | 运行期设 `CC_NODE` |

### 节点标签

[NodeLabelConstants.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/vo/NodeLabelConstants.java) 定义了标签常量:

| 常量 | 标签值 | 说明 |
|---|---|---|
| `dynamicCondition` | `af_syslabel_dynamiccondition` | 动态条件节点 |
| `copyNode` | `af_syslabel_copynode` | 抄送节点V1 |
| `copyNodeV2` | `af_syslabel_copynodeV2` | 抄送节点V2 |
| `automaticNode` | `auto_node` | 自动节点 |
| `conditionApproveNode` | `condition_approve_node` | 条件审批节点 |
| `conditionCopyNode` | `condition_copy_node` | 条件抄送节点 |
| `prevNodeAppointed` | `af_syslabel_prev_node_appointed` | 上一节点指定审批人 |
| `appointNextNodeApprover` | `af_syslabel_appoint_next_node_approver` | 指定下一节点审批人 |

### 不可操作节点(NONE_OPERATIONAL_NODES)

部分节点**存在于 Activiti 引擎中但不可退回**:

```java
public static final List<BpmnNodeLabelVO> NONE_OPERATIONAL_NODES = Lists.newArrayList(
    copyNodeV2, automaticNode, conditionCopyNode
);
```

::: warning 条件审批节点(12)不在列表中
条件审批节点(`condition_approve_node`)**故意不在** NONE_OPERATIONAL_NODES 中,以支持任务召回。因为条件审批节点保留了真实审批人,可能需要人工处理。
:::

## 各节点类型详解

### nodeType=1:发起人节点

- **作用**:流程起点,标记发起人
- **配置**:发起人字段权限、发起人自选模块
- **特殊**:流程必须有且只有一个发起人节点,不可删除
- **抽屉**:[promoterDrawer.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/drawer/promoterDrawer.vue)

### nodeType=2:条件网关

- **作用**:条件分支容器,包含多个 `conditionNodes` 子分支
- **子节点**:每个分支是一个 nodeType=3 的条件节点
- **路由**:运行时根据条件评估结果,选择匹配的分支执行
- **特殊**:支持"动态条件"(isDynamicCondition=true),由发起人运行时手动选择路径
- **前端渲染**:`branch-wrap` 布局,支持添加/删除/排序条件分支

### nodeType=3:条件节点

- **作用**:网关下的条件分支,定义一组条件规则
- **配置**:条件组(AND/OR)、比较运算符、条件值
- **特殊**:支持"默认分支"(isDefault=1),所有条件都不满足时走默认分支
- **抽屉**:[conditionDrawer.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/drawer/conditionDrawer.vue)
- 详见 [条件规则](/workflow-design/condition-rules)

### nodeType=4:审批人节点(核心)

- **作用**:核心审批节点,配置审批人规则和会签类型
- **配置**:15 种审批人来源、会签类型(会签/或签/顺序会签)、字段权限
- **会签类型**:
  - `signType=1`:会签(全部同意才通过)
  - `signType=2`:或签(一人同意即通过)
  - `signType=3`:顺序会签(按顺序依次审批)
- **抽屉**:[approverDrawer.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/drawer/approverDrawer.vue)
- 详见 [审批人规则](/workflow-design/approver-rules)

### nodeType=6:抄送节点V1(旧版)

- **作用**:抄送通知,不需审批
- **配置**:抄送人列表(指定人员/角色)
- **存储**:有独立属性表(hasPropertyTable=1)
- **抽屉**:[copyerDrawer.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/drawer/copyerDrawer.vue)
- **建议**:新流程推荐使用 nodeType=8

### nodeType=7:并行网关

- **作用**:并行分支容器,所有分支同时执行,全部完成后汇合
- **子节点**:每个分支是一个 nodeType=4 的审批人节点
- **与条件网关的区别**:并行网关**不评估条件**,所有分支都会执行
- **前端渲染**:`branch-wrap` + `parallelNodes` 数组

### nodeType=8:抄送节点V2

- **作用**:新版抄送节点,支持运行时动态抄送
- **运行期**:转换为 nodeType=4 + `isCarbonCopyNode=true`,使用虚拟审批人 `CC_NODE`
- **不可退回**:在 NONE_OPERATIONAL_NODES 列表中
- **抽屉**:[copyerDrawerV2.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/drawer/copyerDrawerV2.vue)

### nodeType=9:自动节点

- **作用**:根据条件自动通过,无需人工审批
- **运行期**:转换为 nodeType=4 + `isAutomaticNode=true`,使用虚拟审批人 `AUTO_NODE_SKIP(-3)`
- **条件评估**:调用 `AbstractFormOperationAdaptor.automaticCondition()`,条件满足时自动 complete 任务
- **不可退回**:在 NONE_OPERATIONAL_NODES 列表中
- **抽屉**:[autoNodeDrawer.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/drawer/autoNodeDrawer.vue)

### nodeType=12:条件审批节点

- **作用**:条件满足时自动通过,**条件不满足时由真实审批人人工处理**
- **运行期**:转换为 nodeType=4 + `isConditionApproveNode=true`,**保留真实审批人**(不使用虚拟审批人)
- **条件评估**:
  - 条件为 `true`:自动 complete,写流转记录"条件审批自动通过"
  - 条件为 `false`/`null`:**不 complete**,留给真实审批人
- **可退回**:**不在** NONE_OPERATIONAL_NODES 中,支持任务召回
- **抽屉**:复用 approverDrawer.vue,额外显示"条件设置"tab

::: tip 与自动节点(9)的区别
| 特性 | 自动节点(9) | 条件审批(12) |
|---|---|---|
| 条件满足 | 自动通过 | 自动通过 |
| 条件不满足 | 自动通过(无条件时也通过) | **人工审批** |
| 审批人 | 虚拟(-3) | **真实审批人** |
| 可退回 | 否 | **是** |
| 副作用 | 调用 `automaticAction` | 不调用 |
:::

### nodeType=13:条件抄送节点

- **作用**:条件满足时才执行抄送,不满足时跳过
- **运行期**:转换为 nodeType=4 + `isConditionCopyNode=true`,使用 `CC_NODE`
- **条件评估**:
  - 无论条件结果如何都 complete 任务
  - 条件为 `true`:写 `BpmProcessForward` 抄送记录
  - 条件为 `false`/`null`:**不写抄送记录**
- **不可退回**:在 NONE_OPERATIONAL_NODES 列表中
- **抽屉**:复用 copyerDrawerV2.vue,带条件配置 tab

## 组合节点(10/11)

前端 [addNode.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/addNode.vue) 提供两个组合按钮:

### 办理节点(addType=10)

一次性生成两个审批人节点:
1. 自动节点(nodeType=9):自动通过
2. 发起人确认审批人节点(nodeType=4):由发起人指定审批人

适用于"先自动处理,再由发起人确认"的场景。

### 自动办理(addType=11)

生成:
1. 自动节点(nodeType=9):自动通过
2. 发起人确认审批人节点(nodeType=4):由发起人指定审批人

与"办理节点"类似,但顺序和配置不同。

## 节点配置 JSON 结构

所有节点配置统一存入 `t_bpmn_node.node_config_json` 字段,结构由 [BpmnNodeConfigJson](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/entity/jsonconf/BpmnNodeConfigJson.java) 定义:

```java
public class BpmnNodeConfigJson implements Serializable {
    private BpmnNodeApproverConfJson approverConf;       // 审批人配置
    private BpmnNodeConditionsConfJson conditionsConf;   // 条件配置
    private BpmnNodeButtonSignConfJson buttonSignConf;   // 按钮/标签/加签
    private BpmnNodeTemplateConfJson templateConf;       // 模板/提醒
    private BpmnNodeLowCodeConfJson lowCodeConf;         // 低代码字段控制
    private BpmnNodeAutoNodeConfJson autoNodeConf;       // 自动节点条件
    private Integer backType;                             // 驳回类型
}
```

这是 AntFlow 的 **JSON-first 读取策略**基础:节点配置统一存入 JSON 字段,减少 DB JOIN,提升读取性能。配置读写通过强类型 Java 对象,避免手写 JSON 解析。

## 下一步

- [审批人规则](/workflow-design/approver-rules) — 15 种审批人来源详解
- [条件规则](/workflow-design/condition-rules) — 条件评估机制
- [版本管理与启动](/workflow-design/version-management) — 流程发布与激活
