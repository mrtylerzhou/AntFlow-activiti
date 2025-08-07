package org.openoa.engine.bpmnconf.service.interf.repository;

import org.openoa.base.entity.UserMessage;
import org.openoa.base.vo.SendInfo;
import org.openoa.base.vo.SendParam;
import org.openoa.engine.bpmnconf.mapper.UserMessageMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.BizService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

public interface UserMessageBizService extends BizService<UserMessageMapper,UserMessageService, UserMessage> {
    //send message
    @Transactional
    Boolean sendAllMessage(SendInfo sendInfo);

    @Transactional
    Boolean sendMailOnly(SendInfo sendInfo);

    //do work before send
    @Async
    Boolean sendMessagePre(SendParam sendParam);
}
