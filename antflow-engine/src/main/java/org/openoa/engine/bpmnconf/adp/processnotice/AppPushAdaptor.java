package org.openoa.engine.bpmnconf.adp.processnotice;

import org.openoa.base.constant.enums.MessageSendTypeEnum;
import org.openoa.base.vo.BaseMsgInfo;
import org.openoa.base.vo.UserMsgVo;
import org.openoa.engine.utils.UserMsgUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AppPushAdaptor  extends AbstractMessageSendAdaptor<BaseMsgInfo>{
    @Override
    public void sendMessageBatchByType(List<UserMsgVo> userMsgVos) {
        Map<String, BaseMsgInfo> stringBaseMsgInfoMap = super.messageProcessing(userMsgVos, UserMsgUtils::buildBaseMsgInfo);
        messageService.sendAppPushBatch(stringBaseMsgInfoMap);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(MessageSendTypeEnum.PUSH);
    }
}
