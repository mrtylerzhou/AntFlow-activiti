package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnNodeLabel;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeLabelsMapper;
import org.openoa.base.service.BpmNodeLabelsService;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BpmNodeLabelsServiceImpl extends ServiceImpl<BpmnNodeLabelsMapper, BpmnNodeLabel> implements BpmNodeLabelsService {

    public void  saveNodeLabelsInBatch(List<BpmnNodeLabelVO> nodeLabelVOS,Long nodeId){
        if(CollectionUtils.isEmpty(nodeLabelVOS)){
            return;
        }
        List<BpmnNodeLabel> nodeLables = nodeLabelVOS.stream().map(a -> {
            BpmnNodeLabel bpmnNodeLabel = new BpmnNodeLabel();
            bpmnNodeLabel.setLabelName(a.getLabelName());
            bpmnNodeLabel.setLabelValue(a.getLabelValue());
            bpmnNodeLabel.setNodeId(nodeId);
            return bpmnNodeLabel;
        }).collect(Collectors.toList());
        this.saveBatch(nodeLables);
    }
}
