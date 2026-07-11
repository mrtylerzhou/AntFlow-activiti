-- =====================================================================
-- Multi-form support for low-code flow
-- 支持低代码流程绑定多个外部表单
-- =====================================================================

-- 1. t_bpmn_conf_lf_formdata: 扩展为同时承载"独立表单模板"和"内联设计表单"
--    bpmn_conf_id 为 NULL => 独立表单(由表单管理模块管理); 非 NULL => 内联表单(向后兼容)
ALTER TABLE `t_bpmn_conf_lf_formdata`
    ADD COLUMN `form_code` varchar(100) NULL DEFAULT NULL COMMENT '独立表单家族标识(同族各版本共享;内联表单为NULL)' AFTER `bpmn_conf_id`,
    ADD COLUMN `form_name` varchar(255) NULL DEFAULT NULL COMMENT '独立表单显示名(内联表单为NULL)' AFTER `form_code`,
    ADD COLUMN `effective_status` tinyint NOT NULL DEFAULT 0 COMMENT '是否当前生效版本 0否 1是(仅独立表单使用;内联表单恒为0)' AFTER `form_name`,
    MODIFY COLUMN `bpmn_conf_id` bigint NULL DEFAULT NULL COMMENT '流程配置ID(独立表单为NULL)';
-- 独立表单查询索引(按家族列出生效版本)
ALTER TABLE `t_bpmn_conf_lf_formdata`
    ADD KEY `idx_lf_formdata_form_code_eff` (`form_code`, `effective_status`),
    ADD KEY `idx_lf_formdata_bpmn_conf_id` (`bpmn_conf_id`);

-- 2. t_bpmn_conf: 外部表单模式所需的引用列表
--    lf_formdata_ids: CSV of t_bpmn_conf_lf_formdata.id (版本id), 顺序即 tab 顺序
--    模式标记复用 extra_flags 位掩码(BpmnConfFlagsEnum.USE_EXTERNAL_FORM=0b1000000), 无需新增列
ALTER TABLE `t_bpmn_conf`
    ADD COLUMN `lf_formdata_ids` varchar(500) NULL DEFAULT NULL COMMENT '外部表单引用的表单版本id列表(CSV),仅外部表单模式使用' AFTER `is_lowcode_flow`;

-- 3. t_lf_main_field: 多表单已填数据按表单版本区分
--    formdata_id 指向 t_bpmn_conf_lf_formdata.id; 旧数据为NULL => 内联模式回退
ALTER TABLE `t_lf_main_field`
    ADD COLUMN `formdata_id` bigint NULL DEFAULT NULL COMMENT '表单版本ID(t_bpmn_conf_lf_formdata.id);内联模式旧数据为NULL' AFTER `form_code`;
ALTER TABLE `t_lf_main_field`
    ADD KEY `idx_lf_main_field_formdata_id` (`formdata_id`);
