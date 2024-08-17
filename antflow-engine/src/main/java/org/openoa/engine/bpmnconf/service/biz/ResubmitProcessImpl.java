package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.engine.bpmnconf.confentity.BpmVerifyInfo;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessNodeSubmitServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableSignUpPersonnelServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVerifyInfoServiceImpl;
import org.openoa.base.exception.JiMuBizException;

import org.openoa.base.entity.BpmBusinessProcess;

import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

import static org.openoa.base.constant.enums.ProcessSubmitStateEnum.PROCESS_SIGN_UP;
import static org.openoa.base.constant.enums.ProcessOperationEnum.*;

/**
 *@Author JimuOffice
 * @Description 流程的重新提交/同意操作
 * @Date 2022-04-28 15:55
 * @Param
 * @return
 * @Version 1.0
 */
@Component
public class ResubmitProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private FormFactory formFactory;
    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private BpmVerifyInfoServiceImpl verifyInfoService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmProcessNodeSubmitServiceImpl processNodeSubmitService;

    @Autowired
    private BpmVariableSignUpPersonnelServiceImpl bpmVariableSignUpPersonnelService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        vo.setBusinessId(bpmBusinessProcess.getBusinessId());

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).taskAssignee(SecurityUtils.getLogInEmpIdStr()).list();
        if (ObjectUtils.isEmpty(tasks)) {
            throw new JiMuBizException("当前流程已审批！");
        }
        Task task;
        if (!ObjectUtils.isEmpty(vo.getTaskId())) {
            task = tasks.stream().filter(o -> o.getId().equals(vo.getTaskId())).findFirst().orElse(null);
        } else {
            task = tasks.get(0);
        }
        if (ObjectUtils.isEmpty(task)) {
            throw new JiMuBizException("当前流程代办已审批！");
        }
        vo.setTaskId(task.getId());
        BusinessDataVo businessDataVo = formFactory.getFormAdaptor(vo).consentData(vo);
        //todo 获取当前登陆用户信息
        //保存流程审批记录
        BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                .builder()
                .verifyDate(new Date())
                .taskName(task.getName())
                .taskId(task.getId())
                .procInstId(bpmBusinessProcess.getProcInstId())
                .verifyUserId(SecurityUtils.getLogInEmpIdStr())
                .verifyUserName(SecurityUtils.getLogInEmpName())
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                .verifyDesc(ObjectUtils.isEmpty(vo.getApprovalComment()) ? "同意" : vo.getApprovalComment())
                .processCode(vo.getProcessNumber())
                .build();


        //if process digest is not empty then update process digest
        if (!ObjectUtils.isEmpty(businessDataVo) && !ObjectUtils.isEmpty(businessDataVo.getProcessDigest())) {
            bpmBusinessProcessService.update(BpmBusinessProcess
                    .builder()
                    .processDigest(businessDataVo.getProcessDigest())
                    .build(), new QueryWrapper<BpmBusinessProcess>()
                    .eq("BUSINESS_NUMBER", businessDataVo.getProcessNumber()));
        }

        if (vo.getOperationType().intValue() == BUTTON_TYPE_JP.getCode().intValue()) {
            bpmVerifyInfo.setVerifyStatus(PROCESS_SIGN_UP.getCode());
            bpmVerifyInfo.setVerifyDesc(ObjectUtils.isEmpty(vo.getApprovalComment()) ? "加批" : vo.getApprovalComment());
        }
        verifyInfoService.addVerifyInfo(bpmVerifyInfo);

        //process node sign up
        if (!ObjectUtils.isEmpty(vo.getOperationType()) && vo.getOperationType().intValue() == BUTTON_TYPE_JP.getCode()) {
            bpmVariableSignUpPersonnelService.insertSignUpPersonnel(taskService, task.getId(), vo.getProcessNumber(), task.getTaskDefinitionKey(), task.getAssignee(), vo.getSignUpUsers());
        }
        //submit process
        processNodeSubmitService.processComplete(task);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(
                BUTTON_TYPE_RESUBMIT,
                BUTTON_TYPE_AGREE,
                BUTTON_TYPE_JP
        );
    }
}
