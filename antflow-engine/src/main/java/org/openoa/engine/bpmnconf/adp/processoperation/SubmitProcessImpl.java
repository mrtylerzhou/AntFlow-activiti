package org.openoa.engine.bpmnconf.adp.processoperation;


import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.exception.AFBizException;

import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmProcessName;

import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnConfBizServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessNameBizService;
import org.openoa.engine.factory.FormFactory;
import org.openoa.base.util.MultiTenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * process submit
 */
@Service
@Slf4j
public class SubmitProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private FormFactory formFactory;
    @Autowired
    private BpmnConfBizServiceImpl bpmnConfCommonService;
    @Autowired
    protected BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Autowired
    private BpmProcessNameBizService bpmProcessNameBizService;

    @Override
    public void doProcessButton(BusinessDataVo businessDataVo) {
        log.info("Start submit process. param:{}", businessDataVo);
        FormOperationAdaptor formAdapter = formFactory.getFormAdaptor(businessDataVo);


        if(!Boolean.TRUE.equals(businessDataVo.getIsMigration())){
           formAdapter.submitData(businessDataVo);
        }
        // call the process's launch method to get launch parameters
        BpmnStartConditionsVo bpmnStartConditionsVo = formAdapter.launchParameters(businessDataVo);
        bpmnStartConditionsVo.setBusinessDataVo(businessDataVo);
        bpmnStartConditionsVo.setApproversList(Optional.ofNullable(businessDataVo.getApproversList()).orElse(Maps.newHashMap()));
        String processNumber=businessDataVo.getFormCode() + "_" + businessDataVo.getBusinessId();
        if(Boolean.TRUE.equals(businessDataVo.getIsMigration())){
            processNumber=businessDataVo.getProcessNumber();
        }
        if(StringUtils.isEmpty(businessDataVo.getProcessNumber())){
            businessDataVo.setProcessNumber(processNumber);
        }
        bpmnStartConditionsVo.setProcessNum(processNumber);
        bpmnStartConditionsVo.setEntryId(businessDataVo.getEntityName() + ":" + businessDataVo.getBusinessId());
        bpmnStartConditionsVo.setBusinessId(businessDataVo.getBusinessId());
        bpmnStartConditionsVo.setApprovalEmpls(businessDataVo.getApprovalEmpls());
        bpmnStartConditionsVo.setLowCodeFlow(businessDataVo.getIsLowCodeFlow()!=null&&businessDataVo.getIsLowCodeFlow()==1);
        if(Boolean.TRUE.equals(businessDataVo.getIsMigration())){
            bpmnStartConditionsVo.setIsMigration(businessDataVo.getIsMigration());
        }else{
            String entryId = businessDataVo.getEntityName() + ":" + businessDataVo.getBusinessId();
            if (!bpmBusinessProcessService.checkProcessData(entryId)) {
                throw new AFBizException("the process has already been submitted！");
            }
        }

        //process's name
        String processName = Optional
                .ofNullable(bpmProcessNameBizService.getBpmProcessName(businessDataVo.getFormCode()))
                .orElse(new BpmProcessName()).getProcessName();
        //apply user info
        String applyName = SecurityUtils.getLogInEmpName();
        //save business and process information
        if(!Boolean.TRUE.equals(businessDataVo.getIsMigration())){
            bpmBusinessProcessService.addBusinessProcess(BpmBusinessProcess.builder()
                    .businessId(businessDataVo.getBusinessId())
                    .processinessKey(businessDataVo.getFormCode())
                    .businessNumber(processNumber)
                    .isLowCodeFlow(businessDataVo.getIsLowCodeFlow())
                    .createUser(businessDataVo.getStartUserId())
                    .userName(businessDataVo.getStartUserName())
                    .createTime(new Date())
                    .processDigest(businessDataVo.getProcessDigest())
                    .processState(ProcessStateEnum.HANDLING_STATE.getCode())
                    .entryId(businessDataVo.getEntityName() + ":" + businessDataVo.getBusinessId())
                    .description(applyName + "-" + processName)
                    .dataSourceId(businessDataVo.getDataSourceId())
                    .version(businessDataVo.getBpmnCode())
                    .tenantId(MultiTenantUtil.getCurrentTenantId())
                    .approvalUsers(JSON.toJSONString(businessDataVo.getApprovalEmpls()))
                    .build());
            //the process number is predictable
            businessDataVo.setProcessNumber(businessDataVo.getFormCode() + "_" + businessDataVo.getBusinessId());
        }
        bpmnConfCommonService.startProcess(businessDataVo.getBpmnCode(), bpmnStartConditionsVo);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_SUBMIT);
    }
}
