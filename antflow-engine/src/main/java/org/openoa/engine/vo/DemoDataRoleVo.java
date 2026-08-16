package org.openoa.engine.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 演示数据-角色管理 列表 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoDataRoleVo implements Serializable {

    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色下关联人员数量
     */
    private Long userCount;
}
