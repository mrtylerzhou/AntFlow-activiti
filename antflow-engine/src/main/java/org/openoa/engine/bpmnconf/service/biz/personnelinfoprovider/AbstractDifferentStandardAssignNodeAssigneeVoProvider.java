package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import org.openoa.base.constant.enums.ApprovalStandardEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.vo.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class  AbstractDifferentStandardAssignNodeAssigneeVoProvider extends AbstractMissingAssignNodeAssigneeVoProvider{
    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        List<String> users=new ArrayList<>();
        String startUserId = startConditionsVo.getStartUserId();
        if(bpmnNodeVo.getApprovalStandard()!=null&&bpmnNodeVo.getApprovalStandard()== ApprovalStandardEnum.APPROVAL.getCode()){
            if(CollectionUtils.isEmpty(startConditionsVo.getApprovalEmpls())){
                throw new AFBizException(BusinessErrorEnum.PARAMS_NOT_COMPLETE.getCodeStr(),"审批标准为被审批人,但是无被审批人!");
            }
            users=startConditionsVo.getApprovalEmpls().stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
        } else if(bpmnNodeVo.getApprovalStandard()!=null&&bpmnNodeVo.getApprovalStandard()==ApprovalStandardEnum.FROM_PREV_NODE.getCode()){
            List<BaseIdTranStruVo> baseIdTranStruVos = Optional.ofNullable(bpmnNodeVo.getProperty()).map(BpmnNodePropertysVo::getContextEmplList).orElse(null);
            if(!CollectionUtils.isEmpty(baseIdTranStruVos)){
                users=baseIdTranStruVos.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
            }
        }
        if(CollectionUtils.isEmpty(users)){
            users.add(startUserId);
        }
        List<BaseIdTranStruVo> assignees = queryUsers(users);

        return  super.provideAssigneeList(bpmnNodeVo,assignees);
    }
    protected abstract  List<BaseIdTranStruVo> queryUsers(List<String> userIds);
}
