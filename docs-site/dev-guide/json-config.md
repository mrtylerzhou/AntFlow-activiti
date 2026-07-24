# JSON-first 配置体系全解

> AntFlow 所有流程配置都以 JSON 存储，而非 Activiti 原生的 BPMN XML 或多列表关联。本章完整解析 JSON 配置的设计决策、数据结构、序列化工具和节点标志位体系。

## 一、为什么用 JSON-first

传统 Activiti 项目将流程配置分散在数十张关联表中（节点表、人员表、角色表、条件表、模板表…），JOIN 查询性能差，扩展字段需 DDL。AntFlow 将 **12 张配置表合并为 4 个 JSON 字段**：

| 传统方式 | JSON-first 方式 |
|---|---|
| `t_bpmn_node_personnel_empl_conf`（人员） | 合并到 `t_bpmn_node.node_config_json` |
| `t_bpmn_node_role_conf`（角色） | 同上 |
| `t_bpmn_node_button_conf`（按钮） | 同上 |
| `t_bpmn_node_approve_remind`（提醒） | 同上 |
| `t_bpmn_node_lf_formdata_field_control`（字段权限） | 同上 |
| `t_bpmn_node_sign_up_conf`（加签） | 同上 |
| `t_bpmn_conf_notice_template`（通知模板） | 合并到 `t_bpmn_conf.conf_config_json` |
| ... 共 12 张表 | 4 个 JSON 字段 |

好处：
- **读性能**：一次 SELECT 拿全所有配置，无需多表 JOIN
- **扩展性**：新增属性只需在 JSON 对象加 key，Zero DDL
- **跨数据库**：JSON 是所有数据库的共同能力
- **前端友好**：JSON 直接返回给前端渲染，无需二次转换

---

## 二、JSON 配置层级结构

```
t_bpmn_conf (流程定义)
  └── conf_config_json → BpmnConfConfigJson
       ├── ViewPageButtons          (查看页按钮)
       ├── NoticeTemplateConfig     (通知模板)
       ├── ConfTemplateConf         (设计期模板)
       ├── LowCodeFormConfig        (低代码表单配置)
       └── noticeChannelTypes       (通知渠道)

t_bpmn_node (节点定义)
  └── node_config_json → BpmnNodeConfigJson
       ├── approverConf             → BpmnNodeApproverConfJson
       ├── conditionsConf           → BpmnNodeConditionsConfJson
       ├── buttonSignConf           → BpmnNodeButtonSignConfJson
       ├── templateConf             → BpmnNodeTemplateConfJson
       ├── lowCodeConf              → BpmnNodeLowCodeConfJson
       ├── autoNodeConf             → BpmnNodeAutoNodeConfJson
       └── backType                  (退回类型)

t_bpm_variable (流程变量)
  └── variable_config_json → VariableConfigJson

t_bpm_process_app_application (应用配置)
  └── category_config_json → AppCategoryConfigJson
```

---

## 三、JSON 对象详解

### 3.1 BpmnNodeConfigJson（节点级，最复杂）

```java
// antflow-base/.../entity/jsonconf/BpmnNodeConfigJson.java
public class BpmnNodeConfigJson {

    // 审批人配置：指定谁审批、什么类型、怎么签
    private BpmnNodeApproverConfJson approverConf;

    // 条件配置：AND/OR 双层条件树
    private BpmnNodeConditionsConfJson conditionsConf;

    // 按钮配置：审批页显示哪些按钮、会签/或签策略
    private BpmnNodeButtonSignConfJson buttonSignConf;

    // 模板配置：通知模板和催办设置
    private BpmnNodeTemplateConfJson templateConf;

    // 低代码配置：表单字段权限控制
    private BpmnNodeLowCodeConfJson lowCodeConf;

    // 自动节点配置：conditionList 定义自动通过条件
    private BpmnNodeAutoNodeConfJson autoNodeConf;

    // 退回类型
    private Integer backType;
}
```

### 3.2 BpmnNodeApproverConfJson（审批人配置）

```java
public class BpmnNodeApproverConfJson {
    // 指定人员
    private List<BaseIdTranStruVo> assigneeList;

    // 指定角色
    private List<BaseIdTranStruVo> roleList;

    // 层层审批级别
    private Integer lookLevel;

    // 层层审批出发层级
    private Integer loopLevel;

    // HRBP 类型
    private Integer hrbpConfType;

    // 发起人自选
    private Boolean isStartUserChoose;

    // 表单关联
    private NodeFormAssigneePropertyEnum formAssigneeType;
    private String formFieldWidgetKey;

    // 前节点
    private NodePrevNodeAssigneePropertyEnum prevNodeAssigneeType;

    // UDR 配置
    private String udrConf;
}
```

### 3.3 BpmnNodeConditionsConfJson（条件配置）

```java
public class BpmnNodeConditionsConfJson {
    // 条件组列表（外层 AND）
    private List<ConditionGroup> conditionGroups;

    public static class ConditionGroup {
        // 组内条件列表（内层 OR），默认 AND
        private ConditionRelationShipEnum groupRelation;
        // 条件项
        private List<ConditionItem> conditionList;
    }

    public static class ConditionItem {
        private String conditionType;   // 条件类型码 (LF_STRING, BUSINESS_MONEY, etc.)
        private String fieldKey;        // 字段标识
        private String operator;        // 比较运算符 (GTE, LTE, EQ, etc.)
        private String conditionValue;  // 比较值
    }
}
```

示例 JSON（金额 > 10000 且部门 = 研发部）：

```json
{
  "conditionGroups": [
    {
      "groupRelation": "AND",
      "conditionList": [
        { "conditionType": "BUSINESS_MONEY", "fieldKey": "totalMoney", "operator": "GTE", "conditionValue": "10000" },
        { "conditionType": "BUSINESS_STRING", "fieldKey": "department", "operator": "EQ", "conditionValue": "研发部" }
      ]
    }
  ]
}
```

### 3.4 BpmnNodeButtonSignConfJson（按钮与会签配置）

```java
public class BpmnNodeButtonSignConfJson {
    // 操作按钮列表（如 [3,4,18,21,25] 表示同意/不同意/退回/转办/加签）
    private List<Integer> operationTypes;

    // 会签类型：1=会签, 2=或签
    private Integer signType;

    // 会签后操作
    private Integer signAfterType;

    // 加签配置：允许哪些加签方式（并行/串行/或签式）
    private List<Integer> signUpTypes;

    // 按钮标签
    private List<BpmNodeLabelVO> buttonLabels;
}
```

### 3.5 BpmnNodeLowCodeConfJson（低代码字段权限）

```java
public class BpmnNodeLowCodeConfJson {
    // 字段权限列表
    private List<LFFieldControlVO> fieldControls;
}

// LFFieldControlVO
public class LFFieldControlVO {
    private String fieldId;       // 表单字段 ID
    private String fieldName;     // 字段名称
    private String perm;          // R=只读, E=可编辑, H=隐藏
}
```

---

## 四、JSON 序列化工具：JsonConfUtil

```java
// antflow-base/.../entity/jsonconf/JsonConfUtil.java
public class JsonConfUtil {

    /** 序列化为 JSON 字符串 */
    public static String toNodeConfigJson(BpmnNodeConfigJson config) {
        return JimuJsonUtil.toJsonString(config);
    }

    /** 反序列化节点配置 */
    public static BpmnNodeConfigJson parseNodeConfig(String json) {
        return JimuJsonUtil.parseObject(json, BpmnNodeConfigJson.class);
    }

    /** 序列化流程级配置 */
    public static String toConfConfigJson(BpmnConfConfigJson config) {
        return JimuJsonUtil.toJsonString(config);
    }

    /** 反序列化流程级配置 */
    public static BpmnConfConfigJson parseConfConfig(String json) {
        return JimuJsonUtil.parseObject(json, BpmnConfConfigJson.class);
    }

    /** 序列化流程变量配置 */
    public static String toVariableConfigJson(VariableConfigJson config) {
        return JimuJsonUtil.toJsonString(config);
    }
}
```

所有读写统一经过 `JsonConfUtil`，底层走 `JimuJsonUtil`（封装 Fastjson2），确保序列化行为一致。

---

## 五、节点标志位：BpmnConfFlagsEnum

流程级别的标志位通过**二进制位叠加**存储于 `t_bpmn_conf.extra_flags` 字段，一次读取拿全所有标志位：

| 标志位 | 值 | 含义 | 影响 |
|---|---|---|---|
| `HAS_NODE_LABELS` | `0b1` (1) | 包含节点标签 | BpmnNodeFormatImpl 在元素化时附加 labels |
| `HAS_STARTUSER_CHOOSE_MODULES` | `0b10` (2) | 发起人自选模块 | 启动表单时多出人员选择器 |
| `HAS_DYNAMIC_CONDITIONS` | `0b100` (4) | 动态条件 | 发起人可在提交时选择条件分支 |
| `HAS_COPY` | `0b1000` (8) | 包含抄送节点 | 格式化时处理抄送 |
| `HAS_LAST_NODE_COPY` | `0b10000` (16) | 末节点抄送 | 流程结束时触发抄送 |
| `HAS_FORM_RELATED_ASSIGNEES` | `0b100000` (32) | 表单中选取人员 | 审批人解析需读取表单字段 |
| `USE_EXTERNAL_FORM` | `0b1000000` (64) | 外部表单模式（LF） | 表单设计步骤被禁用 |
| `USE_AUXILIARY_FORM` | `0b10000000` (128) | 辅助表单模式（DIY） | DIY 流程附加低代码辅助表单 |

```java
// 标志位读写
BpmnConfFlagsEnum flags = BpmnConfFlagsEnum.flagEnumsByCode(conf.getExtraFlags());
boolean hasCopy = flags.contains(BpmnConfFlagsEnum.HAS_COPY);
```

二进制叠加的好处：一个 `INT` 字段存储 8 个布尔值，无需 8 个 TINYINT 列。

---

## 六、节点级标志位：BpmnNodeFlagsEnum

存储在 `t_bpmn_node.extra_flags` 字段：

| 标志位 | 值 | 含义 |
|---|---|---|
| `HAS_ADDITIONAL_ASSIGNEE` | `0b1` | 有额外审批人 |
| `HAS_ADDITIONAL_ASSIGNEE_ROLE` | `0b10` | 有额外审批人角色 |
| `HAS_EXCLUDE_ASSIGNEE` | `0b100` | 有排除审批人 |
| `HAS_EXCLUDE_ASSIGNEE_ROLE` | `0b1000` | 有排除审批人角色 |

这些标志位由加签操作（`AddAssigneeProcessImpl`）设置，在审批人解析阶段被 `AbstractBpmnPersonnelAdaptor` 读取，用于附加或排除审批人。

---

## 七、JSON 读写时机

| 时机 | 操作 | 方法 |
|---|---|---|
| **保存流程设计** | 写 JSON | `BpmnConfBizServiceImpl.edit()` → `JsonConfUtil.toNodeConfigJson()` |
| **读取流程设计** | 读 JSON | `BpmnConfBizServiceImpl.selectById()` → `JsonConfUtil.parseNodeConfig()` |
| **发起流程** | 读 JSON | `BpmnPersonnelFormatImpl.formatPersonnelsConf()` → `parseNodeConfig()` → 读 `approverConf` |
| **审批操作** | 读 JSON | `ConfigFlowButtonContans.getButtons()` → `parseNodeConfig()` → 读 `buttonSignConf` |
| **消息发送** | 读 JSON | `ActivitiBpmMsgTemplateServiceImpl` → `parseNodeConfig()` → 读 `templateConf` |

---

## 下一步

- [数据库设计](/dev-guide/db-design) — 完整表结构和 ER 关系
- [BPMN 模型构建管线](/dev-guide/bpmn-pipeline) — JSON 如何转换为 BPMN
- [节点×审批人映射矩阵](/dev-guide/node-mapping-matrix) — 审批人配置的完整映射关系
