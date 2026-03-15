package org.openoa.engine.bpmnconf.adp.processoperation;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.flowcontrol.DefaultTaskFlowControlServiceFactory;
import org.openoa.engine.bpmnconf.service.flowcontrol.TaskFlowControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InsertNodeAfterCurrentImpl implements ProcessOperationAdaptor {
    @Autowired
    private DefaultTaskFlowControlServiceFactory taskFlowControlServiceFactory;
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private TaskService taskService;


    @Override
    public void doProcessButton(BusinessDataVo vo) {
        String processNumber = vo.getProcessNumber();
        //要添加的人会裂变成节点
        List<BaseIdTranStruVo> userInfos = vo.getUserInfos();
        if(StringUtils.isEmpty(processNumber)){
            throw new AFBizException("流程编号不能为空");
        }
        if (CollectionUtils.isEmpty(userInfos)){
            throw new AFBizException("要添加的节点人员不能为空");
        }
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if(null==bpmBusinessProcess){
            throw new AFBizException("未能根据流程编号找到流程信息:"+processNumber);
        }
        String procInstId = bpmBusinessProcess.getProcInstId();
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if(CollectionUtils.isEmpty(tasks)){
            log.error("流程实例未找到任务信息:{}",procInstId);
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"流程实例未找到任务信息:"+procInstId);
        }
       List<String> taskDefKeys=new ArrayList<>();
        List<String> assignees=new ArrayList<>();
        for (Task task : tasks) {
            taskDefKeys.add(task.getTaskDefinitionKey());
            assignees.add(task.getAssignee());
        }
        if(taskDefKeys.stream().distinct().count()>1){
            throw new AFBizException("流程实例中存在并行审批任务中,无法插入节点");
        }
        TaskFlowControlService taskFlowControlService = taskFlowControlServiceFactory.create(procInstId);
       try {
            assignees.addAll(userInfos.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList()));
           taskFlowControlService.split(taskDefKeys.get(0),  assignees.toArray(new String[0]));
       }catch (Exception e){
           log.error("插入节点失败:{}",e.getMessage());
           throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"插入节点失败:"+e.getMessage());
       }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_INSERT_AFTER_CURRENT_NODE);
    }
}
