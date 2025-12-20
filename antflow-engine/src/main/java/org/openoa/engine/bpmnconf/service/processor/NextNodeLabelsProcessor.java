package org.openoa.engine.bpmnconf.service.processor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.dto.BpmNextTaskDto;
import org.openoa.base.entity.BpmProcessForward;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.AFWrappers;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessForwardServiceImpl;
import org.openoa.engine.factory.FormFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NextNodeLabelsProcessor implements AntFlowNextNodeBeforeWriteProcessor {
    @Resource
    private BpmProcessForwardServiceImpl bpmProcessForwardService;
    @Resource
    private BpmVariableMapper bpmVariableMapper;
    @Resource
    private FormFactory formFactory;
    
    @Override
    public void postProcess(BpmNextTaskDto bpmnNextTaskDto) {
        List<BpmnNodeLabelVO> nodeLabelVOS = bpmnNextTaskDto.getNodeLabels();
        if(CollectionUtils.isEmpty(nodeLabelVOS)){
            return;
        }
        String procInstId= bpmnNextTaskDto.getProcessInstanceId();
        String elementId= bpmnNextTaskDto.getTaskDefKey();
        String processNumber= bpmnNextTaskDto.getProcessNumber();
        String assignee= bpmnNextTaskDto.getAssignee();
        String assigneeName= bpmnNextTaskDto.getTaskName();
        BusinessDataVo businessDataVo = bpmnNextTaskDto.getBusinessDataVo();
        String formCode= bpmnNextTaskDto.getFormCode();
        Boolean isOutSide = bpmnNextTaskDto.getIsOutSide();
        DelegateTask delegateTask = bpmnNextTaskDto.getDelegateTask();
        
        for (BpmnNodeLabelVO nodeLabelVO : nodeLabelVOS) {
            processCopy(elementId, processNumber, procInstId,nodeLabelVO);
            processCopyV2(nodeLabelVO, procInstId, assignee, assigneeName, processNumber,delegateTask);
            processAutomaticNode(nodeLabelVO, processNumber, elementId, formCode, businessDataVo, isOutSide);
        }
    }

    private void processAutomaticNode(BpmnNodeLabelVO nodeLabelVO, String processNumber, String elementId, String formCode, BusinessDataVo businessDataVo, Boolean isOutSide) {
        
        if (!StringConstants.AUTOMATIC_NODE.equals(nodeLabelVO.getLabelValue())){
            return;
        }
        BusinessDataVo vo=new BusinessDataVo();
        vo.setProcessNumber(processNumber);
        vo.setTaskDefKey(elementId);
        vo.setFormCode(formCode);
        vo.setIsLowCodeFlow(businessDataVo.getIsLowCodeFlow());
        vo.setFormData(formCode);
        vo.setIsOutSideAccessProc(isOutSide);
        FormOperationAdaptor formAdaptor = formFactory.getFormAdaptor(vo);
        if(formAdaptor==null){
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR,"未能根据流程formcode找到流程适配器信息!");
        }

        BusinessDataVo convertedBusinessDatavo = formFactory.dataFormConversion(vo);
        Boolean conditionResult = formAdaptor.automaticCondition(convertedBusinessDatavo);
        formAdaptor.automaticAction(convertedBusinessDatavo,conditionResult);
    }

    private void processCopyV2(BpmnNodeLabelVO nodeLabelVO, String procInstId, String assignee, String assigneeName, String processNumber,DelegateTask delegateTask) {
        if(!StringConstants.COPY_NODEV2.equals(nodeLabelVO.getLabelValue())){
            return;
        }
        List<BpmProcessForward> bpmProcessForwards = bpmProcessForwardService.list(AFWrappers.<BpmProcessForward>lambdaTenantQuery()
                .eq(BpmProcessForward::getProcessInstanceId, procInstId)
                .eq(BpmProcessForward::getForwardUserId, assignee));
        if(CollectionUtils.isEmpty(bpmProcessForwards)){
            bpmProcessForwardService.addProcessForward(BpmProcessForward.builder()
                    .createTime(new Date())
                    .createUserId(SecurityUtils.getLogInEmpId())
                    .forwardUserId(assignee)
                    .ForwardUserName(assigneeName)
                    .processInstanceId(procInstId)
                    .processNumber(processNumber)
                    .build());
        }
        if(delegateTask instanceof TaskEntity){
            delegateTask.setAssignee(AFSpecialAssigneeEnum.CC_NODE.getId());
            String asseeName=AFSpecialAssigneeEnum.CC_NODE.getDesc()+"("+((TaskEntity) delegateTask).getAssigneeName()+")";
            ((TaskEntity)delegateTask).setAssigneeName(asseeName);
            Map<String,Object> varMap=new HashMap<>();
            varMap.put(StringConstants.TASK_ASSIGNEE_NAME,asseeName);
            ((TaskEntity) delegateTask).complete(varMap,false);
        }
    }

    private void processCopy(String elementId, String processNumber, String procInstId,BpmnNodeLabelVO nodeLabelVO) {
        if (!StringConstants.COPY_NODE.equals(nodeLabelVO.getLabelValue())) {
            return; 
        }
        //如果是最后一个节点通知,在BpmnExecutionListener里面处理,这里跳过,减少数据库查询
        if(StringConstants.LASTNODE_COPY.equals(elementId)){
            return;
        }
        List<String> nodeIdsByeElementId = bpmVariableMapper.getNodeIdsByeElementId(processNumber, elementId);
        if(!CollectionUtils.isEmpty(nodeIdsByeElementId)){
            String nodeId = nodeIdsByeElementId.get(0);
            LambdaQueryWrapper<BpmProcessForward> qryWrapper = Wrappers.<BpmProcessForward>lambdaQuery()
                    .eq(BpmProcessForward::getProcessNumber, processNumber)
                    .eq(BpmProcessForward::getNodeId, nodeId);
            BpmProcessForward processForward=new BpmProcessForward();
            processForward.setProcessInstanceId(procInstId);
            processForward.setIsDel(0);//recover the default state,so that the forward record can be visible
            bpmProcessForwardService.update(processForward, qryWrapper);
        }
    }

    private void processAutoSkipNode(BpmnNodeLabelVO nodeLabelVO,String nodeAssignee,String procInstId,String startUserName,String processNumber,DelegateTask delegateTask){
        if (!StringConstants.SKIPPED_ASSIGNEE.equals(nodeLabelVO.getLabelValue())) {
            return;
        }
        String labelName = nodeLabelVO.getLabelName();
        List<String> skippedAssigneeIds = Arrays.stream(labelName.split(",")).filter(StringUtils::hasText).collect(Collectors.toList());
        List<String> currentSkippedAssignee = skippedAssigneeIds.stream().filter(a -> a.contains(nodeAssignee)).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(currentSkippedAssignee)){
            String assigneeName=((TaskEntity)delegateTask).getAssigneeName();
            Map<String,Object> varMap=new HashMap<>();
            varMap.put(StringConstants.TASK_ASSIGNEE_NAME,assigneeName);
            ((TaskEntity) delegateTask).complete(varMap,false);
            //save process verify info
            BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                    .builder()
                    .verifyDate(new Date())
                    .taskName(delegateTask.getName())
                    .taskId(delegateTask.getId())
                    .runInfoId(procInstId)
                    .verifyUserId(delegateTask.getAssignee())
                    .verifyUserName(startUserName)
                    .taskDefKey(delegateTask.getTaskDefinitionKey())
                    .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                    .verifyDesc(StringConstants.AF_AUTO_SKIP_COMMENT)
                    .processCode(processNumber)
                    .build();
        }
    }
    @Override
    public int order() {
        return 0;
    }
}
