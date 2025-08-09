package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FormRelatedPersonnelProvider extends AbstractMissingAssignNodeAssigneeVoProvider{
    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
       return null;
    }
}
