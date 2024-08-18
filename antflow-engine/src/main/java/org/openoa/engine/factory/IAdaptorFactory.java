package org.openoa.engine.factory;


import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.interf.anno.AutoParse;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;
import org.openoa.common.adaptor.bpmnelementadp.AbstractOrderedSignNodeAdp;
import org.openoa.common.adaptor.bpmnelementadp.BpmnElementAdaptor;
import org.openoa.engine.bpmnconf.adp.bpmnnodeadp.BpmnNodeAdaptor;
import org.openoa.engine.bpmnconf.adp.personneladp.AbstractBusinessConfigurationAdaptor;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.constant.enus.ConfigurationTableAdapterEnum;
import org.openoa.engine.bpmnconf.service.tagparser.*;

/**
 * @Author TylerZhou
 * @Date 2022-05-27 22:26
 * @Param
 * @return
 * @Version 1.0
 */
public interface IAdaptorFactory {
    @SpfService(tagParser = ActivitiTagParser.class)
    public FormOperationAdaptor getActivitiService(BusinessDataVo dataVo);

    @SpfService(tagParser = PersonnelTagParser.class)
    AbstractBpmnPersonnelAdaptor getPersonnelAdaptor(NodePropertyEnum nodePropertyEnum);

    @SpfService(tagParser= OrderedSignTagParser.class)
    AbstractOrderedSignNodeAdp getOrderedSignNodeAdp(OrderNodeTypeEnum orderNodeTypeEnum);

    @SpfService(tagParser = FormOperationTagParser.class)
    ProcessOperationAdaptor getProcessOperation(BusinessDataVo vo);

    @SpfService(tagParser = BpmnNodeAdaptorTagParser.class)
    BpmnNodeAdaptor getBpmnNodeAdaptor(BpmnNodeAdpConfEnum adpConfEnum);

    //@SpfService(tagParser = BpmnElementAdaptorTagParser.class)
    @AutoParse
    BpmnElementAdaptor getBpmnElementAdaptor(NodePropertyEnum nodePropertyEnum);

    @AutoParse
    AbstractBusinessConfigurationAdaptor getBusinessConfigurationAdaptor(ConfigurationTableAdapterEnum tableAdapterEnum);
}
