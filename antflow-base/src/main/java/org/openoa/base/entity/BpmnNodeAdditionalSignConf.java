package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.openoa.base.interf.TenantField;

import java.io.Serializable;
import java.util.Date;

/**
 * 节点附加签核配置
 */
@Data
@TableName("t_bpmn_node_additional_sign_conf")
public class BpmnNodeAdditionalSignConf implements TenantField, Serializable {

    /**
     * d
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 节点id
     */
    @TableField("bpmn_node_id")
    private Long bpmnNodeId;

    /**
     * additional sign id stored as json value
     */
    @TableField("sign_infos")
    private String signInfos;

    /**
     * see NodePropertyEnum
     */
    @TableField("sign_property")
    private Integer signProperty;

    /**
     * 1 for add,2 for exclude
     */
    @TableField("sign_property_type")
    private Integer signPropertyType;

    /**
     * sign type 1: all sign 2:or sign,it is meaning less for additional sign,it will inhere from parent
     */
    @TableField("sign_type")
    private Integer signType;

    /**
     * remark
     */
    @TableField("remark")
    private String remark;

    /**
     * 0:no,1:yes
     */
    @TableField("is_del")
    private Integer isDel;

    /**
     * tenantId
     */
    @TableField("tenant_id")
    private String tenantId;

    /**
     * as its name says
     */
    @TableField("create_user")
    private String createUser;

    /**
     * as its name says
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * as its name says
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * as its name says
     */
    @TableField(value = "update_time")
    private Date updateTime;
}