package org.openoa.base.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.interf.TenantField;

import java.io.Serializable;
import java.util.Date;

/**
 * sys version entity
 * @author tylerZhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_sys_version")
public class SysVersion  implements TenantField, Serializable {
    /**
     * is published 0 for published 1 for unpublished(hidden)
     */
    public static final Integer HIDE_STATUS_0 = 0;
    public static final Integer HIDE_STATUS_1 = 1;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    /**
     * 0 for normal,1 for deleted
     */
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    /**
     * version
     */
    private String version;
    /**
     * version description
     */
    private String description;
    /**
     * index
     */
    private Integer index;
    /**
     * force update 0 for no,1 for yes
     */
    @TableField("is_force")
    private Integer isForce;
    @TableField("android_url")
    private String androidUrl;
    @TableField("ios_url")
    private String iosUrl;
    /**
     * create user
     */
    @TableField("create_user")
    private String createUser;
    /**
     * update user
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * 0 for not hide and 1 for hide
     */
    @TableField("is_hide")
    private Integer isHide;

    @TableField("download_code")
    private String downloadCode;

    @TableField("effective_time")
    private Date effectiveTime;

}
