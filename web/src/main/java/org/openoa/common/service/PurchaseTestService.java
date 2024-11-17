package org.openoa.common.service;

import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.entity.BizPurchase;
import org.openoa.mapper.BizPurchaseMapper;
import org.openoa.vo.BizPurchaseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @Classname AskForLeaveTestService
 * @Date 2024-10-17 20:17
 * @Created by AntOffice
 */
@ActivitiServiceAnno(svcName = "PURCHASE_WMA",desc = "采购申请流程")
//formAdaptor
public class PurchaseTestService implements FormOperationAdaptor<BizPurchaseVo>, ActivitiService {

    @Autowired
    private BizPurchaseMapper bizPurchaseMapper;

    @Override
    public BpmnStartConditionsVo previewSetCondition(BizPurchaseVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId)
                .purchaseType(vo.getPurchaseType())
                .planProcurementTotalMoney(vo.getPlanProcurementTotalMoney()).build();
    }

    @Override
    public BizPurchaseVo initData(BizPurchaseVo vo) {
        return null;
    }


    @Override
    public BpmnStartConditionsVo launchParameters(BizPurchaseVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId)
                .purchaseType(vo.getPurchaseType())
                .planProcurementTotalMoney(vo.getPlanProcurementTotalMoney()).build();
    }

    @Override
    public BizPurchaseVo queryData(String businessId) {
        BizPurchase purchase = bizPurchaseMapper.selectById(businessId);
        BizPurchaseVo vo=new BizPurchaseVo();
        BeanUtils.copyProperties(purchase,vo);
        return vo;
    }

    @Override
    public BizPurchaseVo submitData(BizPurchaseVo vo) {
        BizPurchase purchaseEntity=new BizPurchase();
        BeanUtils.copyProperties(vo,purchaseEntity);

        purchaseEntity.setCreateTime(new Date());
        purchaseEntity.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
        purchaseEntity.setPurchaseUserId(Integer.parseInt(vo.getStartUserId()));
        purchaseEntity.setPurchaseUserName(SecurityUtils.getLogInEmpNameSafe());

        bizPurchaseMapper.insert(purchaseEntity);
        vo.setBusinessId(purchaseEntity.getId().toString());
        vo.setProcessTitle("采购申请");
        vo.setProcessDigest(vo.getRemark());
        vo.setEntityName(BizPurchase.class.getSimpleName());
        return vo;
    }

    @Override
    public BizPurchaseVo consentData(BizPurchaseVo vo) {
        if (vo.getOperationType().equals(ButtonTypeEnum.BUTTON_TYPE_RESUBMIT.getCode())){
            BizPurchase purchaseEntity = new BizPurchase();
            BeanUtils.copyProperties(vo,purchaseEntity);
            Integer id=  Integer.valueOf((vo.getBusinessId()).toString());
            purchaseEntity.setId(id);
            bizPurchaseMapper.updateById(purchaseEntity);
        }
        return vo;
    }

    @Override
    public void backToModifyData(BizPurchaseVo vo) {

    }

    @Override
    public void cancellationData(BizPurchaseVo vo) {

    }

    @Override
    public void finishData(BusinessDataVo vo) {

    }
}
