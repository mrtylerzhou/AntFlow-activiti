package org.openoa.engine.bpmnconf.adp.processoperation;


import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.exception.JiMuBizException;

import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmProcessName;

import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmProcessNameServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnConfCommonServiceImpl;
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
    private BpmnConfCommonServiceImpl bpmnConfCommonService;
    @Autowired
    protected BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Autowired
    private BpmProcessNameServiceImpl bpmProcessNameService;

    @Override
    public void doProcessButton(BusinessDataVo businessDataVo) {
        log.info("Start submit process. param:{}", businessDataVo);
        FormOperationAdaptor formAdapter = formFactory.getFormAdaptor(businessDataVo);

        BusinessDataVo vo = businessDataVo;
        if(!Boolean.TRUE.equals(businessDataVo.getIsMigration())){
            vo=formAdapter.submitData(businessDataVo);
        }
        // call the process's launch method to get launch parameters
        BpmnStartConditionsVo bpmnStartConditionsVo = formAdapter.launchParameters(vo);
        bpmnStartConditionsVo.setBusinessDataVo(vo);
        bpmnStartConditionsVo.setApproversList(Optional.ofNullable(businessDataVo.getApproversList()).orElse(Maps.newHashMap()));
        String processNumber=businessDataVo.getFormCode() + "_" + vo.getBusinessId();
        if(Boolean.TRUE.equals(businessDataVo.getIsMigration())){
            processNumber=businessDataVo.getProcessNumber();
        }
        if(StringUtils.isEmpty(vo.getProcessNumber())){
            vo.setProcessNumber(processNumber);
        }
        bpmnStartConditionsVo.setProcessNum(processNumber);
        bpmnStartConditionsVo.setEntryId(vo.getEntityName() + ":" + vo.getBusinessId());
        bpmnStartConditionsVo.setBusinessId(vo.getBusinessId());
        if(Boolean.TRUE.equals(businessDataVo.getIsMigration())){
            bpmnStartConditionsVo.setIsMigration(vo.getIsMigration());
        }else{
            String entryId = vo.getEntityName() + ":" + vo.getBusinessId();
            if (!bpmBusinessProcessService.checkProcessData(entryId)) {
                throw new JiMuBizException("the process has already been submitted！");
            }
        }

        //process's name
        String processName = Optional
                .ofNullable(bpmProcessNameService.getBpmProcessName(businessDataVo.getFormCode()))
                .orElse(new BpmProcessName()).getProcessName();
        //apply user info
        String applyName = SecurityUtils.getLogInEmpName();
        //save business and process information
        if(!Boolean.TRUE.equals(businessDataVo.getIsMigration())){
            bpmBusinessProcessService.addBusinessProcess(BpmBusinessProcess.builder()
                    .businessId(vo.getBusinessId())
                    .processinessKey(businessDataVo.getFormCode())
                    .businessNumber(processNumber)
                    .isLowCodeFlow(vo.getIsLowCodeFlow())
                    .createUser(businessDataVo.getStartUserId())
                    .userName(businessDataVo.getStartUserName())
                    .createTime(new Date())
                    .processDigest(vo.getProcessDigest())
                    .processState(ProcessStateEnum.HANDLING_STATE.getCode())
                    .entryId(vo.getEntityName() + ":" + vo.getBusinessId())
                    .description(applyName + "-" + processName)
                    .dataSourceId(vo.getDataSourceId())
                    .version(businessDataVo.getBpmnCode())
                    .tenantId(MultiTenantUtil.getCurrentTenantId())
                    .build());
            //the process number is predictable
            businessDataVo.setProcessNumber(businessDataVo.getFormCode() + "_" + vo.getBusinessId());
        }
        bpmnConfCommonService.startProcess(businessDataVo.getBpmnCode(), bpmnStartConditionsVo);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_SUBMIT);
    }
}
