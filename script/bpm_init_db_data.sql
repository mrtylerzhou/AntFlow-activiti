
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

