package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.springframework.stereotype.Component;

@Component
public class NodePropertyDepartmentLeaderAdp extends BpmnNodeAdaptor{
    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        return null;
    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        return null;
    }

    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {

    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_DEPARTMENT_LEADER);
    }
}
