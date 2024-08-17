package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.springframework.stereotype.Component;

/**
 * @Author tylerzhou
 * @since 0.5
 */
@Component
public class NodePropertyStartUserAdp extends BpmnNodeAdaptor {
    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        bpmnNodeVo.setDeduplicationExclude(true);
        return bpmnNodeVo;
    }

    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {

    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        PersonnelRuleVO vo=new PersonnelRuleVO();
        NodePropertyEnum nodePropertyStartUser = NodePropertyEnum.NODE_PROPERTY_START_USER;
        vo.setNodeProperty(nodePropertyStartUser.getCode());
        vo.setNodePropertyName(nodePropertyStartUser.getDesc());
        return vo;
    }


    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_START_USER);
    }
}
