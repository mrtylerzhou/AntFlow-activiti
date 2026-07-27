-- 流程效能统计表
CREATE TABLE `bpm_process_efficiency`
(
    `id`                  bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `form_code`           varchar(64)  DEFAULT NULL COMMENT '流程类型编码',
    `process_number`      varchar(64)  DEFAULT NULL COMMENT '流程编号',
    `proc_inst_id`        varchar(64)  DEFAULT NULL COMMENT '流程实例ID',
    `execution_id`        varchar(64)  DEFAULT NULL COMMENT '执行ID',
    `task_def_key`        varchar(255) DEFAULT NULL COMMENT '任务定义Key',
    `node_name`           varchar(255) DEFAULT NULL COMMENT '节点名称',
    `assignee`            varchar(500) DEFAULT NULL COMMENT '审批人ID(节点级逗号分隔,流程级null)',
    `assignee_name`       varchar(500) DEFAULT NULL COMMENT '审批人姓名(节点级逗号分隔,流程级null)',
    `static_type`         tinyint      NOT NULL COMMENT '统计类型:1=任务,2=节点,3=流程',
    `start_time`          datetime     DEFAULT NULL COMMENT '开始时间',
    `end_time`            datetime     DEFAULT NULL COMMENT '结束时间(未完成存null)',
    `duration`            bigint       DEFAULT NULL COMMENT '耗时(毫秒)',
    `process_state`       int          DEFAULT NULL COMMENT '流程状态(冗余)',
    `process_create_time` datetime     DEFAULT NULL COMMENT '流程创建时间(冗余,用于筛选)',
    `tenant_id`           varchar(255) NOT NULL DEFAULT '' COMMENT '租户ID',
    `is_del`              tinyint      DEFAULT '0' COMMENT '删除标记:0否1是',
    `create_time`         datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time`         datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_form_code` (`form_code`) USING BTREE,
    KEY `idx_process_number` (`process_number`) USING BTREE,
    KEY `idx_proc_inst_id` (`proc_inst_id`) USING BTREE,
    KEY `idx_static_type` (`static_type`) USING BTREE,
    KEY `idx_start_time` (`start_time`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  COMMENT = '流程效能统计表';
