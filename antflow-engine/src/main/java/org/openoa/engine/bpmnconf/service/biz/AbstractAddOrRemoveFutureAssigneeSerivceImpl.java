package org.openoa.engine.bpmnconf.service.biz;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AbstractAddOrRemoveFutureAssigneeSerivceImpl {

    @Autowired
    protected BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected BpmFlowrunEntrustServiceImpl bpmFlowrunEntrustService;
    @Autowired
    protected BpmBusinessProcessServiceImpl businessProcessService;

    protected void checkParam(BusinessDataVo vo){
        String processNumber = vo.getProcessNumber();
        List<BaseIdTranStruVo> userInfos = vo.getUserInfos();
        String nodeId = vo.getNodeId();
        if(StringUtils.isEmpty(processNumber)){
            throw new AFBizException("流程编号不能为空");
        }
        if (CollectionUtils.isEmpty(userInfos)){
            throw new AFBizException("要变更的人员信息不能为空");
        }
        if(StringUtils.isEmpty(nodeId)){
            throw new AFBizException("节点id不能为空");
        }
    }
    /**
     * 这里只需要把要添加/移动的用户的用户信息传进来就可以了,不用维护旧的
     * @param bpmBusinessProcess
     * @param taskdefKey
     * @param userInfos
     * @param action 1添加,2移除
     */
    protected void modifyFutureAssigneesByProcessInstance(BpmBusinessProcess bpmBusinessProcess, String taskdefKey, List<BaseIdTranStruVo> userInfos,int action) {

        String procInstId = bpmBusinessProcess.getProcInstId();
        String processNumber = bpmBusinessProcess.getBusinessNumber();
        String varName = bpmVariableMultiplayerMapper.getVarNameByElementId(processNumber, taskdefKey);
        Object currentValue = runtimeService.getVariable(procInstId, varName);
        if (!(currentValue instanceof List)) {
            throw new AFBizException("Variable " + varName + " is not a list.");
        }
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).taskDefinitionKey(taskdefKey).list();
        if(!CollectionUtils.isEmpty(tasks)){
            throw new AFBizException("流程任务已生成,不能修改,请使用变更对当前节点进行操作的方法修改!");
        }
        @SuppressWarnings("unchecked")
        List<String> currentList = new ArrayList<>((List<String>) currentValue);
        List<String> assigneeIds = userInfos.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
        if (action == 1) {
            currentList.addAll(assigneeIds);
        } else if (action == 2) {
            currentList.removeAll(assigneeIds);
        }else {
            throw new AFBizException("action is not 1 or 2");
        }

        runtimeService.setVariable(procInstId, varName, currentList);
        Map<String, Object> variables = runtimeService.getVariables(procInstId);
        String varSize = taskdefKey + "size";
        if (variables.get(varSize) != null) {
            runtimeService.setVariable(procInstId, varSize, currentList.size());
        }
        for (BaseIdTranStruVo userInfo : userInfos) {
            String userId = userInfo.getId();
            String userName = userInfo.getName();
            if (StringUtils.isEmpty(userName)) {
                continue;
            }
            String adminName=action==1?"管理员加签":"管理员减签";
            bpmFlowrunEntrustService.addFlowrunEntrust(userId, userName, "0", adminName, taskdefKey, 1, procInstId, bpmBusinessProcess.getProcessinessKey());
        }
       /* // 如多实例已启动，还需调整 nrOfInstances（局部变量）
        Execution miExecution = runtimeService.createExecutionQuery()
                .processInstanceId(bpmBusinessProcess.getProcInstId())
                .activityId(taskdefKey) // 多实例节点的 activityId
                .singleResult();
        if (miExecution != null) {
            Integer nr = (Integer) runtimeService.getVariableLocal(miExecution.getId(), "nrOfInstances");
            if (nr != null) {
                runtimeService.setVariableLocal(miExecution.getId(), "nrOfInstances", nr + 1);
            }
        }*/

    }
}
