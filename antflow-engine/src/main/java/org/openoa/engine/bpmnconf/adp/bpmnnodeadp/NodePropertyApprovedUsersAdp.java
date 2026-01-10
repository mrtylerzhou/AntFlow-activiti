package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.springframework.stereotype.Component;

@Component
public class NodePropertyApprovedUsersAdp extends AbstractAdditionSignNodeAdaptor{
    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        super.formatToBpmnNodeVo(bpmnNodeVo);
        bpmnNodeVo.setDeduplicationExclude(true);
    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        return null;
    }

    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {
        super.editBpmnNode(bpmnNodeVo);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_APPROVED_USERS);
    }
}
