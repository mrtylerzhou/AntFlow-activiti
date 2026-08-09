package org.openoa.engine.bpmnconf.adp.processoperation;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.task.Task;
import org.activiti.engine.impl.cmd.ProcessNodeJump;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.*;
import org.openoa.base.entity.ActHiTaskinst;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.util.NodeUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.service.BpmVariableMultiplayerPersonnelServiceImpl;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.common.ProcessConstants;
import org.openoa.engine.bpmnconf.common.TaskMgmtServiceImpl;
import org.openoa.base.entity.BpmProcessNodeSubmit;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.flowcontrol.DefaultTaskFlowControlServiceFactory;
import org.openoa.engine.bpmnconf.service.flowcontrol.TaskFlowControlService;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.entity.BpmBusinessProcess;

import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.base.util.ProcessDefinitionUtils;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNodeSubmitService;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * back to modify
 */
@Slf4j
@Component
public class BackToModifyImpl implements ProcessOperationAdaptor {

    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;

    @Autowired
    private BpmProcessNodeSubmitService processNodeSubmitService;

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
    private HistoryService historyService;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;
    @Autowired
    private BpmVariableMultiplayerPersonnelServiceImpl bpmVariableMultiplayerPersonnelService;
    @Autowired
    private RuntimeService runtimeService;


    @Override
    public void doProcessButton(BusinessDataVo vo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        if (bpmBusinessProcess == null) {
            throw new AFBizException("未查询到流程信息!");
        }
        String procInstId = bpmBusinessProcess.getProcInstId();
        //get a list of running tasks,then reject all of them
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if (CollectionUtils.isEmpty(taskList)) {
            throw new AFBizException("未获取到当前流程信息!,流程编号:" + bpmBusinessProcess.getProcessinessKey());
        }
        if(!Objects.equals(bpmBusinessProcess.getProcessState(), ProcessStateEnum.HANDLING_STATE.getCode())){
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"当前流程非审批中状态,无法操作");
        }
        Task taskData = taskList.stream().filter(a -> a.getId().equals(vo.getTaskId())).findFirst().orElse(null);
        boolean isStartUserDrawBack=ProcessOperationEnum.BUTTON_TYPE_PROCESS_DRAW_BACK.getCode().equals(vo.getOperationType());
        boolean isOtherApproverDrawBack=ProcessOperationEnum.BUTTON_TYPE_DRAW_BACK_AGREE.getCode().equals(vo.getOperationType());
        if(isStartUserDrawBack||isOtherApproverDrawBack){
            taskData=taskList.get(0);
        }
        if (taskData == null) {
            throw new AFBizException("当前流程已审批！");
        }
        String restoreNodeKey;
        String backToNodeKey;
        if(isStartUserDrawBack){
            String createUser = bpmBusinessProcess.getCreateUser();
            if(!SecurityUtils.getLogInEmpIdSafe().equals(createUser)){
                throw new AFBizException(BusinessErrorEnum.RIGHT_VIOLATE.getCodeStr(),"只有发起人可以操作撤回");
            }
            List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInstId).list();
            List<HistoricTaskInstance> completedTasks = historicTaskInstanceList
                    .stream()
                    .filter(a -> a.getEndTime() != null&&!ProcessNodeEnum.START_TASK_KEY.getDesc().equals(a.getTaskDefinitionKey()))
                    .collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(completedTasks)){
                throw new AFBizException(BusinessErrorEnum.RIGHT_INVALID.getCodeStr(),"已被审批的流程允许撤回!");
            }
            vo.setBackToModifyType(ProcessDisagreeTypeEnum.TWO_DISAGREE.getCode());
        }else if(isOtherApproverDrawBack){
            vo.setBackToModifyType(ProcessDisagreeTypeEnum.FOUR_DISAGREE.getCode());
        }
        List<String> taskDefKeys = taskList.stream().map(TaskInfo::getTaskDefinitionKey).distinct().collect(Collectors.toList());



        Integer backToModifyType = vo.getBackToModifyType();
        if (backToModifyType == null) {
            backToModifyType = ProcessDisagreeTypeEnum.THREE_DISAGREE.getCode();
        }

        if (taskDefKeys.size() > 1 && backToModifyType == ProcessDisagreeTypeEnum.FIVE_DISAGREE.getCode()) {
            backToModifyType = ProcessDisagreeTypeEnum.FOUR_DISAGREE.getCode();
        }
        ProcessDisagreeTypeEnum processDisagreeTypeEnum = ProcessDisagreeTypeEnum.getByCode(backToModifyType);
        String backToNodeId=vo.getBackToNodeId();
        switch (processDisagreeTypeEnum) {
            case ONE_DISAGREE:
                ActHiTaskinst prevTask = processConstants.getPrevTask(taskData.getTaskDefinitionKey(), procInstId);
                if (prevTask == null) {
                    throw new AFBizException("无前置节点,无法回退上一节点!");
                }
                restoreNodeKey = taskData.getTaskDefinitionKey();
                backToNodeKey = prevTask.getTaskDefKey();
                if(ProcessNodeEnum.compare(backToNodeKey,restoreNodeKey)>0){
                    backToNodeKey=ProcessNodeEnum.getGeneralPrevNode(restoreNodeKey);
                }
                //运行时校验:退回上一节点时,若上一节点为自动类型节点(不可人工操作),阻止退回
                //反查 elementId -> t_bpm_variable_multiplayer.node_id(主键),供 NodeUtil 读取节点 JSON 标签
                List<String> prevNodeIds = variableMapper.getNodeIdsByeElementId(vo.getProcessNumber(), backToNodeKey);
                if (!CollectionUtils.isEmpty(prevNodeIds)) {
                    String prevNodeId = prevNodeIds.get(0);
                    if (NodeUtil.isCurrentNodeNoneOperational(prevNodeId)) {
                        throw new AFBizException("上一节点为自动类型节点,无法退回!");
                    }
                }
                break;
            case TWO_DISAGREE:
                restoreNodeKey = ProcessNodeEnum.TWO_TASK_KEY.getDesc();
                backToNodeKey = ProcessNodeEnum.START_TASK_KEY.getDesc();
                break;
            case THREE_DISAGREE://default behavior
                restoreNodeKey = taskData.getTaskDefinitionKey();
                backToNodeKey = ProcessNodeEnum.START_TASK_KEY.getDesc();
                break;
            case FOUR_DISAGREE: {
                String elementId = null;
                if(isOtherApproverDrawBack){
                    String logInEmpId = SecurityUtils.getLogInEmpId();
                    BpmVerifyInfo lastProcessNodeByAssignee = bpmVerifyInfoBizService.getLastProcessNodeByAssignee(bpmBusinessProcess.getBusinessNumber(), logInEmpId);
                    if(lastProcessNodeByAssignee==null){
                        throw new AFBizException(BusinessErrorEnum.DATA_NOT_FOUND.getCodeStr(),"未能找到当前用户的审批信息");
                    }
                    elementId=lastProcessNodeByAssignee.getTaskDefKey();
                }else {
                    elementId= variableMapper.getElementIdsdByNodeId(vo.getProcessNumber(), backToNodeId).get(0);
                }
                //运行时校验:退回指定节点时,若目标节点为自动类型节点(不可人工操作),阻止退回
                //用 elementId 反查 t_bpm_variable_multiplayer.node_id(主键),规避 backToNodeId 可能是 UUID 的类型问题
                List<String> fourTargetNodeIds = variableMapper.getNodeIdsByeElementId(vo.getProcessNumber(), elementId);
                if (!CollectionUtils.isEmpty(fourTargetNodeIds)) {
                    if (NodeUtil.isCurrentNodeNoneOperational(fourTargetNodeIds.get(0))) {
                        throw new AFBizException("不可退回到自动类型节点,请重试!");
                    }
                }
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
            }
            case FIVE_DISAGREE: {
                restoreNodeKey = taskData.getTaskDefinitionKey();
                backToNodeKey = variableMapper.getElementIdsdByNodeId(vo.getProcessNumber(), backToNodeId).get(0);
                //运行时校验:退回指定节点(回到当前节点)时,若目标节点为自动类型节点,阻止退回
                List<String> fiveTargetNodeIds = variableMapper.getNodeIdsByeElementId(vo.getProcessNumber(), backToNodeKey);
                if (!CollectionUtils.isEmpty(fiveTargetNodeIds)) {
                    if (NodeUtil.isCurrentNodeNoneOperational(fiveTargetNodeIds.get(0))) {
                        throw new AFBizException("不可退回到自动类型节点,请重试!");
                    }
                }
                break;
            }
            default:
                throw new AFBizException("未支持的退回类型!");
        }
        //save verify info
        bpmVerifyInfoBizService.addVerifyInfo(BpmVerifyInfo.builder()
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
            TaskFlowControlService taskFlowControlService = taskFlowControlServiceFactory.create(taskData.getProcessInstanceId());
            try {
                List<String> unMovedTasks = taskFlowControlService.moveTo(taskData.getTaskDefinitionKey(), backToNodeKey);
                List<String> strings = unMovedTasks.stream().distinct().collect(Collectors.toList());
                if (strings.size() > 0) {
                    Task finalTaskData = taskData;
                    strings = strings.stream().filter(a -> !a.equals(finalTaskData.getTaskDefinitionKey())).collect(Collectors.toList());
                    taskMgmtMapper.deleteExecutionsByProcinstIdAndTaskDefKeys(taskData.getProcessInstanceId(), strings);
                }
                List<BpmVariableMultiplayer> moreNodes = bpmVariableMultiplayerService.getBaseMapper().isMoreNode(bpmBusinessProcess.getBusinessNumber(), backToNodeKey);
               /* if(!CollectionUtils.isEmpty(moreNodes)&&moreNodes.stream().anyMatch(a-> SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode().equals(a.getSignType()))){
                    Long id = moreNodes.get(0).getId();
                  if(moreNodes.size()>1){
                      LambdaQueryWrapper<BpmVariableMultiplayerPersonnel> updateWrapper = Wrappers.<BpmVariableMultiplayerPersonnel>lambdaQuery()
                              .eq(BpmVariableMultiplayerPersonnel::getVariableMultiplayerId, id);
                      BpmVariableMultiplayerPersonnel multiplayerPersonnel=new BpmVariableMultiplayerPersonnel();
                      multiplayerPersonnel.setUndertakeStatus(0);
                      bpmVariableMultiplayerPersonnelService.update(multiplayerPersonnel,updateWrapper);
                  }
                }*/
                List<Task> tasks = taskService.createTaskQuery().processInstanceId(taskData.getProcessInstanceId()).taskDefinitionKey(backToNodeKey).list();
                if(tasks.size()>1){
                    Task firstTask = tasks.get(0);
                    Set<String> otherNewTaskIds = new HashSet<>();
                    //单节点或签节点
                    boolean isOneNodeSingleOrSign=moreNodes.size()==1&&SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode().equals(moreNodes.get(0).getSignType());
                    //单人节点
                    boolean isSingleSign=moreNodes.size()==1;
                    for (Task task : tasks) {
                        if((isOneNodeSingleOrSign||isSingleSign)&&!task.getId().equals(firstTask.getId())){
                            otherNewTaskIds.add(task.getId());
                        }
                    }
                    if(!CollectionUtils.isEmpty(otherNewTaskIds)){
                        List<String> otherNewTaskIdList = new ArrayList<>(otherNewTaskIds);
                        taskMgmtMapper.deleteExecutionsByProcinstIdAndTaskDefKeys(taskData.getProcessInstanceId(), otherNewTaskIdList);
                        taskMgmtMapper.deleteTaskByTaskIds(otherNewTaskIdList);
                    }
                    boolean isOneNodeAllSign=moreNodes.size()==1&&!SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode().equals(moreNodes.get(0).getSignType());
                    List<Task> otherNewTasks=new ArrayList<>();
                    for (Task task : tasks) {
                        if((isOneNodeAllSign||isSingleSign)&&!task.getId().equals(firstTask.getId())){
                            otherNewTasks.add(task);
                        }
                    }
                    if(!CollectionUtils.isEmpty(otherNewTasks)){
                        for (Task otherNewTask : otherNewTasks) {

                            if(otherNewTaskIds.contains(otherNewTask.getId())){
                                continue;
                            }
                            Map<String,Object> varMap=new HashMap<>();
                            varMap.put(StringConstants.TASK_ASSIGNEE_NAME, otherNewTask.getAssigneeName());
                            try {
                                taskService.complete(otherNewTask.getId(),varMap);
                            }catch (Exception e){
                                log.warn("BackToModify complete task failed: taskId={}, err={}", otherNewTask.getId(), e.getMessage());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("流程回退出错了!", e);
                throw new AFBizException("流程回退出错了!");
            }

        } else {
            try {
                processNodeJump.commitProcess(taskData.getId(), null, backToNodeKey, procInstId);
            } catch (Exception e) {
                log.error("流程回退出错了!", e);
                throw new AFBizException("流程回退出错了!");
            }

        }

        variableMapper.resetUnderStatusByProcessNumber(bpmBusinessProcess.getBusinessNumber());
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
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_DRAW_BACK_AGREE);
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_PROCESS_DRAW_BACK);
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_BACK_TO_MODIFY);
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(), ProcessOperationEnum.BUTTON_TYPE_BACK_TO_MODIFY);
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(), ProcessOperationEnum.BUTTON_TYPE_PROCESS_DRAW_BACK);
    }

    /**
     * 自动退回节点专用: 延迟到当前事务提交后, 在新事务里 complete + 退回到目标节点.
     *
     * 与 advanceToTargetNode 对称, 但方向相反(向后跳), 且需要额外写入:
     * - BpmProcessNodeSubmit(restoreNodeKey, 控制目标节点完成后的跳转)
     * - resetUnderStatusByProcessNumber(重置审批状态)
     * - backToModifyData(表单回调)
     *
     * @param delegateTask      当前任务(自动退回节点任务)
     * @param procInstId        流程实例ID
     * @param processNumber     流程编号
     * @param currentTaskDefKey 当前任务 taskDefinitionKey
     * @param targetElementId   目标节点 elementId(taskDefKey)
     * @param targetNodeName    目标节点名称(用于审批日志)
     * @param verifyUserId      审批日志的 verifyUserId(自动退回用 -3)
     * @param verifyUserName    审批日志的 verifyUserName(自动退回用 "自动退回节点自动退回")
     * @param businessDataVo    业务数据(用于 backToModifyData 回调)
     */
    public void returnToTargetNode(DelegateTask delegateTask, String procInstId, String processNumber,
                                   String currentTaskDefKey, String targetElementId,
                                   String targetNodeName,
                                   String verifyUserId, String verifyUserName,
                                   BusinessDataVo businessDataVo) {
        returnToTargetNode(delegateTask, procInstId, processNumber, currentTaskDefKey, targetElementId,
                targetNodeName, verifyUserId, verifyUserName, businessDataVo, ProcessDisagreeTypeEnum.FOUR_DISAGREE.getCode());
    }

    /**
     * 自动退回/条件退回 统一跳转方法(带 backType 参数).
     * @param backType 退回类型: 4=重新开始, 5=回到当前节点
     */
    public void returnToTargetNode(DelegateTask delegateTask, String procInstId, String processNumber,
                                   String currentTaskDefKey, String targetElementId,
                                   String targetNodeName,
                                   String verifyUserId, String verifyUserName,
                                   BusinessDataVo businessDataVo, Integer backType) {
        String taskId = delegateTask.getId();
        String taskName = delegateTask.getName();
        String processDefinitionId = delegateTask.getProcessDefinitionId();

        log.info("自动退回 returnToTargetNode 注册 afterCommit 回调, procInstId={}, currentTaskDefKey={}, targetElementId={}, targetNodeName={}",
                procInstId, currentTaskDefKey, targetElementId, targetNodeName);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        new TransactionTemplate(transactionManager).execute(status -> {
                            doReturnAfterCommit(procInstId, processNumber, currentTaskDefKey, targetElementId,
                                    targetNodeName, verifyUserId, verifyUserName,
                                    taskId, taskName, processDefinitionId, businessDataVo, backType);
                            return null;
                        });
                    } catch (Exception e) {
                        log.error("自动退回 afterCommit 执行失败, 流程停在自动退回节点, 请人工处理. procInstId={}, currentTaskDefKey={}, targetElementId={}, targetNodeName={}",
                                procInstId, currentTaskDefKey, targetElementId, targetNodeName, e);
                    }
                }
            });
        } else {
            try {
                new TransactionTemplate(transactionManager).execute(status -> {
                    doReturnAfterCommit(procInstId, processNumber, currentTaskDefKey, targetElementId,
                            targetNodeName, verifyUserId, verifyUserName,
                            taskId, taskName, processDefinitionId, businessDataVo, backType);
                    return null;
                });
            } catch (Exception e) {
                log.error("自动退回执行失败, procInstId={}, currentTaskDefKey={}, targetElementId={}",
                        procInstId, currentTaskDefKey, targetElementId, e);
                throw new AFBizException("自动退回失败", e);
            }
        }
    }

    /**
     * 在新事务里执行 退回跳转 + 副作用.
     * 注意: 不能先 complete 再跳转! 因为自动退回可能是最后一个节点,
     * complete 后 Activiti 正常流转到 EndEvent 导致流程结束, 无法再跳转.
     * 正确做法: 直接用 moveTo/commitProcess 操作当前任务(内部会处理任务结束+执行跳转).
     */
    private void doReturnAfterCommit(String procInstId, String processNumber, String currentTaskDefKey,
                                     String targetElementId, String targetNodeName,
                                     String verifyUserId, String verifyUserName,
                                     String taskId, String taskName, String processDefinitionId,
                                     BusinessDataVo businessDataVo, Integer backType) {
        log.info("自动退回 doReturnAfterCommit 开始, procInstId={}, currentTaskDefKey={}, targetElementId={}",
                procInstId, currentTaskDefKey, targetElementId);

        // Step 1: 查询当前活动任务
        List<Task> currentTasks = taskService.createTaskQuery().processInstanceId(procInstId).active().list();
        if (CollectionUtils.isEmpty(currentTasks)) {
            log.warn("自动退回: 未查到活动任务, 流程可能已结束. procInstId={}", procInstId);
            return;
        }

        Task autoReturnTask = currentTasks.stream()
                .filter(t -> currentTaskDefKey.equals(t.getTaskDefinitionKey()))
                .findFirst()
                .orElse(null);
        if (autoReturnTask == null) {
            log.warn("自动退回: 未查到自动退回节点任务, 可能已被处理. procInstId={}, currentTaskDefKey={}",
                    procInstId, currentTaskDefKey);
            return;
        }

        // Step 2: 记录审批日志
        bpmVerifyInfoBizService.addVerifyInfo(BpmVerifyInfo.builder()
                .verifyDate(new Date())
                .taskName(taskName)
                .taskId(taskId)
                .runInfoId(procInstId)
                .verifyUserId(verifyUserId)
                .verifyUserName(verifyUserName)
                .taskDefKey(currentTaskDefKey)
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_UPDATE_TYPE.getCode())
                .verifyDesc(String.format("自动退回至[%s]", targetNodeName))
                .processCode(processNumber)
                .build());

        // Step 3: 执行退回跳转(双路径) - 直接操作当前任务, 不先 complete
        if (ProcessDefinitionUtils.isUserTaskParallel(autoReturnTask)) {
            TaskFlowControlService taskFlowControlService = taskFlowControlServiceFactory.create(procInstId);
            List<String> unMovedTasks = null;
            try {
                unMovedTasks = taskFlowControlService.moveTo(autoReturnTask.getTaskDefinitionKey(), targetElementId);
            } catch (Exception e) {
                log.error("自动退回跳转失败!", e);
                throw new RuntimeException(e);
            }
            List<String> distinctUnMoved = unMovedTasks.stream().distinct().collect(Collectors.toList());
            if (!distinctUnMoved.isEmpty()) {
                distinctUnMoved = distinctUnMoved.stream()
                        .filter(a -> !a.equals(autoReturnTask.getTaskDefinitionKey()))
                        .collect(Collectors.toList());
                if (!distinctUnMoved.isEmpty()) {
                    taskMgmtMapper.deleteExecutionsByProcinstIdAndTaskDefKeys(procInstId, distinctUnMoved);
                }
            }
        } else {
            try {
                processNodeJump.commitProcess(autoReturnTask.getId(), null, targetElementId, procInstId);
            } catch (Exception e) {
                log.error("自动退回跳转失败!", e);
                throw new AFBizException("自动退回跳转失败!");
            }
        }

        // Step 6: 写 BpmProcessNodeSubmit(控制目标节点完成后的跳转)
        PvmActivity nextElement = additionalInfoService.getNextElement(targetElementId, procInstId);
        if (nextElement != null) {
            String restoreNodeKey;
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
            if (!StringUtils.isEmpty(restoreNodeKey)) {
                processNodeSubmitService.addProcessNode(BpmProcessNodeSubmit.builder()
                        .state(1)
                        .nodeKey(restoreNodeKey)
                        .processInstanceId(procInstId)
                        .backType(backType != null ? backType : ProcessDisagreeTypeEnum.FOUR_DISAGREE.getCode())
                        .createUser(verifyUserId)
                        .build());
            }
        }

        // Step 7: 重置审批状态
        variableMapper.resetUnderStatusByProcessNumber(processNumber);

        // Step 8: 表单回调
        if (businessDataVo != null && !Boolean.TRUE.equals(businessDataVo.getIsOutSideAccessProc())) {
            try {
                formFactory.getFormAdaptor(businessDataVo).backToModifyData(businessDataVo);
            } catch (Exception e) {
                log.warn("自动退回 backToModifyData 回调失败, 不影响主流程", e);
            }
        }

        log.info("自动退回 doReturnAfterCommit 完成, procInstId={}, targetElementId={}, targetNodeName={}",
                procInstId, targetElementId, targetNodeName);
    }
}
