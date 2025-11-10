create  sequence   SEQ_BPM_BUSINESS;/
create  sequence   SEQ_BPM_BUSINESS_PROCESS;/
create  sequence   SEQ_BPM_FLOWRUNINFO;/
create  sequence   SEQ_BPM_FLOWRUN_ENTRUST;/
create  sequence   SEQ_BPM_MANUAL_NOTIFY;/
create  sequence   SEQ_BPM_PROCESS_APP_APPLICATION;/
create  sequence   SEQ_BPM_PROCESS_DEPT;/
create  sequence   SEQ_BPM_PROCESS_FORWARD;/
create  sequence   SEQ_BPM_PROCESS_NAME;/
create  sequence   SEQ_BPM_PROCESS_NAME_RELEVANCY;/
create  sequence   SEQ_BPM_PROCESS_NODE_BACK;/
create  sequence   SEQ_BPM_PROCESS_NODE_OVERTIME;/
create  sequence   SEQ_BPM_PROCESS_NODE_RECORD;/
create  sequence   SEQ_BPM_PROCESS_NODE_SUBMIT;/
create  sequence   SEQ_BPM_PROCESS_NOTICE;/
create  sequence   SEQ_BPM_PROCESS_OPERATION;/
create  sequence   SEQ_BPM_PROCESS_PERMISSIONS;/
create  sequence   SEQ_BPM_TASKCONFIG;/
create  sequence   SEQ_BPM_VERIFY_INFO;/
create  sequence   SEQ_T_BIZ_ACCOUNT_APPLY;/
create  sequence   SEQ_T_BIZ_PURCHASE;/
create  sequence   SEQ_T_BIZ_REFUND;/
create  sequence   SEQ_T_BIZ_UCAR_REFUEL;/
create  sequence   SEQ_T_BPMN_APPROVE_REMIND;/
create  sequence   SEQ_T_BPMN_CONF;/
create  sequence   SEQ_T_BPMN_CONF_LF_FORMDATA;/
create  sequence   SEQ_T_BPMN_CONF_LF_FORMDATA_FIELD;/
create  sequence   SEQ_T_BPMN_CONF_NOTICE_TEMPLATE;/
create  sequence   SEQ_T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL;/
create  sequence   SEQ_T_BPMN_NODE;/
create  sequence   SEQ_T_BPMN_NODE_ASSIGN_LEVEL_CONF;/
create  sequence   SEQ_T_BPMN_NODE_BUSINESS_TABLE_CONF;/
create  sequence   SEQ_T_BPMN_NODE_BUTTON_CONF;/
create  sequence   SEQ_T_BPMN_NODE_CONDITIONS_CONF;/
create  sequence   SEQ_T_BPMN_NODE_CONDITIONS_PARAM_CONF;/
create  sequence   SEQ_T_BPMN_NODE_CUSTOMIZE_CONF;/
create  sequence   SEQ_T_BPMN_NODE_HRBP_CONF;/
create  sequence   SEQ_T_BPMN_NODE_LABELS;/
create  sequence   SEQ_T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL;/
create  sequence   SEQ_T_BPMN_NODE_LOOP_CONF;/
create  sequence   SEQ_T_BPMN_NODE_OUT_SIDE_ACCESS_CONF;/
create  sequence   SEQ_T_BPMN_NODE_PERSONNEL_CONF;/
create  sequence   SEQ_T_BPMN_NODE_PERSONNEL_EMPL_CONF;/
create  sequence   SEQ_T_BPMN_NODE_ROLE_CONF;/
create  sequence   SEQ_T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF;/
create  sequence   SEQ_T_BPMN_NODE_SIGN_UP_CONF;/
create  sequence   SEQ_T_BPMN_NODE_TO;/
create  sequence   SEQ_T_BPMN_TEMPLATE;/
create  sequence   SEQ_T_BPMN_VIEW_PAGE_BUTTON;/
create  sequence   SEQ_T_BPM_DYNAMIC_CONDITION_CHOSEN;/
create  sequence   SEQ_T_BPM_VARIABLE;/
create  sequence   SEQ_T_BPM_VARIABLE_APPROVE_REMIND;/
create  sequence   SEQ_T_BPM_VARIABLE_BUTTON;/
create  sequence   SEQ_T_BPM_VARIABLE_MESSAGE;/
create  sequence   SEQ_T_BPM_VARIABLE_MULTIPLAYER;/
create  sequence   SEQ_T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL;/
create  sequence   SEQ_T_BPM_VARIABLE_SEQUENCE_FLOW;/
create  sequence   SEQ_T_BPM_VARIABLE_SIGN_UP;/
create  sequence   SEQ_T_BPM_VARIABLE_SIGN_UP_PERSONNEL;/
create  sequence   SEQ_T_BPM_VARIABLE_SINGLE;/
create  sequence   SEQ_T_BPM_VARIABLE_VIEW_PAGE_BUTTON;/
create  sequence   SEQ_T_DEFAULT_TEMPLATE;/
create  sequence   SEQ_T_DEPARTMENT;/
create  sequence   SEQ_T_DICT_DATA;/
create  sequence   SEQ_T_DICT_MAIN;/
create  sequence   SEQ_T_INFORMATION_TEMPLATE;/
create  sequence   SEQ_T_LF_MAIN;/
create  sequence   SEQ_T_LF_MAIN_FIELD;/
create  sequence   SEQ_T_METHOD_REPLAY;/
create  sequence   SEQ_T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF;/
create  sequence   SEQ_T_OUT_SIDE_BPM_ACCESS_BUSINESS;/
create  sequence   SEQ_T_OUT_SIDE_BPM_ADMIN_PERSONNEL;/
create  sequence   SEQ_T_OUT_SIDE_BPM_BUSINESS_PARTY;/
create  sequence   SEQ_T_OUT_SIDE_BPM_CALLBACK_URL_CONF;/
create  sequence   SEQ_T_OUT_SIDE_BPM_CALL_BACK_RECORD;/
create  sequence   SEQ_T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE;/
create  sequence   SEQ_T_QUICK_ENTRY;/
create  sequence   SEQ_T_QUICK_ENTRY_TYPE;/
create  sequence   SEQ_T_ROLE;/
create  sequence   SEQ_T_SYS_VERSION;/
create  sequence   SEQ_T_USER;/
create  sequence   SEQ_T_USER_EMAIL_SEND;/
create  sequence   SEQ_T_USER_ENTRUST;/
create  sequence   SEQ_T_USER_MESSAGE_STATUS;/
create  sequence   SEQ_T_USER_ROLE;/

create table  T_USER
(
    ID NUMBER(11) not null
		constraint PK_T_USER
			primary key,
    USER_NAME NVARCHAR2(255),
    MOBILE NVARCHAR2(50),
    EMAIL NVARCHAR2(50),
    LEADER_ID NUMBER(20),
    HRBP_ID NUMBER(20),
    MOBILE_IS_SHOW NUMBER(3) default 0,
    DEPARTMENT_ID NUMBER(20),
    PATH NVARCHAR2(1000),
    IS_DEL NUMBER(3) default 0,
    HEAD_IMG NVARCHAR2(2000)
)
    /

comment on table  T_USER is '用户表'
/

comment on column  T_USER.ID is '用户ID'
/

comment on column  T_USER.USER_NAME is '用户姓名'
/

comment on column  T_USER.MOBILE is '用户手机号'
/

comment on column  T_USER.EMAIL is '用户邮箱'
/

comment on column  T_USER.LEADER_ID is '直属领导ID'
/

comment on column  T_USER.HRBP_ID is 'HRBP ID'
/

comment on column  T_USER.MOBILE_IS_SHOW is '是否展示手机号'
/

comment on column  T_USER.DEPARTMENT_ID is '部门ID'
/

comment on column  T_USER.PATH is '组织线路径'
/

comment on column  T_USER.IS_DEL is '是否删除 0:正常 1:删除'
/

comment on column  T_USER.HEAD_IMG is '头像'
/

create trigger  TRG_T_USER
    before insert
    on  T_USER
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_USER.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_ROLE
(
    ID NUMBER(11) not null
		constraint PK_T_ROLE
			primary key,
    ROLE_NAME NVARCHAR2(255)
)
    /

comment on table  T_ROLE is '角色表'
/

comment on column  T_ROLE.ID is '角色ID'
/

comment on column  T_ROLE.ROLE_NAME is '角色名称'
/

create trigger  TRG_T_ROLE
    before insert
    on  T_ROLE
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_ROLE.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_DEPARTMENT
(
    ID NUMBER(11) not null
		constraint PK_T_DEPARTMENT
			primary key,
    NAME NVARCHAR2(255),
    SHORT_NAME NVARCHAR2(255),
    PARENT_ID NUMBER(11),
    PATH NVARCHAR2(255),
    "LEVEL" NUMBER(11),
    LEADER_ID NUMBER(20),
    SORT NUMBER(11),
    IS_DEL NUMBER(3),
    IS_HIDE NUMBER(3),
    CREATE_USER NVARCHAR2(255),
    UPDATE_USER NVARCHAR2(255),
    CREATE_TIME DATE,
    UPDATE_TIME DATE
)
    /

comment on table  T_DEPARTMENT is '部门表'
/

comment on column  T_DEPARTMENT.ID is '部门ID'
/

comment on column  T_DEPARTMENT.NAME is '部门名称'
/

comment on column  T_DEPARTMENT.SHORT_NAME is '部门简称'
/

comment on column  T_DEPARTMENT.PARENT_ID is '上级部门ID'
/

comment on column  T_DEPARTMENT.PATH is '部门路径'
/

comment on column  T_DEPARTMENT."LEVEL" is '部门层级'
/

comment on column  T_DEPARTMENT.LEADER_ID is '部门负责人ID'
/

comment on column  T_DEPARTMENT.SORT is '排序'
/

comment on column  T_DEPARTMENT.IS_DEL is '是否删除'
/

comment on column  T_DEPARTMENT.IS_HIDE is '是否隐藏'
/

comment on column  T_DEPARTMENT.CREATE_USER is '创建人'
/

comment on column  T_DEPARTMENT.UPDATE_USER is '更新人'
/

comment on column  T_DEPARTMENT.CREATE_TIME is '创建时间'
/

comment on column  T_DEPARTMENT.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_DEPARTMENT
    before insert
    on  T_DEPARTMENT
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_DEPARTMENT.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_USER_ROLE
(
    ID NUMBER(11) not null
		constraint PK_T_USER_ROLE
			primary key,
    USER_ID NUMBER(11),
    ROLE_ID NUMBER(11)
)
    /

comment on table  T_USER_ROLE is '用户角色关联表'
/

comment on column  T_USER_ROLE.ID is '主键ID'
/

comment on column  T_USER_ROLE.USER_ID is '用户ID'
/

comment on column  T_USER_ROLE.ROLE_ID is '角色ID'
/

create trigger  TRG_T_USER_ROLE
    before insert
    on  T_USER_ROLE
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_USER_ROLE.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_CONF
(
    ID NUMBER(20) not null
		primary key,
    BPMN_CODE NVARCHAR2(60) default ''
		constraint UK_BPMN_CODE
			unique,
    BPMN_NAME NVARCHAR2(60) default '',
    BPMN_TYPE NUMBER(20),
    FORM_CODE NVARCHAR2(100) default '',
    APP_ID NUMBER(20),
    DEDUPLICATION_TYPE NUMBER(20) default 1 not null,
    EFFECTIVE_STATUS NUMBER(20) default 0 not null,
    IS_ALL NUMBER(20) default 0 not null,
    IS_OUT_SIDE_PROCESS NUMBER(20) default 0,
    IS_LOWCODE_FLOW NUMBER(1) default 0,
    BUSINESS_PARTY_ID NUMBER(20),
    EXTRA_FLAGS NUMBER(20),
    REMARK NVARCHAR2(255) default '',
    TENANT_ID NVARCHAR2(64) default '',
    IS_DEL NUMBER(1) default 0 not null,
    CREATE_USER NVARCHAR2(32) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(32) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_CONF is '流程主配置表'
/

comment on column  T_BPMN_CONF.ID is '自增ID'
/

comment on column  T_BPMN_CONF.BPMN_CODE is '流程编码'
/

comment on column  T_BPMN_CONF.BPMN_NAME is '流程名称'
/

comment on column  T_BPMN_CONF.BPMN_TYPE is '流程类型'
/

comment on column  T_BPMN_CONF.FORM_CODE is '流程业务编码'
/

comment on column  T_BPMN_CONF.APP_ID is '关联应用ID'
/

comment on column  T_BPMN_CONF.DEDUPLICATION_TYPE is '去重方式 1:不去重,2:正向去重,3:反向去重'
/

comment on column  T_BPMN_CONF.EFFECTIVE_STATUS is '是否生效 0:否,1:是'
/

comment on column  T_BPMN_CONF.IS_ALL is '是否对所有部门生效 0:否,1:是'
/

comment on column  T_BPMN_CONF.IS_OUT_SIDE_PROCESS is '是否为第三方流程 0:否,1:是'
/

comment on column  T_BPMN_CONF.IS_LOWCODE_FLOW is '是否是低代码审批流 0:否,1:是'
/

comment on column  T_BPMN_CONF.BUSINESS_PARTY_ID is '所属业务方ID'
/

comment on column  T_BPMN_CONF.EXTRA_FLAGS is '额外标志'
/

comment on column  T_BPMN_CONF.REMARK is '备注'
/

comment on column  T_BPMN_CONF.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_CONF.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPMN_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_CONF.UPDATE_TIME is '更新时间'
/

create index  IDX_BPMN_CONF_BUSINESS_PARTY_ID
    on  T_BPMN_CONF (BUSINESS_PARTY_ID)
    /

create index  IDX_BPMN_CONF_FORM_CODE
    on  T_BPMN_CONF (FORM_CODE)
    /

create trigger  TRG_T_BPMN_CONF
    before insert
    on  T_BPMN_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_CONF.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_NODE
(
    ID NUMBER(20) not null
		primary key,
    CONF_ID NUMBER(20) not null,
    NODE_ID NVARCHAR2(60) default '',
    NODE_TYPE NUMBER(10) not null,
    NODE_PROPERTY NUMBER(10) not null,
    NODE_FROM NVARCHAR2(60) default '',
    NODE_FROMS NVARCHAR2(255),
    BATCH_STATUS NUMBER(10) default 0 not null,
    APPROVAL_STANDARD NUMBER(10) default 2 not null,
    NODE_NAME NVARCHAR2(255),
    NODE_DISPLAY_NAME NVARCHAR2(255) default '',
    ANNOTATION NVARCHAR2(255),
    IS_DEDUPLICATION NUMBER(10) default 0,
    DEDUPLICATIONEXCLUDE NUMBER(1) default 0 not null,
    IS_DYNAMICCONDITION NUMBER(1) default 0,
    IS_PARALLEL NUMBER(1) default 0,
    IS_SIGN_UP NUMBER(10) default 0 not null,
    REMARK NVARCHAR2(255) default '',
    TENANT_ID NVARCHAR2(64) default '',
    IS_DEL NUMBER(1) default 0 not null,
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_NODE is '流程节点表'
/

comment on column  T_BPMN_NODE.ID is 'ID'
/

comment on column  T_BPMN_NODE.CONF_ID is '主配置ID'
/

comment on column  T_BPMN_NODE.NODE_ID is '节点ID'
/

comment on column  T_BPMN_NODE.NODE_TYPE is '节点类型'
/

comment on column  T_BPMN_NODE.NODE_PROPERTY is '节点属性'
/

comment on column  T_BPMN_NODE.NODE_FROM is '前驱节点'
/

comment on column  T_BPMN_NODE.NODE_FROMS is '所有前驱节点'
/

comment on column  T_BPMN_NODE.BATCH_STATUS is '是否可批量审批 0:否,1:是'
/

comment on column  T_BPMN_NODE.APPROVAL_STANDARD is '审批标准'
/

comment on column  T_BPMN_NODE.NODE_NAME is '节点名称'
/

comment on column  T_BPMN_NODE.NODE_DISPLAY_NAME is '节点显示名称'
/

comment on column  T_BPMN_NODE.ANNOTATION is '节点注释'
/

comment on column  T_BPMN_NODE.IS_DEDUPLICATION is '是否去重 0:否,1:是'
/

comment on column  T_BPMN_NODE.DEDUPLICATIONEXCLUDE is '是否排除去重 0:否,1:是'
/

comment on column  T_BPMN_NODE.IS_DYNAMICCONDITION is '是否是动态条件节点 0:否,1:是'
/

comment on column  T_BPMN_NODE.IS_PARALLEL is '是否并行节点'
/

comment on column  T_BPMN_NODE.IS_SIGN_UP is '是否可签收 0:否,1:是'
/

comment on column  T_BPMN_NODE.REMARK is '备注'
/

comment on column  T_BPMN_NODE.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPMN_NODE.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE.UPDATE_TIME is '更新时间'
/

create index  IDX_BPMN_NODE_CONF_ID
    on  T_BPMN_NODE (CONF_ID)
    /

create trigger  TRG_T_BPMN_NODE
    before insert
    on  T_BPMN_NODE
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_NODE.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_NODE_BUSINESS_TABLE_CONF
(
    ID NUMBER(20) not null
		primary key,
    BPMN_NODE_ID NUMBER(20) not null,
    CONFIGURATION_TABLE_TYPE NUMBER(11),
    TABLE_FIELD_TYPE NUMBER(11),
    SIGN_TYPE NUMBER(11) not null,
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_NODE_BUSINESS_TABLE_CONF is '节点业务表配置表'
/

comment on column  T_BPMN_NODE_BUSINESS_TABLE_CONF.ID is 'ID'
/

comment on column  T_BPMN_NODE_BUSINESS_TABLE_CONF.BPMN_NODE_ID is '节点ID'
/

comment on column  T_BPMN_NODE_BUSINESS_TABLE_CONF.CONFIGURATION_TABLE_TYPE is '配置表类型'
/

comment on column  T_BPMN_NODE_BUSINESS_TABLE_CONF.TABLE_FIELD_TYPE is '表字段类型'
/

comment on column  T_BPMN_NODE_BUSINESS_TABLE_CONF.SIGN_TYPE is '签名类型 1:全部签名,2:或签名'
/

comment on column  T_BPMN_NODE_BUSINESS_TABLE_CONF.REMARK is '备注'
/

comment on column  T_BPMN_NODE_BUSINESS_TABLE_CONF.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPMN_NODE_BUSINESS_TABLE_CONF.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_BUSINESS_TABLE_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_BUSINESS_TABLE_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_BUSINESS_TABLE_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_BUSINESS_TABLE_CONF.UPDATE_TIME is '更新时间'
/

create index  IDX_BPMN_NODE_BUSINESS_TABLE_CONF_NODE_ID
    on  T_BPMN_NODE_BUSINESS_TABLE_CONF (BPMN_NODE_ID)
    /

create trigger  TRG_T_BPMN_NODE_BUSINESS_TABLE_CONF
    before insert
    on  T_BPMN_NODE_BUSINESS_TABLE_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_NODE_BUSINESS_TABLE_CONF.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_CONF_NOTICE_TEMPLATE
(
    ID NUMBER(20) not null
		primary key,
    BPMN_CODE NVARCHAR2(60) default '',
    TENANT_ID NVARCHAR2(64) default '',
    IS_DEL NUMBER(1) default 0 not null,
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_CONF_NOTICE_TEMPLATE is '流程通知模板表'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE.ID is 'ID'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE.BPMN_CODE is '流程编码'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE.CREATE_USER is '创建人'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_CONF_NOTICE_TEMPLATE
    before insert
    on  T_BPMN_CONF_NOTICE_TEMPLATE
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_CONF_NOTICE_TEMPLATE.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_VIEW_PAGE_BUTTON
(
    ID NUMBER(20) not null
		primary key,
    CONF_ID NUMBER(20) not null,
    VIEW_TYPE NUMBER(10) not null,
    BUTTON_TYPE NUMBER(10) not null,
    BUTTON_NAME NVARCHAR2(60) default '',
    REMARK NVARCHAR2(255) default '',
    TENANT_ID NVARCHAR2(64) default '',
    IS_DEL NUMBER(1) default 0 not null,
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_VIEW_PAGE_BUTTON is '审批流查看页按钮配置表'
/

comment on column  T_BPMN_VIEW_PAGE_BUTTON.ID is 'ID'
/

comment on column  T_BPMN_VIEW_PAGE_BUTTON.CONF_ID is '配置ID'
/

comment on column  T_BPMN_VIEW_PAGE_BUTTON.VIEW_TYPE is '视图类型 1:启动视图,2:审批视图'
/

comment on column  T_BPMN_VIEW_PAGE_BUTTON.BUTTON_TYPE is '按钮类型'
/

comment on column  T_BPMN_VIEW_PAGE_BUTTON.BUTTON_NAME is '按钮名称'
/

comment on column  T_BPMN_VIEW_PAGE_BUTTON.REMARK is '备注'
/

comment on column  T_BPMN_VIEW_PAGE_BUTTON.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_VIEW_PAGE_BUTTON.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPMN_VIEW_PAGE_BUTTON.CREATE_USER is '创建人'
/

comment on column  T_BPMN_VIEW_PAGE_BUTTON.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_VIEW_PAGE_BUTTON.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_VIEW_PAGE_BUTTON.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_VIEW_PAGE_BUTTON
    before insert
    on  T_BPMN_VIEW_PAGE_BUTTON
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_VIEW_PAGE_BUTTON.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_TEMPLATE
(
    ID NUMBER(20) not null
		primary key,
    CONF_ID NUMBER(20),
    NODE_ID NUMBER(20),
    EVENT NUMBER(10),
    INFORMS NVARCHAR2(255),
    EMPS NVARCHAR2(255),
    ROLES NVARCHAR2(255),
    FUNCS NVARCHAR2(255),
    TEMPLATE_ID NUMBER(20),
    FORM_CODE NVARCHAR2(50),
    MESSAGE_SEND_TYPE NVARCHAR2(50),
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_TIME DATE default SYSDATE,
    CREATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default ''
)
    /

comment on table  T_BPMN_TEMPLATE is '通知模板表'
/

comment on column  T_BPMN_TEMPLATE.ID is 'ID'
/

comment on column  T_BPMN_TEMPLATE.CONF_ID is '配置ID'
/

comment on column  T_BPMN_TEMPLATE.NODE_ID is '节点ID'
/

comment on column  T_BPMN_TEMPLATE.EVENT is '事件类型'
/

comment on column  T_BPMN_TEMPLATE.INFORMS is '通知对象'
/

comment on column  T_BPMN_TEMPLATE.EMPS is '指定人员'
/

comment on column  T_BPMN_TEMPLATE.ROLES is '指定角色'
/

comment on column  T_BPMN_TEMPLATE.FUNCS is '指定功能'
/

comment on column  T_BPMN_TEMPLATE.TEMPLATE_ID is '模板ID'
/

comment on column  T_BPMN_TEMPLATE.FORM_CODE is '表单编码'
/

comment on column  T_BPMN_TEMPLATE.MESSAGE_SEND_TYPE is '消息发送类型'
/

comment on column  T_BPMN_TEMPLATE.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPMN_TEMPLATE.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_TEMPLATE.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_TEMPLATE.CREATE_USER is '创建人'
/

comment on column  T_BPMN_TEMPLATE.UPDATE_TIME is '更新时间'
/

comment on column  T_BPMN_TEMPLATE.UPDATE_USER is '更新人'
/

create trigger  TRG_T_BPMN_TEMPLATE
    before insert
    on  T_BPMN_TEMPLATE
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_TEMPLATE.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_INFORMATION_TEMPLATE
(
    ID NUMBER(20) not null
		primary key,
    NAME NVARCHAR2(30) default '',
    NUM NVARCHAR2(10) default '' not null,
    SYSTEM_TITLE NVARCHAR2(100) default '',
    SYSTEM_CONTENT NVARCHAR2(500) default '' not null,
    MAIL_TITLE NVARCHAR2(100) default '' not null,
    MAIL_CONTENT NVARCHAR2(500) default '',
    NOTE_CONTENT NVARCHAR2(200) default '',
    JUMP_URL NUMBER(10),
    REMARK NVARCHAR2(200) default '',
    STATUS NUMBER(1) default 0 not null,
    EVENT NUMBER(10),
    EVENT_NAME NVARCHAR2(50),
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_TIME DATE default SYSDATE,
    CREATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default ''
)
    /

comment on table  T_INFORMATION_TEMPLATE is '消息模板'
/

comment on column  T_INFORMATION_TEMPLATE.ID is 'ID'
/

comment on column  T_INFORMATION_TEMPLATE.NAME is '名称'
/

comment on column  T_INFORMATION_TEMPLATE.NUM is '编号'
/

comment on column  T_INFORMATION_TEMPLATE.SYSTEM_TITLE is '系统标题'
/

comment on column  T_INFORMATION_TEMPLATE.SYSTEM_CONTENT is '系统内容'
/

comment on column  T_INFORMATION_TEMPLATE.MAIL_TITLE is '邮件标题'
/

comment on column  T_INFORMATION_TEMPLATE.MAIL_CONTENT is '邮件内容'
/

comment on column  T_INFORMATION_TEMPLATE.NOTE_CONTENT is '短信内容'
/

comment on column  T_INFORMATION_TEMPLATE.JUMP_URL is '跳转URL'
/

comment on column  T_INFORMATION_TEMPLATE.REMARK is '备注'
/

comment on column  T_INFORMATION_TEMPLATE.STATUS is '状态 0:启用,1:禁用'
/

comment on column  T_INFORMATION_TEMPLATE.EVENT is '事件类型'
/

comment on column  T_INFORMATION_TEMPLATE.EVENT_NAME is '事件名称'
/

comment on column  T_INFORMATION_TEMPLATE.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_INFORMATION_TEMPLATE.TENANT_ID is '租户ID'
/

comment on column  T_INFORMATION_TEMPLATE.CREATE_TIME is '创建时间'
/

comment on column  T_INFORMATION_TEMPLATE.CREATE_USER is '创建人'
/

comment on column  T_INFORMATION_TEMPLATE.UPDATE_TIME is '更新时间'
/

comment on column  T_INFORMATION_TEMPLATE.UPDATE_USER is '更新人'
/

create trigger  TRG_T_INFORMATION_TEMPLATE
    before insert
    on  T_INFORMATION_TEMPLATE
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_INFORMATION_TEMPLATE.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_BUSINESS
(
    ID NUMBER(20) not null
		primary key,
    BUSINESS_ID NVARCHAR2(64),
    CREATE_TIME DATE default SYSDATE,
    PROCESS_CODE NVARCHAR2(50),
    CREATE_USER_NAME NVARCHAR2(50),
    CREATE_USER NVARCHAR2(50),
    PROCESS_KEY NVARCHAR2(50),
    TENANT_ID NVARCHAR2(64) default '',
    IS_DEL NUMBER(11) default 0
)
    /

comment on table  BPM_BUSINESS is '流程草稿表'
/

comment on column  BPM_BUSINESS.ID is 'ID'
/

comment on column  BPM_BUSINESS.BUSINESS_ID is '业务ID'
/

comment on column  BPM_BUSINESS.CREATE_TIME is '创建时间'
/

comment on column  BPM_BUSINESS.PROCESS_CODE is '流程编号'
/

comment on column  BPM_BUSINESS.CREATE_USER_NAME is '创建人名称'
/

comment on column  BPM_BUSINESS.CREATE_USER is '创建人'
/

comment on column  BPM_BUSINESS.PROCESS_KEY is '流程KEY'
/

comment on column  BPM_BUSINESS.TENANT_ID is '租户ID'
/

comment on column  BPM_BUSINESS.IS_DEL is '是否删除 0:否,1:是'
/

create trigger  TRG_BPM_BUSINESS
    before insert
    on  BPM_BUSINESS
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_BUSINESS.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_FLOWRUN_ENTRUST
(
    ID NUMBER(20) not null
		primary key,
    RUNINFOID NVARCHAR2(64),
    RUNTASKID NVARCHAR2(64),
    ORIGINAL NVARCHAR2(64),
    ORIGINAL_NAME NVARCHAR2(255),
    ACTUAL NVARCHAR2(64),
    ACTUAL_NAME NVARCHAR2(100),
    TYPE NUMBER(20),
    IS_READ NUMBER(11) default 2,
    PROC_DEF_ID NVARCHAR2(100),
    IS_VIEW NUMBER(11) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    IS_DEL NUMBER(11) default 0
)
    /

comment on table  BPM_FLOWRUN_ENTRUST is '委托和转发表'
/

comment on column  BPM_FLOWRUN_ENTRUST.ID is 'ID'
/

comment on column  BPM_FLOWRUN_ENTRUST.RUNINFOID is '流程实例ID'
/

comment on column  BPM_FLOWRUN_ENTRUST.RUNTASKID is '任务ID'
/

comment on column  BPM_FLOWRUN_ENTRUST.ORIGINAL is '原处理人'
/

comment on column  BPM_FLOWRUN_ENTRUST.ORIGINAL_NAME is '原处理人名称'
/

comment on column  BPM_FLOWRUN_ENTRUST.ACTUAL is '实际处理人'
/

comment on column  BPM_FLOWRUN_ENTRUST.ACTUAL_NAME is '实际处理人名称'
/

comment on column  BPM_FLOWRUN_ENTRUST.TYPE is '类型 1:委托 2:查看'
/

comment on column  BPM_FLOWRUN_ENTRUST.IS_READ is '是否已读 1:是,2:否'
/

comment on column  BPM_FLOWRUN_ENTRUST.PROC_DEF_ID is '流程定义ID'
/

comment on column  BPM_FLOWRUN_ENTRUST.IS_VIEW is '是否查看'
/

comment on column  BPM_FLOWRUN_ENTRUST.TENANT_ID is '租户ID'
/

comment on column  BPM_FLOWRUN_ENTRUST.IS_DEL is '是否删除 0:否,1:是'
/

create index  IDX_BPM_FLOWRUN_ENTRUST_RUNINFOID_ORIGINAL_ACTUAL
    on  BPM_FLOWRUN_ENTRUST (RUNINFOID, ORIGINAL, ACTUAL)
    /

create trigger  TRG_BPM_FLOWRUN_ENTRUST
    before insert
    on  BPM_FLOWRUN_ENTRUST
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_FLOWRUN_ENTRUST.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_FLOWRUNINFO
(
    ID NUMBER(20) not null
		primary key,
    RUNINFOID NVARCHAR2(64) not null,
    CREATE_USERID NVARCHAR2(64),
    ENTITYKEY NVARCHAR2(100),
    ENTITYCLASS NVARCHAR2(100),
    ENTITYKEYTYPE NVARCHAR2(10),
    CREATEACTOR NVARCHAR2(50),
    CREATEDEPART NVARCHAR2(100),
    CREATEDATE DATE default SYSDATE,
    TENANT_ID NVARCHAR2(64) default '',
    IS_DEL NUMBER(11) default 0
)
    /

comment on table  BPM_FLOWRUNINFO is '流程运行时信息表'
/

comment on column  BPM_FLOWRUNINFO.ID is 'ID'
/

comment on column  BPM_FLOWRUNINFO.RUNINFOID is '流程实例ID'
/

comment on column  BPM_FLOWRUNINFO.CREATE_USERID is '用户ID'
/

comment on column  BPM_FLOWRUNINFO.ENTITYKEY is '业务键'
/

comment on column  BPM_FLOWRUNINFO.ENTITYCLASS is '实体类'
/

comment on column  BPM_FLOWRUNINFO.ENTITYKEYTYPE is '业务类型'
/

comment on column  BPM_FLOWRUNINFO.CREATEACTOR is '创建人'
/

comment on column  BPM_FLOWRUNINFO.CREATEDEPART is '创建部门'
/

comment on column  BPM_FLOWRUNINFO.CREATEDATE is '创建时间'
/

comment on column  BPM_FLOWRUNINFO.TENANT_ID is '租户ID'
/

comment on column  BPM_FLOWRUNINFO.IS_DEL is '是否删除 0:否,1:是'
/

create trigger  TRG_BPM_FLOWRUNINFO
    before insert
    on  BPM_FLOWRUNINFO
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_FLOWRUNINFO.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_MANUAL_NOTIFY
(
    ID NUMBER(20) not null
		primary key,
    BUSINESS_ID NUMBER(20) not null,
    CODE NVARCHAR2(10) not null,
    LAST_TIME DATE default SYSDATE,
    CREATE_TIME DATE default SYSDATE,
    UPDATE_TIME DATE default SYSDATE,
    TENANT_ID NVARCHAR2(64) default '',
    IS_DEL NUMBER(11) default 0
)
    /

comment on table  BPM_MANUAL_NOTIFY is '手动通知表'
/

comment on column  BPM_MANUAL_NOTIFY.ID is 'ID'
/

comment on column  BPM_MANUAL_NOTIFY.BUSINESS_ID is '业务ID'
/

comment on column  BPM_MANUAL_NOTIFY.CODE is '流程类型'
/

comment on column  BPM_MANUAL_NOTIFY.LAST_TIME is '最新提醒时间'
/

comment on column  BPM_MANUAL_NOTIFY.CREATE_TIME is '创建时间'
/

comment on column  BPM_MANUAL_NOTIFY.UPDATE_TIME is '更新时间'
/

comment on column  BPM_MANUAL_NOTIFY.TENANT_ID is '租户ID'
/

comment on column  BPM_MANUAL_NOTIFY.IS_DEL is '是否删除 0:否,1:是'
/

create trigger  TRG_BPM_MANUAL_NOTIFY
    before insert
    on  BPM_MANUAL_NOTIFY
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_MANUAL_NOTIFY.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_APPROVE_REMIND
(
    ID NUMBER(20) not null
		primary key,
    CONF_ID NUMBER(20),
    NODE_ID NUMBER(20),
    TEMPLATE_ID NUMBER(20),
    DAYS NVARCHAR2(255),
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_TIME DATE default SYSDATE,
    CREATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default ''
)
    /

comment on table  T_BPMN_APPROVE_REMIND is '审批提醒表'
/

comment on column  T_BPMN_APPROVE_REMIND.ID is 'ID'
/

comment on column  T_BPMN_APPROVE_REMIND.CONF_ID is '配置ID'
/

comment on column  T_BPMN_APPROVE_REMIND.NODE_ID is '节点ID'
/

comment on column  T_BPMN_APPROVE_REMIND.TEMPLATE_ID is '模板ID'
/

comment on column  T_BPMN_APPROVE_REMIND.DAYS is '提醒天数'
/

comment on column  T_BPMN_APPROVE_REMIND.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPMN_APPROVE_REMIND.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_APPROVE_REMIND.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_APPROVE_REMIND.CREATE_USER is '创建人'
/

comment on column  T_BPMN_APPROVE_REMIND.UPDATE_TIME is '更新时间'
/

comment on column  T_BPMN_APPROVE_REMIND.UPDATE_USER is '更新人'
/

create trigger  TRG_T_BPMN_APPROVE_REMIND
    before insert
    on  T_BPMN_APPROVE_REMIND
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_APPROVE_REMIND.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL
(
    ID NUMBER(20) not null
		primary key,
    BPMN_CODE NVARCHAR2(60) default '' not null,
    NOTICE_TEMPLATE_TYPE NUMBER(4) default 1 not null,
    NOTICE_TEMPLATE_DETAIL NVARCHAR2(512),
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL is '通知模板详情表'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL.ID is 'ID'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL.BPMN_CODE is '流程编码'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL.NOTICE_TEMPLATE_TYPE is '通知类型'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL.NOTICE_TEMPLATE_DETAIL is '内容'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL.CREATE_USER is '创建人'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL.UPDATE_TIME is '更新时间'
/

create index  IDX_BPMN_CONF_NOTICE_TEMPLATE_DETAIL_BPMN_CODE
    on  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL (BPMN_CODE)
    /

create index  IDX_BPMN_CONF_NOTICE_TEMPLATE_DETAIL_NOTICE_TEMPLATE_TYPE
    on  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL (NOTICE_TEMPLATE_TYPE)
    /

create trigger  TRG_T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL
    before insert
    on  T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_CONF_NOTICE_TEMPLATE_DETAIL.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_NODE_CONDITIONS_CONF
(
    ID NUMBER(20) not null
		primary key,
    BPMN_NODE_ID NUMBER(20) not null,
    IS_DEFAULT NUMBER(11) default 0 not null,
    SORT NUMBER(11) not null,
    GROUP_RELATION NUMBER(1),
    EXT_JSON NVARCHAR2(2000),
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_NODE_CONDITIONS_CONF is '节点条件配置表'
/

comment on column  T_BPMN_NODE_CONDITIONS_CONF.ID is 'ID'
/

comment on column  T_BPMN_NODE_CONDITIONS_CONF.BPMN_NODE_ID is '节点ID'
/

comment on column  T_BPMN_NODE_CONDITIONS_CONF.IS_DEFAULT is '是否默认 0:否,1:是'
/

comment on column  T_BPMN_NODE_CONDITIONS_CONF.SORT is '排序'
/

comment on column  T_BPMN_NODE_CONDITIONS_CONF.GROUP_RELATION is '组关系 0:AND,1:OR'
/

comment on column  T_BPMN_NODE_CONDITIONS_CONF.EXT_JSON is '扩展JSON'
/

comment on column  T_BPMN_NODE_CONDITIONS_CONF.REMARK is '备注'
/

comment on column  T_BPMN_NODE_CONDITIONS_CONF.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPMN_NODE_CONDITIONS_CONF.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_CONDITIONS_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_CONDITIONS_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_CONDITIONS_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_CONDITIONS_CONF.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_NODE_CONDITIONS_CONF
    before insert
    on  T_BPMN_NODE_CONDITIONS_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_NODE_CONDITIONS_CONF.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_NODE_CONDITIONS_PARAM_CONF
(
    ID NUMBER(20) not null
		primary key,
    BPMN_NODE_CONDITIONS_ID NUMBER(20) not null,
    CONDITION_PARAM_TYPE NUMBER(11) not null,
    CONDITION_PARAM_NAME NVARCHAR2(50) not null,
    CONDITION_PARAM_JSOM NCLOB not null,
    OPERATOR NUMBER(10),
    COND_RELATION NUMBER(1),
    COND_GROUP NUMBER(10),
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_NODE_CONDITIONS_PARAM_CONF is '条件参数配置表'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.ID is 'ID'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.BPMN_NODE_CONDITIONS_ID is '条件配置ID'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.CONDITION_PARAM_TYPE is '参数类型'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.CONDITION_PARAM_NAME is '参数字段名'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.CONDITION_PARAM_JSOM is '参数JSON'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.OPERATOR is '操作符'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.COND_RELATION is '条件关系 0:AND,1:OR'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.COND_GROUP is '条件组'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.REMARK is '备注'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_CONDITIONS_PARAM_CONF.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_NODE_CONDITIONS_PARAM_CONF
    before insert
    on  T_BPMN_NODE_CONDITIONS_PARAM_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_NODE_CONDITIONS_PARAM_CONF.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_NODE_SIGN_UP_CONF
(
    ID NUMBER(20) not null
		primary key,
    BPMN_NODE_ID NUMBER(20) not null,
    AFTER_SIGN_UP_WAY NUMBER(11) default 1 not null,
    SIGN_UP_TYPE NUMBER(11) default 1 not null,
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_NODE_SIGN_UP_CONF is '节点签收配置表'
/

comment on column  T_BPMN_NODE_SIGN_UP_CONF.ID is 'ID'
/

comment on column  T_BPMN_NODE_SIGN_UP_CONF.BPMN_NODE_ID is '节点ID'
/

comment on column  T_BPMN_NODE_SIGN_UP_CONF.AFTER_SIGN_UP_WAY is '签收后流程方式 1:返回给指定人,2:不返回'
/

comment on column  T_BPMN_NODE_SIGN_UP_CONF.SIGN_UP_TYPE is '签收方式 1:顺序签收,2:全部签收,3:或签'
/

comment on column  T_BPMN_NODE_SIGN_UP_CONF.REMARK is '备注'
/

comment on column  T_BPMN_NODE_SIGN_UP_CONF.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPMN_NODE_SIGN_UP_CONF.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_SIGN_UP_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_SIGN_UP_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_SIGN_UP_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_SIGN_UP_CONF.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_NODE_SIGN_UP_CONF
    before insert
    on  T_BPMN_NODE_SIGN_UP_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_NODE_SIGN_UP_CONF.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_NODE_TO
(
    ID NUMBER(20) not null
		primary key,
    BPMN_NODE_ID NUMBER(20) not null,
    NODE_TO NVARCHAR2(60) default '',
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_NODE_TO is '审批流节点走向表'
/

comment on column  T_BPMN_NODE_TO.ID is 'ID'
/

comment on column  T_BPMN_NODE_TO.BPMN_NODE_ID is '节点ID'
/

comment on column  T_BPMN_NODE_TO.NODE_TO is '节点走向'
/

comment on column  T_BPMN_NODE_TO.REMARK is '备注'
/

comment on column  T_BPMN_NODE_TO.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPMN_NODE_TO.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_TO.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_TO.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_TO.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_TO.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_NODE_TO
    before insert
    on  T_BPMN_NODE_TO
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_NODE_TO.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_PROCESS_DEPT
(
    ID NUMBER(20) not null
		primary key,
    PROCESS_CODE NVARCHAR2(50),
    PROCESS_TYPE NUMBER(11),
    PROCESS_NAME NVARCHAR2(50),
    DEP_ID NUMBER(20),
    REMARKS NVARCHAR2(255),
    CREATE_TIME DATE default SYSDATE,
    CREATE_USER NUMBER(20),
    UPDATE_USER NUMBER(20),
    UPDATE_TIME DATE default SYSDATE,
    PROCESS_KEY NVARCHAR2(50),
    IS_DEL NUMBER(1) default 0,
    TENANT_ID NVARCHAR2(64) default '',
    IS_ALL NUMBER(1) default 0
)
    /

comment on table  BPM_PROCESS_DEPT is '流程部门关联表'
/

comment on column  BPM_PROCESS_DEPT.ID is 'ID'
/

comment on column  BPM_PROCESS_DEPT.PROCESS_CODE is '流程编号'
/

comment on column  BPM_PROCESS_DEPT.PROCESS_TYPE is '流程类型'
/

comment on column  BPM_PROCESS_DEPT.PROCESS_NAME is '流程名称'
/

comment on column  BPM_PROCESS_DEPT.DEP_ID is '部门ID'
/

comment on column  BPM_PROCESS_DEPT.REMARKS is '备注'
/

comment on column  BPM_PROCESS_DEPT.CREATE_TIME is '创建时间'
/

comment on column  BPM_PROCESS_DEPT.CREATE_USER is '创建人'
/

comment on column  BPM_PROCESS_DEPT.UPDATE_USER is '更新人'
/

comment on column  BPM_PROCESS_DEPT.UPDATE_TIME is '更新时间'
/

comment on column  BPM_PROCESS_DEPT.PROCESS_KEY is '流程KEY'
/

comment on column  BPM_PROCESS_DEPT.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  BPM_PROCESS_DEPT.TENANT_ID is '租户ID'
/

comment on column  BPM_PROCESS_DEPT.IS_ALL is '是否全部 0:否,1:是'
/

create trigger  TRG_BPM_PROCESS_DEPT
    before insert
    on  BPM_PROCESS_DEPT
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_PROCESS_DEPT.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_PROCESS_FORWARD
(
    ID NUMBER(20) not null
		primary key,
    FORWARD_USER_ID NVARCHAR2(50),
    FORWARD_USER_NAME NVARCHAR2(50),
    PROCESSINSTANCE_ID NVARCHAR2(64),
    NODE_ID NVARCHAR2(64),
    CREATE_TIME DATE default SYSDATE,
    CREATE_USER_ID NVARCHAR2(50),
    TASK_ID NVARCHAR2(50),
    IS_READ NUMBER(2) default 0,
    IS_DEL NUMBER(11) default 0,
    TENANT_ID NVARCHAR2(64) default '',
    UPDATE_TIME DATE default SYSDATE,
    PROCESS_NUMBER NVARCHAR2(50) default ''
)
    /

comment on table  BPM_PROCESS_FORWARD is '流程转发表'
/

comment on column  BPM_PROCESS_FORWARD.ID is 'ID'
/

comment on column  BPM_PROCESS_FORWARD.FORWARD_USER_ID is '转发用户ID'
/

comment on column  BPM_PROCESS_FORWARD.FORWARD_USER_NAME is '转发用户名称'
/

comment on column  BPM_PROCESS_FORWARD.PROCESSINSTANCE_ID is '流程实例ID'
/

comment on column  BPM_PROCESS_FORWARD.NODE_ID is '节点ID'
/

comment on column  BPM_PROCESS_FORWARD.CREATE_TIME is '创建时间'
/

comment on column  BPM_PROCESS_FORWARD.CREATE_USER_ID is '创建人ID'
/

comment on column  BPM_PROCESS_FORWARD.TASK_ID is '任务ID'
/

comment on column  BPM_PROCESS_FORWARD.IS_READ is '是否已读 0:否,1:是'
/

comment on column  BPM_PROCESS_FORWARD.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  BPM_PROCESS_FORWARD.TENANT_ID is '租户ID'
/

comment on column  BPM_PROCESS_FORWARD.UPDATE_TIME is '更新时间'
/

comment on column  BPM_PROCESS_FORWARD.PROCESS_NUMBER is '流程编号'
/

create index  IDX_BPM_PROCESS_FORWARD_FORWARD_USER_ID
    on  BPM_PROCESS_FORWARD (FORWARD_USER_ID)
    /

create index  IDX_BPM_PROCESS_FORWARD_FORWARD_USER_ID_IS_READ
    on  BPM_PROCESS_FORWARD (FORWARD_USER_ID, IS_READ)
    /

create trigger  TRG_BPM_PROCESS_FORWARD
    before insert
    on  BPM_PROCESS_FORWARD
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_PROCESS_FORWARD.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_PROCESS_NODE_OVERTIME
(
    ID NUMBER(20) not null
		primary key,
    NOTICE_TYPE NUMBER(11),
    NODE_NAME NVARCHAR2(50),
    NODE_KEY NVARCHAR2(50),
    PROCESS_KEY NVARCHAR2(50),
    NOTICE_TIME NUMBER(11),
    IS_DEL NUMBER(11) default 0,
    TENANT_ID NVARCHAR2(64) default ''
)
    /

comment on table  BPM_PROCESS_NODE_OVERTIME is '流程节点超时提醒表'
/

comment on column  BPM_PROCESS_NODE_OVERTIME.ID is 'ID'
/

comment on column  BPM_PROCESS_NODE_OVERTIME.NOTICE_TYPE is '提醒类型 1:邮件,2:短信,3:APP推送'
/

comment on column  BPM_PROCESS_NODE_OVERTIME.NODE_NAME is '节点名称'
/

comment on column  BPM_PROCESS_NODE_OVERTIME.NODE_KEY is '节点ID'
/

comment on column  BPM_PROCESS_NODE_OVERTIME.PROCESS_KEY is '流程KEY'
/

comment on column  BPM_PROCESS_NODE_OVERTIME.NOTICE_TIME is '提醒时间'
/

comment on column  BPM_PROCESS_NODE_OVERTIME.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  BPM_PROCESS_NODE_OVERTIME.TENANT_ID is '租户ID'
/

create trigger  TRG_BPM_PROCESS_NODE_OVERTIME
    before insert
    on  BPM_PROCESS_NODE_OVERTIME
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_PROCESS_NODE_OVERTIME.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_PROCESS_NODE_RECORD
(
    ID NUMBER(20) not null
		primary key,
    PROCESSINSTANCE_ID NVARCHAR2(64),
    TASK_ID NVARCHAR2(50),
    CREATE_TIME DATE default SYSDATE,
    IS_DEL NUMBER(11) default 0,
    TENANT_ID NVARCHAR2(64) default ''
)
    /

comment on table  BPM_PROCESS_NODE_RECORD is '流程节点记录表'
/

comment on column  BPM_PROCESS_NODE_RECORD.ID is 'ID'
/

comment on column  BPM_PROCESS_NODE_RECORD.PROCESSINSTANCE_ID is '流程实例ID'
/

comment on column  BPM_PROCESS_NODE_RECORD.TASK_ID is '任务ID'
/

comment on column  BPM_PROCESS_NODE_RECORD.CREATE_TIME is '创建时间'
/

comment on column  BPM_PROCESS_NODE_RECORD.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  BPM_PROCESS_NODE_RECORD.TENANT_ID is '租户ID'
/

create trigger  TRG_BPM_PROCESS_NODE_RECORD
    before insert
    on  BPM_PROCESS_NODE_RECORD
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_PROCESS_NODE_RECORD.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_PROCESS_NODE_SUBMIT
(
    ID NUMBER(20) not null
		primary key,
    PROCESSINSTANCE_ID NVARCHAR2(64),
    BACK_TYPE NUMBER(11),
    NODE_KEY NVARCHAR2(50),
    CREATE_TIME DATE default SYSDATE,
    CREATE_USER NVARCHAR2(50),
    STATE NUMBER(11),
    IS_DEL NUMBER(11) default 0,
    TENANT_ID NVARCHAR2(64) default ''
)
    /

comment on table  BPM_PROCESS_NODE_SUBMIT is '流程节点提交表'
/

comment on column  BPM_PROCESS_NODE_SUBMIT.ID is 'ID'
/

comment on column  BPM_PROCESS_NODE_SUBMIT.PROCESSINSTANCE_ID is '流程实例ID'
/

comment on column  BPM_PROCESS_NODE_SUBMIT.BACK_TYPE is '退回类型'
/

comment on column  BPM_PROCESS_NODE_SUBMIT.NODE_KEY is '节点KEY'
/

comment on column  BPM_PROCESS_NODE_SUBMIT.CREATE_TIME is '创建时间'
/

comment on column  BPM_PROCESS_NODE_SUBMIT.CREATE_USER is '创建人'
/

comment on column  BPM_PROCESS_NODE_SUBMIT.STATE is '状态'
/

comment on column  BPM_PROCESS_NODE_SUBMIT.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  BPM_PROCESS_NODE_SUBMIT.TENANT_ID is '租户ID'
/

create index  IDX_BPM_PROCESS_NODE_SUBMIT_PROCESSINSTANCE_ID
    on  BPM_PROCESS_NODE_SUBMIT (PROCESSINSTANCE_ID)
    /

create trigger  TRG_BPM_PROCESS_NODE_SUBMIT
    before insert
    on  BPM_PROCESS_NODE_SUBMIT
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_PROCESS_NODE_SUBMIT.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_PROCESS_NOTICE
(
    ID NUMBER(20) not null
		primary key,
    TYPE NUMBER(11),
    PROCESS_KEY NVARCHAR2(50),
    IS_DEL NUMBER(11) default 0,
    TENANT_ID NVARCHAR2(64) default '',
    constraint BPM_PROCESS_NOTICE_UNQ
        unique (PROCESS_KEY, TYPE)
)
    /

comment on table  BPM_PROCESS_NOTICE is '流程通知表'
/

comment on column  BPM_PROCESS_NOTICE.ID is 'ID'
/

comment on column  BPM_PROCESS_NOTICE.TYPE is '通知类型 1:邮件,2:短信,3:APP推送'
/

comment on column  BPM_PROCESS_NOTICE.PROCESS_KEY is '流程KEY'
/

comment on column  BPM_PROCESS_NOTICE.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  BPM_PROCESS_NOTICE.TENANT_ID is '租户ID'
/

create trigger  TRG_BPM_PROCESS_NOTICE
    before insert
    on  BPM_PROCESS_NOTICE
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_PROCESS_NOTICE.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_TASKCONFIG
(
    ID NUMBER(20) not null
		primary key,
    PROC_DEF_ID_ NVARCHAR2(100),
    TASK_DEF_KEY_ NVARCHAR2(100),
    USER_ID NUMBER(11),
    "NUMBER" NUMBER(11),
    STATUS NUMBER(11),
    ORIGINAL_TYPE NUMBER(2),
    IS_DEL NUMBER(11) default 0,
    TENANT_ID NVARCHAR2(64) default ''
)
    /

comment on table  BPM_TASKCONFIG is '任务配置表'
/

comment on column  BPM_TASKCONFIG.ID is 'ID'
/

comment on column  BPM_TASKCONFIG.PROC_DEF_ID_ is '流程定义ID'
/

comment on column  BPM_TASKCONFIG.TASK_DEF_KEY_ is '任务定义KEY'
/

comment on column  BPM_TASKCONFIG.USER_ID is '用户ID'
/

comment on column  BPM_TASKCONFIG."NUMBER" is '编号'
/

comment on column  BPM_TASKCONFIG.STATUS is '状态'
/

comment on column  BPM_TASKCONFIG.ORIGINAL_TYPE is '原始类型'
/

comment on column  BPM_TASKCONFIG.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  BPM_TASKCONFIG.TENANT_ID is '租户ID'
/

create index  IDX_BPM_TASKCONFIG_PROC_DEF_ID_TASK_DEF_KEY
    on  BPM_TASKCONFIG (PROC_DEF_ID_, TASK_DEF_KEY_)
    /

create trigger  TRG_BPM_TASKCONFIG
    before insert
    on  BPM_TASKCONFIG
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_TASKCONFIG.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPM_VARIABLE
(
    ID NUMBER(20) not null
		primary key,
    PROCESS_NUM NVARCHAR2(60) default '',
    PROCESS_NAME NVARCHAR2(60) default '',
    PROCESS_DESC NVARCHAR2(255) default '',
    PROCESS_START_CONDITIONS NCLOB not null,
    BPMN_CODE NVARCHAR2(60) default '',
    IS_NEW_DATA NUMBER(11) default 0,
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPM_VARIABLE is '流程变量表'
/

comment on column  T_BPM_VARIABLE.ID is 'ID'
/

comment on column  T_BPM_VARIABLE.PROCESS_NUM is '流程编号'
/

comment on column  T_BPM_VARIABLE.PROCESS_NAME is '流程名称'
/

comment on column  T_BPM_VARIABLE.PROCESS_DESC is '流程描述'
/

comment on column  T_BPM_VARIABLE.PROCESS_START_CONDITIONS is '流程启动条件'
/

comment on column  T_BPM_VARIABLE.BPMN_CODE is 'BPMN编码'
/

comment on column  T_BPM_VARIABLE.IS_NEW_DATA is '是否新数据 0:否,1:是'
/

comment on column  T_BPM_VARIABLE.REMARK is '备注'
/

comment on column  T_BPM_VARIABLE.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPM_VARIABLE.TENANT_ID is '租户ID'
/

comment on column  T_BPM_VARIABLE.CREATE_USER is '创建人'
/

comment on column  T_BPM_VARIABLE.CREATE_TIME is '创建时间'
/

comment on column  T_BPM_VARIABLE.UPDATE_USER is '更新人'
/

comment on column  T_BPM_VARIABLE.UPDATE_TIME is '更新时间'
/

create index  IDX_T_BPM_VARIABLE_PROCESS_NUM
    on  T_BPM_VARIABLE (PROCESS_NUM)
    /

create trigger  TRG_T_BPM_VARIABLE
    before insert
    on  T_BPM_VARIABLE
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPM_VARIABLE.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPM_VARIABLE_APPROVE_REMIND
(
    ID NUMBER(20) not null
		primary key,
    VARIABLE_ID NUMBER(20) not null,
    ELEMENT_ID NVARCHAR2(60) default '',
    CONTENT NCLOB not null,
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPM_VARIABLE_APPROVE_REMIND is '流程提醒变量表'
/

comment on column  T_BPM_VARIABLE_APPROVE_REMIND.ID is 'ID'
/

comment on column  T_BPM_VARIABLE_APPROVE_REMIND.VARIABLE_ID is '变量ID'
/

comment on column  T_BPM_VARIABLE_APPROVE_REMIND.ELEMENT_ID is '元素ID'
/

comment on column  T_BPM_VARIABLE_APPROVE_REMIND.CONTENT is '审批内容'
/

comment on column  T_BPM_VARIABLE_APPROVE_REMIND.REMARK is '备注'
/

comment on column  T_BPM_VARIABLE_APPROVE_REMIND.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPM_VARIABLE_APPROVE_REMIND.TENANT_ID is '租户ID'
/

comment on column  T_BPM_VARIABLE_APPROVE_REMIND.CREATE_USER is '创建人'
/

comment on column  T_BPM_VARIABLE_APPROVE_REMIND.CREATE_TIME is '创建时间'
/

comment on column  T_BPM_VARIABLE_APPROVE_REMIND.UPDATE_USER is '更新人'
/

comment on column  T_BPM_VARIABLE_APPROVE_REMIND.UPDATE_TIME is '更新时间'
/

create index  IDX_T_BPM_VARIABLE_APPROVE_REMIND_VARIABLE_ID_ELEMENT_ID
    on  T_BPM_VARIABLE_APPROVE_REMIND (VARIABLE_ID, ELEMENT_ID)
    /

create trigger  TRG_T_BPM_VARIABLE_APPROVE_REMIND
    before insert
    on  T_BPM_VARIABLE_APPROVE_REMIND
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPM_VARIABLE_APPROVE_REMIND.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPM_VARIABLE_BUTTON
(
    ID NUMBER(20) not null
		primary key,
    VARIABLE_ID NUMBER(20) not null,
    ELEMENT_ID NVARCHAR2(60) default '',
    BUTTON_PAGE_TYPE NUMBER(11) not null,
    BUTTON_TYPE NUMBER(11) not null,
    BUTTON_NAME NVARCHAR2(60) default '',
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPM_VARIABLE_BUTTON is '变量按钮表'
/

comment on column  T_BPM_VARIABLE_BUTTON.ID is 'ID'
/

comment on column  T_BPM_VARIABLE_BUTTON.VARIABLE_ID is '变量ID'
/

comment on column  T_BPM_VARIABLE_BUTTON.ELEMENT_ID is '元素ID'
/

comment on column  T_BPM_VARIABLE_BUTTON.BUTTON_PAGE_TYPE is '按钮页面类型 1:提交页面,2:审批页面'
/

comment on column  T_BPM_VARIABLE_BUTTON.BUTTON_TYPE is '按钮类型'
/

comment on column  T_BPM_VARIABLE_BUTTON.BUTTON_NAME is '按钮名称'
/

comment on column  T_BPM_VARIABLE_BUTTON.REMARK is '备注'
/

comment on column  T_BPM_VARIABLE_BUTTON.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPM_VARIABLE_BUTTON.TENANT_ID is '租户ID'
/

comment on column  T_BPM_VARIABLE_BUTTON.CREATE_USER is '创建人'
/

comment on column  T_BPM_VARIABLE_BUTTON.CREATE_TIME is '创建时间'
/

comment on column  T_BPM_VARIABLE_BUTTON.UPDATE_USER is '更新人'
/

comment on column  T_BPM_VARIABLE_BUTTON.UPDATE_TIME is '更新时间'
/

create index  IDX_T_BPM_VARIABLE_BUTTON_VARIABLE_ID
    on  T_BPM_VARIABLE_BUTTON (VARIABLE_ID)
    /

create trigger  TRG_T_BPM_VARIABLE_BUTTON
    before insert
    on  T_BPM_VARIABLE_BUTTON
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPM_VARIABLE_BUTTON.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPM_VARIABLE_MESSAGE
(
    ID NUMBER(20) not null
		primary key,
    VARIABLE_ID NUMBER(20) not null,
    ELEMENT_ID NVARCHAR2(60) default '' not null,
    MESSAGE_TYPE NUMBER(11) default 0 not null,
    EVENT_TYPE NUMBER(11) default 0 not null,
    CONTENT NCLOB not null,
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPM_VARIABLE_MESSAGE is '变量消息表'
/

comment on column  T_BPM_VARIABLE_MESSAGE.ID is 'ID'
/

comment on column  T_BPM_VARIABLE_MESSAGE.VARIABLE_ID is '变量ID'
/

comment on column  T_BPM_VARIABLE_MESSAGE.ELEMENT_ID is '元素ID'
/

comment on column  T_BPM_VARIABLE_MESSAGE.MESSAGE_TYPE is '消息类型 1:节点外,2:节点内'
/

comment on column  T_BPM_VARIABLE_MESSAGE.EVENT_TYPE is '事件类型'
/

comment on column  T_BPM_VARIABLE_MESSAGE.CONTENT is '内容'
/

comment on column  T_BPM_VARIABLE_MESSAGE.REMARK is '备注'
/

comment on column  T_BPM_VARIABLE_MESSAGE.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPM_VARIABLE_MESSAGE.TENANT_ID is '租户ID'
/

comment on column  T_BPM_VARIABLE_MESSAGE.CREATE_USER is '创建人'
/

comment on column  T_BPM_VARIABLE_MESSAGE.CREATE_TIME is '创建时间'
/

comment on column  T_BPM_VARIABLE_MESSAGE.UPDATE_USER is '更新人'
/

comment on column  T_BPM_VARIABLE_MESSAGE.UPDATE_TIME is '更新时间'
/

create index  IDX_T_BPM_VARIABLE_MESSAGE_VARIABLE_ID_ELEMENT_ID_MESSAGE_TYPE_EVENT_TYPE
    on  T_BPM_VARIABLE_MESSAGE (VARIABLE_ID, ELEMENT_ID, MESSAGE_TYPE, EVENT_TYPE)
    /

create index  IDX_T_BPM_VARIABLE_MESSAGE_VARIABLE_ID_MESSAGE_TYPE_EVENT_TYPE
    on  T_BPM_VARIABLE_MESSAGE (VARIABLE_ID, MESSAGE_TYPE, EVENT_TYPE)
    /

create trigger  TRG_T_BPM_VARIABLE_MESSAGE
    before insert
    on  T_BPM_VARIABLE_MESSAGE
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPM_VARIABLE_MESSAGE.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPM_VARIABLE_MULTIPLAYER
(
    ID NUMBER(20) not null
		primary key,
    VARIABLE_ID NUMBER(20) not null,
    ELEMENT_ID NVARCHAR2(60) default '' not null,
    ELEMENT_NAME NVARCHAR2(60) default '' not null,
    NODE_ID NVARCHAR2(60),
    COLLECTION_NAME NVARCHAR2(60) default '' not null,
    SIGN_TYPE NUMBER(11) not null,
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPM_VARIABLE_MULTIPLAYER is '流程多人变量表'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.ID is 'ID'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.VARIABLE_ID is '变量ID'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.ELEMENT_ID is '元素ID'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.ELEMENT_NAME is '元素名称'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.NODE_ID is '节点ID'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.COLLECTION_NAME is '集合名称'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.SIGN_TYPE is '签署类型 1:全部签署,2:或签署'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.REMARK is '备注'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.TENANT_ID is '租户ID'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.CREATE_USER is '创建人'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.CREATE_TIME is '创建时间'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.UPDATE_USER is '更新人'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER.UPDATE_TIME is '更新时间'
/

create index  IDX_T_BPM_VARIABLE_MULTIPLAYER_VARIABLE_ID
    on  T_BPM_VARIABLE_MULTIPLAYER (VARIABLE_ID)
    /

create index  IDX_T_BPM_VARIABLE_MULTIPLAYER_VARIABLE_ID_ELEMENT_ID
    on  T_BPM_VARIABLE_MULTIPLAYER (VARIABLE_ID, ELEMENT_ID)
    /

create trigger  TRG_T_BPM_VARIABLE_MULTIPLAYER
    before insert
    on  T_BPM_VARIABLE_MULTIPLAYER
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPM_VARIABLE_MULTIPLAYER.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL
(
    ID NUMBER(20) not null
		primary key,
    VARIABLE_MULTIPLAYER_ID NUMBER(20) not null,
    ASSIGNEE NVARCHAR2(60) default '',
    ASSIGNEE_NAME NVARCHAR2(60) default '',
    UNDERTAKE_STATUS NUMBER(11) not null,
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL is '多人变量人员表'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL.ID is 'ID'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL.VARIABLE_MULTIPLAYER_ID is '多人变量ID'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL.ASSIGNEE is '审批人'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL.ASSIGNEE_NAME is '审批人名称'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL.UNDERTAKE_STATUS is '是否承接(0:否,1:是)'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL.REMARK is '备注'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL.TENANT_ID is '租户ID'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL.CREATE_USER is '创建人'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL.CREATE_TIME is '创建时间'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL.UPDATE_USER is '更新人'
/

comment on column  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL.UPDATE_TIME is '更新时间'
/

create index  IDX_T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL_VARIABLE_MULTIPLAYER_ID
    on  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL (VARIABLE_MULTIPLAYER_ID)
    /

create trigger  TRG_T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL
    before insert
    on  T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPM_VARIABLE_MULTIPLAYER_PERSONNEL.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPM_VARIABLE_SEQUENCE_FLOW
(
    ID NUMBER(20) not null
		primary key,
    VARIABLE_ID NUMBER(20) not null,
    ELEMENT_ID NVARCHAR2(60) default '',
    ELEMENT_NAME NVARCHAR2(60) default '',
    ELEMENT_FROM_ID NVARCHAR2(60) default '' not null,
    ELEMENT_TO_ID NVARCHAR2(60) default '',
    SEQUENCE_FLOW_TYPE NUMBER(11) not null,
    SEQUENCE_FLOW_CONDITIONS NVARCHAR2(100) default '',
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPM_VARIABLE_SEQUENCE_FLOW is '流程变量顺序流表'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.ID is 'ID'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.VARIABLE_ID is '变量ID'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.ELEMENT_ID is '元素ID'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.ELEMENT_NAME is '元素名称'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.ELEMENT_FROM_ID is '前驱节点ID'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.ELEMENT_TO_ID is '后继节点ID'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.SEQUENCE_FLOW_TYPE is '顺序流类型'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.SEQUENCE_FLOW_CONDITIONS is '顺序流条件'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.REMARK is '备注'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.TENANT_ID is '租户ID'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.CREATE_USER is '创建人'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.CREATE_TIME is '创建时间'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.UPDATE_USER is '更新人'
/

comment on column  T_BPM_VARIABLE_SEQUENCE_FLOW.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPM_VARIABLE_SEQUENCE_FLOW
    before insert
    on  T_BPM_VARIABLE_SEQUENCE_FLOW
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPM_VARIABLE_SEQUENCE_FLOW.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPM_VARIABLE_SIGN_UP
(
    ID NUMBER(20) not null
		primary key,
    VARIABLE_ID NUMBER(20) not null,
    ELEMENT_ID NVARCHAR2(60) default '',
    ELEMENT_NAME NVARCHAR2(60) default '',
    NODE_ID NVARCHAR2(60),
    COLLECTION_NAME NVARCHAR2(60) default '',
    SIGN_TYPE NUMBER(11) not null,
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPM_VARIABLE_SIGN_UP is '流程变量会签表'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.ID is 'ID'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.VARIABLE_ID is '变量ID'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.ELEMENT_ID is '元素ID'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.ELEMENT_NAME is '元素名称'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.NODE_ID is '节点ID'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.COLLECTION_NAME is '集合名称'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.SIGN_TYPE is '会签类型 1:全部会签 2:或会签'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.REMARK is '备注'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.TENANT_ID is '租户ID'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.CREATE_USER is '创建人'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.CREATE_TIME is '创建时间'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.UPDATE_USER is '更新人'
/

comment on column  T_BPM_VARIABLE_SIGN_UP.UPDATE_TIME is '更新时间'
/

create index  IDX_T_BPM_VARIABLE_SIGN_UP_VARIABLE_ID
    on  T_BPM_VARIABLE_SIGN_UP (VARIABLE_ID)
    /

create index  IDX_T_BPM_VARIABLE_SIGN_UP_VARIABLE_ID_ELEMENT_ID
    on  T_BPM_VARIABLE_SIGN_UP (VARIABLE_ID, ELEMENT_ID)
    /

create trigger  TRG_T_BPM_VARIABLE_SIGN_UP
    before insert
    on  T_BPM_VARIABLE_SIGN_UP
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPM_VARIABLE_SIGN_UP.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPM_VARIABLE_SIGN_UP_PERSONNEL
(
    ID NUMBER(20) not null
		primary key,
    VARIABLE_SIGN_UP_ID NUMBER(20) not null,
    ASSIGNEE NVARCHAR2(60) default '',
    ASSIGNEE_NAME NVARCHAR2(60) default '',
    UNDERTAKE_STATUS NUMBER(11) not null,
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPM_VARIABLE_SIGN_UP_PERSONNEL is '流程变量会签人员表'
/

comment on column  T_BPM_VARIABLE_SIGN_UP_PERSONNEL.ID is 'ID'
/

comment on column  T_BPM_VARIABLE_SIGN_UP_PERSONNEL.VARIABLE_SIGN_UP_ID is '会签变量ID'
/

comment on column  T_BPM_VARIABLE_SIGN_UP_PERSONNEL.ASSIGNEE is '处理人'
/

comment on column  T_BPM_VARIABLE_SIGN_UP_PERSONNEL.ASSIGNEE_NAME is '处理人姓名'
/

comment on column  T_BPM_VARIABLE_SIGN_UP_PERSONNEL.UNDERTAKE_STATUS is '是否已处理 0:否,1:是'
/

comment on column  T_BPM_VARIABLE_SIGN_UP_PERSONNEL.REMARK is '备注'
/

comment on column  T_BPM_VARIABLE_SIGN_UP_PERSONNEL.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPM_VARIABLE_SIGN_UP_PERSONNEL.TENANT_ID is '租户ID'
/

comment on column  T_BPM_VARIABLE_SIGN_UP_PERSONNEL.CREATE_USER is '创建人'
/

comment on column  T_BPM_VARIABLE_SIGN_UP_PERSONNEL.CREATE_TIME is '创建时间'
/

comment on column  T_BPM_VARIABLE_SIGN_UP_PERSONNEL.UPDATE_USER is '更新人'
/

comment on column  T_BPM_VARIABLE_SIGN_UP_PERSONNEL.UPDATE_TIME is '更新时间'
/

create index  IDX_T_BPM_VARIABLE_SIGN_UP_PERSONNEL_VARIABLE_SIGN_UP_ID
    on  T_BPM_VARIABLE_SIGN_UP_PERSONNEL (VARIABLE_SIGN_UP_ID)
    /

create trigger  TRG_T_BPM_VARIABLE_SIGN_UP_PERSONNEL
    before insert
    on  T_BPM_VARIABLE_SIGN_UP_PERSONNEL
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPM_VARIABLE_SIGN_UP_PERSONNEL.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPM_VARIABLE_SINGLE
(
    ID NUMBER(20) not null
		primary key,
    VARIABLE_ID NUMBER(20) not null,
    ELEMENT_ID NVARCHAR2(60) default '' not null,
    NODE_ID NVARCHAR2(60),
    ELEMENT_NAME NVARCHAR2(60) default '' not null,
    ASSIGNEE_PARAM_NAME NVARCHAR2(60) default '' not null,
    ASSIGNEE NVARCHAR2(60) default '' not null,
    ASSIGNEE_NAME NVARCHAR2(60) default '' not null,
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPM_VARIABLE_SINGLE is '节点单人审批人配置表'
/

comment on column  T_BPM_VARIABLE_SINGLE.ID is '主键ID'
/

comment on column  T_BPM_VARIABLE_SINGLE.VARIABLE_ID is '变量ID'
/

comment on column  T_BPM_VARIABLE_SINGLE.ELEMENT_ID is '元素ID'
/

comment on column  T_BPM_VARIABLE_SINGLE.NODE_ID is '节点ID'
/

comment on column  T_BPM_VARIABLE_SINGLE.ELEMENT_NAME is '元素名称'
/

comment on column  T_BPM_VARIABLE_SINGLE.ASSIGNEE_PARAM_NAME is '审批人参数名（变量名）'
/

comment on column  T_BPM_VARIABLE_SINGLE.ASSIGNEE is '审批人ID'
/

comment on column  T_BPM_VARIABLE_SINGLE.ASSIGNEE_NAME is '审批人姓名'
/

comment on column  T_BPM_VARIABLE_SINGLE.REMARK is '备注'
/

comment on column  T_BPM_VARIABLE_SINGLE.IS_DEL is '是否删除：0-否，1-是'
/

comment on column  T_BPM_VARIABLE_SINGLE.TENANT_ID is '租户ID'
/

comment on column  T_BPM_VARIABLE_SINGLE.CREATE_USER is '创建人'
/

comment on column  T_BPM_VARIABLE_SINGLE.CREATE_TIME is '创建时间'
/

comment on column  T_BPM_VARIABLE_SINGLE.UPDATE_USER is '更新人'
/

comment on column  T_BPM_VARIABLE_SINGLE.UPDATE_TIME is '更新时间'
/

create index  IDX_T_BPM_VARIABLE_SINGLE_VARIABLE_ID
    on  T_BPM_VARIABLE_SINGLE (VARIABLE_ID)
    /

create index  IDX_T_BPM_VARIABLE_SINGLE_VARIABLE_ID_ELEMENT_ID
    on  T_BPM_VARIABLE_SINGLE (VARIABLE_ID, ELEMENT_ID)
    /

create trigger  TRG_T_BPM_VARIABLE_SINGLE
    before insert
    on  T_BPM_VARIABLE_SINGLE
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPM_VARIABLE_SINGLE.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPM_VARIABLE_VIEW_PAGE_BUTTON
(
    ID NUMBER(20) not null
		primary key,
    VARIABLE_ID NUMBER(20) not null,
    ELEMENT_ID NVARCHAR2(60) default '' not null,
    BUTTON_PAGE_TYPE NUMBER(11) not null,
    BUTTON_TYPE NUMBER(11) not null,
    BUTTON_NAME NVARCHAR2(60) default '',
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPM_VARIABLE_VIEW_PAGE_BUTTON is '流程变量查看页按钮表'
/

comment on column  T_BPM_VARIABLE_VIEW_PAGE_BUTTON.ID is 'ID'
/

comment on column  T_BPM_VARIABLE_VIEW_PAGE_BUTTON.VARIABLE_ID is '变量ID'
/

comment on column  T_BPM_VARIABLE_VIEW_PAGE_BUTTON.ELEMENT_ID is '元素ID'
/

comment on column  T_BPM_VARIABLE_VIEW_PAGE_BUTTON.BUTTON_PAGE_TYPE is '按钮页面类型 1:提交页,2:审批页'
/

comment on column  T_BPM_VARIABLE_VIEW_PAGE_BUTTON.BUTTON_TYPE is '按钮类型'
/

comment on column  T_BPM_VARIABLE_VIEW_PAGE_BUTTON.BUTTON_NAME is '按钮名称'
/

comment on column  T_BPM_VARIABLE_VIEW_PAGE_BUTTON.REMARK is '备注'
/

comment on column  T_BPM_VARIABLE_VIEW_PAGE_BUTTON.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPM_VARIABLE_VIEW_PAGE_BUTTON.TENANT_ID is '租户ID'
/

comment on column  T_BPM_VARIABLE_VIEW_PAGE_BUTTON.CREATE_USER is '创建人'
/

comment on column  T_BPM_VARIABLE_VIEW_PAGE_BUTTON.CREATE_TIME is '创建时间'
/

comment on column  T_BPM_VARIABLE_VIEW_PAGE_BUTTON.UPDATE_USER is '更新人'
/

comment on column  T_BPM_VARIABLE_VIEW_PAGE_BUTTON.UPDATE_TIME is '更新时间'
/

create index  IDX_T_BPM_VARIABLE_VIEW_PAGE_BUTTON_VARIABLE_ID
    on  T_BPM_VARIABLE_VIEW_PAGE_BUTTON (VARIABLE_ID)
    /

create trigger  TRG_T_BPM_VARIABLE_VIEW_PAGE_BUTTON
    before insert
    on  T_BPM_VARIABLE_VIEW_PAGE_BUTTON
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPM_VARIABLE_VIEW_PAGE_BUTTON.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_VERIFY_INFO
(
    ID NUMBER(20) not null
		primary key,
    RUN_INFO_ID NVARCHAR2(64),
    VERIFY_USER_ID NVARCHAR2(50),
    VERIFY_USER_NAME NVARCHAR2(100),
    VERIFY_STATUS NUMBER(1),
    VERIFY_DESC NVARCHAR2(500),
    VERIFY_DATE DATE default SYSDATE,
    TASK_NAME NVARCHAR2(64),
    TASK_ID NVARCHAR2(64),
    TASK_DEF_KEY NVARCHAR2(255),
    BUSINESS_TYPE NUMBER(1),
    BUSINESS_ID NVARCHAR2(128),
    ORIGINAL_ID NVARCHAR2(64),
    PROCESS_CODE NVARCHAR2(64),
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default ''
)
    /

comment on table  BPM_VERIFY_INFO is '审核信息表'
/

comment on column  BPM_VERIFY_INFO.ID is 'ID'
/

comment on column  BPM_VERIFY_INFO.RUN_INFO_ID is '流程实例ID'
/

comment on column  BPM_VERIFY_INFO.VERIFY_USER_ID is '审批人'
/

comment on column  BPM_VERIFY_INFO.VERIFY_USER_NAME is '审批人姓名'
/

comment on column  BPM_VERIFY_INFO.VERIFY_STATUS is '审批状态'
/

comment on column  BPM_VERIFY_INFO.VERIFY_DESC is '审批描述'
/

comment on column  BPM_VERIFY_INFO.VERIFY_DATE is '审批日期'
/

comment on column  BPM_VERIFY_INFO.TASK_NAME is '任务名称'
/

comment on column  BPM_VERIFY_INFO.TASK_ID is '任务ID'
/

comment on column  BPM_VERIFY_INFO.TASK_DEF_KEY is '任务定义KEY'
/

comment on column  BPM_VERIFY_INFO.BUSINESS_TYPE is '业务类型'
/

comment on column  BPM_VERIFY_INFO.BUSINESS_ID is '业务ID'
/

comment on column  BPM_VERIFY_INFO.ORIGINAL_ID is '原始审批人'
/

comment on column  BPM_VERIFY_INFO.PROCESS_CODE is '流程编号'
/

comment on column  BPM_VERIFY_INFO.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  BPM_VERIFY_INFO.TENANT_ID is '租户ID'
/

create index  IDX_BPM_VERIFY_INFO_BUSINESS_TYPE_BUSINESS_ID
    on  BPM_VERIFY_INFO (BUSINESS_TYPE, BUSINESS_ID)
    /

create index  IDX_BPM_VERIFY_INFO_PROCESS_CODE
    on  BPM_VERIFY_INFO (PROCESS_CODE)
    /

create trigger  TRG_BPM_VERIFY_INFO
    before insert
    on  BPM_VERIFY_INFO
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_VERIFY_INFO.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_DEFAULT_TEMPLATE
(
    ID NUMBER(20) not null
		primary key,
    EVENT NUMBER(11),
    TEMPLATE_ID NUMBER(20),
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_TIME DATE default SYSDATE,
    CREATE_USER NVARCHAR2(255) default '',
    UPDATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(255) default ''
)
    /

comment on table  T_DEFAULT_TEMPLATE is '默认模板表'
/

comment on column  T_DEFAULT_TEMPLATE.ID is 'ID'
/

comment on column  T_DEFAULT_TEMPLATE.EVENT is '事件'
/

comment on column  T_DEFAULT_TEMPLATE.TEMPLATE_ID is '模板ID'
/

comment on column  T_DEFAULT_TEMPLATE.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_DEFAULT_TEMPLATE.TENANT_ID is '租户ID'
/

comment on column  T_DEFAULT_TEMPLATE.CREATE_TIME is '创建时间'
/

comment on column  T_DEFAULT_TEMPLATE.CREATE_USER is '创建人'
/

comment on column  T_DEFAULT_TEMPLATE.UPDATE_TIME is '更新时间'
/

comment on column  T_DEFAULT_TEMPLATE.UPDATE_USER is '更新人'
/

create trigger  TRG_T_DEFAULT_TEMPLATE
    before insert
    on  T_DEFAULT_TEMPLATE
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_DEFAULT_TEMPLATE.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_USER_EMAIL_SEND
(
    ID NUMBER(20) not null
		primary key,
    SENDER NVARCHAR2(32) not null,
    RECEIVER NVARCHAR2(100) not null,
    TITLE NVARCHAR2(255) not null,
    CONTENT NCLOB not null,
    CREATE_TIME DATE default SYSDATE,
    UPDATE_TIME DATE default SYSDATE,
    CREATE_USER NVARCHAR2(50) not null,
    UPDATE_USER NVARCHAR2(50) not null,
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default ''
)
    /

comment on table  T_USER_EMAIL_SEND is '用户邮件发送表'
/

comment on column  T_USER_EMAIL_SEND.ID is 'ID'
/

comment on column  T_USER_EMAIL_SEND.SENDER is '发送人'
/

comment on column  T_USER_EMAIL_SEND.RECEIVER is '接收人'
/

comment on column  T_USER_EMAIL_SEND.TITLE is '标题'
/

comment on column  T_USER_EMAIL_SEND.CONTENT is '内容'
/

comment on column  T_USER_EMAIL_SEND.CREATE_TIME is '创建时间'
/

comment on column  T_USER_EMAIL_SEND.UPDATE_TIME is '更新时间'
/

comment on column  T_USER_EMAIL_SEND.CREATE_USER is '创建人'
/

comment on column  T_USER_EMAIL_SEND.UPDATE_USER is '更新人'
/

comment on column  T_USER_EMAIL_SEND.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_USER_EMAIL_SEND.TENANT_ID is '租户ID'
/

create index  IDX_T_USER_EMAIL_SEND_RECEIVER
    on  T_USER_EMAIL_SEND (RECEIVER)
    /

create trigger  TRG_T_USER_EMAIL_SEND
    before insert
    on  T_USER_EMAIL_SEND
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_USER_EMAIL_SEND.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_METHOD_REPLAY
(
    ID NUMBER(20) not null
		primary key,
    PROJECT_NAME NVARCHAR2(100),
    CLASS_NAME NVARCHAR2(255),
    METHOD_NAME NVARCHAR2(255),
    PARAM_TYPE NVARCHAR2(255),
    ARGS NCLOB,
    NOW_TIME DATE,
    ERROR_MSG NCLOB,
    ALREADY_REPLAY_TIMES NUMBER(11),
    MAX_REPLAY_TIMES NUMBER(11)
)
    /

comment on table  T_METHOD_REPLAY is '方法重放记录表'
/

comment on column  T_METHOD_REPLAY.ID is 'ID'
/

comment on column  T_METHOD_REPLAY.PROJECT_NAME is '项目名称'
/

comment on column  T_METHOD_REPLAY.CLASS_NAME is '类名'
/

comment on column  T_METHOD_REPLAY.METHOD_NAME is '方法名'
/

comment on column  T_METHOD_REPLAY.PARAM_TYPE is '参数类型'
/

comment on column  T_METHOD_REPLAY.ARGS is '参数'
/

comment on column  T_METHOD_REPLAY.NOW_TIME is '当前时间'
/

comment on column  T_METHOD_REPLAY.ERROR_MSG is '错误信息'
/

comment on column  T_METHOD_REPLAY.ALREADY_REPLAY_TIMES is '已重放次数'
/

comment on column  T_METHOD_REPLAY.MAX_REPLAY_TIMES is '最大重放次数'
/

create index  IDX_T_METHOD_REPLAY_NOW_TIME
    on  T_METHOD_REPLAY (NOW_TIME)
    /

create trigger  TRG_T_METHOD_REPLAY
    before insert
    on  T_METHOD_REPLAY
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_METHOD_REPLAY.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_USER_ENTRUST
(
    ID NUMBER(20) not null
		primary key,
    SENDER NVARCHAR2(64) not null,
    RECEIVER_ID NVARCHAR2(64) not null,
    RECEIVER_NAME NVARCHAR2(255),
    POWER_ID NVARCHAR2(100) not null,
    BEGIN_TIME DATE,
    END_TIME DATE,
    CREATE_TIME DATE default SYSDATE,
    UPDATE_TIME DATE default SYSDATE,
    CREATE_USER NVARCHAR2(50) not null,
    UPDATE_USER NVARCHAR2(50) not null,
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    constraint UK_T_USER_ENTRUST_SENDER_RECEIVER_ID_POWER_ID
        unique (SENDER, RECEIVER_ID, POWER_ID)
)
    /

comment on table  T_USER_ENTRUST is '用户委托表'
/

comment on column  T_USER_ENTRUST.ID is 'ID'
/

comment on column  T_USER_ENTRUST.SENDER is '发送人ID'
/

comment on column  T_USER_ENTRUST.RECEIVER_ID is '接收人ID'
/

comment on column  T_USER_ENTRUST.RECEIVER_NAME is '接收人姓名'
/

comment on column  T_USER_ENTRUST.POWER_ID is '权限ID'
/

comment on column  T_USER_ENTRUST.BEGIN_TIME is '开始时间'
/

comment on column  T_USER_ENTRUST.END_TIME is '结束时间'
/

comment on column  T_USER_ENTRUST.CREATE_TIME is '创建时间'
/

comment on column  T_USER_ENTRUST.UPDATE_TIME is '更新时间'
/

comment on column  T_USER_ENTRUST.CREATE_USER is '创建人'
/

comment on column  T_USER_ENTRUST.UPDATE_USER is '更新人'
/

comment on column  T_USER_ENTRUST.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_USER_ENTRUST.TENANT_ID is '租户ID'
/

create index  IDX_T_USER_ENTRUST_SENDER_POWER_ID
    on  T_USER_ENTRUST (SENDER, POWER_ID)
    /

create trigger  TRG_T_USER_ENTRUST
    before insert
    on  T_USER_ENTRUST
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_USER_ENTRUST.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_USER_MESSAGE_STATUS
(
    ID NUMBER(20) not null
		primary key,
    USER_ID NVARCHAR2(64) not null,
    MESSAGE_STATUS NUMBER(1) default 0,
    MAIL_STATUS NUMBER(1) default 0,
    NOT_TROUBLE_TIME_END DATE,
    NOT_TROUBLE_TIME_BEGIN DATE,
    NOT_TROUBLE NUMBER(1) default 0,
    SHOCK NUMBER(1) default 0,
    SOUND NUMBER(1) default 0,
    OPEN_PHONE NUMBER(1) default 0,
    CREATE_TIME DATE default SYSDATE,
    UPDATE_TIME DATE default SYSDATE,
    CREATE_USER NVARCHAR2(50) not null,
    UPDATE_USER NVARCHAR2(50) not null,
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default ''
)
    /

comment on table  T_USER_MESSAGE_STATUS is '用户消息状态表'
/

comment on column  T_USER_MESSAGE_STATUS.ID is 'ID'
/

comment on column  T_USER_MESSAGE_STATUS.USER_ID is '用户ID'
/

comment on column  T_USER_MESSAGE_STATUS.MESSAGE_STATUS is '短信状态'
/

comment on column  T_USER_MESSAGE_STATUS.MAIL_STATUS is '邮件状态'
/

comment on column  T_USER_MESSAGE_STATUS.NOT_TROUBLE_TIME_END is '免打扰结束时间'
/

comment on column  T_USER_MESSAGE_STATUS.NOT_TROUBLE_TIME_BEGIN is '免打扰开始时间'
/

comment on column  T_USER_MESSAGE_STATUS.NOT_TROUBLE is '是否免打扰'
/

comment on column  T_USER_MESSAGE_STATUS.SHOCK is '是否震动'
/

comment on column  T_USER_MESSAGE_STATUS.SOUND is '是否静音'
/

comment on column  T_USER_MESSAGE_STATUS.OPEN_PHONE is '是否开启电话'
/

comment on column  T_USER_MESSAGE_STATUS.CREATE_TIME is '创建时间'
/

comment on column  T_USER_MESSAGE_STATUS.UPDATE_TIME is '更新时间'
/

comment on column  T_USER_MESSAGE_STATUS.CREATE_USER is '创建人'
/

comment on column  T_USER_MESSAGE_STATUS.UPDATE_USER is '更新人'
/

comment on column  T_USER_MESSAGE_STATUS.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_USER_MESSAGE_STATUS.TENANT_ID is '租户ID'
/

create index  IDX_T_USER_MESSAGE_STATUS_USER_ID
    on  T_USER_MESSAGE_STATUS (USER_ID)
    /

create trigger  TRG_T_USER_MESSAGE_STATUS
    before insert
    on  T_USER_MESSAGE_STATUS
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_USER_MESSAGE_STATUS.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_NODE_BUTTON_CONF
(
    ID NUMBER(20) not null
		primary key,
    BPMN_NODE_ID NUMBER(20) not null,
    BUTTON_PAGE_TYPE NUMBER(11) not null,
    BUTTON_TYPE NUMBER(11) not null,
    BUTTON_NAME NVARCHAR2(60) default '',
    REMARK NVARCHAR2(255) default '',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) default '',
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50) default '',
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_NODE_BUTTON_CONF is '节点按钮配置表'
/

comment on column  T_BPMN_NODE_BUTTON_CONF.ID is 'ID'
/

comment on column  T_BPMN_NODE_BUTTON_CONF.BPMN_NODE_ID is '节点ID'
/

comment on column  T_BPMN_NODE_BUTTON_CONF.BUTTON_PAGE_TYPE is '按钮页面类型 1:提交页面;2:审批页面'
/

comment on column  T_BPMN_NODE_BUTTON_CONF.BUTTON_TYPE is '按钮类型'
/

comment on column  T_BPMN_NODE_BUTTON_CONF.BUTTON_NAME is '按钮名称'
/

comment on column  T_BPMN_NODE_BUTTON_CONF.REMARK is '备注'
/

comment on column  T_BPMN_NODE_BUTTON_CONF.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  T_BPMN_NODE_BUTTON_CONF.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_BUTTON_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_BUTTON_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_BUTTON_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_BUTTON_CONF.UPDATE_TIME is '更新时间'
/

create index  IDX_T_BPMN_NODE_BUTTON_CONF_BPMN_NODE_ID
    on  T_BPMN_NODE_BUTTON_CONF (BPMN_NODE_ID)
    /

create trigger  TRG_T_BPMN_NODE_BUTTON_CONF
    before insert
    on  T_BPMN_NODE_BUTTON_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_NODE_BUTTON_CONF.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_BUSINESS_PROCESS
(
    ID NUMBER(20) not null
		primary key,
    PROCESSINESS_KEY NVARCHAR2(64),
    BUSINESS_ID NVARCHAR2(64) not null,
    BUSINESS_NUMBER NVARCHAR2(64),
    ENTRY_ID NVARCHAR2(64),
    VERSION NVARCHAR2(30),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_TIME DATE default SYSDATE,
    DESCRIPTION NVARCHAR2(100),
    PROCESS_STATE NUMBER(1),
    CREATE_USER NVARCHAR2(64),
    PROCESS_DIGEST NCLOB,
    IS_DEL NUMBER(1) default 0,
    TENANT_ID NVARCHAR2(64) default '',
    DATA_SOURCE_ID NUMBER(20),
    PROC_INST_ID_ NVARCHAR2(64) default '',
    BACK_USER_ID NVARCHAR2(64),
    USER_NAME NVARCHAR2(255),
    IS_OUT_SIDE_PROCESS NUMBER(1) default 0,
    IS_LOWCODE_FLOW NUMBER(1) default 0
)
    /

comment on table  BPM_BUSINESS_PROCESS is '业务流程表'
/

comment on column  BPM_BUSINESS_PROCESS.ID is 'ID'
/

comment on column  BPM_BUSINESS_PROCESS.PROCESSINESS_KEY is '流程KEY'
/

comment on column  BPM_BUSINESS_PROCESS.BUSINESS_ID is '业务ID'
/

comment on column  BPM_BUSINESS_PROCESS.BUSINESS_NUMBER is '业务编号'
/

comment on column  BPM_BUSINESS_PROCESS.ENTRY_ID is '入口ID'
/

comment on column  BPM_BUSINESS_PROCESS.VERSION is '版本'
/

comment on column  BPM_BUSINESS_PROCESS.CREATE_TIME is '创建时间'
/

comment on column  BPM_BUSINESS_PROCESS.UPDATE_TIME is '更新时间'
/

comment on column  BPM_BUSINESS_PROCESS.DESCRIPTION is '描述'
/

comment on column  BPM_BUSINESS_PROCESS.PROCESS_STATE is '流程状态 1:审批中 2:已审批 3:无效 6:已拒绝'
/

comment on column  BPM_BUSINESS_PROCESS.CREATE_USER is '创建人'
/

comment on column  BPM_BUSINESS_PROCESS.PROCESS_DIGEST is '流程摘要'
/

comment on column  BPM_BUSINESS_PROCESS.IS_DEL is '是否删除 0:否,1:是'
/

comment on column  BPM_BUSINESS_PROCESS.TENANT_ID is '租户ID'
/

comment on column  BPM_BUSINESS_PROCESS.DATA_SOURCE_ID is '数据源ID'
/

comment on column  BPM_BUSINESS_PROCESS.PROC_INST_ID_ is '流程实例ID'
/

comment on column  BPM_BUSINESS_PROCESS.BACK_USER_ID is '退回用户ID'
/

comment on column  BPM_BUSINESS_PROCESS.USER_NAME is '用户名'
/

comment on column  BPM_BUSINESS_PROCESS.IS_OUT_SIDE_PROCESS is '是否外部流程 0:否,1:是'
/

comment on column  BPM_BUSINESS_PROCESS.IS_LOWCODE_FLOW is '是否低代码流程 0:否,1:是'
/

create index  IDX_BPM_BUSINESS_PROCESS_PROC_INST_ID_
    on  BPM_BUSINESS_PROCESS (PROC_INST_ID_)
    /

create index  IDX_BPM_BUSINESS_PROCESS_ENTRY_ID
    on  BPM_BUSINESS_PROCESS (ENTRY_ID)
    /

create index  IDX_BPM_BUSINESS_PROCESS_PROCESSINESS_KEY
    on  BPM_BUSINESS_PROCESS (PROCESSINESS_KEY)
    /

create index  IDX_BPM_BUSINESS_PROCESS_BUSINESS_NUMBER
    on  BPM_BUSINESS_PROCESS (BUSINESS_NUMBER)
    /

create index  IDX_BPM_BUSINESS_PROCESS_PROCESS_STATE
    on  BPM_BUSINESS_PROCESS (PROCESS_STATE)
    /

create trigger  TRG_BPM_BUSINESS_PROCESS
    before insert
    on  BPM_BUSINESS_PROCESS
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_BUSINESS_PROCESS.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_NODE_PERSONNEL_CONF
(
    ID NUMBER(10) not null
		constraint PK_T_BPMN_NODE_PERSONNEL_CONF
			primary key,
    BPMN_NODE_ID NUMBER(10) not null,
    SIGN_TYPE NUMBER(3),
    REMARK NVARCHAR2(100),
    IS_DEL NUMBER(1),
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50),
    CREATE_TIME DATE default SYSDATE not null,
    UPDATE_USER NVARCHAR2(50),
    UPDATE_TIME DATE
)
    /

comment on table  T_BPMN_NODE_PERSONNEL_CONF is 'node person conf table'
/

comment on column  T_BPMN_NODE_PERSONNEL_CONF.ID is 'd'
/

comment on column  T_BPMN_NODE_PERSONNEL_CONF.BPMN_NODE_ID is '节点id'
/

comment on column  T_BPMN_NODE_PERSONNEL_CONF.SIGN_TYPE is 'sign type 1: all sign 2:or sign'
/

comment on column  T_BPMN_NODE_PERSONNEL_CONF.REMARK is 'remark'
/

comment on column  T_BPMN_NODE_PERSONNEL_CONF.IS_DEL is '0:no,1:yes'
/

comment on column  T_BPMN_NODE_PERSONNEL_CONF.TENANT_ID is 'tenantId'
/

comment on column  T_BPMN_NODE_PERSONNEL_CONF.CREATE_USER is 'as its name says'
/

comment on column  T_BPMN_NODE_PERSONNEL_CONF.CREATE_TIME is 'as its name says'
/

comment on column  T_BPMN_NODE_PERSONNEL_CONF.UPDATE_USER is 'as its name says'
/

comment on column  T_BPMN_NODE_PERSONNEL_CONF.UPDATE_TIME is 'as its name says'
/

create trigger  TRG_T_BPMN_NODE_PERSONNEL_CONF_ID
    before insert
    on  T_BPMN_NODE_PERSONNEL_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_NODE_PERSONNEL_CONF.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;

END;
/

create table  T_BPMN_NODE_PERSONNEL_EMPL_CONF
(
    ID NUMBER(10) not null
		constraint PK_T_BPMN_NODE_PERSONNEL_EMPL_CONF
			primary key,
    BPMN_NODE_PERSONNE_ID NUMBER(10) not null,
    EMPL_ID NVARCHAR2(50) not null,
    EMPL_NAME NVARCHAR2(50),
    REMARK NVARCHAR2(100),
    IS_DEL NUMBER(1),
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(30),
    CREATE_TIME DATE default SYSDATE not null,
    UPDATE_USER NVARCHAR2(30),
    UPDATE_TIME DATE
)
    /

comment on table  T_BPMN_NODE_PERSONNEL_EMPL_CONF is '节点审批人（员工）配置表'
/

comment on column  T_BPMN_NODE_PERSONNEL_EMPL_CONF.ID is '主键ID'
/

comment on column  T_BPMN_NODE_PERSONNEL_EMPL_CONF.BPMN_NODE_PERSONNE_ID is '人员配置ID'
/

comment on column  T_BPMN_NODE_PERSONNEL_EMPL_CONF.EMPL_ID is '审批人ID'
/

comment on column  T_BPMN_NODE_PERSONNEL_EMPL_CONF.EMPL_NAME is '审批人姓名'
/

comment on column  T_BPMN_NODE_PERSONNEL_EMPL_CONF.REMARK is '备注'
/

comment on column  T_BPMN_NODE_PERSONNEL_EMPL_CONF.IS_DEL is '是否删除：0-否，1-是'
/

comment on column  T_BPMN_NODE_PERSONNEL_EMPL_CONF.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_PERSONNEL_EMPL_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_PERSONNEL_EMPL_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_PERSONNEL_EMPL_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_PERSONNEL_EMPL_CONF.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_NODE_PERS_EMPL_CONF_ID
    before insert
    on  T_BPMN_NODE_PERSONNEL_EMPL_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_NODE_PERSONNEL_EMPL_CONF.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_PROCESS_PERMISSIONS
(
    ID NUMBER(19) not null
		constraint PK_BPM_PROCESS_PERMISSIONS
			primary key,
    USER_ID NVARCHAR2(64),
    DEP_ID NUMBER(19),
    PERMISSIONS_TYPE NUMBER(10),
    CREATE_USER NVARCHAR2(64),
    CREATE_TIME DATE default SYSDATE not null,
    PROCESS_KEY NVARCHAR2(50),
    OFFICE_ID NUMBER(19),
    IS_DEL NUMBER(10) default 0 not null,
    TENANT_ID NVARCHAR2(64) default ''
)
    /

comment on table  BPM_PROCESS_PERMISSIONS is '流程权限表'
/

comment on column  BPM_PROCESS_PERMISSIONS.ID is '主键'
/

comment on column  BPM_PROCESS_PERMISSIONS.USER_ID is '用户ID'
/

comment on column  BPM_PROCESS_PERMISSIONS.DEP_ID is '部门ID'
/

comment on column  BPM_PROCESS_PERMISSIONS.PERMISSIONS_TYPE is '权限类型（1-查看，2-发起，3-监控）'
/

comment on column  BPM_PROCESS_PERMISSIONS.CREATE_USER is '创建人ID'
/

comment on column  BPM_PROCESS_PERMISSIONS.CREATE_TIME is '创建时间'
/

comment on column  BPM_PROCESS_PERMISSIONS.PROCESS_KEY is '流程标识'
/

comment on column  BPM_PROCESS_PERMISSIONS.OFFICE_ID is '机构ID'
/

comment on column  BPM_PROCESS_PERMISSIONS.IS_DEL is '删除状态：0-正常，1-删除'
/

comment on column  BPM_PROCESS_PERMISSIONS.TENANT_ID is '租户ID'
/

create trigger  TRG_BPM_PROCESS_PERMISSIONS_ID
    before insert
    on  BPM_PROCESS_PERMISSIONS
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_PROCESS_PERMISSIONS.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_DICT_MAIN
(
    ID NUMBER(19) not null
		constraint PK_T_DICT_MAIN
			primary key,
    DICT_NAME NVARCHAR2(100) default '',
    DICT_TYPE NVARCHAR2(100) default ''
		constraint UK_T_DICT_MAIN_DICT_TYPE
			unique,
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(255),
    CREATE_TIME DATE default SYSDATE not null,
    UPDATE_USER NVARCHAR2(255),
    UPDATE_TIME DATE default SYSDATE not null,
    REMARK NVARCHAR2(500)
)
    /

comment on table  T_DICT_MAIN is '字典类型表,仅作展示之用,用户可以替换为自己的字段表,能查出需要的内容就行了'
/

comment on column  T_DICT_MAIN.ID is '字典主键'
/

comment on column  T_DICT_MAIN.DICT_NAME is '字典名称'
/

comment on column  T_DICT_MAIN.DICT_TYPE is '字典类型'
/

comment on column  T_DICT_MAIN.IS_DEL is '删除状态'
/

comment on column  T_DICT_MAIN.TENANT_ID is '租户ID'
/

comment on column  T_DICT_MAIN.CREATE_USER is '创建人'
/

comment on column  T_DICT_MAIN.CREATE_TIME is '创建时间'
/

comment on column  T_DICT_MAIN.UPDATE_USER is '更新人'
/

comment on column  T_DICT_MAIN.UPDATE_TIME is '更新时间'
/

comment on column  T_DICT_MAIN.REMARK is '备注'
/

create trigger  TRG_T_DICT_MAIN_ID
    before insert
    on  T_DICT_MAIN
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_DICT_MAIN.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_DICT_DATA
(
    ID NUMBER(19) not null
		constraint PK_T_DICT_DATA
			primary key,
    DICT_SORT NUMBER(10) default 0,
    DICT_LABEL NVARCHAR2(100) default '',
    DICT_VALUE NVARCHAR2(100) default '',
    DICT_TYPE NVARCHAR2(100) default '',
    CSS_CLASS NVARCHAR2(100),
    LIST_CLASS NVARCHAR2(100),
    IS_DEFAULT NCHAR(1) default 'N',
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(255),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(255),
    UPDATE_TIME DATE default SYSDATE,
    REMARK NVARCHAR2(500)
)
    /

comment on table  T_DICT_DATA is '字典表子表,用于存储字典值,一般现有系统都有自己的字典表,可以替换掉,给出sql能查出需要的数据就可以了'
/

comment on column  T_DICT_DATA.ID is '字典编码'
/

comment on column  T_DICT_DATA.DICT_SORT is '字典排序'
/

comment on column  T_DICT_DATA.DICT_LABEL is '字典标签'
/

comment on column  T_DICT_DATA.DICT_VALUE is '字典键值'
/

comment on column  T_DICT_DATA.DICT_TYPE is '字典类型'
/

comment on column  T_DICT_DATA.CSS_CLASS is '样式属性（其他样式扩展）'
/

comment on column  T_DICT_DATA.LIST_CLASS is '表格回显样式'
/

comment on column  T_DICT_DATA.IS_DEFAULT is '是否默认（Y是 N否）'
/

comment on column  T_DICT_DATA.IS_DEL is '删除状态'
/

comment on column  T_DICT_DATA.TENANT_ID is '租户ID'
/

comment on column  T_DICT_DATA.CREATE_USER is '创建人'
/

comment on column  T_DICT_DATA.CREATE_TIME is '创建时间'
/

comment on column  T_DICT_DATA.UPDATE_USER is '更新人'
/

comment on column  T_DICT_DATA.UPDATE_TIME is '更新时间'
/

comment on column  T_DICT_DATA.REMARK is '备注'
/

create trigger  TRG_T_DICT_DATA_ID
    before insert
    on  T_DICT_DATA
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_DICT_DATA.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF
(
    ID NUMBER(10) not null
		constraint PK_T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF
			primary key,
    NODE_ID NUMBER(19),
    EMPL_ID NVARCHAR2(64),
    EMPL_NAME NVARCHAR2(50),
    CREATE_USER NVARCHAR2(50),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(255),
    UPDATE_TIME DATE default SYSDATE,
    IS_DEL NUMBER(1) default 0 not null,
    TENANT_ID NVARCHAR2(64) default ''
)
    /

comment on table  T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF is '外部业务方指定角色的审批人信息'
/

comment on column  T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF.ID is '自增主键ID'
/

comment on column  T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF.NODE_ID is '关联 t_bpmn_node_role_conf 的外键'
/

comment on column  T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF.EMPL_ID is '审批人ID'
/

comment on column  T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF.EMPL_NAME is '审批人姓名'
/

comment on column  T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF.UPDATE_TIME is '更新时间'
/

comment on column  T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF.IS_DEL is '状态：0-正常，1-已删除'
/

comment on column  T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF.TENANT_ID is '租户ID'
/

create trigger  TRG_T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF_ID
    before insert
    on  T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF
    for each row
BEGIN
    IF
    :NEW.ID IS NULL THEN
    SELECT SEQ_T_BPMN_NODE_ROLE_OUTSIDE_EMP_CONF.NEXTVAL
    INTO :NEW.ID
    FROM DUAL;
END IF;
END;
/

create table  BPM_PROCESS_APP_APPLICATION
(
    ID NUMBER(10) not null
		constraint PK_BPM_PROCESS_APP_APPLICATION
			primary key,
    BUSINESS_CODE NVARCHAR2(50),
    PROCESS_NAME NVARCHAR2(50),
    APPLY_TYPE NUMBER(10),
    PERMISSIONS_CODE NVARCHAR2(50),
    PC_ICON NVARCHAR2(500),
    EFFECTIVE_SOURCE NVARCHAR2(500),
    IS_SON NUMBER(10),
    LOOK_URL NVARCHAR2(500),
    SUBMIT_URL NVARCHAR2(500),
    CONDITION_URL NVARCHAR2(500),
    PARENT_ID NUMBER(10),
    APPLICATION_URL NVARCHAR2(500),
    USER_REQUEST_URI NVARCHAR2(500),
    ROLE_REQUEST_URI NVARCHAR2(500),
    ROUTE NVARCHAR2(500),
    PROCESS_KEY NVARCHAR2(50),
    CREATE_TIME DATE default SYSDATE not null,
    UPDATE_TIME DATE default SYSDATE,
    IS_DEL NUMBER(1) default 0 not null,
    CREATE_USER_ID NVARCHAR2(64),
    UPDATE_USER NVARCHAR2(255),
    IS_ALL NUMBER(1) default 0,
    STATE NUMBER(1) default 1,
    SORT NUMBER(10),
    SOURCE NVARCHAR2(255)
)
    /

comment on table  BPM_PROCESS_APP_APPLICATION is 'BPM流程应用表'
/

comment on column  BPM_PROCESS_APP_APPLICATION.ID is '主键'
/

comment on column  BPM_PROCESS_APP_APPLICATION.BUSINESS_CODE is '业务编码，主应用通常为空'
/

comment on column  BPM_PROCESS_APP_APPLICATION.PROCESS_NAME is '应用名称'
/

comment on column  BPM_PROCESS_APP_APPLICATION.APPLY_TYPE is '应用类型（1:流程, 2:应用, 3:父级应用）'
/

comment on column  BPM_PROCESS_APP_APPLICATION.PERMISSIONS_CODE is '权限编码'
/

comment on column  BPM_PROCESS_APP_APPLICATION.PC_ICON is 'PC端图标URL或路径'
/

comment on column  BPM_PROCESS_APP_APPLICATION.EFFECTIVE_SOURCE is '移动端图标URL或标识'
/

comment on column  BPM_PROCESS_APP_APPLICATION.IS_SON is '是否子应用（0:否, 1:是）'
/

comment on column  BPM_PROCESS_APP_APPLICATION.LOOK_URL is '查看应用URL'
/

comment on column  BPM_PROCESS_APP_APPLICATION.SUBMIT_URL is '提交应用URL'
/

comment on column  BPM_PROCESS_APP_APPLICATION.CONDITION_URL is '条件/规则配置URL'
/

comment on column  BPM_PROCESS_APP_APPLICATION.PARENT_ID is '父级应用ID'
/

comment on column  BPM_PROCESS_APP_APPLICATION.APPLICATION_URL is '应用主URL'
/

comment on column  BPM_PROCESS_APP_APPLICATION.USER_REQUEST_URI is '获取用户信息接口'
/

comment on column  BPM_PROCESS_APP_APPLICATION.ROLE_REQUEST_URI is '获取角色信息接口'
/

comment on column  BPM_PROCESS_APP_APPLICATION.ROUTE is '应用路由路径'
/

comment on column  BPM_PROCESS_APP_APPLICATION.PROCESS_KEY is '流程标识'
/

comment on column  BPM_PROCESS_APP_APPLICATION.CREATE_TIME is '创建时间'
/

comment on column  BPM_PROCESS_APP_APPLICATION.UPDATE_TIME is '更新时间'
/

comment on column  BPM_PROCESS_APP_APPLICATION.IS_DEL is '删除状态：0-正常，1-删除'
/

comment on column  BPM_PROCESS_APP_APPLICATION.CREATE_USER_ID is '创建人ID'
/

comment on column  BPM_PROCESS_APP_APPLICATION.UPDATE_USER is '更新人'
/

comment on column  BPM_PROCESS_APP_APPLICATION.IS_ALL is '是否全局可见（0:否, 1:是）'
/

comment on column  BPM_PROCESS_APP_APPLICATION.STATE is '状态（0:禁用, 1:启用）'
/

comment on column  BPM_PROCESS_APP_APPLICATION.SORT is '排序序号'
/

comment on column  BPM_PROCESS_APP_APPLICATION.SOURCE is '来源标识'
/

create trigger  TRG_BPM_PROCESS_APP_APPLICATION_ID
    before insert
    on  BPM_PROCESS_APP_APPLICATION
    for each row
BEGIN
    IF
    :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_PROCESS_APP_APPLICATION.NEXTVAL
    INTO :NEW.ID
    FROM DUAL;
END IF;
END;
/


create table  BPM_PROCESS_NAME_RELEVANCY
(
    ID NUMBER(19) not null
		constraint PK_BPM_PROCESS_NAME_RELEVANCY
			primary key,
    PROCESS_NAME_ID NUMBER(19),
    PROCESS_KEY NVARCHAR2(50),
    IS_DEL NUMBER(10) default 0,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_TIME DATE default SYSDATE not null
)
    /

comment on table  BPM_PROCESS_NAME_RELEVANCY is '流程高级搜索关联表'
/

comment on column  BPM_PROCESS_NAME_RELEVANCY.ID is '主键ID'
/

comment on column  BPM_PROCESS_NAME_RELEVANCY.PROCESS_NAME_ID is '流程名称ID'
/

comment on column  BPM_PROCESS_NAME_RELEVANCY.PROCESS_KEY is '流程定义Key'
/

comment on column  BPM_PROCESS_NAME_RELEVANCY.IS_DEL is '删除状态：0-正常，1-删除'
/

comment on column  BPM_PROCESS_NAME_RELEVANCY.TENANT_ID is '租户ID'
/

comment on column  BPM_PROCESS_NAME_RELEVANCY.CREATE_TIME is '创建时间'
/

create index  PROCESS_KEY_INDEX
    on  BPM_PROCESS_NAME_RELEVANCY (PROCESS_KEY)
    /

create index  PROCESS_NAME_ID_INDEX
    on  BPM_PROCESS_NAME_RELEVANCY (PROCESS_NAME_ID)
    /

create trigger  TRG_BPM_PROCESS_NAME_RELEVANCY_ID
    before insert
    on  BPM_PROCESS_NAME_RELEVANCY
    for each row
BEGIN
    IF
    :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_BPM_PROCESS_NAME_RELEVANCY.NEXTVAL;
END IF;
END;
/

create table  BPM_PROCESS_NODE_BACK
(
    ID NUMBER(19) not null
		constraint PK_BPM_PROCESS_NODE_BACK
			primary key,
    NODE_KEY NVARCHAR2(50),
    NODE_ID NUMBER(19),
    BACK_TYPE NUMBER(10),
    PROCESS_KEY NVARCHAR2(100),
    IS_DEL NUMBER(3),
    TENANT_ID NVARCHAR2(64) default ''
)
    /

comment on table  BPM_PROCESS_NODE_BACK is '流程节点回退配置表'
/

comment on column  BPM_PROCESS_NODE_BACK.ID is '主键ID'
/

comment on column  BPM_PROCESS_NODE_BACK.NODE_KEY is '节点Key'
/

comment on column  BPM_PROCESS_NODE_BACK.NODE_ID is '节点ID'
/

comment on column  BPM_PROCESS_NODE_BACK.BACK_TYPE is '回退类型'
/

comment on column  BPM_PROCESS_NODE_BACK.PROCESS_KEY is '流程定义Key'
/

comment on column  BPM_PROCESS_NODE_BACK.IS_DEL is '是否删除：0-否，1-是'
/

comment on column  BPM_PROCESS_NODE_BACK.TENANT_ID is '租户ID'
/

create index  IDX_BPM_PROC_NODE_BACK_PKEY
    on  BPM_PROCESS_NODE_BACK (PROCESS_KEY)
    /

create index  IDX_BPM_PROC_NODE_BACK_NKEY
    on  BPM_PROCESS_NODE_BACK (NODE_KEY)
    /

create trigger  TRG_BPM_PROCESS_NODE_BACK_ID
    before insert
    on  BPM_PROCESS_NODE_BACK
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_BPM_PROCESS_NODE_BACK.NEXTVAL;
END IF;
END;
/

create table  BPM_PROCESS_OPERATION
(
    ID NUMBER(19) not null
		constraint PK_BPM_PROCESS_OPERATION
			primary key,
    PROCESS_KEY NVARCHAR2(50),
    PROCESS_NODE NVARCHAR2(50),
    TYPE NUMBER(2),
    IS_DEL NUMBER(3),
    TENANT_ID NVARCHAR2(64) default ''
)
    /

comment on table  BPM_PROCESS_OPERATION is '流程操作配置表'
/

comment on column  BPM_PROCESS_OPERATION.ID is '主键ID'
/

comment on column  BPM_PROCESS_OPERATION.PROCESS_KEY is '流程定义Key'
/

comment on column  BPM_PROCESS_OPERATION.PROCESS_NODE is '流程节点ID'
/

comment on column  BPM_PROCESS_OPERATION.TYPE is '操作类型：1-批量提交，2-委托'
/

comment on column  BPM_PROCESS_OPERATION.IS_DEL is '是否删除：0-否，1-是'
/

comment on column  BPM_PROCESS_OPERATION.TENANT_ID is '租户ID'
/

create index  IDX_BPM_PROC_OP_PKEY
    on  BPM_PROCESS_OPERATION (PROCESS_KEY)
    /

create index  IDX_BPM_PROC_OP_PNODE
    on  BPM_PROCESS_OPERATION (PROCESS_NODE)
    /

create trigger  TRG_BPM_PROCESS_OPERATION_ID
    before insert
    on  BPM_PROCESS_OPERATION
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_BPM_PROCESS_OPERATION.NEXTVAL;
END IF;
END;
/

create table  T_BIZ_ACCOUNT_APPLY
(
    ID NUMBER(10) not null
		constraint PK_T_BIZ_ACCOUNT_APPLY
			primary key,
    ACCOUNT_TYPE NUMBER(3),
    ACCOUNT_OWNER_NAME NVARCHAR2(50),
    REMARK NVARCHAR2(200),
    IS_DEL NUMBER(10) default 0,
    TENANT_ID NVARCHAR2(64) default ''
)
    /

comment on table  T_BIZ_ACCOUNT_APPLY is '第三方账号申请示例表'
/

comment on column  T_BIZ_ACCOUNT_APPLY.ID is '主键ID'
/

comment on column  T_BIZ_ACCOUNT_APPLY.ACCOUNT_TYPE is '账号类型'
/

comment on column  T_BIZ_ACCOUNT_APPLY.ACCOUNT_OWNER_NAME is '账号持有人姓名'
/

comment on column  T_BIZ_ACCOUNT_APPLY.REMARK is '备注'
/

comment on column  T_BIZ_ACCOUNT_APPLY.IS_DEL is '删除状态：0-正常，1-已删除'
/

comment on column  T_BIZ_ACCOUNT_APPLY.TENANT_ID is '租户ID'
/

create trigger  TRG_T_BIZ_ACCOUNT_APPLY_ID
    before insert
    on  T_BIZ_ACCOUNT_APPLY
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BIZ_ACCOUNT_APPLY.NEXTVAL;
END IF;
END;
/

create table  T_BIZ_PURCHASE
(
    ID NUMBER(10) not null
		constraint PK_T_BIZ_PURCHASE
			primary key,
    PURCHASE_USER_ID NUMBER(10) not null,
    PURCHASE_USER_NAME NVARCHAR2(255) not null,
    PURCHASE_TYPE NUMBER(10) default 1 not null,
    PURCHASE_TIME DATE default SYSDATE,
    PLAN_PROCUREMENT_TOTAL_MONEY BINARY_DOUBLE default 0 not null,
    REMARK NVARCHAR2(255),
    CREATE_USER NVARCHAR2(255) not null,
    CREATE_TIME DATE default SYSDATE not null,
    UPDATE_USER NVARCHAR2(255),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BIZ_PURCHASE is '采购业务表'
/

comment on column  T_BIZ_PURCHASE.ID is '主键ID'
/

comment on column  T_BIZ_PURCHASE.PURCHASE_USER_ID is '采购人ID'
/

comment on column  T_BIZ_PURCHASE.PURCHASE_USER_NAME is '采购人姓名'
/

comment on column  T_BIZ_PURCHASE.PURCHASE_TYPE is '采购类型，默认1'
/

comment on column  T_BIZ_PURCHASE.PURCHASE_TIME is '采购时间'
/

comment on column  T_BIZ_PURCHASE.PLAN_PROCUREMENT_TOTAL_MONEY is '计划采购总金额'
/

comment on column  T_BIZ_PURCHASE.REMARK is '备注'
/

comment on column  T_BIZ_PURCHASE.CREATE_USER is '创建人'
/

comment on column  T_BIZ_PURCHASE.CREATE_TIME is '创建时间'
/

comment on column  T_BIZ_PURCHASE.UPDATE_USER is '更新人'
/

comment on column  T_BIZ_PURCHASE.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BIZ_PURCHASE_ID
    before insert
    on  T_BIZ_PURCHASE
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BIZ_PURCHASE.NEXTVAL;
END IF;
END;
/

create table  T_BIZ_REFUND
(
    ID NUMBER(10) not null
		constraint PK_T_BIZ_REFUND
			primary key,
    REFUND_USER_ID NUMBER(10) not null,
    REFUND_USER_NAME NVARCHAR2(255) not null,
    REFUND_TYPE NUMBER(10) default 1 not null,
    REFUND_DATE DATE not null,
    REFUND_MONEY BINARY_DOUBLE default 0 not null,
    REMARK NVARCHAR2(255),
    CREATE_USER NVARCHAR2(255) not null,
    CREATE_TIME DATE default SYSDATE not null,
    UPDATE_USER NVARCHAR2(255),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BIZ_REFUND is '退款业务表'
/

comment on column  T_BIZ_REFUND.ID is '主键ID'
/

comment on column  T_BIZ_REFUND.REFUND_USER_ID is '退款人ID'
/

comment on column  T_BIZ_REFUND.REFUND_USER_NAME is '退款人姓名'
/

comment on column  T_BIZ_REFUND.REFUND_TYPE is '退款类型，默认1'
/

comment on column  T_BIZ_REFUND.REFUND_DATE is '退款日期'
/

comment on column  T_BIZ_REFUND.REFUND_MONEY is '退款金额'
/

comment on column  T_BIZ_REFUND.REMARK is '备注'
/

comment on column  T_BIZ_REFUND.CREATE_USER is '创建人'
/

comment on column  T_BIZ_REFUND.CREATE_TIME is '创建时间'
/

comment on column  T_BIZ_REFUND.UPDATE_USER is '更新人'
/

comment on column  T_BIZ_REFUND.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BIZ_REFUND_ID
    before insert
    on  T_BIZ_REFUND
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BIZ_REFUND.NEXTVAL;
END IF;
END;
/

create table  T_BIZ_UCAR_REFUEL
(
    ID NUMBER(10) not null
		constraint PK_T_BIZ_UCAR_REFUEL
			primary key,
    LICENSE_PLATE_NUMBER NVARCHAR2(32),
    REFUEL_TIME DATE,
    REMARK NVARCHAR2(255),
    CREATE_USER NVARCHAR2(50),
    CREATE_TIME DATE,
    UPDATE_USER NVARCHAR2(50),
    UPDATE_TIME DATE
)
    /

comment on table  T_BIZ_UCAR_REFUEL is '加油表'
/

comment on column  T_BIZ_UCAR_REFUEL.ID is '主键ID'
/

comment on column  T_BIZ_UCAR_REFUEL.LICENSE_PLATE_NUMBER is '车牌号'
/

comment on column  T_BIZ_UCAR_REFUEL.REFUEL_TIME is '加油日期'
/

comment on column  T_BIZ_UCAR_REFUEL.REMARK is '备注'
/

comment on column  T_BIZ_UCAR_REFUEL.CREATE_USER is '创建人'
/

comment on column  T_BIZ_UCAR_REFUEL.CREATE_TIME is '创建日期'
/

comment on column  T_BIZ_UCAR_REFUEL.UPDATE_USER is '更新人'
/

comment on column  T_BIZ_UCAR_REFUEL.UPDATE_TIME is '更新日期'
/

create trigger  TRG_T_BIZ_UCAR_REFUEL_ID
    before insert
    on  T_BIZ_UCAR_REFUEL
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BIZ_UCAR_REFUEL.NEXTVAL;
END IF;
END;
/

create table  T_BPM_DYNAMIC_CONDITION_CHOSEN
(
    ID NUMBER(19) not null
		constraint T_BPM_DYNAMIC_CONDITION_CHOSEN_PK
			primary key,
    PROCESS_NUMBER NVARCHAR2(255),
    NODE_ID NVARCHAR2(100),
    NODE_FROM NVARCHAR2(100),
    IS_DEL NUMBER(3) default 0 not null,
    TENANT_ID NVARCHAR2(64) default ''
)
    /

comment on table  T_BPM_DYNAMIC_CONDITION_CHOSEN is '流程动态条件选择条件记录表'
/

comment on column  T_BPM_DYNAMIC_CONDITION_CHOSEN.ID is '主键ID'
/

comment on column  T_BPM_DYNAMIC_CONDITION_CHOSEN.PROCESS_NUMBER is '流程编号'
/

comment on column  T_BPM_DYNAMIC_CONDITION_CHOSEN.NODE_ID is '被选中条件节点的ID'
/

comment on column  T_BPM_DYNAMIC_CONDITION_CHOSEN.NODE_FROM is '来源节点ID'
/

comment on column  T_BPM_DYNAMIC_CONDITION_CHOSEN.IS_DEL is '删除状态：0-正常，1-删除'
/

comment on column  T_BPM_DYNAMIC_CONDITION_CHOSEN.TENANT_ID is '租户ID'
/

create index  INDX_PROCESS_NUMBER
    on  T_BPM_DYNAMIC_CONDITION_CHOSEN (PROCESS_NUMBER)
    /

create trigger  TRG_T_BPM_DYNAMIC_CONDITION_CHOSEN_ID
    before insert
    on  T_BPM_DYNAMIC_CONDITION_CHOSEN
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BPM_DYNAMIC_CONDITION_CHOSEN.NEXTVAL;
END IF;
END;
/

create table  T_BPMN_CONF_LF_FORMDATA
(
    ID NUMBER(19) not null
		constraint T_BPMN_CONF_LF_FORMDATA_PK
			primary key,
    BPMN_CONF_ID NUMBER(19) not null,
    FORMDATA CLOB,
    IS_DEL NUMBER(3) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(255),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(255),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_CONF_LF_FORMDATA is 'BPMN配置关联的表单数据表'
/

comment on column  T_BPMN_CONF_LF_FORMDATA.ID is '主键ID'
/

comment on column  T_BPMN_CONF_LF_FORMDATA.BPMN_CONF_ID is 'BPMN配置ID'
/

comment on column  T_BPMN_CONF_LF_FORMDATA.FORMDATA is '表单数据（JSON或XML等）'
/

comment on column  T_BPMN_CONF_LF_FORMDATA.IS_DEL is '删除状态：0-正常，1-删除'
/

comment on column  T_BPMN_CONF_LF_FORMDATA.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_CONF_LF_FORMDATA.CREATE_USER is '创建人'
/

comment on column  T_BPMN_CONF_LF_FORMDATA.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_CONF_LF_FORMDATA.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_CONF_LF_FORMDATA.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_CONF_LF_FORMDATA_ID
    before insert
    on  T_BPMN_CONF_LF_FORMDATA
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BPMN_CONF_LF_FORMDATA.NEXTVAL;
END IF;
END;
/

create table  T_BPMN_CONF_LF_FORMDATA_FIELD
(
    ID NUMBER(19) not null
		constraint T_BPMN_CONF_LF_FORMDATA_FIELD_PK
			primary key,
    BPMN_CONF_ID NUMBER(19),
    FORMDATA_ID NUMBER(19),
    FIELD_ID NVARCHAR2(255),
    FIELD_NAME NVARCHAR2(255),
    FIELD_TYPE NUMBER(3),
    IS_CONDITION NUMBER(3) default 0,
    IS_DEL NUMBER(3) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(255),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(255),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_CONF_LF_FORMDATA_FIELD is '低代码配置字段明细表'
/

comment on column  T_BPMN_CONF_LF_FORMDATA_FIELD.ID is '主键ID'
/

comment on column  T_BPMN_CONF_LF_FORMDATA_FIELD.BPMN_CONF_ID is 'BPMN配置ID'
/

comment on column  T_BPMN_CONF_LF_FORMDATA_FIELD.FORMDATA_ID is '表单数据ID'
/

comment on column  T_BPMN_CONF_LF_FORMDATA_FIELD.FIELD_ID is '字段标识'
/

comment on column  T_BPMN_CONF_LF_FORMDATA_FIELD.FIELD_NAME is '字段名称'
/

comment on column  T_BPMN_CONF_LF_FORMDATA_FIELD.FIELD_TYPE is '字段类型'
/

comment on column  T_BPMN_CONF_LF_FORMDATA_FIELD.IS_CONDITION is '是否是流程条件：0-否，1-是'
/

comment on column  T_BPMN_CONF_LF_FORMDATA_FIELD.IS_DEL is '删除状态：0-正常，1-删除'
/

comment on column  T_BPMN_CONF_LF_FORMDATA_FIELD.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_CONF_LF_FORMDATA_FIELD.CREATE_USER is '创建人'
/

comment on column  T_BPMN_CONF_LF_FORMDATA_FIELD.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_CONF_LF_FORMDATA_FIELD.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_CONF_LF_FORMDATA_FIELD.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_CONF_LF_FORMDATA_FIELD_ID
    before insert
    on  T_BPMN_CONF_LF_FORMDATA_FIELD
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BPMN_CONF_LF_FORMDATA_FIELD.NEXTVAL;
END IF;
END;
/

create table  T_BPMN_NODE_ASSIGN_LEVEL_CONF
(
    ID NUMBER(19) not null
		constraint PK_T_BPMN_NODE_ASSIGN_LEVEL_CONF
			primary key,
    BPMN_NODE_ID NUMBER(19),
    ASSIGN_LEVEL_TYPE NUMBER(3),
    ASSIGN_LEVEL_GRADE NUMBER(3),
    REMARK NVARCHAR2(255),
    IS_DEL NUMBER(3) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(255),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(255),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_NODE_ASSIGN_LEVEL_CONF is '指定层级审批配置表'
/

comment on column  T_BPMN_NODE_ASSIGN_LEVEL_CONF.ID is '自增ID'
/

comment on column  T_BPMN_NODE_ASSIGN_LEVEL_CONF.BPMN_NODE_ID is '节点ID'
/

comment on column  T_BPMN_NODE_ASSIGN_LEVEL_CONF.ASSIGN_LEVEL_TYPE is '层级类型：1-组织线，2-员工直属上级，3-汇报线'
/

comment on column  T_BPMN_NODE_ASSIGN_LEVEL_CONF.ASSIGN_LEVEL_GRADE is '层级等级'
/

comment on column  T_BPMN_NODE_ASSIGN_LEVEL_CONF.REMARK is '备注'
/

comment on column  T_BPMN_NODE_ASSIGN_LEVEL_CONF.IS_DEL is '删除状态：0-未删除，1-已删除'
/

comment on column  T_BPMN_NODE_ASSIGN_LEVEL_CONF.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_ASSIGN_LEVEL_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_ASSIGN_LEVEL_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_ASSIGN_LEVEL_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_ASSIGN_LEVEL_CONF.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_NODE_ASSIGN_LEVEL_CONF_ID
    before insert
    on  T_BPMN_NODE_ASSIGN_LEVEL_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BPMN_NODE_ASSIGN_LEVEL_CONF.NEXTVAL;
END IF;
END;
/

create table  T_BPMN_NODE_CUSTOMIZE_CONF
(
    ID NUMBER(19) not null
		constraint PK_T_BPMN_NODE_CUSTOMIZE_CONF
			primary key,
    BPMN_NODE_ID NUMBER(19),
    SIGN_TYPE NUMBER(10) not null,
    REMARK NVARCHAR2(255),
    IS_DEL NUMBER(10) default 0,
    TENANT_ID NVARCHAR2(255) default '',
    CREATE_USER NVARCHAR2(255),
    CREATE_TIME DATE,
    UPDATE_USER NVARCHAR2(255),
    UPDATE_TIME DATE
)
    /

comment on table  T_BPMN_NODE_CUSTOMIZE_CONF is '节点自定义配置表'
/

comment on column  T_BPMN_NODE_CUSTOMIZE_CONF.ID is '自增ID'
/

comment on column  T_BPMN_NODE_CUSTOMIZE_CONF.BPMN_NODE_ID is '节点ID'
/

comment on column  T_BPMN_NODE_CUSTOMIZE_CONF.SIGN_TYPE is '会签类型：1-全部会签，2-或签'
/

comment on column  T_BPMN_NODE_CUSTOMIZE_CONF.REMARK is '备注'
/

comment on column  T_BPMN_NODE_CUSTOMIZE_CONF.IS_DEL is '删除状态：0-正常，1-已删除'
/

comment on column  T_BPMN_NODE_CUSTOMIZE_CONF.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_CUSTOMIZE_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_CUSTOMIZE_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_CUSTOMIZE_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_CUSTOMIZE_CONF.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_NODE_CUSTOMIZE_CONF_ID
    before insert
    on  T_BPMN_NODE_CUSTOMIZE_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BPMN_NODE_CUSTOMIZE_CONF.NEXTVAL;
END IF;
END;
/

create table  T_BPMN_NODE_HRBP_CONF
(
    ID NUMBER(19) not null
		constraint PK_T_BPMN_NODE_HRBP_CONF
			primary key,
    BPMN_NODE_ID NUMBER(19),
    HRBP_CONF_TYPE NUMBER(10),
    REMARK NVARCHAR2(255),
    IS_DEL NUMBER(10) default 0,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(255),
    CREATE_TIME DATE,
    UPDATE_USER NVARCHAR2(255),
    UPDATE_TIME DATE
)
    /

comment on table  T_BPMN_NODE_HRBP_CONF is 'HRBP 配置表'
/

comment on column  T_BPMN_NODE_HRBP_CONF.ID is '自增ID'
/

comment on column  T_BPMN_NODE_HRBP_CONF.BPMN_NODE_ID is '节点ID'
/

comment on column  T_BPMN_NODE_HRBP_CONF.HRBP_CONF_TYPE is 'HRBP类型：1-HRBP，2-HRBP负责人；可用于扩展（如3-HRBP经理等）'
/

comment on column  T_BPMN_NODE_HRBP_CONF.REMARK is '备注'
/

comment on column  T_BPMN_NODE_HRBP_CONF.IS_DEL is '删除状态：0-正常，1-已删除'
/

comment on column  T_BPMN_NODE_HRBP_CONF.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_HRBP_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_HRBP_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_HRBP_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_HRBP_CONF.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_NODE_HRBP_CONF_ID
    before insert
    on  T_BPMN_NODE_HRBP_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BPMN_NODE_HRBP_CONF.NEXTVAL;
END IF;
END;
/

create table  T_BPMN_NODE_LABELS
(
    ID NUMBER(19) not null
		constraint T_BPMN_NODE_LABELS_PK
			primary key,
    NODEID NUMBER(19),
    LABEL_NAME NVARCHAR2(50),
    LABEL_VALUE NVARCHAR2(64),
    REMARK NVARCHAR2(255) default '' not null,
    IS_DEL NUMBER(3) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(32) default '',
    CREATE_TIME DATE default SYSDATE not null,
    UPDATE_USER NVARCHAR2(32) default '',
    UPDATE_TIME DATE default SYSDATE not null
)
    /

comment on table  T_BPMN_NODE_LABELS is '流程节点标签表，用于存储额外的自定义信息'
/

comment on column  T_BPMN_NODE_LABELS.ID is '主键ID'
/

comment on column  T_BPMN_NODE_LABELS.NODEID is '节点ID'
/

comment on column  T_BPMN_NODE_LABELS.LABEL_NAME is '标签名称'
/

comment on column  T_BPMN_NODE_LABELS.LABEL_VALUE is '标签值（用户自定义标识）'
/

comment on column  T_BPMN_NODE_LABELS.REMARK is '备注'
/

comment on column  T_BPMN_NODE_LABELS.IS_DEL is '删除状态：0-正常，1-删除'
/

comment on column  T_BPMN_NODE_LABELS.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_LABELS.CREATE_USER is '创建人（邮箱前缀）'
/

comment on column  T_BPMN_NODE_LABELS.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_LABELS.UPDATE_USER is '更新人（邮箱前缀）'
/

comment on column  T_BPMN_NODE_LABELS.UPDATE_TIME is '更新时间'
/

create index  INDX_NODE_ID
    on  T_BPMN_NODE_LABELS (NODEID)
    /

create trigger  TRG_T_BPMN_NODE_LABELS_ID
    before insert
    on  T_BPMN_NODE_LABELS
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BPMN_NODE_LABELS.NEXTVAL;
END IF;
END;
/

create table  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL
(
    ID NUMBER(19) not null
		constraint T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL_PK
			primary key,
    NODE_ID NUMBER(19) not null,
    FORMDATA_ID NUMBER(19) not null,
    FIELD_ID NVARCHAR2(100),
    FIELD_NAME NVARCHAR2(255),
    FIELD_PERM NVARCHAR2(10),
    IS_DEL NUMBER(3) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(255),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(255),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL is '流程节点低代码表单字段控制权限表'
/

comment on column  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL.ID is '主键ID'
/

comment on column  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL.NODE_ID is '节点ID'
/

comment on column  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL.FORMDATA_ID is '表单数据ID'
/

comment on column  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL.FIELD_ID is '字段ID'
/

comment on column  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL.FIELD_NAME is '字段名称'
/

comment on column  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL.FIELD_PERM is '字段权限（如：显示/隐藏、只读/可编辑等）'
/

comment on column  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL.IS_DEL is '删除状态：0-正常，1-删除'
/

comment on column  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL_ID
    before insert
    on  T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BPMN_NODE_LF_FORMDATA_FIELD_CONTROL.NEXTVAL;
END IF;
END;
/

create table  T_BPMN_NODE_LOOP_CONF
(
    ID NUMBER(19) not null
		constraint PK_T_BPMN_NODE_LOOP_CONF
			primary key,
    BPMN_NODE_ID NUMBER(19),
    LOOP_END_TYPE NUMBER(10),
    LOOP_NUMBER_PLIES NUMBER(10),
    LOOP_END_PERSON NVARCHAR2(50),
    NOPARTICIPATING_STAFF_IDS NVARCHAR2(255),
    LOOP_END_GRADE NUMBER(10),
    REMARK NVARCHAR2(255),
    IS_DEL NUMBER(3) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_NODE_LOOP_CONF is '循环审批配置表'
/

comment on column  T_BPMN_NODE_LOOP_CONF.ID is '自增ID'
/

comment on column  T_BPMN_NODE_LOOP_CONF.BPMN_NODE_ID is '节点ID'
/

comment on column  T_BPMN_NODE_LOOP_CONF.LOOP_END_TYPE is '循环结束类型：1-组织线，2-汇报线'
/

comment on column  T_BPMN_NODE_LOOP_CONF.LOOP_NUMBER_PLIES is '循环层级数量'
/

comment on column  T_BPMN_NODE_LOOP_CONF.LOOP_END_PERSON is '循环结束人员（用户名/工号等）'
/

comment on column  T_BPMN_NODE_LOOP_CONF.NOPARTICIPATING_STAFF_IDS is '不参与人员ID列表（逗号分隔）'
/

comment on column  T_BPMN_NODE_LOOP_CONF.LOOP_END_GRADE is '循环结束层级'
/

comment on column  T_BPMN_NODE_LOOP_CONF.REMARK is '备注'
/

comment on column  T_BPMN_NODE_LOOP_CONF.IS_DEL is '删除状态：0-未删除，1-已删除'
/

comment on column  T_BPMN_NODE_LOOP_CONF.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_LOOP_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_LOOP_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_LOOP_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_LOOP_CONF.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_NODE_LOOP_CONF_ID
    before insert
    on  T_BPMN_NODE_LOOP_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BPMN_NODE_LOOP_CONF.NEXTVAL;
END IF;
END;
/

create table  T_BPMN_NODE_OUT_SIDE_ACCESS_CONF
(
    ID NUMBER(19) not null
		constraint PK_T_BPMN_NODE_OUT_SIDE_ACCESS_CONF
			primary key,
    BPMN_NODE_ID NUMBER(19) not null,
    NODE_MARK NVARCHAR2(50),
    SIGN_TYPE NUMBER(10),
    REMARK NVARCHAR2(255),
    IS_DEL NUMBER(10) default 0 not null,
    CREATE_USER NVARCHAR2(50),
    CREATE_TIME DATE default SYSDATE not null,
    UPDATE_USER NVARCHAR2(50),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_NODE_OUT_SIDE_ACCESS_CONF is '外部访问节点配置表'
/

comment on column  T_BPMN_NODE_OUT_SIDE_ACCESS_CONF.ID is '自增ID'
/

comment on column  T_BPMN_NODE_OUT_SIDE_ACCESS_CONF.BPMN_NODE_ID is 'BPMN节点ID'
/

comment on column  T_BPMN_NODE_OUT_SIDE_ACCESS_CONF.NODE_MARK is '节点标识'
/

comment on column  T_BPMN_NODE_OUT_SIDE_ACCESS_CONF.SIGN_TYPE is '会签类型：1-全部会签，2-或签'
/

comment on column  T_BPMN_NODE_OUT_SIDE_ACCESS_CONF.REMARK is '备注'
/

comment on column  T_BPMN_NODE_OUT_SIDE_ACCESS_CONF.IS_DEL is '删除状态：0-正常，1-删除'
/

comment on column  T_BPMN_NODE_OUT_SIDE_ACCESS_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_OUT_SIDE_ACCESS_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_OUT_SIDE_ACCESS_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_OUT_SIDE_ACCESS_CONF.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_NODE_OUT_SIDE_ACCESS_CONF_ID
    before insert
    on  T_BPMN_NODE_OUT_SIDE_ACCESS_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BPMN_NODE_OUT_SIDE_ACCESS_CONF.NEXTVAL;
END IF;
END;
/

create table  T_BPMN_NODE_ROLE_CONF
(
    ID NUMBER(19) not null
		constraint PK_T_BPMN_NODE_ROLE_CONF
			primary key,
    BPMN_NODE_ID NUMBER(19) not null,
    ROLE_ID NVARCHAR2(64) not null,
    ROLE_NAME NVARCHAR2(64) not null,
    SIGN_TYPE NUMBER(10) not null,
    REMARK NVARCHAR2(255),
    IS_DEL NUMBER(3) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(50) not null,
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_BPMN_NODE_ROLE_CONF is '指定角色审批人配置表'
/

comment on column  T_BPMN_NODE_ROLE_CONF.ID is '自增ID'
/

comment on column  T_BPMN_NODE_ROLE_CONF.BPMN_NODE_ID is '节点ID'
/

comment on column  T_BPMN_NODE_ROLE_CONF.ROLE_ID is '角色ID'
/

comment on column  T_BPMN_NODE_ROLE_CONF.ROLE_NAME is '角色名称'
/

comment on column  T_BPMN_NODE_ROLE_CONF.SIGN_TYPE is '会签类型：1-全部会签，2-或签'
/

comment on column  T_BPMN_NODE_ROLE_CONF.REMARK is '备注'
/

comment on column  T_BPMN_NODE_ROLE_CONF.IS_DEL is '删除状态：0-正常，1-已删除'
/

comment on column  T_BPMN_NODE_ROLE_CONF.TENANT_ID is '租户ID'
/

comment on column  T_BPMN_NODE_ROLE_CONF.CREATE_USER is '创建人'
/

comment on column  T_BPMN_NODE_ROLE_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_BPMN_NODE_ROLE_CONF.UPDATE_USER is '更新人'
/

comment on column  T_BPMN_NODE_ROLE_CONF.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_BPMN_NODE_ROLE_CONF_ID
    before insert
    on  T_BPMN_NODE_ROLE_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_BPMN_NODE_ROLE_CONF.NEXTVAL;
END IF;
END;
/

create table  T_LF_MAIN
(
    ID NUMBER(19) not null
		constraint T_LF_MAIN_PK
			primary key,
    CONF_ID NUMBER(19),
    FORM_CODE NVARCHAR2(255),
    IS_DEL NUMBER(3) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(255),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(255),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_LF_MAIN is '低代码表单主表'
/

comment on column  T_LF_MAIN.ID is '主键ID'
/

comment on column  T_LF_MAIN.CONF_ID is '配置ID'
/

comment on column  T_LF_MAIN.FORM_CODE is '表单编码'
/

comment on column  T_LF_MAIN.IS_DEL is '删除状态：0-正常，1-删除'
/

comment on column  T_LF_MAIN.TENANT_ID is '租户ID'
/

comment on column  T_LF_MAIN.CREATE_USER is '创建人'
/

comment on column  T_LF_MAIN.CREATE_TIME is '创建时间'
/

comment on column  T_LF_MAIN.UPDATE_USER is '更新人'
/

comment on column  T_LF_MAIN.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_LF_MAIN_ID
    before insert
    on  T_LF_MAIN
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_LF_MAIN.NEXTVAL;
END IF;
END;
/

create table  T_LF_MAIN_FIELD
(
    ID NUMBER(19) not null
		constraint T_LF_MAIN_FIELD_PK
			primary key,
    MAIN_ID NUMBER(19) not null,
    FORM_CODE NVARCHAR2(255),
    FIELD_ID NVARCHAR2(255),
    FIELD_NAME NVARCHAR2(255),
    PARENT_FIELD_ID NVARCHAR2(255),
    PARENT_FIELD_NAME NVARCHAR2(255),
    FIELD_VALUE NVARCHAR2(2000),
    FIELD_VALUE_NUMBER NUMBER(14,2),
    FIELD_VALUE_DT DATE,
    FIELD_VALUE_TEXT CLOB,
    SORT NUMBER(10) default 0 not null,
    IS_DEL NUMBER(3) default 0 not null,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_USER NVARCHAR2(255),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(255),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_LF_MAIN_FIELD is '低代码表单字段值表'
/

comment on column  T_LF_MAIN_FIELD.ID is '主键ID'
/

comment on column  T_LF_MAIN_FIELD.MAIN_ID is '关联主表ID（t_lf_main.id）'
/

comment on column  T_LF_MAIN_FIELD.FORM_CODE is '表单编码'
/

comment on column  T_LF_MAIN_FIELD.FIELD_ID is '字段ID'
/

comment on column  T_LF_MAIN_FIELD.FIELD_NAME is '字段名称'
/

comment on column  T_LF_MAIN_FIELD.PARENT_FIELD_ID is '父字段ID（用于嵌套结构）'
/

comment on column  T_LF_MAIN_FIELD.PARENT_FIELD_NAME is '父字段名称'
/

comment on column  T_LF_MAIN_FIELD.FIELD_VALUE is '字段值（字符串类型，长度≤2000）'
/

comment on column  T_LF_MAIN_FIELD.FIELD_VALUE_NUMBER is '字段值（数值类型，精度14，小数位2）'
/

comment on column  T_LF_MAIN_FIELD.FIELD_VALUE_DT is '字段值（日期时间类型）'
/

comment on column  T_LF_MAIN_FIELD.FIELD_VALUE_TEXT is '字段值（长文本，用于超长内容）'
/

comment on column  T_LF_MAIN_FIELD.SORT is '排序序号'
/

comment on column  T_LF_MAIN_FIELD.IS_DEL is '删除状态：0-正常，1-删除'
/

comment on column  T_LF_MAIN_FIELD.TENANT_ID is '租户ID'
/

comment on column  T_LF_MAIN_FIELD.CREATE_USER is '创建人'
/

comment on column  T_LF_MAIN_FIELD.CREATE_TIME is '创建时间'
/

comment on column  T_LF_MAIN_FIELD.UPDATE_USER is '更新人'
/

comment on column  T_LF_MAIN_FIELD.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_LF_MAIN_FIELD_ID
    before insert
    on  T_LF_MAIN_FIELD
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_LF_MAIN_FIELD.NEXTVAL;
END IF;
END;
/

create table  T_OUT_SIDE_BPM_ACCESS_BUSINESS
(
    ID NUMBER(19) not null
		constraint PK_T_OUT_SIDE_BPM_ACCESS_BUSINESS
			primary key,
    BUSINESS_PARTY_ID NUMBER(19) not null,
    BPMN_CONF_ID NUMBER(19) not null,
    FORM_CODE NVARCHAR2(50),
    PROCESS_NUMBER NVARCHAR2(50),
    FORM_DATA_PC CLOB,
    FORM_DATA_APP CLOB,
    TEMPLATE_MARK NVARCHAR2(50),
    START_USERNAME NVARCHAR2(50),
    REMARK CLOB,
    IS_DEL NUMBER(3) default 0,
    CREATE_USER NVARCHAR2(50),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_OUT_SIDE_BPM_ACCESS_BUSINESS is '外部BPM业务接入配置表'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.ID is '主键ID'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.BUSINESS_PARTY_ID is '业务方ID'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.BPMN_CONF_ID is '流程配置ID'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.FORM_CODE is '表单编码'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.PROCESS_NUMBER is '流程编号'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.FORM_DATA_PC is 'PC端表单数据（JSON等）'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.FORM_DATA_APP is '移动端表单数据（JSON等）'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.TEMPLATE_MARK is '模板标识'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.START_USERNAME is '流程发起人用户名'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.REMARK is '备注'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.IS_DEL is '删除状态：0-正常，1-删除'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.CREATE_USER is '创建人'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.CREATE_TIME is '创建时间'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.UPDATE_USER is '更新人'
/

comment on column  T_OUT_SIDE_BPM_ACCESS_BUSINESS.UPDATE_TIME is '更新时间'
/

create index  IDX_BPM_CONF_ID
    on  T_OUT_SIDE_BPM_ACCESS_BUSINESS (BPMN_CONF_ID)
    /

create index  IDX_BUSINESS_PARTY_ID
    on  T_OUT_SIDE_BPM_ACCESS_BUSINESS (BUSINESS_PARTY_ID)
    /

create trigger  TRG_T_OUT_SIDE_BPM_ACCESS_BUSINESS_ID
    before insert
    on  T_OUT_SIDE_BPM_ACCESS_BUSINESS
    for each row
BEGIN
    IF :NEW.ID IS N
/

    create table  T_OUT_SIDE_BPM_ADMIN_PERSONNEL
    (
        ID NUMBER(19) not null
		constraint PK_T_OUT_SIDE_BPM_ADMIN_PERSONNEL
			primary key,
        BUSINESS_PARTY_ID NUMBER(19),
        TYPE NUMBER(10),
        EMPLOYEE_ID NVARCHAR2(64),
        EMPLOYEE_NAME NVARCHAR2(64),
        REMARK NVARCHAR2(255),
        IS_DEL NUMBER(10),
        CREATE_USER NVARCHAR2(50),
        CREATE_TIME DATE default SYSDATE,
        UPDATE_USER NVARCHAR2(50),
        UPDATE_TIME DATE default SYSDATE
    )
        /

comment on table  T_OUT_SIDE_BPM_ADMIN_PERSONNEL is '流程外部服务 - 业务方管理员表'
/

comment on column  T_OUT_SIDE_BPM_ADMIN_PERSONNEL.ID is '自增ID'
/

comment on column  T_OUT_SIDE_BPM_ADMIN_PERSONNEL.BUSINESS_PARTY_ID is '业务方主表ID'
/

comment on column  T_OUT_SIDE_BPM_ADMIN_PERSONNEL.TYPE is '管理员类型：1-流程管理员，2-应用管理员，3-接口管理员'
/

comment on column  T_OUT_SIDE_BPM_ADMIN_PERSONNEL.EMPLOYEE_ID is '管理员ID（员工工号）'
/

comment on column  T_OUT_SIDE_BPM_ADMIN_PERSONNEL.EMPLOYEE_NAME is '管理员姓名（员工姓名）'
/

comment on column  T_OUT_SIDE_BPM_ADMIN_PERSONNEL.REMARK is '备注'
/

comment on column  T_OUT_SIDE_BPM_ADMIN_PERSONNEL.IS_DEL is '删除标志：0-正常，1-已删除'
/

comment on column  T_OUT_SIDE_BPM_ADMIN_PERSONNEL.CREATE_USER is '创建人'
/

comment on column  T_OUT_SIDE_BPM_ADMIN_PERSONNEL.CREATE_TIME is '创建时间'
/

comment on column  T_OUT_SIDE_BPM_ADMIN_PERSONNEL.UPDATE_USER is '更新人'
/

comment on column  T_OUT_SIDE_BPM_ADMIN_PERSONNEL.UPDATE_TIME is '更新时间'
/

    create trigger  TRG_T_OUT_SIDE_BPM_ADMIN_PERSONNEL_ID
        before insert
        on  T_OUT_SIDE_BPM_ADMIN_PERSONNEL
        for each row
    BEGIN
        IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_OUT_SIDE_BPM_ADMIN_PERSONNEL.NEXTVAL;
    END IF;
END;
/



create table  T_OUT_SIDE_BPM_BUSINESS_PARTY
(
    ID NUMBER(19) not null
		constraint PK_T_OUT_SIDE_BPM_BUSINESS_PARTY
			primary key,
    BUSINESS_PARTY_MARK NVARCHAR2(50),
    NAME NVARCHAR2(255),
    TYPE NUMBER(3),
    REMARK NVARCHAR2(255),
    IS_DEL NUMBER(3) default 0,
    CREATE_USER NVARCHAR2(50),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_OUT_SIDE_BPM_BUSINESS_PARTY is '外部BPM系统业务方信息表'
/

comment on column  T_OUT_SIDE_BPM_BUSINESS_PARTY.ID is '自增ID'
/

comment on column  T_OUT_SIDE_BPM_BUSINESS_PARTY.BUSINESS_PARTY_MARK is '业务方标识'
/

comment on column  T_OUT_SIDE_BPM_BUSINESS_PARTY.NAME is '业务方名称'
/

comment on column  T_OUT_SIDE_BPM_BUSINESS_PARTY.TYPE is '业务类型：1-嵌入式接入，2-API接入'
/

comment on column  T_OUT_SIDE_BPM_BUSINESS_PARTY.REMARK is '备注'
/

comment on column  T_OUT_SIDE_BPM_BUSINESS_PARTY.IS_DEL is '删除标志：0-正常，1-已删除'
/

comment on column  T_OUT_SIDE_BPM_BUSINESS_PARTY.CREATE_USER is '创建人'
/

comment on column  T_OUT_SIDE_BPM_BUSINESS_PARTY.CREATE_TIME is '创建时间'
/

comment on column  T_OUT_SIDE_BPM_BUSINESS_PARTY.UPDATE_USER is '更新人'
/

comment on column  T_OUT_SIDE_BPM_BUSINESS_PARTY.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_OUT_SIDE_BPM_BUSINESS_PARTY_ID
    before insert
    on  T_OUT_SIDE_BPM_BUSINESS_PARTY
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_OUT_SIDE_BPM_BUSINESS_PARTY.NEXTVAL;
END IF;
END;
/

create table  T_OUT_SIDE_BPM_CALL_BACK_RECORD
(
    ID NUMBER(10) not null
		constraint PK_T_OUT_SIDE_BPM_CALL_BACK_RECORD
			primary key,
    PROCESS_NUMBER NVARCHAR2(50),
    STATUS NUMBER(3),
    RETRY_TIMES NUMBER(3),
    BUTTON_OPERATION_TYPE NUMBER(3),
    CALL_BACK_TYPE_NAME NVARCHAR2(255),
    BUSINESS_ID NUMBER(19),
    FORM_CODE NVARCHAR2(50),
    IS_DEL NUMBER(3) default 0,
    CREATE_USER NVARCHAR2(50),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_OUT_SIDE_BPM_CALL_BACK_RECORD is '回调记录表'
/

comment on column  T_OUT_SIDE_BPM_CALL_BACK_RECORD.ID is '自增ID'
/

comment on column  T_OUT_SIDE_BPM_CALL_BACK_RECORD.PROCESS_NUMBER is '流程编号'
/

comment on column  T_OUT_SIDE_BPM_CALL_BACK_RECORD.STATUS is '推送状态：0-成功，1-失败'
/

comment on column  T_OUT_SIDE_BPM_CALL_BACK_RECORD.RETRY_TIMES is '重试次数'
/

comment on column  T_OUT_SIDE_BPM_CALL_BACK_RECORD.BUTTON_OPERATION_TYPE is '操作类型（参考 MsgProcessEventEnum 枚举）'
/

comment on column  T_OUT_SIDE_BPM_CALL_BACK_RECORD.CALL_BACK_TYPE_NAME is '回调类型名称（参考 CallbackTypeEnum 枚举）'
/

comment on column  T_OUT_SIDE_BPM_CALL_BACK_RECORD.BUSINESS_ID is '业务ID'
/

comment on column  T_OUT_SIDE_BPM_CALL_BACK_RECORD.FORM_CODE is '表单编码'
/

comment on column  T_OUT_SIDE_BPM_CALL_BACK_RECORD.IS_DEL is '删除标志：0-正常，1-已删除'
/

comment on column  T_OUT_SIDE_BPM_CALL_BACK_RECORD.CREATE_USER is '创建人'
/

comment on column  T_OUT_SIDE_BPM_CALL_BACK_RECORD.CREATE_TIME is '创建时间'
/

comment on column  T_OUT_SIDE_BPM_CALL_BACK_RECORD.UPDATE_USER is '更新人'
/

comment on column  T_OUT_SIDE_BPM_CALL_BACK_RECORD.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_OUT_SIDE_BPM_CALL_BACK_RECORD_ID
    before insert
    on  T_OUT_SIDE_BPM_CALL_BACK_RECORD
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_OUT_SIDE_BPM_CALL_BACK_RECORD.NEXTVAL;
END IF;
END;
/

create table  T_OUT_SIDE_BPM_CALLBACK_URL_CONF
(
    ID NUMBER(19) not null
		constraint PK_T_OUT_SIDE_BPM_CALLBACK_URL_CONF
			primary key,
    BUSINESS_PARTY_ID NUMBER(19),
    APPLICATION_ID NUMBER(19),
    BPMN_CONF_ID NUMBER(19),
    FORM_CODE NVARCHAR2(64),
    BPM_CONF_CALLBACK_URL NVARCHAR2(500),
    BPM_FLOW_CALLBACK_URL NVARCHAR2(500),
    API_CLIENT_ID NVARCHAR2(100),
    API_CLIENT_SECRET NVARCHAR2(100),
    STATUS NUMBER(3) default 0,
    CREATE_USER NVARCHAR2(50),
    UPDATE_USER NVARCHAR2(50),
    REMARK NVARCHAR2(50),
    IS_DEL NUMBER(3) default 0,
    CREATE_TIME DATE default SYSDATE,
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_OUT_SIDE_BPM_CALLBACK_URL_CONF is '业务方回调URL配置表'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.ID is '自增ID'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.BUSINESS_PARTY_ID is '业务方ID'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.APPLICATION_ID is '业务方应用ID'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.BPMN_CONF_ID is 'BPMN流程配置ID'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.FORM_CODE is '表单编码'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.BPM_CONF_CALLBACK_URL is '流程配置回调URL'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.BPM_FLOW_CALLBACK_URL is '流程实例回调URL'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.API_CLIENT_ID is '应用ID（clientId）'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.API_CLIENT_SECRET is '应用密钥（clientSecret）'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.STATUS is '状态：0-启用，1-禁用'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.CREATE_USER is '创建人'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.UPDATE_USER is '更新人'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.REMARK is '备注'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.IS_DEL is '删除标志：0-正常，1-已删除'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_OUT_SIDE_BPM_CALLBACK_URL_CONF.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_OUT_SIDE_BPM_CALLBACK_URL_CONF_ID
    before insert
    on  T_OUT_SIDE_BPM_CALLBACK_URL_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_OUT_SIDE_BPM_CALLBACK_URL_CONF.NEXTVAL;
END IF;
END;
/

create table  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE
(
    ID NUMBER(19) not null
		constraint PK_T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE
			primary key,
    BUSINESS_PARTY_ID NUMBER(19),
    TEMPLATE_MARK NVARCHAR2(50),
    TEMPLATE_NAME NVARCHAR2(50),
    APPLICATION_ID NUMBER(10),
    REMARK NVARCHAR2(255),
    IS_DEL NUMBER(3) default 0,
    CREATE_USER NVARCHAR2(50),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50),
    UPDATE_TIME DATE default SYSDATE,
    CREATE_USER_ID NVARCHAR2(64)
)
    /

comment on table  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE is '外部接入流程条件模板配置表'
/

comment on column  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE.ID is '自增ID'
/

comment on column  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE.BUSINESS_PARTY_ID is '业务方ID'
/

comment on column  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE.TEMPLATE_MARK is '模板标识'
/

comment on column  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE.TEMPLATE_NAME is '模板名称'
/

comment on column  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE.APPLICATION_ID is '应用ID'
/

comment on column  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE.REMARK is '备注'
/

comment on column  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE.IS_DEL is '删除标志：0-正常，1-已删除'
/

comment on column  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE.CREATE_USER is '创建人用户名'
/

comment on column  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE.CREATE_TIME is '创建时间'
/

comment on column  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE.UPDATE_USER is '更新人用户名'
/

comment on column  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE.UPDATE_TIME is '更新时间'
/

comment on column  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE.CREATE_USER_ID is '创建人用户ID'
/

create trigger  TRG_T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE_ID
    before insert
    on  T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE
    for each row
BEGIN

    IF :NEW.ID IS NULL THEN

        :NEW.ID := SEQ_T_OUT_SIDE_BPM_CONDITIONS_TEMPLATE.NEXTVAL;

END IF;

END;
/

create table  T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF
(
    ID NUMBER(19) not null
		constraint PK_T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF
			primary key,
    BPMN_NODE_ID NUMBER(19),
    OUT_SIDE_ID NVARCHAR2(50),
    REMARK NVARCHAR2(255),
    IS_DEL NUMBER(10),
    CREATE_USER NVARCHAR2(50),
    CREATE_TIME DATE default SYSDATE,
    UPDATE_USER NVARCHAR2(50),
    UPDATE_TIME DATE default SYSDATE
)
    /

comment on table  T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF is '外部接入流程 - 业务方节点条件配置表'
/

comment on column  T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF.ID is '自增ID'
/

comment on column  T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF.BPMN_NODE_ID is 'BPMN节点ID'
/

comment on column  T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF.OUT_SIDE_ID is '外部条件ID'
/

comment on column  T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF.REMARK is '备注'
/

comment on column  T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF.IS_DEL is '删除标志：0-正常，1-已删除'
/

comment on column  T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF.CREATE_USER is '创建人'
/

comment on column  T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF.CREATE_TIME is '创建时间'
/

comment on column  T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF.UPDATE_USER is '更新人'
/

comment on column  T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF.UPDATE_TIME is '更新时间'
/

create trigger  TRG_T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF_ID
    before insert
    on  T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_OUT_SIDE_BPMN_NODE_CONDITIONS_CONF.NEXTVAL;
END IF;
END;
/

create table  T_QUICK_ENTRY
(
    ID NUMBER(10) not null
		constraint PK_T_QUICK_ENTRY
			primary key,
    TITLE NVARCHAR2(100) not null,
    EFFECTIVE_SOURCE NVARCHAR2(255),
    IS_DEL NUMBER(3) default 0,
    TENANT_ID NVARCHAR2(64) default '',
    ROUTE NVARCHAR2(500) not null,
    SORT NUMBER(3) default 0,
    CREATE_TIME DATE default SYSDATE,
    STATUS NUMBER(3) default 0 not null,
    VARIABLE_URL_FLAG NUMBER(3) default 0 not null
)
    /

comment on table  T_QUICK_ENTRY is '快捷入口配置表'
/

comment on column  T_QUICK_ENTRY.TENANT_ID is '租户ID'
/

create index  IDX_ROUTE
    on  T_QUICK_ENTRY (ROUTE)
    /

create trigger  TRG_T_QUICK_ENTRY_ID
    before insert
    on  T_QUICK_ENTRY
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_QUICK_ENTRY.NEXTVAL;
END IF;
END;
/

create table  T_QUICK_ENTRY_TYPE
(
    ID NUMBER(19) not null
		constraint PK_T_QUICK_ENTRY_TYPE
			primary key,
    QUICK_ENTRY_ID NUMBER(19) not null,
    TYPE NUMBER(10) not null,
    IS_DEL NUMBER(3) default 0,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_TIME DATE default SYSDATE,
    TYPE_NAME NVARCHAR2(255) not null
)
    /

comment on table  T_QUICK_ENTRY_TYPE is '快捷入口类型配置表'
/

comment on column  T_QUICK_ENTRY_TYPE.TYPE is '类型：1-PC端，2-移动端'
/

comment on column  T_QUICK_ENTRY_TYPE.IS_DEL is '删除标志'
/

comment on column  T_QUICK_ENTRY_TYPE.TENANT_ID is '租户ID'
/

comment on column  T_QUICK_ENTRY_TYPE.TYPE_NAME is '类型名称'
/

create index  IDX_QUICK_ENTRY_ID
    on  T_QUICK_ENTRY_TYPE (QUICK_ENTRY_ID)
    /

create trigger  TRG_T_QUICK_ENTRY_TYPE_ID
    before insert
    on  T_QUICK_ENTRY_TYPE
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
        :NEW.ID := SEQ_T_QUICK_ENTRY_TYPE.NEXTVAL;
END IF;
END;
/

create table  T_SYS_VERSION
(
    ID NUMBER(20) not null
		constraint PK_T_SYS_VERSION
			primary key,
    CREATE_TIME DATE default SYSDATE,
    UPDATE_TIME DATE default SYSDATE,
    IS_DEL NUMBER(1) default 0,
    TENANT_ID NVARCHAR2(64) default '',
    VERSION NVARCHAR2(100) not null,
    DESCRIPTION NVARCHAR2(255),
    IDX NUMBER(10),
    IS_FORCE NUMBER(1),
    ANDROID_URL NVARCHAR2(500),
    IOS_URL NVARCHAR2(500),
    CREATE_USER NVARCHAR2(50),
    UPDATE_USER NVARCHAR2(50),
    IS_HIDE NUMBER(1),
    DOWNLOAD_CODE NVARCHAR2(255),
    EFFECTIVE_TIME DATE default SYSDATE
)
    /

comment on table  T_SYS_VERSION is '系统版本控制表'
/

comment on column  T_SYS_VERSION.ID is '自增主键'
/

comment on column  T_SYS_VERSION.CREATE_TIME is '创建时间'
/

comment on column  T_SYS_VERSION.UPDATE_TIME is '更新时间'
/

comment on column  T_SYS_VERSION.IS_DEL is '是否删除 0:正常 1:已删除'
/

comment on column  T_SYS_VERSION.TENANT_ID is '租户ID'
/

comment on column  T_SYS_VERSION.VERSION is '版本号'
/

comment on column  T_SYS_VERSION.DESCRIPTION is '版本描述'
/

comment on column  T_SYS_VERSION.IDX is '索引编号'
/

comment on column  T_SYS_VERSION.IS_FORCE is '是否强制更新 0:否 1:是'
/

comment on column  T_SYS_VERSION.ANDROID_URL is 'Android 下载地址'
/

comment on column  T_SYS_VERSION.IOS_URL is 'iOS 下载地址'
/

comment on column  T_SYS_VERSION.CREATE_USER is '创建人'
/

comment on column  T_SYS_VERSION.UPDATE_USER is '更新人'
/

comment on column  T_SYS_VERSION.IS_HIDE is '是否隐藏 0:否 1:是'
/

comment on column  T_SYS_VERSION.DOWNLOAD_CODE is '下载码'
/

comment on column  T_SYS_VERSION.EFFECTIVE_TIME is '生效时间'
/

create index  IDX_T_SYS_VERSION_VERSION
    on  T_SYS_VERSION (VERSION)
    /

create trigger  TRG_T_SYS_VERSION
    before insert
    on  T_SYS_VERSION
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_T_SYS_VERSION.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

create table  BPM_PROCESS_NAME
(
    ID NUMBER(19) not null
		constraint PK_BPM_PROCESS_NAME
			primary key,
    PROCESS_NAME NVARCHAR2(50),
    IS_DEL NUMBER(10) default 0,
    TENANT_ID NVARCHAR2(64) default '',
    CREATE_TIME DATE default SYSDATE
)
    /

comment on table  BPM_PROCESS_NAME is '流程高级搜索表'
/

comment on column  BPM_PROCESS_NAME.PROCESS_NAME is '流程名称'
/

comment on column  BPM_PROCESS_NAME.IS_DEL is '删除标志'
/

comment on column  BPM_PROCESS_NAME.TENANT_ID is '租户ID'
/

comment on column  BPM_PROCESS_NAME.CREATE_TIME is '创建时间'
/

create trigger  TRG_BPM_PROCESS_NAME_ID
    before insert
    on  BPM_PROCESS_NAME
    for each row
BEGIN
    IF :NEW.ID IS NULL THEN
    SELECT SEQ_BPM_PROCESS_NAME.NEXTVAL INTO :NEW.ID FROM DUAL;
END IF;
END;
/

