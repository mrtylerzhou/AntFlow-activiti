package org.openoa.engine.bpmnconf.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.MessageSendTypeEnum;
import org.openoa.base.entity.UserMessage;
import org.openoa.base.interf.ProcessNoticeAdaptor;
import org.openoa.base.service.AfUserService;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.service.biz.MessageServiceImpl;
import org.openoa.engine.factory.AdaptorFactory;
import org.openoa.engine.factory.IAdaptorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.MessageSendTypeEnum.*;

public class UserMsgUtils {
    private static final Logger log = LoggerFactory.getLogger(UserMsgUtils.class);
    //=================================================send message(single) Start===========================================

    /**
     * 发送信息(单个)
     *
     * @param userMsgVo
     * @param messageSendTypeEnums
     */
    public static void sendMessages(UserMsgVo userMsgVo, MessageSendTypeEnum... messageSendTypeEnums) {

        MessageServiceImpl messageService = getMessageService();

        //do execute send message method
        doSendMessages(userMsgVo, messageService, messageSendTypeEnums);

        //write user messages to db
        insertUserMessage(userMsgVo, messageService);
    }

    /**
     * send message(single without in site notice)
     *
     * @param userMsgVo
     * @param messageSendTypeEnums
     */
    public static void sendMessagesNoUserMessage(UserMsgVo userMsgVo, MessageSendTypeEnum... messageSendTypeEnums) {
        MessageServiceImpl messageService = getMessageService();

        //do execute send message method
        doSendMessages(userMsgVo, messageService, messageSendTypeEnums);

    }

    /**
     * insert user messages
     *
     * @param userMsgVo
     */
    public static void insertUserMessage(UserMsgVo userMsgVo) {
        MessageServiceImpl messageService = getMessageService();

        //insert to db
        insertUserMessage(userMsgVo, messageService);
    }

    /**
     * do send messages
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
            //send email
            if (messageSendTypeEnumList.contains(MAIL)) {
                sendMail(userMsgVo, messageService);
            }
            //send text message
            if (messageSendTypeEnumList.contains(MESSAGE)) {
                sendSms(userMsgVo, messageService);
            }
            //app push
            if (messageSendTypeEnumList.contains(PUSH)) {
                sendAppPush(userMsgVo, messageService);
            }
        }
    }

    /**
     * set messages all(email、text message and App-PUSH so for)
     *
     * @param userMsgVo
     * @param messageService
     */
    private static void sendAllMsg(UserMsgVo userMsgVo, MessageServiceImpl messageService) {
        //send email
        sendMail(userMsgVo, messageService);
        //send text message
        sendSms(userMsgVo, messageService);
        //send app push
        sendAppPush(userMsgVo, messageService);
    }

    /**
     * get send message service
     *
     * @return
     */
    private static MessageServiceImpl getMessageService() {
        return SpringBeanUtils.getBean(MessageServiceImpl.class);
    }

    /**
     * insert in site messages
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
     * send app push
     *
     * @param userMsgVo
     * @param messageService
     */
    private static void sendAppPush(UserMsgVo userMsgVo, MessageServiceImpl messageService) {
        BaseMsgInfo baseMsgInfo = buildBaseMsgInfo(userMsgVo);
        messageService.sendAppPush(baseMsgInfo, userMsgVo.getUserId());
    }

    /**
     * send text message
     *
     * @param userMsgVo
     * @param messageService
     */
    private static void sendSms(UserMsgVo userMsgVo, MessageServiceImpl messageService) {
        MessageInfo messageInfo = buildMessageInfo(userMsgVo);
        messageService.sendSms(messageInfo, userMsgVo.getUserId());
    }

    /**
     * send email
     *
     * @param userMsgVo
     * @param messageService
     */
    private static void sendMail(UserMsgVo userMsgVo, MessageServiceImpl messageService) {
        MailInfo mailInfo = buildMailInfo(userMsgVo);
        messageService.sendMail(mailInfo, userMsgVo.getUserId());
    }


    /**
     * send messages in batch
     *
     * @param userMsgBatchVos
     */
    public static void sendMessageBatch(List<UserMsgBatchVo> userMsgBatchVos) {

        MessageServiceImpl messageService = getMessageService();

        //send messages in batch
        doSendMessageBatch(userMsgBatchVos, messageService);

        //insert in site messages in batch
        insertUserMessageBatch(userMsgBatchVos, messageService);

    }

    /**
     * send messages in batch,but without in site message
     *
     * @param userMsgBatchVos
     */
    public static void sendMessageBatchNoUserMessage(List<UserMsgBatchVo> userMsgBatchVos) {

        MessageServiceImpl messageService = getMessageService();

        //执行发送信息(批量)
        doSendMessageBatch(userMsgBatchVos, messageService);

    }

    /**
     * insert user messages in batch
     *
     * @param userMsgBatchVos
     */
    public static void insertUserMessageBatch(List<UserMsgBatchVo> userMsgBatchVos) {

        MessageServiceImpl messageService = getMessageService();


        insertUserMessageBatch(userMsgBatchVos, messageService);
    }

    /**
     * send messages in batch,without in site messages
     *
     * @param userMsgBatchVos
     */
    public static void sendMessageBatchNoInsertUserMessageBatch(List<UserMsgBatchVo> userMsgBatchVos) {

        MessageServiceImpl messageService = getMessageService();


        doSendMessageBatch(userMsgBatchVos, messageService);

    }

    /**
     * send messages in batch
     *
     * @param userMsgBatchVos
     * @param messageService
     */
    private static void doSendMessageBatch(List<UserMsgBatchVo> userMsgBatchVos, MessageServiceImpl messageService) {


        Map<MessageSendTypeEnum, List<UserMsgVo>> grouped = userMsgBatchVos.stream()
                .flatMap(batch -> batch.messageSendTypeEnums.stream()
                        .map(type -> new AbstractMap.SimpleEntry<>(type, batch.userMsgVo)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        for (Map.Entry<MessageSendTypeEnum, List<UserMsgVo>> messageSendTypeEnumListEntry : grouped.entrySet()) {
            MessageSendTypeEnum messageSendTypeEnum = messageSendTypeEnumListEntry.getKey();
            if(messageSendTypeEnum==null){
                continue;
            }
            List<UserMsgVo> userMsgVos = messageSendTypeEnumListEntry.getValue();
            IAdaptorFactory adaptorFactory = SpringBeanUtils.getBean(IAdaptorFactory.class);
            ProcessNoticeAdaptor processNoticeAdaptor = adaptorFactory.getProcessNoticeAdaptor(messageSendTypeEnum);
            if(processNoticeAdaptor!=null){
                processNoticeAdaptor.sendMessageBatchByType(userMsgVos);
            }else{
                log.warn("未实现的消息发送策略!{}",messageSendTypeEnum);
            }
        }

    }

    /**
     *  write user messages in batch
     *
     * @param userMsgBatchVos
     * @param messageService
     */
    private static void insertUserMessageBatch(List<UserMsgBatchVo> userMsgBatchVos, MessageServiceImpl messageService) {
        messageService.insertUserMessageBatch(userMsgBatchVos
                .stream()
                .filter(o -> checkEmployeeStatus(o.userMsgVo.getUserId()))
                .map(o -> buildUserMessage(o.getUserMsgVo()))
                .collect(Collectors.toList()));
    }

    /**
     * send app push in batch
     *
     * @param messageService
     */
    private static void sendAppPushBatch(MessageServiceImpl messageService, List<UserMsgVo> userMsgVos) {
        Map<String, BaseMsgInfo> map = Maps.newHashMap();
        userMsgVos.forEach(o -> {
            map.put(o.getUserId(), buildBaseMsgInfo(o));
        });
        messageService.sendAppPushBatch(map);
    }

    /**
     * send text message in batch
     *
     * @param messageService
     */
    private static void sendSmsBatch(MessageServiceImpl messageService,  List<UserMsgVo> userMsgVos) {
        Map<String, MessageInfo> map = Maps.newHashMap();
        userMsgVos.forEach(o -> {
            map.put(o.getUserId(), buildMessageInfo(o));
        });
        messageService.sendSmsBatch(map);
    }

    /**
     * send emails in batch
     *
     * @param messageService
     */
    private static void sendMailBatch(MessageServiceImpl messageService, List<UserMsgVo> userMsgVos) {
        Map<String, MailInfo> map = Maps.newHashMap();
        userMsgVos.forEach(o -> {
            map.put(o.getUserId(), buildMailInfo(o));
        });
        messageService.sendMailBatch(map);
    }

    /**
     * get user messages
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
     * convert messages
     *
     * @param userMsgBatchVos
     * @return
     */
    private static Multimap<MessageSendTypeEnum, UserMsgVo> formatUserMsgBatchVos(List<UserMsgBatchVo> userMsgBatchVos) {
        //入参去除重复
        userMsgBatchVos = userMsgBatchVos.stream().distinct().collect(Collectors.toList());
        //转换入参格式
        ArrayListMultimap<MessageSendTypeEnum, UserMsgVo> almMap = ArrayListMultimap.create();

        userMsgBatchVos.forEach(o -> {
            if (checkEmployeeStatus(o.userMsgVo.getUserId())) {
                if (!ObjectUtils.isEmpty(o.getMessageSendTypeEnums())) {
                    o.getMessageSendTypeEnums().forEach(messageSendTypeEnum -> almMap.put(messageSendTypeEnum, o.getUserMsgVo()));
                }
            }
        });
        return almMap;
    }


    /**
     * build in site user messages
     *
     * @param userMsgVo
     * @return
     */
    private static UserMessage buildUserMessage(UserMsgVo userMsgVo) {

        UserMessage userMessage = UserMessage
                .builder()
                .userId(Optional.ofNullable(userMsgVo.getUserId()).orElse("-999"))
                .title(Optional.ofNullable(userMsgVo.getTitle()).orElse(userMsgVo.getContent()))
                .content(userMsgVo.getContent())
                .isRead(false)
                .url(userMsgVo.getUrl())
                .appUrl(userMsgVo.getAppPushUrl())
                .node(userMsgVo.getTaskId())
                .source(userMsgVo.getSource() == null ? 0 : userMsgVo.getSource())
                .build();


        if (userMessage.getSource() != 0) {
            userMessage.setTitle(userMsgVo.getTitle());
        }

        return userMessage;
    }

    /**
     * build app push message
     *
     * @param userMsgVo
     * @return
     */
    public static BaseMsgInfo buildBaseMsgInfo(UserMsgVo userMsgVo) {
        return BaseMsgInfo
                .builder()
                .msgTitle(userMsgVo.getTitle())
                .content(userMsgVo.getContent())
                .url(userMsgVo.getAppPushUrl())
                .build();
    }

    /**
     * build text message
     *
     * @param userMsgVo
     * @return
     */
    public static MessageInfo buildMessageInfo(UserMsgVo userMsgVo) {
        return MessageInfo
                .builder()
                .receiver(userMsgVo.getMobile())
                .content(userMsgVo.getContent())
                .build();
    }

    /**
     * build email info
     *
     * @param userMsgVo
     * @return
     */
    public static MailInfo buildMailInfo(UserMsgVo userMsgVo) {
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
    private static Boolean checkEmployeeStatus(String userId) {

        if (ObjectUtils.isEmpty(userId)) {
            return false;
        }

        AfUserService bean = SpringBeanUtils.getBean(AfUserService.class);

        long count = bean.checkEmployeeEffective(userId);

        if (count == 0) {
            return false;
        }

        return true;
    }
}
