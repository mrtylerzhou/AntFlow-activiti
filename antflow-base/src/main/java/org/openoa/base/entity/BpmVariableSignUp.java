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
 * variable sign up
 * @since 0.5
 * @author AntFlow
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_bpm_variable_sign_up")
public class BpmVariableSignUp implements TenantField, Serializable {


    /**
     * auto incr id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * variable id
     */
    @TableField("variable_id")
    private Long variableId;
    /**
     * element id
     */
    @TableField("element_id")
    private String elementId;
    @TableField("node_id")
    private String nodeId;
    /**
     * after sign up way(1 comback to sign up user;2 not callback to sign up user)
     */
    @TableField("after_sign_up_way")
    private Integer afterSignUpWay;
    /**
     * sign up elements as json
     */
    @TableField("sub_elements")
    private String subElements;
    /**
     * remark
     */
    private String remark;
    /**
     * 0 for normal,1 for delete
     */
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
     * update user
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;

}