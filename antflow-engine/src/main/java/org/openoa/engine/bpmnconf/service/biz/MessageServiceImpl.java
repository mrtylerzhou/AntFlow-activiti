package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.UserMessage;
import org.openoa.base.entity.UserMessageStatus;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseMsgInfo;
import org.openoa.base.vo.MailInfo;
import org.openoa.base.vo.MessageInfo;
import org.openoa.engine.bpmnconf.service.impl.UserMessageServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.UserMessageStatusServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MessageServiceImpl {

    @Autowired
    private BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService;

    @Autowired
    private UserMessageServiceImpl userMessageService;

    @Autowired
    private UserMessageStatusServiceImpl userMessageStatusService;


    /**
     * send single email
     *
     * @param mailInfo
     * @param userId
     */
    @Async
    public void sendMail(MailInfo mailInfo, Long userId) {

        UserMessageStatus userMessageStatus = getUserMessageStatus(userId);
        //todo
    }

    /**
     * send email in batch
     *
     * @param map
     */
    @Async
    public void sendMailBath(Map<Long, MailInfo> map) {
        List<MailInfo> mailInfos = Lists.newArrayList();

        for (Map.Entry<Long, MailInfo> entry : map.entrySet()) {

            UserMessageStatus userMessageStatus = getUserMessageStatus(entry.getKey());

            //todo
        }
    }

    /**
     * send single sms
     *
     * @param messageInfo
     * @param userId
     */
    @Async
    public void sendSms(MessageInfo messageInfo, Long userId) {

        UserMessageStatus userMessageStatus = getUserMessageStatus(userId);
//todo
    }

    /**
     * send sms in batch
     *
     * @param map
     */
    @Async
    public void sendSmsBath(Map<Long, MessageInfo> map) {
        List<MessageInfo> messageInfos = Lists.newArrayList();

        for (Map.Entry<Long, MessageInfo> entry : map.entrySet()) {

            UserMessageStatus userMessageStatus = getUserMessageStatus(entry.getKey());

            //todo
        }
    }

    /**
     * push single app notification
     *
     * @param baseMsgInfo
     * @param userId
     */
    @Async
    public void sendAppPush(BaseMsgInfo baseMsgInfo, Long userId) {
        doSendAppPush(baseMsgInfo, userId);
    }

    /**
     * push app notification in batch
     *
     * @param map
     */
    @Async
    public void sendAppPushBath(Map<Long, BaseMsgInfo> map) {
        for (Map.Entry<Long, BaseMsgInfo> entry : map.entrySet()) {
            doSendAppPush(entry.getValue(), entry.getKey());
        }
    }

    /**
     * insert user message
     *
     * @param userMessage
     */
    @Async
    public void insertUserMessage(UserMessage userMessage) {

        if (ObjectUtils.isEmpty(userMessage.getUserId())) {
            return;
        }

        userMessage.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
        userMessage.setCreateTime(new Date());
        userMessage.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
        userMessage.setUpdateTime(new Date());
        userMessageService.insertMessage(userMessage);
    }

    /**
     * insert user message in batch
     *
     * @param userMessages
     */
    @Async
    public void insertUserMessageBath(List<UserMessage> userMessages) {

        for (UserMessage userMessage : userMessages) {
            if (ObjectUtils.isEmpty(userMessage.getUserId())) {
                continue;
            }

            userMessage.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessage.setCreateTime(new Date());
            userMessage.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessage.setUpdateTime(new Date());
        }

        userMessageService.saveBatch(userMessages);
    }

    /**
     * do send app push
     *
     * @param baseMsgInfo
     * @param userId
     */
    private void doSendAppPush(BaseMsgInfo baseMsgInfo, Long userId) {

        baseMsgInfo.setUsername(getUsernameByUserId(userId));
        UserMessageStatus userMessageStatus = getUserMessageStatus(userId);

        //todo
    }

    /**
     * get user message config
     *
     * @param userId
     * @return
     */
    private UserMessageStatus getUserMessageStatus(Long userId) {
        return userMessageStatusService.getBaseMapper().selectOne(new QueryWrapper<UserMessageStatus>().eq("user_id", userId));
    }

    /**
     * get user name by id
     *
     * @param userId
     * @return
     */
    private String getUsernameByUserId(Long userId) {
        Map<String, String> employeeInfo = bpmnEmployeeInfoProviderService.provideEmployeeInfo(Lists.newArrayList(String.valueOf(userId)));
        return employeeInfo.get(String.valueOf(userId));
    }
}
