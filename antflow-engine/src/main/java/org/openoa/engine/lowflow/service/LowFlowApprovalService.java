package org.openoa.engine.lowflow.service;

import org.openoa.base.constant.StringConstants;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.lowflow.vo.UDLFApplyVo;

@ActivitiServiceAnno(svcName = StringConstants.LOWFLOW_FORM_CODE,desc = "拖拽表单低代码审批流")
public class LowFlowApprovalService implements FormOperationAdaptor<UDLFApplyVo>, ActivitiService {
    @Override
    public BpmnStartConditionsVo previewSetCondition(UDLFApplyVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId)
                .build();
    }

    @Override
    public UDLFApplyVo initData(UDLFApplyVo vo) {
        return null;
    }

    @Override
    public BpmnStartConditionsVo launchParameters(UDLFApplyVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId)
                .build();
    }

    @Override
    public UDLFApplyVo queryData(String businessId) {
        return null;
    }

    @Override
    public UDLFApplyVo submitData(UDLFApplyVo vo) {
        return null;
    }

    @Override
    public UDLFApplyVo consentData(UDLFApplyVo vo) {
        return null;
    }

    @Override
    public void backToModifyData(UDLFApplyVo vo) {

    }

    @Override
    public void cancellationData(String businessId) {

    }

    @Override
    public void finishData(String businessId) {

    }
}
