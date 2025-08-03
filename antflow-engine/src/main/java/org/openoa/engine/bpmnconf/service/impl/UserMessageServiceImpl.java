package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.UserMessage;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.mapper.UserMessageMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.UserMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * //todo 注意相关项配置
 */
@Slf4j
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {


    @Override
    public ResultAndPage<UserMessage> page(PageDto pageDto) {

        String userId =  SecurityUtils.getLogInEmpIdSafe();

        QueryWrapper<UserMessage> wrapper = new QueryWrapper<>();
        Page page = PageUtils.getPageByPageDto(pageDto);
        wrapper.eq("user_id", userId);
        wrapper.eq("del", 0);
        long totalCount = this.getBaseMapper().selectCount(wrapper);

        page.setTotal(totalCount);
        //todo
        /* pageDto.setStartIndex(page.);*/
        List<UserMessage> dtoList = totalCount > 0 ? this.getBaseMapper().pageList(pageDto, userId) : Collections.EMPTY_LIST;

        return new ResultAndPage<>(dtoList, PageUtils.getPageDto(page));
    }

    //delete messages by id
    public Boolean deleteByIds(String id) {
        String userId =  SecurityUtils.getLogInEmpIdSafe();
        //split id string into array
        String[] arr = id.trim().split(",");
        return this.getBaseMapper().deleteByIds(arr, userId);
    }

    //clear readed
    public Boolean clean() {
        String userId =  SecurityUtils.getLogInEmpIdSafe();
        return this.getBaseMapper().clean(userId);
    }

    //mark as read
    public Integer isRead(Long id) {
        String userId =  SecurityUtils.getLogInEmpIdSafe();
        UserMessage userMessage = new UserMessage();
        userMessage.setId(id);
        userMessage.setIsRead(true);
        userMessage.setUserId(userId);
        return this.getBaseMapper().updateById(userMessage);
    }


    //insert
    @Override
    public Integer insertMessage(UserMessage userMessage) {
        return this.getBaseMapper().insert(userMessage);
    }



    /**
     * send sms
     *
     * @param sendInfo
     * @return
     */
    @Transactional
    @Override
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



    //send email
    @Override
    public Boolean sendMail(MailInfo mail) {
        //todo 发送邮件

        return true;
    }

    //send sms
    @Override
    public Boolean sendMessage(MessageInfo messageInfo) {

        return null;
    }

    //send verification code
    @Override
    public Boolean sendMessageCode(String phone) {
        return false;
    }



    //check a user' unread count
    @Override
    public Long getUnread() {
        QueryWrapper<UserMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",  SecurityUtils.getLogInEmpIdSafe());
        wrapper.eq("`read`", 0);
        wrapper.eq("del", 0);
        return this.getBaseMapper().selectCount(wrapper);
    }

    //mark unread as read
    @Override
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
    @Override
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
    @Override
    public void cleanUserMessage(String beforeDate) {
        this.getBaseMapper().cleanUserMessage(beforeDate);
    }


}
