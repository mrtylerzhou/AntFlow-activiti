package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.AfNodeUtils;
import org.openoa.base.vo.*;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.springframework.stereotype.Component;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class NodePropertyHrbpAdp extends AbstractAdditionSignNodeAdaptor {


    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        super.formatToBpmnNodeVo(bpmnNodeVo);

        // Prefer JSON config if available
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getApproverConf() != null
                && nodeConfig.getApproverConf().getHrbpConf() != null) {
            AfNodeUtils.addOrEditProperty(bpmnNodeVo, p -> p.setHrbpConfType(nodeConfig.getApproverConf().getHrbpConf().getHrbpConfType()));
            return;
        }

       throw new AFBizException("migration error,please contact the author");

    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {

        return null;
    }


    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_HRBP);
    }
}
