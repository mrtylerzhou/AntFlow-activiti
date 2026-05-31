package org.openoa.base.vo;

import lombok.Getter;
import lombok.Setter;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.util.DateUtil;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Process comprehensive data DTO, for pushing to Elasticsearch.
 *
 * @Author tylerzhou
 */
@Getter
@Setter
public class ProcessDataESDto {

    public static final String FIELD_PROCESS_STATE = "processState";
    public static final String FIELD_CURRENT_PROCESSING_USERS = "currentProcessingUsers";

    /**
     * process code (business number)
     */
    private String processCode;
    /**
     * process digest/summary
     */
    private String processDigest;
    /**
     * process state
     *
     * @see ProcessStateEnum
     */
    private Integer processState;
    /**
     * process business state (for extended state tracking)
     */
    private Integer processBusinessState;
    /**
     * process initiator user id
     */
    private String startUserId;
    /**
     * process initiator user name
     */
    private String startUserName;
    /**
     * applicant employee id (defaults to process initiator)
     */
    private String applyEmployeeId;
    /**
     * applicant employee name
     */
    private String applyEmployeeName;
    /**
     * applicant department id
     */
    private String applyEmployeeDeptId;
    /**
     * applicant department name
     */
    private String applyEmployeeDeptName;
    /**
     * current processing user ids
     */
    private List<String> currentProcessingUsers;
    /**
     * current processing user names
     */
    private List<String> currentProcessingUserNames;
    /**
     * already processed user ids
     */
    private List<String> alreadyProcessedUsers;
    /**
     * back-to-modify related user id
     */
    private String backToModifyRelatedUser;
    /**
     * all process related user ids (all approvers)
     */
    private List<String> allProcessRelatedUsers;

    /**
     * process create time
     */
    private String processCreateTime;
    /**
     * process update time
     */
    private String processUpdateTime;
    /**
     * ES record create time
     */
    private String createTime;
    /**
     * ES record update time
     */
    private String updateTime;
    /**
     * process form code
     */
    private String formCode;
    /**
     * process key
     */
    private String processKey;
    /**
     * process description
     */
    private String description;

    /**
     * current node's task create time
     */
    private String currentTaskCreateTime;
    /**
     * current node name
     */
    private String currentNodeName;
    /**
     * current task ids
     */
    private List<String> currentTaskIds;
    /**
     * current task definition key
     */
    private String currentTaskDefKey;

    /**
     * current operation type
     *
     * @see org.openoa.base.constant.enums.MsgProcessEventEnum
     */
    private Integer currentType;
    /**
     * current operation type description
     */
    private String currentTypeDesc;

    /**
     * process instance id (Activiti)
     */
    private String processInstanceId;
    /**
     * soft delete flag
     */
    private Integer isDel = 0;
    /**
     * historic task list
     */
    private List<HisTaskVo> taskList;

    /**
     * approval user id
     */
    private String approvalUserId;
    /**
     * approval user name
     */
    private String approvalUserName;

    /**
     * Populate basic fields from BpmBusinessProcess.
     */
    public void of(BpmBusinessProcess businessProcess) {
        this.processCode = businessProcess.getBusinessNumber();
        this.processDigest = businessProcess.getProcessDigest();
        this.processState = businessProcess.getProcessState();
        this.processBusinessState = businessProcess.getProcessState();
        this.startUserId = businessProcess.getCreateUser();
        this.processCreateTime = formatDateTime(businessProcess.getCreateTime());
        this.processUpdateTime = formatDateTime(businessProcess.getUpdateTime());
        this.processKey = businessProcess.getProcessinessKey();
        this.description = businessProcess.getDescription();
        this.isDel = businessProcess.getIsDel();
        this.processInstanceId = businessProcess.getProcInstId();
    }

    /**
     * Populate current task info from TaskMgmtVO list.
     */
    public void of(List<TaskMgmtVO> taskMgmtVOS) {
        if (taskMgmtVOS == null || taskMgmtVOS.isEmpty()) {
            return;
        }
        TaskMgmtVO taskMgmtVO = taskMgmtVOS.get(0);
        this.currentTaskCreateTime = formatDateTime(taskMgmtVO.getCreateTime());
        this.currentNodeName = taskMgmtVO.getTaskName();
        this.currentTaskIds = taskMgmtVOS.stream().map(TaskMgmtVO::getTaskId).collect(Collectors.toList());
    }

    private String formatDateTime(Date date) {
        return date == null ? null : DateUtil.SDF_DATETIME_PATTERN.format(date);
    }
}
