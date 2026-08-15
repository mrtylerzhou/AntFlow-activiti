package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 流程权限管理批量保存请求
 * 三层笛卡尔积: processKeys × 授权对象(objectIds) × permissionsTypes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPermissionsSaveVo implements Serializable {

    /**
     * 流程 formCode 集合
     */
    private List<String> processKeys;
    /**
     * 权限类型集合 1查看 2创建 3监控
     */
    private List<Integer> permissionsTypes;
    /**
     * 授权对象类型 true=部门权限 false=人员权限(兼容旧调用,新调用请使用 objectType)
     */
    private Boolean isDepartment;
    /**
     * 授权对象类型 1=人员 2=部门 3=角色(优先于 isDepartment)
     */
    private Integer objectType;
    /**
     * 授权对象 id 集合(人员id/部门id/角色id,与 objectType 对应)
     */
    private List<String> objectIds;
}
