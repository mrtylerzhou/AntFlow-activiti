package org.openoa.engine.bpmnconf.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.MessageSendTypeEnum;
import org.openoa.base.entity.UserMessage;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.service.biz.MessageServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.EmployeeServiceImpl;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.MessageSendTypeEnum.*;

public class UserMsgUtils {
    //=================================================发送信息(单个) Start===========================================

    /**
     * 发送信息(单个)
     *
     * @param userMsgVo
     * @param messageSendTypeEnums
     */
    public static void sendMessages(UserMsgVo userMsgVo, MessageSendTypeEnum... messageSendTypeEnums) {

        MessageServiceImpl messageService = getMessageService();

        //执行发送信息
        doSendMessages(userMsgVo, messageService, messageSendTypeEnums);

        //写站内信记录
        insertUserMessage(userMsgVo, messageService);
    }

    /**
     * 发送信息(单个-无站内信)
     *
     * @param userMsgVo
     * @param messageSendTypeEnums
     */
    public static void sendMessagesNoUserMessage(UserMsgVo userMsgVo, MessageSendTypeEnum... messageSendTypeEnums) {
        MessageServiceImpl messageService = getMessageService();

        //执行发送信息
        doSendMessages(userMsgVo, messageService, messageSendTypeEnums);

    }

    /**
     * 记录站内信
     *
     * @param userMsgVo
     */
    public static void insertUserMessage(UserMsgVo userMsgVo) {
        MessageServiceImpl messageService = getMessageService();

        //写站内信记录
        insertUserMessage(userMsgVo, messageService);
    }

    /**
     * 执行发送信息
     *
     * @param userMsgVo
     * @param messageService
     * @param messageSendTypeEnums
     */
    private static void doSendMessages(UserMsgVo userMsgVo, MessageServiceImpl messageService, MessageSendTypeEnum[] messageSendTypeEnums) {
        if (!ObjectUtils.isEmpty(messageSendTypeEnums)) {

            if (!checkEmployeeStatus(userMsgVo.getUserId())) {
                return;
            }

            List<MessageSendTypeEnum> messageSendTypeEnumList = Lists.newArrayList(messageSendTypeEnums);
            //发送类型邮件
            if (messageSendTypeEnumList.contains(MAIL)) {
                sendMail(userMsgVo, messageService);
            }
            //发送类型短信
            if (messageSendTypeEnumList.contains(MESSAGE)) {
                sendSms(userMsgVo, messageService);
            }
            //发送类型App-PUSH
            if (messageSendTypeEnumList.contains(PUSH)) {
                sendAppPush(userMsgVo, messageService);
            }
        }
    }

    /**
     * 发送所有消息(邮件、短信、App-PUSH)
     *
     * @param userMsgVo
     * @param messageService
     */
    private static void sendAllMsg(UserMsgVo userMsgVo, MessageServiceImpl messageService) {
        //发送邮件
        sendMail(userMsgVo, messageService);
        //发送短信
        sendSms(userMsgVo, messageService);
        //发送App推送
        sendAppPush(userMsgVo, messageService);
    }

    /**
     * 获得消息发送服务类
     *
     * @return
     */
    private static MessageServiceImpl getMessageService() {
        return SpringBeanUtils.getBean(MessageServiceImpl.class);
    }

    /**
     * 写站内信记录
     *
     * @param userMsgVo
     * @param messageService
     */
    private static void insertUserMessage(UserMsgVo userMsgVo, MessageServiceImpl messageService) {

        if (!checkEmployeeStatus(userMsgVo.getUserId())) {
            return;
        }

        UserMessage userMessage = buildUserMessage(userMsgVo);
        messageService.insertUserMessage(userMessage);
    }

    /**
     * 发送App推送
     *
     * @param userMsgVo
     * @param messageService
     */
    private static void sendAppPush(UserMsgVo userMsgVo, MessageServiceImpl messageService) {
        BaseMsgInfo baseMsgInfo = buildBaseMsgInfo(userMsgVo);
        messageService.sendAppPush(baseMsgInfo, userMsgVo.getUserId());
    }

    /**
     * 发送短信
     *
     * @param userMsgVo
     * @param messageService
     */
    private static void sendSms(UserMsgVo userMsgVo, MessageServiceImpl messageService) {
        MessageInfo messageInfo = buildMessageInfo(userMsgVo);
        messageService.sendSms(messageInfo, userMsgVo.getUserId());
    }

    /**
     * 发送邮件
     *
     * @param userMsgVo
     * @param messageService
     */
    private static void sendMail(UserMsgVo userMsgVo, MessageServiceImpl messageService) {
        MailInfo mailInfo = buildMailInfo(userMsgVo);
        messageService.sendMail(mailInfo, userMsgVo.getUserId());
    }


    /**
     * 发送信息(批量)
     *
     * @param userMsgBathVos
     */
    public static void sendMessageBath(List<UserMsgBathVo> userMsgBathVos) {

        MessageServiceImpl messageService = getMessageService();

        //执行发送信息(批量)
        doSendMessageBath(userMsgBathVos, messageService);

        //批量写站内信记录
        insertUserMessageBath(userMsgBathVos, messageService);

    }

    /**
     * 发送信息(批量-无站内信)
     *
     * @param userMsgBathVos
     */
    public static void sendMessageBathNoUserMessage(List<UserMsgBathVo> userMsgBathVos) {

        MessageServiceImpl messageService = getMessageService();

        //执行发送信息(批量)
        doSendMessageBath(userMsgBathVos, messageService);

    }

    /**
     * 批量记录站内信
     *
     * @param userMsgBathVos
     */
    public static void insertUserMessageBath(List<UserMsgBathVo> userMsgBathVos) {

        MessageServiceImpl messageService = getMessageService();

        //批量写站内信记录
        insertUserMessageBath(userMsgBathVos, messageService);
    }

    /**
     * 发送信息(批量)-没有批量写站内信记录
     *
     * @param userMsgBathVos
     */
    public static void sendMessageBathNoInsertUserMessageBath(List<UserMsgBathVo> userMsgBathVos) {

        MessageServiceImpl messageService = getMessageService();

        //执行发送信息(批量)
        doSendMessageBath(userMsgBathVos, messageService);

    }

    /**
     * 执行发送信息(批量)
     *
     * @param userMsgBathVos
     * @param messageService
     */
    private static void doSendMessageBath(List<UserMsgBathVo> userMsgBathVos, MessageServiceImpl messageService) {


        //转换批量发送消息入参格式
        Multimap<MessageSendTypeEnum, UserMsgVo> almMap = formatUserMsgBathVos(userMsgBathVos);

        //批量发送邮件
        if (almMap.containsKey(MAIL)) {
            sendMailBath(messageService, almMap);
        }

        //批量发送短消息
        if (almMap.containsKey(MESSAGE)) {
            sendSmsBath(messageService, almMap);
        }

        //批量发送App-PUSH
        if (almMap.containsKey(PUSH)) {
            sendAppPushBath(messageService, almMap);
        }
    }

    /**
     * 批量写站内信记录
     *
     * @param userMsgBathVos
     * @param messageService
     */
    private static void insertUserMessageBath(List<UserMsgBathVo> userMsgBathVos, MessageServiceImpl messageService) {
        messageService.insertUserMessageBath(userMsgBathVos
                .stream()
                .filter(o -> checkEmployeeStatus(o.userMsgVo.getUserId()))
                .map(o -> buildUserMessage(o.getUserMsgVo()))
                .collect(Collectors.toList()));
    }

    /**
     * 批量发送App-PUSH
     *
     * @param messageService
     * @param almMap
     */
    private static void sendAppPushBath(MessageServiceImpl messageService, Multimap<MessageSendTypeEnum, UserMsgVo> almMap) {
        Map<Long, BaseMsgInfo> map = Maps.newHashMap();
        getUserMsgVos(almMap, PUSH).forEach(o -> {
            map.put(o.getUserId(), buildBaseMsgInfo(o));
        });
        messageService.sendAppPushBath(map);
    }

    /**
     * 批量发送短消息
     *
     * @param messageService
     * @param almMap
     */
    private static void sendSmsBath(MessageServiceImpl messageService, Multimap<MessageSendTypeEnum, UserMsgVo> almMap) {
        Map<Long, MessageInfo> map = Maps.newHashMap();
        getUserMsgVos(almMap, MESSAGE).forEach(o -> {
            map.put(o.getUserId(), buildMessageInfo(o));
        });
        messageService.sendSmsBath(map);
    }

    /**
     * 批量发送邮件
     *
     * @param messageService
     * @param almMap
     */
    private static void sendMailBath(MessageServiceImpl messageService, Multimap<MessageSendTypeEnum, UserMsgVo> almMap) {
        Map<Long, MailInfo> map = Maps.newHashMap();
        getUserMsgVos(almMap, MAIL).forEach(o -> {
            map.put(o.getUserId(), buildMailInfo(o));
        });
        messageService.sendMailBath(map);
    }

    /**
     * 获得用户消息Vo列表
     *
     * @param almMap
     * @param messageSendTypeEnum
     * @return
     */
    private static ArrayList<UserMsgVo> getUserMsgVos(Multimap<MessageSendTypeEnum, UserMsgVo> almMap, MessageSendTypeEnum messageSendTypeEnum) {
        ArrayList<UserMsgVo> userMsgVos = Lists.newArrayList(almMap.get(messageSendTypeEnum));
        return userMsgVos;
    }

    /**
     * 转换批量发送消息入参格式
     *
     * @param userMsgBathVos
     * @return
     */
    private static Multimap<MessageSendTypeEnum, UserMsgVo> formatUserMsgBathVos(List<UserMsgBathVo> userMsgBathVos) {
        //入参去除重复
        userMsgBathVos = userMsgBathVos.stream().distinct().collect(Collectors.toList());
        //转换入参格式
        ArrayListMultimap<MessageSendTypeEnum, UserMsgVo> almMap = ArrayListMultimap.create();

        userMsgBathVos.forEach(o -> {
            if (checkEmployeeStatus(o.userMsgVo.getUserId())) {
                if (!ObjectUtils.isEmpty(o.getMessageSendTypeEnums())) {
                    o.getMessageSendTypeEnums().forEach(messageSendTypeEnum -> almMap.put(messageSendTypeEnum, o.getUserMsgVo()));
                }
            }
        });
        return almMap;
    }


    /**
     * 构建用户站内信Vo
     *
     * @param userMsgVo
     * @return
     */
    private static UserMessage buildUserMessage(UserMsgVo userMsgVo) {

        UserMessage userMessage = UserMessage
                .builder()
                .userId(Optional.ofNullable(userMsgVo.getUserId()).orElse(0L))
                .title(Optional.ofNullable(userMsgVo.getTitle()).orElse(userMsgVo.getContent()))
                .content(userMsgVo.getContent())
                .isRead(false)
                .url(userMsgVo.getUrl())
                .appUrl(userMsgVo.getAppPushUrl())
                .node(userMsgVo.getTaskId())
                .newDate(1)
                .source(userMsgVo.getSource() == null ? 0 : userMsgVo.getSource())
                .build();

        //如果站内信来源不是OA系统，则title设置为对象传入title
        if (userMessage.getSource() != 0) {
            userMessage.setTitle(userMsgVo.getTitle());
        }

        return userMessage;
    }

    /**
     * 构建App-PUSH对象
     *
     * @param userMsgVo
     * @return
     */
    private static BaseMsgInfo buildBaseMsgInfo(UserMsgVo userMsgVo) {
        return BaseMsgInfo
                .builder()
                .msgTitle(userMsgVo.getTitle())
                .content(userMsgVo.getContent())
                .url(userMsgVo.getAppPushUrl())
                .build();
    }

    /**
     * 构建发送短信对象
     *
     * @param userMsgVo
     * @return
     */
    private static MessageInfo buildMessageInfo(UserMsgVo userMsgVo) {
        return MessageInfo
                .builder()
                .receiver(userMsgVo.getMobile())
                .content(userMsgVo.getContent())
                .build();
    }

    /**
     * 构建发送邮件对象
     *
     * @param userMsgVo
     * @return
     */
    private static MailInfo buildMailInfo(UserMsgVo userMsgVo) {
        return MailInfo
                .builder()
                .receiver(userMsgVo.getEmail())
                .cc(userMsgVo.getCc())
                .title(userMsgVo.getTitle())
                .content(joinEmailUrl(userMsgVo.getSsoSessionDomain(), userMsgVo.getContent(), userMsgVo.getEmailUrl()))
                .build();
    }

    /**
     * email url concat
     *
     * @param systemDomain
     * @param content
     * @param emailUrl
     * @return
     */
    private static String joinEmailUrl(String systemDomain, String content, String emailUrl) {
        if (StringUtils.isNotEmpty(emailUrl)) {
            emailUrl = StringUtils.join("<a href='http://", systemDomain, "#", emailUrl, "'>点击查看详情</a>");
        }
        return StringUtils.join(content, emailUrl);
    }

    /**
     * check whether an employee is still effective
     *
     * @param userId
     * @return
     */
    private static Boolean checkEmployeeStatus(Long userId) {

        if (ObjectUtils.isEmpty(userId)) {
            return false;
        }

        EmployeeServiceImpl bean = SpringBeanUtils.getBean(EmployeeServiceImpl.class);

        long count = bean.checkEmployeeEffective(userId);

        if (count == 0) {
            return false;
        }

        return true;
    }
}
