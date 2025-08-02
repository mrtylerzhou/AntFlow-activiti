package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.collect.Lists;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.util.AssigneeVoBuildUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author TylerZhou
 * @Date 2024/7/17 22:02
 * @Version 1.0
 */
@Component
public class StartUserPersonnelProvider implements BpmnPersonnelProviderService {
    @Autowired
    private AssigneeVoBuildUtils assigneeVoBuildUtils;
    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        if(startConditionsVo.getStartUserId()==null){
            throw new AFBizException("未获取到发起人信息!");
        }
        String startUserId = startConditionsVo.getStartUserId();
        String elementName=bpmnNodeVo.getNodeName();
        List<BpmnNodeParamsAssigneeVo> bpmnNodeParamsAssigneeVos = assigneeVoBuildUtils.buildVos(Lists.newArrayList(startUserId), elementName,false);
        return bpmnNodeParamsAssigneeVos;
    }
}
