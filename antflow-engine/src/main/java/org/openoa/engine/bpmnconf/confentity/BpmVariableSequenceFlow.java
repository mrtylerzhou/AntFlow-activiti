package org.openoa.engine.bpmnconf.confentity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Classname BpmVariableSequenceFlow
 * @Description TODO
 * @Date 2021-11-27 15:36
 * @Created by AntOffice
 * @since 0.5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_bpm_variable_sequence_flow")
public class BpmVariableSequenceFlow {


    /**
     * aut incr id
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
    /**
     * element name
     */
    @TableField("element_name")
    private String elementName;
    /**
     * element start flow id
     */
    @TableField("element_from_id")
    private String elementFromId;
    /**
     * 连线连接走向节点编号
     * element to id
     */
    @TableField("element_to_id")
    private String elementToId;
    /**
     * flow type 1 has no param 2 has param
     */
    @TableField("sequence_flow_type")
    private Integer sequenceFlowType;
    /**
     * flow conditions
     */
    @TableField("sequence_flow_conditions")
    private String sequenceFlowConditions;
    /**
     * remark
     */
    private String remark;
    /**
     *0 for no delete 1 for delete
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