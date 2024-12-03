package org.openoa.base.util;

import org.openoa.base.vo.BpmnNodeVo;

import java.util.Collection;
import java.util.List;

public class BpmnUtils {
    public static BpmnNodeVo getAggregationNode(BpmnNodeVo parallelNode, Collection<BpmnNodeVo> bpmnNodeVos){
        String parallelNodeNodeId = parallelNode.getNodeId();
        List<String> parallelNodeNodeTo = parallelNode.getNodeTo();
        for (BpmnNodeVo bpmnNodeVo : bpmnNodeVos) {
            if(bpmnNodeVo.getNodeFrom().equals(parallelNodeNodeId)&& !parallelNodeNodeTo.contains(bpmnNodeVo.getNodeId())){
                return bpmnNodeVo;
            }
        }
        return null;
    }
}
