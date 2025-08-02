package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.constant.enums.MissingAssigneeProcessStragtegyEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmnProcessAdminProvider;
import org.openoa.base.interf.MissAssigneeProcessing;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class AbstractMissingAssignNodeAssigneeVoProvider  extends AbstractNodeAssigneeVoProvider implements MissAssigneeProcessing {
    @Autowired
    private BpmnProcessAdminProvider bpmnProcessAdminProvider;

    @Override
    protected List<BpmnNodeParamsAssigneeVo> provideAssigneeList(BpmnNodeVo nodeVo, Collection<BaseIdTranStruVo> emplList) {
        Integer missingAssigneeDealWay = nodeVo.getNoHeaderAction();
        if((CollectionUtils.isEmpty(emplList)||emplList.stream().allMatch(Objects::isNull))&&
            missingAssigneeDealWay==null||missingAssigneeDealWay==MissingAssigneeProcessStragtegyEnum.NOT_ALLOWED.getCode()){
            throw new AFBizException("存在未找到审批人的节点,流程不允许发起!");
        }
        BaseIdTranStruVo baseIdTranStruVo = processMissAssignee(missingAssigneeDealWay);
        emplList.add(baseIdTranStruVo);
        return super.provideAssigneeList(nodeVo, emplList);
    }
    @Override
    public BaseIdTranStruVo processMissAssignee(Integer processingWay){
        MissingAssigneeProcessStragtegyEnum processingStrategy = MissingAssigneeProcessStragtegyEnum.getByCode(processingWay);
        if(processingStrategy==null){
            return null;
        }
        switch (processingStrategy){
            case SKIP:
                return BaseIdTranStruVo.builder().id(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId()).name(AFSpecialAssigneeEnum.TO_BE_REMOVED.getDesc()).build();
            case TRANSFER_TO_ADMIN:
                BaseIdTranStruVo processAdminAndOutsideProcess = bpmnProcessAdminProvider.provideProcessAdminInfo();
                return processAdminAndOutsideProcess;
            default:
                throw new AFBizException("not support miss assignee processing strategy");
        }
    }
}
