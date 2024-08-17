package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * time out remind
 * @author AntFlow
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnTimeoutReminderTaskVo implements Serializable {

    /**
     * process instance id
     */
    private String procInstId;

    /**
     * task id
     */
    private String taskId;

    /**
     * element id(TASK_DEF_KEY_)
     */
    private String elementId;

    /**
     * assignee
     */
    private String assignee;

    /**
     * create time
     */
    private Date createTime;

    /**
     * stand by days
     */
    private Integer standbyDay;

}
