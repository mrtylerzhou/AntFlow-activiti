package org.openoa.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 流程权限管理列表查询请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPermissionsPageReq implements Serializable {

    private PageDto pageDto;
    /**
     * 流程 formCode(模糊)
     */
    private String formCode;
    /**
     * 权限类型 1查看 2创建 3监控
     */
    private Integer permissionsType;
    /**
     * 授权对象名称(人员姓名/部门名称,模糊)
     */
    private String objectName;
}
