package org.openoa.engine.bpmnconf.service.tagparser;

import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.engine.factory.TagParser;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.common.adaptor.bpmnelementadp.BpmnElementAdaptor;

import java.util.Collection;

/**
 * @Author TylerZhou
 * @Date 2024/7/8 22:17
 * @Version 1.0
 */
public class BpmnElementAdaptorTagParser implements TagParser<BpmnElementAdaptor, NodePropertyEnum> {
    @Override
    public BpmnElementAdaptor parseTag(NodePropertyEnum data) {
        if(data==null){
            throw new JiMuBizException("provided data to find an element adaptor method is null");
        }

        Collection<BpmnElementAdaptor> elementAdaptors = SpringBeanUtils.getBeans(BpmnElementAdaptor.class);
        for (BpmnElementAdaptor elementAdaptor : elementAdaptors) {
            if(elementAdaptor.isSupportBusinessObject(data)){
                return elementAdaptor;
            }
        }
        return null;
    }
}
