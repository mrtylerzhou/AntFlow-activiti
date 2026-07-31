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

    /**
     * Auto node configuration (conditions for automatic execution)
     */
    private BpmnNodeAutoNodeConfJson autoNodeConf;

    /**
     * Back type for disagree action (migrated from bpm_process_node_back)
     */
    private Integer backType;

    /**
     * Target node ID (design-time UUID) for disagree-back behavior.
     * Used when backType is 4 or 5 to specify which node to return to.
     */
    private String backToNodeId;

    /**
     * Draw-back button behavior type.
     * 0=unrestricted(default), 1=back to prev node, 2=back to initiator(no return),
     * 3=back to initiator(return to sender), 4=back to specified nodes(no return),
     * 5=back to specified nodes(return to sender)
     */
    private Integer drawBackType;

    /**
     * Allowed target node IDs (design-time UUIDs) for draw-back behavior.
     * Only used when drawBackType is 4 or 5.
     */
    private java.util.List<String> drawBackNodeIds;
}
