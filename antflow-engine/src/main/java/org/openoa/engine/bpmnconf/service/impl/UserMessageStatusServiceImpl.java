package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.entity.UserMessageStatus;
import org.openoa.engine.bpmnconf.mapper.UserMessageStatusMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMessageStatusServiceImpl extends ServiceImpl<UserMessageStatusMapper, UserMessageStatus> {


    @Autowired
    private UserMessageStatusMapper mapper;


    //update insert sms
    public Boolean updateMessageStatus(UserMessageStatus userMessageStatus) {
        UserMessageStatus userMessageStatusInfo = this.getUserMessageStatus();

        if (userMessageStatusInfo == null) {
            //insert
            userMessageStatus.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessageStatus.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            mapper.insert(userMessageStatus);
        } else {
            //update
            userMessageStatusInfo.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessageStatusInfo.setMessageStatus(userMessageStatus.getMessageStatus());
            mapper.updateById(userMessageStatusInfo);
        }
        return true;
    }


    //insert or update
    public Boolean updateMailStatus(UserMessageStatus userMessageStatus) {
        UserMessageStatus userMessageStatusInfo = this.getUserMessageStatus();

        if (userMessageStatusInfo == null) {
            //insert
            userMessageStatus.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessageStatus.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
            mapper.insert(userMessageStatus);
        } else {
            //update
            userMessageStatusInfo.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessageStatusInfo.setMailStatus(userMessageStatus.getMailStatus());
            mapper.updateById(userMessageStatusInfo);
        }
        return true;
    }


    //insert or update vibration
    public Boolean updateShock(UserMessageStatus userMessageStatus) {
        UserMessageStatus userMessageStatusInfo = this.getUserMessageStatus();

        if (userMessageStatusInfo == null) {
            //insert
            userMessageStatus.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessageStatus.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            mapper.insert(userMessageStatus);
        } else {
            //update
            userMessageStatusInfo.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessageStatusInfo.setShock(userMessageStatus.getShock());
            mapper.updateById(userMessageStatusInfo);
        }
        return true;
    }


    //insert or update phone call info
    public Boolean updateOpenPhone(UserMessageStatus userMessageStatus) {
        UserMessageStatus userMessageStatusInfo = this.getUserMessageStatus();

        if (userMessageStatusInfo == null) {
            //insert
            userMessageStatus.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessageStatus.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            mapper.insert(userMessageStatus);
        } else {
            //update
            userMessageStatusInfo.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessageStatusInfo.setOpenPhone(userMessageStatus.getOpenPhone());
            mapper.updateById(userMessageStatusInfo);
        }
        return true;
    }



    //insert or update voice
    public Boolean updateSound(UserMessageStatus userMessageStatus) {
        UserMessageStatus userMessageStatusInfo = this.getUserMessageStatus();

        if (userMessageStatusInfo == null) {
            //insert
            userMessageStatus.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessageStatus.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            mapper.insert(userMessageStatus);
        } else {
            //update
            userMessageStatusInfo.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessageStatusInfo.setSound(userMessageStatus.getSound());
            mapper.updateById(userMessageStatusInfo);
        }
        return true;
    }


    //insert/upate dnd
    public Boolean updateTrouble(UserMessageStatus userMessageStatus) {
        UserMessageStatus userMessageStatusInfo = this.getUserMessageStatus();

        if (userMessageStatusInfo == null) {
            //insert
            userMessageStatus.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessageStatus.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            mapper.insert(userMessageStatus);
        } else {
            //update
            userMessageStatusInfo.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            userMessageStatusInfo.setNotTrouble(userMessageStatus.getNotTrouble());
            userMessageStatusInfo.setNotTroubleTimeBegin(userMessageStatus.getNotTroubleTimeBegin());
            userMessageStatusInfo.setNotTroubleTimeEnd(userMessageStatus.getNotTroubleTimeEnd());
            mapper.updateById(userMessageStatusInfo);
        }
        return true;
    }


    private UserMessageStatus getUserMessageStatus() {
        QueryWrapper<UserMessageStatus> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",  SecurityUtils.getLogInEmpIdSafe());
        return this.mapper.selectOne(wrapper);
    }

}
