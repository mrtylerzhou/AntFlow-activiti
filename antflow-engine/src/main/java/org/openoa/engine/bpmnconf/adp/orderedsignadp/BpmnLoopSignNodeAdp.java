package org.openoa.engine.bpmnconf.adp.orderedsignadp;

import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.service.UserServiceImpl;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.adaptor.bpmnelementadp.AbstractOrderedSignNodeAdp;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author TylerZhou
 * @Date 2024/7/18 22:30
 * @Version 0.5
 */
@Service
public class BpmnLoopSignNodeAdp extends AbstractOrderedSignNodeAdp {
    @Autowired
    private UserServiceImpl userService;
    @Override
    public List<String> getAssigneeIds(BpmnNodeVo nodeVo, BpmnStartConditionsVo bpmnStartConditions) {
        BpmnNodePropertysVo propertysVo = nodeVo.getProperty();
        if(propertysVo==null){
            throw new JiMuBizException("loop sign failure,node has no property!");
        }
        String startUserId = bpmnStartConditions.getStartUserId();

        //type organization line,reporting line,you can also give it other meaning
        //it is just a property,it is only meaningful when you use it in your business
        Integer loopEndType = propertysVo.getLoopEndType();

        //two parameters,can not be both empty
        //how many levels
        Integer loopNumberPlies = propertysVo.getLoopNumberPlies();
        //end levels
        Integer loopEndGrade = propertysVo.getLoopEndGrade();
        //end person
        HashSet<String> loopEndPersonList = new HashSet<>();
        if (!CollectionUtils.isEmpty(propertysVo.getLoopEndPersonList())) {
            for (Serializable s : propertysVo.getLoopEndPersonList()) {
                loopEndPersonList.add(s.toString());
            }
        }
        if (loopNumberPlies==null && loopEndGrade==null) {
            throw new JiMuBizException("组织线层层审批找人时，两个入参都为空！");
        }
        List<BaseIdTranStruVo> baseIdTranStruVos=null;
        if(loopNumberPlies!=null){
            baseIdTranStruVos= userService.queryLeadersByEmployeeIdAndTier(startUserId, loopNumberPlies);
            if(CollectionUtils.isEmpty(baseIdTranStruVos)){
                throw new JiMuBizException("未能根据发起人找到层层审批人信息");
            }
        }
        if(loopEndGrade!=null){
            baseIdTranStruVos=userService.queryLeadersByEmployeeIdAndGrade(startUserId, loopEndGrade);
            if(CollectionUtils.isEmpty(baseIdTranStruVos)){
                throw new JiMuBizException("未能根据发起人找到汇报线审批人信息");
            }
        }
        if(CollectionUtils.isEmpty(baseIdTranStruVos)){
            throw new JiMuBizException("未能根据发起人找到审批人信息");
        }
        List<String> approverIds = baseIdTranStruVos.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
        List<String> finalApproverIds = new ArrayList<>();
        for (String approverId : approverIds) {
            if(!loopEndPersonList.contains(approverId)){
                finalApproverIds.add(approverId);
            }
        }
        return  finalApproverIds;
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(OrderNodeTypeEnum.LOOP_NODE);
    }
}
