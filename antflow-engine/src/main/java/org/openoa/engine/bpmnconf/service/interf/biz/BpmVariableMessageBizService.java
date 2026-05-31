package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.vo.BpmVariableMessageVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.InformationTemplateVo;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;

public interface BpmVariableMessageBizService {

    Boolean checkIsSendByTemplate(BpmVariableMessageVo vo);

    BpmVariableMessageVo getBpmVariableMessageVo(BpmVariableMessageVo vo);

    Map<String, String> getUrlMap(BpmVariableMessageVo vo, InformationTemplateVo informationTemplateVo);

    BpmVariableMessageVo fromBusinessDataVo(BusinessDataVo businessDataVo);

    @Async
    void sendTemplateMessagesAsync(BpmVariableMessageVo vo);

    void sendTemplateMessages(BpmVariableMessageVo vo);

    void sendTemplateMessages(BusinessDataVo businessDataVo);
}
