package org.openoa.engine.bpmnconf.adp.processoperation;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ProcessNodeEnum;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.service.BpmVariableService;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.StrUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.common.TaskMgmtServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmFlowrunEntrustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FastForwardProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmVariableService bpmVariableService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected TaskMgmtServiceImpl taskMgmtService;
    @Autowired
    private ActivitiAdditionalInfoServiceImpl activitiAdditionalInfoService;
    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;
    @Autowired
    private BpmFlowrunEntrustService bpmFlowrunEntrustService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        String processNumber = vo.getProcessNumber();
        String taskDefKey = vo.getTaskDefKey();
        if(!StringUtils.hasText(processNumber)){
            throw new AFBizException("请输入流程编号");
        }
        if(!StringUtils.hasText(taskDefKey)){
            throw new AFBizException("请输入要跳转到的节点");
        }
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if(bpmBusinessProcess==null){
            throw  new AFBizException(Strings.lenientFormat("未能根据流程编号:%s找到流程信息",processNumber));
        }
        String procInstId=bpmBusinessProcess.getProcInstId();
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if (CollectionUtils.isEmpty(taskList)) {
            throw new AFBizException("未获取到当前流程信息!,流程编号:" + bpmBusinessProcess.getProcessinessKey());
        }

        for (Task task : taskList) {
            if (ProcessNodeEnum.compare(taskDefKey,task.getTaskDefinitionKey())<0) {
                throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"流程推进只能向前!");
            }
        }
        completeTaskRecursively(taskList,procInstId,taskDefKey,processNumber,vo.getApprovalComment(),bpmBusinessProcess.getProcessinessKey());
    }
    private void completeTaskRecursively( List<Task> taskList,String processInstanceId,String forwardToNodeElementId,String processNumber,String verifyComment,String processKey){
        if(CollectionUtils.isEmpty(taskList)){
            return;
        }

        Map<String, Object> varMap = new HashMap<>();
        varMap.put(StringConstants.TASK_ASSIGNEE_NAME,"管理员跳过");
        for (Task task : taskList) {
            if (ProcessNodeEnum.compare(task.getTaskDefinitionKey(),forwardToNodeElementId)>0) {//如果已经到当前节点后面了,就直接return掉了
                return;
            }
            String actual=SecurityUtils.getLogInEmpId();
            String actualName=SecurityUtils.getLogInEmpName();
            taskService.complete(task.getId(),varMap);
            BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                    .builder()
                    .verifyDate(new Date())
                    .taskName(task.getName())
                    .taskId(task.getId())
                    .runInfoId(processInstanceId)
                    .verifyUserId(actual)
                    .verifyUserName("管理员-"+actualName)
                    .taskDefKey(task.getTaskDefinitionKey())
                    .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                    .verifyDesc("管理员跳过,原因:"+ StrUtils.nullOrBlankToWhiteSpace(verifyComment))
                    .processCode(processNumber)
                    .build();
            bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);
            bpmFlowrunEntrustService.addFlowrunEntrust(actual,actualName,task.getAssignee(),task.getAssigneeName(),task.getTaskDefinitionKey(),0,
                    processInstanceId,processKey);
        }
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        completeTaskRecursively(tasks,processInstanceId,forwardToNodeElementId,processNumber,verifyComment,processKey);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_PROCESS_MOVE_AHEAD);
    }
}
