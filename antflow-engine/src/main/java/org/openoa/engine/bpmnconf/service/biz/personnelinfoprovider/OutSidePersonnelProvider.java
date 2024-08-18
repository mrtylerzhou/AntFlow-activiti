package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author TylerZhou
 * @Date 2024/7/16 8:08
 * @Version 1.0
 */
@Component
public class OutSidePersonnelProvider implements BpmnPersonnelProviderService {
    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        return null;
    }
}
