package org.openoa.common.service;

import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.adp.processoperation.AbstractLowFlowSpyFormOperationAdaptor;
import org.openoa.entity.ThirdPartyAccountApply;
import org.openoa.mapper.ThirdPartyAccountApplyMapper;
import org.openoa.base.vo.ThirdPartyAccountApplyVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Classname TestFormService
 * @Date 2022-02-20 11:14
 * @Created by AntOffice
 */
@ActivitiServiceAnno(svcName = "DSFZH_WMA",desc = "第三方账号申请")
//formAdaptor
public class TestFormService extends AbstractLowFlowSpyFormOperationAdaptor<ThirdPartyAccountApplyVo> {
    @Autowired
    private ThirdPartyAccountApplyMapper thirdPartyAccountApplyMapper;
    @Override
    public BpmnStartConditionsVo previewSetCondition(ThirdPartyAccountApplyVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId)
                .accountType(vo.getAccountType()).build();
    }

    @Override
    public void initData(ThirdPartyAccountApplyVo vo) {

    }

    @Override
    public BpmnStartConditionsVo launchParameters(ThirdPartyAccountApplyVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId)
                .accountType(vo.getAccountType())
                .build();
    }

    @Override
    public Boolean automaticCondition(ThirdPartyAccountApplyVo businessDataVo) {
        return null;
    }

    @Override
    public void automaticAction(ThirdPartyAccountApplyVo businessDataVo, Boolean conditionResult) {

    }

    @Override
    public void queryData(ThirdPartyAccountApplyVo vo) {
        ThirdPartyAccountApply accountApply = thirdPartyAccountApplyMapper.selectById(vo.getBusinessId());
        BeanUtils.copyProperties(accountApply,vo);
    }

    @Override
    public void submitData(ThirdPartyAccountApplyVo vo) {
        ThirdPartyAccountApply thirdPartyAccountApply=new ThirdPartyAccountApply();
        BeanUtils.copyProperties(vo,thirdPartyAccountApply);
        thirdPartyAccountApplyMapper.insert(thirdPartyAccountApply);
        vo.setBusinessId(thirdPartyAccountApply.getId().toString());
        vo.setProcessTitle("第三方账号申请");
        vo.setProcessDigest(vo.getRemark());
        vo.setEntityName(ThirdPartyAccountApply.class.getSimpleName());

    }

    @Override
    public void consentData(ThirdPartyAccountApplyVo vo) {
        if (vo.getOperationType().equals(ButtonTypeEnum.BUTTON_TYPE_RESUBMIT.getCode())
                && !vo.getOperationType().equals(ButtonTypeEnum.BUTTON_TYPE_AGREE.getCode()) ){
            ThirdPartyAccountApply thirdPartyAccountApply=new ThirdPartyAccountApply();
            BeanUtils.copyProperties(vo,thirdPartyAccountApply);
            Integer id=  Integer.valueOf((vo.getBusinessId()));
            thirdPartyAccountApply.setId(id);
            thirdPartyAccountApplyMapper.updateById(thirdPartyAccountApply);
        }

    }

    @Override
    public void backToModifyData(ThirdPartyAccountApplyVo vo) {

    }

    @Override
    public void cancellationData(ThirdPartyAccountApplyVo vo) {

    }

    @Override
    public void onProcessRecover(BusinessDataVo businessData) {

    }

    @Override
    public void finishData(BusinessDataVo vo) {

    }
}
