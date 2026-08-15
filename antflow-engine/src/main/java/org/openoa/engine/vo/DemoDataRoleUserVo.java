package org.openoa.engine.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 演示数据-角色详情(角色下人员) VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoDataRoleUserVo implements Serializable {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 姓名
     */
    private String userName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门名称
     */
    private String departmentName;
}
