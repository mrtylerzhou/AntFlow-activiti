CREATE TABLE `ACT_EVT_LOG`
(
    `LOG_NR_`       bigint        NOT NULL AUTO_INCREMENT,
    `TYPE_`         varchar(64)   DEFAULT NULL,
    `PROC_DEF_ID_`  varchar(64)   DEFAULT NULL,
    `PROC_INST_ID_` varchar(64)   DEFAULT NULL,
    `EXECUTION_ID_` varchar(64)   DEFAULT NULL,
    `TASK_ID_`      varchar(64)   DEFAULT NULL,
    `TIME_STAMP_`   timestamp(3)  NOT NULL,
    `USER_ID_`      varchar(255)  DEFAULT NULL,
    `DATA_`         longblob,
    `LOCK_OWNER_`   varchar(255)  DEFAULT NULL,
    `LOCK_TIME_`    timestamp(3)  NULL DEFAULT NULL,
    `IS_PROCESSED_` tinyint       DEFAULT '0',
    PRIMARY KEY (`LOG_NR_`)
) ENGINE = InnoDB
  ;

CREATE TABLE `ACT_RE_DEPLOYMENT`
(
    `ID_`          varchar(64)  NOT NULL,
    `NAME_`        varchar(255) DEFAULT NULL,
    `CATEGORY_`    varchar(255) DEFAULT NULL,
    `TENANT_ID_`   varchar(255) DEFAULT '',
    `DEPLOY_TIME_` timestamp(3) NULL DEFAULT NULL,
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB
  ;


CREATE TABLE `ACT_GE_BYTEARRAY`
(
    `ID_`            varchar(64)   NOT NULL,
    `REV_`           int           DEFAULT NULL,
    `NAME_`          varchar(255)  DEFAULT NULL,
    `DEPLOYMENT_ID_` varchar(64)   DEFAULT NULL,
    `BYTES_`         longblob,
    `GENERATED_`     tinyint       DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`),
    CONSTRAINT `ACT_FK_BYTEARR_DEPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`)
) ENGINE = InnoDB;

CREATE TABLE `ACT_GE_PROPERTY`
(
    `NAME_`  varchar(64)   NOT NULL,
    `VALUE_` varchar(300)  DEFAULT NULL,
    `REV_`   int           DEFAULT NULL,
    PRIMARY KEY (`NAME_`)
) ENGINE = InnoDB;



CREATE TABLE `ACT_HI_ACTINST`
(
    `ID_`                varchar(64)   NOT NULL,
    `PROC_DEF_ID_`       varchar(64)   NOT NULL,
    `PROC_INST_ID_`      varchar(64)   NOT NULL,
    `EXECUTION_ID_`      varchar(64)   NOT NULL,
    `ACT_ID_`            varchar(255)  NOT NULL,
    `TASK_ID_`           varchar(64)   DEFAULT NULL,
    `CALL_PROC_INST_ID_` varchar(64)   DEFAULT NULL,
    `ACT_NAME_`          varchar(255)  DEFAULT NULL,
    `ACT_TYPE_`          varchar(255)  NOT NULL,
    `ASSIGNEE_`          varchar(255)  DEFAULT NULL,
    `START_TIME_`        datetime(3)   NOT NULL,
    `END_TIME_`          datetime(3)   DEFAULT NULL,
    `DURATION_`          bigint        DEFAULT NULL,
    `TENANT_ID_`         varchar(255)  DEFAULT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_ACT_INST_START` (`START_TIME_`),
    KEY `ACT_IDX_HI_ACT_INST_END` (`END_TIME_`),
    KEY `ACT_IDX_HI_ACT_INST_PROCINST` (`PROC_INST_ID_`, `ACT_ID_`),
    KEY `ACT_IDX_HI_ACT_INST_EXEC` (`EXECUTION_ID_`, `ACT_ID_`)
) ENGINE = InnoDB;

CREATE TABLE `ACT_HI_ATTACHMENT`
(
    `ID_`           varchar(64)    NOT NULL,
    `REV_`          int            DEFAULT NULL,
    `USER_ID_`      varchar(255)   DEFAULT NULL,
    `NAME_`         varchar(255)   DEFAULT NULL,
    `DESCRIPTION_`  varchar(4000)  DEFAULT NULL,
    `TYPE_`         varchar(255)   DEFAULT NULL,
    `TASK_ID_`      varchar(64)    DEFAULT NULL,
    `PROC_INST_ID_` varchar(64)    DEFAULT NULL,
    `URL_`          varchar(4000)  DEFAULT NULL,
    `CONTENT_ID_`   varchar(64)    DEFAULT NULL,
    `TIME_`         datetime(3)    DEFAULT NULL,
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB;

CREATE TABLE `ACT_HI_COMMENT`
(
    `ID_`           varchar(64)    NOT NULL,
    `TYPE_`         varchar(255)   DEFAULT NULL,
    `TIME_`         datetime(3)    NOT NULL,
    `USER_ID_`      varchar(255)   DEFAULT NULL,
    `TASK_ID_`      varchar(64)    DEFAULT NULL,
    `PROC_INST_ID_` varchar(64)    DEFAULT NULL,
    `ACTION_`       varchar(255)   DEFAULT NULL,
    `MESSAGE_`      varchar(4000)  DEFAULT NULL,
    `FULL_MSG_`     longblob,
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB;

CREATE TABLE `ACT_HI_DETAIL`
(
    `ID_`           varchar(64)    NOT NULL,
    `TYPE_`         varchar(255)   NOT NULL,
    `PROC_INST_ID_` varchar(64)    DEFAULT NULL,
    `EXECUTION_ID_` varchar(64)    DEFAULT NULL,
    `TASK_ID_`      varchar(64)    DEFAULT NULL,
    `ACT_INST_ID_`  varchar(64)    DEFAULT NULL,
    `NAME_`         varchar(255)   NOT NULL,
    `VAR_TYPE_`     varchar(255)   DEFAULT NULL,
    `REV_`          int            DEFAULT NULL,
    `TIME_`         datetime(3)    NOT NULL,
    `BYTEARRAY_ID_` varchar(64)    DEFAULT NULL,
    `DOUBLE_`       double         DEFAULT NULL,
    `LONG_`         bigint         DEFAULT NULL,
    `TEXT_`         varchar(4000)  DEFAULT NULL,
    `TEXT2_`        varchar(4000)  DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_DETAIL_PROC_INST` (`PROC_INST_ID_`),
    KEY `ACT_IDX_HI_DETAIL_ACT_INST` (`ACT_INST_ID_`),
    KEY `ACT_IDX_HI_DETAIL_TIME` (`TIME_`),
    KEY `ACT_IDX_HI_DETAIL_NAME` (`NAME_`),
    KEY `ACT_IDX_HI_DETAIL_TASK_ID` (`TASK_ID_`)
) ENGINE = InnoDB;

CREATE TABLE `ACT_HI_IDENTITYLINK`
(
    `ID_`           varchar(64)   NOT NULL,
    `GROUP_ID_`     varchar(255)  DEFAULT NULL,
    `TYPE_`         varchar(255)  DEFAULT NULL,
    `USER_ID_`      varchar(255)  DEFAULT NULL,
    `TASK_ID_`      varchar(64)   DEFAULT NULL,
    `PROC_INST_ID_` varchar(64)   DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_IDENT_LNK_USER` (`USER_ID_`),
    KEY `ACT_IDX_HI_IDENT_LNK_TASK` (`TASK_ID_`),
    KEY `ACT_IDX_HI_IDENT_LNK_PROCINST` (`PROC_INST_ID_`)
) ENGINE = InnoDB;


CREATE TABLE `ACT_HI_PROCINST`
(
    `ID_`                        varchar(64)    NOT NULL,
    `PROC_INST_ID_`              varchar(64)    NOT NULL,
    `BUSINESS_KEY_`              varchar(255)   DEFAULT NULL,
    `PROC_DEF_ID_`               varchar(64)    NOT NULL,
    `START_TIME_`                datetime(3)    NOT NULL,
    `END_TIME_`                  datetime(3)    DEFAULT NULL,
    `DURATION_`                  bigint         DEFAULT NULL,
    `START_USER_ID_`             varchar(255)   DEFAULT NULL,
    `START_ACT_ID_`              varchar(255)   DEFAULT NULL,
    `END_ACT_ID_`                varchar(255)   DEFAULT NULL,
    `SUPER_PROCESS_INSTANCE_ID_` varchar(64)    DEFAULT NULL,
    `DELETE_REASON_`             varchar(4000)  DEFAULT NULL,
    `TENANT_ID_`                 varchar(255)   DEFAULT '',
    `NAME_`                      varchar(255)   DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    UNIQUE KEY `PROC_INST_ID_` (`PROC_INST_ID_`),
    KEY `ACT_IDX_HI_PRO_INST_END` (`END_TIME_`),
    KEY `ACT_IDX_HI_PRO_I_BUSKEY` (`BUSINESS_KEY_`)
) ENGINE = InnoDB;

CREATE TABLE `ACT_HI_TASKINST`
(
    `ID_`             varchar(64)    NOT NULL,
    `PROC_DEF_ID_`    varchar(64)    DEFAULT NULL,
    `TASK_DEF_KEY_`   varchar(255)   DEFAULT NULL,
    `PROC_INST_ID_`   varchar(64)    DEFAULT NULL,
    `EXECUTION_ID_`   varchar(64)    DEFAULT NULL,
    `NAME_`           varchar(255)   DEFAULT NULL,
    `PARENT_TASK_ID_` varchar(64)    DEFAULT NULL,
    `DESCRIPTION_`    varchar(4000)  DEFAULT NULL,
    `OWNER_`          varchar(255)   DEFAULT NULL,
    `ASSIGNEE_`       varchar(255)   DEFAULT NULL,
     `ASSIGNEE_NAME`  varchar(255)   DEFAULT NULL,
    `START_TIME_`     datetime(3)    NOT NULL,
    `CLAIM_TIME_`     datetime(3)    DEFAULT NULL,
    `END_TIME_`       datetime(3)    DEFAULT NULL,
    `DURATION_`       bigint         DEFAULT NULL,
    `DELETE_REASON_`  varchar(4000)  DEFAULT NULL,
    `PRIORITY_`       int            DEFAULT NULL,
    `DUE_DATE_`       datetime(3)    DEFAULT NULL,
    `FORM_KEY_`       varchar(255)   DEFAULT NULL,
    `CATEGORY_`       varchar(255)   DEFAULT NULL,
    `TENANT_ID_`      varchar(255)   DEFAULT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_TASK_INST_PROCINST` (`PROC_INST_ID_`)
) ENGINE = InnoDB;

CREATE TABLE `ACT_HI_VARINST`
(
    `ID_`                varchar(64)    NOT NULL,
    `PROC_INST_ID_`      varchar(64)    DEFAULT NULL,
    `EXECUTION_ID_`      varchar(64)    DEFAULT NULL,
    `TASK_ID_`           varchar(64)    DEFAULT NULL,
    `NAME_`              varchar(255)   NOT NULL,
    `VAR_TYPE_`          varchar(100)   DEFAULT NULL,
    `REV_`               int            DEFAULT NULL,
    `BYTEARRAY_ID_`      varchar(64)    DEFAULT NULL,
    `DOUBLE_`            double         DEFAULT NULL,
    `LONG_`              bigint         DEFAULT NULL,
    `TEXT_`              varchar(4000)  DEFAULT NULL,
    `TEXT2_`             varchar(4000)  DEFAULT NULL,
    `CREATE_TIME_`       datetime(3)    DEFAULT NULL,
    `LAST_UPDATED_TIME_` datetime(3)    DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_PROCVAR_PROC_INST` (`PROC_INST_ID_`),
    KEY `ACT_IDX_HI_PROCVAR_NAME_TYPE` (`NAME_`, `VAR_TYPE_`),
    KEY `ACT_IDX_HI_PROCVAR_TASK_ID` (`TASK_ID_`)
) ENGINE = InnoDB;

CREATE TABLE `ACT_ID_GROUP`
(
    `ID_`   varchar(64)   NOT NULL,
    `REV_`  int           DEFAULT NULL,
    `NAME_` varchar(255)  DEFAULT NULL,
    `TYPE_` varchar(255)  DEFAULT NULL,
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB;

CREATE TABLE `ACT_ID_INFO`
(
    `ID_`        varchar(64)   NOT NULL,
    `REV_`       int           DEFAULT NULL,
    `USER_ID_`   varchar(64)   DEFAULT NULL,
    `TYPE_`      varchar(64)   DEFAULT NULL,
    `KEY_`       varchar(255)  DEFAULT NULL,
    `VALUE_`     varchar(255)  DEFAULT NULL,
    `PASSWORD_`  longblob,
    `PARENT_ID_` varchar(255)  DEFAULT NULL,
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB;

CREATE TABLE `ACT_ID_USER`
(
    `ID_`         varchar(64)  NOT NULL,
    `REV_`        int           DEFAULT NULL,
    `FIRST_`      varchar(255)  DEFAULT NULL,
    `LAST_`       varchar(255)  DEFAULT NULL,
    `EMAIL_`      varchar(255)  DEFAULT NULL,
    `PWD_`        varchar(255)  DEFAULT NULL,
    `PICTURE_ID_` varchar(64)   DEFAULT NULL,
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB;


CREATE TABLE `ACT_ID_MEMBERSHIP`
(
    `USER_ID_`  varchar(64)  NOT NULL,
    `GROUP_ID_` varchar(64)  NOT NULL,
    PRIMARY KEY (`USER_ID_`, `GROUP_ID_`),
    KEY `ACT_FK_MEMB_GROUP` (`GROUP_ID_`),
    CONSTRAINT `ACT_FK_MEMB_GROUP` FOREIGN KEY (`GROUP_ID_`) REFERENCES `act_id_group` (`ID_`),
    CONSTRAINT `ACT_FK_MEMB_USER` FOREIGN KEY (`USER_ID_`) REFERENCES `act_id_user` (`ID_`)
) ENGINE = InnoDB;

CREATE TABLE `ACT_RE_PROCDEF`
(
    `ID_`                     varchar(64)    NOT NULL,
    `REV_`                    int            DEFAULT NULL,
    `CATEGORY_`               varchar(255)   DEFAULT NULL,
    `NAME_`                   varchar(255)   DEFAULT NULL,
    `KEY_`                    varchar(255)   NOT NULL,
    `VERSION_`                int            NOT NULL,
    `DEPLOYMENT_ID_`          varchar(64)    DEFAULT NULL,
    `RESOURCE_NAME_`          varchar(4000)  DEFAULT NULL,
    `DGRM_RESOURCE_NAME_`     varchar(4000)  DEFAULT NULL,
    `DESCRIPTION_`            varchar(4000)  DEFAULT NULL,
    `HAS_START_FORM_KEY_`     tinyint        DEFAULT NULL,
    `HAS_GRAPHICAL_NOTATION_` tinyint        DEFAULT NULL,
    `SUSPENSION_STATE_`       int            DEFAULT NULL,
    `TENANT_ID_`              varchar(255)   DEFAULT '',
    PRIMARY KEY (`ID_`),
    UNIQUE KEY `ACT_UNIQ_PROCDEF` (`KEY_`, `VERSION_`, `TENANT_ID_`)
) ENGINE = InnoDB;

CREATE TABLE `ACT_PROCDEF_INFO`
(
    `ID_`           varchar(64)  NOT NULL,
    `PROC_DEF_ID_`  varchar(64)  NOT NULL,
    `REV_`          int          DEFAULT NULL,
    `INFO_JSON_ID_` varchar(64)  DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    UNIQUE KEY `ACT_UNIQ_INFO_PROCDEF` (`PROC_DEF_ID_`),
    KEY `ACT_IDX_INFO_PROCDEF` (`PROC_DEF_ID_`),
    KEY `ACT_FK_INFO_JSON_BA` (`INFO_JSON_ID_`),
    CONSTRAINT `ACT_FK_INFO_JSON_BA` FOREIGN KEY (`INFO_JSON_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
    CONSTRAINT `ACT_FK_INFO_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`)
) ENGINE = InnoDB;



CREATE TABLE `ACT_RE_MODEL`
(
    `ID_`                           varchar(64)       NOT NULL,
    `REV_`                          int               DEFAULT NULL,
    `NAME_`                         varchar(255)      DEFAULT NULL,
    `KEY_`                          varchar(255)      DEFAULT NULL,
    `CATEGORY_`                     varchar(255)      DEFAULT NULL,
    `CREATE_TIME_`                  timestamp(3)      NULL DEFAULT NULL,
    `LAST_UPDATE_TIME_`             timestamp(3)      NULL DEFAULT NULL,
    `VERSION_`                      int               DEFAULT NULL,
    `META_INFO_`                    varchar(4000)     DEFAULT NULL,
    `DEPLOYMENT_ID_`                varchar(64)       DEFAULT NULL,
    `EDITOR_SOURCE_VALUE_ID_`       varchar(64)       DEFAULT NULL,
    `EDITOR_SOURCE_EXTRA_VALUE_ID_` varchar(64)       DEFAULT NULL,
    `TENANT_ID_`                    varchar(255)      DEFAULT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_FK_MODEL_SOURCE` (`EDITOR_SOURCE_VALUE_ID_`),
    KEY `ACT_FK_MODEL_SOURCE_EXTRA` (`EDITOR_SOURCE_EXTRA_VALUE_ID_`),
    KEY `ACT_FK_MODEL_DEPLOYMENT` (`DEPLOYMENT_ID_`),
    CONSTRAINT `ACT_FK_MODEL_DEPLOYMENT` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`),
    CONSTRAINT `ACT_FK_MODEL_SOURCE` FOREIGN KEY (`EDITOR_SOURCE_VALUE_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
    CONSTRAINT `ACT_FK_MODEL_SOURCE_EXTRA` FOREIGN KEY (`EDITOR_SOURCE_EXTRA_VALUE_ID_`) REFERENCES `act_ge_bytearray` (`ID_`)
) ENGINE = InnoDB;


CREATE TABLE `ACT_RU_EXECUTION`
(
    `ID_`               varchar(64)       NOT NULL,
    `REV_`              int               DEFAULT NULL,
    `PROC_INST_ID_`     varchar(64)       DEFAULT NULL,
    `BUSINESS_KEY_`     varchar(255)      DEFAULT NULL,
    `PARENT_ID_`        varchar(64)       DEFAULT NULL,
    `PROC_DEF_ID_`      varchar(64)       DEFAULT NULL,
    `SUPER_EXEC_`       varchar(64)       DEFAULT NULL,
    `ACT_ID_`           varchar(255)      DEFAULT NULL,
    `IS_ACTIVE_`        tinyint           DEFAULT NULL,
    `IS_CONCURRENT_`    tinyint           DEFAULT NULL,
    `IS_SCOPE_`         tinyint           DEFAULT NULL,
    `IS_EVENT_SCOPE_`   tinyint           DEFAULT NULL,
    `SUSPENSION_STATE_` int               DEFAULT NULL,
    `CACHED_ENT_STATE_` int               DEFAULT NULL,
    `TENANT_ID_`        varchar(255)      DEFAULT '',
    `NAME_`             varchar(255)      DEFAULT NULL,
    `LOCK_TIME_`        timestamp(3)      NULL DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_EXEC_BUSKEY` (`BUSINESS_KEY_`),
    KEY `ACT_FK_EXE_PROCINST` (`PROC_INST_ID_`),
    KEY `ACT_FK_EXE_PARENT` (`PARENT_ID_`),
    KEY `ACT_FK_EXE_SUPER` (`SUPER_EXEC_`),
    KEY `ACT_FK_EXE_PROCDEF` (`PROC_DEF_ID_`),
    CONSTRAINT `ACT_FK_EXE_PARENT` FOREIGN KEY (`PARENT_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_EXE_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`),
    CONSTRAINT `ACT_FK_EXE_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `ACT_FK_EXE_SUPER` FOREIGN KEY (`SUPER_EXEC_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE = InnoDB;


CREATE TABLE `ACT_RU_EVENT_SUBSCR`
(
    `ID_`            varchar(64)   NOT NULL,
    `REV_`           int           DEFAULT NULL,
    `EVENT_TYPE_`    varchar(255)  NOT NULL,
    `EVENT_NAME_`    varchar(255)  DEFAULT NULL,
    `EXECUTION_ID_`  varchar(64)   DEFAULT NULL,
    `PROC_INST_ID_`  varchar(64)   DEFAULT NULL,
    `ACTIVITY_ID_`   varchar(64)   DEFAULT NULL,
    `CONFIGURATION_` varchar(255)  DEFAULT NULL,
    `CREATED_`       timestamp(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `PROC_DEF_ID_`   varchar(64)   DEFAULT NULL,
    `TENANT_ID_`     varchar(255)  DEFAULT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_EVENT_SUBSCR_CONFIG_` (`CONFIGURATION_`),
    KEY `ACT_FK_EVENT_EXEC` (`EXECUTION_ID_`),
    CONSTRAINT `ACT_FK_EVENT_EXEC` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE = InnoDB;


CREATE TABLE `ACT_RU_TASK`
(
    `ID_`               varchar(64)       NOT NULL,
    `REV_`              int               DEFAULT NULL,
    `EXECUTION_ID_`     varchar(64)       DEFAULT NULL,
    `PROC_INST_ID_`     varchar(64)       DEFAULT NULL,
    `PROC_DEF_ID_`      varchar(64)       DEFAULT NULL,
    `NAME_`             varchar(255)      DEFAULT NULL,
    `PARENT_TASK_ID_`   varchar(64)       DEFAULT NULL,
    `DESCRIPTION_`      varchar(4000)     DEFAULT NULL,
    `TASK_DEF_KEY_`     varchar(255)      DEFAULT NULL,
    `OWNER_`            varchar(255)      DEFAULT NULL,
    `ASSIGNEE_`         varchar(255)      DEFAULT NULL,
     `ASSIGNEE_NAME`  varchar(255)   DEFAULT NULL,
    `DELEGATION_`       varchar(64)       DEFAULT NULL,
    `PRIORITY_`         int               DEFAULT NULL,
    `CREATE_TIME_`      timestamp(3)      NULL DEFAULT NULL,
    `DUE_DATE_`         datetime(3)       DEFAULT NULL,
    `CATEGORY_`         varchar(255)      DEFAULT NULL,
    `SUSPENSION_STATE_` int               DEFAULT NULL,
    `TENANT_ID_`        varchar(255)      DEFAULT '',
    `FORM_KEY_`         varchar(255)      DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_TASK_CREATE` (`CREATE_TIME_`),
    KEY `ACT_FK_TASK_EXE` (`EXECUTION_ID_`),
    KEY `ACT_FK_TASK_PROCINST` (`PROC_INST_ID_`),
    KEY `ACT_FK_TASK_PROCDEF` (`PROC_DEF_ID_`),
    CONSTRAINT `ACT_FK_TASK_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_TASK_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`),
    CONSTRAINT `ACT_FK_TASK_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE = InnoDB;



CREATE TABLE `ACT_RU_IDENTITYLINK`
(
    `ID_`           varchar(64)   NOT NULL,
    `REV_`          int           DEFAULT NULL,
    `GROUP_ID_`     varchar(255)  DEFAULT NULL,
    `TYPE_`         varchar(255)  DEFAULT NULL,
    `USER_ID_`      varchar(255)  DEFAULT NULL,
    `TASK_ID_`      varchar(64)   DEFAULT NULL,
    `PROC_INST_ID_` varchar(64)   DEFAULT NULL,
    `PROC_DEF_ID_`  varchar(64)   DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_IDENT_LNK_USER` (`USER_ID_`),
    KEY `ACT_IDX_IDENT_LNK_GROUP` (`GROUP_ID_`),
    KEY `ACT_IDX_ATHRZ_PROCEDEF` (`PROC_DEF_ID_`),
    KEY `ACT_FK_TSKASS_TASK` (`TASK_ID_`),
    KEY `ACT_FK_IDL_PROCINST` (`PROC_INST_ID_`),
    CONSTRAINT `ACT_FK_ATHRZ_PROCEDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`),
    CONSTRAINT `ACT_FK_IDL_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_TSKASS_TASK` FOREIGN KEY (`TASK_ID_`) REFERENCES `act_ru_task` (`ID_`)
) ENGINE = InnoDB;



CREATE TABLE `ACT_RU_JOB`
(
    `ID_`                  varchar(64)        NOT NULL,
    `REV_`                 int                DEFAULT NULL,
    `TYPE_`                varchar(255)       NOT NULL,
    `LOCK_EXP_TIME_`       timestamp(3)       NULL DEFAULT NULL,
    `LOCK_OWNER_`          varchar(255)       DEFAULT NULL,
    `EXCLUSIVE_`           tinyint(1)         DEFAULT NULL,
    `EXECUTION_ID_`        varchar(64)        DEFAULT NULL,
    `PROCESS_INSTANCE_ID_` varchar(64)        DEFAULT NULL,
    `PROC_DEF_ID_`         varchar(64)        DEFAULT NULL,
    `RETRIES_`             int                DEFAULT NULL,
    `EXCEPTION_STACK_ID_`  varchar(64)        DEFAULT NULL,
    `EXCEPTION_MSG_`       varchar(4000)      DEFAULT NULL,
    `DUEDATE_`             timestamp(3)       NULL DEFAULT NULL,
    `REPEAT_`              varchar(255)       DEFAULT NULL,
    `HANDLER_TYPE_`        varchar(255)       DEFAULT NULL,
    `HANDLER_CFG_`         varchar(4000)      DEFAULT NULL,
    `TENANT_ID_`           varchar(255)       DEFAULT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_FK_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
    CONSTRAINT `ACT_FK_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`)
) ENGINE = InnoDB;



CREATE TABLE `ACT_RU_VARIABLE`
(
    `ID_`           varchar(64)    NOT NULL,
    `REV_`          int            DEFAULT NULL,
    `TYPE_`         varchar(255)   NOT NULL,
    `NAME_`         varchar(255)   NOT NULL,
    `EXECUTION_ID_` varchar(64)    DEFAULT NULL,
    `PROC_INST_ID_` varchar(64)    DEFAULT NULL,
    `TASK_ID_`      varchar(64)    DEFAULT NULL,
    `BYTEARRAY_ID_` varchar(64)    DEFAULT NULL,
    `DOUBLE_`       double         DEFAULT NULL,
    `LONG_`         bigint         DEFAULT NULL,
    `TEXT_`         varchar(4000)  DEFAULT NULL,
    `TEXT2_`        varchar(4000)  DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_VARIABLE_TASK_ID` (`TASK_ID_`),
    KEY `ACT_FK_VAR_EXE` (`EXECUTION_ID_`),
    KEY `ACT_FK_VAR_PROCINST` (`PROC_INST_ID_`),
    KEY `ACT_FK_VAR_BYTEARRAY` (`BYTEARRAY_ID_`),
    CONSTRAINT `ACT_FK_VAR_BYTEARRAY` FOREIGN KEY (`BYTEARRAY_ID_`) REFERENCES `act_ge_bytearray` (`ID_`),
    CONSTRAINT `ACT_FK_VAR_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`),
    CONSTRAINT `ACT_FK_VAR_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`)
) ENGINE = InnoDB;



INSERT INTO `act_ge_property` (`NAME_`, `VALUE_`, `REV_`)
VALUES ('next.dbid', '1', 1);
INSERT INTO `act_ge_property` (`NAME_`, `VALUE_`, `REV_`)
VALUES ('schema.history', 'create(5.23.0.0)', 1);
INSERT INTO `act_ge_property` (`NAME_`, `VALUE_`, `REV_`)
VALUES ('schema.version', '5.23.0.0', 1);


CREATE INDEX idx_assignee_name ON ACT_HI_TASKINST(`ASSIGNEE_NAME`);