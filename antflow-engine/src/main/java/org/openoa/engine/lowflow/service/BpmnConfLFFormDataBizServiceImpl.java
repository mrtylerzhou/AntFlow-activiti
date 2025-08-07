package org.openoa.engine.lowflow.service;

import org.openoa.base.entity.BpmnConfLfFormdata;
import org.openoa.engine.bpmnconf.service.biz.BizServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfLfFormdataServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BpmnConfLFFormDataBizServiceImpl extends BizServiceImpl<BpmnConfLfFormdataServiceImpl> {

    public BpmnConfLfFormdata getLFFormDataByFormCode(String formCode){
        BpmnConfLfFormdata byFormCode = this.getService().getBaseMapper().getByFormCode(formCode);
        return byFormCode;
    }
}
