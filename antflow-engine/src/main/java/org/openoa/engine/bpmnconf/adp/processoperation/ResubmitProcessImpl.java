package org.openoa.engine.bpmnconf.adp.processoperation;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnConfBizServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnProcessMigrationServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessNodeSubmitServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableSignUpPersonnelServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVerifyInfoServiceImpl;
import org.openoa.base.exception.AFBizException;

import org.openoa.base.entity.BpmBusinessProcess;

import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

import static org.openoa.base.constant.enums.ProcessSubmitStateEnum.PROCESS_SIGN_UP;
import static org.openoa.base.constant.enums.ProcessOperationEnum.*;

/**
 * @Author JimuOffice
 * @Description submit/approve
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
    @Autowired
    private BpmnProcessMigrationServiceImpl bpmnProcessMigrationService;
    @Autowired
    private BpmnConfBizServiceImpl bpmnConfCommonService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        vo.setStartUserId(SecurityUtils.getLogInEmpIdStr());
        vo.setStartUserName(SecurityUtils.getLogInEmpName());
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        vo.setBusinessId(bpmBusinessProcess.getBusinessId());
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).list();
        if (ObjectUtils.isEmpty(tasks)) {
            throw new AFBizException("当前流程已审批！");
        }
        if(tasks.stream().noneMatch(a->a.getAssignee().equals(SecurityUtils.getLogInEmpIdStr()))){
            throw new AFBizException("当前流程已审批！");
        }
        Task task;
        if (!ObjectUtils.isEmpty(vo.getTaskId())) {
            task = tasks.stream().filter(o -> o.getId().equals(vo.getTaskId())).findFirst().orElse(null);
        } else {
            task = tasks.get(0);
            if (StringUtils.isEmpty(task.getAssigneeName())) {
                task.setAssigneeName(SecurityUtils.getLogInEmpNameSafe());
            }
        }
        if (ObjectUtils.isEmpty(task)) {
            throw new AFBizException("当前流程代办已审批或不存在！");
        }
        String formKey = task.getFormKey();
        //实际上存的是label信息
        if (!StringUtils.isEmpty(formKey)) {
            NodeExtraInfoDTO extraInfoDTO = JSON.parseObject(formKey, NodeExtraInfoDTO.class);
            List<BpmnNodeLabelVO> nodeLabelVOS = extraInfoDTO.getNodeLabelVOS();
            if (!CollectionUtils.isEmpty(nodeLabelVOS)) {
                for (BpmnNodeLabelVO nodeLabelVO : nodeLabelVOS) {
                    if (StringConstants.DYNAMIC_CONDITION_NODE.equals(nodeLabelVO.getLabelValue())) {
                        if (tasks.size() == 1) {//只有当前节点到最后一个审批人了才执行迁移
                            boolean conditionsChanged = bpmnConfCommonService.migrationCheckConditionsChange(vo);
                           if(conditionsChanged){
                               bpmnProcessMigrationService.migrateAndJumpToCurrent(task, bpmBusinessProcess, vo, this::executeTaskCompletion);
                               return;
                           }
                        }
                    }
                }
            }
        }

        if (ObjectUtils.isEmpty(task)) {
            throw new AFBizException("当前流程代办已审批！");
        }

        executeTaskCompletion(vo, task, bpmBusinessProcess);
        vo.setStartUserId(bpmBusinessProcess.getCreateUser());//这里主要是为了发消息通知使用
    }

    private void executeTaskCompletion(BusinessDataVo vo, Task task, BpmBusinessProcess bpmBusinessProcess) {
        vo.setTaskId(task.getId());
//        BusinessDataVo businessDataVo = formFactory.getFormAdaptor(vo).consentData(vo);
        BusinessDataVo businessDataVo = vo;
        if (!vo.getIsOutSideAccessProc()) {
            businessDataVo = formFactory.getFormAdaptor(vo).consentData(vo);
        }

        //save process verify info
        BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                .builder()
                .verifyDate(new Date())
                .taskName(task.getName())
                .taskId(task.getId())
                .runInfoId(bpmBusinessProcess.getProcInstId())
                .verifyUserId(task.getAssignee())
                .verifyUserName(vo.getStartUserName())
                .taskDefKey(task.getTaskDefinitionKey())
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
        if(!StringConstants.CURRENT_USER_ALREADY_PROCESSED.equals(bpmVerifyInfo.getVerifyDesc())){
            verifyInfoService.addVerifyInfo(bpmVerifyInfo);
        }


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
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(), BUTTON_TYPE_RESUBMIT,
                BUTTON_TYPE_AGREE,
                BUTTON_TYPE_JP);
    }
}
