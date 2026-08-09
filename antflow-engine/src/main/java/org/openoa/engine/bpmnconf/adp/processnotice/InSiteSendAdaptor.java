package org.openoa.engine.bpmnconf.adp.processnotice;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.MessageSendTypeEnum;
import org.openoa.base.vo.UserMsgVo;
import org.openoa.engine.utils.UserMsgUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * In-site message send adaptor for timeout reminder channel dispatch.
 */
@Component
@Slf4j
public class InSiteSendAdaptor extends AbstractMessageSendAdaptor<UserMsgVo> {
    @Override
    public void sendMessageBatchByType(List<UserMsgVo> userMsgVos) {
        if (CollectionUtils.isEmpty(userMsgVos)) {
            log.warn("发送消息消息内容为空!");
            return;
        }
        for (UserMsgVo userMsgVo : userMsgVos) {
            UserMsgUtils.insertUserMessage(userMsgVo);
        }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(MessageSendTypeEnum.IN_SITE);
    }
}
