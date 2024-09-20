package org.activiti.engine.impl.cmd;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.exception.JiMuBizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProcessNodeJump {
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected RepositoryService repositoryService;
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
                turnTransition(taskId, activityId, variables);
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
                                Map<String, Object> variables) throws Exception {
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
        // 删除目标节点新流入
        pointActivity.getIncomingTransitions().remove(newTransition);

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
            throw new JiMuBizException("任务实例未找到!");
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
}
