package org.activiti.engine.impl.cmd;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.ProcessDefinitionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProcessNodeJump {
    @Autowired
    protected TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;

    public void jumpTransAction(String taskId, String endActivityId) throws Exception {
        HistoricTaskInstance currTask = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .singleResult();

        if (currTask == null) {
            throw new RuntimeException("未找到当前任务");
        }

        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(currTask.getProcessInstanceId())
                .singleResult();

        if (instance == null) {
            throw new RuntimeException("未找到当前流程实例");
        }

        // **1. 终止所有并行任务**
        cancelAllParallelTasks(instance.getId(), currTask.getTaskDefinitionKey());

        // **2. 删除所有已完成任务**
        //deleteCompletedTasks(instance.getId(), currTask);
        // **3. 让所有任务回退**
        jumpAllParallelTasks(instance, endActivityId);

    }

    private void cancelAllParallelTasks(String processInstanceId, String taskDefinitionKey) {
        List<Task> activeTasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();

        for (Task task : activeTasks) {
            taskService.setAssignee(task.getId(), "自动跳过"); // **取消当前审批人，避免权限问题**
            taskService.complete(task.getId()); // **让任务完成**
        }
    }
    private void jumpAllParallelTasks(ProcessInstance instance, String targetActivityId) throws Exception {
        List<Task> parallelTasks = taskService.createTaskQuery()
                .processInstanceId(instance.getId())
                .list();

        for (Task task : parallelTasks) {
            jumpToActivity(task.getId(), targetActivityId);
        }
    }
    private void jumpToActivity(String taskId, String targetActivityId) throws Exception {
        ActivityImpl targetActivity = findActivitiImpl(taskId, targetActivityId);

        // **获取当前任务**
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();

        // **获取当前任务的活动**
        ActivityImpl currActivity = findActivitiImpl(taskId, null);

        // **清除原始流向**
        List<PvmTransition> originalTransitions = clearTransition(currActivity);

        // **创建新流向**
        TransitionImpl newTransition = currActivity.createOutgoingTransition();
        newTransition.setDestination(targetActivity);

        // **强制完成当前任务**
        taskService.complete(taskId);

        // **删除新创建的流向**
        targetActivity.getIncomingTransitions().remove(newTransition);

        // **恢复原始流向**
        restoreTransition(currActivity, originalTransitions);
    }
    /**
     * @param taskId     current task id
     * @param variables  variables
     * @param activityId activiti id
     *                  if it is empty,then default to submit
     * @throws Exception
     */
    public void commitProcess(String taskId, Map<String, Object> variables,
                              String activityId) {
        if (variables == null) {
            variables = new HashMap<>();
        }

        if (StringUtils.isEmpty(activityId)) {
            taskService.complete(taskId, variables);
        } else {
            try {
                turnTransition(taskId, activityId, variables, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param taskId     current task id
     * @param variables  variables
     * @param activityId activiti id
     *                  if it is empty,then default to submit
     * @throws Exception
     */
    public void commitProcess(String taskId, Map<String, Object> variables,
                              String activityId, String procInstId) {
        if (variables == null) {
            variables = new HashMap<>();
        }

        if (StringUtils.isEmpty(activityId)) {
            taskService.complete(taskId, variables);
        } else {
            try {
                turnTransition(taskId, activityId, variables, procInstId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 流程转向操作
     *
     * @param taskId     当前任务ID
     * @param activityId 目标节点任务ID
     * @param variables  流程变量
     * @throws Exception
     */
    private void turnTransition(String taskId, String activityId,
                                Map<String, Object> variables, String procInstId) throws Exception {
        // 当前节点
        ActivityImpl currActivity = findActivitiImpl(taskId, null);
        // 清空当前流向
        List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

        // 创建新流向
        TransitionImpl newTransition = currActivity.createOutgoingTransition();
        // 目标节点
        ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
        // 设置新流向的目标节点
        newTransition.setDestination(pointActivity);

        // 执行转向任务
        taskService.complete(taskId, variables);
        if(!currActivity.getId().equals(activityId)) {
            ActivityBehavior activityBehavior = currActivity.getActivityBehavior();
            if (activityBehavior instanceof SequentialMultiInstanceBehavior){
                if(null!= procInstId && !procInstId.isEmpty()) {
                    boolean isDo = true;
                    while (isDo) {
                        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).taskDefinitionKey(currActivity.getId()).list();
                        if(tasks.isEmpty()) {
                            isDo = false;
                        }

                        for (Task task:tasks) {
                            taskService.complete(task.getId(), variables);
                        }
                    }
                }
            }
        }

        // 删除目标节点新流入
        pointActivity.getIncomingTransitions().remove(newTransition);

        // 还原以前流向
        restoreTransition(currActivity, oriPvmTransitionList);
    }
    private void deleteCompletedTasks(String processInstanceId, HistoricTaskInstance historicTaskInstance) {
        List<HistoricTaskInstance> finishedTasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(historicTaskInstance.getTaskDefinitionKey())
                .finished()
                .list();
        List<ActivityImpl> allUserTasks=new ArrayList<>();
        ActivityImpl closestStartParallelGateway = ProcessDefinitionUtils.findClosestStartParallelGateway(historicTaskInstance.getProcessInstanceId());
        if(closestStartParallelGateway!=null){
            ActivityImpl joinParallelGateway = ProcessDefinitionUtils.findJoinParallelGatewayRecursively(closestStartParallelGateway);
            ProcessDefinitionUtils.findUserTasksBetweenGatewaysRecursively(closestStartParallelGateway,joinParallelGateway,allUserTasks);
        }
        if(!CollectionUtils.isEmpty(allUserTasks)){
            for (ActivityImpl activityImpl : allUserTasks) {
                List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .taskDefinitionKey(activityImpl.getId())
                        .list();
                if(!CollectionUtils.isEmpty(historicTaskInstances)){
                    for (HistoricTaskInstance historicActivityInstance : historicTaskInstances) {
                        historyService
                                .deleteHistoricTaskInstance(historicActivityInstance.getId());
                    }
                }
            }
        }else{
            for (HistoricTaskInstance task : finishedTasks) {
                historyService
                        .deleteHistoricTaskInstance(task.getId());
            }
        }

    }
    /**
     * 流程转向操作
     *
     * @param taskId     当前任务ID
     * @param activityId 目标节点任务ID
     * @param variables  流程变量
     * @throws Exception
     */
    private void turnTransitionV2(String taskId, String activityId,
                                Map<String, Object> variables) throws Exception {
        // 当前节点
        ActivityImpl currActivity = findActivitiImpl(taskId, null);


        // 获取目标活动节点
        ActivityImpl targetActivity = findActivitiImpl(taskId, activityId);
        // 如果目标节点是并行网关，则找到对应的起始网关
        if ("parallelGateway".equals(targetActivity.getProperty("type"))) {
            String startGatewayId = findParallelGatewayId(targetActivity);
            if (startGatewayId != null) {
                targetActivity = findActivitiImpl(taskId, startGatewayId);
            }
        }

        // 清空当前流向
        List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

        // 创建新流向
        TransitionImpl newTransition = currActivity.createOutgoingTransition();

        // 设置新流向的目标节点
        newTransition.setDestination(targetActivity);

        // 执行转向任务
        taskService.complete(taskId, variables);
        // 删除目标节点新流入
        targetActivity.getIncomingTransitions().remove(newTransition);

        // 还原以前流向
        restoreTransition(currActivity, oriPvmTransitionList);
    }

    /**
     * @param activityImpl
     * @param oriPvmTransitionList
     */
    private void restoreTransition(ActivityImpl activityImpl,
                                   List<PvmTransition> oriPvmTransitionList) {

        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        pvmTransitionList.clear();
        // 还原以前流向
        for (PvmTransition pvmTransition : oriPvmTransitionList) {
            pvmTransitionList.add(pvmTransition);
        }
    }

    /**
     *
     * @param taskId
     * @param activityId
     * @return
     * @throws Exception
     */
    public ActivityImpl findActivitiImpl(String taskId, String activityId)
            throws Exception {

        ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);


        if (StringUtils.isEmpty(activityId)) {
            activityId = findTaskById(taskId).getTaskDefinitionKey();
        }


        if (activityId.toUpperCase().equals("END")) {
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {
                List<PvmTransition> pvmTransitionList = activityImpl
                        .getOutgoingTransitions();
                if (pvmTransitionList.isEmpty()) {
                    return activityImpl;
                }
            }
        }

        ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition)
                .findActivity(activityId);

        return activityImpl;
    }

    /**
     *
     * @param taskId
     * @return
     * @throws Exception
     */
    private TaskEntity findTaskById(String taskId) throws Exception {
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(
                taskId).singleResult();
        if (task == null) {
            throw new AFBizException("任务实例未找到!");
        }
        return task;
    }

    /**
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(
            String taskId) throws Exception {
        // 取得流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(findTaskById(taskId)
                        .getProcessDefinitionId());

        if (processDefinition == null) {
            throw new Exception("流程定义未找到!");
        }

        return processDefinition;
    }

    /**
     *
     * @param activityImpl
     * @return
     */
    private List<PvmTransition> clearTransition(ActivityImpl activityImpl) {

        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            oriPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();

        return oriPvmTransitionList;
    }
    /**
     * 迭代循环流程树结构，查询当前节点可驳回的任务节点
     *
     * @param taskId       当前任务ID
     * @param currActivity 当前活动节点
     * @param rtnList      存储回退节点集合
     * @param tempList     临时存储节点集合（存储一次迭代过程中的同级userTask节点）
     * @return 回退节点集合
     */
    private List<ActivityImpl> iteratorBackActivity(String taskId,
                                                    ActivityImpl currActivity, List<ActivityImpl> rtnList,
                                                    List<ActivityImpl> tempList) throws Exception {
        // 查询流程定义，生成流程树结构
        ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);

        // 当前节点的流入来源
        List<PvmTransition> incomingTransitions = currActivity
                .getIncomingTransitions();
        // 条件分支节点集合，userTask节点遍历完毕，迭代遍历此集合，查询条件分支对应的userTask节点
        List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();
        // 并行节点集合，userTask节点遍历完毕，迭代遍历此集合，查询并行节点对应的userTask节点
        List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();
        // 遍历当前节点所有流入路径
        for (PvmTransition pvmTransition : incomingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            ActivityImpl activityImpl = transitionImpl.getSource();
            String type = (String) activityImpl.getProperty("type");
            /**
             * 并行节点配置要求：<br>
             * 必须成对出现，且要求分别配置节点ID为:XXX_start(开始)，XXX_end(结束)
             */
            if ("parallelGateway".equals(type)) {// 并行路线
                String gatewayId = activityImpl.getId();
                String gatewayType = gatewayId.substring(gatewayId
                        .lastIndexOf("_") + 1);
                if ("START".equals(gatewayType.toUpperCase())) {// 并行起点，停止递归
                    return rtnList;
                } else {// 并行终点，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
                    parallelGateways.add(activityImpl);
                }
            } else if ("startEvent".equals(type)) {// 开始节点，停止递归
                return rtnList;
            } else if ("userTask".equals(type)) {// 用户任务
                tempList.add(activityImpl);
            } else if ("exclusiveGateway".equals(type)) {// 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
                currActivity = transitionImpl.getSource();
                exclusiveGateways.add(currActivity);
            }
        }

        /**
         * 迭代条件分支集合，查询对应的userTask节点
         */
        for (ActivityImpl activityImpl : exclusiveGateways) {
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
        }

        /**
         * 迭代并行集合，查询对应的userTask节点
         */
        for (ActivityImpl activityImpl : parallelGateways) {
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
        }

        /**
         * 根据同级userTask集合，过滤最近发生的节点
         */
        currActivity = filterNewestActivity(processInstance, tempList);
        if (currActivity != null) {
            // 查询当前节点的流向是否为并行终点，并获取并行起点ID
            String id = findParallelGatewayId(currActivity);
            if (id == null || id.equals("")) {// 并行起点ID为空，此节点流向不是并行终点，符合驳回条件，存储此节点
                rtnList.add(currActivity);
            } else {// 根据并行起点ID查询当前节点，然后迭代查询其对应的userTask任务节点
                currActivity = findActivitiImpl(taskId, id);
            }

            // 清空本次迭代临时集合
            tempList.clear();
            // 执行下次迭代
            iteratorBackActivity(taskId, currActivity, rtnList, tempList);
        }
        return rtnList;
    }
    /**
     * 根据当前节点，查询输出流向是否为并行终点，如果为并行终点，则拼装对应的并行起点ID
     *
     * @param activityImpl 当前节点
     * @return
     */
    private String findParallelGatewayId(ActivityImpl activityImpl) {
        List<PvmTransition> incomingTransitions = activityImpl
                .getOutgoingTransitions();
        for (PvmTransition pvmTransition : incomingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            activityImpl = transitionImpl.getDestination();
            String type = (String) activityImpl.getProperty("type");
            if ("parallelGateway".equals(type)) {// 并行路线
                String gatewayId = activityImpl.getId();
                String gatewayType = gatewayId.substring(gatewayId
                        .lastIndexOf("_") + 1);
                if ("END".equals(gatewayType.toUpperCase())) {
                    return gatewayId.substring(0, gatewayId.lastIndexOf("_"))
                            + "_start";
                }
            }
        }
        return null;
    }
    /**
     * 根据流入任务集合，查询最近一次的流入任务节点
     *
     * @param processInstance 流程实例
     * @param tempList        流入任务集合
     * @return
     */
    private ActivityImpl filterNewestActivity(ProcessInstance processInstance,
                                              List<ActivityImpl> tempList) {
        while (tempList.size() > 0) {
            ActivityImpl activity_1 = tempList.get(0);
            HistoricActivityInstance activityInstance_1 = findHistoricUserTask(
                    processInstance, activity_1.getId());
            if (activityInstance_1 == null) {
                tempList.remove(activity_1);
                continue;
            }

            if (tempList.size() > 1) {
                ActivityImpl activity_2 = tempList.get(1);
                HistoricActivityInstance activityInstance_2 = findHistoricUserTask(
                        processInstance, activity_2.getId());
                if (activityInstance_2 == null) {
                    tempList.remove(activity_2);
                    continue;
                }

                if (activityInstance_1.getEndTime().before(
                        activityInstance_2.getEndTime())) {
                    tempList.remove(activity_1);
                } else {
                    tempList.remove(activity_2);
                }
            } else {
                break;
            }
        }
        if (tempList.size() > 0) {
            return tempList.get(0);
        }
        return null;
    }

    /**
     * 查询指定任务节点的最新记录
     *
     * @param processInstance 流程实例
     * @param activityId
     * @return
     */
    private HistoricActivityInstance findHistoricUserTask(
            ProcessInstance processInstance, String activityId) {
        HistoricActivityInstance rtnVal = null;
        // 查询当前流程实例审批结束的历史节点
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery().activityType("userTask")
                .processInstanceId(processInstance.getId()).activityId(
                        activityId).finished()
                .orderByHistoricActivityInstanceEndTime().desc().list();
        if (historicActivityInstances.size() > 0) {
            rtnVal = historicActivityInstances.get(0);
        }

        return rtnVal;
    }

    /**
     * 根据任务ID获取对应的流程实例
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    private ProcessInstance findProcessInstanceByTaskId(String taskId)
            throws Exception {
        // 找到流程实例
        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery().processInstanceId(
                        findTaskById(taskId).getProcessInstanceId())
                .singleResult();
        if (processInstance == null) {
            throw new AFBizException("流程实例未找到!");
        }
        return processInstance;
    }
}
