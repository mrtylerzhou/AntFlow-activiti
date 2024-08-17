package org.openoa.engine.bpmnconf.service.tagparser;

import org.openoa.base.exception.JiMuBizException;
import org.openoa.engine.factory.TagParser;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;
import org.openoa.base.constant.enums.PersonnelEnum;

import java.util.Collection;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-01 11:09
 * @Param
 * @return
 * @Version 1.0
 */
public class PersonnelTagParser implements TagParser<AbstractBpmnPersonnelAdaptor, PersonnelEnum> {
    @Override
    public AbstractBpmnPersonnelAdaptor parseTag(PersonnelEnum data) {
        if(data==null){
            throw new JiMuBizException("provided data to find a personnel adaptor method is null");
        }
        Collection<AbstractBpmnPersonnelAdaptor> beans = SpringBeanUtils.getBeans(AbstractBpmnPersonnelAdaptor.class);
        for (AbstractBpmnPersonnelAdaptor bean : beans) {
            if(bean.isSupportBusinessObject(data)){
                return bean;
            }
        }
       return null;
    }
}
