package org.openoa.engine.bpmnconf.activitilistener;


import com.alibaba.fastjson2.JSON;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.dto.BpmNextTaskDto;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.service.ProcessorFactory;
import org.openoa.base.util.ThreadLocalContainer;
import org.openoa.base.vo.*;
import org.openoa.base.constant.enums.ProcessNodeEnum;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @ description: bpmn node execution listener
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
@Scope("prototype")
public class BpmnTaskListener implements TaskListener {

    @Setter
    private FixedValue extraInfo;

    @Override
    public void notify(DelegateTask delegateTask) {

        //if it is the start user node then do nothing
        if (delegateTask.getTaskDefinitionKey().equals(ProcessNodeEnum.START_TASK_KEY.getDesc())) {
            return;
        }
        //bpmnCode
        String bpmnCode = Optional.ofNullable(delegateTask.getVariable("bpmnCode"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //processNumber
        String processNumber = Optional.ofNullable(delegateTask.getVariable("processNumber"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //formCode
        String formCode = Optional.ofNullable(delegateTask.getVariable("formCode"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //businessId
        String businessId = Optional.ofNullable(delegateTask.getVariable("businessId"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //start user id
        String startUser = Optional.ofNullable(delegateTask.getVariable("startUser"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        boolean isOutside=Optional.ofNullable(delegateTask.getVariable(StringConstants.ActVarKeys.Is_OUTSIDEPROC)).map(t->Boolean.valueOf(t.toString())).orElse(false);
        String procInstId=Optional.ofNullable(delegateTask.getVariable(StringConstants.ActVarKeys.PROCINSTID)).map(Object::toString).orElse(StringUtils.EMPTY);
        String bpmnName=Optional.ofNullable(delegateTask.getVariable(StringConstants.ActVarKeys.BPMN_NAME)).map(Object::toString).orElse(StringUtils.EMPTY);

        BpmNextTaskDto nextTaskDto=new BpmNextTaskDto();
        nextTaskDto.setProcessNumber(processNumber);
        nextTaskDto.setBpmnCode(bpmnCode);
        nextTaskDto.setBpmnName(bpmnName);
        nextTaskDto.setFormCode(formCode);
        nextTaskDto.setBusinessId(businessId);
        nextTaskDto.setStartUser(startUser);
        nextTaskDto.setTaskDefKey(delegateTask.getTaskDefinitionKey());
        nextTaskDto.setIsOutSide(isOutside);
        nextTaskDto.setProcessInstanceId(procInstId);
        nextTaskDto.setTaskId(delegateTask.getId());
        nextTaskDto.setTaskName(delegateTask.getName());
        nextTaskDto.setAssignee(delegateTask.getAssignee());
        nextTaskDto.setDelegateTask(delegateTask);
        if(extraInfo!=null){
            String expressionText = extraInfo.getExpressionText();
            delegateTask.setFormKey(expressionText);
            NodeExtraInfoDTO extraInfoDTO = JSON.parseObject(expressionText, NodeExtraInfoDTO.class);
            nextTaskDto.setNodeLabels(extraInfoDTO.getNodeLabelVOS());
        }
        BusinessDataVo businessDataVo= (BusinessDataVo)ThreadLocalContainer.get(StringConstants.AF_RUNTIME_BUISINESS_INFO);
        nextTaskDto.setBusinessDataVo(businessDataVo);

        //AntFlowNextNodeBeforeWriteProcessor
        ProcessorFactory.executePostProcessors(nextTaskDto);

    }
}
