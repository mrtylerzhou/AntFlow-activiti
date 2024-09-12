package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ApprovalFormCodeEnum;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.Employee;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.confentity.Department;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmAccessBusiness;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmConditionsTemplate;
import org.openoa.engine.bpmnconf.service.impl.DepartmentServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.EmployeeServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmAccessBusinessServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmConditionsTemplateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.Optional;

/**
 * third party process submit
 */
@Slf4j
@Component
public class OutSideAccessSubmitProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private BpmnConfCommonServiceImpl bpmnConfCommonService;

    @Autowired
    protected BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Autowired
    private EmployeeServiceImpl employeeService;

    @Autowired
    private DepartmentServiceImpl departmentService;

    @Autowired
    private OutSideBpmAccessBusinessServiceImpl outSideBpmAccessBusinessService;

    @Autowired
    private OutSideBpmConditionsTemplateServiceImpl outSideBpmConditionsTemplateService;

    @Override
    public void doProcessButton(BusinessDataVo businessDataVo) {


        //generate process number by rule
        String processNum = StringUtils.join(businessDataVo.getFormCode(), "_", businessDataVo.getBusinessId());


        //to check whether the process is already started
        if (!bpmBusinessProcessService.checkProcessData(processNum)) {
            throw new JiMuBizException("流程已发起！");
        }


        //query outside access business info
        OutSideBpmAccessBusiness outSideBpmAccessBusiness = outSideBpmAccessBusinessService.getById(businessDataVo.getBusinessId());
        //new start conditions vo
        BpmnStartConditionsVo bpmnStartConditionsVo = new BpmnStartConditionsVo();
        String templateMark=outSideBpmAccessBusiness.getTemplateMark();
        if(!StringUtils.isEmpty(templateMark)){
            //query template mark
            OutSideBpmConditionsTemplate outSideBpmConditionsTemplate = outSideBpmConditionsTemplateService.getOne(new QueryWrapper<OutSideBpmConditionsTemplate>()
                    .eq("is_del", 0)
                    .eq("business_party_id", outSideBpmAccessBusiness.getBusinessPartyId())
                    .eq("template_mark", outSideBpmAccessBusiness.getTemplateMark()));

            if (outSideBpmConditionsTemplate==null) {
                throw new JiMuBizException("条件模板[" + outSideBpmAccessBusiness.getTemplateMark() + "]已经失效，无法发起流程");
            }
            bpmnStartConditionsVo.setTemplateMarkId(outSideBpmConditionsTemplate.getId().intValue());
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
        bpmnStartConditionsVo.setApproversList(Optional.ofNullable(businessDataVo.getApproversList()).orElse(Lists.newArrayList()));

        //set process number
        bpmnStartConditionsVo.setProcessNum(processNum);

        //set entry id
        bpmnStartConditionsVo.setEntryId(processNum);


        bpmnStartConditionsVo.setEmbedNodes(businessDataVo.getEmbedNodes());

        bpmnStartConditionsVo.setOutSideLevelNodes(businessDataVo.getOutSideLevelNodes());

        bpmnStartConditionsVo.setIsOutSideAccessProc(true);

        //set process title
        String processTitlePrefix;
        if (ApprovalFormCodeEnum.exist(businessDataVo.getFormCode())) {
            // 被审批人
            processTitlePrefix = Optional
                    .ofNullable(employeeService.getEmployeeDetailById(bpmnStartConditionsVo.getApprovalEmplId()))
                    .orElse(new Employee()).getUsername();
        } else {
            //start user
            processTitlePrefix = Optional
                    .ofNullable(employeeService.getEmployeeDetailById(businessDataVo.getStartUserId()))
                    .orElse(new Employee()).getUsername();
        }


        //save business process info
        bpmBusinessProcessService.addBusinessProcess(BpmBusinessProcess.builder()
                .businessId(businessDataVo.getBusinessId())
                .processinessKey(businessDataVo.getFormCode())
                .businessNumber(processNum)
                .createUser(businessDataVo.getStartUserId())
                .createTime(new Date())
                .processState(ProcessStateEnum.COMLETE_STATE.getCode())
                .entryId(processNum)
                .description(processTitlePrefix + "-" + businessDataVo.getBpmnName())
                //.approvalUserId(businessDataVo.getEmplId())
                .build());
        businessDataVo.setProcessNumber(processNum);


        //start process
        bpmnConfCommonService.startProcess(businessDataVo.getBpmnCode(), bpmnStartConditionsVo);

        //fill info
        outSideBpmAccessBusinessService.updateById(OutSideBpmAccessBusiness
                .builder()
                .id(Long.parseLong(businessDataVo.getBusinessId()))
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
