package org.openoa.engine.factory;

import org.openoa.common.adaptor.bpmnelementadp.AbstractOrderedSignNodeAdp;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.openoa.engine.bpmnconf.service.tagparser.ActivitiTagParser;
import org.openoa.engine.bpmnconf.service.tagparser.OrderedSignTagParser;
import org.openoa.engine.bpmnconf.service.tagparser.PersonnelTagParser;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.vo.BusinessDataVo;


public class AdaptorFactory {


    @SpfService(tagParser = ActivitiTagParser.class)
    public FormOperationAdaptor getActivitiService(BusinessDataVo dataVo) {
        return null;
    }


    @SpfService(tagParser = PersonnelTagParser.class)
    public AbstractBpmnPersonnelAdaptor getPersonnelAdaptor(NodePropertyEnum nodePropertyEnum) {
        return null;
    }

    @SpfService(tagParser= OrderedSignTagParser.class)
    public AbstractOrderedSignNodeAdp getOrderedSignNodeAdp(OrderNodeTypeEnum orderNodeTypeEnum){
        return null;
    }

}
