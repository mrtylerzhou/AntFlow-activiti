package org.openoa.engine.bpmnconf.service.processor;

import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.service.AntFlowOrderPostProcessor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.entity.BpmnNodeLabel;
import org.openoa.engine.bpmnconf.service.biz.BpmNodeLabelsServiceImpl;
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
            }
        }
        if(!CollectionUtils.isEmpty(nodeLabels)){
            Integer extraFlags = confVo.getExtraFlags();
            Integer binariedOr = BpmnConfFlagsEnum.binaryOr(extraFlags, BpmnConfFlagsEnum.HAS_NODE_LABELS.getCode());
            confVo.setExtraFlags(binariedOr);
            nodeLabelsService.saveBatch(nodeLabels);
        }
    }


    @Override
    public int order() {
        return 1;
    }
}
