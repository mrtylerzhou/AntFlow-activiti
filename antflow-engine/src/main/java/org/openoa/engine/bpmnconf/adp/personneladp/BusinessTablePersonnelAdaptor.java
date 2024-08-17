package org.openoa.engine.bpmnconf.adp.personneladp;

import org.openoa.base.constant.enums.PersonnelEnum;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Author TylerZhou
 * @Date 2024/7/28 7:59
 * @Version 1.0
 */
@Component
public class BusinessTablePersonnelAdaptor extends AbstractBpmnPersonnelAdaptor {
    public BusinessTablePersonnelAdaptor(BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService,@Qualifier("businessTablePersonnelProvider") BpmnPersonnelProviderService bpmnPersonnelProviderService) {
        super(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(PersonnelEnum.BUSINESS_TABLE_PERSONNEL);
    }
}
