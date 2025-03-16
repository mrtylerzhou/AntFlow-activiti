package org.openoa.base.util;

import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.BpmnNodeVo;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AfNodeUtils {
    public static BpmnNodeVo getNextNodeOfTypeRecursively(String nodeId, List<Integer> types, Map<String, BpmnNodeVo> mapNodes, boolean includeDeduplicated) {
        if (CollectionUtils.isEmpty(types)) {
            return null;
        }
        if (nodeId == null) {
           return null;
        }
        if (CollectionUtils.isEmpty(mapNodes)) {
          return null;
        }
        BpmnNodeVo node = mapNodes.get(nodeId);
        if (types.contains(node.getNodeType())) {
            if (!includeDeduplicated && node.getParams().getIsNodeDeduplication() == 0) {
                return node;
            }
        }
        if (node.getParams().getNodeTo() != null) {
            return getNextNodeOfTypeRecursively(node.getParams().getNodeTo(), types, mapNodes, includeDeduplicated);
        } else {
            return null;
        }
    }
    public static BpmnNodeVo getNeareastPrevNodeOfTypeRecursively(String nodeId, List<Integer> types, Map<String, BpmnNodeVo> mapNodes, boolean includeDeduplicated) {
        if (CollectionUtils.isEmpty(types)) {
           return null;
        }
        if (nodeId == null) {
           return null;
        }
        if (CollectionUtils.isEmpty(mapNodes)) {
           return null;
        }
        BpmnNodeVo node = mapNodes.get(nodeId);
        String nodeFrom = node.getNodeFrom();
        BpmnNodeVo fromNode = mapNodes.get(nodeFrom);
        if (types.contains(fromNode.getNodeType())) {
            if (!includeDeduplicated && node.getParams().getIsNodeDeduplication() == 0) {
                return fromNode;
            }
        }
        if (node.getParams().getNodeTo() != null) {
            return getNeareastPrevNodeOfTypeRecursively(node.getParams().getNodeTo(), types, mapNodes, includeDeduplicated);
        } else {
            return node;
        }
    }
}
