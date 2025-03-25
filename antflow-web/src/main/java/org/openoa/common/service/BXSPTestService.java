package org.openoa.common.service;

import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.entity.BizRefund;
import org.openoa.mapper.BizRefundMapper;
import org.openoa.vo.BizRefundVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @Classname BXSPTestService
 * @Date 2022-02-20 11:14
 * @Created by AntOffice
 */
@ActivitiServiceAnno(svcName = "BXSP_WMA",desc = "报销审批测试")
//formAdaptor
public class BXSPTestService implements FormOperationAdaptor<BizRefundVo>, ActivitiService {

    @Autowired
    private BizRefundMapper bizRefundMapper;

    @Override
    public BpmnStartConditionsVo previewSetCondition(BizRefundVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId).build();
    }

    @Override
    public BizRefundVo initData(BizRefundVo vo) {
        return null;
    }

    @Override
    public BpmnStartConditionsVo launchParameters(BizRefundVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId).build();
    }

    @Override
    public BizRefundVo queryData(BizRefundVo vo) {
        BizRefund refund = bizRefundMapper.selectById(vo.getBusinessId());
        BeanUtils.copyProperties(refund,vo);
        return vo;
    }

    @Override
    public BizRefundVo submitData(BizRefundVo vo) {
        BizRefund entity=new BizRefund();
        BeanUtils.copyProperties(vo,entity);
        entity.setCreateTime(new Date());
        entity.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
        entity.setRefundUserId(Integer.parseInt(vo.getStartUserId()));
        entity.setRefundUserName(SecurityUtils.getLogInEmpNameSafe());

        bizRefundMapper.insert(entity);
        vo.setBusinessId(entity.getId().toString());
        vo.setProcessTitle("报销申请");
        vo.setProcessDigest(vo.getRemark());
        vo.setEntityName(BizRefund.class.getSimpleName());
        return vo;
    }

    @Override
    public BizRefundVo consentData(BizRefundVo vo) {
        if (vo.getOperationType().equals(ButtonTypeEnum.BUTTON_TYPE_RESUBMIT.getCode())){
            BizRefund entity = new BizRefund();
            BeanUtils.copyProperties(vo,entity);
            Integer id=  Integer.valueOf((vo.getBusinessId()).toString());
            entity.setId(id);
            bizRefundMapper.updateById(entity);
        }
        return vo;
    }

    @Override
    public void backToModifyData(BizRefundVo vo) {

    }

    @Override
    public void cancellationData(BizRefundVo vo) {

    }

    @Override
    public void finishData(BusinessDataVo vo) {

    }
}
