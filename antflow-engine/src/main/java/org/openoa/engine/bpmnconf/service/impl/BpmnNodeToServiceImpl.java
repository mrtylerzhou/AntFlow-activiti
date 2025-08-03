package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnNodeTo;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeToMapper;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeToService;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.engine.utils.AFWrappers;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BpmnNodeToServiceImpl extends ServiceImpl<BpmnNodeToMapper, BpmnNodeTo> implements BpmnNodeToService {


    @Override
    public void editNodeTo(BpmnNodeVo bpmnNodeVo, Long bpmnNodeId) {
        if (bpmnNodeVo==null) {
            return;
        }
        List<String> nodeTo = bpmnNodeVo.getNodeTo();
        //delete existing data
        this.getBaseMapper().delete( AFWrappers.<BpmnNodeTo>lambdaTenantQuery()
                .eq(BpmnNodeTo::getBpmnNodeId, bpmnNodeId));

        String logInEmpNameSafe = SecurityUtils.getLogInEmpNameSafe();
        Date nowDate = new Date();
        List<BpmnNodeTo> bpmnNodeToList = nodeTo.stream()
                .map(o -> BpmnNodeTo
                        .builder()
                        .bpmnNodeId(bpmnNodeId)
                        .nodeTo(o)
                        .createUser(logInEmpNameSafe)
                        .createTime(nowDate)
                        .tenantId(MultiTenantUtil.getCurrentTenantId())
                        .build())
                .collect(Collectors.toList());

        this.saveBatch(bpmnNodeToList);
    }

}
