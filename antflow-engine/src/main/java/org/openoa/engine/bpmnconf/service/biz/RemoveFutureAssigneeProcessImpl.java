package org.openoa.engine.bpmnconf.service.biz;

import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BusinessDataVo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemoveFutureAssigneeProcessImpl extends AbstractAddOrRemoveFutureAssigneeSerivceImpl implements ProcessOperationAdaptor {
    @Override
    public void doProcessButton(BusinessDataVo vo) {
       super.checkParam(vo);
       String processNumber=vo.getProcessNumber();
       String nodeId=vo.getNodeId();
       BpmBusinessProcess bpmBusinessProcess = businessProcessService.getBpmBusinessProcess(processNumber);
       if(null==bpmBusinessProcess){
           throw new JiMuBizException("未能根据流程编号找到流程信息:"+processNumber);
       }
        String elementId = bpmVariableMultiplayerMapper.getElementIdByNodeId(processNumber, nodeId);
        if(StringUtils.isEmpty(elementId)){
            throw new JiMuBizException("未能根据节点id获取元素Id"+nodeId);
        }
        super.modifyFutureAssigneesByProcessInstance(bpmBusinessProcess,elementId,vo.getUserInfos(),2);

    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_REMOVE_FUTURE_ASSIGNEE);
    }
}
