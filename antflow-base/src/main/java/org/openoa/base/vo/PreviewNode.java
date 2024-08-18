package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *  preview node structure
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreviewNode{


    /**
     * bpmn name
     */
    private String bpmnName;

    /**
     * form code
     */
    private String formCode;

    //node list
    private List<BpmnNodeVo> bpmnNodeList;

    //start user info
    private PrevEmployeeInfo startUserInfo;

    //approval employee info
    private PrevEmployeeInfo employeeInfo;

    //deduplication type
    private Integer deduplicationType;
    //deduplication type name
    private String deduplicationTypeName;


}
