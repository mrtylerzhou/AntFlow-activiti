package org.openoa.base.entity.jsonconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Aggregate node-level configuration JSON for t_bpmn_node.
 * Contains all consolidated sub-configurations.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnNodeConfigJson implements Serializable {

    /**
     * Approver configuration (personnel, role, loop, level, hrbp, customize, udr, form-related, outside-access, business-table)
     */
    private BpmnNodeApproverConfJson approverConf;

    /**
     * Conditions configuration
     */
    private BpmnNodeConditionsConfJson conditionsConf;

    /**
     * Button, label, sign-up, additional sign configuration
     */
    private BpmnNodeButtonSignConfJson buttonSignConf;

    /**
     * Template and reminder configuration
     */
    private BpmnNodeTemplateConfJson templateConf;

    /**
     * Low-code form field control configuration
     */
    private BpmnNodeLowCodeConfJson lowCodeConf;
}
