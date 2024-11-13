package org.openoa.base.vo;

import lombok.Getter;
import lombok.Setter;
import org.openoa.base.constant.enums.MsgProcessEventEnum;

import java.util.Date;

/**
 * 工作流
 * @Author tylerzhou
 */
@Getter
@Setter
public class MqProcessEventVo {
    /**
     * 流程编号(必填)
     *
     */
    private String processCode;
    /**
     * 流程formCode
     */
    private String formCode;
    /**
     * 流程实例id
     */
    private String procInstId;
    /**
     * 业务表businessId
     */
    private String businessId;
    /**
     * 流程任务节点的id
     */
    private String taskId;
    /**
     * 操作时间
     */
    private Date opTime;
    /**
     * 按钮事件类型 (必填)
     * @see MsgProcessEventEnum
     */
    private Integer buttonOperationType;
    /**
     * 触发当前按钮事件人员id
     */
    private String operationUserId;
}
