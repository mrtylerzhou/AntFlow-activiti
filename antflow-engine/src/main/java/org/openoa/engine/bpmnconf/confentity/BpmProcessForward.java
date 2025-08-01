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
@TableName("bpm_process_forward")
public class BpmProcessForward implements TenantField, Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * forward user id
     */
    @TableField("forward_user_id")
    private String forwardUserId;
    /**
     * forward user's name
     */
    @TableField("Forward_user_name")
    private String ForwardUserName;
    /**
     * process instance id
     */
    @TableField("processInstance_Id")
    private String processInstanceId;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * create user id
     */
    @TableField("create_user_id")
    private String createUserId;
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    /**
     * is read
     */
    @TableField("is_read")
    private Integer isRead;
    /**
     * task id
     */
    @TableField("task_id")
    private String taskId;

    /**
     * process number
     */
    @TableField("process_number")
    private String processNumber;
    @TableField("node_id")
    private String nodeId;

}
