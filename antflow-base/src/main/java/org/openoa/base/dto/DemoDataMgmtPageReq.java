package org.openoa.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 演示数据-人员/部门/角色管理 列表分页请求
 * <p>通用请求体:不同列表按需使用对应字段</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoDataMgmtPageReq implements Serializable {

    private PageDto pageDto;

    /**
     * 人员管理:姓名(模糊)
     */
    private String userName;

    /**
     * 人员管理:手机号(模糊)
     */
    private String mobile;

    /**
     * 部门管理:部门名称(模糊)
     */
    private String deptName;

    /**
     * 角色管理:角色名称(模糊)
     */
    private String roleName;

    /**
     * 角色详情:角色ID(查看角色下人员)
     */
    private Long roleId;
}
