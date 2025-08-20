package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openoa.base.interf.*;

import java.io.Serializable;
import java.util.Date;

/**
 * specified role approver configs
 */
@Data
@TableName("t_bpmn_node_udr_conf")
public class BpmnNodeUDRConf implements BpmnNodeIdField, BpmnNodeSignTypeField, CreateUpdateField, IsDelField, TenantField,Serializable {

    private static final long serialVersionUID = 1L;

    /** auto incr id */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** node id */
    @TableField("bpmn_node_id")
    private Long bpmnNodeId;

    /** value as json array */
    @TableField("value_json")
    private String valueJson;

    /** sign type 1 all sign,2 or sign */
    @TableField("sign_type")
    private Integer signType;


    @TableField("udr_property")
    private String udrProperty;
    @TableField("urd_property_name")
    private String udrPropertyName;
    /*自定义扩展字段*/
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;

    /** remark */
    @TableField("remark")
    private String remark;

    /** 0:normal,1:deleted */
    @TableField("is_del")
    private Integer isDel;

    /** tenantId */
    @TableField("tenant_id")
    private String tenantId;

    /** create user */
    @TableField("create_user")
    private String createUser;

    /** create time */
    @TableField(value = "create_time")
    private Date createTime;

    /** update user */
    @TableField("update_user")
    private String updateUser;

    /** update time */
    @TableField(value = "update_time")
    private Date updateTime;
}