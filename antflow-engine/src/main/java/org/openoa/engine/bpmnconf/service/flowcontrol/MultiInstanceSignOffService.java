package org.openoa.engine.bpmnconf.service.flowcontrol;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MultiInstanceSignOffService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final TaskMgmtMapper taskMgmtMapper;
    private final BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;
    private final BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    private final BpmFlowrunEntrustServiceImpl flowrunEntrustService;
    private final RepositoryService repositoryService;

    public MultiInstanceSignOffService(@Autowired ProcessEngine processEngine,
                                       TaskMgmtMapper taskMgmtMapper,
                                       BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper,
                                       BpmBusinessProcessServiceImpl bpmBusinessProcessService,
                                       BpmFlowrunEntrustServiceImpl flowrunEntrustService) {
        this.runtimeService = processEngine.getRuntimeService();
        this.taskService = processEngine.getTaskService();
        this.historyService = processEngine.getHistoryService();
        this.repositoryService = processEngine.getRepositoryService();
        this.taskMgmtMapper = taskMgmtMapper;
        this.bpmVariableMultiplayerMapper = bpmVariableMultiplayerMapper;
        this.bpmBusinessProcessService = bpmBusinessProcessService;
        this.flowrunEntrustService = flowrunEntrustService;
    }

    /**
     * 执行减签操作
     * 只用会签节点可以减签(顺序会签减签相当于跳过了当前节点,不允许通过此方法操作),或签需要承办,承办以后只有一个审批人,因此承办以后无法再减签了,承办前可以
     * @param processNumber 流程实例ID
     * @param userToRemove      需移除的用户ID
     */

    public void removeAssignee(String processNumber, String taskDefKey, String userToRemove,String userToRemoveName) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if(bpmBusinessProcess == null){
            throw new RuntimeException("未找根据流程编号找到流程实例"+processNumber);
        }
        String processInstanceId = bpmBusinessProcess.getProcInstId();
        // 1. 获取当前多实例节点的执行实例
        List<Execution> executions = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .activityId(taskDefKey)
                .list();
        if (CollectionUtils.isEmpty(executions)) {
            throw new RuntimeException("未找到多实例节点执行实例");
        }


        // 3. 删除该用户的未完成任务
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(taskDefKey)
                .list();
        if (CollectionUtils.isEmpty(tasks)) {
            throw new RuntimeException("未能根据当前taskdefkey"+taskDefKey+"找到任务");
        }
        if(tasks.size() == 1){
            throw new RuntimeException("当前任务只有一个审批人,无法去掉!");
        }
        List<Task> currentAssigneeTasks = tasks.stream().filter(a -> a.getAssignee().equals(userToRemove)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(currentAssigneeTasks)){
            throw new RuntimeException("未能根据当前用户"+userToRemove+"的审批任务");
        }
        Task currentAssigneeTask = currentAssigneeTasks.get(0);
        String myExecutionId =currentAssigneeTask.getExecutionId();
        // 获取原始执行
        Execution myExecution = runtimeService.createExecutionQuery().executionId(myExecutionId).singleResult();
        if (myExecution == null) {
            throw new RuntimeException("未找到用户 " + userToRemove + " 的 Execution");
        }
        String variableName = bpmVariableMultiplayerMapper.getVarNameByElementId(processNumber, taskDefKey);
        if(!StringUtils.hasText(variableName)){
            throw new RuntimeException("未找根据流程编号:"+processNumber+"和节点id:"+taskDefKey+"找到流程变量名称");
        }
        // 2. 获取并更新参与者列表
        List<String> assigneeList = (List<String>) runtimeService.getVariable(myExecution.getId(), variableName);
        if (!assigneeList.remove(userToRemove)) {
            throw new RuntimeException("用户 " + userToRemove + " 不在参与者列表中");
        }
        runtimeService.setVariable(myExecution.getParentId(), "personnelList2", assigneeList);
        taskMgmtMapper.deleteExecutionById(myExecution.getId());
        taskMgmtMapper.deletTask(currentAssigneeTask.getId());
        flowrunEntrustService.addFlowrunEntrust("0","管理员减签",userToRemove,userToRemoveName,
                currentAssigneeTask.getId(),0,processInstanceId,bpmBusinessProcess.getProcessinessKey());
        Integer nrOfCompleted = (Integer) runtimeService.getVariable(myExecution.getParentId(), "nrOfCompletedInstances");
        Integer nrOfInstances = (Integer) runtimeService.getVariable(myExecution.getParentId(), "nrOfInstances");
        // 处理已完成的数量
        long completedCount = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .taskAssignee(userToRemove)
                .finished()
                .count();
        if (completedCount > 0 && nrOfCompleted != null) {
            runtimeService.setVariable(myExecution.getParentId(), "nrOfCompletedInstances", nrOfCompleted - 1);
        }

        // 更新总实例数
        if (nrOfInstances != null) {
            runtimeService.setVariable(myExecution.getParentId(), "nrOfInstances", nrOfInstances - 1);
        }

        // 更新活动实例数
        Integer updatedCompleted = (Integer) runtimeService.getVariable(myExecution.getParentId(), "nrOfCompletedInstances");
        int activeInstances = assigneeList.size() - updatedCompleted;
        runtimeService.setVariable(myExecution.getParentId(), "nrOfActiveInstances", activeInstances);
    }


}