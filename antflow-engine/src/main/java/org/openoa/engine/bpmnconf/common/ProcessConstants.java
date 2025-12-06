package org.openoa.engine.bpmnconf.common;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.entity.ActHiTaskinst;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.NodeUtil;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.NodeLabelConstants;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.engine.bpmnconf.service.impl.ActHiTaskinstServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * process common service
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Service
public class ProcessConstants extends ProcessServiceFactory {
    @Autowired
    private ActHiTaskinstServiceImpl actHiTaskinstService;

    /**
     * get next node activity by process instance id
     *
     * @param procInstanceId
     * @return
     */
    public PvmActivity getNextNodePvmActivity(String procInstanceId) {

        //1.get current tasks by process instance id
        List<Task> tasks = processEngine.getTaskService().createTaskQuery().processInstanceId(procInstanceId).list();
        String nextId = "";
        for (Task task : tasks) {
            RepositoryService rs = processEngine.getRepositoryService();

            // 2. then get current process definition and get all activities
            ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) rs)
                    .getDeployedProcessDefinition(task.getProcessDefinitionId());
            List<ActivityImpl> activitiList = def.getActivities();

            //3. get task's execution id
            String excId = task.getExecutionId();
            RuntimeService runtimeService = processEngine.getRuntimeService();
            ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(excId)
                    .singleResult();
            String activitiId = execution.getActivityId();

            // iterate all activities, if id equals current activitiId, get this activity's outgoing transitions,finally get its destination
            for (ActivityImpl activityImpl : activitiList) {
                String id = activityImpl.getId();
                if (id.equals(activitiId)) {
                    List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
                    for (PvmTransition tr : outTransitions) {
                        PvmActivity ac = tr.getDestination();
                        return ac;
                    }
                    break;
                }
            }
        }
        return null;
    }




    /**
     * get value by map
     *
     * @param map
     * @param key
     * @return
     */
    public String getMapValue(Map<String, Object> map, String key) {
        return Optional.ofNullable(map)
                .map(o -> {
                    if (o.containsKey(key)) {
                        return Optional.ofNullable(o.get(key))
                                .orElse("").toString();
                    }
                    return "";
                })
                .orElse("");
    }


    /**
     * query task by business id and process number
     */
    public String getTaskId(String businessId, String processCode) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessMapper.findBpmBusinessProcess(BpmBusinessProcess.builder().businessId(businessId).businessNumber(processCode).build());
        if (!ObjectUtils.isEmpty(bpmBusinessProcess)) {
            TaskMgmtVO taskMgmtVO = SpringBeanUtils.getBean(TaskMgmtServiceImpl.class).findByTask(TaskMgmtVO.builder().businessId(businessId).code(processCode).build());
            if (!ObjectUtils.isEmpty(taskMgmtVO)) {
                throw new AFBizException("00", "current task is finished！！！");
            } else {
                return taskMgmtVO.getTaskId();
            }
        } else {
            return null;
        }
    }


    /**
     * get all tasks by process instance id and task key
     *
     * @param processInstanceId
     * @return
     */
    public List<Task> findTaskListByKey(String processInstanceId, String key) {
        return taskService.createTaskQuery().processInstanceId(
                processInstanceId).taskDefinitionKey(key).list();
    }

    public ActHiTaskinst getPrevTask(String taskDefKey,String procInstId){
        if(StringUtils.isEmpty(taskDefKey)){
            return null;
        }
        if(StringUtils.isEmpty(procInstId)){
            throw new AFBizException("taskId不为空,流程实例Id不存在!");
        }
        List<ActHiTaskinst> list = actHiTaskinstService.queryRecordsByProcInstIdOrderByCreateTimeDesc(procInstId);
        ActHiTaskinst historicTaskInstance = list.stream().filter(a ->{
            boolean b= a.getEndTime()!=null&& !taskDefKey.equals(a.getTaskDefKey());
            String formKey = a.getFormKey();
            boolean c=true;
            if(!StringUtils.isEmpty(formKey)){
                NodeExtraInfoDTO extraInfoDTO = JSON.parseObject(formKey, NodeExtraInfoDTO.class);
                c=NodeUtil.nodeLabelContainsAny(extraInfoDTO,NodeLabelConstants.copyNodeV2.getLabelValue());
            }
            return b&&c;
        }).findFirst().orElse(null);
        return historicTaskInstance;
    }
}
