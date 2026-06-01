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


-- ----------------------------
-- 初始化自定义审批人规则字典数据
INSERT INTO t_dict_data(`dict_label`, `dict_value`, `dic_value_type`, `remark`,`dict_type`, `dict_second_level_type`, `css_class`, `list_class`,`is_default`, `is_del`, `tenant_id`, `create_user`)
VALUES ( '自定义审批人1', 'zdysp1', NULL, NULL, 'udr', NULL, NULL, NULL, 'N', 0, '', '系统');
INSERT INTO t_dict_data(`dict_label`, `dict_value`, `dic_value_type`, `remark`,`dict_type`, `dict_second_level_type`, `css_class`, `list_class`,`is_default`, `is_del`, `tenant_id`, `create_user`)
VALUES ( '自定义审批人2', 'zdysp2', NULL, NULL, 'udr', NULL, NULL, NULL, 'N', 0, '', '系统');
INSERT INTO t_dict_data(`dict_label`, `dict_value`, `dic_value_type`, `remark`,`dict_type`, `dict_second_level_type`, `css_class`, `list_class`,`is_default`, `is_del`, `tenant_id`, `create_user`)
VALUES ( '自定义审批人3', 'zdysp3', NULL, NULL, 'udr', NULL, NULL, NULL, 'N', 0, '', '系统');
INSERT INTO t_dict_data(`dict_label`, `dict_value`, `dic_value_type`, `remark`,`dict_type`, `dict_second_level_type`, `css_class`, `list_class`,`is_default`, `is_del`, `tenant_id`, `create_user`)
VALUES ( '自定义审批人4', 'zdysp4', NULL, NULL, 'udr', NULL, NULL, NULL, 'N', 0, '', '系统');
INSERT INTO t_dict_data(`dict_label`, `dict_value`, `dic_value_type`, `remark`,`dict_type`, `dict_second_level_type`, `css_class`, `list_class`,`is_default`, `is_del`, `tenant_id`, `create_user`)
VALUES ( '自定义审批人5', 'zdysp5', NULL, NULL, 'udr', NULL, NULL, NULL, 'N', 0, '', '系统');
INSERT INTO t_dict_data(`dict_label`, `dict_value`, `dic_value_type`, `remark`,`dict_type`, `dict_second_level_type`, `css_class`, `list_class`,`is_default`, `is_del`, `tenant_id`, `create_user`)
VALUES ( '自定义审批人6', 'zdysp6', NULL, NULL, 'udr', NULL, NULL, NULL, 'N', 0, '', '系统');
INSERT INTO t_dict_data(`dict_label`, `dict_value`, `dic_value_type`, `remark`,`dict_type`, `dict_second_level_type`, `css_class`, `list_class`,`is_default`, `is_del`, `tenant_id`, `create_user`)
VALUES ( '自定义审批人7', 'zdysp7', NULL, NULL, 'udr', NULL, NULL, NULL, 'N', 0, '', '系统');
INSERT INTO t_dict_data(`dict_label`, `dict_value`, `dic_value_type`, `remark`,`dict_type`, `dict_second_level_type`, `css_class`, `list_class`,`is_default`, `is_del`, `tenant_id`, `create_user`)
VALUES ( '自定义审批人8', 'zdysp8', NULL, NULL, 'udr', NULL, NULL, NULL, 'N', 0, '', '系统');
INSERT INTO t_dict_data(`dict_label`, `dict_value`, `dic_value_type`, `remark`,`dict_type`, `dict_second_level_type`, `css_class`, `list_class`,`is_default`, `is_del`, `tenant_id`, `create_user`)
VALUES ( '自定义审批人1', 'zdysp9', NULL, NULL, 'udr', NULL, NULL, NULL, 'N', 0, '', '系统');
INSERT INTO t_dict_data(`dict_label`, `dict_value`, `dic_value_type`, `remark`,`dict_type`, `dict_second_level_type`, `css_class`, `list_class`,`is_default`, `is_del`, `tenant_id`, `create_user`)
VALUES ( '自定义审批人1', 'zdysp10', NULL, NULL, 'udr', NULL, NULL, NULL, 'N', 0, '', '系统');
-- ----------------------------
-- Records of t_user
-- 表字段很多,大部分是为了demo展示使用,引擎用到的核心数据字段是id和name,其它的都是非必须
-- 关于用户表demo数据的使用说明
-- t_user,t_role,t_department表都是测试数据,实际使用中,一般用户系统里面都会有基本的用户表,角色表,部门表,审批的时候用户可以根据实际情况去使用或者关联使用自己已有系统的表,选择出来的数据结构符合BaseIdTranStruVo结构即可,即有id和name两个字段
-- 初次使用时,用户可以先初始化demo表,看一下流程是否满足自己的业务需求,然后逐步改sql,满足自己的业务需求.Antflow demo里审批人规则特别多,实际上用户可能只需要一个或者多个规则(一般指定人员,直属领导,直接角色就满足了),根据需求实现部分即可,像hrbp有的公司根本没有这个概念,自然也没必要实现
-- 用户实现审批人规则时,查看PersonnelEnum枚举,参照指定人员来实现其它的,实现无非就是改写sql而已,其实很简单,很多用户绕不过来,以为自己不熟悉antflow就不敢改,只要返回的数据结构符合BaseIdTranStruVo实体即可
-- ----------------------------
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (1, '张三', NULL, 'zypqqgc@qq.com', 13, 17, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (2, '李四', NULL, 'zypqqgc@qq.com', 13, 17, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (3, '王五', NULL, 'zypqqgc@qq.com', 13, 17, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (4, '菜六', NULL, 'zypqqgc@qq.com', 13, 17, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (5, '牛七', NULL, 'zypqqgc@qq.com', 13, 17, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (6, '马八', NULL, 'zypqqgc@qq.com', 13, 17, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (7, '李九', NULL, 'zypqqgc@qq.com', 13, 17, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (8, '周十', NULL, 'zypqqgc@qq.com', 13, 17, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (9, '肖十一', NULL, 'zypqqgc@qq.com', 13, 17, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (10, '令狐冲', NULL, 'zypqqgc@qq.com', 13, 17, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (11, '风清扬', NULL, 'zypqqgc@qq.com', 18, 19, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (12, '刘正风', NULL, 'zypqqgc@qq.com', 18, 19, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (13, '岳不群', NULL, 'zypqqgc@qq.com', 18, 19, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (14, '宁中则', NULL, 'zypqqgc@qq.com', 18, 19, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (15, '桃谷六仙', NULL, 'zypqqgc@qq.com', 18, 19, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (16, '不介和尚', NULL, 'zypqqgc@qq.com', 18, 19, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (17, '丁一师太', NULL, 'zypqqgc@qq.com', 18, 19, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (18, '依林师妹', NULL, 'zypqqgc@qq.com', 18, 19, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (19, '邱灵珊', NULL, 'zypqqgc@qq.com', 18, 19, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (20, '任盈盈', NULL, 'zypqqgc@qq.com', 18, 19, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (235, '斯克', NULL, 'zypqqgc@qq.com', 18, 19, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (237, '川普', NULL, 'zypqqgc@qq.com', 18, 19, 0, NULL, 0, NULL, 9);
INSERT INTO `t_user`(`id`, `user_name`, `mobile`, `email`, `leader_id`, `hrbp_id`, `mobile_is_show`, `path`, `is_del`, `head_img`, `department_id`) VALUES (1001, '小马', NULL, 'zypqqgc@qq.com', 18, 19, 0, NULL, 0, NULL, 9);

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES (1, '审核管理员');
INSERT INTO `t_role` VALUES (2, '招商事业部');
INSERT INTO `t_role` VALUES (3, '互联网部门');
INSERT INTO `t_role` VALUES (4, '销售部');
INSERT INTO `t_role` VALUES (5, '战区一');
INSERT INTO `t_role` VALUES (6, '战区二');
INSERT INTO `t_role` VALUES (7, 'JAVA开发');
INSERT INTO `t_role` VALUES (8, '测试审批角色');

-- ----------------------------
-- Records of t_user_role
-- ----------------------------
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (1, 1, 1);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (2, 1, 1);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (3, 1, 3);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (4, 2, 2);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (5, 2, 3);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (6, 2, 3);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (7, 3, 3);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (8, 4, 3);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (9, 5, 3);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (10, 6, 3);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (11, 7, 3);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (12, 11, 3);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (13, 10, 6);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (14, 8, 7);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (15, 19, 8);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (16, 12, 4);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (17, 13, 5);
INSERT INTO `t_user_role`(`id`, `user_id`, `role_id`) VALUES (18, 16, 4);


-- ----------------------------
-- Records of t_department --表字段很多,大部分是为了demo展示使用,引擎用到的核心数据字段是id和name,其它的都是非必须
-- ----------------------------
INSERT INTO `t_department`(`id`, `name`, `short_name`, `parent_id`, `path`, `level`, `leader_id`, `sort`, `is_del`, `is_hide`, `create_user`, `update_user`, `create_time`, `update_time`) VALUES (1, '一级部门', NULL, NULL, '/1', 1, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `t_department`(`id`, `name`, `short_name`, `parent_id`, `path`, `level`, `leader_id`, `sort`, `is_del`, `is_hide`, `create_user`, `update_user`, `create_time`, `update_time`) VALUES (2, '二级部门', NULL, 3, '/1/2', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `t_department`(`id`, `name`, `short_name`, `parent_id`, `path`, `level`, `leader_id`, `sort`, `is_del`, `is_hide`, `create_user`, `update_user`, `create_time`, `update_time`) VALUES (3, '三级部门', NULL, 4, '/1/2/3', 3, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `t_department`(`id`, `name`, `short_name`, `parent_id`, `path`, `level`, `leader_id`, `sort`, `is_del`, `is_hide`, `create_user`, `update_user`, `create_time`, `update_time`) VALUES (4, '四级部门', NULL, 5, '/1/2/3/4', 4, 4, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `t_department`(`id`, `name`, `short_name`, `parent_id`, `path`, `level`, `leader_id`, `sort`, `is_del`, `is_hide`, `create_user`, `update_user`, `create_time`, `update_time`) VALUES (5, '五级部门', NULL, 6, '/1/2/3/4/5', 5, 5, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `t_department`(`id`, `name`, `short_name`, `parent_id`, `path`, `level`, `leader_id`, `sort`, `is_del`, `is_hide`, `create_user`, `update_user`, `create_time`, `update_time`) VALUES (6, '六级部门', NULL, 7, '/1/2/3/4/5/6', 6, 6, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `t_department`(`id`, `name`, `short_name`, `parent_id`, `path`, `level`, `leader_id`, `sort`, `is_del`, `is_hide`, `create_user`, `update_user`, `create_time`, `update_time`) VALUES (7, '七级部门', NULL, 8, '/1/2/3/4/5/6/7', 7, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `t_department`(`id`, `name`, `short_name`, `parent_id`, `path`, `level`, `leader_id`, `sort`, `is_del`, `is_hide`, `create_user`, `update_user`, `create_time`, `update_time`) VALUES (8, '市场部', NULL, 9, '/1/2/3/4/5/6/7/8', 8, 8, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `t_department`(`id`, `name`, `short_name`, `parent_id`, `path`, `level`, `leader_id`, `sort`, `is_del`, `is_hide`, `create_user`, `update_user`, `create_time`, `update_time`) VALUES (9, '销售部', NULL, 9, '/1/2/3/4/5/6/7/8/9', 9, 9, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

