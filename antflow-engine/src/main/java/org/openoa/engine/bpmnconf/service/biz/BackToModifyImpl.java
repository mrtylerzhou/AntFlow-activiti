package org.openoa.engine.bpmnconf.service.biz;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.task.Task;
import org.activiti.engine.impl.cmd.ProcessNodeJump;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.common.ProcessConstants;
import org.openoa.engine.bpmnconf.common.TaskMgmtServiceImpl;
import org.openoa.base.constant.enums.ProcessDisagreeTypeEnum;
import org.openoa.base.constant.enums.ProcessNodeEnum;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.engine.bpmnconf.confentity.BpmProcessNodeSubmit;
import org.openoa.engine.bpmnconf.confentity.BpmVerifyInfo;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.flowcontrol.DefaultTaskFlowControlServiceFactory;
import org.openoa.engine.bpmnconf.service.flowcontrol.TaskFlowControlService;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessNodeSubmitServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVerifyInfoServiceImpl;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.entity.BpmBusinessProcess;

import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.base.util.ProcessDefinitionUtils;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.security.Provider;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * back to modify
 */
@Slf4j
@Component
public class BackToModifyImpl implements ProcessOperationAdaptor {

    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    @Autowired
    private BpmVerifyInfoServiceImpl verifyInfoService;

    @Autowired
    private BpmProcessNodeSubmitServiceImpl processNodeSubmitService;

    @Autowired
    private ProcessNodeJump processNodeJump;

    @Autowired
    private FormFactory formFactory;

    @Autowired
    protected TaskMgmtServiceImpl taskMgmtService;
    @Autowired
    private ProcessConstants processConstants;
    @Autowired
    private BpmVariableMapper variableMapper;
    @Autowired
    private ActivitiAdditionalInfoServiceImpl additionalInfoService;
    @Autowired
    private DefaultTaskFlowControlServiceFactory taskFlowControlServiceFactory;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;

    @Autowired
    private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;
    @Autowired
    private RuntimeService runtimeService;


    @Override
    public void doProcessButton(BusinessDataVo vo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        if (bpmBusinessProcess == null) {
            throw new JiMuBizException("未查询到流程信息!");
        }
        String procInstId = bpmBusinessProcess.getProcInstId();
        //get a list of running tasks,then reject all of them
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if (CollectionUtils.isEmpty(taskList)) {
            throw new JiMuBizException("未获取到当前流程信息!,流程编号:" + bpmBusinessProcess.getProcessinessKey());
        }
        Task taskData = taskList.stream().filter(a -> a.getId().equals(vo.getTaskId())).findFirst().orElse(null);

        if (taskData == null) {
            throw new JiMuBizException("当前流程已审批！");
        }

        List<String> taskDefKeys = taskList.stream().map(TaskInfo::getTaskDefinitionKey).distinct().collect(Collectors.toList());

        String restoreNodeKey;
        String backToNodeKey;

        Integer backToModifyType = vo.getBackToModifyType();
        if (backToModifyType == null) {
            backToModifyType = ProcessDisagreeTypeEnum.THREE_DISAGREE.getCode();
        }

        if (taskDefKeys.size() > 1 && backToModifyType == ProcessDisagreeTypeEnum.FIVE_DISAGREE.getCode()) {
            backToModifyType = ProcessDisagreeTypeEnum.FOUR_DISAGREE.getCode();
        }
        ProcessDisagreeTypeEnum processDisagreeTypeEnum = ProcessDisagreeTypeEnum.getByCode(backToModifyType);
        switch (processDisagreeTypeEnum) {
            case ONE_DISAGREE:
                HistoricTaskInstance prevTask = processConstants.getPrevTask(taskData.getTaskDefinitionKey(), procInstId);
                if (prevTask == null) {
                    throw new JiMuBizException("无前置节点,无法回退上一节点!");
                }
                restoreNodeKey = taskData.getTaskDefinitionKey();
                backToNodeKey = prevTask.getTaskDefinitionKey();
                break;
            case TWO_DISAGREE:
                restoreNodeKey = ProcessNodeEnum.TOW_TASK_KEY.getDesc();
                backToNodeKey = ProcessNodeEnum.START_TASK_KEY.getDesc();
                break;
            case THREE_DISAGREE://default behavior
                restoreNodeKey = taskData.getTaskDefinitionKey();
                backToNodeKey = ProcessNodeEnum.START_TASK_KEY.getDesc();
                break;
            case FOUR_DISAGREE:
                String elementId = variableMapper.getElementIdsdByNodeId(vo.getProcessNumber(), vo.getBackToNodeId()).get(0);
                backToNodeKey = elementId;
                PvmActivity nextElement = additionalInfoService.getNextElement(elementId, bpmBusinessProcess.getProcInstId());

                String type = (String) nextElement.getProperty("type");
                if ("parallelGateway".equals(type)) {
                    if (nextElement.getOutgoingTransitions().size() > 1) {
                        restoreNodeKey = "";
                    } else {
                        restoreNodeKey = nextElement.getOutgoingTransitions().get(0).getDestination().getId();
                    }
                } else {
                    restoreNodeKey = nextElement.getId();
                }
                break;
            case FIVE_DISAGREE:
                restoreNodeKey = taskData.getTaskDefinitionKey();
                backToNodeKey = variableMapper.getElementIdsdByNodeId(vo.getProcessNumber(), vo.getBackToNodeId()).get(0);
                break;
            default:
                throw new JiMuBizException("未支持的退回类型!");
        }
        //save verify info
        verifyInfoService.addVerifyInfo(BpmVerifyInfo.builder()
                .businessId(bpmBusinessProcess.getBusinessId())
                .verifyUserName(vo.getStartUserName())
                .verifyUserId(vo.getStartUserId())
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_UPDATE_TYPE.getCode())
                .processCode(bpmBusinessProcess.getBusinessNumber())
                .runInfoId(bpmBusinessProcess.getProcInstId())
                .verifyDesc(vo.getApprovalComment())
                .taskName(taskData.getName())
                .taskId(taskData.getId())
                .build());

        if (!StringUtils.isEmpty(restoreNodeKey)) {
            //add back node
            processNodeSubmitService.addProcessNode(BpmProcessNodeSubmit.builder()
                    .state(1)
                    .nodeKey(restoreNodeKey)
                    .processInstanceId(taskData.getProcessInstanceId())
                    .backType(backToModifyType)
                    .createUser(vo.getStartUserId())
                    .build());
        }
        //boolean userTaskParallel = ProcessDefinitionUtils.isUserTaskParallel(taskData.getProcessInstanceId(), backToNodeKey);
        if (ProcessDefinitionUtils.isUserTaskParallel(taskData)) {
            TaskFlowControlService taskFlowControlService = taskFlowControlServiceFactory.create(taskData.getProcessInstanceId(), bpmVariableMultiplayerService);
            try {
                List<String> strings = taskFlowControlService.moveTo(taskData.getTaskDefinitionKey(), backToNodeKey).stream().distinct().collect(Collectors.toList());
                if (strings.size() > 1) {
                    strings = strings.stream().filter(a -> !a.equals(taskData.getTaskDefinitionKey())).collect(Collectors.toList());
                    taskMgmtMapper.deleteExecutionsByProcinstIdAndTaskDefKeys(taskData.getProcessInstanceId(), strings);
                }
            } catch (Exception e) {
                log.error("流程回退出错了!", e);
                throw new JiMuBizException("流程回退出错了!");
            }

        } else {
            try {
                processNodeJump.commitProcess(taskData.getId(), null, backToNodeKey);
            } catch (Exception e) {
                log.error("流程回退出错了!", e);
                throw new JiMuBizException("流程回退出错了!");
            }

        }


        //parallel tasks reject
           /* for (Task task : taskList) {
                Map<String,Object> varMap=new HashMap<>();
                varMap.put(StringConstants.TASK_ASSIGNEE_NAME,task.getAssigneeName());
                //do reject
                processNodeJump.commitProcess(task.getId(), varMap, backToNodeKey);
            }*/
        vo.setBusinessId(bpmBusinessProcess.getBusinessId());
        if (!vo.getIsOutSideAccessProc()) {
            formFactory.getFormAdaptor(vo).backToModifyData(vo);
        }


        // back to specified person
        if (!ObjectUtils.isEmpty(vo.getBackToEmployeeId())) {
            //save back userId
            bpmBusinessProcess.setBackUserId(vo.getBackToEmployeeId());
            bpmBusinessProcessService.updateById(bpmBusinessProcess);

            Task task = taskList.get(0);
            TaskMgmtVO taskMgmtVO = TaskMgmtVO.builder().taskIds(Collections.singletonList(task.getId())).applyUser(vo.getBackToEmployeeId()).build();
            taskMgmtService.updateTask(taskMgmtVO);
        }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_BACK_TO_MODIFY);
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(), ProcessOperationEnum.BUTTON_TYPE_BACK_TO_MODIFY);
    }
}
