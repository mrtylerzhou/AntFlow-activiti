package org.openoa.base.dto;

import lombok.Data;

@Data
public class NodeXelementXvarXverifyInfo {
    private String nodeId;
    private String elementId;
    //是否是单人审批，0是不是，1是
    private Integer isSingle;
    private String varName;
    private String assignee;
    private String assigneeName;
}
