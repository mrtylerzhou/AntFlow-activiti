package org.openoa.engine.bpmnconf.service.processor;

import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.jsonconf.BpmnNodeButtonSignConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.service.AntFlowOrderPostProcessor;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodeLabelsPostProcessor implements AntFlowOrderPostProcessor<BpmnConfVo> {
    @Autowired
    private BpmnNodeService bpmnNodeService;

    @Override
    public void postProcess(BpmnConfVo confVo) {
        List<BpmnNodeVo> nodeVos = confVo.getNodes();
        for (BpmnNodeVo nodeVo : nodeVos) {
            List<BpmnNodeLabelVO> labelList = nodeVo.getLabelList();
            if (!CollectionUtils.isEmpty(labelList)){
                updateLabelsToNodeJson(nodeVo.getId(), labelList);
            }
        }

    }

    private void updateLabelsToNodeJson(Long nodeId, List<BpmnNodeLabelVO> labelList) {
        BpmnNode node = bpmnNodeService.getById(nodeId);
        if (node == null) {
            return;
        }
        BpmnNodeConfigJson nodeConfig = node.getNodeConfigJson() == null
                ? BpmnNodeConfigJson.builder().build()
                : JsonConfUtil.parseNodeConfig(node.getNodeConfigJson());
        if (nodeConfig == null) {
            nodeConfig = BpmnNodeConfigJson.builder().build();
        }
        if (nodeConfig.getButtonSignConf() == null) {
            nodeConfig.setButtonSignConf(BpmnNodeButtonSignConfJson.builder().build());
        }
        List<BpmnNodeButtonSignConfJson.NodeLabel> jsonLabels = labelList.stream()
                .map(l -> BpmnNodeButtonSignConfJson.NodeLabel.builder()
                        .labelValue(l.getLabelValue())
                        .labelName(l.getLabelName())
                        .build())
                .collect(Collectors.toList());
        nodeConfig.getButtonSignConf().setLabels(jsonLabels);
        node.setNodeConfigJson(JsonConfUtil.toNodeConfigJson(nodeConfig));
        bpmnNodeService.updateById(node);
    }


    @Override
    public int order() {
        return 1;
    }
}
