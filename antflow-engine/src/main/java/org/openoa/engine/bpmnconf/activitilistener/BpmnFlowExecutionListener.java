package org.openoa.engine.bpmnconf.activitilistener;

import com.alibaba.fastjson2.JSON;
import org.activiti.engine.EngineServices;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.el.FixedValue;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.service.BpmVariableService;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BpmnFlowExecutionListener implements ExecutionListener {
    @Autowired
    private BpmVariableService bpmVariableService;
    private FixedValue extraInfo;
    public void setExtraInfo(FixedValue extraInfo) {
        this.extraInfo = extraInfo;
    }


    @Override
    public void notify(DelegateExecution execution) throws Exception {
        EngineServices engineServices = execution.getEngineServices();
        if(extraInfo!=null){
            NodeExtraInfoDTO extraInfoDTO = JSON.parseObject(extraInfo.getExpressionText(), NodeExtraInfoDTO.class);
            List<BpmnNodeLabelVO> nodeLabelVOS = extraInfoDTO.getNodeLabelVOS();
            Map<String, Object> variables = execution.getVariables();
            String processNumber=variables.get("processNumber").toString();
            String currentActivityId = execution.getCurrentActivityId();
            String variableName = bpmVariableService.getVarNameByProcessNumberAndElementId(processNumber, currentActivityId);
            //execution.setVariable(variableName,new ArrayList<>());
        }

    }
}
