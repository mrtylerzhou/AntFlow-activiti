package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.MessageLimit;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Employee;
import org.openoa.base.entity.UserMessage;
import org.openoa.base.entity.UserMessageStatus;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.confentity.UserEmailSend;
import org.openoa.engine.bpmnconf.mapper.UserEmailSendMapper;
import org.openoa.engine.bpmnconf.mapper.UserMessageMapper;
import org.openoa.engine.bpmnconf.mapper.UserMessageStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * //todo 注意相关项配置
 */
@Slf4j
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> {
    @Value("${message.email.password:none}")
    private String password;

    @Value("${message.email.account:none}")
    private String account;

    @Value("${message.email.host:none}")
    private String host;

    @Value("${system.domain:none}")
    private String systemDomain;


    //apply for update
    private static final String APPLY_UPDATE = "applyUpdate";

    //examine(processing)
    private static final String EXAMINE = "examine";
    //check
    private static final String CHECK = "check";
    //news
    private static final String NEWS = "news";

    //process list
    private static final String WORKFLOW_LIST = "workflowList";
    //my todotask
    private static final String MY_TASK = "myTask";



    @Autowired
    private UserMessageStatusMapper userMessageStatusMapper;

    @Autowired
    private UserEmailSendMapper userEmailSendMapper;

    @Autowired
    private EmployeeServiceImpl employeeService;


    public ResultAndPage<UserMessage> page(PageDto pageDto) {

        Integer userId =  SecurityUtils.getLogInEmpIdSafe().intValue();

        QueryWrapper<UserMessage> wrapper = new QueryWrapper<>();
        Page page = PageUtils.getPageByPageDto(pageDto);
        wrapper.eq("user_id", userId);
        wrapper.eq("del", 0);
        long totalCount = this.getBaseMapper().selectCount(wrapper);

        page.setTotal(totalCount);
        //todo
        /* pageDto.setStartIndex(page.);*/
        List<UserMessage> dtoList = totalCount > 0 ? this.getBaseMapper().pageList(pageDto, userId.longValue()) : Collections.EMPTY_LIST;

        return new ResultAndPage<>(dtoList, PageUtils.getPageDto(page));
    }

    //delete messages by id
    public Boolean deleteByIds(String id) {
        Integer userId =  SecurityUtils.getLogInEmpIdSafe().intValue();
        //split id string into array
        String[] arr = id.trim().split(",");
        return this.getBaseMapper().deleteByIds(arr, (long) userId);
    }

    //clear readed
    public Boolean clean() {
        Integer userId =  SecurityUtils.getLogInEmpIdSafe().intValue();
        return this.getBaseMapper().clean(userId);
    }

    //mark as read
    public Integer isRead(Long id) {
        Integer userId =  SecurityUtils.getLogInEmpIdSafe().intValue();
        UserMessage userMessage = new UserMessage();
        userMessage.setId(id);
        userMessage.setIsRead(true);
        userMessage.setUserId((long) userId);
        return this.getBaseMapper().updateById(userMessage);
    }

    //insert
    public Integer insertMessage(UserMessage userMessage) {
        return this.getBaseMapper().insert(userMessage);
    }

    //send message
    @Transactional
    public Boolean sendAllMessage(SendInfo sendInfo) {

        //build url by different flags
        if ("".equals(sendInfo.getUserMessage().getUrl()) || sendInfo.getUserMessage().getUrl() == null) {
            UrlParams urlParams = sendInfo.getUserMessage().getUrlParams();
            String url = "";
            if (sendInfo.getUserMessage().getUrlParams() != null) {
                switch (sendInfo.getUserMessage().getParams()) {
                    //todo
                }
            }
            sendInfo.getUserMessage().setUrl(url);
            //sendInfo.getUserMessage().setUrl("oa" + systemDomain +"#" + sendInfo.getUserMessage().getUrl());
        }

        this.insertMessage(sendInfo.getUserMessage());

        //check whether a user enabled to receive email and sms
        UserMessageStatus userMessageStatus = new UserMessageStatus();
        String senderName = "system";
        //todo check login employee
        senderName = SecurityUtils.getLogInEmpNameSafe();
        userMessageStatus.setUserId(sendInfo.getUserMessage().getUserId().intValue());
        UserMessageStatus userMessageStatusInfo = null;//todo
        if (userMessageStatusInfo != null) {
            //current day end
            Date dayEnd = DateUtil.getDayEnd(new Date());
            Date dayStar = DateUtil.getDayStart(new Date());
            if (userMessageStatusInfo.getMailStatus()) {
                //send pre check
                if (sendInfo.getMail() != null) {

                    //to check whether the user has sent the emails reach the ceiling today
                    QueryWrapper<UserEmailSend> wrapper = new QueryWrapper<>();
                    wrapper.eq("receiver", sendInfo.getMail().getReceiver());
                    wrapper.ge("create_time", dayStar);
                    wrapper.le("create_time", dayEnd);
                    long totalCount = userEmailSendMapper.selectCount(wrapper).longValue();
                    if (totalCount < MessageLimit.EMAIL_A_DAY.getCode()) {

                        UserEmailSend userEmailSend = new UserEmailSend();
                        userEmailSend.setTitle(sendInfo.getMail().getTitle());
                        userEmailSend.setReceiver(sendInfo.getMail().getReceiver());

                        //build email address
                        String emailJumpUrl = "";
                        if ("".equals(sendInfo.getUserMessage().getUrl()) || sendInfo.getUserMessage().getUrl() == null) {
                            emailJumpUrl = "";
                        } else {
                            emailJumpUrl = "<a href='http://" + systemDomain + "#" + sendInfo.getUserMessage().getUrl() + "'>点击查看详情</a>";
                        }
                        //String emailJumpUrl = "<a href='oa" + systemDomain +"#" + sendInfo.getUserMessage().getUrl() + "'>查看更多</a>";
                        sendInfo.getMail().setContent(sendInfo.getMail().getContent() + emailJumpUrl);
                        userEmailSend.setContent(sendInfo.getMail().getContent());
                        userEmailSend.setUpdateUser(senderName);
                        userEmailSend.setCreateUser(senderName);
                        userEmailSend.setSender(senderName);
                        userEmailSendMapper.insert(userEmailSend);

                        //send email
                        MailInfo mail = new MailInfo();
                        mail.setContent(sendInfo.getMail().getContent());
                        mail.setReceiver(sendInfo.getMail().getReceiver());
                        mail.setTitle(sendInfo.getMail().getTitle());

                        this.sendMail(mail);
                    }
                }
            }
            //todo 去除发信息
            if (userMessageStatusInfo.getMessageStatus()) {

                MessageInfo messageInfo = new MessageInfo();

                messageInfo.setReceiver(sendInfo.getMessageInfo().getReceiver());
                messageInfo.setContent(sendInfo.getMessageInfo().getContent());
                this.sendMessage(messageInfo);
            }

            // APP push
            //is dnd enabled
            //
//            Employee employee = employeeService.selectById(sendInfo.getUserMessage().getUserId());
//            sendInfo.getBaseMsgInfo().setUsername(employee.getUsername());
//            sendInfo.getBaseMsgInfo().setUserMessageStatus(userMessageStatusInfo);
//            userAppPushService.appPush(sendInfo.getBaseMsgInfo());
            return true;
        } else {
            return false;
        }
    }

    /**
     * send a single email
     *
     * @param sendInfo
     * @return
     */
    @Transactional
    public Boolean sendMailOnly(SendInfo sendInfo) {


        this.insertMessage(sendInfo.getUserMessage());

        String senderName = "system";

        senderName = SecurityUtils.getLogInEmpNameSafe();

        Date dayEnd = DateUtil.getDayEnd(new Date());
        Date dayStar = DateUtil.getDayStart(new Date());

        if (sendInfo.getMail() != null) {

            QueryWrapper<UserEmailSend> wrapper = new QueryWrapper<>();
            wrapper.eq("receiver", sendInfo.getMail().getReceiver());
            wrapper.ge("create_time", dayStar);
            wrapper.le("create_time", dayEnd);
            long totalCount = userEmailSendMapper.selectCount(wrapper).longValue();
            if (totalCount < MessageLimit.EMAIL_A_DAY.getCode()) {

                UserEmailSend userEmailSend = new UserEmailSend();
                userEmailSend.setTitle(sendInfo.getMail().getTitle());
                userEmailSend.setReceiver(sendInfo.getMail().getReceiver());


                String emailJumpUrl = "";
                if ("".equals(sendInfo.getUserMessage().getUrl()) || sendInfo.getUserMessage().getUrl() == null) {
                    emailJumpUrl = "";
                } else {
                    emailJumpUrl = "<a href='http://" + systemDomain + "#" + sendInfo.getUserMessage().getUrl() + "'>点击查看详情</a>";
                }
                //String emailJumpUrl = "<a href='oa" + systemDomain +"#" + sendInfo.getUserMessage().getUrl() + "'>查看更多</a>";
                sendInfo.getMail().setContent(sendInfo.getMail().getContent() + emailJumpUrl);
                userEmailSend.setContent(sendInfo.getMail().getContent());
                userEmailSend.setUpdateUser(senderName);
                userEmailSend.setCreateUser(senderName);
                userEmailSend.setSender(senderName);
                userEmailSendMapper.insert(userEmailSend);


                MailInfo mail = new MailInfo();
                mail.setContent(sendInfo.getMail().getContent());
                mail.setReceiver(sendInfo.getMail().getReceiver());
                mail.setTitle(sendInfo.getMail().getTitle());

                this.sendMail(mail);

                return true;
            }
        }
        return false;
    }

    /**
     * send sms
     *
     * @param sendInfo
     * @return
     */
    @Transactional
    public Boolean sendMessageOnly(SendInfo sendInfo) {

        //消息站内信
        this.insertMessage(sendInfo.getUserMessage());

        //todo 需要实现
        //send sms
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setReceiver(sendInfo.getMessageInfo().getReceiver());
        messageInfo.setContent(sendInfo.getMessageInfo().getContent());
        this.sendMessage(messageInfo);
        return true;
    }

    //do work before send
    @Async
    public Boolean sendMessagePre(SendParam sendParam) {
        //check receiver
        Employee detail =employeeService.getEmployeeDetailById(sendParam.getUserId());
        if (detail == null) {
            throw new JiMuBizException("999999", "发送消息失败，不存在的用户!");
        }
        String senderUser = "";

        //todo 获取登陆用户
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
        return this.sendAllMessage(build);
    }


    //send email

    public Boolean sendMail(MailInfo mail) {
        //todo 发送邮件

        return true;
    }

    //send sms

    public Boolean sendMessage(MessageInfo messageInfo) {

        return null;
    }

    //send verification code
    public Boolean sendMessageCode(String phone) {
        return false;
    }



    //check a user' unread count
    public Long getUnread() {
        QueryWrapper<UserMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",  SecurityUtils.getLogInEmpIdSafe());
        wrapper.eq("`read`", 0);
        wrapper.eq("del", 0);
        return this.getBaseMapper().selectCount(wrapper);
    }

    //mark unread as read
    public Boolean refreshUnRead() {
        QueryWrapper<UserMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",  SecurityUtils.getLogInEmpIdSafe());
        wrapper.eq("`read`", 0);
        wrapper.eq("del", 0);
        List<UserMessage> userMessages = this.getBaseMapper().selectList(wrapper);
        for (UserMessage userMessage : userMessages) {
            userMessage.setIsRead(true);
            this.getBaseMapper().updateById(userMessage);
        }
        return true;
    }

    /**
     * mark read as unread
     *
     * @param id
     */
    public void refreshUnRead(Serializable id) {
        this.getBaseMapper().updateById(UserMessage
                .builder()
                .id(Long.parseLong(id.toString()))
                .isRead(true)
                .build());
    }

    public void readNode(String node) {
        QueryWrapper<UserMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("node", node);
        List<UserMessage> userMessages = this.getBaseMapper().selectList(wrapper);
        for (UserMessage userMessage : userMessages) {
            userMessage.setIsRead(true);
            this.getBaseMapper().updateById(userMessage);
        }
    }

    /**
     * clear latest 3 month's message box
     *
     * @param beforeDate
     */
    public void cleanUserMessage(String beforeDate) {
        this.getBaseMapper().cleanUserMessage(beforeDate);
    }


}
