
-- ----------------------------
-- 本sql里的表最终都可以删除掉
-- ----------------------------

-- ----------------------------
-- 如果用户没有对接自己系统的用户表,这个表是必须的
-- ----------------------------
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