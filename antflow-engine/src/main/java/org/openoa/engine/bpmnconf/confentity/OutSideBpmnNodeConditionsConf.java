package org.openoa.engine.bpmnconf.confentity;


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
 * third party process service,node conditions conf
 * @author tylerZhou
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_out_side_bpmn_node_conditions_conf")
public class OutSideBpmnNodeConditionsConf implements TenantField, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * auto increment id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * node id
     */
    @TableField("bpmn_node_id")
    private Long bpmnNodeId;
    /**
     * outSide Conditions id
     */
    @TableField("out_side_id")
    private String outSideId;
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
     * as its name says
     */
    @TableField("create_user")
    private String createUser;
    /**
     * as its name says
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * as its name says
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * as its name says
     */
    @TableField("update_time")
    private Date updateTime;

}
