package org.openoa.engine.bpmnconf.adp.processoperation;

import org.activiti.engine.ManagementService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.cmd.MultiCharacterInstanceParallelSign;
import org.openoa.engine.bpmnconf.service.cmd.MultiCharacterInstanceSequentialSign;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.openoa.engine.utils.MultiInstanceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AddAssigneeProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private TaskService taskService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private ActivitiAdditionalInfoServiceImpl additionalInfoService;
    @Autowired
    private BpmFlowrunEntrustServiceImpl flowrunEntrustService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        String processNumber = vo.getProcessNumber();
        String taskDefKey = vo.getTaskDefKey();
        List<BaseIdTranStruVo> userInfos = vo.getUserInfos();
        if(CollectionUtils.isEmpty(userInfos)){
            throw new RuntimeException("请选择要加签的人员");
        }
        if(userInfos.size()>1){
            throw new JiMuBizException("每次加能加签1人");
        }
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if(bpmBusinessProcess==null){
            throw new RuntimeException("未能根据流程编号找到流程实例:"+processNumber);
        }
        String procInstId = bpmBusinessProcess.getProcInstId();
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).taskDefinitionKey(taskDefKey).list();
        if(CollectionUtils.isEmpty(tasks)){
            throw new RuntimeException("未能根据流程实例id:"+procInstId+"和任务节点key:"+taskDefKey+"找到当前审批任务");
        }
        Task task = tasks.get(0);
        List<ActivityImpl> activitiList = additionalInfoService.getActivitiList(task.getProcessDefinitionId());
        if(CollectionUtils.isEmpty(activitiList)){
            throw new RuntimeException("未能根据流程定义id:"+task.getProcessDefinitionId()+"找到流程图");
        }
        List<ActivityImpl> currentActivities = activitiList.stream().filter(a -> a.getId().equals(task.getTaskDefinitionKey())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(currentActivities)){
            throw new RuntimeException("未能根据流程节点key:"+task.getTaskDefinitionKey()+"找到流程图对应节点信息");
        }
        ActivityImpl currentActiviti = currentActivities.get(0);
        ActivityBehavior activityBehavior = currentActiviti.getActivityBehavior();
        if(!(activityBehavior instanceof MultiInstanceActivityBehavior)){
            throw new JiMuBizException("不支持非多实例节点!");
        }
        String collectionName = MultiInstanceUtils.getCollectionNameByBehavior(activityBehavior);
        Object variable = taskService.getVariable(task.getId(), collectionName);
        List<String> assigneeValues=new ArrayList<>();
        Iterable iterable = (Iterable) variable;
        Iterator iterator = iterable.iterator();
        List<String> assignees = userInfos.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
        while (iterator.hasNext()){
            Object next = iterator.next();
            String nextValue = next.toString();
            if (assignees.contains(nextValue)) {
               continue;
            }
            assigneeValues.add(nextValue);
            //添加在当前任务审批人后面
            if(task.getAssignee().equals(nextValue)){
               assigneeValues.addAll(assignees);
            }
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put(collectionName, assigneeValues);
        Command command=null;
        if(activityBehavior instanceof ParallelMultiInstanceBehavior){
            command=new MultiCharacterInstanceParallelSign(task.getId(), userInfos);

        }else if (activityBehavior instanceof SequentialMultiInstanceBehavior){
            command = new MultiCharacterInstanceSequentialSign(task.getId(), variables);
        }else {
            throw new JiMuBizException("不支持加签的节点类型!");
        }
        managementService.executeCommand(command);
        BaseIdTranStruVo userinfo = userInfos.get(0);
        flowrunEntrustService.addFlowrunEntrust(userinfo.getId(),userinfo.getName(),"0","管理员加签",task.getId(),0,
                task.getProcessInstanceId(),bpmBusinessProcess.getProcessinessKey());
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_ADD_ASSIGNEE);
    }
}
