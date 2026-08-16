package org.openoa.engine.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 演示数据-部门管理 列表 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoDataDepartmentVo implements Serializable {

    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 简称
     */
    private String shortName;

    /**
     * 上级部门ID
     */
    private Long parentId;

    /**
     * 上级部门名称
     */
    private String parentName;

    /**
     * 负责人ID
     */
    private Long leaderId;

    /**
     * 负责人姓名
     */
    private String leaderName;

    /**
     * 部门层级
     */
    private Integer level;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否删除:0正常 1删除
     */
    private Integer isDel;
}

