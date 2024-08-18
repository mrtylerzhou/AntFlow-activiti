package org.openoa.common.service;

import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;

/**
 * @Classname AskForLeaveTestService
 * @Date 2022-02-20 11:14
 * @Created by AntOffice
 */
@ActivitiServiceAnno(svcName = "LEAVE_WMA",desc = "请假申请")
//formAdaptor
public class AskForLeaveTestService implements FormOperationAdaptor, ActivitiService {

    @Override
    public BpmnStartConditionsVo previewSetCondition(BusinessDataVo vo) {
        return BpmnStartConditionsVo.builder().build();
    }

    @Override
    public BusinessDataVo initData(BusinessDataVo vo) {
        return null;
    }

    @Override
    public BpmnStartConditionsVo launchParameters(BusinessDataVo vo) {
        return BpmnStartConditionsVo.builder().build();
    }

    @Override
    public BusinessDataVo queryData(Long businessId) {
        return new BusinessDataVo();
    }

    @Override
    public BusinessDataVo submitData(BusinessDataVo vo) {
        return new BusinessDataVo();
    }

    @Override
    public BusinessDataVo consentData(BusinessDataVo vo) {
        return new BusinessDataVo();
    }

    @Override
    public void backToModifyData(BusinessDataVo vo) {

    }

    @Override
    public void cancellationData(Long businessId) {

    }

    @Override
    public void finishData(Long businessId) {

    }
}
