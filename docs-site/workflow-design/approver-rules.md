# 审批人规则

> AntFlow 通过**适配器 + 策略模式**支持 15 种审批人来源,覆盖中国式办公的所有常见场景。每种来源有独立的 Provider 实现解析逻辑,新增来源只需实现一个接口。

## 审批人规则总览

AntFlow 的审批人类型定义在 [NodePropertyEnum.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/constant/enums/NodePropertyEnum.java):

| # | Code | 枚举值 | 描述 | 参数类型 |
|---|:---:|---|---|---|
| 1 | 2 | NODE_PROPERTY_LOOP | 层层审批 | SINGLE |
| 2 | 3 | NODE_PROPERTY_LEVEL | 指定层级审批 | SINGLE |
| 3 | 4 | NODE_PROPERTY_ROLE | 指定角色 | MULTIPLAYER |
| 4 | 5 | NODE_PROPERTY_PERSONNEL | 指定人员 | MULTIPLAYER |
| 5 | 6 | NODE_PROPERTY_HRBP | HRBP | MULTIPLAYER |
| 6 | 7 | NODE_PROPERTY_CUSTOMIZE | 发起人自选 | MULTIPLAYER |
| 7 | 8 | NODE_PROPERTY_BUSINESSTABLE | 关联业务表 | MULTIPLAYER |
| 8 | 11 | NODE_PROPERTY_OUT_SIDE_ACCESS | 外部传入人员 | MULTIPLAYER |
| 9 | 12 | NODE_PROPERTY_START_USER | 发起人自己 | SINGLE |
| 10 | 13 | NODE_PROPERTY_DIRECT_LEADER | 直属领导 | MULTIPLAYER |
| 11 | 14 | NODE_PROPERTY_DEPARTMENT_LEADER | 部门负责人 | MULTIPLAYER |
| 12 | 15 | NODE_PROPERTY_APPROVED_USERS | 被审批人自己 | MULTIPLAYER |
| 13 | 16 | NODE_PROPERTY_FORM_RELATED | 表单中相关人员 | MULTIPLAYER |
| 14 | 17 | NODE_PROPERTY_ZDY_RULES | 自定义规则(UDR) | MULTIPLAYER |
| 15 | 18 | NODE_PROPERTY_PREV_NODE_RELATED | 上一节点相关人员 | MULTIPLAYER |

::: tip 关于"12 种"的说法
部分文档说 AntFlow 支持 12 种审批人类型,但代码层实际定义了 **15 种**。差异来自:
- 层层审批(LOOP)和外部传入(OUT_SIDE_ACCESS)的 Provider 返回 null,由其他机制处理,UI 可能不直接可选
- "被审批人自己"(APPROVED_USERS)用于代提场景,UI 较少暴露
- 实际 UI 可配约 12-13 种
:::

参数类型说明:
- `SINGLE`:单人审批,取列表第 1 个
- `MULTIPLAYER`:多人会签,支持会签/或签/顺序会签

## 会签类型(signType)

审批人节点的会签类型定义在 [approverDrawer.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/drawer/approverDrawer.vue):

| signType | 名称 | 说明 |
|---|---|---|
| 1 | 会签 | 多人全部同意才通过(不限顺序) |
| 2 | 或签 | 一人同意即通过 |
| 3 | 顺序会签 | 按指定顺序依次审批 |

## 解析架构

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 380" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <text x="460" y="24" text-anchor="middle" font-size="16" font-weight="700" fill="#1e293b">审批人解析架构(适配器 + 策略模式)</text>

  <!-- 入口 -->
  <rect x="320" y="50" width="280" height="50" rx="8" fill="#eef2ff" stroke="#6366f1" stroke-width="1.5"/>
  <text x="460" y="80" text-anchor="middle" font-size="12" fill="#1e293b">BpmnConfBizServiceImpl.formatProcessConf()</text>
  <line x1="460" y1="100" x2="460" y2="120" stroke="#475569" stroke-width="2" marker-end="url(#c)"/>

  <!-- 格式化器 -->
  <rect x="320" y="120" width="280" height="50" rx="8" fill="#fef3c7" stroke="#d97706" stroke-width="1.5"/>
  <text x="460" y="150" text-anchor="middle" font-size="12" fill="#1e293b">BpmnPersonnelFormatImpl.formatPersonnelsConf()</text>
  <line x1="460" y1="170" x2="460" y2="190" stroke="#475569" stroke-width="2" marker-end="url(#c)"/>

  <!-- 派发器 -->
  <rect x="320" y="190" width="280" height="50" rx="8" fill="#dcfce7" stroke="#16a34a" stroke-width="1.5"/>
  <text x="460" y="220" text-anchor="middle" font-size="12" fill="#1e293b">PersonnelTagParser.parseTag(PersonnelEnum)</text>
  <line x1="460" y1="240" x2="460" y2="260" stroke="#475569" stroke-width="2" marker-end="url(#c)"/>

  <!-- 适配器层 -->
  <rect x="20" y="260" width="200" height="100" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="1.5"/>
  <text x="120" y="285" text-anchor="middle" font-size="11" font-weight="700" fill="#1e40af">Adaptor 层(薄包装)</text>
  <text x="120" y="305" text-anchor="middle" font-size="10" fill="#1e293b">DirectLeaderPersonnelAdaptor</text>
  <text x="120" y="320" text-anchor="middle" font-size="10" fill="#1e293b">RolePersonnelAdaptor</text>
  <text x="120" y="335" text-anchor="middle" font-size="10" fill="#1e293b">UserPointedPersonnelAdp ...</text>
  <text x="120" y="352" text-anchor="middle" font-size="9" fill="#64748b">15 个薄适配器</text>

  <rect x="360" y="260" width="200" height="100" rx="8" fill="#fce7f3" stroke="#db2777" stroke-width="1.5"/>
  <text x="460" y="285" text-anchor="middle" font-size="11" font-weight="700" fill="#9d174d">Provider 层(实际逻辑)</text>
  <text x="460" y="305" text-anchor="middle" font-size="10" fill="#1e293b">DirectLeaderPersonnelProvider</text>
  <text x="460" y="320" text-anchor="middle" font-size="10" fill="#1e293b">RolePersonnelProvider</text>
  <text x="460" y="335" text-anchor="middle" font-size="10" fill="#1e293b">UserPointedPersonnelProvider ...</text>
  <text x="460" y="352" text-anchor="middle" font-size="9" fill="#64748b">15 个 Provider 实现</text>

  <rect x="700" y="260" width="200" height="100" rx="8" fill="#fee2e2" stroke="#ef4444" stroke-width="1.5"/>
  <text x="800" y="285" text-anchor="middle" font-size="11" font-weight="700" fill="#991b1b">用户系统</text>
  <text x="800" y="305" text-anchor="middle" font-size="10" fill="#1e293b">AfUserService</text>
  <text x="800" y="320" text-anchor="middle" font-size="10" fill="#1e293b">t_user 表 / 外部系统</text>
  <text x="800" y="335" text-anchor="middle" font-size="10" fill="#1e293b">SSO / 企微 / 钉钉</text>

  <line x1="220" y1="310" x2="360" y2="310" stroke="#475569" stroke-width="2" marker-end="url(#c)"/>
  <line x1="560" y1="310" x2="700" y2="310" stroke="#475569" stroke-width="2" marker-end="url(#c)"/>

  <defs><marker id="c" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse"><path d="M 0 0 L 10 5 L 0 10 z" fill="#475569"/></marker></defs>
</svg>

### 调用链路

```
BpmnConfBizServiceImpl.formatProcessConf()
    ↓
BpmnPersonnelFormatImpl.formatPersonnelsConf()  // 遍历每个审批人节点
    ↓
PersonnelTagParser.parseTag(PersonnelEnum)      // Spring 容器扫描匹配适配器
    ↓
AbstractBpmnPersonnelAdaptor.setNodeParams()     // 去重、补全姓名、设置 assignee
    ↓
BpmnPersonnelProviderService.getAssigneeList()  // 实际解析逻辑
    ↓
返回 List<BpmnNodeParamsAssigneeVo>
```

### 派发器:PersonnelTagParser

[PersonnelTagParser.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/tagparser/PersonnelTagParser.java) 通过 Spring 容器扫描所有 `AbstractBpmnPersonnelAdaptor` 实现,根据 `isSupportBusinessObject` 匹配:

```java
public AbstractBpmnPersonnelAdaptor parseTag(PersonnelEnum data) {
    Collection<AbstractBpmnPersonnelAdaptor> beans = SpringBeanUtils.getBeans(AbstractBpmnPersonnelAdaptor.class);
    for (AbstractBpmnPersonnelAdaptor bean : beans) {
        if (bean.isSupportBusinessObject(data)) {
            return bean;
        }
    }
    return null;
}
```

## 各审批人类型详解

### 1. 指定人员(Code=5)

直接从配置中读取用户列表。

- **Provider**:[UserPointedPersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/UserPointedPersonnelProvider.java)
- **配置字段**:`emplIds`、`emplNames`
- **解析**:直接从 `bpmnNodeVo.getProperty().getEmplList()` 读取

### 2. 指定角色(Code=4)

查询角色下的所有用户。

- **Provider**:[RolePersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/RolePersonnelProvider.java)
- **解析逻辑**:
  - 普通流程:`roleInfoProvider.provideRoleEmployeeInfo(roleIds)` 查询角色下所有用户
  - 外部流程(`isOutSideProcess==1`):从 `OutSideBpmApproveTemplateService` 取角色 API URL,通过 `RestTemplate` GET 调用外部接口

### 3. 直属领导(Code=13)

查询发起人的直属领导。

- **Provider**:[DirectLeaderPersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/DirectLeaderPersonnelProvider.java)
- **继承**:`AbstractDifferentStandardAssignNodeAssigneeVoProvider`
- **解析**:`userService.queryEmployeeDirectLeaderByIds(userIds)`
- **审批基准**:支持"被审批人"/"上一节点"/"发起人"三种基准(通过 `approvalStandard` 配置)

### 4. 部门负责人(Code=14)

查询发起人所在部门的负责人。

- **Provider**:[DepartmentLeaderPersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/DepartmentLeaderPersonnelProvider.java)
- **解析**:`userService.queryDepartmentLeaderByIds(userIds)`

### 5. HRBP(Code=6)

查询发起人的 HRBP。

- **Provider**:[HrbpPersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/HrbpPersonnelProvider.java)
- **解析**:`userService.queryEmployeeHrpbByEmployeeIds(userIds)`

### 6. 层层审批(Code=2)

沿组织架构逐层向上找领导审批,直到顶层。

- **Provider**:[LoopPersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/LoopPersonnelProvider.java)
- **特殊**:`getAssigneeList()` 返回 null,实际由 `BpmnLoopSignNodeAdp`(有序签名适配器)处理
- **配置**:`loopEndType`(结束条件)、`loopNumberPlies`(审批层数)、`loopEndPerson`(终止人员)、`loopEndGrade`(终止层级)

### 7. 指定层级审批(Code=3)

查询发起人指定层级的领导。

- **Provider**:[LevelPersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/LevelPersonnelProvider.java)
- **解析**:`userService.queryLeaderByEmployeeIdAndLevel(startUserId, assignLevelGrade)`
- **配置**:`assignLevelType`(层级类型)、`assignLevelGrade`(层级数)

### 8. 发起人自己(Code=12)

直接用发起人作为审批人。

- **Provider**:[StartUserPersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/StartUserPersonnelProvider.java)
- **解析**:直接用 `startConditionsVo.getStartUserId()`

### 9. 被审批人自己(Code=15)

用于代提场景,取被审批对象(而非发起人)。

- **Provider**:[ApprovedUserPersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/ApprovedUserPersonnelProvider.java)
- **解析**:从 `startConditionsVo.getApprovalEmpls()` 取被审批人列表

### 10. 发起人自选(Code=7)

发起人在提交流程时,手动选择审批人。

- **Provider**:[CustomizePersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/CustomizePersonnelProvider.java)
- **解析**:从 `bpmnStartConditions.getApproversList()`(Map<节点ID, List<审批人>>)读取
- **特殊**:若只有一个节点则忽略 key 直接取值;若未选择则填入 `TO_BE_REMOVED` 标记后续移除

### 11. 表单中相关人员(Code=16)

从表单字段值中提取审批人,支持 8 种子类型。

- **Provider**:[FormRelatedPersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/FormRelatedPersonnelProvider.java)
- **子类型枚举**:[NodeFormAssigneePropertyEnum](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/constant/enums/NodeFormAssigneePropertyEnum.java)

| 子类型 Code | 名称 | 解析逻辑 |
|---|---|---|
| 1 | 表单中的人员 | `queryUserByIds(表单字段值)` |
| 2 | 表单中的角色 | `queryUserByRoleIds(表单字段值)` |
| 3 | 表单中人员的HRBP | `queryEmployeeHrpbByEmployeeIds(表单字段值)` |
| 4 | 表单中人员的直属领导 | `queryEmployeeDirectLeaderByIds(表单字段值)` |
| 5 | 表单中人员所在部门负责人 | `queryDepartmentLeaderByIds(表单字段值)` |
| 6 | 表单中部门的负责人 | `queryDepartmentLeaderByIds(表单部门值)` |
| 7 | 表单中人员多级领导 | 按指定层级查领导 |
| 8 | 表单中人员全部层级领导 | 层层审批逻辑 |

### 12. 上一节点相关人员(Code=18)

从上一节点审批人派生,支持 7 种子类型。

- **Provider**:[PrevNodeRelatedPersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/PrevNodeRelatedPersonnelProvider.java)
- **子类型枚举**:[NodePrevNodeAssigneePropertyEnum](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/constant/enums/NodePrevNodeAssigneePropertyEnum.java)

| 子类型 | 名称 |
|---|---|
| PREV_NODE_ASSIGNEE | 上一节点人员本身 |
| PREV_NODE_USER_HRBP | 上一节点人员的HRBP |
| PREV_NODE_USER_DIRECT_LEADER | 上一节点人员的直属领导 |
| PREV_NODE_USER_DEPART_LEADER | 上一节点人员的部门负责人 |
| ... | 共 7 种 |

### 13. 关联业务表(Code=8)

从业务表中查询审批人。

- **Provider**:[BusinessTablePersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/BusinessTablePersonnelProvider.java)
- **解析**:根据 `tableFieldType` 找到对应的 `ConfigurationTableAdapterEnum`,通过 `adaptorFactory.getBusinessConfigurationAdaptor()` 取 `AbstractBusinessConfigurationAdaptor` 实现,调用 `doFindBusinessPerson()`

### 14. 外部传入人员(Code=11)

由外部接入流程通过 Open API 传入。

- **Provider**:[OutSidePersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/OutSidePersonnelProvider.java)
- **特殊**:`getAssigneeList()` 返回 null,实际由外部接入机制处理

### 15. 自定义规则 UDR(Code=17)

用户自定义规则,支持不改源码的扩展。

- **Provider**:[UDRPersonnelProvider](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/UDRPersonnelProvider.java)
- **Demo 实现**:读取 `property.udrAssigneeProperty`(标识规则类型)和 `property.udrValueJson`(自定义 JSON)
- **扩展机制**:创建名为 `udrPersonnelProvider1` 的 bean,`AbstractBpmnPersonnelAdaptor` 构造时自动检测并替换默认实现

::: tip UDR 扩展点
UDR 是 AntFlow 预留的**不改源码扩展点**。用户以 jar 包形式引入,创建一个名为 `udrPersonnelProvider1` 的 `BpmnPersonnelProviderService` bean,即可覆盖默认的 `udrPersonnelProvider` 实现。
:::

## 配置存储

审批人配置以 JSON 形式存储在 `t_bpmn_node.node_config_json` 字段的 `approverConf` 子对象中,由 [BpmnNodeApproverConfJson](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/entity/jsonconf/BpmnNodeApproverConfJson.java) 定义。

该类合并了原先的 12 张表:

```
t_bpmn_node_personnel_conf, t_bpmn_node_personnel_empl_conf,
t_bpmn_node_role_conf, t_bpmn_node_role_outside_emp_conf,
t_bpmn_node_loop_conf, t_bpmn_node_assign_level_conf,
t_bpmn_node_hrbp_conf, t_bpmn_node_customize_conf,
t_bpmn_node_udr_conf, t_bpmn_node_form_related_user_conf,
t_bpmn_node_out_side_access_conf, t_bpmn_node_business_table_conf
```

11 个子配置字段:

| 字段 | 对应审批人类型 | 关键配置 |
|---|---|---|
| `personnelConf` | 指定人员(5) | `signType`, `employees: [{emplId, emplName}]` |
| `roleConfList` | 指定角色(4) | `roleId`, `roleName`, `signType` |
| `loopConf` | 层层审批(2) | `loopEndType`, `loopNumberPlies`, `loopEndPerson` |
| `assignLevelConf` | 指定层级(3) | `assignLevelType`, `assignLevelGrade` |
| `hrbpConf` | HRBP(6) | `hrbpConfType` |
| `customizeConf` | 发起人自选(7) | `signType` |
| `udrConfList` | 自定义规则(17) | `valueJson`, `udrProperty` |
| `formRelatedUserConfList` | 表单相关(16) | `valueJson`, `valueType` |
| `prevNodeRelatedUserConfList` | 上一节点相关(18) | `valueType` |
| `outSideAccessConf` | 外部传入(11) | `nodeMark`, `signType` |
| `businessTableConf` | 关联业务表(8) | `configurationTableType`, `tableFieldType` |

## 无人时的处理策略

当审批人规则解析后无人时(如直属领导为空),通过 [MissingAssigneeProcessStragtegyEnum](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/AbstractMissingAssignNodeAssigneeVoProvider.java) 处理:

| 策略 | 说明 |
|---|---|
| `SKIP` | 自动跳过该节点 |
| `TRANSFER_TO_ADMIN` | 转交给管理员处理 |
| `NOT_ALLOWED` | 抛异常,不允许提交 |

## 扩展审批人类型

新增审批人类型需:

1. 在 `NodePropertyEnum` 增加枚举值
2. 在 `PersonnelEnum` 增加对应枚举值
3. 实现 `BpmnPersonnelProviderService`(Provider)
4. 继承 `AbstractBpmnPersonnelAdaptor` 写薄 Adaptor
5. 实现 `BpmnNodeAdaptor`(`NodeProperty*Adp`)处理 JSON 配置读写
6. 在 `BpmnNodeApproverConfJson` 增加子配置字段
7. 在 `BpmnNodeAdpConfEnum` 增加枚举值

详见 [扩展审批人来源](/dev-guide/extend-approver)。

## 下一步

- [条件规则](/workflow-design/condition-rules) — 条件评估机制
- [低代码表单设计](/workflow-design/form-design) — vform 表单设计器
- [扩展审批人来源](/dev-guide/extend-approver) — 如何新增审批人类型
