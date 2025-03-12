package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.activiti.engine.impl.cmd.ProcessNodeJump;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmProcessNodeSubmit;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNodeSubmitMapper;
import org.openoa.base.constant.StringConstants;
import org.openoa.engine.bpmnconf.util.ProcessDefinitionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BpmProcessNodeSubmitServiceImpl extends ServiceImpl<BpmProcessNodeSubmitMapper, BpmProcessNodeSubmit> {

    @Autowired
    private BpmProcessNodeSubmitMapper bpmProcessNodeSubmitMapper;
    @Autowired
    private ProcessNodeJump processJump;
    @Autowired
    protected TaskService taskService;
    @Autowired
    private ActivitiAdditionalInfoServiceImpl additionalInfoService;

    /**
     * query to check whether the previous operation is submit or disagree
     *
     * @param processInstanceId
     * @return
     */
    public BpmProcessNodeSubmit findBpmProcessNodeSubmit(String processInstanceId) {
        QueryWrapper<BpmProcessNodeSubmit> wrapper = new QueryWrapper<>();
        wrapper.eq("processInstance_Id", processInstanceId);
        wrapper.orderByDesc("create_time");
        List<BpmProcessNodeSubmit> list = bpmProcessNodeSubmitMapper.selectList(wrapper);
        if (!ObjectUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * add process node submit info
     *
     * @return
     */
    public boolean addProcessNode(BpmProcessNodeSubmit processNodeSubmit) {
        QueryWrapper<BpmProcessNodeSubmit> wrapper = new QueryWrapper<>();
        wrapper.eq("processInstance_Id", processNodeSubmit.getProcessInstanceId());
        bpmProcessNodeSubmitMapper.delete(wrapper);
        bpmProcessNodeSubmitMapper.insert(processNodeSubmit);
        return true;
    }

    /**
     * delete node submit info
     */
    public boolean deleteProcessNode(String processInstanceId) {
        QueryWrapper<BpmProcessNodeSubmit> wrapper = new QueryWrapper<>();
        wrapper.eq("processInstance_Id", processInstanceId);
        bpmProcessNodeSubmitMapper.delete(wrapper);
        return true;
    }

    /**
     * 流程审批
     *
     * @param task
     */
    public void processComplete(Task task) {

        BpmProcessNodeSubmit processNodeSubmit = this.findBpmProcessNodeSubmit(task.getProcessInstanceId());
        String restoreNodeKey = "";
        Map<String, Object> varMap = new HashMap<>();
        //varMap.put(StringConstants.TASK_ASSIGNEE_NAME,SecurityUtils.getLogInEmpName());
        if (!ObjectUtils.isEmpty(processNodeSubmit)) {
            this.addProcessNode(BpmProcessNodeSubmit.builder()
                    .state(0)
                    .nodeKey(task.getTaskDefinitionKey())
                    .processInstanceId(task.getProcessInstanceId())
                    .backType(0)
                    .createUser(task.getAssignee())
                    .build());

            PvmActivity nextElement = additionalInfoService.getNextElement(task.getTaskDefinitionKey(), task.getProcessInstanceId());
            if (nextElement != null) {
                String type = (String) nextElement.getProperty("type");
                if ("parallelGateway".equals(type)) {
                    if (nextElement.getOutgoingTransitions().size() > 1) {
                        restoreNodeKey = "";
                    } else {
                        restoreNodeKey = nextElement.getOutgoingTransitions().get(0).getDestination().getId();
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
                // node disagree type（1：back to previous node submit next node 2：back to initiator submit next node
                // 3. back to initiator submit next node 4. back to history node submit next node 5. back to history node submit back node
                switch (processNodeSubmit.getBackType()) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        processJump.commitProcess(task.getId(), varMap, processNodeSubmit.getNodeKey());
                        break;
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
        }
    }
}


