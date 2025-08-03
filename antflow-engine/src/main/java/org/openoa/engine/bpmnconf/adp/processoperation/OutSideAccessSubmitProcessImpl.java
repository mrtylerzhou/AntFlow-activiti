package org.openoa.engine.bpmnconf.adp.processoperation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ApprovalFormCodeEnum;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.service.AfUserService;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.entity.OutSideBpmAccessBusiness;
import org.openoa.base.entity.OutSideBpmConditionsTemplate;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnConfBizServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.DepartmentServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmAccessBusinessServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmConditionsTemplateServiceImpl;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * third party process submit
 */
@Slf4j
@Component
public class OutSideAccessSubmitProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private BpmnConfBizServiceImpl bpmnConfCommonService;

    @Autowired
    protected BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Autowired
    private AfUserService employeeService;

    @Autowired
    private DepartmentServiceImpl departmentService;

    @Autowired
    private OutSideBpmAccessBusinessServiceImpl outSideBpmAccessBusinessService;

    @Autowired
    private OutSideBpmConditionsTemplateServiceImpl outSideBpmConditionsTemplateService;
    @Autowired
    private FormFactory formFactory;

    @Override
    public void doProcessButton(BusinessDataVo businessDataVo) {


        //generate process number by rule
        String processNum = StringUtils.join(businessDataVo.getFormCode(), "_", businessDataVo.getBusinessId());


        //to check whether the process is already started
        if (!bpmBusinessProcessService.checkProcessData(processNum)) {
            throw new AFBizException("流程已发起！");
        }
        String originalBusinessId=businessDataVo.getBusinessId();
        BpmnStartConditionsVo bpmnStartConditionsVo = new BpmnStartConditionsVo();
        if(Objects.equals(businessDataVo.getIsLowCodeFlow(),1)){
            FormOperationAdaptor formAdapter = formFactory.getFormAdaptor(businessDataVo);
            bpmnStartConditionsVo=formAdapter.launchParameters(businessDataVo);
            formAdapter.submitData(businessDataVo);

        }

        //query outside access business info
        OutSideBpmAccessBusiness outSideBpmAccessBusiness = outSideBpmAccessBusinessService.getById(originalBusinessId);
        //new start conditions vo

        //调整以后,这里存储的实际上是逗号分割的字符串
        String templateMark=outSideBpmAccessBusiness.getTemplateMark();
        if(!StringUtils.isEmpty(templateMark)){
            String[] templateMarkList = templateMark.split(",");
            //query template mark
            List<OutSideBpmConditionsTemplate> outSideBpmConditionsTemplate = outSideBpmConditionsTemplateService.list(new QueryWrapper<OutSideBpmConditionsTemplate>()
                    .eq("is_del", 0)
                    .eq("business_party_id", outSideBpmAccessBusiness.getBusinessPartyId())
                    .in("template_mark", outSideBpmAccessBusiness.getTemplateMark()));

            if (outSideBpmConditionsTemplate==null) {
                throw new AFBizException("条件模板[" + outSideBpmAccessBusiness.getTemplateMark() + "]已经失效，无法发起流程");
            }
            List<Integer> templateMarkIds = outSideBpmConditionsTemplate.stream().map(a -> a.getId().intValue()).collect(Collectors.toList());
            bpmnStartConditionsVo.setTemplateMarkIds(templateMarkIds);
        }

        bpmnStartConditionsVo.setOutSideType(businessDataVo.getOutSideType());


        bpmnStartConditionsVo.setBusinessId(businessDataVo.getBusinessId());


        bpmnStartConditionsVo.setStartUserId(businessDataVo.getStartUserId());

        //bpmnStartConditionsVo.setApprovalEmplId(Long.parseLong(businessDataVo.getEmplId()));

        //set start user dept id
//        Department department = departmentService.getDepartmentByEmployeeId(businessDataVo.getStartUserId());
//        if (department!=null && department.getId()!=null) {
//            bpmnStartConditionsVo.setStartUserDeptId(department.getId().longValue());
//        }

        //set approvers list
        bpmnStartConditionsVo.setApproversList(Optional.ofNullable(businessDataVo.getApproversList()).orElse(Maps.newHashMap()));

        //set process number
        bpmnStartConditionsVo.setProcessNum(processNum);

        //set entry id
        bpmnStartConditionsVo.setEntryId(processNum);


        bpmnStartConditionsVo.setEmbedNodes(businessDataVo.getEmbedNodes());

        bpmnStartConditionsVo.setOutSideLevelNodes(businessDataVo.getOutSideLevelNodes());

        bpmnStartConditionsVo.setIsOutSideAccessProc(true);
        bpmnStartConditionsVo.setBusinessDataVo(businessDataVo);

        //set process title
        String processTitlePrefix;
        if (ApprovalFormCodeEnum.exist(businessDataVo.getFormCode())) {
            // 被审批人
            processTitlePrefix = Optional
                    .ofNullable(employeeService.getById(bpmnStartConditionsVo.getApprovalEmplId()))
                    .orElse(new BaseIdTranStruVo()).getName();
        } else {
            //start user
            processTitlePrefix = businessDataVo.getSubmitUser();
        }

        BpmBusinessProcess bpmBusinessProcess = BpmBusinessProcess.builder()
                .businessId(businessDataVo.getBusinessId())
                .processinessKey(businessDataVo.getFormCode())
                .businessNumber(processNum)
                .createUser(businessDataVo.getStartUserId())
                .userName(businessDataVo.getSubmitUser())
                .createTime(new Date())
                .processState(ProcessStateEnum.HANDLING_STATE.getCode())
                .entryId(processNum)
                .description(processTitlePrefix + "-" + businessDataVo.getBpmnName())
                .version(businessDataVo.getBpmnCode())
                .isOutSideProcess(1)
                .isLowCodeFlow(businessDataVo.getIsLowCodeFlow())
                //.approvalUserId(businessDataVo.getEmplId())
                .build();
        //save business process info
        bpmBusinessProcessService.addBusinessProcess(bpmBusinessProcess);
        businessDataVo.setProcessNumber(processNum);


        //start process
        bpmnConfCommonService.startProcess(businessDataVo.getBpmnCode(), bpmnStartConditionsVo);

        //fill info
        outSideBpmAccessBusinessService.updateById(OutSideBpmAccessBusiness
                .builder()
                .id(Long.parseLong(originalBusinessId))
                .processNumber(processNum)
                .bpmnConfId(Optional.ofNullable(businessDataVo.getBpmnConfVo()).orElse(new BpmnConfVo()).getId())
                .build());

        //call back to business party
    /*    CallbackFactory.build().doCallback(businessDataVo.getBpmFlowCallbackUrl(), PROC_STARTED_CALL_BACK,
                businessDataVo.getBpmnConfVo(), processNum, businessDataVo.getBusinessId().toString());*/
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(),ProcessOperationEnum.BUTTON_TYPE_SUBMIT);
    }
}
