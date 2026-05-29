package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component("nodeTypeOutSideConditionsAdp")
public class NodeTypeOutSideConditionsAdp extends NodeTypeConditionsAdp {


    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
       super.formatToBpmnNodeVo(bpmnNodeVo);

        if(ObjectUtils.isEmpty(bpmnNodeVo.getConditionsUrl())){
            return ;
        }

        // Prefer JSON config if available
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getConditionsConf() != null
                && !StringUtils.isEmpty(nodeConfig.getConditionsConf().getOutSideConditionId())) {
            String outSideConditionsUrl = StringUtils.join(bpmnNodeVo.getConditionsUrl(),
                    nodeConfig.getConditionsConf().getOutSideConditionId());
            bpmnNodeVo.getProperty().getConditionsConf().setOutSideConditionsUrl(outSideConditionsUrl);
            bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_CONDITIONS.getCode());
            return;
        }
      throw new AFBizException("migration error,please contact the author");

    }



    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_TYPE_OUT_SIDE_CONDITIONS);
    }
}
