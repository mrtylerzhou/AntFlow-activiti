package org.openoa.engine.bpmnconf.adp.processoperation;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.ThirdPartyCallBackServiceImpl;
import org.openoa.base.exception.AFBizException;

import org.openoa.base.entity.BpmBusinessProcess;

import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.ProcessStateEnum.REJECT_STATE;
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
    protected BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;
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
        boolean isAbandon=ProcessOperationEnum.BUTTON_TYPE_ABANDON.getCode().equals(vo.getOperationType());
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

        Integer processState = vo.getFlag() ? END_STATE.getCode() : REJECT_STATE.getCode();
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).list();
        if(CollectionUtils.isEmpty(taskList)){
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"当前流程实例不存在!");
        }
        Task taskData;
        if (isAbandon) {
            taskData = taskList.get(0);
        } else {
            taskData = taskList.stream().filter(task -> SecurityUtils.getLogInEmpId().equals(task.getAssignee()))
                    .findFirst().orElseThrow(() -> new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(), "当前流程已审批!"));
        }
        //save verify info
        bpmVerifyInfoBizService.addVerifyInfo(BpmVerifyInfo.builder()
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
        //terminate process (update state + delete process instance + cancellation)
        endProcessWithoutVerify(vo);
    }

    /**
     * terminate a process without recording verify info, so that other handlers
     * (e.g. OpposeProcessImpl) can terminate the process while recording their own verify info.
     *
     * @param vo business data vo
     */
    public void endProcessWithoutVerify(BusinessDataVo vo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        String processInstanceId = bpmBusinessProcess.getProcInstId();
        Integer processState = vo.getFlag() ? END_STATE.getCode() : REJECT_STATE.getCode();
        //update process state
        bpmBusinessProcessService.updateBusinessProcess(BpmBusinessProcess.builder()
                .businessNumber(bpmBusinessProcess.getBusinessNumber())
                .processState(processState)
                .build());
        //stop a process
        businessContans.deleteProcessInstance(processInstanceId);
        //call business adaptor method
        vo.setBusinessId(bpmBusinessProcess.getBusinessId());
        if(!vo.getIsOutSideAccessProc()){
            formFactory.getFormAdaptor(vo).cancellationData(vo);
        }
        vo.setStartUserId(bpmBusinessProcess.getCreateUser());
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_STOP,
                ProcessOperationEnum.BUTTON_TYPE_DIS_AGREE,
                ProcessOperationEnum.BUTTON_TYPE_ABANDON);
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(), ProcessOperationEnum.BUTTON_TYPE_STOP, ProcessOperationEnum.BUTTON_TYPE_DIS_AGREE,
                ProcessOperationEnum.BUTTON_TYPE_ABANDON);
    }
}
