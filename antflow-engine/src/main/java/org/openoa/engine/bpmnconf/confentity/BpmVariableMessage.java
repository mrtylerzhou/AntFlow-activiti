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
 * @Classname BpmVariableMessage
 * @Description variable message
 * @Date 2021-11-27 15:42
 * @Created by AntOffice
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_bpm_variable_message")
public class BpmVariableMessage {


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
    /**
     * 1 for out of node message, 2 for node inner message
     */
    @TableField("message_type")
    private Integer messageType;
    /**
     * event type
     */
    @TableField("event_type")
    private Integer eventType;
    /**
     * notice content
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