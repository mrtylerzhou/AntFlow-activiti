package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.common.util.AssigneeVoBuildUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

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
       if(bpmnNodeVo.getIsOutSideProcess()!=null&&bpmnNodeVo.getIsOutSideProcess().equals(1)){
           List<BaseIdTranStruVo> emplList = bpmnNodeVo.getProperty().getEmplList();
           if(CollectionUtils.isEmpty(emplList)){
               throw new JiMuBizException("thirdy party process role node has no employee info");
           }
           return assigneeVoBuildUtils.buildVOs(emplList,bpmnNodeVo.getNodeName(),false);
       }
       return assigneeVoBuildUtils.buildVos(assignees,bpmnNodeVo.getNodeName(),false);
    }
}
