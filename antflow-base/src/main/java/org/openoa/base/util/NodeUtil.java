package org.openoa.base.util;

import com.google.common.collect.Lists;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.entity.BpmnNodeLabel;
import org.openoa.base.service.BpmNodeLabelsService;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.NodeLabelConstants;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class NodeUtil {
    public static boolean nodeLabelContainsAny(NodeExtraInfoDTO nodeExtraInfoDTO,String ... labelValues){
        if(labelValues.length<1){
            return true;

        }
        if(nodeExtraInfoDTO==null){
            return false;
        }
        List<BpmnNodeLabelVO> nodeLabelVOS = nodeExtraInfoDTO.getNodeLabelVOS();
        return nodeLabelContainsAny(nodeLabelVOS,labelValues);
    }
    public static boolean nodeLabelContainsAny( List<BpmnNodeLabelVO> nodeLabelVOS,String ... labelValues){
        if(labelValues.length<1){
            return true;

        }
        if(CollectionUtils.isEmpty(nodeLabelVOS)){
            return false;
        }
        List<String> providedLabelValues = nodeLabelVOS.stream().map(BpmnNodeLabelVO::getLabelValue).collect(Collectors.toList());
        return CollectionUtils.containsAny(providedLabelValues, Lists.newArrayList(labelValues));
    }
    public static boolean isCurrentNodeNoneOperational(String nodeId){
        BpmNodeLabelsService bpmNodeLabelsService = SpringBeanUtils.getBean(BpmNodeLabelsService.class);
        List<BpmnNodeLabel> nodeLabels = bpmNodeLabelsService.list(AFWrappers.<BpmnNodeLabel>lambdaTenantQuery().eq(BpmnNodeLabel::getNodeId,nodeId));
        if(CollectionUtils.isEmpty(nodeLabels)){
            return true;
        }
        List<String> collect = nodeLabels.stream().map(BpmnNodeLabel::getLabelValue).collect(Collectors.toList());
        List<String> noneOperationalLables = NodeLabelConstants.NONE_OPERATIONAL_NODES.stream().map(BpmnNodeLabelVO::getLabelValue).collect(Collectors.toList());
        return CollectionUtils.containsAny(collect,noneOperationalLables);
    }
}
