package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
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
public class NodePropertyCustomizeAdp extends BpmnNodeAdaptor{
    @Autowired
    private BpmnNodeCustomizeConfServiceImpl bpmnNodeCustomizeConfService;
    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        List<BpmnNodeCustomizeConf> list = bpmnNodeCustomizeConfService.list(new QueryWrapper<BpmnNodeCustomizeConf>()
                .eq("bpmn_node_id", bpmnNodeVo.getId()));

        if (CollectionUtils.isEmpty(list)) {
            return bpmnNodeVo;
        }
        BpmnNodeCustomizeConf customizeConf = list.get(0);
        bpmnNodeVo.setProperty(BpmnNodePropertysVo
                .builder()
                .signType(customizeConf.getSignType())
                .build());
        return bpmnNodeVo;
    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        return null;
    }

    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {
        BpmnNodePropertysVo bpmnNodePropertysVo = Optional.ofNullable(bpmnNodeVo.getProperty())
                .orElse(new BpmnNodePropertysVo());

        BpmnNodeCustomizeConf customizeConf=new BpmnNodeCustomizeConf();
        customizeConf.setBpmnNodeId(bpmnNodeVo.getId());
        customizeConf.setSignType(bpmnNodePropertysVo.getSignType());
        customizeConf.setTenantId(MultiTenantUtil.getCurrentTenantId());
        bpmnNodeCustomizeConfService.save(customizeConf);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_CUSTOMIZE);
    }
}
