package org.openoa.engine.utils;

import org.openoa.base.constant.enums.MessageSendTypeEnum;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.engine.bpmnconf.service.biz.UserMessagePreServiceImpl;
import org.openoa.base.vo.SendParam;

import java.util.List;

public class MessageUtils {

    public static Boolean sendMessage(SendParam sendParam) {
        UserMessagePreServiceImpl userMessagePreService = SpringBeanUtils.getBean(UserMessagePreServiceImpl.class);
        userMessagePreService.sendMessagePre(sendParam);
        return true;
    }

    /**
     * send message by enum
     *
     * @param sendParam
     * @param messageSendTypeEnum
     * @return
     */
    public static Boolean sendMessage(SendParam sendParam, MessageSendTypeEnum messageSendTypeEnum) {
        UserMessagePreServiceImpl userMessagePreService = SpringBeanUtils.getBean(UserMessagePreServiceImpl.class);
        userMessagePreService.sendMessagePre(sendParam, messageSendTypeEnum);
        return true;
    }

    public static Boolean sendMessageBatch(List<SendParam> sendParams) {
        UserMessagePreServiceImpl userMessagePreService = SpringBeanUtils.getBean(UserMessagePreServiceImpl.class);
        userMessagePreService.sendMessagePreBatch(sendParams);
        return true;
    }

    /**
     * send messages by enum in batch
     *
     * @param sendParams
     * @param messageSendTypeEnum
     * @return
     */
    public static Boolean sendMessageBatch(List<SendParam> sendParams, MessageSendTypeEnum messageSendTypeEnum) {
        UserMessagePreServiceImpl userMessagePreService = SpringBeanUtils.getBean(UserMessagePreServiceImpl.class);
        userMessagePreService.sendMessagePreBatch(sendParams, messageSendTypeEnum);
        return true;
    }


}
