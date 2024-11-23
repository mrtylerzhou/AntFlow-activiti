package org.openoa.engine.bpmnconf.activitilistener;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessNoticeEnum;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.ActivitiBpmMsgVo;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.common.NodeAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.base.constant.enums.ProcessNodeEnum;
import org.openoa.engine.bpmnconf.confentity.BpmFlowrunEntrust;
import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.engine.bpmnconf.confentity.BpmnNode;
import org.openoa.engine.bpmnconf.constant.enus.EventTypeEnum;
import org.openoa.engine.bpmnconf.service.biz.BpmVariableMessageListenerServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.UserEntrustServiceImpl;
import org.openoa.engine.bpmnconf.util.ActivitiTemplateMsgUtils;
import org.openoa.engine.vo.BpmVariableMessageVo;
import org.openoa.engine.vo.ProcessInforVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @ description: bpmn node execution listener
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class BpmnTaskListener implements TaskListener {

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
        //bpmn conf
        BpmnConf bpmnConf = bpmnConfService.getBaseMapper().selectOne(new QueryWrapper<BpmnConf>()
                .eq("bpmn_code", bpmnCode));

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


        if (bpmnConf==null) {
            log.error("Task监听-查询流程配置数据为空，流程编号{}", processNumber);
            throw new JiMuBizException("Task监听-查询流程配置数据为空");
        }


        boolean isOutside = Optional.ofNullable(bpmnConf.getIsOutSideProcess()).orElse(0).equals(1);


        if (bpmVariableMessageListenerService.listenerCheckIsSendByTemplate(bpmVariableMessageVo)) {
            //set is outside
            bpmVariableMessageVo.setIsOutside(isOutside);

            //set template message
            bpmVariableMessageListenerService.listenerSendTemplateMessages(bpmVariableMessageVo);
        } else {

            ActivitiTemplateMsgUtils.sendBpmApprovalMsg(
                    ActivitiBpmMsgVo
                            .builder()
                            .userId(delegateTask.getAssignee())
                            .processId(processNumber)
                            .bpmnCode(bpmnCode)
                            .formCode(formCode)
                            .processType("")//todo set process type
                            .processName(bpmnConf.getBpmnName())
                            .emailUrl(processBusinessContans.getRoute(ProcessNoticeEnum.EMAIL_TYPE.getCode(),
                                    ProcessInforVo
                                            .builder()
                                            .processinessKey(bpmnCode)
                                            .businessNumber(processNumber)
                                            .formCode(formCode)
                                            .type(2)
                                            .build(), isOutside))
                            .url(processBusinessContans.getRoute(ProcessNoticeEnum.EMAIL_TYPE.getCode(),
                                    ProcessInforVo
                                            .builder()
                                            .processinessKey(bpmnCode)
                                            .businessNumber(processNumber)
                                            .formCode(formCode)
                                            .type(2)
                                            .build(), isOutside))
                            .appPushUrl(processBusinessContans.getRoute(ProcessNoticeEnum.APP_TYPE.getCode(),
                                    ProcessInforVo
                                            .builder()
                                            .processinessKey(bpmnCode)
                                            .businessNumber(processNumber)
                                            .formCode(formCode)
                                            .type(2)
                                            .build(), isOutside))
                            .taskId(delegateTask.getProcessInstanceId())
                            .build());
        }
    }
}
