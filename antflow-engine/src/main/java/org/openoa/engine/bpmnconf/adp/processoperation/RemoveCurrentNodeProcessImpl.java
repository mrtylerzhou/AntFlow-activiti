package org.openoa.engine.bpmnconf.adp.processoperation;

import org.activiti.engine.TaskService;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BpmVerifyInfoVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.service.flowcontrol.DefaultTaskFlowControlServiceFactory;
import org.openoa.engine.bpmnconf.service.flowcontrol.MultiInstanceSignOffService;
import org.openoa.engine.bpmnconf.service.flowcontrol.TaskFlowControlService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RemoveCurrentNodeProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private DefaultTaskFlowControlServiceFactory taskFlowControlServiceFactory;
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private MultiInstanceSignOffService multiInstanceSignOffService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;
    @Autowired
    private ActivitiAdditionalInfoServiceImpl additionalInfoService;
    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        String processNumber = vo.getProcessNumber();
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        String procInstId = bpmBusinessProcess.getProcInstId();
        TaskFlowControlService taskFlowControlService = taskFlowControlServiceFactory.create(procInstId);
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        List<String> taskDefKeys = tasks.stream().map(TaskInfo::getTaskDefinitionKey).distinct().collect(Collectors.toList());
        if(taskDefKeys.size()>1){
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"当前流程存在多个并行节点,无法移除当前节点!");
        }

        String taskDefkey = taskDefKeys.get(0);
        if(tasks.size()>1){
            String nodeId = bpmVariableMultiplayerMapper.getNodeIdByElementId(processNumber, taskDefkey);
            List<Task> tasksExceptLastOne = tasks.subList(0, tasks.size() - 1);
            for (Task task : tasksExceptLastOne) {
                multiInstanceSignOffService.removeAssignee(processNumber,task.getTaskDefinitionKey(),task.getAssignee(),task.getAssigneeName(),nodeId);
            }
        }

        try {
            List<BpmVerifyInfoVo> bpmVerifyInfoVos = bpmVerifyInfoBizService.getBpmVerifyInfoVos(processNumber, false);
            String nextNodeTaskDefKey="";
            int fitIndex=-1;
            for (int i = 0; i < bpmVerifyInfoVos.size(); i++) {
                String elementId = bpmVerifyInfoVos.get(i).getElementId();
                if(taskDefkey.equals(elementId)){
                    fitIndex=i+1;
                }
                if(fitIndex==i){
                    nextNodeTaskDefKey=bpmVerifyInfoVos.get(i).getElementId();
                    break;
                }
            }
            if(StringUtils.isBlank(nextNodeTaskDefKey)){
                throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"未找到当前节点下一个审批人节点,操作失败!");
            }
            List<String> strings = taskFlowControlService.moveTo(tasks.get(0).getTaskDefinitionKey(), nextNodeTaskDefKey);
        }catch (Exception e){
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"移除当前节点出错啦!");
        }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_REMOVE_CURRENT_NODE);
    }
}
