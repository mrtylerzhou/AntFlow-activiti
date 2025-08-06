package org.openoa.engine.bpmnconf.service.tagparser;

import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.common.adaptor.bpmnelementadp.AbstractOrderedSignNodeAdp;
import org.openoa.engine.factory.TagParser;

import java.util.Collection;

public class OrderedSignTagParser implements TagParser<AbstractOrderedSignNodeAdp,OrderNodeTypeEnum> {
    @Override
    public AbstractOrderedSignNodeAdp parseTag(OrderNodeTypeEnum aEnum) {
        if(aEnum==null){
            throw new AFBizException("provided data to find an element adaptor method is null");
        }
        Collection<AbstractOrderedSignNodeAdp> beans = SpringBeanUtils.getBeans(AbstractOrderedSignNodeAdp.class);
        for (AbstractOrderedSignNodeAdp bean : beans) {
            if(bean.isSupportBusinessObject(aEnum)){
                return bean;
            }
        }
        return null;
    }
}
