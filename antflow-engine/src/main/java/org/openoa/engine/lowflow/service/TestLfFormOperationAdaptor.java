package org.openoa.engine.lowflow.service;

import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.lowflow.vo.UDLFApplyVo;
import org.springframework.stereotype.Service;
//LFFormOperationAdaptor和FormOperationAdaptorapi都是一样的,不同之处在LFFormOperationAdaptor里说明了
@Service("JIUN")
public class TestLfFormOperationAdaptor implements LFFormOperationAdaptor{
    @Override
    public void previewSetCondition(UDLFApplyVo vo) {
        //实现预览时自定义参数
    }

    @Override
    public void initData(UDLFApplyVo vo) {

    }

    @Override
    public void launchParameters(UDLFApplyVo vo) {

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
    public void cancellationData(UDLFApplyVo vo) {

    }

    @Override
    public void finishData(BusinessDataVo vo) {

    }
}
