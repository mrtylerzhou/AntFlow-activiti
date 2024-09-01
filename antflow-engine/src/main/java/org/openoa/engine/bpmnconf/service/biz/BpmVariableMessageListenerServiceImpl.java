package org.openoa.engine.bpmnconf.service.biz;


import org.openoa.engine.bpmnconf.service.impl.BpmVariableMessageServiceImpl;
import org.openoa.engine.vo.BpmVariableMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BpmVariableMessageListenerServiceImpl {

    @Autowired
    private BpmVariableMessageServiceImpl bpmVariableMessageService;

    /**
     * check whether send by template
     *
     * @param bpmVariableMessageVo
     * @return
     */
    public Boolean listenerCheckIsSendByTemplate(BpmVariableMessageVo bpmVariableMessageVo) {
        Boolean checkResult = bpmVariableMessageService.checkIsSendByTemplate(bpmVariableMessageVo);
        return checkResult;
    }

    /**
     * 监听发送模板消息
     *
     * @param bpmVariableMessageVo
     */
    public void listenerSendTemplateMessages(BpmVariableMessageVo bpmVariableMessageVo) {
        //build variable message
        BpmVariableMessageVo vo = bpmVariableMessageService.getBpmVariableMessageVo(bpmVariableMessageVo);
        //send template message
        if (vo!=null) {
            bpmVariableMessageService.sendTemplateMessages(vo);
        }
    }

}
