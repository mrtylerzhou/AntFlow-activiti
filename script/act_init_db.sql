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
    KEY `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`)
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
    KEY `ACT_FK_INFO_JSON_BA` (`INFO_JSON_ID_`)
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
    KEY `ACT_FK_EXE_PROCDEF` (`PROC_DEF_ID_`)
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
    KEY `ACT_FK_TASK_PROCDEF` (`PROC_DEF_ID_`)
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
    KEY `ACT_FK_VAR_BYTEARRAY` (`BYTEARRAY_ID_`)
) ENGINE = InnoDB;



INSERT INTO `ACT_GE_PROPERTY` (`NAME_`, `VALUE_`, `REV_`)
VALUES ('next.dbid', '1', 1);
INSERT INTO `ACT_GE_PROPERTY` (`NAME_`, `VALUE_`, `REV_`)
VALUES ('schema.history', 'create(5.23.0.0)', 1);
INSERT INTO `ACT_GE_PROPERTY` (`NAME_`, `VALUE_`, `REV_`)
VALUES ('schema.version', '5.23.0.0', 1);


CREATE INDEX idx_assignee_name ON ACT_HI_TASKINST(`ASSIGNEE_NAME`);