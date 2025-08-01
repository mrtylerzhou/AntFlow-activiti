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
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_bpmn_node_conditions_param_conf")
public class BpmnNodeConditionsParamConf implements TenantField, Serializable {

    /**
     * auto incr id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * conf id
     */
    @TableField("bpmn_node_conditions_id")
    private Long bpmnNodeConditionsId;

    @TableField("condition_param_type")
    private Integer conditionParamType;
    @TableField("condition_param_name")
    private String conditionParamName;
    /**
     * condition param json
     */
    @TableField("condition_param_jsom")
    private String conditionParamJsom;
    private Integer operator;
    @TableField("cond_relation")
    private Integer condRelation;
    @TableField("cond_group")
    private Integer condGroup;
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