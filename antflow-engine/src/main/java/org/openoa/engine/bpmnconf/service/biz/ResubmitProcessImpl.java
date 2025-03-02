package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmFlowrunEntrust;
import org.openoa.engine.bpmnconf.confentity.BpmVerifyInfo;
import org.openoa.engine.bpmnconf.mapper.BpmTaskconfigMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessNodeSubmitServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableSignUpPersonnelServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVerifyInfoServiceImpl;
import org.openoa.base.exception.JiMuBizException;

import org.openoa.base.entity.BpmBusinessProcess;

import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.openoa.base.constant.enums.ProcessSubmitStateEnum.PROCESS_SIGN_UP;
import static org.openoa.base.constant.enums.ProcessOperationEnum.*;

/**
 *@Author JimuOffice
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
    private ProcessApprovalServiceImpl processApprovalService;
    @Autowired
    private ActivitiAdditionalInfoServiceImpl additionalInfoService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;


    @Override
    public void doProcessButton(BusinessDataVo vo) {
        vo.setStartUserId(SecurityUtils.getLogInEmpIdStr());
        vo.setStartUserName(SecurityUtils.getLogInEmpName());
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
            if(StringUtils.isEmpty(task.getAssigneeName())){
                task.setAssigneeName(SecurityUtils.getLogInEmpNameSafe());
            }
        }
        if(1==2){
            BusinessDataVo submitVo=new BusinessDataVo();
            submitVo.setAccountType(1);
            submitVo.setFormCode("DSFZH_WMA");
            submitVo.setOperationType(1);
            submitVo.setIsLowCodeFlow(0);
            submitVo.setBusinessId(bpmBusinessProcess.getBusinessId());
            submitVo.setProcessNumber(bpmBusinessProcess.getBusinessNumber());
            submitVo.setIsMigration(true);
            submitVo.setStartUserId(bpmBusinessProcess.getCreateUser());
            processApprovalService.buttonsOperation(JSON.toJSONString(submitVo),submitVo.getFormCode());
            bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
            String procDefIdByInstId = taskMgmtMapper.findProcDefIdByInstId(bpmBusinessProcess.getProcInstId());
            if(StringUtils.isBlank(procDefIdByInstId)){
                throw new JiMuBizException("未能根据流程实例id查找到流程定义id,请检查逻辑!");
            }
            List<ActivityImpl> activitiList = additionalInfoService.getActivitiList(procDefIdByInstId);
            boolean currentExecuted=false;
            for (ActivityImpl activity : activitiList) {
                if (currentExecuted) {
                    break;
                }
                String id = activity.getId();

                // 查找当前流程实例的任务
                List<Task> tsks = taskService.createTaskQuery()
                        .processInstanceId(bpmBusinessProcess.getProcInstId())
                        .taskDefinitionKey(activity.getId()).list();
                if (!CollectionUtils.isEmpty(tsks)) {
                    Map<String, BpmVerifyInfo> verifyInfoMap =verifyInfoMap=verifyInfoService.getByProcInstIdAndTaskDefKey(bpmBusinessProcess.getBusinessNumber(), id);
                    for (Task tsk : tsks) {
                        if(!CollectionUtils.isEmpty(verifyInfoMap)){
                            BpmVerifyInfo bpmVerifyInfo = verifyInfoMap.get(tsk.getTaskDefinitionKey() + tsk.getAssignee());
                            vo.setStartUserId(tsk.getAssignee());
                            if(bpmVerifyInfo!=null){
                                if(!StringUtils.isEmpty(tsk.getAssigneeName())){
                                    vo.setStartUserName(tsk.getAssigneeName());
                                }else{
                                   vo.setStartUserName(bpmVerifyInfo.getVerifyUserName());
                                }
                                vo.setApprovalComment(bpmVerifyInfo.getVerifyDesc());
                            }else{
                                if(!StringUtils.isEmpty(tsk.getAssigneeName())){
                                    vo.setStartUserName(tsk.getAssigneeName());
                                }
                            }
                        }
                        executeTaskCompletion(vo,tsk,bpmBusinessProcess);
                    }
                }
                if (task.getTaskDefinitionKey().equals(id)) {
                    currentExecuted = true;
                }
            }
            return;
        }
        if (ObjectUtils.isEmpty(task)) {
            throw new JiMuBizException("当前流程代办已审批！");
        }

        executeTaskCompletion(vo,task,bpmBusinessProcess);
    }
    private void executeTaskCompletion(BusinessDataVo vo,Task task,BpmBusinessProcess bpmBusinessProcess){
        vo.setTaskId(task.getId());
//        BusinessDataVo businessDataVo = formFactory.getFormAdaptor(vo).consentData(vo);
        BusinessDataVo businessDataVo = vo;
        if(!vo.getIsOutSideAccessProc()){
            businessDataVo= formFactory.getFormAdaptor(vo).consentData(vo);
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
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(),  BUTTON_TYPE_RESUBMIT,
                BUTTON_TYPE_AGREE,
                BUTTON_TYPE_JP);
    }
}
