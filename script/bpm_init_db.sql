DROP TABLE IF EXISTS `t_bpmn_conf`;
CREATE TABLE if not exists `t_bpmn_conf`
(
    `id`                  int           NOT NULL AUTO_INCREMENT COMMENT 'Auto Incr ID',
    `bpmn_code`           varchar(60)         NOT NULL DEFAULT '' COMMENT 'Process code',
    `bpmn_name`           varchar(60)         NOT NULL DEFAULT '' COMMENT 'Process Name',
    `bpmn_type`           int                      DEFAULT NULL COMMENT 'Process Type',
    `form_code`           varchar(100)        NOT NULL DEFAULT '' COMMENT 'Process Business Code',
    `app_id`              int                      DEFAULT NULL COMMENT 'associated app id',
    `deduplication_type`  int             NOT NULL DEFAULT '1' COMMENT 'deduplication way 1.no deduplication,2 forward deduplication,3.backward deduplication',
    `effective_status`    int             NOT NULL DEFAULT '0' COMMENT 'is effect 0:no 1:yes',
    `is_all`              int             NOT NULL DEFAULT '0' COMMENT 'is to all,0 no 1yes',
    `is_out_side_process` int                      DEFAULT '0' COMMENT 'is it a third party process',
    `is_lowcode_flow` tinyint default 0 null comment '是否是低代码审批流0,否,1是',
    `business_party_id`   int                      DEFAULT NULL COMMENT 'its belong to business party',
    `extra_flags`         int                                           null,
    `conf_config_json`    longtext                                      null COMMENT 'consolidated conf-level configuration JSON',
    `remark`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'remark',
    `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    `is_del`              tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0:in use,1:delete',
    `create_user`         varchar(32)                  DEFAULT '' COMMENT 'as its name says',
    `create_time`         timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'as its name says',
    `update_user`         varchar(32)                  DEFAULT '' COMMENT '更新人',
    `update_time`         timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'as its name says',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `bpmn_code` (`bpmn_code`) USING BTREE,
    KEY `index_business_party_id` (`business_party_id`) USING BTREE,
    KEY `index_form_code` (`form_code`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
   COMMENT ='process main configuration table';

DROP TABLE IF EXISTS `t_bpmn_node`;
CREATE TABLE if not exists `t_bpmn_node`
(
    `id`                bigint          NOT NULL AUTO_INCREMENT COMMENT 'id',
    `conf_id`           bigint          NOT NULL COMMENT 'the main conf id',
    `node_id`           varchar(60)        NOT NULL DEFAULT '' COMMENT 'node id',
    `node_type`         int            NOT NULL COMMENT 'its node type,see NodeTypeEnum for detail',
    `node_property`     int            NOT NULL COMMENT 'node property,rules for finding out approvers,see NodePropertyEnum for detail',
    `node_from`         varchar(60)         NOT NULL DEFAULT '' COMMENT 'its prev node',
    `node_froms`        varchar(255)                 DEFAULT NULL COMMENT 'all its prev nodes',
    `batch_status`      int            NOT NULL DEFAULT '0' COMMENT 'can the process approved in batch,0:no,1:Yes',
    `approval_standard` int           NOT NULL DEFAULT '1' COMMENT 'approve standard,1 startup user,2 approved',
    `node_name`         varchar(255)                 DEFAULT NULL COMMENT 'node name',
    `node_display_name` varchar(255)                  DEFAULT '' COMMENT 'node display name shown in web or app',
    `annotation`        varchar(255)                 DEFAULT NULL COMMENT 'annotation on this conf',
    `is_deduplication`  int           NOT NULL DEFAULT '0' COMMENT 'whether this node should be deduplicated,0:No,1:Yes',
    `deduplicationExclude` tinyint             default 0                 null comment '0 for no,default value,and 1 for yes',
    `is_dynamicCondition` tinyint default 0 not null comment '是否是动态条件节点,0,否,1是',
     `is_parallel`         tinyint             default 0                 null,
    `is_sign_up`        int            NOT NULL DEFAULT '0' COMMENT 'whether this node can be sign up,0:No,1:Yes',
    `no_header_action`  tinyint             NULL,
    `remark`            varchar(255)        NOT NULL DEFAULT '' COMMENT 'remark',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
     `extra_flags`          int                                        null,
    `node_config_json`   longtext                                     null COMMENT 'consolidated node-level configuration JSON',
    `is_del`            tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0:No,1:yes',
    `create_user`       varchar(50)                  DEFAULT '' COMMENT 'as its name says',
    `create_time`       timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'as its name says',
    `update_user`       varchar(50)                  DEFAULT '' COMMENT 'as its name says',
    `update_time`       timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'as its name says',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `index_conf_id` (`conf_id`) USING BTREE,
    KEY `t_bpmn_node_dx2` (`node_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
   COMMENT ='the conf,s node table';

DROP TABLE IF EXISTS `t_bpmn_node_to`;
CREATE TABLE if not exists `t_bpmn_node_to`
(
    `id`           bigint          NOT NULL AUTO_INCREMENT COMMENT 'id',
    `bpmn_node_id` bigint          NOT NULL COMMENT 'node id',
    `node_to`      varchar(60)         NOT NULL DEFAULT '' COMMENT 'node to',
    `remark`       varchar(255)        NOT NULL DEFAULT '' COMMENT 'remark',
    `is_del`       tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0:no,1:yes',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    `create_user`  varchar(50)                  DEFAULT '' COMMENT 'as its name says',
    `create_time`  timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'as its name says',
    `update_user`  varchar(50)                  DEFAULT '' COMMENT 'as its name says',
    `update_time`  timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'as its name says',
    PRIMARY KEY (`id`) USING BTREE,
     KEY `t_bpmn_node_to_idx1` (`bpmn_node_id`),
     KEY `t_bpmn_node_to_idx2` (`node_to`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
   COMMENT ='审批流节点走向表';







DROP TABLE IF EXISTS `t_information_template`;
CREATE TABLE if not exists `t_information_template`
(
    `id`             bigint   NOT NULL AUTO_INCREMENT,
    `name`           varchar(30)  NOT NULL DEFAULT '' COMMENT 'name',
    `num`            varchar(10)  NOT NULL DEFAULT '' COMMENT 'num',
    `system_title`   varchar(100) NOT NULL DEFAULT '' COMMENT 'title',
    `system_content` varchar(500) NOT NULL DEFAULT '' COMMENT 'content',
    `mail_title`     varchar(100) NOT NULL DEFAULT '' COMMENT 'mail title',
    `mail_content`   varchar(500) NOT NULL DEFAULT '' COMMENT 'mail content',
    `note_content`   varchar(200) NOT NULL DEFAULT '' COMMENT 'sms content',
    `jump_url`       int               DEFAULT NULL COMMENT 'url to jump to',
    `remark`         varchar(200) NOT NULL DEFAULT '' COMMENT 'remark',
    `status`         tinyint   NOT NULL DEFAULT '0' COMMENT 'status 0:in use,1:disabled',
     `event`          int                                    null,
    `event_name`     varchar(50)              null,
    `is_default`     tinyint   NOT NULL DEFAULT '0' COMMENT 'is default template for event, 0:no,1:yes',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    `is_del`         tinyint   NOT NULL DEFAULT '0' COMMENT '0:no,1:yes',
    `create_time`    timestamp    NOT NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'as its name says',
    `create_user`    varchar(50)          DEFAULT '' COMMENT 'as its name says',
    `update_time`    timestamp    NOT NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'as its name says',
    `update_user`    varchar(50)          DEFAULT '' COMMENT 'as its name says',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
   COMMENT ='消息模板';

DROP TABLE IF EXISTS `bpm_business_draft`;
CREATE TABLE if not exists `bpm_business_draft`
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `bpmn_code`      varchar(64)   DEFAULT NULL COMMENT 'business id',
    `create_time`      timestamp    not null default CURRENT_TIMESTAMP COMMENT 'as its name says',
    `process_code`     varchar(50)  DEFAULT NULL COMMENT 'process Number',
    `create_user_name` varchar(50)  DEFAULT NULL COMMENT 'as its name says',
    `create_user`      varchar(50)   DEFAULT NULL COMMENT 'as its name says',
    `process_key`      varchar(50) DEFAULT NULL COMMENT 'as its name says',
     `draft_json`       text                                   null,
    `is_del`           int      DEFAULT '0',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    PRIMARY KEY (`id`) USING BTREE,
     constraint bpm_business_draft_unq1
            unique (bpmn_code, create_user),
    KEY `bpm_business_idx1` (`process_key`)
) ENGINE = InnoDB
   COMMENT ='process draft';

DROP TABLE IF EXISTS `bpm_flowrun_entrust`;
CREATE TABLE if not exists `bpm_flowrun_entrust`
(
    `id`          int NOT NULL AUTO_INCREMENT,
    `runinfoid`   varchar(64)      DEFAULT NULL COMMENT 'process instance id',
    `runtaskid`   varchar(64)      DEFAULT NULL COMMENT 'task id',
    `original`    varchar(64)          DEFAULT NULL COMMENT 'original assignee',
     `original_name`    varchar(255)          DEFAULT NULL COMMENT 'original assignee name',
    `actual`      varchar(64)          DEFAULT NULL COMMENT 'actual assignee',
     `actual_name`   varchar(100)  null comment 'actual assignee name',
    `type`        int          DEFAULT NULL COMMENT 'type 1: entrust 2:view',
    `is_read`     int          DEFAULT '2' COMMENT 'is read 1:yes,2:no',
    `proc_def_id` varchar(100)     DEFAULT NULL COMMENT 'proces deployment id',
    `is_view`     int NOT NULL DEFAULT '0',
    `is_del`           int      DEFAULT '0',
    `node_id`        varchar(64)            null,
    `action_type`    int         default 0  null comment '0 global user configed entrust,1.change assignee entrust,2 add assignee 3 remove assignee',
    `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `BPM_IDX_ID` (`runinfoid`, `original`, `actual`) USING BTREE
) ENGINE = InnoDB
   COMMENT ='entrust and forward view conf table';


DROP TABLE IF EXISTS `bpm_flowruninfo`;
CREATE TABLE if not exists `bpm_flowruninfo`
(
    `id`            bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `runinfoid`     varchar(64) NOT NULL COMMENT 'process instance id',
    `create_UserId` varchar(64)   DEFAULT NULL COMMENT 'userid',
    `entitykey`     varchar(100) DEFAULT NULL COMMENT 'business key',
    `entityclass`   varchar(100) DEFAULT NULL COMMENT 'entity class',
    `entitykeytype` varchar(10)  DEFAULT NULL COMMENT 'business type',
    `createactor`   varchar(50)  DEFAULT NULL COMMENT 'create user',
    `createdepart`  varchar(100) DEFAULT NULL COMMENT 'create user,s depart',
    `createdate`    timestamp  NOT NULL     DEFAULT CURRENT_TIMESTAMP NULL COMMENT 'create time',
     `is_del`           int      DEFAULT '0',
      `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `bpm_flowruninfo_idx1` (`runinfoid`)
) ENGINE = InnoDB
  COMMENT ='process run time info';

DROP TABLE IF EXISTS `bpm_manual_notify`;
CREATE TABLE if not exists `bpm_manual_notify`
(
    `id`          int     NOT NULL AUTO_INCREMENT,
    `business_id` bigint  NOT NULL COMMENT 'business id',
    `code`        varchar(10) NOT NULL COMMENT 'process type',
    `last_time`   timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'latest remind time',
    `create_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     `is_del`           int      DEFAULT '0',
      `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `bpm_manual_notify_idx1` (`business_id`)
) ENGINE = InnoDB
   COMMENT ='manual notify';








DROP TABLE IF EXISTS `bpm_process_forward`;
CREATE TABLE if not exists `bpm_process_forward`
(
    `id`                 int      NOT NULL AUTO_INCREMENT,
    `forward_user_id`    varchar(50)            DEFAULT NULL COMMENT 'forwarded user id',
    `Forward_user_name`  varchar(50)           DEFAULT NULL COMMENT 'forwarded user name',
    `processInstance_Id` varchar(64)           DEFAULT NULL COMMENT 'process instance id',
     `node_id`            varchar(64)                            null,
    `create_time`        timestamp             not null default CURRENT_TIMESTAMP COMMENT 'as its name says',
    `create_user_id`     varchar(50)            DEFAULT NULL COMMENT 'as its name says',
    `task_id`            varchar(50)           DEFAULT NULL COMMENT 'taskid',
    `is_read`            int               DEFAULT '0' COMMENT 'is read',
    `is_del`             int               DEFAULT '0',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    `update_time`        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `process_number`     varchar(50) NOT NULL DEFAULT '' COMMENT 'process number',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `forward_user_id` (`forward_user_id`) USING BTREE,
    KEY `index_forward_user_id_is_read` (`forward_user_id`, `is_read`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
   COMMENT ='process forward table';


DROP TABLE IF EXISTS `bpm_process_node_submit`;
CREATE TABLE if not exists `bpm_process_node_submit`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `processInstance_Id` varchar(64)         DEFAULT NULL COMMENT 'process instance id',
    `back_type`          tinyint             DEFAULT NULL COMMENT 'back type',
    `node_key`           varchar(50)         DEFAULT NULL COMMENT 'node key',
    `create_time`        timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_user`        varchar(50)          DEFAULT NULL COMMENT 'creator',
    `state`              tinyint             DEFAULT NULL COMMENT 'state',
    `is_del`             int              DEFAULT '0',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_processInstance_Id` (`processInstance_Id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  COMMENT ='process node submit';


DROP TABLE IF EXISTS `bpm_taskconfig`;
CREATE TABLE if not exists `bpm_taskconfig`
(
    `id`            bigint NOT NULL AUTO_INCREMENT,
    `proc_def_id_`  varchar(100) DEFAULT NULL COMMENT 'process def id',
    `task_def_key_` varchar(100) DEFAULT NULL COMMENT 'task def key',
    `user_id`       bigint   DEFAULT NULL COMMENT 'user id',
    `number`        int      DEFAULT NULL COMMENT 'number',
    `status`        tinyint      DEFAULT NULL COMMENT 'status',
    `original_type` tinyint       DEFAULT NULL COMMENT 'orginal type',
    `is_del`             int               DEFAULT '0',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `BPM_IDX__TASK_CONFIG` (`proc_def_id_`, `task_def_key_`) USING BTREE
) ENGINE = InnoDB
   COMMENT ='task config';



DROP TABLE IF EXISTS `t_bpm_variable`;
CREATE TABLE if not exists `t_bpm_variable`
(
    `id`                       bigint          NOT NULL AUTO_INCREMENT COMMENT 'id',
    `process_num`              varchar(60)         NOT NULL DEFAULT '' COMMENT 'process number',
    `process_name`             varchar(60)         NOT NULL DEFAULT '' COMMENT 'process name',
    `process_desc`             varchar(255)        NOT NULL DEFAULT '' COMMENT 'process desc',
    `process_start_conditions` text                NOT NULL COMMENT 'process start conditions',
    `bpmn_code`                varchar(60)         NOT NULL DEFAULT '' COMMENT 'bpmn code',
    `is_new_data`              int                      DEFAULT '0' COMMENT 'is new data 0:no 1:yes',
    `variable_config_json`     text                         DEFAULT NULL COMMENT 'JSON config for buttons, messages, sign-ups, approve-reminds',
    `remark`                   varchar(255)        NOT NULL DEFAULT '' COMMENT 'remark',
    `is_del`                   tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0:no,1:yes',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    `create_user`              varchar(50)                  DEFAULT '' COMMENT 'as its name says',
    `create_time`              timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'as its name says',
    `update_user`              varchar(50)                  DEFAULT '' COMMENT 'as its name says',
    `update_time`              timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'as its name says',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `index_process_num` (`process_num`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
   COMMENT ='process variable table';

DROP TABLE IF EXISTS `t_bpm_variable_multiplayer`;
CREATE TABLE if not exists `t_bpm_variable_multiplayer`
(
    `id`              bigint          NOT NULL AUTO_INCREMENT COMMENT 'id',
    `variable_id`     bigint          NOT NULL COMMENT 'variable id',
    `element_id`      varchar(60)         NOT NULL DEFAULT '' COMMENT 'element id',
    `element_name`    varchar(60)         NOT NULL DEFAULT '' COMMENT 'element name',
     `node_id`        varchar(60)                                             null,
    `collection_name` varchar(60)         NOT NULL DEFAULT '' COMMENT 'collection name',
    `sign_type`       int             NOT NULL COMMENT 'sign type 1: all sign 2:or sign',
    `remark`          varchar(255)        NOT NULL DEFAULT '' COMMENT 'remark',
    `is_del`          tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0:no,1:yes',
    `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    `create_user`     varchar(50)                  DEFAULT '' COMMENT 'as its name says',
    `create_time`     timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'as its name says',
    `update_user`     varchar(50)                  DEFAULT '' COMMENT 'as its name says',
    `update_time`     timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'as its name says',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `variable_id` (`variable_id`) USING BTREE,
    KEY `variable_id_element_id` (`variable_id`, `element_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  COMMENT ='process multiplayer variable table';

DROP TABLE IF EXISTS `t_bpm_variable_multiplayer_personnel`;
CREATE TABLE if not exists `t_bpm_variable_multiplayer_personnel`
(
    `id`                      bigint          NOT NULL AUTO_INCREMENT COMMENT 'id',
    `variable_multiplayer_id` bigint          NOT NULL COMMENT 'variable id',
    `assignee`                varchar(60)         NOT NULL DEFAULT '' COMMENT 'assignee,that is the approver',
     `assignee_name`                varchar(60)         NOT NULL DEFAULT '' COMMENT 'assignee name',
    `undertake_status`        int             NOT NULL COMMENT 'is undertaked(0:no,1:yes)',
    `remark`                  varchar(255)        NOT NULL DEFAULT '' COMMENT 'remark',
    `is_del`                  tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0:no,1:yes',
    `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    `create_user`             varchar(50)                  DEFAULT '' COMMENT 'as its name says',
    `create_time`             timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'as its name says',
    `update_user`             varchar(50)                  DEFAULT '' COMMENT 'as its name says',
    `update_time`             timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'as its name says',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `variable_multiplayer_id` (`variable_multiplayer_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
   COMMENT ='multiplayer assignees variable table';

DROP TABLE IF EXISTS `bpm_verify_info`;
CREATE TABLE if not exists `bpm_verify_info`
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `run_info_id`      varchar(64)          DEFAULT NULL COMMENT 'process instance id',
    `verify_user_id`   varchar(50)         DEFAULT NULL COMMENT 'approver',
    `verify_user_name` varchar(100)        DEFAULT NULL COMMENT 'approver name',
    `verify_status`    int              DEFAULT NULL COMMENT 'verify status',
    `verify_desc`      varchar(500)       DEFAULT NULL COMMENT 'verify desc',
    `verify_date`      timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `task_name`        varchar(64)         DEFAULT NULL COMMENT 'tsk name',
    `task_id`          varchar(64)         DEFAULT NULL COMMENT 'task id',
    `task_def_key`     varchar(255)                        null,
    `business_type`    int              DEFAULT NULL COMMENT 'business type',
    `business_id`      varchar(128)        DEFAULT NULL COMMENT 'business id',
    `original_id`      varchar(64)          DEFAULT NULL COMMENT 'orig approver name',
    `process_code`     varchar(64)         DEFAULT NULL COMMENT 'process number',
    `is_del`      tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0:no,1:yes',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    `attachments_json`      text                                 COMMENT 'attachments json',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `BPM_IDX__INFOR` (`business_type`, `business_id`) USING BTREE,
    KEY `process_code_index` (`process_code`) USING BTREE,
     KEY `bpm_verify_info_idx3` (`run_info_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
   COMMENT ='verify info';


-- 此表和租户无关
DROP TABLE IF EXISTS `t_method_replay`;
create table if not exists t_method_replay
(
    id                   int auto_increment
        primary key,
    PROJECT_NAME         varchar(100) null comment 'project name',
    CLASS_NAME           varchar(255) null,
    METHOD_NAME          varchar(255) null,
    PARAM_TYPE           varchar(255) null,
    ARGS                 text         null,
    NOW_TIME             timestamp    null,
    ERROR_MSG            text         null,
    ALREADY_REPLAY_TIMES int          null,
    MAX_REPLAY_TIMES     int          null
)ENGINE = InnoDB
   comment 'method replay records';

create index t_method_replay_NOW_TIME_index
    on t_method_replay (NOW_TIME);


DROP TABLE IF EXISTS `t_user_entrust`;
CREATE TABLE if not exists `t_user_entrust`
(
    `id`            int      NOT NULL AUTO_INCREMENT,
    `sender`        varchar(64)      NOT NULL COMMENT 'sender id',
    `receiver_id`   varchar(64)      NOT NULL,
    `receiver_name` varchar(255)          DEFAULT NULL,
    `power_id`      varchar(100) NOT NULL,
    `begin_time`    timestamp    NULL  DEFAULT NULL,
    `end_time`      timestamp    NULL  DEFAULT NULL,
    `create_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user`   varchar(50)  NOT NULL,
    `update_user`   varchar(50)  NOT NULL,
     `is_del`      tinyint NOT NULL DEFAULT '0' COMMENT '（0:no 1:yes',
      `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `s_r_id` (`sender`, `receiver_id`, `power_id`) USING BTREE,
    KEY `user_id` (`sender`, `power_id`) USING BTREE
) ENGINE = InnoDB
  COMMENT ='user entrust info';



DROP TABLE IF EXISTS `t_user_message_status`;
CREATE TABLE if not exists `t_user_message_status`
(
    `id`                     int     NOT NULL AUTO_INCREMENT,
    `user_id`                varchar(64)     NOT NULL,
    `message_status`         tinyint  NOT NULL DEFAULT '0' COMMENT 'sms status',
    `mail_status`            tinyint  NOT NULL DEFAULT '0' COMMENT 'email status',
    `not_trouble_time_end`   time             DEFAULT NULL COMMENT 'do not disturb end time',
    `not_trouble_time_begin` timestamp   NULL  DEFAULT NULL COMMENT 'do not disturb begin time',
    `not_trouble`            tinyint  NOT NULL DEFAULT '0' COMMENT 'is do not disturb enabled',
    `shock`                  tinyint  NOT NULL DEFAULT '0' COMMENT 'should shock',
    `sound`                  tinyint  NOT NULL DEFAULT '0' COMMENT 'is in silent mode',
    `open_phone`             tinyint  NOT NULL DEFAULT '0' COMMENT '',
    `create_time`            timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`            timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_user`            varchar(50) NOT NULL,
    `update_user`            varchar(50) NOT NULL,
     `is_del`      tinyint NOT NULL DEFAULT '0' COMMENT '（0:no 1:yes',
      `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `user_id` (`user_id`) USING BTREE
) ENGINE = InnoDB
   COMMENT ='user receive message table';


DROP TABLE IF EXISTS `bpm_business_process`;
CREATE TABLE if not exists `bpm_business_process`
(
    `id`               bigint  NOT NULL AUTO_INCREMENT COMMENT 'id',
    `PROCESSINESS_KEY` varchar(64)  DEFAULT NULL,
    `BUSINESS_ID`      varchar(64) NOT NULL COMMENT 'business id',
    `BUSINESS_NUMBER`  varchar(64)  DEFAULT NULL COMMENT 'process number',
    `ENTRY_ID`         varchar(64)  DEFAULT NULL,
    `VERSION`          varchar(30)   DEFAULT NULL COMMENT 'version',
    `CREATE_TIME`      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'as its name says',
    `UPDATE_TIME`      timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'as its name says',
    `description`      varchar(100) DEFAULT NULL COMMENT 'title',
    `process_state`    int       DEFAULT NULL COMMENT 'process state 1:approving 2:approved 3:invalid 6:rejected',
    `create_user`      varchar(64)  DEFAULT NULL,
    `process_digest`   text COMMENT 'process digest',
    `is_del`           tinyint   DEFAULT '0' COMMENT '0: no 1: yes）',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    `data_source_id`   bigint   DEFAULT NULL COMMENT 'data source id',
    `PROC_INST_ID_`    varchar(64)  DEFAULT '' COMMENT 'process instance id',
    `back_user_id`     varchar(64)      DEFAULT NULL COMMENT 'back to user id',
     `approval_users`     varchar(3000)      DEFAULT NULL COMMENT 'support multiple users,they are json array',
     user_name           varchar(255)           null,
     is_out_side_process tinyint     default 0  null comment 'is it an outside process,0 no,1 yes',
      is_lowcode_flow     tinyint     default 0  null comment '是否是低代码工作流0,否,1是',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `PROC_INST_ID_index` (`PROC_INST_ID_`) USING BTREE,
    KEY `process_entry_id` (`ENTRY_ID`) USING BTREE,
    KEY `process_key_index` (`PROCESSINESS_KEY`) USING BTREE,
    KEY `process_number_index` (`BUSINESS_NUMBER`) USING BTREE,
    KEY `process_state_index` (`process_state`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
   COMMENT ='process and business association table';






DROP TABLE IF EXISTS `t_user_message`;
CREATE TABLE if not exists `t_user_message`
(
      id          bigint auto_increment
          primary key,
      user_id     varchar(64)          null comment '用户id',
      title       varchar(50)  null comment '标题',
      content     varchar(255) null comment '消息内容',
      url         varchar(255) null comment '发送url',
      node        varchar(50)  null comment '发送节点id',
      params      varchar(255) null comment '发送类型',
      is_read     tinyint null comment '0为未读 1为已读',
      is_del         tinyint null comment '0为未删除 1为已删除',
      `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
      create_time datetime     null,
      update_time datetime     null,
      create_user varchar(50)  null,
      update_user varchar(50)  null,
      app_url     varchar(255) null comment 'appurl',
       source      int          null,
        KEY `t_user_message_idx1` (`user_id`)
) ENGINE = InnoDB
  ;

DROP TABLE IF EXISTS `t_op_log`;
CREATE TABLE IF NOT EXISTS `t_op_log`
(
    `id`             bigint NOT NULL AUTO_INCREMENT,
    `msg_id`         varchar(64)  DEFAULT NULL COMMENT ' msg id',
    `op_flag`        tinyint DEFAULT NULL COMMENT '0=success, 1=fail, 2=business exception',
    `op_user_no`     varchar(50)  DEFAULT NULL COMMENT 'user no',
    `op_user_name`   varchar(50)  DEFAULT NULL COMMENT 'user name',
    `op_method`      varchar(255) DEFAULT NULL COMMENT 'op method',
    `op_time`        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'op time',
    `op_use_time`    bigint   DEFAULT NULL COMMENT 'time cost',
    `op_param`       text COMMENT 'op params',
    `op_result`      text COMMENT 'op result',
    `system_type`    tinyint DEFAULT NULL COMMENT 'operation system type，iOS，Android，1=PC',
    `app_version`    varchar(50)  DEFAULT NULL COMMENT 'app version',
    `hardware`       varchar(50)  DEFAULT NULL COMMENT 'hardware info',
    `system_version` varchar(50)  DEFAULT NULL COMMENT 'app version',
    `remark`         varchar(255) DEFAULT NULL COMMENT 'remark',
    `log_type`       tinyint DEFAULT NULL COMMENT 'log type: null/0=operation, 1=email send',
    `receiver`       varchar(255) DEFAULT NULL COMMENT 'email receiver (for email send logs)',
     is_del         tinyint null comment '0为未删除 1为已删除',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
 ;





DROP TABLE IF EXISTS `bpm_process_app_application`;
CREATE TABLE IF NOT EXISTS  bpm_process_app_application
(
    id               int auto_increment comment 'Primary key'
        primary key,
    business_code    varchar(50)                       null comment 'Business code, typically empty for main applications',
    process_name     varchar(50)                       null comment 'Application name',
    apply_type       int                                null comment 'Application type (1: Process, 2: Application, 3: Parent Application)',
    permissions_code varchar(50)                       null,
    pc_icon          varchar(500)                       null comment 'PC icon URL or path',
    effective_source varchar(500)                       null comment 'Mobile platform icon URL or identifier (based on the field name)',
    is_son           int                                null comment 'Whether it is a child application (0: No, 1: Yes)',
    look_url         varchar(500)                       null comment 'URL for viewing the application',
    submit_url       varchar(500)                       null comment 'URL for submitting the application',
    user_request_uri varchar(500)                       null comment 'get  user info',
    role_request_uri varchar(500)                       null comment 'get Role info',
    condition_url    varchar(500)                       null comment 'URL for accessing conditions or rules related to the application',
    parent_id        int                                null comment 'Parent application ID (if this is a child application)',
    application_url  varchar(500)                       null comment 'Main URL of the application',
    route            varchar(500)                       null comment 'Application route or path',
    process_key      varchar(50)                       null comment 'Process key or identifier',
    create_time      timestamp  not null default CURRENT_TIMESTAMP  comment 'Creation timestamp',
    update_time      timestamp not null default CURRENT_TIMESTAMP  on update CURRENT_TIMESTAMP comment 'Last update timestamp',
    is_del           tinyint      default 0                not null,
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    create_user_id   varchar(64)                                null,
    update_user      varchar(255)                       null,
    is_all           tinyint  default 0                 null,
    state            tinyint  default 1                 null,
    sort             int                                null,
    source           varchar(255)                       null,
    KEY `bpm_process_app_application_idx1` (`business_code`)
)comment 'BPM Process Application Table';


DROP TABLE IF EXISTS `bpm_process_app_data`;
CREATE TABLE IF NOT EXISTS `bpm_process_app_data` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Primary key',
    `process_key` VARCHAR(50) COMMENT 'Process key',
    `process_name` VARCHAR(50) COMMENT 'Process name',
    `state` INT COMMENT 'Is online (0 for no, 1 for yes)',
    `route` VARCHAR(500) COMMENT 'APP route',
    `sort` INT COMMENT 'Sort order',
    `source` VARCHAR(500) COMMENT 'Pic source route',
    `is_all` tinyint COMMENT 'Is for all (0 or 1)',
    `version_id` BIGINT COMMENT 'Version ID',
    `application_id` VARCHAR(64) COMMENT 'Application ID',
    `type` INT COMMENT 'Type (1 for version app, 2 for app data)',
    `is_del` int NOT NULL DEFAULT '0' COMMENT '0 for normal 1 for delete',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    PRIMARY KEY (`id`),
    KEY `bpm_process_app_data_idx1` (`process_key`)
) ENGINE=InnoDB COMMENT='App Online Process Data Table';

DROP TABLE IF EXISTS `bpm_process_application_type`;
CREATE TABLE IF NOT EXISTS `bpm_process_application_type` (
      `id` BIGINT AUTO_INCREMENT COMMENT 'PRIMARY KEY',
      `application_id` BIGINT COMMENT 'Application ID',
      `category_id` BIGINT COMMENT 'Category ID',
      `is_del` INT COMMENT 'Deletion flag (0 for not deleted, 1 for deleted)',
       `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
      `sort` INT COMMENT 'Sort order',
      `state` INT COMMENT 'Is frequently used (0 for no, 1 for yes)',
      `history_id` BIGINT COMMENT 'History ID',
      `visble_state` INT COMMENT 'Visibility state (0 for hidden, 1 for visible)',
      `create_time` timestamp not null default current_timestamp COMMENT 'Creation time',
      `common_use_state` INT COMMENT 'Common use state',
      PRIMARY KEY (`id`),
       KEY `bpm_process_application_type_idx1` (`application_id`)
) ENGINE=InnoDB  COMMENT='BPM Process Application Type Table';

DROP TABLE IF EXISTS `bpm_process_category`;
CREATE TABLE IF NOT EXISTS `bpm_process_category` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Primary key',
    `process_type_name` VARCHAR(255) COMMENT 'Process type name',
    `is_del` TINYINT COMMENT 'Deletion flag (0 for not deleted, 1 for deleted)',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    `state` INT COMMENT 'State of the category (specific meaning depends on application logic)',
    `sort` INT COMMENT 'Sort order',
    `is_app` TINYINT COMMENT 'Is for app (0 for no, 1 for yes)',
    `entrance` VARCHAR(255) COMMENT 'Entrance (specific meaning depends on application logic)',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='BPM Process Category Table';

DROP TABLE IF EXISTS `bpm_process_permissions`;
CREATE TABLE IF NOT EXISTS `bpm_process_permissions` (
      `id` BIGINT AUTO_INCREMENT COMMENT 'Primary key',
      `user_id` varchar(64) COMMENT 'User ID',
      `dep_id` BIGINT COMMENT 'Department ID',
      `permissions_type` INT COMMENT 'Permission type (1 for view, 2 for create, 3 for monitor)',
      `create_user` varchar(64) COMMENT 'Create user ID',
      `create_time` timestamp not null default current_timestamp COMMENT 'Create time',
      `process_key` VARCHAR(50) COMMENT 'Process key',
      `office_id` BIGINT COMMENT 'Office ID',
      `is_del` TINYINT COMMENT 'Deletion flag (0 for not deleted, 1 for deleted)',
       `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
      PRIMARY KEY (`id`)
) ENGINE=InnoDB  COMMENT='process permission';


DROP TABLE IF EXISTS `t_out_side_bpm_access_business`;
CREATE TABLE IF NOT EXISTS  `t_out_side_bpm_access_business` (
     `id` BIGINT AUTO_INCREMENT,
     `business_party_id` BIGINT NOT NULL,
     `bpmn_conf_id` BIGINT NOT NULL,
     `form_code` VARCHAR(50) DEFAULT NULL,
     `process_number` VARCHAR(50) DEFAULT NULL,
     `form_data_pc` LONGTEXT,
     `form_data_app` LONGTEXT,
     `template_mark` VARCHAR(50) DEFAULT NULL,
     `start_username` VARCHAR(50) DEFAULT NULL,
     `remark` TEXT,
     `is_del` TINYINT DEFAULT 0,
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
     `create_user` VARCHAR(50) DEFAULT NULL,
     `create_time` timestamp DEFAULT CURRENT_TIMESTAMP,
     `update_user` VARCHAR(50) DEFAULT NULL,
     `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`),
     KEY `idx_bpm_conf_id` (`bpmn_conf_id`),
     KEY `idx_business_party_id` (`business_party_id`),
     KEY `t_out_side_bpm_access_business__idx3` (`process_number`),
     KEY `t_out_side_bpm_access_business_idx4` (`form_code`)
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `t_out_side_bpm_admin_personnel`;
CREATE TABLE  IF NOT EXISTS  `t_out_side_bpm_admin_personnel` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Auto increment ID',
    `business_party_id` bigint DEFAULT NULL COMMENT 'Business party main table ID',
    `type` int DEFAULT NULL COMMENT 'Administrator type: 1-Process administrator, 2-Application administrator, 3-Interface administrator',
    `employee_id` varchar(64) DEFAULT NULL COMMENT 'Administrator ID (Employee ID)',
    `employee_name` varchar(64) DEFAULT NULL COMMENT 'Administrator name (Employee name)',
    `remark` varchar(255) DEFAULT NULL COMMENT 'Remark',
    `is_del` int DEFAULT NULL COMMENT 'Deletion flag: 0 for normal, 1 for deleted',
    `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    `create_user` varchar(50) DEFAULT NULL COMMENT 'Creator user',
    `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    `update_user` varchar(50) DEFAULT NULL COMMENT 'Updater user',
    `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    PRIMARY KEY (`id`),
     KEY `t_out_side_bpm_admin_personnel_idx1` (`business_party_id`)
) ENGINE=InnoDB  COMMENT='Workflow External Service - Business Party Administrator Table';

DROP TABLE IF EXISTS `t_out_side_bpm_business_party`;
CREATE TABLE IF NOT EXISTS  `t_out_side_bpm_business_party` (
       `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Auto incr id',
       `business_party_mark` varchar(50) DEFAULT NULL COMMENT 'Business party mark',
       `name` varchar(255) DEFAULT NULL COMMENT 'Business party name',
       `type` tinyint DEFAULT NULL COMMENT 'Business type: 1 for embedded, 2 for API access',
       `remark` varchar(255) DEFAULT NULL COMMENT 'Remark',
       `is_del` tinyint DEFAULT 0 COMMENT 'Deletion flag: 0 for normal, 1 for deleted',
       `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
       `create_user` varchar(50) DEFAULT NULL COMMENT 'Creator user',
       `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
       `update_user` varchar(50) DEFAULT NULL COMMENT 'Updater user',
       `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT 'Update time',
       PRIMARY KEY (`id`),
       KEY `t_out_side_bpm_business_party_idx1` (`business_party_mark`)
) ENGINE=InnoDB  COMMENT='Table for storing business party information in the external BPM system';

DROP TABLE IF EXISTS `t_out_side_bpm_callback_url_conf`;
CREATE TABLE IF NOT EXISTS  t_out_side_bpm_callback_url_conf
(
    id                    bigint auto_increment comment 'auto increment id'
        primary key,
    business_party_id     bigint       null comment '项目ID(或业务方ID)',
    application_id        bigint       null comment '应用ID',
    form_code             varchar(64) null comment 'formcode',
    bpm_conf_callback_url varchar(500) null comment 'conf callback url',
    bpm_flow_callback_url varchar(500) null comment 'process flow call back url',
    api_client_id         varchar(100) null comment 'appId',
    api_client_secret    varchar(100) null comment 'appSecret',
    status                tinyint default 0 comment '0 for enable,1 for disable',
    create_user           varchar(50) null comment 'as its name says',
    update_user           varchar(50) null,
    remark                varchar(50)         null comment 'remark',
    is_del                tinyint     default 0 comment '0 for normal,1 for delete',
    `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    create_time           timestamp DEFAULT CURRENT_TIMESTAMP comment 'as its name says',
    update_time           timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment 'as its name says',
    KEY `t_out_side_bpm_callback_url_conf_idx1` (`business_party_id`),
    KEY `t_out_side_bpm_callback_url_conf_idx2` (`application_id`)
) comment 'business party callback url conf';

-- ----------------------------
-- Table structure for t_out_side_bpm_approve_template
-- ----------------------------
DROP TABLE IF EXISTS `t_out_side_bpm_approve_template`;
CREATE TABLE IF NOT EXISTS  `t_out_side_bpm_approve_template` (
     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',
     `business_party_id` BIGINT NULL COMMENT '业务方项目 Id',
	 `application_id` INT NULL COMMENT '项目下业务表单 id',
	 `approve_type_id` INT NULL COMMENT '审批人类型 id',
     `approve_type_name` VARCHAR(50) NULL COMMENT '审批人类型名称',
     `api_client_id` VARCHAR(50) NULL COMMENT 'api_client_id',
     `api_client_secret` VARCHAR(50) NULL COMMENT 'api_client_secret',
	 `api_token` VARCHAR(50) NULL COMMENT 'api_token',
	 `api_url` VARCHAR(50) NULL COMMENT 'api_url',
     `remark` varchar(255) NULL COMMENT 'remark',
     `is_del` TINYINT default  0 COMMENT '0 for normal, 1 for delete',
      `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
     `create_user` VARCHAR(50) NULL COMMENT 'as its name says',
     `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'as its name says',
     `update_user` VARCHAR(50) NULL COMMENT 'as its name says',
     `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'as its name says',
     `create_user_id` varchar(64) NULL COMMENT 'as its name says',
     PRIMARY KEY (`id`),
      KEY `t_out_side_bpm_approve_template_idx1` (`business_party_id`),
      KEY `t_out_side_bpm_approve_template_idx2` (`application_id`)
) ENGINE=InnoDB  COMMENT='outside access process,approve template config';

-- ----------------------------
-- Table structure for t_out_side_bpm_conditions_template
-- ----------------------------
DROP TABLE IF EXISTS `t_out_side_bpm_conditions_template`;
CREATE TABLE IF NOT EXISTS  `t_out_side_bpm_conditions_template` (
     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',
     `business_party_id` BIGINT NULL COMMENT 'business party Id',
     `template_mark` VARCHAR(50) NULL COMMENT 'template mark',
     `template_name` VARCHAR(50) NULL COMMENT 'template name',
     `application_id` INT NULL COMMENT 'application id',
     `remark` varchar(255) NULL COMMENT 'remark',
     `is_del` TINYINT default  0 COMMENT '0 for normal, 1 for delete',
      `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
     `create_user` VARCHAR(50) NULL COMMENT 'as its name says',
     `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'as its name says',
     `update_user` VARCHAR(50) NULL COMMENT 'as its name says',
     `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'as its name says',
     `create_user_id` varchar(64) NULL COMMENT 'as its name says',
     PRIMARY KEY (`id`),
     KEY `t_out_side_bpm_conditions_template_idx1` (`business_party_id`)
) ENGINE=InnoDB  COMMENT='outside access process,condition template config';


DROP TABLE IF EXISTS `t_out_side_bpm_call_back_record`;
CREATE TABLE IF NOT EXISTS  `t_out_side_bpm_call_back_record` (
     `id` INT NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',
     `process_number` VARCHAR(50) NULL COMMENT 'process number',
     `status` tinyint NULL COMMENT 'push status 0 for success,1 for fail',
     `retry_times` tinyint NULL COMMENT 'retry times',
     `button_operation_type` tinyint NULL COMMENT 'operation type (see MsgProcessEventEnum)',
     `call_back_type_name` VARCHAR(255) NULL COMMENT 'call back type name (see CallbackTypeEnum)',
     `business_id` BIGINT NULL COMMENT 'business id',
     `form_code` VARCHAR(50) NULL COMMENT 'form code',
     `is_del` tinyint default 0 COMMENT '0 for normal,1 for delete',
      `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
     `create_user` VARCHAR(50) NULL COMMENT 'create user',
     `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
     `update_user` VARCHAR(50) NULL COMMENT 'update user',
     `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
     PRIMARY KEY (`id`),
     KEY `t_out_side_bpm_call_back_record_idx1` (`process_number`),
     KEY `t_out_side_bpm_call_back_record_idx2` (`tenant_id`)
) ENGINE=InnoDB  COMMENT='Table for storing callback records';

DROP TABLE IF EXISTS `t_quick_entry`;
CREATE TABLE IF NOT EXISTS  `t_quick_entry` (
     `id` INT AUTO_INCREMENT PRIMARY KEY,
     `title` VARCHAR(100) NOT NULL,
     `effective_source` VARCHAR(255),
     `is_del` TINYINT DEFAULT 0,
      `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
     `route` VARCHAR(500) NOT NULL,
     `sort` tinyint DEFAULT 0,
     `create_time` timestamp DEFAULT CURRENT_TIMESTAMP,
     `status` TINYINT NOT NULL DEFAULT 0,
     `variable_url_flag` TINYINT NOT NULL DEFAULT 0,
     `type_config_json` VARCHAR(500) DEFAULT NULL COMMENT 'type config json',
     INDEX `idx_route` (`route`)
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `t_sys_version`;
CREATE TABLE IF NOT EXISTS  `t_sys_version` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `create_time` timestamp DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp not null default current_timestamp on update current_timestamp,
    `is_del` TINYINT DEFAULT 0 COMMENT '0 for normal, 1 for deleted',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    `version` VARCHAR(100) NOT NULL COMMENT 'Version',
    `description` varchar(255) COMMENT 'Version description',
    `index` INT COMMENT 'Index',
    `is_force` TINYINT COMMENT 'Force update 0 for no, 1 for yes',
    `android_url` VARCHAR(500) COMMENT 'Android download URL',
    `ios_url` VARCHAR(500) COMMENT 'iOS download URL',
    `create_user` VARCHAR(50) COMMENT 'Create user',
    `update_user` VARCHAR(50) COMMENT 'Update user',
    `is_hide` TINYINT COMMENT '0 for not hide and 1 for hide',
    `download_code` VARCHAR(255) COMMENT 'Download code',
    `effective_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'Effective time',
    INDEX `idx_version` (`version`) -- Optional index for improved performance on `version` queries
) ENGINE=InnoDB  COMMENT='sys version control';







DROP TABLE IF EXISTS `t_bpmn_conf_lf_formdata`;
create table t_bpmn_conf_lf_formdata
(
	id bigint auto_increment,
	bpmn_conf_id bigint not null,
	formdata longtext null,
	is_del tinyint default 0 not null,
	`tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
	create_user varchar(255) null,
	create_time timestamp default current_timestamp,
	update_user varchar(255) null,
	update_time timestamp default current_timestamp ON UPDATE CURRENT_TIMESTAMP,
	constraint t_bpmn_conf_lf_formdata_pk
		primary key (id),
	 KEY `t_bpmn_conf_lf_formdata__idx1` (`bpmn_conf_id`)
)ENGINE=InnoDB ;
DROP TABLE IF EXISTS t_bpmn_conf_lf_formdata_field;
create table t_bpmn_conf_lf_formdata_field
(
	id bigint auto_increment,
	bpmn_conf_id bigint null,
	formdata_id bigint null,
	field_id varchar(255) null,
	field_name varchar(255) null,
	field_type tinyint null,
	is_condition tinyint default 0 null comment '是否是流程条件,0否,1是',
	is_del tinyint default 0 not null,
	 `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
	create_user varchar(255) null,
	create_time timestamp default current_timestamp,
	update_user varchar(255) null,
	update_time timestamp default current_timestamp ON UPDATE CURRENT_TIMESTAMP,
	constraint t_bpmn_conf_lf_formdata_field_pk
		primary key (id),
	 KEY `t_bpmn_conf_lf_formdata_field_idx1` (`bpmn_conf_id`),
      KEY `t_bpmn_conf_lf_formdata_field_idx2` (`formdata_id`)
)ENGINE=InnoDB  comment '低代码配置字段明细表';



-- ----------------------------
-- 此表为路由表,通过lf.main.table.count控制,默认为2个,索引从0开始,需要自己手动创建
-- ----------------------------
DROP TABLE IF EXISTS t_lf_main;
create table t_lf_main
(
	id bigint auto_increment,
	conf_id bigint null,
	form_code varchar(255) null,
	is_del tinyint default 0 not null,
	`tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
	create_user varchar(255) null,
	create_time timestamp default current_timestamp,
	update_user varchar(255) null,
	update_time timestamp default current_timestamp ON UPDATE CURRENT_TIMESTAMP,
	constraint t_lf_main_pk
		primary key (id),
     KEY `t_lf_main_dx2` (`form_code`),
     KEY `t_lf_main_idx1` (`conf_id`)
)ENGINE=InnoDB  comment '低代码表单主表';

-- ----------------------------
-- 此表为路由表,通过lf.field.table.count控制,默认为10个,索引从0开始,需要自己手动创建
-- ----------------------------
DROP TABLE IF EXISTS t_lf_main_field;
create table t_lf_main_field
(
	id bigint auto_increment,
	main_id bigint not null,
	form_code varchar(255) null,
	field_id varchar(255) null,
	field_name varchar(255) null,
	parent_field_id varchar(255) null,
    parent_field_name varchar(255) null,
	field_value varchar(2000) null,
	field_value_number double(14,2) null,
	field_value_dt timestamp null,
	field_value_text longtext null,
	sort int default 0 not null,
	is_del tinyint default 0 not null,
	`tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    create_user varchar(255) null,
    create_time timestamp default current_timestamp,
    update_user varchar(255) null,
    update_time timestamp default current_timestamp ON UPDATE CURRENT_TIMESTAMP,
	constraint t_lf_main_field_pk
		primary key (id),
	KEY `t_lf_main_field_dx1` (`main_id`),
     KEY `t_lf_main_field_idx2` (`form_code`),
     KEY `t_lf_main_field_idx3` (`field_id`)
)ENGINE=InnoDB  comment '低代码表单字段值表';


DROP TABLE IF EXISTS t_dict_data;
create table t_dict_data
(
    id          bigint auto_increment comment '字典编码'
        primary key,
    dict_sort   int       default 0                 null comment '字典排序',
    dict_label  varchar(100) default ''                null comment '字典标签',
    dict_value  varchar(100) default ''                null comment '字典键值',
    dic_value_type         tinyint                                null comment '0:string,1.number,3.namevalue object,4 array of namevalue object,5.array of string,6.array of number',
    dict_type   varchar(100) default ''                null comment '字典类型',
    dict_second_level_type varchar(100)                           null,
    css_class   varchar(100)                           null comment '样式属性（其他样式扩展）',
    list_class  varchar(100)                           null comment '表格回显样式',
    is_default  char         default 'N'               null comment '是否默认（Y是 N否）',
    is_del      tinyint      default 0                 not null,
    `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    create_user varchar(255)                           null,
    create_time timestamp    default CURRENT_TIMESTAMP,
    update_user varchar(255)                           null,
    update_time timestamp    default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
    remark      varchar(500)                           null comment '备注',
    KEY `t_dict_data_idx1` (`dict_type`),
    KEY `t_dict_data_idx2` (`dict_second_level_type`)
) comment '字典表子表,用于存储字典值,一般现有系统都有自己的字典表,可以替换掉,给出sql能查出需要的数据就可以了';


SET FOREIGN_KEY_CHECKS = 1;


DROP TABLE IF EXISTS t_bpm_dynamic_condition_choosen;
create table t_bpm_dynamic_condition_choosen
(
	id bigint auto_increment,
	process_number varchar(255) null comment '流程编号',
	node_id varchar(100) null comment '被选中条件节点的id',
	node_from      varchar(100) null,
	constraint t_bpm_dynamic_condition_choosen_pk
		primary key (id),
    KEY `indx_process_number` (`process_number`),
     KEY `t_bpm_dynamic_condition_choosen_idx2` (`node_id`)
)
comment '流程动态条件选择条件记录表';








-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL COMMENT ' user id ',
  `role_id` int NULL DEFAULT NULL COMMENT 'role id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19  COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS t_department;
create table t_department
(
    id          int auto_increment comment 'Primary key'
        primary key,
    name        varchar(255) null comment 'Name',
    short_name  varchar(255) null comment 'Short name',
    parent_id   int          null comment 'Parent ID',
    path        varchar(255) null comment 'Path',
    level       int          null comment 'Department level',
    leader_id   bigint       null,
    sort        int          null comment 'Sort order',
    is_del      tinyint      null comment 'Is deleted (0 for no, 1 for yes)',
    `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    is_hide     tinyint      null comment 'Is hidden (0 for show, 1 for hide)',
    create_user varchar(255) null comment 'Create user',
    update_user varchar(255) null comment 'Update user',
    create_time datetime     null comment 'Creation time',
    update_time datetime     null comment 'Update time'
) comment 'department info';


DROP TABLE IF EXISTS `t_user`;
create table t_user
(
    id             int auto_increment
        primary key,
    user_name      varchar(255)      null,
    mobile         varchar(50)       null comment 'user''s mobile phone number',
    email          varchar(50)       null comment 'user''s email address',
    leader_id      bigint            null comment 'emp direct leader id',
    hrbp_id        bigint            null comment '用户的hrb的id,这里仅仅是用作选择hrbp审批时展示使用,实际上有的的公司组织架构里每个员工都有一个hrbp,有的则是hrbp挂在部门下面.具体根据公司业务而定',
    mobile_is_show tinyint default 0 null comment '是否展示用户手机号,如果用户手机号不展示时发送流程短信通知时也不应当通知给他.当然有的公司设置必须发送短信.这个根据公司业务而定,这里只是展示可以做很多人性化的定制设置',
    department_id  bigint            null comment '部门id',
    path           varchar(1000)     null comment '员工组织线path,用于层层审批流程展示,有些公司的表员工的组织链并非这样的,这里只是展示用,具体根据公司业务而定只要能根据员工的id找到他的上级,上级的上线,上级的上级的上线的上线即可.当然如果没有这样的组织架构关系,不用这种审批规则即可',
    is_del         tinyint default 0 null comment '0,正常1,删除',
    `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    head_img       varchar(3000)     null
);


DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`  (
     `id` int NOT NULL AUTO_INCREMENT,
     `role_name` varchar(255)  NULL DEFAULT NULL,
     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9  ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for t_biz_leavetime
-- ----------------------------
DROP TABLE IF EXISTS `t_biz_leavetime`;
CREATE TABLE `t_biz_leavetime`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `leave_user_id` int NOT NULL,
  `leave_user_name` varchar(255)  NOT NULL,
  `leave_type` int NOT NULL,
  `begin_time` datetime NULL,
  `end_time` datetime NULL,
  `leavehour` double NOT NULL,
  `remark` varchar(255)  NULL DEFAULT NULL,
  `create_user` varchar(255)  NOT NULL,
  `create_time` timestamp default current_timestamp,
  `update_user` varchar(255)  NULL DEFAULT NULL,
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_biz_purchase
-- ----------------------------
DROP TABLE IF EXISTS `t_biz_purchase`;
CREATE TABLE `t_biz_purchase`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `purchase_user_id` int NOT NULL,
  `purchase_user_name` varchar(255)  NOT NULL,
  `purchase_type` int NOT NULL default 1,
  `purchase_time` timestamp default current_timestamp,
  `plan_procurement_total_money` double NOT NULL default 0,
  `remark` varchar(255)  NULL DEFAULT NULL,
  `create_user` varchar(255)  NOT NULL,
  `create_time` timestamp default current_timestamp,
  `update_user` varchar(255)  NULL DEFAULT NULL,
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_biz_ucar_refuel
-- ----------------------------
DROP TABLE IF EXISTS `t_biz_ucar_refuel`;
CREATE TABLE `t_biz_ucar_refuel` (
  `id` int NOT NULL AUTO_INCREMENT,
`license_plate_number` varchar(32) DEFAULT NULL COMMENT '车牌号',
`refuel_time` datetime DEFAULT NULL COMMENT '加油日期',
`remark` varchar(255)  NULL DEFAULT NULL,
`create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
`create_time` datetime DEFAULT NULL COMMENT '创建日期',
`update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
`update_time` datetime DEFAULT NULL COMMENT '更新日期',
PRIMARY KEY (`id`)
) ENGINE=InnoDB  COMMENT='加油表';

-- ----------------------------
-- Table structure for t_biz_refund
-- ----------------------------
DROP TABLE IF EXISTS `t_biz_refund`;
CREATE TABLE `t_biz_refund`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `refund_user_id` int NOT NULL,
  `refund_user_name` varchar(255)  NOT NULL,
  `refund_type` int NOT NULL default 1,
  `refund_date` timestamp NOT NULL,
  `refund_money` double NOT NULL default 0,
  `remark` varchar(255)  NULL DEFAULT NULL,
  `create_user` varchar(255)  NOT NULL,
  `create_time` timestamp default current_timestamp,
  `update_user` varchar(255)  NULL DEFAULT NULL,
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1  ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS t_biz_account_apply;
CREATE TABLE `t_biz_account_apply`
(
    `id`                 int NOT NULL AUTO_INCREMENT,
    `account_type`       tinyint   DEFAULT NULL COMMENT 'account type',
    `account_owner_name` varchar(50)  DEFAULT NULL COMMENT 'account owner',
    `remark`             varchar(200) DEFAULT NULL COMMENT 'remark',
    `is_del`       int         DEFAULT '0',
     `tenant_id`              varchar(255)        NOT NULL DEFAULT '' COMMENT 'tenantId',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
   COMMENT ='a third party account apply demo';