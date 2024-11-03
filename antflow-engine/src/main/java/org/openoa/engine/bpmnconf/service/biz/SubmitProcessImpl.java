package org.openoa.engine.bpmnconf.service.biz;


import lombok.extern.slf4j.Slf4j;
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
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

        BusinessDataVo vo = formAdapter.submitData(businessDataVo);
        // call the process's launch method to get launch parameters
        BpmnStartConditionsVo bpmnStartConditionsVo = formAdapter.launchParameters(vo);
        bpmnStartConditionsVo.setApproversList(Optional.ofNullable(businessDataVo.getApproversList()).orElse(Arrays.asList()));
        bpmnStartConditionsVo.setProcessNum(businessDataVo.getFormCode() + "_" + vo.getBusinessId());
        bpmnStartConditionsVo.setEntryId(vo.getEntityName() + ":" + vo.getBusinessId());
        bpmnStartConditionsVo.setBusinessId(vo.getBusinessId());
        String entryId = vo.getEntityName() + ":" + vo.getBusinessId();
        if (!bpmBusinessProcessService.checkProcessData(entryId)) {
            throw new JiMuBizException("the process has already been submitted！");
        }
        //process's name
        String processName = Optional
                .ofNullable(bpmProcessNameService.getBpmProcessName(businessDataVo.getFormCode()))
                .orElse(new BpmProcessName()).getProcessName();
        //apply user info
        String applyName = SecurityUtils.getLogInEmpName();
        //save business and process information
        bpmBusinessProcessService.addBusinessProcess(BpmBusinessProcess.builder()
                .businessId(vo.getBusinessId())
                .processinessKey(businessDataVo.getFormCode())
                .businessNumber(businessDataVo.getFormCode() + "_" + vo.getBusinessId())
                .createUser(businessDataVo.getStartUserId())
                .userName(businessDataVo.getStartUserName())
                .createTime(new Date())
                .processDigest(vo.getProcessDigest())
                .processState(ProcessStateEnum.COMLETE_STATE.getCode())
                .entryId(vo.getEntityName() + ":" + vo.getBusinessId())
                .description(applyName + "-" + processName)
                .dataSourceId(vo.getDataSourceId())
                .version(businessDataVo.getBpmnCode())
                .build());
        //the process number is predictable
        businessDataVo.setProcessNumber(businessDataVo.getFormCode() + "_" + vo.getBusinessId());
        bpmnConfCommonService.startProcess(businessDataVo.getBpmnCode(), bpmnStartConditionsVo);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_SUBMIT);
    }
}
