package org.openoa.base.dto;

import lombok.Data;
import org.activiti.engine.delegate.DelegateTask;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.BusinessDataVo;

import java.util.List;


@Data
public class BpmNextTaskDto {
    private String taskId;
    private String taskName;
    private String assignee;
    private String processNumber;
    private String processInstanceId;
    private String taskDefKey;
    private String bpmnCode;
    private String businessId;
    private String startUser;
    private String formCode;
    private String bpmnName;
    private Boolean isOutSide;
    private List<BpmnNodeLabelVO> nodeLabels;
    private BusinessDataVo businessDataVo;
    private DelegateTask delegateTask;
}
