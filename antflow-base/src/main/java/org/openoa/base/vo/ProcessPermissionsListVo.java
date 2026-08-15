package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程权限管理列表行
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPermissionsListVo implements Serializable {

    private Long id;
    /**
     * 流程 formCode
     */
    private String processKey;
    /**
     * 流程名称(后置处理补全)
     */
    private String bpmnName;
    /**
     * 权限类型 1查看 2创建 3监控
     */
    private Integer permissionsType;
    /**
     * 授权对象类型 true=部门 false=人员(兼容旧字段)
     */
    private Boolean isDepartment;
    /**
     * 授权对象类型 1=人员 2=部门 3=角色
     */
    private Integer objectType;
    /**
     * 授权对象名称(后置处理补全)
     */
    private String objectName;
    /**
     * 创建人 id
     */
    private String createUser;
    /**
     * 创建人名称(后置处理补全)
     */
    private String createUserName;
    /**
     * 创建时间
     */
    private Date createTime;
}
