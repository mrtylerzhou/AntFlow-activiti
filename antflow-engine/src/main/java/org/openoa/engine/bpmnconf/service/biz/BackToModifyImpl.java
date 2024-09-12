package org.openoa.engine.bpmnconf.service.biz;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.impl.cmd.ProcessNodeJump;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.engine.bpmnconf.common.TaskMgmtServiceImpl;
import org.openoa.base.constant.enums.ProcessDisagreeTypeEnum;
import org.openoa.base.constant.enums.ProcessNodeEnum;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.engine.bpmnconf.confentity.BpmProcessNodeSubmit;
import org.openoa.engine.bpmnconf.confentity.BpmVerifyInfo;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessNodeSubmitServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVerifyInfoServiceImpl;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.entity.BpmBusinessProcess;

import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

/**
 * back to modify
 */
@Component
public class BackToModifyImpl implements ProcessOperationAdaptor {

    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BpmVerifyInfoServiceImpl verifyInfoService;

    @Autowired
    private BpmProcessNodeSubmitServiceImpl processNodeSubmitService;

    @Autowired
    private ProcessNodeJump processNodeJump;

    @Autowired
    private FormFactory formFactory;

    @Autowired
    protected TaskMgmtServiceImpl taskMgmtService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        Task taskData = taskService.createTaskQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).taskAssignee(SecurityUtils.getLogInEmpIdStr()).list().get(0);
        if (ObjectUtils.isEmpty(taskData)) {
            throw new JiMuBizException("当前流程已审批！");
        }

        //save verify info
        verifyInfoService.addVerifyInfo(BpmVerifyInfo.builder()
                .businessId(bpmBusinessProcess.getBusinessId().toString())
                .verifyUserName(SecurityUtils.getLogInEmpName())
                .verifyUserId(SecurityUtils.getLogInEmpIdStr())
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_UPDATE_TYPE.getCode())
                .processCode(bpmBusinessProcess.getBusinessNumber())
                .runInfoId(bpmBusinessProcess.getProcInstId())
                .verifyDesc(vo.getApprovalComment())
                .taskName(taskData.getName())
                .taskId(taskData.getId())
                .build());

        //add back node
        processNodeSubmitService.addProcessNode(BpmProcessNodeSubmit.builder()
                .state(1)
                .nodeKey(taskData.getTaskDefinitionKey())
                .processInstanceId(taskData.getProcessInstanceId())
                .backType(ProcessDisagreeTypeEnum.THREE_DISAGREE.getCode())
                .createUser(SecurityUtils.getLogInEmpIdStr())
                .build());
        String taskNode = ProcessNodeEnum.START_TASK_KEY.getDesc();

        //get a list of running tasks,then reject all of them
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).list();
        //parallel tasks reject
        for (Task task : taskList) {
            //do reject
            processNodeJump.commitProcess(task.getId(), null, taskNode);
        }
        vo.setBusinessId(bpmBusinessProcess.getBusinessId());
        if(!vo.getIsOutSideAccessProc()){
            formFactory.getFormAdaptor(vo).backToModifyData(vo);
        }



        // back to specified person
        if (!ObjectUtils.isEmpty(vo.getBackToEmployeeId())) {
            //save back userId
            bpmBusinessProcess.setBackUserId(vo.getBackToEmployeeId());
            bpmBusinessProcessService.updateById(bpmBusinessProcess);

            Task task = taskService.createTaskQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).singleResult();
            TaskMgmtVO taskMgmtVO = TaskMgmtVO.builder().taskIds(Collections.singletonList(task.getId())).applyUser(vo.getBackToEmployeeId()).build();
            taskMgmtService.updateTask(taskMgmtVO);
        }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_BACK_TO_MODIFY);
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(),ProcessOperationEnum.BUTTON_TYPE_BACK_TO_MODIFY);
    }
}
