package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.exception.JiMuBizException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
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
            throw  new JiMuBizException("node can not be null!");
        }
        Map<String, List<BaseIdTranStruVo>> nodeId2Assignees = bpmnStartConditions.getApproversList();
        List<String> currentNodeAssigneeIds=new ArrayList<>();
        List<BpmnNodeParamsAssigneeVo> emList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(nodeId2Assignees)){
            if (nodeId2Assignees.size()==1) {//只有一个节点时忽略前端传入节点id,减少复杂交互
                List<String> ids = nodeId2Assignees.values().stream().flatMap(a -> a.stream().map(BaseIdTranStruVo::getId)).collect(Collectors.toList());
                currentNodeAssigneeIds.addAll(ids);
            }else{//must have at least 2 elements
                List<BaseIdTranStruVo> baseIdTranStruVos = nodeId2Assignees.get(bpmnNodeVo.getId().toString());
                if(!CollectionUtils.isEmpty(baseIdTranStruVos)){
                    List<String> ids = baseIdTranStruVos.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
                    currentNodeAssigneeIds.addAll(ids);
                }
            }
            if (!ObjectUtils.isEmpty(currentNodeAssigneeIds)) {
                //has sign up approver
                int fIndex = 1;
                for (String s : currentNodeAssigneeIds) {
                    BpmnNodeParamsAssigneeVo vo = new BpmnNodeParamsAssigneeVo();
                    vo.setAssignee(s);
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
            vo.setAssignee("0");
            emList.add(vo);
        }
        return emList;
    }
}
