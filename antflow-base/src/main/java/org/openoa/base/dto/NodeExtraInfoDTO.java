package org.openoa.base.dto;

import lombok.Data;
import org.openoa.base.vo.BpmnNodeLabelVO;

import java.util.List;

@Data
public class NodeExtraInfoDTO {
    private List<BpmnNodeLabelVO> nodeLabelVOS;
}
