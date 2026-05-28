package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.entity.jsonconf.BpmnNodeApproverConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.service.interf.DicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class NodePropertyUDRAdp implements BpmnNodeAdaptor {

    @Autowired
    private DicDataService dicDataService;

    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        // Prefer JSON config if available
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getApproverConf() != null
                && !CollectionUtils.isEmpty(nodeConfig.getApproverConf().getUdrConfList())) {
            BpmnNodeApproverConfJson.UDRConf udr = nodeConfig.getApproverConf().getUdrConfList().get(0);
            bpmnNodeVo.setProperty(BpmnNodePropertysVo.builder()
                    .signType(udr.getSignType())
                    .udrAssigneeProperty(BaseIdTranStruVo.builder().id(udr.getUdrProperty()).name(udr.getUdrPropertyName()).build())
                    .udrValueJson(udr.getValueJson())
                    .ext1(udr.getExt1())
                    .ext2(udr.getExt2())
                    .ext3(udr.getExt3())
                    .ext4(udr.getExt4())
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
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_UDR_USERS);
    }
}
