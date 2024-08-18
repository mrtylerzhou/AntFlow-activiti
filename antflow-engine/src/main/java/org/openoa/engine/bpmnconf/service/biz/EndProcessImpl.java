package org.openoa.engine.bpmnconf.service.biz;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.engine.bpmnconf.confentity.BpmVerifyInfo;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmVerifyInfoServiceImpl;
import org.openoa.base.exception.JiMuBizException;

import org.openoa.base.entity.BpmBusinessProcess;

import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.openoa.base.constant.enums.ProcessStateEnum.CRMCEL_STATE;
import static org.openoa.base.constant.enums.ProcessStateEnum.END_STATE;

/**
 * end/abort/disagree a process
 */
@Component
public class EndProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private FormFactory formFactory;
    @Autowired
    protected BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private BpmVerifyInfoServiceImpl verifyInfoService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private ProcessBusinessContans businessContans;
    @Autowired
    private TaskService taskService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());

        //get the permission right
        List<HistoricProcessInstance> hisList = Optional.of(historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(bpmBusinessProcess.getEntryId()).list()).orElse(Arrays.asList());
        if (ObjectUtils.isEmpty(hisList)) {
            throw new JiMuBizException("当前流程已审批！");
        }
        String processInstanceId = hisList.get(0).getId();
        Integer processState = CRMCEL_STATE.getCode();
        if (vo.getFlag()) {
            processState = END_STATE.getCode();
        }
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).taskAssignee(SecurityUtils.getLogInEmpId().toString()).list();
        Task taskData;
        if (!ObjectUtils.isEmpty(taskList)) {
            taskData = taskList.get(0);
        } else {
            throw  new JiMuBizException("当前流程已审批!");
        }
        //update process state
        bpmBusinessProcessService.updateBusinessProcess(BpmBusinessProcess.builder()
                .businessNumber(bpmBusinessProcess.getBusinessNumber())
                .processState(processState)
                .build());
        //save verify info
        verifyInfoService.addVerifyInfo(BpmVerifyInfo.builder()
                .businessId(bpmBusinessProcess.getBusinessId().toString())
                .verifyUserName(SecurityUtils.getLogInEmpName())
                .verifyUserId(SecurityUtils.getLogInEmpIdStr())
                .verifyStatus(processState.equals(END_STATE.getCode()) ? ProcessSubmitStateEnum.END_AGRESS_TYPE.getCode() : processState)
                .verifyDate(new Date())
                .processCode(vo.getProcessNumber())
                .verifyDesc(vo.getApprovalComment())
                .taskName(taskData.getName())
                .taskId(taskData.getId())
                .procInstId(bpmBusinessProcess.getProcInstId())
                .build());

        //stop a process
        businessContans.deleteProcessInstance(processInstanceId);
        //call business adaptor method
        vo.setBusinessId(bpmBusinessProcess.getBusinessId());
        formFactory.getFormAdaptor(vo).cancellationData(vo.getBusinessId());

    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_STOP,
                ProcessOperationEnum.BUTTON_TYPE_DIS_AGREE,
                ProcessOperationEnum.BUTTON_TYPE_ABANDON);
    }
}
