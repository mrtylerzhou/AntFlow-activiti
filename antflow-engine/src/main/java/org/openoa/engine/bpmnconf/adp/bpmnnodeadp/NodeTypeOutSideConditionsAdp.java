package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmnNodeConditionsConf;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmnNodeConditionsConfServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component("nodeTypeOutSideConditionsAdp")
public class NodeTypeOutSideConditionsAdp extends NodeTypeConditionsAdp {

    @Autowired
    private OutSideBpmnNodeConditionsConfServiceImpl outSideBpmnNodeConditionsConfService;

    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        BpmnNodeVo nodeVo = super.formatToBpmnNodeVo(bpmnNodeVo);

        if(StringUtil.isEmpty(bpmnNodeVo.getConditionsUrl())){
            return nodeVo;
        }



        //get conditions conf by node id
        OutSideBpmnNodeConditionsConf outSideBpmnNodeConditionsConf = outSideBpmnNodeConditionsConfService.getOne(new QueryWrapper<OutSideBpmnNodeConditionsConf>()
                .eq("bpmn_node_id", bpmnNodeVo.getId()));

        //join conditions url and outside conditions id to shape the final outSideConditionsUrl
        String outSideConditionsUrl = StringUtils.join(bpmnNodeVo.getConditionsUrl(), outSideBpmnNodeConditionsConf.getOutSideId());


        //set outside conditions url to node property
        nodeVo.getProperty().getConditionsConf().setOutSideConditionsUrl(outSideConditionsUrl);


        //set node type
        nodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_CONDITIONS.getCode());

        return nodeVo;
    }

    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {
        super.editBpmnNode(bpmnNodeVo);


        //edit out side conditions conf
        OutSideBpmnNodeConditionsConf outSideBpmnNodeConditionsConf = new OutSideBpmnNodeConditionsConf();
        outSideBpmnNodeConditionsConf.setBpmnNodeId(bpmnNodeVo.getId());
        if (bpmnNodeVo.getProperty().getConditionsConf()!=null) {//todo
            outSideBpmnNodeConditionsConf.setOutSideId(bpmnNodeVo.getProperty().getConditionsConf().getOutSideConditionsId());
        }


        //save config
        outSideBpmnNodeConditionsConfService.save(outSideBpmnNodeConditionsConf);

    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_TYPE_OUT_SIDE_CONDITIONS);
    }
}
