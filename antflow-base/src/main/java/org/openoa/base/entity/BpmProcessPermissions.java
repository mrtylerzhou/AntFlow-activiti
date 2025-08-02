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
 * process permission
 *
 * @author tylerZhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_process_permissions")
public class BpmProcessPermissions implements TenantField, Serializable {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * user id
     */
    @TableField("user_id")
    private String userId;
    /**
     * dept id
     */
    @TableField("dep_id")
    private Long depId;
    /**
     * permission type 1 for view 2 for create 3 for monitor
     */
    @TableField("permissions_type")
    private Integer permissionsType;
    /**
     * create user
     */
    @TableField("create_user")
    private String createUser;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * process key
     */
    @TableField("process_key")
    private String processKey;

    /**
     * office id
     */
    @TableField("office_id")
    private Long officeId;
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
}
