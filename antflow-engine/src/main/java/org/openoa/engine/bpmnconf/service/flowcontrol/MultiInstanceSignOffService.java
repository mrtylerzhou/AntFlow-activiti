package org.openoa.engine.bpmnconf.service.flowcontrol;

import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.*;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MultiInstanceSignOffService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final TaskMgmtMapper taskMgmtMapper;
    private final RepositoryService repositoryService;

    public MultiInstanceSignOffService(@Autowired ProcessEngine processEngine, TaskMgmtMapper taskMgmtMapper) {
        this.runtimeService = processEngine.getRuntimeService();
        this.taskService = processEngine.getTaskService();
        this.historyService = processEngine.getHistoryService();
        this.repositoryService = processEngine.getRepositoryService();
        this.taskMgmtMapper = taskMgmtMapper;
    }

    /**
     * 执行减签操作
     * @param processInstanceId 流程实例ID
     * @param userToRemove      需移除的用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeAssignee(String processInstanceId, String taskDefKey, String userToRemove) {
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
                .taskAssignee(userToRemove)
                .list();
        if (CollectionUtils.isEmpty(tasks)) {
            throw new RuntimeException("未找到用户 " + userToRemove + " 的任务");
        }
        String myExecutionId = tasks.get(0).getExecutionId();

        // 获取原始执行
        Execution myExecution = runtimeService.createExecutionQuery().executionId(myExecutionId).singleResult();

        if (myExecution == null) {
            throw new RuntimeException("未找到用户 " + userToRemove + " 的 Execution");
        }

        // 2. 获取并更新参与者列表
        List<String> assigneeList = (List<String>) runtimeService.getVariable(myExecution.getId(), "personnelList2");
        if (!assigneeList.remove(userToRemove)) {
            throw new RuntimeException("用户 " + userToRemove + " 不在参与者列表中");
        }
        runtimeService.setVariable(myExecution.getParentId(), "personnelList2", assigneeList);
        taskMgmtMapper.deleteExecutionById(myExecution.getId());
        taskMgmtMapper.deletTask(tasks.get(0).getId());
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