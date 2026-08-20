package org.openoa.engine.bpmnconf.adp.personneladp;

import org.openoa.base.constant.enums.PersonnelEnum;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * "根据标签选择"审批人规则人员适配器(nodeProperty=20)
 *
 * 职责: 注册 PersonnelEnum.LABEL_BASED_PERSONNEL,使 BpmnPersonnelFormatImpl 能匹配到此规则
 *       并委托 LabelBasedPersonnelProvider 评估审批人
 * 元素生成: 由 BpmnGeneralPurposeElementAdaptor 回退处理(按 signType 生成多实例 BPMN 元素)
 *
 * @Author JimuOffice
 * @since 1.0
 */
@Component
public class LabelBasedPersonnelAdaptor extends AbstractBpmnPersonnelAdaptor {
    public LabelBasedPersonnelAdaptor(BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService,
                                      @Qualifier("labelBasedPersonnelProvider") BpmnPersonnelProviderService bpmnPersonnelProviderService) {
        super(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(PersonnelEnum.LABEL_BASED_PERSONNEL);
    }
}
