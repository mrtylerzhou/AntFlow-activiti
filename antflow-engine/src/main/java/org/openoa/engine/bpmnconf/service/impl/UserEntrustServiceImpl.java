package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.bpmnconf.confentity.UserEntrust;
import org.openoa.engine.bpmnconf.mapper.UserEntrustMapper;
import org.openoa.base.vo.DataVo;
import org.openoa.base.vo.Entrust;
import org.openoa.base.vo.IdsVo;
import org.openoa.base.exception.JiMuBizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@Service
public class UserEntrustServiceImpl extends ServiceImpl<UserEntrustMapper, UserEntrust> {


    @Autowired
    private UserEntrustMapper mapper;

    //获get current login employee's entrust list
    public List<Entrust> getEntrustList() {
        return mapper.getEntrustListNew( SecurityUtils.getLogInEmpIdSafe().intValue());
    }


    //batch save or update entrust list
    @Transactional
    public Boolean updateEntrustList(DataVo dataVo) {
        for (IdsVo idsVo : dataVo.getIds()) {
            UserEntrust userEntrust = new UserEntrust();
            userEntrust.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            userEntrust.setBeginTime(dataVo.getBeginTime());
            userEntrust.setEndTime(dataVo.getEndTime());
            userEntrust.setReceiverId(dataVo.getReceiverId());
            userEntrust.setReceiverName(dataVo.getReceiverName());
            userEntrust.setSender( SecurityUtils.getLogInEmpIdSafe().intValue());
            if (idsVo.getId() != null) {
                //插入
                UserEntrust userEntrustCheck = mapper.selectById(idsVo.getId());
                if (userEntrustCheck == null) {
                    throw new JiMuBizException("300001", "更新的记录不存在");
                }
                userEntrust.setId(idsVo.getId());
                userEntrust.setPowerId(userEntrustCheck.getPowerId());
                userEntrust.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
                //todo update
            } else if (idsVo.getPowerId() != null) {
                if (userEntrust.getReceiverId() == null
                        || userEntrust.getReceiverName() == null) {
                    throw new JiMuBizException("300002", "请选择委托对象");
                }
                //更新
                userEntrust.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
                userEntrust.setPowerId(idsVo.getPowerId());
                mapper.insert(userEntrust);
            }
        }
        return true;
    }


    public Integer getEntrustEmployee(Integer employeeId, String powerId) {
        if (ObjectUtils.isEmpty(employeeId) || ObjectUtils.isEmpty(powerId)) {
            return employeeId;
        }

        Integer

        result = this.getEntrustEmployeeOnly(employeeId, powerId);
        return result;
    }

    /**
     * get one's entryst employee,if has no then return himself
     *
     * @param employeeId
     * @param powerId    formid
     * @return
     */
    public Integer getEntrustEmployeeOnly(Integer employeeId, String powerId) {
        if (ObjectUtils.isEmpty(employeeId) || ObjectUtils.isEmpty(powerId)) {
            return employeeId;
        }
        QueryWrapper<UserEntrust> wrapper = new QueryWrapper<>();
        wrapper.eq("power_id", powerId).eq("sender", employeeId);
        List<UserEntrust> list = this.mapper.selectList(wrapper);
        if(!CollectionUtils.isEmpty(list)){
            for (UserEntrust u : list) {
                if (u.getBeginTime()!=null && u.getEndTime()!=null && (new Date().getTime() >= DateUtil.getDayStart(u.getBeginTime()).getTime()) && (new Date().getTime() <= DateUtil.getDayEnd(u.getEndTime()).getTime())) {
                    return u.getReceiverId();
                } else if (u.getBeginTime()!=null && u.getEndTime()==null && (new Date().getTime() >= DateUtil.getDayStart(u.getBeginTime()).getTime())) {
                    return u.getReceiverId();
                } else if (u.getBeginTime()==null && u.getEndTime()==null) {
                    return u.getReceiverId();
                } else if (u.getBeginTime()==null && u.getEndTime()!=null && (new Date().getTime() <= DateUtil.getDayStart(u.getEndTime()).getTime())) {
                    return u.getReceiverId();
                }
            }
        }
        return employeeId;
    }


}
