package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.exception.AFBizException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-12 19:42
 * @Param
 * @return
 * @Version 1.0
 */
@Component
public class CustomizePersonnelProvider implements BpmnPersonnelProviderService {
    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo bpmnStartConditions) {
        if(bpmnNodeVo==null){
            throw  new AFBizException("node can not be null!");
        }
        Map<String, List<BaseIdTranStruVo>> nodeId2Assignees = bpmnStartConditions.getApproversList();
        List<BaseIdTranStruVo> currentNodeAssignees=new ArrayList<>();
        List<BpmnNodeParamsAssigneeVo> emList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(nodeId2Assignees)){
            if (nodeId2Assignees.size()==1) {//只有一个节点时忽略前端传入节点id,减少复杂交互
                List<BaseIdTranStruVo> assignees = nodeId2Assignees.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
                currentNodeAssignees.addAll(assignees);
            }else{//must have at least 2 elements
                List<BaseIdTranStruVo> baseIdTranStruVos = nodeId2Assignees.get(bpmnNodeVo.getId().toString());
                if(!CollectionUtils.isEmpty(baseIdTranStruVos)){
                    currentNodeAssignees.addAll(baseIdTranStruVos);
                }
            }
            if (!ObjectUtils.isEmpty(currentNodeAssignees)) {
                //has sign up approver
                int fIndex = 1;
                for (BaseIdTranStruVo s : currentNodeAssignees) {
                    BpmnNodeParamsAssigneeVo vo = new BpmnNodeParamsAssigneeVo();
                    vo.setAssignee(s.getId());
                    vo.setAssigneeName(s.getName());
                    if (!ObjectUtils.isEmpty(bpmnNodeVo.getNodeName())) {
                        vo.setElementName(bpmnNodeVo.getNodeName());
                    } else {
                        vo.setElementName("自定义审批人" + fIndex);
                    }
                    fIndex++;
                    emList.add(vo);
                }
            } else {
                //set zero
                BpmnNodeParamsAssigneeVo vo = new BpmnNodeParamsAssigneeVo();
                vo.setAssignee("0");
                emList.add(vo);
            }
        }else{
            //set zero
            BpmnNodeParamsAssigneeVo vo = new BpmnNodeParamsAssigneeVo();
            vo.setAssignee(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            emList.add(vo);
        }
        return emList;
    }
}
