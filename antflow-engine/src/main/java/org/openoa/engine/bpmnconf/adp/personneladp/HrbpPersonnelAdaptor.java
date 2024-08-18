package org.openoa.engine.bpmnconf.adp.personneladp;

import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.constant.enums.PersonnelEnum;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @Author TylerZhou
 * @Date 2024/7/19 19:51
 * @Version 0.5
 */
@Service
public class HrbpPersonnelAdaptor extends AbstractBpmnPersonnelAdaptor {
    public HrbpPersonnelAdaptor(BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService,@Qualifier("hrbpPersonnelProvider") BpmnPersonnelProviderService bpmnPersonnelProviderService) {
        super(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(PersonnelEnum.HRBP_PERSONNEL);
    }
}
