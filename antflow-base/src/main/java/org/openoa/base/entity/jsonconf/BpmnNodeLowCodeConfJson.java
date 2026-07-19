package org.openoa.base.entity.jsonconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
     * Form field permissions for this node (per formdataId via FieldControl.formdataId)
     */
    private List<FieldControl> fieldControls;

    /**
     * Per-form whole-form hide flag for external-form mode.
     * Key = formdataId (t_bpmn_conf_lf_formdata.id), Value = true if the whole form is hidden at this node.
     * Only used in external-form mode; null/empty in inline mode.
     */
    private Map<String, Boolean> formHidden;

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
