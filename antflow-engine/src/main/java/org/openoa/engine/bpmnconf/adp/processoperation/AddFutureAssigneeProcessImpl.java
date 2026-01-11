package org.openoa.engine.bpmnconf.adp.processoperation;

import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.dto.NodeElementDto;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.biz.AbstractAddOrRemoveFutureAssigneeSerivceImpl;
import org.springframework.stereotype.Component;

@Component
public class AddFutureAssigneeProcessImpl extends AbstractAddOrRemoveFutureAssigneeSerivceImpl implements ProcessOperationAdaptor {
    @Override
    public void doProcessButton(BusinessDataVo vo) {
        super.checkParam(vo);
        String processNumber=vo.getProcessNumber();
        String nodeId=vo.getNodeId();
        BpmBusinessProcess bpmBusinessProcess = businessProcessService.getBpmBusinessProcess(processNumber);
        if(null==bpmBusinessProcess){
            throw new AFBizException("未能根据流程编号找到流程信息:"+processNumber);
        }
        NodeElementDto nodeElementDto = bpmVariableBizService.queryElementIdByNodeIdDetail(processNumber, nodeId);
        if(nodeElementDto==null){
            throw new AFBizException("未能根据节点id获取元素Id"+nodeId);
        }
        super.modifyFutureAssigneesByProcessInstance(bpmBusinessProcess,nodeElementDto,vo.getUserInfos(),1);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_ADD_FUTURE_ASSIGNEE);
    }
}
