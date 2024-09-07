package org.openoa.common.service;

import org.activiti.engine.impl.identity.Authentication;
import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.constant.enums.ProcessTypeEnum;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.util.AntCollectionUtil;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.entity.ThirdPartyAccountApply;
import org.openoa.mapper.ThirdPartyAccountApplyMapper;
import org.openoa.base.interf.FormOperationAdaptor;
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
public class TestFormService implements FormOperationAdaptor<ThirdPartyAccountApplyVo>, ActivitiService {
    @Autowired
    private ThirdPartyAccountApplyMapper thirdPartyAccountApplyMapper;
    @Override
    public BpmnStartConditionsVo previewSetCondition(ThirdPartyAccountApplyVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId)
                .jobLevelVo(vo.getJobLevelVo())
                .accountType(vo.getAccountType()).build();
    }

    @Override
    public ThirdPartyAccountApplyVo initData(ThirdPartyAccountApplyVo vo) {
        return null;
    }

    @Override
    public BpmnStartConditionsVo launchParameters(ThirdPartyAccountApplyVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId)
                .accountType(vo.getAccountType())
                .jobLevelVo(vo.getJobLevelVo())
                .build();
    }

    @Override
    public ThirdPartyAccountApplyVo queryData(Long businessId) {
        ThirdPartyAccountApply accountApply = thirdPartyAccountApplyMapper.selectById(businessId);
        ThirdPartyAccountApplyVo vo=new ThirdPartyAccountApplyVo();
        BeanUtils.copyProperties(accountApply,vo);
        return vo;
    }

    @Override
    public ThirdPartyAccountApplyVo submitData(ThirdPartyAccountApplyVo vo) {
        ThirdPartyAccountApply thirdPartyAccountApply=new ThirdPartyAccountApply();
        BeanUtils.copyProperties(vo,thirdPartyAccountApply);
        thirdPartyAccountApplyMapper.insert(thirdPartyAccountApply);
        vo.setBusinessId(thirdPartyAccountApply.getId().longValue());
        vo.setProcessTitle("第三方账号申请");
        vo.setProcessDigest(vo.getRemark());
        vo.setEntityName(ThirdPartyAccountApply.class.getSimpleName());
        return vo;
    }

    @Override
    public ThirdPartyAccountApplyVo consentData(ThirdPartyAccountApplyVo vo) {
        if (vo.getOperationType().equals(ButtonTypeEnum.BUTTON_TYPE_RESUBMIT.getCode())){
            ThirdPartyAccountApply thirdPartyAccountApply=new ThirdPartyAccountApply();
            BeanUtils.copyProperties(vo,thirdPartyAccountApply);
            Integer id=  Integer.valueOf((vo.getBusinessId()).toString());
            thirdPartyAccountApply.setId(id);
            thirdPartyAccountApplyMapper.updateById(thirdPartyAccountApply);
        }
        return vo;
    }

    @Override
    public void backToModifyData(ThirdPartyAccountApplyVo vo) {

    }

    @Override
    public void cancellationData(Long businessId) {

    }

    @Override
    public void finishData(Long businessId) {

    }
}
