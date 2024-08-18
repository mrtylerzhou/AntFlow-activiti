package org.openoa.engine.bpmnconf.adp.personneladp;

import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.constant.enums.PersonnelEnum;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Author TylerZhou
 * @Date 2024/7/18 6:41
 * @Version 0.5
 */
@Component
public class RolePersonnelAdaptor extends AbstractBpmnPersonnelAdaptor {
    public RolePersonnelAdaptor(BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService,@Qualifier("rolePersonnelProvider") BpmnPersonnelProviderService bpmnPersonnelProviderService) {
        super(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(PersonnelEnum.ROLE_PERSONNEL);
    }
}
