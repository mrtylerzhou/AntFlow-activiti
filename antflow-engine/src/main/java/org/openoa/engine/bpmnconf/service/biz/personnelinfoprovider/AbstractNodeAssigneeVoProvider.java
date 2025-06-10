package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.collect.Lists;
import com.mysql.cj.util.LazyString;
import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.constant.enums.MissingAssigneeProcessStragtegyEnum;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.interf.BpmnProcessAdminProvider;
import org.openoa.base.interf.MissAssigneeProcessing;
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
public abstract class AbstractNodeAssigneeVoProvider implements BpmnPersonnelProviderService, MissAssigneeProcessing {
    @Autowired
    private AssigneeVoBuildUtils assigneeVoBuildUtils;
    @Autowired
    private BpmnProcessAdminProvider bpmnProcessAdminProvider;
   protected List<BpmnNodeParamsAssigneeVo> provideAssigneeList(BpmnNodeVo bpmnNodeVo, Collection<String> assignees,String failFastInfo) {
       if(CollectionUtils.isEmpty(assignees)){
           if (bpmnNodeVo.getMissingAssigneeDealWay()!=null) {
               assignees.add(processMissAssignee(bpmnNodeVo.getMissingAssigneeDealWay()));
           }else {
               throw new JiMuBizException(failFastInfo);
           }

       }
       if(bpmnNodeVo.getIsOutSideProcess()!=null&&bpmnNodeVo.getIsOutSideProcess().equals(1)){
           List<BaseIdTranStruVo> emplList = bpmnNodeVo.getProperty().getEmplList();
           if(CollectionUtils.isEmpty(emplList)){
               throw new JiMuBizException("thirdy party process role node has no employee info");
           }
           return assigneeVoBuildUtils.buildVOs(emplList,bpmnNodeVo.getNodeName(),false);
       }
       return assigneeVoBuildUtils.buildVos(assignees,bpmnNodeVo.getNodeName(),false);
    }
    @Override
    public String processMissAssignee(Integer processingWay){
        MissingAssigneeProcessStragtegyEnum processingStrategy = MissingAssigneeProcessStragtegyEnum.getByCode(processingWay);
        switch (processingStrategy){
            case SKIP:
               return AFSpecialAssigneeEnum.TO_BE_REMOVED.getId();
            case TRANSFER_TO_ADMIN:
                BaseIdTranStruVo processAdminAndOutsideProcess = bpmnProcessAdminProvider.provideProcessAdminInfo();
                return processAdminAndOutsideProcess.getId();
            default:
                throw new JiMuBizException("not support miss assignee processing strategy");
        }
    }
}
