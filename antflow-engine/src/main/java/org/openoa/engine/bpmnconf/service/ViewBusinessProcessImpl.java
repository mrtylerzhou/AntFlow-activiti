package org.openoa.engine.bpmnconf.service;

import org.openoa.base.constant.enums.ButtonPageTypeEnum;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.ProcessActionButtonVo;
import org.openoa.engine.bpmnconf.common.ConfigFlowButtonContans;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableSignUpBizService;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

import static org.openoa.base.constant.enums.ProcessOperationEnum.BUTTON_TYPE_JP;
import static org.openoa.base.constant.enums.ProcessOperationEnum.BUTTON_TYPE_VIEW_BUSINESS_PROCESS;
import static org.openoa.base.constant.enums.ProcessStateEnum.END_STATE;
import static org.openoa.base.constant.enums.ProcessStateEnum.REJECT_STATE;

@Service
public class ViewBusinessProcessImpl  implements ProcessOperationAdaptor {
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private FormFactory formFactory;
    @Autowired
    private ProcessBusinessContans businessContans;
    @Autowired
    private ConfigFlowButtonContans configFlowButtonContans;
    @Autowired
    private BpmVariableSignUpBizService variableSignUpBizService;

    @Override
    public void doProcessButton(BusinessDataVo businessDataVo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(businessDataVo.getProcessNumber());
        if(ObjectUtils.isEmpty(bpmBusinessProcess)){
            throw  new AFBizException(String.format("processNumber%s,its data not in existence!",businessDataVo.getProcessNumber()));
        }
        businessDataVo.setBusinessId(bpmBusinessProcess.getBusinessId());
        businessDataVo =formFactory.getFormAdaptor(businessDataVo).queryData(businessDataVo);

        //set the businessId
        businessDataVo.setBusinessId(bpmBusinessProcess.getBusinessId());



        // checking process right,and set some information that from business table
        businessDataVo.setProcessRecordInfo(businessContans.processInfo(bpmBusinessProcess));
        businessDataVo.setProcessKey(bpmBusinessProcess.getBusinessNumber());
        businessDataVo.setProcessState(!bpmBusinessProcess.getProcessState().equals(END_STATE.getCode()) && !bpmBusinessProcess.getProcessState().equals(REJECT_STATE.getCode()));

        boolean flag = businessDataVo.getProcessRecordInfo().getStartUserId().equals(SecurityUtils.getLogInEmpIdStr());

        boolean isJurisdiction=false;//todo not implemented at the moment
        // set operating buttons

        businessDataVo.getProcessRecordInfo().setPcButtons(configFlowButtonContans.getButtons(bpmBusinessProcess.getBusinessNumber(),
                businessDataVo.getProcessRecordInfo().getNodeId(), isJurisdiction, flag));


        //check whether current node is a signup node and set the property
        String nodeId = businessDataVo.getProcessRecordInfo().getNodeId();
        Boolean nodeIsSignUp = variableSignUpBizService.checkNodeIsSignUp(businessDataVo.getProcessNumber(), nodeId);
        businessDataVo.setIsSignUpNode(nodeIsSignUp);
        //add a "choose a verifier" button if it is a signup node
        if (nodeIsSignUp) {
            //set the add approver button
            addApproverButton(businessDataVo);
        }

    }

    /**
     * set the add approver button
     *
     * @param businessDataVo
     */
    private void addApproverButton(BusinessDataVo businessDataVo) {
        //set the approver button
        ProcessActionButtonVo addApproverButton = ProcessActionButtonVo
                .builder()
                .buttonType(BUTTON_TYPE_JP.getCode())
                .name(BUTTON_TYPE_JP.getDesc())
                .build();

        //set add approver button on the pc
        Map<String, List<ProcessActionButtonVo>> pcButtons = businessDataVo.getProcessRecordInfo().getPcButtons();
        List<ProcessActionButtonVo> pcProcButtons = pcButtons.get(ButtonPageTypeEnum.AUDIT.getName());
        if (!pcProcButtons.stream().anyMatch(a->BUTTON_TYPE_JP.getCode().equals(a.getButtonType()))) {
            pcProcButtons.add(addApproverButton);
        }
        businessDataVo.getProcessRecordInfo().setPcButtons(pcButtons);
    }
    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(
                BUTTON_TYPE_VIEW_BUSINESS_PROCESS
        );
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(),  BUTTON_TYPE_VIEW_BUSINESS_PROCESS);
    }
}
