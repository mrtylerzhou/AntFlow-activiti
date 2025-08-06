package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openoa.base.interf.TenantField;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("t_bpmn_node_labels")
public class BpmnNodeLabel implements TenantField, Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("nodeid")
    private Long nodeId;

    @TableField("label_name")
    private String labelName;

    @TableField("label_value")
    private String labelValue;

    @TableField("remark")
    private String remark;

    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;

    @TableField("create_user")
    private String createUser;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_user")
    private String updateUser;

    @TableField("update_time")
    private Date updateTime;
}