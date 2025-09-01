package org.openoa.engine.bpmnconf.adp.personneladp;

import org.openoa.base.constant.enums.PersonnelEnum;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DeppartmentLeaderPersonnelAdaptor extends AbstractBpmnPersonnelAdaptor {
    public DeppartmentLeaderPersonnelAdaptor(BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService,@Qualifier("departmentLeaderPersonnelProvider") BpmnPersonnelProviderService bpmnPersonnelProviderService) {
        super(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(PersonnelEnum.DEPARTMENT_LEADER_PERSONNEL);
    }
}
