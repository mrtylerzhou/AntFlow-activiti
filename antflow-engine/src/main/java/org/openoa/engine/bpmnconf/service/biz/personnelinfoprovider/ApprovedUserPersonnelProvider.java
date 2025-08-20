package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.collect.Lists;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.util.AssigneeVoBuildUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class ApprovedUserPersonnelProvider implements BpmnPersonnelProviderService {
    @Autowired
    private AssigneeVoBuildUtils assigneeVoBuildUtils;

    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        String elementName=bpmnNodeVo.getNodeName();
        List<BpmnNodeParamsAssigneeVo> bpmnNodeParamsAssigneeVos = assigneeVoBuildUtils.buildVOs(startConditionsVo.getApprovalEmpls(), elementName,false);
        return bpmnNodeParamsAssigneeVos;
    }
}
