# 数据库设计

> AntFlow 数据库由两部分组成:AntFlow 业务表(34 个)+ Activiti 引擎表(12+ 个)。本章详解核心业务表结构、字段含义与表关系,为二次开发与运维提供参考。

## 表分类总览

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 380" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <!-- AntFlow 业务表 -->
  <rect x="20" y="20" width="430" height="340" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="235" y="44" text-anchor="middle" font-size="14" font-weight="700" fill="#1e40af">AntFlow 业务表(34 个)</text>

  <text x="40" y="70" font-size="11" font-weight="700" fill="#1e40af">流程定义</text>
  <rect x="40" y="78" width="180" height="28" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="130" y="96" text-anchor="middle" font-size="10" fill="#1e293b">t_bpmn_conf</text>
  <rect x="240" y="78" width="180" height="28" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="330" y="96" text-anchor="middle" font-size="10" fill="#1e293b">t_bpmn_node / t_bpmn_node_to</text>

  <text x="40" y="124" font-size="11" font-weight="700" fill="#1e40af">流程实例</text>
  <rect x="40" y="132" width="180" height="28" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="130" y="150" text-anchor="middle" font-size="10" fill="#1e293b">bpm_business_process</text>
  <rect x="240" y="132" width="180" height="28" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="330" y="150" text-anchor="middle" font-size="10" fill="#1e293b">t_bpm_variable</text>

  <text x="40" y="178" font-size="11" font-weight="700" fill="#1e40af">审批历史</text>
  <rect x="40" y="186" width="180" height="28" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="130" y="204" text-anchor="middle" font-size="10" fill="#1e293b">bpm_verify_info</text>
  <rect x="240" y="186" width="180" height="28" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="330" y="204" text-anchor="middle" font-size="10" fill="#1e293b">bpm_process_forward</text>

  <text x="40" y="232" font-size="11" font-weight="700" fill="#1e40af">消息通知</text>
  <rect x="40" y="240" width="180" height="28" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="130" y="258" text-anchor="middle" font-size="10" fill="#1e293b">t_information_template</text>
  <rect x="240" y="240" width="180" height="28" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="330" y="258" text-anchor="middle" font-size="10" fill="#1e293b">t_bpmn_approve_remind</text>

  <text x="40" y="286" font-size="11" font-weight="700" fill="#1e40af">加签/草稿</text>
  <rect x="40" y="294" width="180" height="28" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="130" y="312" text-anchor="middle" font-size="10" fill="#1e293b">t_bpmn_node_additional_sign_conf</text>
  <rect x="240" y="294" width="180" height="28" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="330" y="312" text-anchor="middle" font-size="10" fill="#1e293b">bpm_business_draft</text>

  <text x="40" y="340" font-size="11" font-weight="700" fill="#1e40af">三方接入</text>
  <rect x="40" y="348" width="390" height="20" rx="4" fill="#fff" stroke="#3b82f6"/>
  <text x="235" y="362" text-anchor="middle" font-size="10" fill="#1e293b">t_outside_bpm_*(8 张表)</text>

  <!-- Activiti 表 -->
  <rect x="470" y="20" width="430" height="340" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="685" y="44" text-anchor="middle" font-size="14" font-weight="700" fill="#92400e">Activiti 5.23 引擎表(12+ 个)</text>

  <text x="490" y="70" font-size="11" font-weight="700" fill="#92400e">流程定义</text>
  <rect x="490" y="78" width="180" height="28" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="580" y="96" text-anchor="middle" font-size="10" fill="#1e293b">ACT_RE_PROCDEF</text>
  <rect x="690" y="78" width="180" height="28" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="780" y="96" text-anchor="middle" font-size="10" fill="#1e293b">ACT_RE_DEPLOYMENT</text>

  <text x="490" y="124" font-size="11" font-weight="700" fill="#92400e">运行时数据</text>
  <rect x="490" y="132" width="180" height="28" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="580" y="150" text-anchor="middle" font-size="10" fill="#1e293b">ACT_RU_EXECUTION</text>
  <rect x="690" y="132" width="180" height="28" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="780" y="150" text-anchor="middle" font-size="10" fill="#1e293b">ACT_RU_TASK</text>
  <rect x="490" y="170" width="180" height="28" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="580" y="188" text-anchor="middle" font-size="10" fill="#1e293b">ACT_RU_VARIABLE</text>
  <rect x="690" y="170" width="180" height="28" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="780" y="188" text-anchor="middle" font-size="10" fill="#1e293b">ACT_RU_IDENTITYLINK</text>

  <text x="490" y="216" font-size="11" font-weight="700" fill="#92400e">历史数据</text>
  <rect x="490" y="224" width="180" height="28" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="580" y="242" text-anchor="middle" font-size="10" fill="#1e293b">ACT_HI_PROCINST</text>
  <rect x="690" y="224" width="180" height="28" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="780" y="242" text-anchor="middle" font-size="10" fill="#1e293b">AF_HI_TASKINST</text>
  <rect x="490" y="262" width="180" height="28" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="580" y="280" text-anchor="middle" font-size="10" fill="#1e293b">ACT_HI_VARINST</text>
  <rect x="690" y="262" width="180" height="28" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="780" y="280" text-anchor="middle" font-size="10" fill="#1e293b">ACT_HI_ACTINST</text>

  <text x="490" y="308" font-size="11" font-weight="700" fill="#92400e">通用</text>
  <rect x="490" y="316" width="380" height="28" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="680" y="334" text-anchor="middle" font-size="10" fill="#1e293b">ACT_GE_BYTEARRAY / ACT_ID_USER / ACT_ID_GROUP …</text>
</svg>

## 通用字段

所有 AntFlow 业务表都包含以下通用字段(下表不再重复列出):

| 字段 | 类型 | 含义 |
|---|---|---|
| `tenant_id` | String | 多租户 ID |
| `is_del` | Integer | 软删标志(0=未删除,1=已删除) |
| `create_user` | String | 创建人 ID |
| `create_time` | Date | 创建时间 |
| `update_user` | String | 更新人 ID |
| `update_time` | Date | 更新时间 |

## t_bpmn_conf — 流程配置主表

实体:[BpmnConf.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/entity/BpmnConf.java)

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | Long | 主键 |
| `bpmn_code` | String | 流程编码(唯一版本标识,格式 `xxx-00001`) |
| `bpmn_name` | String | 流程名称 |
| `bpmn_type` | Integer | 流程类型 |
| `form_code` | String | 表单编码(关联 `FormOperationAdaptor` 的 `svcName`) |
| `app_id` | Integer | 应用 id |
| `deduplication_type` | Integer | 去重类型(1=不去重,2=正向,3=反向) |
| `effective_status` | Integer | 生效状态(0=未生效,1=已生效) |
| `is_all` | Integer | 是否全员可见 |
| `is_out_side_process` | Integer | 是否三方流程 |
| `is_lowcode_flow` | Integer | 是否低代码流程 |
| `lf_formdata_ids` | String(CSV) | 外部表单版本 id 列表 |
| `business_party_id` | Long | 业务方 id(三方接入) |
| `extra_flags` | Integer | 扩展标志位(位运算,如 `USE_EXTERNAL_FORM=64`) |
| `conf_config_json` | String(JSON) | 整合配置 JSON(`BpmnConfConfigJson`) |
| `remark` | String | 备注 |

### BpmnConfConfigJson 结构

```json
{
  "noticeChannelTypes": [1, 2, 3],
  "deduplicationType": 2,
  "isLowCodeFlow": true,
  "useExternalForm": false,
  "extraFlags": 0
}
```

## t_bpmn_node — 流程节点表

实体:[BpmnNode.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/entity/BpmnNode.java)

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | Long | 主键 |
| `conf_id` | Long | 所属流程配置 id → `t_bpmn_conf.id` |
| `node_id` | String | BPMN 节点 id(逻辑标识) |
| `node_type` | Integer | 节点类型(`NodeTypeEnum`:1 发起/4 审批/2 网关…) |
| `node_property` | Integer | 节点属性(`NodePropertyEnum`:指定人员/角色/HRBP…) |
| `node_from` | String | 上一节点 id |
| `node_froms` | String | 多个上一节点(并行场景,CSV) |
| `batch_status` | Integer | 是否可批量同意 |
| `approval_standard` | Integer | 审批标准(1=发起人,2=审批人) |
| `node_name` | String | 节点名称 |
| `node_display_name` | String | 节点显示名 |
| `annotation` | String | 注释 |
| `is_deduplication` | Integer | 是否去重 |
| `deduplication_exclude` | Boolean | 去重时是否排除 |
| `is_sign_up` | Integer | 是否报名节点 |
| `no_header_action` | Integer | 无表头操作 |
| `extra_flags` | Integer | 扩展标志位 |
| `is_dynamic_condition` | Boolean | 是否动态条件网关 |
| `is_parallel` | Boolean | 是否并行节点 |
| `node_config_json` | String(JSON) | 节点配置 JSON(`BpmnNodeConfigJson`) |

### BpmnNodeConfigJson 结构

```json
{
  "approver": {
    "nodeProperty": 5,
    "assigneeList": [{"id": "u001", "name": "张三"}],
    "approvalStandard": 1,
    "orderedNodeType": 1
  },
  "buttonSignConf": {
    "operationTypes": [3, 4, 18, 21, 25]
  },
  "conditionConf": {
    "groupedConditionParamTypes": [...],
    "groupedConditionsMap": {...}
  },
  "templateConf": {
    "overtimeConf": {
      "noticeTypes": [1, 2],
      "remindDays": 3
    }
  },
  "autoNodeConf": {
    "conditionExpression": "...",
    "autoAction": "..."
  }
}
```

## t_bpmn_node_to — 节点路由表

实体:[BpmnNodeTo.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/entity/BpmnNodeTo.java)

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | Long | 主键 |
| `bpmn_node_id` | Long | 当前节点 id → `t_bpmn_node.id` |
| `node_to` | String | 下一节点 id(逻辑标识,对应 `t_bpmn_node.node_id`) |

## bpm_business_process — 业务流程实例表

实体:[BpmBusinessProcess.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/entity/BpmBusinessProcess.java)

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | Long | 主键 |
| `PROCESSINESS_KEY` | String | 流程 key |
| `BUSINESS_ID` | String | 业务数据 id |
| `BUSINESS_NUMBER` | String | 业务编号(展示用,如 `LEAVE_20260724001`) |
| `ENTRY_ID` | String | 入口 id(Activiti process instance id) |
| `VERSION` | String | 流程版本号(对应 `bpmn_code`) |
| `PROC_INST_ID_` | String | Activiti 流程实例 id |
| `description` | String | 流程描述 |
| `process_state` | Integer | 流程状态(2=办理中,3=已结束,6=已驳回,7=已作废) |
| `create_user` | String | 发起人 id |
| `user_name` | String | 发起人姓名 |
| `process_digest` | String | 流程摘要 |
| `data_source_id` | Long | 数据源 id |
| `back_user_id` | String | 退回用户 id |
| `approval_users` | String(JSON) | 被审批人信息数组 |
| `is_out_side_process` | Integer | 是否三方流程 |
| `is_lowcode_flow` | Integer | 是否低代码流程 |

## t_bpm_variable — 流程变量配置表

实体:[BpmVariable.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/entity/BpmVariable.java)

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | Long | 主键 |
| `process_num` | String | 流程编号 |
| `process_name` | String | 流程名称 |
| `process_desc` | String | 流程描述 |
| `process_start_conditions` | String | 启动条件 JSON(LF 表单数据存这里) |
| `bpmn_code` | String | BPMN 编码 → `t_bpmn_conf.bpmn_code` |
| `variable_config_json` | String(JSON) | 变量配置 JSON(按钮/消息/signUps/approveReminds) |

## bpm_verify_info — 审批记录表

实体:[BpmVerifyInfo.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/entity/BpmVerifyInfo.java)

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | Long | 主键 |
| `run_info_id` | String | 流程实例 id |
| `verify_user_id` | String | 审批人 id |
| `verify_user_name` | String | 审批人姓名 |
| `verify_status` | Integer | 审批状态(1=提交,2=同意,3=不同意…) |
| `verify_desc` | String | 审批意见 |
| `verify_date` | Date | 审批时间 |
| `task_name` | String | 任务名称 |
| `task_id` | String | Activiti 任务 id |
| `task_def_key` | String | 任务定义 key(对应 `t_bpmn_node.node_id`) |
| `business_type` | Integer | 业务类型 |
| `business_id` | String | 业务 id |
| `original_id` | String | 原始审批人 id(转办场景) |
| `process_code` | String | 流程编码 |
| `attachments_json` | String(JSON) | 附件 JSON |

## bpm_process_forward — 流程抄送/转发表

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | Integer | 主键 |
| `forward_user_id` | String | 抄送目标用户 id |
| `forward_user_name` | String | 目标用户姓名 |
| `processInstance_Id` | String | 流程实例 id |
| `create_user_id` | String | 抄送发起人 id |
| `is_read` | Integer | 是否已读 |
| `task_id` | String | 关联任务 id |
| `process_number` | String | 流程编号 |
| `node_id` | String | 节点 id |

## t_information_template — 消息模板表

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | Long | 主键 |
| `name` | String | 模板名称 |
| `num` | String | 编号 |
| `system_title` | String | 系统消息标题 |
| `system_content` | String | 系统消息内容 |
| `mail_title` | String | 邮件标题 |
| `mail_content` | String | 邮件内容 |
| `note_content` | String | 短信内容 |
| `jump_url` | Integer | 跳转类型(1=审批页,2=详情页,3=待办列表) |
| `status` | Integer | 状态(0=启用,1=禁用) |
| `event` | Integer | 触发事件(`EventTypeEnum`) |
| `event_name` | String | 事件名 |
| `is_default` | Integer | 是否默认模板 |

## t_bpmn_approve_remind — 审批提醒配置表

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | Long | 主键 |
| `conf_id` | Long | 流程配置 id → `t_bpmn_conf.id` |
| `node_id` | Long | 节点 id → `t_bpmn_node.id` |
| `template_id` | Long | 消息模板 id → `t_information_template.id` |
| `days` | String | 提醒天数配置 |

## t_bpmn_node_additional_sign_conf — 节点加签配置表

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | Integer | 主键 |
| `bpmn_node_id` | Long | 所属节点 id → `t_bpmn_node.id` |
| `sign_infos` | String(JSON) | 附加签核人员信息 JSON |
| `sign_property` | Integer | 签核属性(`NodePropertyEnum`) |
| `sign_property_type` | Integer | 签核类型(1=加签,2=减签) |
| `sign_type` | Integer | 签名类型(1=会签,2=或签) |

## bpm_business_draft — 业务草稿表

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | Long | 主键 |
| `bpmn_code` | String | 流程编码 |
| `process_code` | String | 流程编号 |
| `process_key` | String | 流程 key |
| `create_user_name` | String | 创建人姓名 |
| `create_user` | String | 创建人 id |
| `draft_json` | String(JSON) | 草稿数据 JSON(表单字段值) |

## 表关系图

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 440" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr15" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- t_bpmn_conf -->
  <rect x="380" y="20" width="160" height="60" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="460" y="44" text-anchor="middle" font-size="12" font-weight="700" fill="#1e40af">t_bpmn_conf</text>
  <text x="460" y="62" text-anchor="middle" font-size="10" fill="#1e3a8a">流程定义主表</text>

  <!-- t_bpmn_node -->
  <rect x="200" y="120" width="160" height="60" rx="8" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/>
  <text x="280" y="144" text-anchor="middle" font-size="12" font-weight="700" fill="#155e2f">t_bpmn_node</text>
  <text x="280" y="162" text-anchor="middle" font-size="10" fill="#14532d">流程节点表</text>

  <!-- t_bpmn_node_to -->
  <rect x="40" y="120" width="140" height="60" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="110" y="144" text-anchor="middle" font-size="11" font-weight="700" fill="#92400e">t_bpmn_node_to</text>
  <text x="110" y="162" text-anchor="middle" font-size="10" fill="#78350f">节点路由</text>

  <!-- t_bpmn_approve_remind -->
  <rect x="380" y="120" width="160" height="60" rx="8" fill="#fce7f3" stroke="#db2777"/>
  <text x="460" y="144" text-anchor="middle" font-size="11" font-weight="700" fill="#9d174d">t_bpmn_approve_remind</text>
  <text x="460" y="162" text-anchor="middle" font-size="10" fill="#831843">审批提醒</text>

  <!-- t_information_template -->
  <rect x="380" y="220" width="160" height="60" rx="8" fill="#fee2e2" stroke="#dc2626"/>
  <text x="460" y="244" text-anchor="middle" font-size="11" font-weight="700" fill="#991b1b">t_information_template</text>
  <text x="460" y="262" text-anchor="middle" font-size="10" fill="#7f1d1d">消息模板</text>

  <!-- t_bpmn_node_additional_sign_conf -->
  <rect x="200" y="220" width="160" height="60" rx="8" fill="#e0e7ff" stroke="#4f46e5"/>
  <text x="280" y="244" text-anchor="middle" font-size="10" font-weight="700" fill="#3730a3">t_bpmn_node_additional_sign_conf</text>
  <text x="280" y="262" text-anchor="middle" font-size="10" fill="#312e81">加签配置</text>

  <!-- t_bpm_variable -->
  <rect x="560" y="120" width="160" height="60" rx="8" fill="#cffafe" stroke="#0891b2"/>
  <text x="640" y="144" text-anchor="middle" font-size="12" font-weight="700" fill="#155e75">t_bpm_variable</text>
  <text x="640" y="162" text-anchor="middle" font-size="10" fill="#083344">流程变量</text>

  <!-- bpm_business_process -->
  <rect x="740" y="220" width="160" height="60" rx="8" fill="#fef9c3" stroke="#a16207" stroke-width="2"/>
  <text x="820" y="244" text-anchor="middle" font-size="11" font-weight="700" fill="#713f12">bpm_business_process</text>
  <text x="820" y="262" text-anchor="middle" font-size="10" fill="#422006">流程实例</text>

  <!-- bpm_verify_info -->
  <rect x="740" y="320" width="160" height="60" rx="8" fill="#f1f5f9" stroke="#475569"/>
  <text x="820" y="344" text-anchor="middle" font-size="11" font-weight="700" fill="#1e293b">bpm_verify_info</text>
  <text x="820" y="362" text-anchor="middle" font-size="10" fill="#475569">审批历史</text>

  <!-- bpm_process_forward -->
  <rect x="540" y="320" width="160" height="60" rx="8" fill="#f1f5f9" stroke="#475569"/>
  <text x="620" y="344" text-anchor="middle" font-size="11" font-weight="700" fill="#1e293b">bpm_process_forward</text>
  <text x="620" y="362" text-anchor="middle" font-size="10" fill="#475569">抄送记录</text>

  <!-- bpm_business_draft -->
  <rect x="340" y="320" width="160" height="60" rx="8" fill="#f1f5f9" stroke="#475569"/>
  <text x="420" y="344" text-anchor="middle" font-size="11" font-weight="700" fill="#1e293b">bpm_business_draft</text>
  <text x="420" y="362" text-anchor="middle" font-size="10" fill="#475569">业务草稿</text>

  <!-- 关系连线 -->
  <line x1="460" y1="80" x2="280" y2="120" stroke="#475569" stroke-width="1.5" marker-end="url(#arr15)"/>
  <text x="360" y="105" font-size="9" fill="#475569">1:N conf_id</text>

  <line x1="200" y1="150" x2="180" y2="150" stroke="#475569" stroke-width="1.5" marker-end="url(#arr15)"/>
  <text x="190" y="142" font-size="9" fill="#475569">N:N</text>

  <line x1="460" y1="80" x2="460" y2="120" stroke="#475569" stroke-width="1.5" marker-end="url(#arr15)"/>
  <text x="470" y="105" font-size="9" fill="#475569">1:N conf_id</text>

  <line x1="460" y1="80" x2="640" y2="120" stroke="#475569" stroke-width="1.5" marker-end="url(#arr15)"/>
  <text x="540" y="105" font-size="9" fill="#475569">1:N bpmn_code</text>

  <line x1="460" y1="180" x2="460" y2="220" stroke="#475569" stroke-width="1.5" marker-end="url(#arr15)"/>
  <text x="470" y="205" font-size="9" fill="#475569">N:1 template_id</text>

  <line x1="280" y1="180" x2="280" y2="220" stroke="#475569" stroke-width="1.5" marker-end="url(#arr15)"/>
  <text x="290" y="205" font-size="9" fill="#475569">1:N</text>

  <line x1="640" y1="180" x2="820" y2="220" stroke="#475569" stroke-width="1.5" marker-end="url(#arr15)"/>
  <text x="710" y="205" font-size="9" fill="#475569">1:N bpmn_code</text>

  <line x1="820" y1="280" x2="820" y2="320" stroke="#475569" stroke-width="1.5" marker-end="url(#arr15)"/>
  <text x="830" y="305" font-size="9" fill="#475569">1:N</text>

  <line x1="820" y1="280" x2="620" y2="320" stroke="#475569" stroke-width="1.5" marker-end="url(#arr15)"/>
  <text x="700" y="305" font-size="9" fill="#475569">1:N</text>

  <line x1="820" y1="280" x2="420" y2="320" stroke="#475569" stroke-width="1.5" marker-end="url(#arr15)"/>
  <text x="600" y="305" font-size="9" fill="#475569">1:N</text>
</svg>

## 多数据库支持

AntFlow 通过 MyBatis-Plus 的 `DbType` 机制支持 12+ 种数据库:

| 数据库 | 兼容性 |
|---|---|
| MySQL | 完整支持 |
| PostgreSQL | 完整支持 |
| Oracle | 完整支持 |
| SQL Server | 完整支持 |
| 达梦 DM | 完整支持 |
| 人大金仓 Kingbase | 完整支持 |
| 南大通用 GBase | 完整支持 |
| OceanBase | 完整支持 |
| 高斯 GaussDB | 完整支持 |
| PolarDB | 完整支持 |
| MongoDB | 实验性支持 |

详见 [多数据库支持](/ops/db-multi)。

## 小结

- AntFlow 业务表 34 个 + Activiti 引擎表 12+ 个,通过 `bpmn_code`/`conf_id`/`node_id`/`entry_id` 等字段串联
- 所有业务表统一包含 `tenant_id`、`is_del`、审计字段,支持多租户与软删
- 流程配置 JSON 化:`conf_config_json`、`node_config_json`、`variable_config_json`,扩展灵活
- LF 表单数据存 `t_bpm_variable.process_start_conditions` JSON,DIY 表单数据存对应业务表
- 通过 MyBatis-Plus `DbType` 支持 12+ 种数据库,业务表 SQL 跨库兼容

下一节 [扩展审批人来源](/dev-guide/extend-approver) 介绍如何自定义 PersonnelAdaptor。
