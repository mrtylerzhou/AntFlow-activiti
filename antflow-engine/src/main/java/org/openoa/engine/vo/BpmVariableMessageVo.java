package org.openoa.engine.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.activiti.engine.delegate.DelegateTask;
import org.openoa.engine.bpmnconf.constant.enus.EventTypeEnum;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmVariableMessageVo implements Serializable {

    /**
     * id
     */
    private Long id;
    /**
     * process variable id
     */
    private Long variableId;
    /**
     * process flow element id
     */
    private String elementId;
    /**
     * 1-out of node message；2-in node message
     */
    private Integer messageType;
    /**
     * event type
     */
    private Integer eventType;
    /**
     * message content
     */
    private String content;
    /**
     * remark
     */
    private String remark;
    /**
     * 0:valid,1:deleted
     */
    private Integer isDel;
    /**
     * as its name says
     */
    private String createUser;
    /**
     * as its name says
     */
    private Date createTime;
    /**
     * update by name
     */
    private String updateUser;
    /**
     *update time
     */
    private Date updateTime;

    //===============>>ext fields<<===================


    /**
     * process instance id
     */
    private String processInsId;

    /**
     * bpmn code of a process
     */
    private String bpmnCode;

    /**
     * bpmn name of a process
     */
    private String bpmnName;

    /**
     * process number,it is also named process code in other place,for historical reason
     */
    private String processNumber;

    /**
     * process type
     */
    private String processType;

    /**
     * process task id,task is node specified by bpmn
     */
    private String taskId;

    /**
     * formCode
     */
    private String formCode;
    /**
     * operation type
     */
    public Integer type;
    /**
     * the applier
     */
    private String startUser;

    /**
     * current assignee
     */
    private String assignee;

    /**
     * already approved users
     */
    private List<String> approveds;

    /**
     * forwarded to users
     */
    private List<String> forwardUsers;

    /**
     * node sign up users
     */
    private List<String> signUpUsers;

    /**
     * to be implemented
     */
    private String approvalEmplId;

    /**
     * apply date(yyyy-MM-dd without time)
     */
    private String applyDate;

    /**
     * apply time(HH:mm:ss without date)
     */
    private String applyTime;

    /**
     * next node approvals
     */
    private List<String> nextNodeApproveds;

    /**
     * process start up conditions,there are many form fields in a process,but only some of them are used as process start up conditions
     */
    private BpmnStartConditionsVo bpmnStartConditions;

    /**
     * 事件类型枚举
     */
    private EventTypeEnum eventTypeEnum;

    /**
     * is it an outside(third party system started process,not implemented yet at the moment) process
     */
    @Builder.Default
    private Boolean isOutside = false;

    private DelegateTask delegateTask;
}
