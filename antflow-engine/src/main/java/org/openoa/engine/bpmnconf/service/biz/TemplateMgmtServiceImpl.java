package org.openoa.engine.bpmnconf.service.biz;

import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.ActHiTaskinst;
import org.openoa.base.vo.BpmVerifyInfoVo;
import org.openoa.engine.bpmnconf.common.ProcessServiceFactory;
import org.openoa.engine.bpmnconf.mapper.BpmVerifyInfoMapper;
import org.openoa.engine.bpmnconf.service.impl.ActHiTaskinstServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
public class TemplateMgmtServiceImpl extends ProcessServiceFactory {
    public final static String PROCESS_TEMPLATE = "process201804161604";
    @Autowired
    private BpmVerifyInfoMapper bpmVerifyInfoMapper;
    @Autowired
    private ActHiTaskinstServiceImpl actHiTaskinstService;

    /**
     * deployment a process,for compatibility purpose,it is highly recommended to use thei newer one
     *
     * @param filePath    file path
     * @param processName process name
     * @throws Exception
     */
    public void processDeployment(String filePath, String processName) throws Exception {
        //String filePath = "processDiagram/employmentProcess.bpmn";
        //String filePng = "processDiagram/employmentProcess.png";
        InputStream fis = new ClassPathResource(filePath).getInputStream();
        repositoryService.createDeployment()
                .addInputStream(processName, fis)
                .deploy();//用userAndGroupInUserTask.bpmn作为资源名称
    }

    /***
     * delete process template
     */
    public void delProcessTemplate(String deployId) {
        this.repositoryService.deleteDeployment(
                deployId, true);
    }


    /**
     * find verify info
     */
    /**
     * query verify info by business id
     *
     * @param business_id business id
     * @return
     */
    @Deprecated
    public List<BpmVerifyInfoVo> findVerifyInfo(String business_id) {
        return bpmVerifyInfoMapper.findVerifyInfo(business_id);
    }

    /**

     * get process by process id
     *
     * @param processId
     */
    public List<String> findProcess(String processId) {
        List<String> list = new ArrayList<>();
        BpmnModel model = repositoryService.getBpmnModel(processId);
        if (model != null) {
            Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
            for (FlowElement e : flowElements) {
                if (!e.getId().contains("flow")) {
                    if (!e.getName().contains("Start") && !e.getName().contains("End")) {
                        list.add(e.getId());
                    }
                }
            }
        }
        return list;
    }

    /**
     * @param taskId     current task
     * @param variables  variables
     * @param activityId process flow turn id
     *                   if it is empty,then default is submit operation
     * @throws Exception
     */
    public void commitProcess(String taskId, Map<String, Object> variables,
                              String activityId) {
        if (variables == null) {
            variables = new HashMap<String, Object>();
        }

        //if jump to node is null,then default is submit operation
        if (StringUtils.isEmpty(activityId)) {
            taskService.complete(taskId, variables);
        } else {// jump
            try {
                turnTransition(taskId, activityId, variables);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * end process,super admin's privilege
     *
     * @param taskId
     */
    public void endProcess(String taskId) throws Exception {
        ActivityImpl endActivity = findActivitiImpl(taskId, "end");
        commitProcess(taskId, null, endActivity.getId());
    }

    /**
     * process turn
     *
     * @param taskId     current task id
     * @param activityId target activiti id
     * @param variables  variables
     * @throws Exception
     */
    private void turnTransition(String taskId, String activityId,
                                Map<String, Object> variables) throws Exception {
        // find current activiti
        ActivityImpl currActivity = findActivitiImpl(taskId, null);
        // clear current activiti outgoing transitions
        List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

        // create new outgoing transitions for current activiti
        TransitionImpl newTransition = currActivity.createOutgoingTransition();
        // find target activiti
        ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
        // set target activiti as destination of new transition
        newTransition.setDestination(pointActivity);

        // begin to turn
        taskService.complete(taskId, variables);
        // clear target activiti incoming transitions
        pointActivity.getIncomingTransitions().remove(newTransition);

        // restore current activiti outgoing transitions
        restoreTransition(currActivity, oriPvmTransitionList);
    }

    /**

     * get activiti by task id and activity id
     *
     * @param taskId     task id
     * @param activityId activiti id
     *                   if it is null or empty(""),then default get current activiti
     *                   if it is "end",then get end activiti
     *
     * @return
     * @throws Exception
     */
    public ActivityImpl findActivitiImpl(String taskId, String activityId)
            throws Exception {
        // find process definition
        ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);

        // get current activiti
        if (StringUtils.isEmpty(activityId)) {
            activityId = findTaskById(taskId).getTaskDefinitionKey();
        }


        //get end activiti by processDefinition
        if (activityId.toUpperCase().equals("END")) {
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {
                List<PvmTransition> pvmTransitionList = activityImpl
                        .getOutgoingTransitions();
                if (pvmTransitionList.isEmpty()) {
                    return activityImpl;
                }
            }
        }


        //get active node by node id
        ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition)
                .findActivity(activityId);

        return activityImpl;
    }

    /**
     * get process definition entity by task id
     *
     * @param taskId task id
     * @return
     * @throws Exception
     */
    private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(
            String taskId) throws Exception {

        //get process definition
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(findTaskById(taskId)
                        .getProcessDefinitionId());

        if (processDefinition == null) {
            throw new Exception("流程定义未找到!");
        }

        return processDefinition;
    }

    /**
     * get task entity by task id
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    private TaskEntity findTaskById(String taskId) throws Exception {
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(
                taskId).singleResult();
        if (task == null) {
            throw new Exception("任务实例未找到!");
        }
        return task;
    }

    /**
     * restore specified activiti's transition
     *
     * @param activityImpl         activiti
     * @param oriPvmTransitionList original transition list
     */
    private void restoreTransition(ActivityImpl activityImpl,
                                   List<PvmTransition> oriPvmTransitionList) {
        // clearn current outgoing transitions
        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        pvmTransitionList.clear();
        // restore current activiti's original transitions
        for (PvmTransition pvmTransition : oriPvmTransitionList) {
            pvmTransitionList.add(pvmTransition);
        }
    }

    /**
     * clear specified activiti's outgoing transition
     *
     * @param activityImpl
     * @return node flow list
     */
    private List<PvmTransition> clearTransition(ActivityImpl activityImpl) {

        //set current outgoing transitions
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();

        //get current outgoing transitions,store it in oriPvmTransitionList,then clear it
        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            oriPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();

        return oriPvmTransitionList;
    }

    /***
     * reject process at specified node
     */
    public void rejectNode(String taskId, String taskNode) {
        // store task node
        String destTaskkey = taskNode;

        Map<String, Object> variables;
        // query current task
        ActHiTaskinst currTask = actHiTaskinstService.queryByTaskId(taskId);
        ExecutionEntity entity = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(currTask.getProcInstId()).singleResult();
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(entity.getProcessDefinitionId());
        variables = entity.getProcessVariables();
        //get active node
        ActivityImpl currActivityImpl = definition.findActivity(entity.getActivityId());
        //get next to be active node
        ActivityImpl nextActivityImpl = ((ProcessDefinitionImpl) definition).findActivity(destTaskkey);
        if (currActivityImpl != null) {
            //get outgoing transitions
            List<PvmTransition> pvmTransitions = currActivityImpl.getOutgoingTransitions();
            List<PvmTransition> oriPvmTransitions = new ArrayList<PvmTransition>();
            for (PvmTransition transition : pvmTransitions) {
                oriPvmTransitions.add(transition);
            }
            //clear outgoing transitions
            pvmTransitions.clear();
            //建立新的出口
            List<TransitionImpl> transitionImpls = new ArrayList<TransitionImpl>();
            TransitionImpl tImpl = currActivityImpl.createOutgoingTransition();
            tImpl.setDestination(nextActivityImpl);
            transitionImpls.add(tImpl);

            List<Task> list = taskService.createTaskQuery().processInstanceId(entity.getProcessInstanceId())
                    .taskDefinitionKey(entity.getActivityId()).list();
            for (Task task : list) {
                //variables.put(Contants.START_USER,taskService.getVariable(task.getId(),"startUser"));
                taskService.complete(task.getId(), variables);
                actHiTaskinstService.deleteById(task.getId());
            }

            for (TransitionImpl transitionImpl : transitionImpls) {
                currActivityImpl.getOutgoingTransitions().remove(transitionImpl);
            }

            for (PvmTransition pvmTransition : oriPvmTransitions) {
                pvmTransitions.add(pvmTransition);
            }
        }
    }

    /**
     * get next node by process instance id
     *
     * @param procInstanceId
     * @return
     */
    public String getNextNode(String procInstanceId) {

        //1.get current tasks by process instance id
        List<Task> tasks = processEngine.getTaskService().createTaskQuery().processInstanceId(procInstanceId).list();
        String nextId = "";
        for (Task task : tasks) {
            RepositoryService rs = processEngine.getRepositoryService();

            //2. get process definition by task's process definition id,the get all activities
            ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) rs)
                    .getDeployedProcessDefinition(task.getProcessDefinitionId());
            List<ActivityImpl> activitiList = def.getActivities();

            //3.get current task's execution id
            String excId = task.getExecutionId();
            RuntimeService runtimeService = processEngine.getRuntimeService();
            ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(excId)
                    .singleResult();
            String activitiId = execution.getActivityId();

            //4. get activityImpl list,if an activiti id equals current task's activity id,get next node
            for (ActivityImpl activityImpl : activitiList) {
                String id = activityImpl.getId();
                if (activitiId.equals(id)) {
                    List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();//get specified activity's outgoing transitions
                    for (PvmTransition tr : outTransitions) {
                        PvmActivity ac = tr.getDestination(); // get outgoing activity's destination
                        nextId = ac.getId();
                    }
                    break;
                }
            }
        }
        return nextId;
    }
}
