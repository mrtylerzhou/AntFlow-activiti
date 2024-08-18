package org.openoa.engine.bpmnconf.adp.personneladp;

import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.constant.enums.PersonnelEnum;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-12 19:39
 * @Param
 * @return
 * @Version 0.5
 */
@Service
public class CustomizablePersonnelAdp extends AbstractBpmnPersonnelAdaptor {
    public CustomizablePersonnelAdp(BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService, @Qualifier("customizePersonnelProvider")BpmnPersonnelProviderService bpmnPersonnelProviderService) {
        super(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(PersonnelEnum.CUSTOMIZABLE_PERSONNEL);
    }
}
