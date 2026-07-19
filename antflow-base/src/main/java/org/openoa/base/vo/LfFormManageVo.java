package org.openoa.base.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 独立表单管理 VO
 */
@Data
public class LfFormManageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表单版本ID (t_bpmn_conf_lf_formdata.id)
     */
    private Long id;

    /**
     * 家族标识（同族各版本共享）
     */
    private String formCode;

    /**
     * 表单显示名
     */
    private String formName;

    /**
     * 表单数据 JSON
     */
    private String formdata;

    /**
     * 是否当前生效版本 0否 1是
     */
    private Integer effectiveStatus;

    /**
     * 模糊搜索关键字
     */
    private String search;

    private String createUser;
    private Date createTime;
    private String updateUser;
    private Date updateTime;

    /**
     * 该家族的版本总数（列表展示用）
     */
    private Integer versionCount;
}
