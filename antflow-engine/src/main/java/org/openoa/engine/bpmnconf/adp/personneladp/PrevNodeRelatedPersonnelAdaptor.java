package org.openoa.engine.bpmnconf.adp.personneladp;

import org.openoa.base.constant.enums.PersonnelEnum;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PrevNodeRelatedPersonnelAdaptor extends AbstractBpmnPersonnelAdaptor {
    public PrevNodeRelatedPersonnelAdaptor(BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService,@Qualifier("prevNodeRelatedPersonnelProvider") BpmnPersonnelProviderService bpmnPersonnelProviderService) {
        super(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(PersonnelEnum.PREV_NODE_USERS_PERSONNEL);
    }
}
