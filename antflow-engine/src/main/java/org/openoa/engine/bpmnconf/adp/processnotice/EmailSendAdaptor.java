package org.openoa.engine.bpmnconf.adp.processnotice;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.MessageSendTypeEnum;
import org.openoa.base.util.MailUtils;
import org.openoa.base.vo.MailInfo;
import org.openoa.base.vo.UserMsgVo;
import org.openoa.engine.bpmnconf.util.UserMsgUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class EmailSendAdaptor extends AbstractMessageSendAdaptor<MailInfo>{
    @Override
    public void sendMessageBatchByType(List<UserMsgVo> userMsgVos) {
        Map<String, MailInfo> stringMailInfoMap = super.messageProcessing(userMsgVos, UserMsgUtils::buildMailInfo);
        messageService.sendMailBatch(stringMailInfoMap);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(MessageSendTypeEnum.MAIL);
    }
}
