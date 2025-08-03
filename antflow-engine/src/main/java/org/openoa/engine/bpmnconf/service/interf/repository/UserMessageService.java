package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.UserMessage;
import org.openoa.base.vo.MailInfo;
import org.openoa.base.vo.MessageInfo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.vo.SendInfo;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

public interface UserMessageService extends IService<UserMessage> {
    ResultAndPage<UserMessage> page(PageDto pageDto);

    //insert
    Integer insertMessage(UserMessage userMessage);

    @Transactional
    Boolean sendMessageOnly(SendInfo sendInfo);

    //send email
    Boolean sendMail(MailInfo mail);

    //send sms
    Boolean sendMessage(MessageInfo messageInfo);

    //send verification code
    Boolean sendMessageCode(String phone);

    //check a user' unread count
    Long getUnread();

    //mark unread as read
    Boolean refreshUnRead();

    void refreshUnRead(Serializable id);

    void cleanUserMessage(String beforeDate);
}
