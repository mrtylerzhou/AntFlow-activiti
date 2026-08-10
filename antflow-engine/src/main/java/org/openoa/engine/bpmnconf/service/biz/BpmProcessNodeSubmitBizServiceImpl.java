package org.openoa.engine.bpmnconf.service.biz;

import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cmd.ProcessNodeJump;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmProcessNodeSubmit;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.flowcontrol.DefaultTaskFlowControlServiceFactory;
import org.openoa.engine.bpmnconf.service.flowcontrol.TaskFlowControlService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessNodeSubmitBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnBizCustomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BpmProcessNodeSubmitBizServiceImpl implements BpmProcessNodeSubmitBizService {
    @Autowired
    private ProcessNodeJump processJump;
    @Autowired
    protected TaskService taskService;
    @Autowired
    private ActivitiAdditionalInfoServiceImpl additionalInfoService;
    @Autowired
    private DefaultTaskFlowControlServiceFactory taskFlowControlServiceFactory;
    @Autowired
    private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;


    /**
     * 流程审批
     *
     * @param task
     */
    @Override
    public void processComplete(Task task) {

        BpmProcessNodeSubmit processNodeSubmit = this.getService().findBpmProcessNodeSubmit(task.getProcessInstanceId());
        String restoreNodeKey = "";
        Map<String, Object> varMap = new HashMap<>();
        //varMap.put(StringConstants.TASK_ASSIGNEE_NAME,SecurityUtils.getLogInEmpName());
        if (!ObjectUtils.isEmpty(processNodeSubmit)) {
            this.getService().addProcessNode(BpmProcessNodeSubmit.builder()
                    .state(0)
                    .nodeKey(task.getTaskDefinitionKey())
                    .processInstanceId(task.getProcessInstanceId())
                    .backType(0)
                    .createUser(task.getAssignee())
                    .build());
            boolean nextElementParallelGateway=false;
            PvmActivity nextElement = additionalInfoService.getNextElement(task.getTaskDefinitionKey(), task.getProcessInstanceId());
            if (nextElement != null) {
                String type = (String) nextElement.getProperty("type");
                if ("parallelGateway".equals(type)) {
                    nextElementParallelGateway=true;
                    List<Task> tasks = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
                    List<String> currentTaskDefKeys = tasks.stream().map(TaskInfo::getTaskDefinitionKey).distinct().collect(Collectors.toList());
                    if(currentTaskDefKeys.size()<=1){
                        if (nextElement.getOutgoingTransitions().size() > 1) {
                            restoreNodeKey = "";
                        } else {
                            restoreNodeKey = nextElement.getOutgoingTransitions().get(0).getDestination().getId();
                        }
                    }
                }
            }
            if (processNodeSubmit.getState().equals(0)) {
                if (!StringUtils.isEmpty(restoreNodeKey)) {
                    processJump.commitProcess(task.getId(), varMap, restoreNodeKey);
                } else {
                    taskService.complete(task.getId(), varMap);
                }
            } else {
                if(nextElementParallelGateway &&(processNodeSubmit.getBackType()==1||processNodeSubmit.getBackType()==2)){
                    taskService.complete(task.getId(), varMap);
                    return;
                }
                // node disagree type（1：back to previous node submit next node 2：back to initiator submit next node
                // 3. back to initiator submit next node 4. back to history node submit next node 5. back to history node submit back node
                String targetNodeKey = processNodeSubmit.getNodeKey();
                switch (processNodeSubmit.getBackType()) {
                    case 1:
                    case 4:
                        if (isMultiPlayerTargetNode(task, targetNodeKey) && isSingleActiveNode(task)) {
                            moveToMultiPlayerTarget(task, targetNodeKey);
                        } else {
                            processJump.commitProcess(task.getId(), varMap, targetNodeKey);
                        }
                        break;
                    case 2:
                    case 3:
                    case 5:
                        processJump.commitProcess(task.getId(), varMap, processNodeSubmit.getNodeKey());
                        break;
                    default:
                        taskService.complete(task.getId(), varMap);
                        break;
                }
            }
        } else {
            taskService.complete(task.getId(), varMap);

            //执行自定义业务逻辑
            Collection<BpmnBizCustomService> beans = SpringBeanUtils.getBeans(BpmnBizCustomService.class);
            if (!ObjectUtils.isEmpty(beans)) {
                for (BpmnBizCustomService bean : beans) {
                    bean.execute(task);
                }
            }
        }
    }

    /**
     * 判断提交目标节点是否为多人节点(会签/或签)
     */
    private boolean isMultiPlayerTargetNode(Task task, String targetNodeKey) {
        if (StringUtils.isEmpty(targetNodeKey)) {
            return false;
        }
        try {
            Map<String, Object> variables = taskService.getVariables(task.getId());
            Object processNumberObj = variables == null ? null : variables.get("processNumber");
            if (processNumberObj == null) {
                return false;
            }
            List<BpmVariableMultiplayer> moreNodes = bpmVariableMultiplayerService.getBaseMapper()
                    .isMoreNode(processNumberObj.toString(), targetNodeKey);
            return !CollectionUtils.isEmpty(moreNodes);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断当前流程实例是否只存在单一活动节点(无并行分支),避免moveTo误删其他并行分支任务
     */
    private boolean isSingleActiveNode(Task task) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        if (tasks.isEmpty()) {
            return false;
        }
        long distinctKeys = tasks.stream().map(TaskInfo::getTaskDefinitionKey).distinct().count();
        return distinctKeys <= 1;
    }

    /**
     * 多人节点作为提交目标时,使用moveTo重建多实例任务,避免强制跳转导致流程中断
     */
    private void moveToMultiPlayerTarget(Task task, String targetNodeKey) {
        TaskFlowControlService taskFlowControlService = taskFlowControlServiceFactory.create(task.getProcessInstanceId());
        try {
            List<String> unMovedTasks = taskFlowControlService.moveTo(task.getTaskDefinitionKey(), targetNodeKey);
            List<String> strings = unMovedTasks.stream().distinct().collect(Collectors.toList());
            if (strings.size() > 0) {
                strings = strings.stream().filter(a -> !a.equals(task.getTaskDefinitionKey())).collect(Collectors.toList());
                taskMgmtMapper.deleteExecutionsByProcinstIdAndTaskDefKeys(task.getProcessInstanceId(), strings);
            }
        } catch (Exception e) {
            throw new AFBizException("流程提交跳转到多人节点失败!");
        }
    }
}
