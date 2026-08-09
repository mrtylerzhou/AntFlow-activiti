package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.entity.jsonconf.BpmnNodeApproverConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class NodePropertyPrevNodeAdp implements BpmnNodeAdaptor {

    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getApproverConf() != null
                && !CollectionUtils.isEmpty(nodeConfig.getApproverConf().getPrevNodeRelatedUserConfList())) {
            BpmnNodeApproverConfJson.PrevNodeRelatedUserConf conf = nodeConfig.getApproverConf().getPrevNodeRelatedUserConfList().get(0);
            bpmnNodeVo.setProperty(BpmnNodePropertysVo.builder()
                    .signType(conf.getSignType())
                    .arbitrationRatio(conf.getArbitrationRatio())
                    .formAssigneeProperty(conf.getValueType())
                    .build());
        }
    }


    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        return null;
    }
    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_PREV_NODE_RELATED_USERS);
    }
}
