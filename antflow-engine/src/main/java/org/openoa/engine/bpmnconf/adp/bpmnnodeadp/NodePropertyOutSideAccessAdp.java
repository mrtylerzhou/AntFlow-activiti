package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.entity.jsonconf.BpmnNodeApproverConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.base.entity.BpmnNodeOutSideAccessConf;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeOutSideAccessConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class NodePropertyOutSideAccessAdp implements BpmnNodeAdaptor {


    @Autowired
    private BpmnNodeOutSideAccessConfService bpmnNodeOutSideAccessConfService;

    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {

        // Prefer JSON config if available
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getApproverConf() != null
                && nodeConfig.getApproverConf().getOutSideAccessConf() != null) {
            BpmnNodeApproverConfJson.OutSideAccessConf osac = nodeConfig.getApproverConf().getOutSideAccessConf();
            bpmnNodeVo.setProperty(BpmnNodePropertysVo.builder()
                    .signType(osac.getSignType())
                    .nodeMark(osac.getNodeMark())
                    .build());
            bpmnNodeVo.setOrderedNodeType(OrderNodeTypeEnum.OUT_SIDE_NODE.getCode());
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
        addSupportBusinessObjects(
                BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_OUT_SIDE_ACCESS);
    }
}
