package org.openoa.engine.bpmnconf.service.biz;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.engine.bpmnconf.confentity.BpmVerifyInfo;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.engine.bpmnconf.service.biz.callback.BusinessCallBackFactory;
import org.openoa.engine.bpmnconf.service.impl.BpmVerifyInfoServiceImpl;
import org.openoa.base.exception.JiMuBizException;

import org.openoa.base.entity.BpmBusinessProcess;

import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.factory.FormFactory;
import org.openoa.engine.factory.ThirdPartyCallbackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static org.openoa.base.constant.enums.CallbackTypeEnum.PROC_END_CALL_BACK;
import static org.openoa.base.constant.enums.ProcessOperationEnum.BUTTON_TYPE_AGREE;
import static org.openoa.base.constant.enums.ProcessStateEnum.CRMCEL_STATE;
import static org.openoa.base.constant.enums.ProcessStateEnum.END_STATE;

/**
 * end/abort/disagree a process
 */
@Slf4j
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
    @Autowired
    private ThirdPartyCallBackServiceImpl thirdPartyCallBackService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());

        String verifyUserName = StringUtils.EMPTY;

        String verifyUserId = StringUtils.EMPTY;

        if (vo.getIsOutSideAccessProc()) {
            Map<String, Object> objectMap = vo.getObjectMap();
            if (!CollectionUtils.isEmpty(objectMap)) {
                verifyUserName = Optional.ofNullable(objectMap.get("employeeName")).map(String::valueOf).orElse(StringUtils.EMPTY);
                verifyUserId = Optional.ofNullable(objectMap.get("employeeId")).map(Object::toString).orElse("");
            }
        } else {
                verifyUserName =SecurityUtils.getLogInEmpName();
                verifyUserId = SecurityUtils.getLogInEmpIdStr();
        }

        String processInstanceId = bpmBusinessProcess.getProcInstId();
        Integer processState = CRMCEL_STATE.getCode();
        if (vo.getFlag()) {
            processState = END_STATE.getCode();
        }
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).taskAssignee(SecurityUtils.getLogInEmpId()).list();
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
                .businessId(bpmBusinessProcess.getBusinessId())
                .verifyUserId(verifyUserId)
                .verifyUserName(verifyUserName)
                .verifyStatus(processState.equals(END_STATE.getCode()) ? ProcessSubmitStateEnum.END_AGRESS_TYPE.getCode() : processState)
                .verifyDate(new Date())
                .processCode(vo.getProcessNumber())
                .verifyDesc(vo.getApprovalComment())
                .taskName(taskData.getName())
                .taskId(taskData.getId())
                .runInfoId(bpmBusinessProcess.getProcInstId())
                .build());

        //stop a process
        businessContans.deleteProcessInstance(processInstanceId);
        //call business adaptor method
        vo.setBusinessId(bpmBusinessProcess.getBusinessId());
        if(!vo.getIsOutSideAccessProc()){
            formFactory.getFormAdaptor(vo).cancellationData(vo);
        }

    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_STOP,
                ProcessOperationEnum.BUTTON_TYPE_DIS_AGREE,
                ProcessOperationEnum.BUTTON_TYPE_ABANDON);
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(),  ProcessOperationEnum.BUTTON_TYPE_DIS_AGREE,
                ProcessOperationEnum.BUTTON_TYPE_ABANDON);
    }
}
