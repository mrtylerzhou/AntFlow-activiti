package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.base.entity.BpmnNodeOutSideAccessConf;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeOutSideAccessConfServiceImpl;
import org.openoa.base.util.MultiTenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class NodePropertyOutSideAccessAdp extends BpmnNodeAdaptor {


    @Autowired
    private BpmnNodeOutSideAccessConfServiceImpl bpmnNodeOutSideAccessConfService;

    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {

        BpmnNodeOutSideAccessConf nodeOutSideAccessConf = bpmnNodeOutSideAccessConfService.getOne(new QueryWrapper<BpmnNodeOutSideAccessConf>()
                .eq("bpmn_node_id", bpmnNodeVo.getId()));

        if (nodeOutSideAccessConf!=null) {
            bpmnNodeVo.setProperty(BpmnNodePropertysVo
                    .builder()
                    .signType(nodeOutSideAccessConf.getSignType())
                    .nodeMark(nodeOutSideAccessConf.getNodeMark())
                    .build());
            bpmnNodeVo.setOrderedNodeType(OrderNodeTypeEnum.OUT_SIDE_NODE.getCode());
        }

        return bpmnNodeVo;
    }

    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {

        BpmnNodePropertysVo propertysVo = Optional.ofNullable(bpmnNodeVo.getProperty())
                .orElse(new BpmnNodePropertysVo());

        bpmnNodeOutSideAccessConfService.save(BpmnNodeOutSideAccessConf
                .builder()
                .bpmnNodeId(bpmnNodeVo.getId())
                .signType(propertysVo.getSignType())
                .nodeMark(propertysVo.getNodeMark())
                .tenantId(MultiTenantUtil.getCurrentTenantId())
                .build());

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
