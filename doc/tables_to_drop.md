# 已删除/可删除表清单

> 本文档记录 AntFlow 项目数据库精简过程中所有已删除或标记为可删除的表，便于后续迁移问题追查。

---

## 一、BPMN 配置子表（26 张 → JSON 合并）

原 26 张 `t_bpmn_*` 配置子表已合并为 JSON 字段存储在两张主表上：
- `t_bpmn_conf.conf_config_json` — 流程级配置
- `t_bpmn_node.node_config_json` — 节点级配置

| # | 表名 | 说明 | 替代方案 | 删除时间 |
|---|------|------|---------|---------|
| 1 | `t_bpmn_view_page_button` | 流程级视图页按钮 | `conf_config_json` → `viewPageButtons[]` | 2026-05-31 (7a0bea4e) |
| 2 | `t_bpmn_conf_notice_template` | 通知模板头 | `conf_config_json` → `noticeTemplateConfig` | 2026-05-31 (7a0bea4e) |
| 3 | `t_bpmn_conf_notice_template_detail` | 通知模板明细 | `conf_config_json` → `noticeTemplateConfig.details[]` | 2026-05-31 (7a0bea4e) |
| 4 | `t_bpmn_node_button_conf` | 节点按钮配置 | `node_config_json` → `buttonSignConf.buttonConfList[]` | 2026-05-31 (7a0bea4e) |
| 5 | `t_bpmn_node_sign_up_conf` | 节点签收配置 | `node_config_json` → `buttonSignConf.signUpConf` | 2026-05-31 (7a0bea4e) |
| 6 | `t_bpmn_node_labels` | 节点标签 | `node_config_json` → `buttonSignConf.labels[]` | 2026-05-31 (7a0bea4e) |
| 7 | `t_bpmn_node_additional_sign_conf` | 额外加签审批人 | `node_config_json` → `buttonSignConf.additionalSignConfList[]` | 2026-05-31 (7a0bea4e) |
| 8 | `t_bpmn_node_personnel_conf` | 指定人员审批（主表） | `node_config_json` → `approverConf.personnelConf` | 2026-05-31 (7a0bea4e) |
| 9 | `t_bpmn_node_personnel_empl_conf` | 指定人员审批（人员明细） | `node_config_json` → `approverConf.personnelConf.employees[]` | 2026-05-31 (7a0bea4e) |
| 10 | `t_bpmn_node_role_conf` | 指定角色审批 | `node_config_json` → `approverConf.roleConfList[]` | 2026-05-31 (7a0bea4e) |
| 11 | `t_bpmn_node_role_outside_emp_conf` | 角色外部人员 | `node_config_json` → `approverConf.roleConfList[].outsideEmployees[]` | 2026-05-31 (7a0bea4e) |
| 12 | `t_bpmn_node_loop_conf` | 层层审批配置 | `node_config_json` → `approverConf.loopConf` | 2026-05-31 (7a0bea4e) |
| 13 | `t_bpmn_node_assign_level_conf` | 指定层级审批 | `node_config_json` → `approverConf.assignLevelConf` | 2026-05-31 (7a0bea4e) |
| 14 | `t_bpmn_node_hrbp_conf` | HRBP 审批配置 | `node_config_json` → `approverConf.hrbpConf` | 2026-05-31 (7a0bea4e) |
| 15 | `t_bpmn_node_customize_conf` | 自选审批人配置 | `node_config_json` → `approverConf.customizeConf` | 2026-05-31 (7a0bea4e) |
| 16 | `t_bpmn_node_udr_conf` | 自定义规则审批 | `node_config_json` → `approverConf.udrConfList[]` | 2026-05-31 (7a0bea4e) |
| 17 | `t_bpmn_node_form_related_user_conf` | 表单关联用户审批 | `node_config_json` → `approverConf.formRelatedUserConfList[]` | 2026-05-31 (7a0bea4e) |
| 18 | `t_bpmn_node_out_side_access_conf` | 外部接入审批 | `node_config_json` → `approverConf.outSideAccessConf` | 2026-05-31 (7a0bea4e) |
| 19 | `t_bpmn_node_business_table_conf` | 关联业务表审批 | `node_config_json` → `approverConf.businessTableConf` | 2026-05-31 (7a0bea4e) |
| 20 | `t_bpmn_node_conditions_conf` | 条件节点配置 | `node_config_json` → `conditionsConf.conditionGroups[].extJson` | 2026-05-31 (7a0bea4e) |
| 21 | `t_bpmn_node_conditions_param_conf` | 条件参数配置 | `node_config_json` → `conditionsConf.conditionGroups[].extJson`（Vue3 模型已包含全部运行时数据） | 2026-05-31 (7a0bea4e) |
| 22 | `t_out_side_bpmn_node_conditions_conf` | 外部条件配置 | `node_config_json` → `conditionsConf.outSideConditionId` | 2026-05-31 (7a0bea4e) |
| 23 | `t_bpmn_template`（node_id 非空） | 节点级通知模板 | `node_config_json` → `templateConf.templates[]` | 2026-05-31 (7a0bea4e) |
| 24 | `t_bpmn_approve_remind` | 审批催办配置 | `node_config_json` → `templateConf.approveRemind` | 2026-05-31 (7a0bea4e) |
| 25 | `t_bpmn_node_lf_formdata_field_control` | 低代码字段权限 | `node_config_json` → `lowCodeConf.fieldControls[]` | 2026-05-31 (7a0bea4e) |
| 26 | `t_bpmn_template`（node_id 为空） | 流程级通知模板 | `conf_config_json` → `confTemplates[]` | 2026-05-31 (7a0bea4e) |

---

## 二、Activiti 冗余表（12 张）

AntFlow 对 Activiti 原生表进行了裁剪，以下表已从 Java 代码（Mapper/Entity/Service）中移除，`act_init_db.sql` 中也不再包含这些表的 DDL。

| # | 表名 | 说明 | 删除时间 | Commit |
|---|------|------|---------|--------|
| 1 | `ACT_RU_JOB` | 运行时定时任务 | 2026-05-31 | 081f09e8 |
| 2 | `ACT_RE_MODEL` | 模型定义 | 2026-05-31 | 0bc40dbf |
| 3 | `ACT_RU_EVENT_SUBSCR` | 运行时事件订阅 | 2026-05-31 | 4a808fa6 |
| 4 | `ACT_RU_IDENTITYLINK` | 运行时身份链接 | 2026-05-31 | eb050a2b |
| 5 | `ACT_HI_IDENTITYLINK` | 历史身份链接 | 2026-05-31 | eb050a2b |
| 6 | `ACT_HI_ATTACHMENT` | 历史附件 | 2026-05-31 | 8d74579f |
| 7 | `ACT_HI_DETAIL` | 历史详情 | 2026-05-31 | 111db426 |
| 8 | `ACT_HI_COMMENT` | 历史评论 | 2026-05-31 | 8b4a3cc7 |
| 9 | `ACT_ID_USER` | 用户信息 | 2026-05-31 | 159c5570 |
| 10 | `ACT_ID_MEMBERSHIP` | 用户组成员关系 | 2026-05-31 | 159c5570 |
| 11 | `ACT_ID_INFO` | 用户扩展信息 | 2026-05-31 | 159c5570 |
| 12 | `ACT_ID_GROUP` | 用户组 | 2026-05-31 | 159c5570 |

---

## 三、死表 / 废弃表（4 张）

| # | 表名 | 说明 | 删除方式 | Commit |
|---|------|------|---------|--------|
| 1 | `t_bpm_business` | 业务流程表（死表，无读写） | 删除 Entity/Mapper/Service，SQL 中无 DDL | da9da2d0 |
| 2 | `t_bpm_variable_single` | 单人审批变量表（已废弃） | 删除 DDL + 相关 Java 代码 | b80699f3 |
| 3 | `bpm_process_name_relevancy` | 流程名称关联表（已废弃） | 删除 DDL + 相关 Java 代码 | 1544f51d |
| 4 | `t_dict_main` | 字典主表（死表，无读写） | 删除 Entity/Mapper，清理 Service 中死代码 | 本次会话 Phase 1a |

---

## 四、本次会话删除的表（10 张）

### Phase 1: 死表删除

| 表名 | 说明 | 替代方案 | 删除的文件 |
|------|------|---------|-----------|
| `t_dict_main` | 字典主表 | 无（死表，从未被读写） | `DictMain.java`, `DictMainMapper.java` |
| `t_bpm_variable_sequence_flow` | 流程变量顺序流 | 无（仅写入从未读取） | `BpmVariableSequenceFlow.java`, `BpmVariableSequenceFlowMapper.java`, `BpmVariableSequenceFlowService.java`, `BpmVariableSequenceFlowServiceImpl.java` |

### Phase 2: 废弃表删除

| 表名 | 说明 | 替代方案 | 删除的文件 |
|------|------|---------|-----------|
| `bpm_process_dept` | 流程部门配置 | 已废弃，`BpmProcessDeptVo` 重命名为 `ProcessConfVo` | Entity, Mapper, Mapper XML, Service, BizService 共 8 个文件 |

### Phase 3: 通知配置合并

| 表名 | 说明 | 替代方案 | 删除的文件 |
|------|------|---------|-----------|
| `bpm_process_notice` | 流程通知渠道配置 | `t_bpmn_conf.conf_config_json` → `noticeChannelTypes: List<Integer>` | `BpmProcessNotice.java`, `BpmProcessNoticeMapper.java`, `BpmProcessNoticeService.java`, `BpmProcessNoticeServiceImpl.java` |

**读路径变更：**
- `ActivitiBpmMsgTemplateServiceImpl` — 从 `conf_config_json` 解析 `noticeChannelTypes`
- `BpmVariableMessageBizServiceImpl` — 同上
- `TaskMgmtServiceImpl` — 批量查询时从 JSON 构建 `formCode2NoticeTypes` Map
- `DictServiceImpl` — 同上
- `InformationTemplateController` — 通过 `BpmnConfBizService` 获取

### Phase 4: 催办配置合并

| 表名 | 说明 | 替代方案 | 删除的文件 |
|------|------|---------|-----------|
| `bpm_process_node_overtime` | 节点超时催办配置 | `t_bpmn_node.node_config_json` → `templateConf.overtimeConf.{noticeTime, noticeTypes}` | Entity, Mapper, Mapper XML, Service, BizService, VO 共 8 个文件 |

**读路径变更：**
- `ActivitiBpmMsgTemplateServiceImpl` — 查询所有节点 JSON，提取 `overtimeConf.noticeTypes`

### Phase 5: 操作配置合并

| 表名 | 说明 | 替代方案 | 删除的文件 |
|------|------|---------|-----------|
| `bpm_process_operation` | 节点操作类型配置 | `t_bpmn_node.node_config_json` → `buttonSignConf.operationTypes: List<Integer>` | 无独立文件，从 Mapper XML 和 Mapper Java 中移除方法 |

**读路径变更：**
- `ProcessApprovalServiceImpl.isOperatable()` — 从节点 JSON 读取 `operationTypes` 判断是否可操作

### Phase 6: 回退配置合并

| 表名 | 说明 | 替代方案 | 删除的文件 |
|------|------|---------|-----------|
| `bpm_process_node_back` | 节点回退类型配置 | `t_bpmn_node.node_config_json` → `backType: Integer` | 无独立文件，从 Mapper XML/Java/Service 中移除 `disagreeType` 方法 |

### Phase 7: 查看页按钮合并

| 表名 | 说明 | 替代方案 | 删除的文件 |
|------|------|---------|-----------|
| `t_bpm_variable_view_page_button` | 流程变量查看页按钮 | `t_bpm_variable_button` 新增 `view_type` 列，`button_page_type=3` 表示查看页 | `BpmVariableViewPageButton.java`, `BpmVariableViewPageButtonMapper.java`, `BpmVariableViewPageButtonMapper.xml`, `BpmVariableViewPageButtonService.java`, `BpmVariableViewPageButtonServiceImpl.java` |

**Schema 变更：**
- `t_bpm_variable_button` 新增 `view_type INT` 列（1=发起人视图，2=其他审批人视图）

**读路径变更：**
- `ConfigFlowButtonContans` — 使用 `bpmVariableButtonService.getButtonsByProcessNumberAndPageType(processNum, 3)` 替代 `viewPageButtonService`

**写路径变更：**
- `BpmnInsertVariablesImpl.insertViewPageButton()` — 使用 `bpmVariableButtonService.saveBatch()` 替代 `viewPageButtonService`

### Phase 8: 审批附件合并

| 表名 | 说明 | 替代方案 | 删除的文件 |
|------|------|---------|-----------|
| `bpm_verify_attachment` | 审批记录附件 | `bpm_verify_info` 新增 `attachments_json TEXT` 列 | `BpmVerifyAttachment.java`, `BpmVerifyAttachmentMapper.java`, `BpmVerifyAttachmentMapper.xml`, `BpmVerifyAttachmentService.java`, `BpmVerifyAttachmentServiceImpl.java` |

**Schema 变更：**
- `bpm_verify_info` 新增 `attachments_json TEXT` 列，存储 `List<BpmVerifyAttachmentVo>` 的 JSON

**读路径变更：**
- `BpmVerifyInfoBizServiceImpl` — 从 `bpmVerifyInfoVo.getAttachmentsJson()` 解析 JSON 替代子表查询
- `BpmVerifyInfoMapper.xml` — `getVerifyInfo` SQL 新增 `attachments_json` 字段

**写路径变更：**
- `ResubmitProcessImpl` — 在 `addVerifyInfo()` 前将 `vo.getVerifyAttachments()` 序列化为 JSON 设置到 `bpmVerifyInfo`

**保留的文件：** `BpmVerifyAttachmentVo.java`（JSON 序列化仍使用此 VO）

### Phase 9: 快捷入口类型合并

| 表名 | 说明 | 替代方案 | 删除的文件 |
|------|------|---------|-----------|
| `t_quick_entry_type` | 快捷入口类型（PC/APP） | `t_quick_entry` 新增 `type_config_json VARCHAR(500)` 列 | `QuickEntryType.java`, `QuickEntryTypeMapper.java`, `QuickEntryTypeService.java`, `QuickEntryTypeServiceImpl.java` |

**Schema 变更：**
- `t_quick_entry` 新增 `type_config_json VARCHAR(500)` 列，存储 `[{"type":1,"typeName":"PC"},{"type":2,"typeName":"APP"}]`

**读路径变更：**
- `QuickEntryMapper.xml` — `listQuickEntry` 移除 `GROUP_CONCAT` 子查询，改为直接选择 `type_config_json`
- `QuickEntryMapper.xml` — `searchQuickEntry` 使用 `JSON_CONTAINS(type_config_json, '{"type":2}')` 替代子查询
- `QuickEntryServiceImpl.getPcProcessData()` — 从 `typeConfigJson` JSON 解析 `types` 列表
- `QuickEntryBizServiceImpl.listQuickEntry(Boolean isApp)` — 查询全量后在 Java 层按 JSON 过滤

**写路径变更：**
- `QuickEntryBizServiceImpl.editQuickEntry()` — 将 `vo.getTypes()` 序列化为 JSON 存入 `typeConfigJson`

**保留的文件：** `QuickEntryTypeVo.java`（测试文件引用）

---

## 保留的表

| 表名 | 说明 | 新增字段 |
|------|------|---------|
| `t_bpmn_conf` | 流程配置主表 | `conf_config_json` TEXT |
| `t_bpmn_node` | 节点主表 | `node_config_json` TEXT |
| `t_bpmn_node_to` | 节点流转关系 | — |
| `t_bpm_variable` | 流程变量主表 | — |
| `t_bpm_variable_button` | 流程变量按钮 | `view_type` INT |
| `bpm_verify_info` | 审批记录 | `attachments_json` TEXT |
| `t_quick_entry` | 快捷入口 | `type_config_json` VARCHAR(500) |

---

## 迁移路线总结

```
第一批（t_bpmn_* 配置子表 → JSON）: 26 张表
  ├── commit: 7a0bea4e "清除业务表"
  └── DDL 已从 bpm_init_db.sql 移除

第二批（Activiti 冗余表裁剪）: 12 张表
  ├── commits: 081f09e8 ~ 159c5570
  └── 代码已移除，act_init_db.sql 中已无这些表

第三批（死表/废弃表）: 4 张表
  ├── t_bpm_business: da9da2d0 "清除死表"
  ├── t_bpm_variable_single: b80699f3 "清除t_bpm_variable_single"
  ├── bpm_process_name_relevancy: 1544f51d "清除 bpm_process_name_relevancy"
  └── t_dict_main: 本次会话 Phase 1a

第四批（子表合并/JSON 化）: 10 张表
  ├── bpm_process_notice → conf_config_json (Phase 3)
  ├── bpm_process_node_overtime → node_config_json (Phase 4)
  ├── bpm_process_operation → node_config_json (Phase 5)
  ├── bpm_process_node_back → node_config_json (Phase 6)
  ├── t_bpm_variable_view_page_button → t_bpm_variable_button (Phase 7)
  ├── bpm_verify_attachment → bpm_verify_info (Phase 8)
  ├── t_quick_entry_type → t_quick_entry (Phase 9)
  ├── t_dict_main (Phase 1a, 死表)
  ├── t_bpm_variable_sequence_flow (Phase 1b, 写死表)
  └── bpm_process_dept (Phase 2, 废弃表)

合计: 52 张表
```

---

## 注意事项

1. **数据迁移**：第四批合并表需要执行数据迁移脚本，将子表数据回填到父表的 JSON 字段
2. **迁移脚本位置**：待创建 `script/migrate_tables_to_json.sql`
3. **验证步骤**：
   - `mvn clean compile` 编译通过
   - `mvn test` 测试通过
   - Grep 确认无残留引用
4. **生产环境建议**：先在测试环境验证，确认无误后再在生产环境执行
