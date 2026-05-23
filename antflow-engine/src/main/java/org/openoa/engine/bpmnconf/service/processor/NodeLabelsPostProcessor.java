package org.openoa.engine.bpmnconf.service.processor;

import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.BpmnNodeLabel;
import org.openoa.base.entity.jsonconf.BpmnNodeButtonSignConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.service.AntFlowOrderPostProcessor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.engine.bpmnconf.service.impl.BpmNodeLabelsServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodeLabelsPostProcessor implements AntFlowOrderPostProcessor<BpmnConfVo> {
    @Autowired
    private BpmNodeLabelsServiceImpl nodeLabelsService;
    @Autowired
    private BpmnNodeService bpmnNodeService;

    @Override
    public void postProcess(BpmnConfVo confVo) {
        List<BpmnNodeVo> nodeVos = confVo.getNodes();
        List<BpmnNodeLabel> nodeLabels=new ArrayList<>();
        for (BpmnNodeVo nodeVo : nodeVos) {
            List<BpmnNodeLabelVO> labelList = nodeVo.getLabelList();
            if (!CollectionUtils.isEmpty(labelList)){
                List<BpmnNodeLabel> labels = labelList.stream().map(a -> {
                    Long nodeId = nodeVo.getId();
                    BpmnNodeLabel nodeLabel = new BpmnNodeLabel();
                    nodeLabel.setNodeId(nodeId);
                    nodeLabel.setLabelName(a.getLabelName());
                    nodeLabel.setLabelValue(a.getLabelValue());
                    nodeLabel.setCreateUser(SecurityUtils.getLogInEmpName());
                    return nodeLabel;
                }).collect(Collectors.toList());
                nodeLabels.addAll(labels);

                updateLabelsToNodeJson(nodeVo.getId(), labelList);
            }
        }
        if(!CollectionUtils.isEmpty(nodeLabels)){
            Integer extraFlags = confVo.getExtraFlags();
            Integer binariedOr = BpmnConfFlagsEnum.HAS_NODE_LABELS.binaryOr(extraFlags);
            confVo.setExtraFlags(binariedOr);
            nodeLabelsService.saveBatch(nodeLabels);
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
