package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.common.util.AssigneeVoBuildUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

/**
 * @Author TylerZhou
 * @Date 2024/7/18 7:02
 * @Version 1.0
 */
public abstract class AbstractNodeAssigneeVoProvider implements BpmnPersonnelProviderService {
    @Autowired
    private AssigneeVoBuildUtils assigneeVoBuildUtils;
   protected List<BpmnNodeParamsAssigneeVo> provideAssigneeList(BpmnNodeVo bpmnNodeVo, Collection<String> assignees) {
       return assigneeVoBuildUtils.buildVos(assignees,bpmnNodeVo.getNodeName(),false);
    }
}
