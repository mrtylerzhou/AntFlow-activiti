package org.openoa.base.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * Historic task instance VO, for storing task history in ES.
 * Maps to Activiti ACT_HI_TASKINST table.
 *
 * @Author tylerzhou
 */
@Getter
@Setter
public class HisTaskVo {
    /**
     * task id (ACT_HI_TASKINST.ID_)
     */
    private String taskId;
    /**
     * task name (ACT_HI_TASKINST.NAME_)
     */
    private String taskName;
    /**
     * assignee user id (ACT_HI_TASKINST.ASSIGNEE_)
     */
    private String assignee;
    /**
     * task start time (ACT_HI_TASKINST.START_TIME_)
     */
    private String startTime;
    /**
     * task end time (ACT_HI_TASKINST.END_TIME_)
     */
    private String endTime;
    /**
     * duration in millis (ACT_HI_TASKINST.DURATION_)
     */
    private Long durationInMillis;
    /**
     * task definition key (ACT_HI_TASKINST.TASK_DEF_KEY_)
     */
    private String taskDefinitionKey;
    /**
     * process instance id (ACT_HI_TASKINST.PROC_INST_ID_)
     */
    private String processInstanceId;
    /**
     * same as assignee, for ES query filter
     */
    private String userId;
    /**
     * same as endTime, for ES nested sort
     */
    private String processTime;
}
