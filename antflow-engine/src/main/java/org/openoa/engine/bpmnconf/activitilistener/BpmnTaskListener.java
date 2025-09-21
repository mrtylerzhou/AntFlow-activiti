package org.openoa.engine.bpmnconf.activitilistener;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.constant.enums.ProcessNoticeEnum;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.ActivitiBpmMsgVo;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.engine.bpmnconf.common.NodeAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.base.constant.enums.ProcessNodeEnum;
import org.openoa.base.entity.BpmFlowrunEntrust;
import org.openoa.base.entity.BpmProcessForward;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.constant.enums.EventTypeEnum;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmVariableMessageListenerServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessForwardServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.UserEntrustServiceImpl;
import org.openoa.engine.utils.ActivitiTemplateMsgUtils;
import org.openoa.base.vo.BpmVariableMessageVo;
import org.openoa.engine.vo.ProcessInforVo;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
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

    private FixedValue extraInfo;
    public void setExtraInfo(FixedValue extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Resource
    private BpmnConfServiceImpl bpmnConfService;

    @Resource
    private ProcessBusinessContans processBusinessContans;

    @Resource
    private UserEntrustServiceImpl userEntrustService;

    @Resource
    private BpmFlowrunEntrustServiceImpl bpmFlowrunEntrustService;
    @Resource
    private BpmVariableMessageListenerServiceImpl bpmVariableMessageListenerService;
    @Resource
    private NodeAdditionalInfoServiceImpl nodeAdditionalInfoService;
    @Resource
    private BpmVariableMapper bpmVariableMapper;
    @Resource
    private BpmProcessForwardServiceImpl bpmProcessForwardService;


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
        boolean isCarbonCopyNode=false;
        if(extraInfo!=null){
            String expressionText = extraInfo.getExpressionText();
            if(!StringUtils.isEmpty(expressionText)){
                delegateTask.setFormKey(expressionText);
                NodeExtraInfoDTO extraInfoDTO = JSON.parseObject(expressionText, NodeExtraInfoDTO.class);
                List<BpmnNodeLabelVO> nodeLabelVOS = extraInfoDTO.getNodeLabelVOS();
                if (!CollectionUtils.isEmpty(nodeLabelVOS)) {
                    for (BpmnNodeLabelVO nodeLabelVO : nodeLabelVOS) {
                        if (StringConstants.COPY_NODE.equals(nodeLabelVO.getLabelValue())) {
                            String processInstanceId = delegateTask.getProcessInstanceId();
                            String elementId=delegateTask.getTaskDefinitionKey();
                            //如果是最后一个节点通知,在BpmnExecutionListener里面处理,这里跳过,减少数据库查询
                            if(StringConstants.LASTNODE_COPY.equals(elementId)){
                                continue;
                            }
                            List<String> nodeIdsByeElementId = bpmVariableMapper.getNodeIdsByeElementId(processNumber, elementId);
                            if(!CollectionUtils.isEmpty(nodeIdsByeElementId)){
                                String nodeId = nodeIdsByeElementId.get(0);
                                LambdaQueryWrapper<BpmProcessForward> qryWrapper = Wrappers.<BpmProcessForward>lambdaQuery()
                                        .eq(BpmProcessForward::getProcessNumber, processNumber)
                                        .eq(BpmProcessForward::getNodeId, nodeId);
                                BpmProcessForward processForward=new BpmProcessForward();
                                processForward.setProcessInstanceId(processInstanceId);
                                processForward.setIsDel(0);//recover the default state,so that the forward record can be visible
                                bpmProcessForwardService.update(processForward, qryWrapper);
                            }
                        }
                        if(StringConstants.COPY_NODEV2.equals(nodeLabelVO.getLabelValue())){
                            isCarbonCopyNode=true;
                            bpmProcessForwardService.addProcessForward(BpmProcessForward.builder()
                                    .createTime(new Date())
                                    .createUserId(SecurityUtils.getLogInEmpId())
                                    .forwardUserId(delegateTask.getAssignee())
                                    .ForwardUserName(delegateTask.getAssignee())
                                    .processInstanceId(procInstId)
                                    .processNumber(processNumber)
                                    .build());
                        }
                    }
                }
            }
        }

        if(isCarbonCopyNode){
            if(delegateTask instanceof  TaskEntity){
                delegateTask.setAssignee(AFSpecialAssigneeEnum.CC_NODE.getId());
                String assigneeName=AFSpecialAssigneeEnum.CC_NODE.getDesc()+"("+((TaskEntity) delegateTask).getAssigneeName()+")";
                ((TaskEntity)delegateTask).setAssigneeName(assigneeName);
                Map<String,Object> varMap=new HashMap<>();
                varMap.put(StringConstants.TASK_ASSIGNEE_NAME,assigneeName);
                ((TaskEntity) delegateTask).complete(varMap,false);
            }
        }

        //set process entrust info
        String oldUserId = delegateTask.getAssignee();
        String oldUserName="";
        if(delegateTask instanceof TaskEntity){
            oldUserName=((TaskEntity)delegateTask).getAssigneeName();
        }
        BaseIdTranStruVo entrustEmployee = userEntrustService.getEntrustEmployee(oldUserId,oldUserName, formCode);
        String userId =entrustEmployee.getId();
        String userName=entrustEmployee.getName();

        //if userId is not null and valid then set user task delegate
        if (!StringUtils.isEmpty(userId)) {
            delegateTask.setAssignee(userId);
            if(delegateTask instanceof  TaskEntity){
                ((TaskEntity)delegateTask).setAssigneeName(userName);
            }
        }



        //如果委托生效 则在我的委托列表中加一条数据
        if (!oldUserId.equals(userId)) {
            BpmFlowrunEntrust entrust = new BpmFlowrunEntrust();
            entrust.setType(1);
            entrust.setRuntaskid(delegateTask.getId());
            entrust.setActual(userId);
            entrust.setActualName(userName);
            entrust.setOriginal(oldUserId);
            entrust.setOriginalName(oldUserName);
            entrust.setIsRead(2);
            entrust.setProcDefId(formCode);
            entrust.setRuninfoid(delegateTask.getProcessInstanceId());
            bpmFlowrunEntrustService.addFlowrunEntrust(entrust);
            log.info("委托生效，委托前：{}，委托后；{}", oldUserId, userId);
        }

        BpmVariableMessageVo bpmVariableMessageVo = BpmVariableMessageVo
                .builder()
                .processNumber(processNumber)
                .formCode(formCode)
                .eventType(EventTypeEnum.PROCESS_FLOW.getCode())
                .messageType(Boolean.TRUE.equals(EventTypeEnum.PROCESS_FLOW.getIsInNode()) ? 2 : 1)
                .elementId(delegateTask.getTaskDefinitionKey())
                .assignee(delegateTask.getAssignee())
                .taskId(delegateTask.getId())
                .eventTypeEnum(EventTypeEnum.PROCESS_FLOW)
                .type(2)
                .delegateTask(delegateTask)
                .build();




        if (bpmVariableMessageListenerService.listenerCheckIsSendByTemplate(bpmVariableMessageVo)) {
            //set is outside
            bpmVariableMessageVo.setIsOutside(isOutside);

            //set template message
            bpmVariableMessageListenerService.listenerSendTemplateMessages(bpmVariableMessageVo);
        } else {
            ProcessInforVo processInforVo = ProcessInforVo
                    .builder()
                    .processinessKey(bpmnCode)
                    .businessNumber(processNumber)
                    .formCode(formCode)
                    .type(2)
                    .build();

            ActivitiTemplateMsgUtils.sendBpmApprovalMsg(
                    ActivitiBpmMsgVo
                            .builder()
                            .userId(delegateTask.getAssignee())
                            .processId(processNumber)
                            .bpmnCode(bpmnCode)
                            .formCode(formCode)
                            .processType("")//todo set process type
                            .processName(bpmnName)
                            .emailUrl(processBusinessContans.getRoute(ProcessNoticeEnum.EMAIL_TYPE.getCode(),
                                    processInforVo , isOutside))
                            .url(processBusinessContans.getRoute(ProcessNoticeEnum.EMAIL_TYPE.getCode(),
                                    processInforVo, isOutside))
                            .appPushUrl(processBusinessContans.getRoute(ProcessNoticeEnum.APP_TYPE.getCode(),
                                    processInforVo, isOutside))
                            .taskId(delegateTask.getProcessInstanceId())
                            .build());
        }
    }
}
