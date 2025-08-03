package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.UserEntrust;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.mapper.UserEntrustMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.UserEntrustService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@Repository
public class UserEntrustServiceImpl extends ServiceImpl<UserEntrustMapper, UserEntrust> implements UserEntrustService {



    //获get current login employee's entrust list
    public List<Entrust> getEntrustList() {
        return getBaseMapper().getEntrustListNew( SecurityUtils.getLogInEmpIdSafe());
    }
    public UserEntrust getEntrustDetail(Integer id) {
        return this.getBaseMapper().selectById(id);
    }
    public ResultAndPage<Entrust> getEntrustPageList(PageDto pageDto, Entrust vo, Integer type) {
        if (type == 1){
            vo.setReceiverId(SecurityUtils.getLogInEmpIdSafe());
        }
        Page<Entrust> page = PageUtils.getPageByPageDto(pageDto);
        List<Entrust> resultData = this.getBaseMapper().getEntrustPageList(page, vo.getReceiverId());
        if (resultData==null) {
            return PageUtils.getResultAndPage(page);
        }
        page.setRecords(resultData);
        return PageUtils.getResultAndPage(page);
    }

    //batch save or update entrust list
    @Transactional
    public void updateEntrustList(DataVo dataVo) {
        for (IdsVo idsVo : dataVo.getIds()) {
            UserEntrust userEntrust = new UserEntrust();
            userEntrust.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            userEntrust.setBeginTime(dataVo.getBeginTime());
            userEntrust.setEndTime(dataVo.getEndTime());
            userEntrust.setReceiverId(dataVo.getReceiverId());
            userEntrust.setReceiverName(dataVo.getReceiverName());
            //userEntrust.setSender( SecurityUtils.getLogInEmpIdSafe().intValue());
            userEntrust.setSender(dataVo.getSender());
            if (idsVo.getId() != null && idsVo.getId() > 0) {
                //更新
                UserEntrust userEntrustCheck = getBaseMapper().selectById(idsVo.getId());
                if (userEntrustCheck == null) {
                    throw new AFBizException("300001", "更新的记录不存在");
                }
                userEntrust.setId(idsVo.getId());
                userEntrust.setPowerId(userEntrustCheck.getPowerId());
                userEntrust.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
                getBaseMapper().updateById(userEntrust);
            } else if (idsVo.getPowerId() != null) {
                if (userEntrust.getReceiverId() == null) {
                    throw new AFBizException("300002", "请选择委托对象");
                }
                //插入
                userEntrust.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
                userEntrust.setPowerId(idsVo.getPowerId());
                getBaseMapper().insert(userEntrust);
            }
        }
    }


    public BaseIdTranStruVo getEntrustEmployee(String employeeId,String employeeName, String powerId) {
        if (ObjectUtils.isEmpty(employeeId) || ObjectUtils.isEmpty(powerId)) {
            return BaseIdTranStruVo.builder().id(employeeId).name(employeeName).build();
        }

        BaseIdTranStruVo result = this.getEntrustEmployeeOnly(employeeId,employeeName, powerId);
        return result;
    }

    /**
     * get one's entryst employee,if has no then return himself
     *
     * @param employeeId
     * @param powerId    formid
     * @return
     */
    public BaseIdTranStruVo getEntrustEmployeeOnly(String employeeId,String employeeName, String powerId) {
        if (ObjectUtils.isEmpty(employeeId) || ObjectUtils.isEmpty(powerId)) {
            return BaseIdTranStruVo.builder().id(employeeId).name(employeeName).build();
        }
        QueryWrapper<UserEntrust> wrapper = new QueryWrapper<>();
        wrapper.eq("power_id", powerId).eq("sender", employeeId);
        List<UserEntrust> list = this.getBaseMapper().selectList(wrapper);
        if(!CollectionUtils.isEmpty(list)){
            for (UserEntrust u : list) {
                if (u.getBeginTime()!=null && u.getEndTime()!=null && (new Date().getTime() >= DateUtil.getDayStart(u.getBeginTime()).getTime()) && (new Date().getTime() <= DateUtil.getDayEnd(u.getEndTime()).getTime())) {
                    return BaseIdTranStruVo.builder().id(u.getReceiverId()).name(u.getReceiverName()).build();
                } else if (u.getBeginTime()!=null && u.getEndTime()==null && (new Date().getTime() >= DateUtil.getDayStart(u.getBeginTime()).getTime())) {
                    return BaseIdTranStruVo.builder().id(u.getReceiverId()).name(u.getReceiverName()).build();
                } else if (u.getBeginTime()==null && u.getEndTime()==null) {
                    return BaseIdTranStruVo.builder().id(u.getReceiverId()).name(u.getReceiverName()).build();
                } else if (u.getBeginTime()==null && u.getEndTime()!=null && (new Date().getTime() <= DateUtil.getDayStart(u.getEndTime()).getTime())) {
                    return BaseIdTranStruVo.builder().id(u.getReceiverId()).name(u.getReceiverName()).build();
                }
            }
        }
        return BaseIdTranStruVo.builder().id(employeeId).name(employeeName).build();
    }


}
