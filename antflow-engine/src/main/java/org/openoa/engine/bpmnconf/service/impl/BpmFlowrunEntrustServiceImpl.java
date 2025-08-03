package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import org.openoa.base.entity.BpmFlowrunEntrust;
import org.openoa.base.entity.UserEntrust;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmFlowrunEntrustVo;

import org.openoa.engine.bpmnconf.mapper.BpmFlowrunEntrustMapper;

import org.openoa.engine.bpmnconf.service.interf.repository.BpmFlowrunEntrustService;
import org.openoa.engine.utils.AFWrappers;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BpmFlowrunEntrustServiceImpl extends ServiceImpl<BpmFlowrunEntrustMapper, BpmFlowrunEntrust> implements BpmFlowrunEntrustService {


    /**
     * add flow entrust
     *
     * @param actual    current entrust user
     * @param runtaskid task id
     * @param type      0 entrust 1:circulate
     */
    @Override
    public void addFlowrunEntrust(String actual, String actualName, String original, String originalName, String runtaskid, Integer type, String ProcessInstanceId, String processKey) {
        BpmFlowrunEntrust entrust = new BpmFlowrunEntrust();
        entrust.setType(type);
        entrust.setRuntaskid(runtaskid);
        entrust.setActual(actual);
        entrust.setActualName(actualName);
        entrust.setOriginal(original);
        entrust.setOriginalName(originalName);
        entrust.setIsRead(2);
        entrust.setProcDefId(processKey);
        entrust.setRuninfoid(ProcessInstanceId);
        getBaseMapper().insert(entrust);
    }

    @Override
    public boolean addFlowrunEntrust(BpmFlowrunEntrust flowrunEntrust) {
        getBaseMapper().insert(flowrunEntrust);
        return true;
    }

    /**
     * check whether current process has entrust
     *
     * @param receiverId
     * @param processKey
     * @return
     * @throws Exception
     */
    @Override
    public UserEntrust getBpmEntrust(String receiverId, String processKey) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = df.parse(df.format(new Date()));
        } catch (ParseException e) {
            log.error("error occur when parsing:",e);
        }
        String key = "";
        if (processKey.contains(":")) {
            key = processKey.split(":")[0];
        }
        UserEntrust entrust = getBaseMapper().getBpmEntrust(receiverId, !Strings.isNullOrEmpty(key) ? key : processKey);
        UserEntrust userEntrust = null;
        if (!ObjectUtils.isEmpty(entrust) && date != null) {
            if (entrust.getBeginTime() == null && entrust.getEndTime() == null) {
                userEntrust = entrust;
            } else if (entrust.getBeginTime() != null && entrust.getEndTime() != null) {
                if (date.getTime() >= entrust.getBeginTime().getTime() && date.getTime() <= entrust.getEndTime().getTime()) {
                    userEntrust = entrust;
                }
            } else if (entrust.getBeginTime() == null && entrust.getEndTime() != null) {
                if (entrust.getEndTime().getTime() >= date.getTime()) {
                    userEntrust = entrust;
                }
            } else if (entrust.getBeginTime() != null && entrust.getEndTime() == null) {
                if (entrust.getBeginTime().getTime() <= date.getTime()) {
                    userEntrust = entrust;
                }
            }
        }
        return userEntrust;
    }


    @Override
    public Boolean updateBpmFlowrunEntrust(String processInstanceId, Integer loginUserId) {
        getBaseMapper().updateBpmFlowrunEntrust(processInstanceId, loginUserId);
        return true;
    }

    /**
     * update entrusted flow status as viewed
     */
    @Override
    public boolean editFlowrunEntrustState(String processInstanceId) {
        getBaseMapper().selectList(
                AFWrappers.<BpmFlowrunEntrust>lambdaTenantQuery()
                        .eq(BpmFlowrunEntrust::getRuninfoid,processInstanceId)
                        .eq(BpmFlowrunEntrust::getOriginal,SecurityUtils.getLogInEmpIdSafe())
        ).forEach(o -> {
            o.setIsView(1);
            getBaseMapper().updateById(o);
        });
        return true;
    }

    /**
     * query entrust and circulate data by process instance id
     *
     * @param vo
     * @return
     */
    @Override
    public List<BpmFlowrunEntrust> findFlowrunEntrustByProcessInstanceId(BpmFlowrunEntrustVo vo) {
        return Optional.ofNullable(this.getBaseMapper()
                .selectList(
                        AFWrappers.<BpmFlowrunEntrust>lambdaTenantQuery()
                                .eq(BpmFlowrunEntrust::getType,vo.getType())
                                .eq(BpmFlowrunEntrust::getRuninfoid,vo.getRuninfoid())))
                .orElse(Collections.emptyList());
    }

}
