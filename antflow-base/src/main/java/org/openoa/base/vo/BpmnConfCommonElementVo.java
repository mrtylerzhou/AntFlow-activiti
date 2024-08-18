package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnConfCommonElementVo {

    /**
     * element id
     */
    private String elementId;
    /**
     * node id
     */
    private String nodeId;
    /**
     * element name
     */
    private String elementName;

    /**
     * element type
     * @see org.openoa.base.constant.enums.ElementTypeEnum
     */
    private Integer elementType;

    /**
     * element property
     */
    private Integer elementProperty;

    /**
     * single assignee param name
     */
    private String assigneeParamName;

    /**
     * single assignee param value
     */
    private String assigneeParamValue;

    /**
     * multiplayer assignees collection name
     */
    private String collectionName;

    /**
     * multiplayer assignees collection value
     */
    private List<String> collectionValue;

    /**
     * flow from
     */
    private String flowFrom;

    /**
     * flow to
     */
    private String flowTo;

    /**
     * sequence flow conditions
     */
    private String sequenceFlowConditions;

    /**
     * is last sequence flow 0 for no and 1 for yes
     */
    private int isLastSequenceFlow = 0;

    /**
     * is node sign up 0 for no and 1 for yes
     */
    private int isSignUp = 0;

    /**
     * after sign up way,1-back to sign up,2-not back to sign up
     */
    private Integer afterSignUpWay;

    /**
     * sign up type 1 for sequence sign 2 for no sequence sign 3 for or sign
     */
    private Integer signUpType;

    /**
     * is back to sign up 0 for no and 1 for yes
     */
    private int isBackSignUp = 0;

    /**
     * sign up sub element 0 for no and 1 for yes
     */
    private int isSignUpSubElement = 0;

    /**
     * sign up element id
     */
    private String signUpElementId;

    /**
     * is sign up sequence flow 0 for no and 1 for yes
     */
    private int isSignUpSequenceFlow = 0;

    /**
     * sing up node buttons
     */
    private BpmnConfCommonButtonsVo buttons;

    /**
     * template vos
     */
    private List<BpmnTemplateVo> templateVos;

    /**
     * approve remind vo
     */
    private BpmnApproveRemindVo approveRemindVo;
}