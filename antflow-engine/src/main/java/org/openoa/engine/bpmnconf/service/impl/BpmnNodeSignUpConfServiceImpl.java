package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnNodeSignUpConf;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeSignUpConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeSignUpConfService;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.Date;

@Repository
public class BpmnNodeSignUpConfServiceImpl extends ServiceImpl<BpmnNodeSignUpConfMapper, BpmnNodeSignUpConf> implements BpmnNodeSignUpConfService {

    @Override
    public void editSignUpConf(BpmnNodeVo bpmnNodeVo, Long bpmnNodeId) {
        if (ObjectUtils.isEmpty(bpmnNodeVo.getIsSignUp()) || bpmnNodeVo.getIsSignUp() != 1) {
            return;
        }

        BpmnNodeSignUpConf bpmnNodeSignUpConf = BpmnNodeSignUpConf
                .builder()
                .bpmnNodeId(bpmnNodeId)
                .afterSignUpWay(bpmnNodeVo.getProperty().getAfterSignUpWay())
                .signUpType(bpmnNodeVo.getProperty().getSignUpType())
                .createUser(SecurityUtils.getLogInEmpNameSafe())
                .createTime(new Date())
                .tenantId(MultiTenantUtil.getCurrentTenantId())
                .build();
        this.getBaseMapper().insert(bpmnNodeSignUpConf);
    }
}