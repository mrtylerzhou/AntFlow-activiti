package org.openoa.engine.bpmnconf.service.biz;


import org.openoa.base.constant.enums.EventTypeEnum;
import org.openoa.base.constant.enums.ProcessNoticeEnum;
import org.openoa.base.vo.ActivitiBpmMsgVo;
import org.openoa.base.vo.BpmVariableMessageVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableMessageBizService;
import org.openoa.engine.utils.ActivitiTemplateMsgUtils;
import org.openoa.engine.vo.ProcessInforVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BpmVariableMessageListenerServiceImpl {

    @Autowired
    private BpmVariableMessageBizService bpmVariableMessageBizService;
    @Autowired
    private ProcessBusinessContans processBusinessContans;

    /**
     * check whether send by template
     *
     * @param bpmVariableMessageVo
     * @return
     */
    public Boolean listenerCheckIsSendByTemplate(BpmVariableMessageVo bpmVariableMessageVo) {
        return bpmVariableMessageBizService.checkIsSendByTemplate(bpmVariableMessageVo);
    }

    /**
     * 监听发送模板消息
     *
     * @param bpmVariableMessageVo
     */
    public void listenerSendTemplateMessages(BpmVariableMessageVo bpmVariableMessageVo) {
        //build variable message
        BpmVariableMessageVo vo = bpmVariableMessageBizService.getBpmVariableMessageVo(bpmVariableMessageVo);
        //send template message
        if (vo!=null) {
            bpmVariableMessageBizService.sendTemplateMessages(vo);
        }
    }

    public void sendProcessMessages(EventTypeEnum eventTypeEnum, BusinessDataVo vo){
        String processNumber=vo.getProcessNumber();
        String formCode = vo.getFormCode();
        String startUserId=vo.getStartUserId();
        boolean isOutside=Boolean.TRUE.equals(vo.getIsOutSideAccessProc());
        BpmVariableMessageVo bpmVariableMessageVo = BpmVariableMessageVo
                .builder()
                .processNumber(processNumber)
                .formCode(formCode)
                .eventType(eventTypeEnum.getCode())
                .messageType(Boolean.TRUE.equals(eventTypeEnum.getIsInNode()) ? 2 : 1)
                .eventTypeEnum(eventTypeEnum)
                .type(1)
                .build();
        Boolean sendByTemplate = listenerCheckIsSendByTemplate(bpmVariableMessageVo);
        if(Boolean.TRUE.equals(sendByTemplate)){
            bpmVariableMessageVo.setIsOutside(isOutside);

            this.listenerSendTemplateMessages(bpmVariableMessageVo);
        }else{
            ProcessInforVo processInforVo = ProcessInforVo
                    .builder()
                    .businessNumber(processNumber)
                    .formCode(formCode)
                    .type(1)
                    .build();
            ActivitiTemplateMsgUtils.sendBpmApprovalMsg(
                    ActivitiBpmMsgVo
                            .builder()
                            .userId(startUserId)
                            .processId(processNumber)
                            .formCode(formCode)
                            .processType("")//todo set process type
                            .emailUrl(processBusinessContans.getRoute(ProcessNoticeEnum.EMAIL_TYPE.getCode(),
                                    processInforVo , vo.getIsOutSideAccessProc()))
                            .url(processBusinessContans.getRoute(ProcessNoticeEnum.EMAIL_TYPE.getCode(),
                                    processInforVo, isOutside))
                            .appPushUrl(processBusinessContans.getRoute(ProcessNoticeEnum.APP_TYPE.getCode(),
                                    processInforVo, isOutside))
                            .taskId(null)
                            .build());
        }
    }
}
