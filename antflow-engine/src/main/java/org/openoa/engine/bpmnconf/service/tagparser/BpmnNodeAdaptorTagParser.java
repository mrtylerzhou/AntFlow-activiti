package org.openoa.engine.bpmnconf.service.tagparser;

import org.openoa.base.exception.AFBizException;
import org.openoa.engine.factory.TagParser;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.engine.bpmnconf.adp.bpmnnodeadp.BpmnNodeAdaptor;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;

import java.util.Collection;

/**
 * @Author TylerZhou
 * @Date 2024/7/8 20:41
 * @Version 1.0
 */
public class BpmnNodeAdaptorTagParser implements TagParser<BpmnNodeAdaptor, BpmnNodeAdpConfEnum> {
    @Override
    public BpmnNodeAdaptor parseTag(BpmnNodeAdpConfEnum data) {
        if(data==null){
            throw new AFBizException("provided data to find a bpmnNodeAdaptor method is null");
        }
        Collection<BpmnNodeAdaptor> bpmnNodeAdaptors = SpringBeanUtils.getBeans(BpmnNodeAdaptor.class);
        for (BpmnNodeAdaptor bpmnNodeAdaptor : bpmnNodeAdaptors) {
            if(bpmnNodeAdaptor.isSupportBusinessObject(data)){
                return bpmnNodeAdaptor;
            }
        }
        return null;
    }
}
