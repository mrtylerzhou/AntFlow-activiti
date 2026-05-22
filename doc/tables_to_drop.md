# BPMN 配置表精简 — 可删除表清单

| # | 待删除表名 | 说明 | 替代方案 |
|---|-----------|------|---------|
| 1 | `t_bpmn_view_page_button` | 流程级视图页按钮 | `t_bpmn_conf.conf_config_json` -> `viewPageButtons[]` |
| 2 | `t_bpmn_conf_notice_template` | 通知模板头 | `t_bpmn_conf.conf_config_json` -> `noticeTemplateConfig` |
| 3 | `t_bpmn_conf_notice_template_detail` | 通知模板明细 | `t_bpmn_conf.conf_config_json` -> `noticeTemplateConfig.details[]` |
| 4 | `t_bpmn_node_button_conf` | 节点按钮配置 | `t_bpmn_node.node_config_json` -> `buttonSignConf.buttonConfList[]` |
| 5 | `t_bpmn_node_sign_up_conf` | 节点签收配置 | `t_bpmn_node.node_config_json` -> `buttonSignConf.signUpConf` |
| 6 | `t_bpmn_node_labels` | 节点标签 | `t_bpmn_node.node_config_json` -> `buttonSignConf.labels[]` |
| 7 | `t_bpmn_node_additional_sign_conf` | 额外加签审批人 | `t_bpmn_node.node_config_json` -> `buttonSignConf.additionalSignConfList[]` |
| 8 | `t_bpmn_node_personnel_conf` | 指定人员审批（主表） | `t_bpmn_node.node_config_json` -> `approverConf.personnelConf` |
| 9 | `t_bpmn_node_personnel_empl_conf` | 指定人员审批（人员明细） | `t_bpmn_node.node_config_json` -> `approverConf.personnelConf.employees[]` |
| 10 | `t_bpmn_node_role_conf` | 指定角色审批 | `t_bpmn_node.node_config_json` -> `approverConf.roleConfList[]` |
| 11 | `t_bpmn_node_role_outside_emp_conf` | 角色外部人员 | `t_bpmn_node.node_config_json` -> `approverConf.roleConfList[].outsideEmployees[]` |
| 12 | `t_bpmn_node_loop_conf` | 层层审批配置 | `t_bpmn_node.node_config_json` -> `approverConf.loopConf` |
| 13 | `t_bpmn_node_assign_level_conf` | 指定层级审批 | `t_bpmn_node.node_config_json` -> `approverConf.assignLevelConf` |
| 14 | `t_bpmn_node_hrbp_conf` | HRBP 审批配置 | `t_bpmn_node.node_config_json` -> `approverConf.hrbpConf` |
| 15 | `t_bpmn_node_customize_conf` | 自选审批人配置 | `t_bpmn_node.node_config_json` -> `approverConf.customizeConf` |
| 16 | `t_bpmn_node_udr_conf` | 自定义规则审批 | `t_bpmn_node.node_config_json` -> `approverConf.udrConfList[]` |
| 17 | `t_bpmn_node_form_related_user_conf` | 表单关联用户审批 | `t_bpmn_node.node_config_json` -> `approverConf.formRelatedUserConfList[]` |
| 18 | `t_bpmn_node_out_side_access_conf` | 外部接入审批 | `t_bpmn_node.node_config_json` -> `approverConf.outSideAccessConf` |
| 19 | `t_bpmn_node_business_table_conf` | 关联业务表审批 | `t_bpmn_node.node_config_json` -> `approverConf.businessTableConf` |
| 20 | `t_bpmn_node_conditions_conf` | 条件节点配置 | `t_bpmn_node.node_config_json` -> `conditionsConf.conditionGroups[].{isDefault,groupRelation,sort,extJson}` |
| 21 | `t_bpmn_node_conditions_param_conf` | 条件参数配置（冗余） | `t_bpmn_node.node_config_json` -> `conditionsConf.conditionGroups[].extJson`（Vue3 模型已包含 `optType`/`zdy1` 等全部数据，`operator` 字段为历史遗留，可从 `optType` 推导） |
| 22 | `t_out_side_bpmn_node_conditions_conf` | 外部条件配置 | `t_bpmn_node.node_config_json` -> `conditionsConf.outSideConditionId` |
| 23 | `t_bpmn_template`（node_id 非空） | 节点级通知模板 | `t_bpmn_node.node_config_json` -> `templateConf.templates[]` |
| 24 | `t_bpmn_approve_remind` | 审批催办配置 | `t_bpmn_node.node_config_json` -> `templateConf.approveRemind` |
| 25 | `t_bpmn_node_lf_formdata_field_control` | 低代码字段权限 | `t_bpmn_node.node_config_json` -> `lowCodeConf.fieldControls[]` |
| 26 | `t_bpmn_template`（node_id 为空） | 流程级通知模板 | `t_bpmn_conf.conf_config_json` -> `confTemplates[]` |

## 保留的表

| 表名 | 说明 |
|------|------|
| `t_bpmn_conf` | 流程配置主表（新增 `conf_config_json` 字段） |
| `t_bpmn_node` | 节点主表（新增 `node_config_json` 字段） |
| `t_bpmn_node_to` | 节点流转关系 |
| `t_bpmn_template` | conf 级别模板（node_id IS NULL 的记录，已迁入 JSON 但保留兼容） |

## 注意事项

- #20-#22 条件表的读路径已切换为 JSON-first（`NodeTypeConditionsAdp.formatFromJson`），`extJson` 中的 Vue3 模型已包含全部运行时所需字段（`conditionParamTypes`、`groupedNumberOperatorListMap` 等），无需再查 `t_bpmn_node_conditions_param_conf`
- #21 `t_bpmn_node_conditions_param_conf` 的 `operator` 字段为历史遗留设计（数字类型会多写入一条），实际可从 Vue3 模型的 `optType` 推导，删除后不影响功能
- 写路径（`editBpmnNode`）仍会写入 DB 表（`OutSideBpmConditionsTemplateBizServiceImpl` 运行时依赖），待该依赖也切换到 JSON 后可完全停止写入
- 删除前需先执行数据迁移脚本，将现有子表数据回填到 JSON 字段
- 建议在测试环境验证无误后再在生产环境执行
