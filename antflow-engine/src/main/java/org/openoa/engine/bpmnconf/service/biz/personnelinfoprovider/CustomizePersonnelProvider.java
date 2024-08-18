package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.exception.JiMuBizException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

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
        List<BpmnNodeParamsAssigneeVo> emList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(bpmnStartConditions.getApproversList())) {
            //has sign up approver
            int fIndex = 1;
            for (String s : bpmnStartConditions.getApproversList()) {
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
        return emList;
    }
}
