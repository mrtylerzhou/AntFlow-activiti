package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmnApproveRemind;
import org.openoa.base.util.SecurityUtils;

import org.openoa.engine.bpmnconf.mapper.BpmnApproveRemindMapper;
import org.openoa.base.vo.BpmnApproveRemindVo;
import org.openoa.base.vo.BpmnNodeVo;

import org.openoa.engine.bpmnconf.service.interf.repository.BpmnApproveRemindService;
import org.openoa.base.util.MultiTenantUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class BpmnApproveRemindServiceImpl extends ServiceImpl<BpmnApproveRemindMapper, BpmnApproveRemind> implements BpmnApproveRemindService {


    /**
     * edit remind info
     *
     * @param bpmnNodeVo
     */
    @Override
    public void editBpmnApproveRemind(BpmnNodeVo bpmnNodeVo) {
        BpmnApproveRemindVo o = bpmnNodeVo.getApproveRemindVo();
        if (ObjectUtils.isEmpty(o)) {
            return;
        }

        BpmnApproveRemind bpmnApproveRemind = new BpmnApproveRemind();
        BeanUtils.copyProperties(o, bpmnApproveRemind);
        bpmnApproveRemind.setConfId(bpmnNodeVo.getConfId());
        bpmnApproveRemind.setNodeId(bpmnNodeVo.getId());
        if (!ObjectUtils.isEmpty(o.getIsInuse())) {
            bpmnApproveRemind.setDays(StringUtils.join(o.getDayList(), ","));
        } else {
            bpmnApproveRemind.setTemplateId(null);
        }
        bpmnApproveRemind.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
        bpmnApproveRemind.setTenantId(MultiTenantUtil.getCurrentTenantId());
        getBaseMapper().insert(bpmnApproveRemind);

    }

}
