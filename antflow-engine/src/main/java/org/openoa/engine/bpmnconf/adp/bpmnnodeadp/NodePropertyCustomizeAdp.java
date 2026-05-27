package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.AfNodeUtils;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.base.entity.BpmnNodeCustomizeConf;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeCustomizeConfServiceImpl;
import org.openoa.base.util.MultiTenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Component
public class NodePropertyCustomizeAdp extends AbstractAdditionSignNodeAdaptor{
    @Autowired
    private BpmnNodeCustomizeConfServiceImpl bpmnNodeCustomizeConfService;
    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        super.formatToBpmnNodeVo(bpmnNodeVo);

        // Prefer JSON config if available
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getApproverConf() != null
                && nodeConfig.getApproverConf().getCustomizeConf() != null) {
            AfNodeUtils.addOrEditProperty(bpmnNodeVo, p -> p.setSignType(nodeConfig.getApproverConf().getCustomizeConf().getSignType()));
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
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_CUSTOMIZE);
    }
}
