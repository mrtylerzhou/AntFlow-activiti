package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.entity.BpmVariableApproveRemind;

import java.io.Serializable;
import java.util.List;

/**
 * @author AntFlow
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnTimeoutReminderVariableVo implements Serializable {

    /**
     * variable id
     */
    private Long id;
    /**
     * process number
     */
    private String processNum;
    /**
     * process name
     */
    private String processName;
    /**
     * process desc
     */
    private String processDesc;
    /**
     *process start conditions in json format
     */
    private String processStartConditions;
    /**
     * bpmn code
     */
    private String bpmnCode;

    /**
     * process key
     */
    private String processinessKey;

    /**
     * business id
     */
    private String businessId;

    /**
     * entry id
     */
    private String entryId;

    /**
     * a list of reminders
     */
    private List<BpmVariableApproveRemind> bpmVariableApproveReminds;

    /**
     * bpmn name
     */
    private String bpmnName;

    /**
     * process number
     */
    private String processNumber;

    /**
     * start user
     */
    private String startUser;

    /**
     * approval employee
     */
    private String approvalEmpl;

    /**
     * apply date
     */
    private String applyDate;

    /**
     * apply tim
     */
    private String applyTime;

    /**
     * current assignee
     */
    private String assignee;

}
