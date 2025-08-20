package org.openoa.engine.bpmnconf.adp.personneladp;

import org.openoa.base.constant.enums.PersonnelEnum;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;
import org.springframework.beans.factory.annotation.Qualifier;

public class ApprovedUsersPersonnelAdaptor extends AbstractBpmnPersonnelAdaptor {
    public ApprovedUsersPersonnelAdaptor(BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService,@Qualifier("approvedUserPersonnelProvider") BpmnPersonnelProviderService bpmnPersonnelProviderService) {
        super(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(PersonnelEnum.APPROVED_USERS_PERSONNEL);
    }
}
