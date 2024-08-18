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
 * @Date 2022-05-01 10:18
 * @Param
 * @return
 * @Version 0.5
 */
@Service
public class UserPointedPersonnelAdp extends AbstractBpmnPersonnelAdaptor {
    public UserPointedPersonnelAdp(BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService,@Qualifier("userPointedPersonnelProvider") BpmnPersonnelProviderService bpmnPersonnelProviderService) {
        super(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(PersonnelEnum.USERAPPOINTED_PERSONNEL);
    }
}
