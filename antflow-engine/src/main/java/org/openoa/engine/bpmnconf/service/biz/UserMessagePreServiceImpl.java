package org.openoa.engine.bpmnconf.service.biz;

import org.openoa.base.constant.enums.MessageSendTypeEnum;
import org.openoa.base.entity.DetailedUser;
import org.openoa.base.entity.UserMessage;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.service.AfUserService;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.service.interf.repository.UserMessageBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMessagePreServiceImpl {

    @Autowired
    private UserMessageBizService userMessageBizService;
    @Autowired
    private AfUserService employeeService;

    //do some work before send message
    @Async
    public Boolean sendMessagePre(SendParam sendParam) {

        //query receiver's info
        DetailedUser detail = employeeService.getEmployeeDetailById(sendParam.getUserId());
        if (detail == null) {
            throw new AFBizException("999999", "发送消息失败，不存在的用户!");
        }
        //todo 判断用户存在
        String senderUser = SecurityUtils.getLogInEmpNameSafe();

        MailInfo mailInfo = MailInfo.builder().content(sendParam.getContent())
                .receiver(detail.getEmail())
                .title(sendParam.getTitle()).build();

        MessageInfo message = MessageInfo.builder().receiver(detail.getMobile())
                .content(sendParam.getContent()).build();

        UserMessage userMessage = UserMessage.builder()
                .content(sendParam.getContent())
                .title(sendParam.getTitle())
                .createUser(senderUser)
                .isRead(false)
                .node(sendParam.getNode())
                .params(sendParam.getParams())
                .updateUser(SecurityUtils.getLogInEmpNameSafe())
                .urlParams(sendParam.getUrlParams())
                .appUrl(sendParam.getAppUrl() == null ? "" : sendParam.getAppUrl())
                .url(sendParam.getUrl()).userId(sendParam.getUserId()).build();

        BaseMsgInfo baseMsgInfo = BaseMsgInfo.builder().url(sendParam.getAppUrl()).msgTitle(sendParam.getTitle()).build();

        SendInfo build = SendInfo.builder().mail(mailInfo).messageInfo(message).userMessage(userMessage).baseMsgInfo(baseMsgInfo).build();

        return userMessageBizService.sendAllMessage(build);

    }

    /**
     * send a single message, according to the enumeration configuration to call the separate message sending mechanism
     *
     * @param sendParam
     * @param messageSendTypeEnum
     * @return
     */
    @Async
    public Boolean sendMessagePre(SendParam sendParam, MessageSendTypeEnum messageSendTypeEnum) {
        //get receiver's info
        DetailedUser detail = employeeService.getEmployeeDetailById(sendParam.getUserId());
        if (detail == null) {
            throw new AFBizException("999999", "发送消息失败，不存在的用户!");
        }
        String senderUser = "";
        //todo
        //check whether a use is logged in
        senderUser = SecurityUtils.getLogInEmpNameSafe();


        MailInfo mailInfo = MailInfo.builder().content(sendParam.getContent())
                .receiver(detail.getEmail())
                .title(sendParam.getTitle()).build();

        MessageInfo message = MessageInfo.builder().receiver(detail.getMobile())
                .content(sendParam.getContent()).build();

        UserMessage userMessage = UserMessage.builder()
                .content(sendParam.getContent())
                .title(sendParam.getTitle())
                .createUser(senderUser)
                .isRead(false)
                .params(sendParam.getParams())
                .updateUser(SecurityUtils.getLogInEmpNameSafe())
                .urlParams(sendParam.getUrlParams())
                .appUrl(sendParam.getAppUrl() == null ? "" : sendParam.getAppUrl())
                .url(sendParam.getUrl()).userId(sendParam.getUserId()).build();

        BaseMsgInfo baseMsgInfo = BaseMsgInfo.builder().url(sendParam.getAppUrl()).msgTitle(sendParam.getTitle()).build();

        SendInfo build = SendInfo.builder().mail(mailInfo).messageInfo(message).userMessage(userMessage).baseMsgInfo(baseMsgInfo).build();

        switch (messageSendTypeEnum) {
            case MAIL:
                return userMessageBizService.sendMailOnly(build);
            case MESSAGE:
                return userMessageBizService.getService().sendMessageOnly(build);
            default:
                return false;
        }

    }



    //pre work for sending batch message
    @Async
    public Boolean sendMessagePreBatch(List<SendParam> sendParams) {
        for (SendParam sendParam : sendParams) {
            try {
                Thread.sleep(200);
                //query receiver's info
                DetailedUser detail = employeeService.getEmployeeDetailById(sendParam.getUserId());
                if (detail == null) {
                    throw new AFBizException("999999", "发送消息失败，不存在的用户!");
                }
                String senderUser = "";

                senderUser = SecurityUtils.getLogInEmpNameSafe();

                MailInfo mailInfo = MailInfo.builder().content(sendParam.getContent())
                        .receiver(detail.getEmail())
                        .title(sendParam.getTitle()).build();

                MessageInfo message = MessageInfo.builder().receiver(detail.getMobile())
                        .content(sendParam.getContent()).build();

                UserMessage userMessage = UserMessage.builder()
                        .content(sendParam.getContent())
                        .title(sendParam.getTitle())
                        .createUser(senderUser)
                        .isRead(false)
                        .params(sendParam.getParams())
                        .updateUser(SecurityUtils.getLogInEmpNameSafe())
                        .urlParams(sendParam.getUrlParams())
                        .appUrl(sendParam.getAppUrl() == null ? "" : sendParam.getAppUrl())
                        .url(sendParam.getUrl()).userId(sendParam.getUserId()).build();

                BaseMsgInfo baseMsgInfo = BaseMsgInfo.builder().url(sendParam.getAppUrl()).msgTitle(sendParam.getTitle()).build();

                SendInfo build = SendInfo.builder().mail(mailInfo).messageInfo(message).userMessage(userMessage).baseMsgInfo(baseMsgInfo).build();
                userMessageBizService.sendAllMessage(build);
                //return userMessageService.sendAllMessage(build);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * send messages in batch(according to the enumeration configuration to call the separate message sending mechanism)
     *
     * @param sendParams
     * @param messageSendTypeEnum
     * @return
     */
    @Async
    public Boolean sendMessagePreBatch(List<SendParam> sendParams, MessageSendTypeEnum messageSendTypeEnum) {
        for (SendParam sendParam : sendParams) {
            try {
                Thread.sleep(200);
                //search receiver
                DetailedUser detail = employeeService.getEmployeeDetailById(sendParam.getUserId());
                if (detail == null) {
                    throw new AFBizException("999999", "发送消息失败，不存在的用户!");
                }
                String senderUser = "";

                senderUser = SecurityUtils.getLogInEmpNameSafe();

                MailInfo mailInfo = MailInfo.builder().content(sendParam.getContent())
                        .receiver(detail.getEmail())
                        .title(sendParam.getTitle()).build();

                MessageInfo message = MessageInfo.builder().receiver(detail.getMobile())
                        .content(sendParam.getContent()).build();

                UserMessage userMessage = UserMessage.builder()
                        .content(sendParam.getContent())
                        .title(sendParam.getTitle())
                        .createUser(senderUser)
                        .isRead(false)
                        .params(sendParam.getParams())
                        .updateUser(SecurityUtils.getLogInEmpNameSafe())
                        .urlParams(sendParam.getUrlParams())
                        .appUrl(sendParam.getAppUrl() == null ? "" : sendParam.getAppUrl())
                        .url(sendParam.getUrl()).userId(sendParam.getUserId()).build();

                BaseMsgInfo baseMsgInfo = BaseMsgInfo.builder().url(sendParam.getAppUrl()).msgTitle(sendParam.getTitle()).build();

                SendInfo build = SendInfo.builder().mail(mailInfo).messageInfo(message).userMessage(userMessage).baseMsgInfo(baseMsgInfo).build();

                switch (messageSendTypeEnum) {
                    case MAIL:
                        userMessageBizService.sendMailOnly(build);
                        break;
                    case MESSAGE:
                        userMessageBizService.getService().sendMessageOnly(build);
                        break;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


}
