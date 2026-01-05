package org.openoa.common.service;

import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.ThirdPartyAccountApplyVo;
import org.openoa.engine.bpmnconf.adp.processoperation.AbstractFormOperationAdaptor;
import org.openoa.entity.BizLeaveTime;
import org.openoa.vo.BizLeaveTimeVo;
import org.openoa.mapper.BizLeaveTimeMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @Classname AskForLeaveTestService
 * @Date 2024-08-26 11:14
 * @Created by AntOffice
 */
@ActivitiServiceAnno(svcName = "LEAVE_WMA",desc = "请假申请")
//formAdaptor
public class AskForLeaveTestService extends AbstractFormOperationAdaptor<BizLeaveTimeVo> {

    @Autowired
    private BizLeaveTimeMapper bizLeaveTimeMapper;

    /**
     * 为预览条件设置，这不是必须的方法，但大多数时候预览流程都需要它
     * @param vo vo
     * @return
     */
    @Override
    public BpmnStartConditionsVo previewSetCondition(BizLeaveTimeVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId)
                .leaveHour(vo.getLeaveHour()).build();
    }
    /**
     * 为流程初始化一些数据，大多数时候这不是必须的方法
     * @param vo vo
     * @return
     */
    @Override
    public void initData(BizLeaveTimeVo vo) {

    }
    /**
     * 流程的启动参数，大多数时候这是一个必须的方法，即使你没有任何启动条件，你也应该始终定义一个新的空条件
     * @param vo vo
     * @return
     */
    @Override
    public BpmnStartConditionsVo launchParameters(BizLeaveTimeVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId)
                .leaveHour(vo.getLeaveHour()).build();
    }
    /**
     * 查询业务数据，大多数时候这是一个必须的方法，它用于查询指定流程的业务数据以供审批人参考
     * @param vo businessId 业务id，它将由antflow引擎传递，你应该使用它来查询业务数据
     * @return 业务数据
     */
    @Override
    public void queryData(BizLeaveTimeVo vo) {
        BizLeaveTime leaveTime = bizLeaveTimeMapper.selectById(vo.getBusinessId());
        BeanUtils.copyProperties(leaveTime,vo);

    }
    /**
     * 提交数据，大多数时候这是一个必须的方法，它用于为流程提交业务数据
     * ⚠️⚠️⚠️ 入参和出参类型一致,返回对象时一定要返回原对象(参考第三方账号申请,将新属性赋值给原对象)
     * @param vo vo
     * @return T(business data)
     */
    @Override
    public void submitData(BizLeaveTimeVo vo) {
        BizLeaveTime leaveTime=new BizLeaveTime();
        BeanUtils.copyProperties(vo,leaveTime);

        leaveTime.setCreateTime(new Date());
        leaveTime.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
        leaveTime.setLeaveUserId(Integer.parseInt(vo.getStartUserId()));
        leaveTime.setLeaveUserName(SecurityUtils.getLogInEmpNameSafe());

        bizLeaveTimeMapper.insert(leaveTime);
        vo.setBusinessId(leaveTime.getId().toString());
        vo.setProcessTitle("请假申请");
        vo.setProcessDigest(vo.getRemark());
        vo.setEntityName(BizLeaveTime.class.getSimpleName());

    }
    /**
     * 当审批人提交他的批准时，它将调用此方法来执行业务逻辑，这大多数时候不是必须的方法
     * @param vo vo
     * @return T(business data)
     */
    @Override
    public void consentData(BizLeaveTimeVo vo) {
        if (vo.getOperationType().equals(ButtonTypeEnum.BUTTON_TYPE_RESUBMIT.getCode())
                && !vo.getOperationType().equals(ButtonTypeEnum.BUTTON_TYPE_AGREE.getCode())){
            BizLeaveTime leaveTime = new BizLeaveTime();
            BeanUtils.copyProperties(vo,leaveTime);
            Integer id=  Integer.valueOf((vo.getBusinessId()).toString());
            leaveTime.setId(id);
            bizLeaveTimeMapper.updateById(leaveTime);
        }

    }
    /**
     * 当流程退回修改时调用此方法来执行业务逻辑，这大多数时候不是必须的方法
     * @param vo vo
     */
    @Override
    public void backToModifyData(BizLeaveTimeVo vo) {

    }
    /**
     * 当流程被取消时，它将调用此方法来执行业务逻辑，这通常是一个必须的方法，通常用于使业务数据无效
     * @param vo businessDataVo
     */
    @Override
    public void cancellationData(BizLeaveTimeVo vo) {

    }
    /**
     * 流程结束
     * @param vo
     */
    @Override
    public void finishData(BusinessDataVo vo) {

    }
}
