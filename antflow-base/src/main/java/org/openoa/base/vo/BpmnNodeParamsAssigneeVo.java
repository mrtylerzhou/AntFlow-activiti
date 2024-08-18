package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Classname BpmnNodeParamsAssigneeVo
 * @Description node params
 * @Date 2021-10-31 10:12
 * @Created by AntOffice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnNodeParamsAssigneeVo implements Serializable {
    /**
     * assignee
     */
    private String assignee;

    /**
     * assignee name
     */
    private String assigneeName;

    /**
     * element name
     */
    private String elementName;

    /**
     * is deduplication 0 for no 1 for yes
     */
    private Integer isDeduplication = 0;
}
