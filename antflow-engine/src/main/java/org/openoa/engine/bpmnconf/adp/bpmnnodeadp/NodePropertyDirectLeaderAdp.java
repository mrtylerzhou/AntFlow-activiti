package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.springframework.stereotype.Service;

/**
 * @Author TylerZhou
 * @Date 2024/7/19 20:58
 * @Version 0.5
 */
@Service
public class NodePropertyDirectLeaderAdp extends BpmnNodeAdaptor {
    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        return null;
    }

    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {

    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        PersonnelRuleVO vo=new  PersonnelRuleVO();
        NodePropertyEnum nodePropertyDirectLeader = NodePropertyEnum.NODE_PROPERTY_DIRECT_LEADER;
        vo.setNodeProperty(nodePropertyDirectLeader.getCode());
        vo.setNodePropertyName(nodePropertyDirectLeader.getDesc());
        return vo;
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_DIRECT_LEADER);
    }


}
