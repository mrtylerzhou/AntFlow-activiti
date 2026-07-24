# 节点类型 × 审批人映射矩阵

> AntFlow 用 **12 种节点类型** × **15 种审批人属性** × **17 种 BpmnElementAdaptor** × **16 种 Provider** 组成四层映射体系。本章以矩阵形式完整展示所有映射关系，作为快速查阅的手册。

## 一、四层结构

```
NodeTypeEnum (12种)        ← 前端设计器选什么类型的节点
    ↓
NodePropertyEnum (15种)    ← 审批人节点配什么类型的审批人
    ↓
BpmnElementAdaptor (17种)  ← 如何将 Node VO 转为 Element
    ↓
Provider (16种)            ← 实际从哪个数据源取审批人数据
```

---

## 二、NodeTypeEnum → NodePropertyEnum 第一层映射

| nodeType | 枚举 | 节点类型 | 可选审批人类型 | 备注 |
|:---:|---|---|---|---|
| 1 | NODE_TYPE_START | 发起人 | — | 无审批人，发起人节点 |
| 2 | NODE_TYPE_GATEWAY | 条件网关 | — | 条件分支，无审批人 |
| 3 | NODE_TYPE_CONDITIONS | 条件节点 | — | 条件判断 |
| 4 | NODE_TYPE_APPROVER | 审批人 | 全部 15 种 | **最常用节点** |
| 5 | NODE_TYPE_OUT_SIDE_CONDITIONS | 外部条件 | — | 三方条件 |
| 6 | NODE_TYPE_COPY | 抄送 V1 | 指定人员 | 抄送，虚拟用户 -3 |
| 7 | NODE_TYPE_PARALLEL_GATEWAY | 并行网关 | — | 并行分支 |
| 8 | NODE_TYPE_COPY_V2 | 抄送 V2 | 指定人员 | 运行时动态抄送 |
| 9 | NODE_TYPE_AUTO_NODE | 自动节点 | 条件自动 | 运行时转为 nodeType=4 |
| 12 | NODE_TYPE_CONDITION_APPROVE | 条件审批 | 全部 15 种 | 满足条件自动通过 |
| 13 | NODE_TYPE_CONDITION_COPY | 条件抄送 | 指定人员 | 满足条件才抄送 |

源码：`antflow-base/.../constant/enums/NodeTypeEnum.java`

---

## 三、NodePropertyEnum 全表（15 种）

| Code | 枚举 | 说明 | 参数类型 |
|:---:|---|---|---|
| 2 | LOOP | 层层审批（按组织层级逐级上批） | 单人 |
| 3 | LEVEL | 指定审批级别 | 单人 |
| 4 | ROLE | 指定角色 | 多人 |
| 5 | PERSONNEL | 指定人员 | 多人 |
| 6 | HRBP | HR 业务伙伴 | 单人 |
| 7 | CUSTOMIZE | 发起人自选 | 多人 |
| 8 | BUSINESS_TABLE | 业务表关联 | 多人 |
| 11 | OUT_SIDE_ACCESS | 外部系统接入 | 多人 |
| 12 | START_USER | 发起人自己 | 单人 |
| 13 | DIRECT_LEADER | 直属领导 | 单人 |
| 14 | DEPARTMENT_LEADER | 部门负责人 | 单人 |
| 15 | APPROVED_USERS | 已审批过的人 | 多人 |
| 16 | FORM_RELATED | 表单关联人员（8 子类型） | 多人 |
| 17 | UDR | 自定义规则 | 多人 |
| 18 | PREV_NODE_RELATED | 上节点相关人员（7 子类型） | 多人 |

源码：`antflow-base/.../constant/enums/NodePropertyEnum.java`

### 3.1 特殊类型详解

**FORM_RELATED (16)** 有 8 个子类型：

| 子类型 | 说明 |
|---|---|
| `FORM_DIRECT_LEADER` | 表单中指定字段用户的直属领导 |
| `FORM_DEPARTMENT_LEADER` | 表单中指定字段用户的部门负责人 |
| `FORM_SELF` | 表单中指定字段用户本人 |
| `FORM_LEADER_LEVEL_1` | 表单中指定字段用户的一级领导 |
| `FORM_LEADER_LEVEL_2` | 表单中指定字段用户的二级领导 |
| `FORM_LEADER_TOP` | 表单中指定字段用户的顶级领导 |
| `FORM_HRBP` | 表单中指定字段用户的 HRBP |
| `FORM_UP_LEVEL` | 表单中指定字段用户的向上 N 级 |

**PREV_NODE_RELATED (18)** 有 7 个子类型：上一节点的审批人、发起人、抄送人等。

---

## 四、NodePropertyEnum → BpmnElementAdaptor 映射

| NodePropertyEnum | BpmnElementAdaptor | 会签支持 |
|---|---|---|
| LOOP(2) | `BpmnNodePropertyLoopAdp` | 顺序会签 |
| LEVEL(3) | `NodePropertyLevelElmAdp` | 单人 |
| ROLE(4) | `NodePropertyRoleElmAdp` | 会签/或签 |
| PERSONNEL(5) | `NodePropertyPersonnelElmAdp` | 会签/或签 |
| HRBP(6) | `BpmnNodePropertyHrbpAdp` | 单人 |
| CUSTOMIZE(7) | `BpmnNodePropertyCustomizeElmAdp` | 会签/或签 |
| BUSINESS_TABLE(8) | `BpmnNodePropertyBusinessTableAdp` | 会签/或签 |
| OUT_SIDE_ACCESS(11) | `BpmnNodePropertyOutSideAccessAdp` | 会签/或签 |
| START_USER(12) | `BpmnNodePropertyStartUserElmAdp` | 单人 |
| DIRECT_LEADER(13) | `NodePropertyDirectLeaderElmAdp` | 单人 |
| DEPARTMENT_LEADER(14) | `NodePropertyDepartmentLeaderElmAdp` | 单人 |
| APPROVED_USERS(15) | `BpmnNodePropertyApprovedUsersAdp` | 会签/或签 |
| FORM_RELATED(16) | `BpmnNodePropertyFormRelatedAdp` | 按子类型 |
| UDR(17) | `BpmnNodePropertyUDRAdp` | 会签/或签 |
| PREV_NODE_RELATED(18) | `BpmnNodePropertyPrevNodeRelatedAdp` | 按子类型 |

注意：`BpmnNodeAdpConfEnum` 是进一步路由 ElementAdaptor 的开关配置，控制 nodeType=4/9/12 的不同行为。

---

## 五、Provider 解析链（最终取数层）

以 `PERSONNEL(5)` 为例，完整解析链路：

```
BpmnPersonnelFormatImpl.formatPersonnelsConf()
  ↓
PersonnelTagParser.parseTag(PERSONNEL)
  → "NodePropertyPersonnelAdp"
  ↓
NodePropertyPersonnelAdp.setNodeParams()
  ↓
AbstractBpmnPersonnelAdaptor.buildAssignees()
  ↓
BpmnPersonnelProviderService.getAssigneeList()
  ↓
NodePropertyPersonnelProvider (默认取 emplList 中的人员)
  ↓
返回 List<BpmnNodeParamsAssigneeVo>
```

各类 Provider 实现：

| 审批人类型 | Provider | 数据源 |
|---|---|---|
| PERSONNEL | `NodePropertyPersonnelProvider` | BpmnNodeVo.params.assigneeList |
| ROLE | `NodePropertyRoleProvider` | AfRoleService.queryUserByRoleIds() |
| DIRECT_LEADER | `NodePropertyDirectLeaderProvider` | AfUserService.queryEmployeeDirectLeaderByIds() |
| DEPARTMENT_LEADER | `NodePropertyDepartmentLeaderProvider` | AfUserService.queryDepartmentLeaderByIds() |
| HRBP | `NodePropertyHrbpProvider` | AfUserService.queryEmployeeHrpbByEmployeeIds() |
| LEVEL | `NodePropertyLevelProvider` | AfUserService.queryLeadersByEmployeeIdAndTier() |
| LOOP | `LoopPersonnelProvider` | 返回 null，由 BpmnLoopSignNodeAdp 单独处理 |
| START_USER | `NodePropertyStartUserProvider` | startConditionsVo.startUserId |
| CUSTOMIZE | `NodePropertyCustomizeProvider` | 运行时发起人选择，存入 t_bpm_variable |
| FORM_RELATED | `FormRelatedPersonnelProvider` | 表单字段值 → 用户查找 |
| UDR | `udrPersonnelProvider1` (约定 bean 名) | 自定义 Java 逻辑 |
| PREV_NODE_RELATED | `PrevNodeRelatedPersonnelProvider` | 查前序节点的审批人列表 |

---

## 六、会签类型 × Element 映射

| SignType (code) | 含义 | ElementPropertyEnum | 生成的 BPMN 特征 |
|:---:|---|---|---|
| SIGN_TYPE_SIGN(1) | 会签（全部同意） | MULTIPLAYER_SIGN(2) | `multiInstanceLoopCharacteristics` + `completionCondition` (nrOfCompletedInstances == nrOfInstances) |
| SIGN_TYPE_OR_SIGN(2) | 或签（一人同意） | MULTIPLAYER_ORSIGN(3) | `multiInstanceLoopCharacteristics` + `completionCondition` (nrOfCompletedInstances >= 1) |
| SIGN_TYPE_SIGN_IN_ORDER(3) | 顺序会签 | MULTIPLAYER_SIGN_IN_ORDER(21) | 生成 N 个串联 UserTask，每人完成后链到下一个 |

---

## 七、快速查阅：完整矩阵

| NodeType | NodeProperty | ElementAdaptor | Provider | SignType 支持 |
|:---:|:---:|---|---|---|
| 4 | PERSONNEL(5) | NodePropertyPersonnelElmAdp | NodePropertyPersonnelProvider | 1/2 |
| 4 | ROLE(4) | NodePropertyRoleElmAdp | NodePropertyRoleProvider | 1/2 |
| 4 | DIRECT_LEADER(13) | NodePropertyDirectLeaderElmAdp | NodePropertyDirectLeaderProvider | — |
| 4 | DEPARTMENT_LEADER(14) | NodePropertyDepartmentLeaderElmAdp | NodePropertyDepartmentLeaderProvider | — |
| 4 | HRBP(6) | BpmnNodePropertyHrbpAdp | NodePropertyHrbpProvider | — |
| 4 | LEVEL(3) | NodePropertyLevelElmAdp | NodePropertyLevelProvider | — |
| 4 | LOOP(2) | BpmnNodePropertyLoopAdp | LoopPersonnelProvider (null) | 3 (强制顺序) |
| 4 | START_USER(12) | BpmnNodePropertyStartUserElmAdp | NodePropertyStartUserProvider | — |
| 4 | CUSTOMIZE(7) | BpmnNodePropertyCustomizeElmAdp | NodePropertyCustomizeProvider | 1/2 |
| 4 | BUSINESS_TABLE(8) | BpmnNodePropertyBusinessTableAdp | NodePropertyBusinessTableProvider | 1/2 |
| 4 | APPROVED_USERS(15) | BpmnNodePropertyApprovedUsersAdp | NodePropertyApprovedUsersProvider | 1/2 |
| 4 | FORM_RELATED(16) | BpmnNodePropertyFormRelatedAdp | FormRelatedPersonnelProvider | 按子类型 |
| 4 | UDR(17) | BpmnNodePropertyUDRAdp | udrPersonnelProvider1 | 1/2 |
| 4 | PREV_NODE_RELATED(18) | BpmnNodePropertyPrevNodeRelatedAdp | PrevNodeRelatedPersonnelProvider | 按子类型 |
| 9 | (自动节点) | BpmnNodePropertyAutoAdp | — | — |
| 12 | (条件审批) | BpmnNodePropertyConditionApproveAdp | (继承自 4) | 1/2 |

---

## 下一步

- [审批人规则详解](/workflow-design/approver-rules) — 前端配置与 JSON 格式
- [扩展审批人来源](/dev-guide/extend-approver) — 新增自定义审批人类型
- [适配器 SPI 体系全解](/dev-guide/spi-architecture) — 理解 TagParser 路由机制
