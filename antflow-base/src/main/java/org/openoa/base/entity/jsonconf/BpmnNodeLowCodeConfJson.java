package org.openoa.base.entity.jsonconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Low-code form field control configuration JSON for a BPMN node.
 * Consolidates: t_bpmn_node_lf_formdata_field_control
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnNodeLowCodeConfJson implements Serializable {

    /**
     * Form field permissions for this node
     */
    private List<FieldControl> fieldControls;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FieldControl implements Serializable {
        private Long formdataId;
        private String fieldId;
        private String fieldName;
        private String perm;
    }
}
