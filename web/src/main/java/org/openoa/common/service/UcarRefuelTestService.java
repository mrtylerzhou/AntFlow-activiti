package org.openoa.common.service;

import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.entity.BizUcarfuel;
import org.openoa.mapper.BizUcarFuelMapper;
import org.openoa.vo.BizUcarRefuelVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @Classname AskForLeaveTestService
 * @Date 2024-08-26 11:14
 * @Created by AntOffice
 */
@ActivitiServiceAnno(svcName = "UCARREFUEl_WMA",desc = "加油上报")
//formAdaptor
public class UcarRefuelTestService implements FormOperationAdaptor<BizUcarRefuelVo>, ActivitiService {

    @Autowired
    private BizUcarFuelMapper bizUcarfuelMapper;

    @Override
    public BpmnStartConditionsVo previewSetCondition(BizUcarRefuelVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId).build();
    }

    @Override
    public BizUcarRefuelVo initData(BizUcarRefuelVo vo) {
        return null;
    }


    @Override
    public BpmnStartConditionsVo launchParameters(BizUcarRefuelVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId).build();
    }

    @Override
    public BizUcarRefuelVo queryData(String businessId) {
        BizUcarfuel ucarFuel = bizUcarfuelMapper.selectById(businessId);
        BizUcarRefuelVo vo=new BizUcarRefuelVo();
        BeanUtils.copyProperties(ucarFuel,vo);
        return vo;
    }

    @Override
    public BizUcarRefuelVo submitData(BizUcarRefuelVo vo) {
        BizUcarfuel ucarFuel=new BizUcarfuel();
        BeanUtils.copyProperties(vo,ucarFuel);

        ucarFuel.setCreateTime(new Date());
        ucarFuel.setCreateUser(SecurityUtils.getLogInEmpNameSafe());

        bizUcarfuelMapper.insert(ucarFuel);
        vo.setBusinessId(ucarFuel.getId().toString());
        vo.setProcessTitle("加油上报");
        vo.setProcessDigest(vo.getRemark());
        vo.setEntityName(BizUcarfuel.class.getSimpleName());
        return vo;
    }

    @Override
    public BizUcarRefuelVo consentData(BizUcarRefuelVo vo) {
        if (vo.getOperationType().equals(ButtonTypeEnum.BUTTON_TYPE_RESUBMIT.getCode())){
            BizUcarfuel ucarFuel = new BizUcarfuel();
            BeanUtils.copyProperties(vo,ucarFuel);
            Integer id=  Integer.valueOf((vo.getBusinessId()).toString());
            ucarFuel.setId(id);
            bizUcarfuelMapper.updateById(ucarFuel);
        }
        return vo;
    }

    @Override
    public void backToModifyData(BizUcarRefuelVo vo) {

    }

    @Override
    public void cancellationData(String businessId) {

    }

    @Override
    public void finishData(String businessId) {

    }
}
