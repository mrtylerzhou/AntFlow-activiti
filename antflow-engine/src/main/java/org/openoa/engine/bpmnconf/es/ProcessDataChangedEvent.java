package org.openoa.engine.bpmnconf.es;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Spring event published when a workflow operation changes process data.
 *
 * @Author tylerzhou
 */
@Getter
public class ProcessDataChangedEvent extends ApplicationEvent {

    private final String processNumber;
    private final String formCode;
    private final String businessId;
    private final String taskId;
    /**
     * Maps to MsgProcessEventEnum codes.
     * @see org.openoa.base.constant.enums.MsgProcessEventEnum
     */
    private final Integer operationType;

    public ProcessDataChangedEvent(Object source, String processNumber, String formCode,
                                   String businessId, String taskId, Integer operationType) {
        super(source);
        this.processNumber = processNumber;
        this.formCode = formCode;
        this.businessId = businessId;
        this.taskId = taskId;
        this.operationType = operationType;
    }
}
