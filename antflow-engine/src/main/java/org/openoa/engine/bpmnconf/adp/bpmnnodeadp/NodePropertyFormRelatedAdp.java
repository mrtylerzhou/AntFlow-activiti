package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.alibaba.fastjson2.JSON;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.entity.jsonconf.BpmnNodeApproverConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodePropertyFormRelatedAdp extends AbstractCommonBpmnNodeAdaptor {

    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        // Prefer JSON config if available
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getApproverConf() != null
                && !org.springframework.util.CollectionUtils.isEmpty(nodeConfig.getApproverConf().getFormRelatedUserConfList())) {
            BpmnNodeApproverConfJson.FormRelatedUserConf fr = nodeConfig.getApproverConf().getFormRelatedUserConfList().get(0);
            List<BaseIdTranStruVo> formNameAndValues = JSON.parseArray(fr.getValueJson(), BaseIdTranStruVo.class);
            bpmnNodeVo.setProperty(BpmnNodePropertysVo.builder()
                    .signType(fr.getSignType())
                    .formAssigneeProperty(fr.getValueType())
                    .formInfos(formNameAndValues)
                    .build());
        }
    throw new AFBizException("migration error,please contact the author");
    }


    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        return null;
    }
    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_FORM_RELATED_USERS);
    }
}
