package org.openoa.engine.bpmnconf.adp.processoperation;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
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
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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
            if(taskList.size()>1){
                throw new AFBizException(BusinessErrorEnum.RIGHT_INVALID.getCodeStr(),"流程已审批,不允许操作!");
            }
            String taskDefinitionKey = taskList.get(0).getTaskDefinitionKey();
            String twoTaskKeyDesc = ProcessNodeEnum.TWO_TASK_KEY.getDesc();
            if (ProcessNodeEnum.compare(taskDefinitionKey,twoTaskKeyDesc)>0) {
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
                break;
            case TWO_DISAGREE:
                restoreNodeKey = ProcessNodeEnum.TWO_TASK_KEY.getDesc();
                backToNodeKey = ProcessNodeEnum.START_TASK_KEY.getDesc();
                break;
            case THREE_DISAGREE://default behavior
                restoreNodeKey = taskData.getTaskDefinitionKey();
                backToNodeKey = ProcessNodeEnum.START_TASK_KEY.getDesc();
                break;
            case FOUR_DISAGREE:
                if (!NodeUtil.isCurrentNodeNoneOperational(backToNodeId)) {
                    throw new AFBizException("不可退回到目标节点,请重试!");
                }
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
                if(!NodeUtil.isCurrentNodeNoneOperational(backToNodeId)){
                    throw new AFBizException("不可退回到目标节点,请重试!");
                }
                backToNodeKey = variableMapper.getElementIdsdByNodeId(vo.getProcessNumber(), backToNodeId).get(0);
                break;
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
            TaskFlowControlService taskFlowControlService = taskFlowControlServiceFactory.create(taskData.getProcessInstanceId(), bpmVariableMultiplayerService);
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
                    boolean isSingleSign=CollectionUtils.isEmpty(moreNodes);
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
}
