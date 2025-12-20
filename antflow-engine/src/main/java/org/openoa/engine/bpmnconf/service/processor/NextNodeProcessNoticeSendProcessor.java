package org.openoa.engine.bpmnconf.service.processor;

import org.activiti.engine.delegate.DelegateTask;
import org.openoa.base.constant.enums.EventTypeEnum;
import org.openoa.base.constant.enums.ProcessNoticeEnum;
import org.openoa.base.dto.BpmNextTaskDto;
import org.openoa.base.vo.ActivitiBpmMsgVo;
import org.openoa.base.vo.BpmVariableMessageVo;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.service.biz.BpmVariableMessageListenerServiceImpl;
import org.openoa.engine.utils.ActivitiTemplateMsgUtils;
import org.openoa.engine.vo.ProcessInforVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class NextNodeProcessNoticeSendProcessor implements AntFlowNextNodeBeforeWriteProcessor{
    @Resource
    private BpmVariableMessageListenerServiceImpl bpmVariableMessageListenerService;
    @Resource
    private ProcessBusinessContans processBusinessContans;

    @Override
    public void postProcess(BpmNextTaskDto bpmNextTaskDto) {

        DelegateTask delegateTask = bpmNextTaskDto.getDelegateTask();
        String formCode = bpmNextTaskDto.getFormCode();
        String processNumber = delegateTask.getProcessInstanceId();
        String bpmnCode = bpmNextTaskDto.getBpmnCode();
        String bpmnName = bpmNextTaskDto.getBpmnName();
        Boolean isOutside = bpmNextTaskDto.getIsOutSide();
        BpmVariableMessageVo bpmVariableMessageVo = BpmVariableMessageVo
                .builder()
                .processNumber(processNumber)
                .formCode(formCode)
                .eventType(EventTypeEnum.PROCESS_FLOW.getCode())
                .messageType(Boolean.TRUE.equals(EventTypeEnum.PROCESS_FLOW.getIsInNode()) ? 2 : 1)
                .elementId(delegateTask.getTaskDefinitionKey())
                .assignee(delegateTask.getAssignee())
                .taskId(delegateTask.getId())
                .eventTypeEnum(EventTypeEnum.PROCESS_FLOW)
                .type(2)
                .delegateTask(delegateTask)
                .build();

        if (bpmVariableMessageListenerService.listenerCheckIsSendByTemplate(bpmVariableMessageVo)) {
            //set is outside
            bpmVariableMessageVo.setIsOutside(isOutside);

            //set template message
            bpmVariableMessageListenerService.listenerSendTemplateMessages(bpmVariableMessageVo);
        } else {
            ProcessInforVo processInforVo = ProcessInforVo
                    .builder()
                    .processinessKey(bpmnCode)
                    .businessNumber(processNumber)
                    .formCode(formCode)
                    .type(2)
                    .build();

            ActivitiTemplateMsgUtils.sendBpmApprovalMsg(
                    ActivitiBpmMsgVo
                            .builder()
                            .userId(delegateTask.getAssignee())
                            .processId(processNumber)
                            .bpmnCode(bpmnCode)
                            .formCode(formCode)
                            .processType("")//todo set process type
                            .processName(bpmnName)
                            .emailUrl(processBusinessContans.getRoute(ProcessNoticeEnum.EMAIL_TYPE.getCode(),
                                    processInforVo , isOutside))
                            .url(processBusinessContans.getRoute(ProcessNoticeEnum.EMAIL_TYPE.getCode(),
                                    processInforVo, isOutside))
                            .appPushUrl(processBusinessContans.getRoute(ProcessNoticeEnum.APP_TYPE.getCode(),
                                    processInforVo, isOutside))
                            .taskId(delegateTask.getProcessInstanceId())
                            .build());
        }
    }

    @Override
    public int order() {
        return 2;
    }
}
