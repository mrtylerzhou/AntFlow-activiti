package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Classname BpmnNodeParamsVo
 * @Date 2021-10-31 10:11
 * @Created by AntOffice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnNodeParamsVo implements Serializable {
    /**
     * node to id
     */
    private String nodeTo;

    /**
     * param type 1 for single person 2 for multi person 3 for multi person in order
     */
    private Integer paramType;

    /**
     * assignee
     */
    private BpmnNodeParamsAssigneeVo assignee;

    /**
     * assignee lisrt
     */
    private List<BpmnNodeParamsAssigneeVo> assigneeList;

    /**
     * is duplication 0 no 1 yes
     */
    private int isNodeDeduplication = 0;
}
