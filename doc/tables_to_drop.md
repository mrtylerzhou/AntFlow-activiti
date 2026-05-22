# BPMN 配置表精简 — 可删除表清单

| # | 待删除表名 | 说明 | 替代方案 | 状态 |
|---|-----------|------|---------|------|
| 1 | `t_bpmn_view_page_button` | 流程级视图页按钮 | `t_bpmn_conf.conf_config_json` -> `viewPageButtons[]` | 读写已解耦 |
| 2 | `t_bpmn_conf_notice_template` | 通知模板头 | `t_bpmn_conf.conf_config_json` -> `noticeTemplateConfig` | 读写已解耦 |
| 3 | `t_bpmn_conf_notice_template_detail` | 通知模板明细 | `t_bpmn_conf.conf_config_json` -> `noticeTemplateConfig.details[]` | 读写已解耦 |
| 4 | `t_bpmn_node_button_conf` | 节点按钮配置 | `t_bpmn_node.node_config_json` -> `buttonSignConf.buttonConfList[]` | 读写已解耦 |
| 5 | `t_bpmn_node_sign_up_conf` | 节点签收配置 | `t_bpmn_node.node_config_json` -> `buttonSignConf.signUpConf` | 读写已解耦 |
| 6 | `t_bpmn_node_labels` | 节点标签 | `t_bpmn_node.node_config_json` -> `buttonSignConf.labels[]` | 读写已解耦 |
| 7 | `t_bpmn_node_additional_sign_conf` | 额外加签审批人 | `t_bpmn_node.node_config_json` -> `buttonSignConf.additionalSignConfList[]` | 读写已解耦 |
| 8 | `t_bpmn_node_personnel_conf` | 指定人员审批（主表） | `t_bpmn_node.node_config_json` -> `approverConf.personnelConf` | 读写已解耦 |
| 9 | `t_bpmn_node_personnel_empl_conf` | 指定人员审批（人员明细） | `t_bpmn_node.node_config_json` -> `approverConf.personnelConf.employees[]` | 读写已解耦 |
| 10 | `t_bpmn_node_role_conf` | 指定角色审批 | `t_bpmn_node.node_config_json` -> `approverConf.roleConfList[]` | 读写已解耦 |
| 11 | `t_bpmn_node_role_outside_emp_conf` | 角色外部人员 | `t_bpmn_node.node_config_json` -> `approverConf.roleConfList[].outsideEmployees[]` | 读写已解耦 |
| 12 | `t_bpmn_node_loop_conf` | 层层审批配置 | `t_bpmn_node.node_config_json` -> `approverConf.loopConf` | 读写已解耦 |
| 13 | `t_bpmn_node_assign_level_conf` | 指定层级审批 | `t_bpmn_node.node_config_json` -> `approverConf.assignLevelConf` | 读写已解耦 |
| 14 | `t_bpmn_node_hrbp_conf` | HRBP 审批配置 | `t_bpmn_node.node_config_json` -> `approverConf.hrbpConf` | 读写已解耦 |
| 15 | `t_bpmn_node_customize_conf` | 自选审批人配置 | `t_bpmn_node.node_config_json` -> `approverConf.customizeConf` | 读写已解耦 |
| 16 | `t_bpmn_node_udr_conf` | 自定义规则审批 | `t_bpmn_node.node_config_json` -> `approverConf.udrConfList[]` | 读写已解耦 |
| 17 | `t_bpmn_node_form_related_user_conf` | 表单关联用户审批 | `t_bpmn_node.node_config_json` -> `approverConf.formRelatedUserConfList[]` | 读写已解耦 |
| 18 | `t_bpmn_node_out_side_access_conf` | 外部接入审批 | `t_bpmn_node.node_config_json` -> `approverConf.outSideAccessConf` | 读写已解耦 |
| 19 | `t_bpmn_node_business_table_conf` | 关联业务表审批 | `t_bpmn_node.node_config_json` -> `approverConf.businessTableConf` | 读写已解耦 |
| 20 | `t_bpmn_node_conditions_conf` | 条件节点配置 | `t_bpmn_node.node_config_json` -> `conditionsConf.conditionGroups[].{isDefault,groupRelation,sort,extJson}` | 读写已解耦 |
| 21 | `t_bpmn_node_conditions_param_conf` | 条件参数配置 | `t_bpmn_node.node_config_json` -> `conditionsConf.conditionGroups[].extJson`（Vue3 模型已包含 `optType`/`zdy1` 等全部数据，`operator` 字段为历史遗留，可从 `optType` 推导） | **可安全删除** |
| 22 | `t_out_side_bpmn_node_conditions_conf` | 外部条件配置 | `t_bpmn_node.node_config_json` -> `conditionsConf.outSideConditionId` | 读写已解耦 |
| 23 | `t_bpmn_template`（node_id 非空） | 节点级通知模板 | `t_bpmn_node.node_config_json` -> `templateConf.templates[]` | 读写已解耦 |
| 24 | `t_bpmn_approve_remind` | 审批催办配置 | `t_bpmn_node.node_config_json` -> `templateConf.approveRemind` | 读写已解耦 |
| 25 | `t_bpmn_node_lf_formdata_field_control` | 低代码字段权限 | `t_bpmn_node.node_config_json` -> `lowCodeConf.fieldControls[]` | 读写已解耦 |
| 26 | `t_bpmn_template`（node_id 为空） | 流程级通知模板 | `t_bpmn_conf.conf_config_json` -> `confTemplates[]` | 读写已解耦 |

## 保留的表

| 表名 | 说明 |
|------|------|
| `t_bpmn_conf` | 流程配置主表（新增 `conf_config_json` 字段） |
| `t_bpmn_node` | 节点主表（新增 `node_config_json` 字段） |
| `t_bpmn_node_to` | 节点流转关系 |
| `t_bpmn_template` | conf 级别模板（node_id IS NULL 的记录，已迁入 JSON 但保留兼容） |

## 状态说明

- **读写已解耦**：读路径 JSON-first + DB fallback，写路径双写（DB + JSON）。数据迁移完成后即可删除
- **可安全删除**：读路径和写路径均不再依赖该表，无任何运行时引用，可直接 DROP

## 注意事项

- #21 `t_bpmn_node_conditions_param_conf` 已完全解耦：
  - 读路径：`NodeTypeConditionsAdp.formatFromJson()` 从 `extJson` 推导运行时字段
  - 写路径：`editBpmnNode()` 已移除 param conf 插入逻辑
  - 运行时检查：`OutSideBpmConditionsTemplateBizServiceImpl.templateIsUsed()` 已切换为从 `extJson` 读取模板标识（columnId=9999）
  - JSON 构建：`buildConditionsJsonFromDb()` 已移除 param conf 查询
- 其余表（#1-#20, #22-#26）读写已解耦，但写路径仍双写 DB + JSON，需数据迁移后方可删除
- 删除前需先执行数据迁移脚本，将现有子表数据回填到 JSON 字段
- 建议在测试环境验证无误后再在生产环境执行
