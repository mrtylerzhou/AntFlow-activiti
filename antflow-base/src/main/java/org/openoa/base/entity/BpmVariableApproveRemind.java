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
 * @Classname BpmVariableApproveRemind
 * @Date 2021-11-27 15:47
 * @Created by AntOffice
 * @since 0.5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_bpm_variable_approve_remind")
public class BpmVariableApproveRemind implements TenantField, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * auto increment id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * process variable id
     */
    @TableField("variable_id")
    private Long variableId;
    /**
     * process element id
     */
    @TableField("element_id")
    private String elementId;
    /**
     * remind contents
     */
    private String content;
    /**
     * remark
     */
    private String remark;

    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
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
     * update by user
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;

}