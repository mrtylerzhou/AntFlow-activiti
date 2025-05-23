package org.openoa.engine.bpmnconf.service.flowcontrol;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Administrator
 */
public class GeneralRejectManagerImpl{

    @Autowired    
    private TaskService taskService;
    @Autowired    
    private RuntimeService runtimeService;
    @Autowired    
    private RepositoryService repositoryService;

    private List<ActivityImpl> activitiList = new ArrayList();
    
    //根据ActivitiId获取Acitiviti
    public ActivityImpl findActivityImpl(List<ActivityImpl> activitiList, String activitiId) {

        for (ActivityImpl activityImpl : activitiList) {
            String id = activityImpl.getId();
            if (id.equals(activitiId)) {
                return activityImpl;
            }
        }
        return null;
    }

    public List<ActivityImpl> findEndActivityImpls(List<ActivityImpl> activitiList) {
        List<ActivityImpl> activityImpls = new ArrayList<ActivityImpl>();
        for (ActivityImpl activityImpl : activitiList) {
            List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
            if (pvmTransitionList.isEmpty()) {
                activityImpls.add(activityImpl);
            }
        }
        return activityImpls;
    }
    

    public void rejectAndEnd(String taskId) {

        TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
        
        ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(taskEntity.getProcessDefinitionId());


        ExecutionEntity executionEntity = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(taskEntity.getExecutionId()).singleResult();//执行实例

        String activitiId = executionEntity.getActivityId();//当前实例的执行到哪个节点
        activitiList = def.getActivities();//获得当前任务的所有节点
        
        ActivityImpl activeActivity = findActivityImpl(activitiList, activitiId);
        ActivityImpl endActivity = findEndActivityImpls(activitiList).get(0);
        taskEntity.insert(executionEntity);
        //设置当前节点的出口
        List<PvmTransition> pvmTransitionList = activeActivity.getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            transitionImpl.setDestination(endActivity);
        }
        
        taskService.complete(taskId);
    }

}
