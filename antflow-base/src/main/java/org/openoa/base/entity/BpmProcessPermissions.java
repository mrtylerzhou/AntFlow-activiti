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
     * 授权对象类型 1=人员 2=部门 3=角色
     */
    @TableField("object_type")
    private Integer objectType;
    /**
     * 授权对象 id(人员id/部门id/角色id)
     */
    @TableField("object_id")
    private String objectId;
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

    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
}
