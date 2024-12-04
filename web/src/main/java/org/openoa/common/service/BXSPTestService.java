package org.openoa.common.service;

import org.apache.commons.lang3.RandomUtils;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.ThirdPartyAccountApplyVo;

/**
 * @Classname BXSPTestService
 * @Date 2022-02-20 11:14
 * @Created by AntOffice
 */
@ActivitiServiceAnno(svcName = "BXSP_WMA",desc = "报销审批测试")
//formAdaptor
public class BXSPTestService implements FormOperationAdaptor<ThirdPartyAccountApplyVo>, ActivitiService {

    @Override
    public BpmnStartConditionsVo previewSetCondition(ThirdPartyAccountApplyVo vo) {
        return BpmnStartConditionsVo.builder().build();
    }

    @Override
    public ThirdPartyAccountApplyVo initData(ThirdPartyAccountApplyVo vo) {
        return new ThirdPartyAccountApplyVo();
    }

    @Override
    public BpmnStartConditionsVo launchParameters(ThirdPartyAccountApplyVo vo) {
        return BpmnStartConditionsVo.builder().startUserId("1").build();
    }

    @Override
    public ThirdPartyAccountApplyVo queryData(ThirdPartyAccountApplyVo vo) {
        return new ThirdPartyAccountApplyVo();
    }

    @Override
    public ThirdPartyAccountApplyVo submitData(ThirdPartyAccountApplyVo vo) {
        ThirdPartyAccountApplyVo thirdPartyAccountApplyVo = new ThirdPartyAccountApplyVo();
        thirdPartyAccountApplyVo.setBusinessId(String.valueOf(RandomUtils.nextLong()));
        return thirdPartyAccountApplyVo;
    }

    @Override
    public ThirdPartyAccountApplyVo consentData(ThirdPartyAccountApplyVo vo) {
        return vo;
    }

    @Override
    public void backToModifyData(ThirdPartyAccountApplyVo vo) {

    }

    @Override
    public void cancellationData(ThirdPartyAccountApplyVo vo) {

    }

    @Override
    public void finishData(BusinessDataVo vo) {

    }
}
