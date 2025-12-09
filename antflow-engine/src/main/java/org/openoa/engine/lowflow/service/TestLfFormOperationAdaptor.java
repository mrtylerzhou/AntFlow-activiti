package org.openoa.engine.lowflow.service;

import org.openoa.base.interf.ConditionService;
import org.openoa.base.interf.LFFormOperationAdaptor;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.UDLFApplyVo;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnNodeBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//LFFormOperationAdaptor和FormOperationAdaptorapi都是一样的,不同之处在LFFormOperationAdaptor里说明了
@Service("JIUN")
public class TestLfFormOperationAdaptor implements LFFormOperationAdaptor<UDLFApplyVo> {
    @Autowired
    private BpmnNodeBizService bpmnNodeBizService;
    @Autowired
    private ConditionService conditionService;


    @Override
    public void finishData(BusinessDataVo vo) {

    }

    @Override
    public BpmnStartConditionsVo previewSetCondition(UDLFApplyVo vo) {
        return null;
    }

    @Override
    public void initData(UDLFApplyVo vo) {

    }

    @Override
    public BpmnStartConditionsVo launchParameters(UDLFApplyVo vo) {
        return null;
    }

    @Override
    public Boolean automaticCondition(UDLFApplyVo vo) {
        String processNumber = vo.getProcessNumber();
        String currentTaskDefKey = vo.getTaskDefKey();
        BpmnNodeVo bpmnNodeVo = bpmnNodeBizService.getBpmnNodeVoByTaskDefKey(processNumber, currentTaskDefKey);
        this.queryData(vo);
        BpmnStartConditionsVo bpmnStartConditionsVo = this.previewSetCondition(vo);
        boolean matchCondition = conditionService.checkMatchCondition(bpmnNodeVo, bpmnNodeVo.getProperty().getConditionsConf(), bpmnStartConditionsVo,false);
        return matchCondition;
    }

    @Override
    public void automaticAction(UDLFApplyVo vo,Boolean conditionResult) {

    }

    @Override
    public void queryData(UDLFApplyVo vo) {

    }

    @Override
    public void submitData(UDLFApplyVo vo) {

    }

    @Override
    public void consentData(UDLFApplyVo vo) {

    }

    @Override
    public void backToModifyData(UDLFApplyVo vo) {

    }

    @Override
    public void cancellationData(UDLFApplyVo businessDataVo) {

    }

    @Override
    public void onProcessRecover(BusinessDataVo businessData) {

    }
}
