package org.openoa.engine.bpmnconf.adp.processnotice;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.MessageSendTypeEnum;
import org.openoa.base.vo.MessageInfo;
import org.openoa.base.vo.UserMsgVo;
import org.openoa.engine.utils.UserMsgUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 这里只实现了策略,并没有实现发送短信方法,可以自己引入短信sdk实现短信发送
 */
@Component
@Slf4j
public class SMSSendAdaptor  extends AbstractMessageSendAdaptor<MessageInfo>{
    @Override
    public void sendMessageBatchByType(List<UserMsgVo> userMsgVos) {
        Map<String, MessageInfo> stringMessageInfoMap = super.messageProcessing(userMsgVos, UserMsgUtils::buildMessageInfo);
        messageService.sendSmsBatch(stringMessageInfoMap);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(MessageSendTypeEnum.MESSAGE);
    }
}
