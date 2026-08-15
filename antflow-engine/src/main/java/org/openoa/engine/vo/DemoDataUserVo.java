package org.openoa.engine.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 演示数据-人员管理 列表 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoDataUserVo implements Serializable {

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
     * 部门ID
     */
    private Long departmentId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 直属领导ID
     */
    private Long leaderId;

    /**
     * 直属领导姓名
     */
    private String leaderName;

    /**
     * HRBP ID
     */
    private Long hrbpId;

    /**
     * HRBP 姓名
     */
    private String hrbpName;

    /**
     * 是否删除:0正常 1删除
     */
    private Integer isDel;
}

