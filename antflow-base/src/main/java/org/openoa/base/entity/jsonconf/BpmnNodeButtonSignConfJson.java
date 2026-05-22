package org.openoa.base.entity.jsonconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Button, label, sign-up, and additional sign configuration JSON for a BPMN node.
 * Consolidates: t_bpmn_node_button_conf, t_bpmn_node_sign_up_conf,
 * t_bpmn_node_labels, t_bpmn_node_additional_sign_conf
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnNodeButtonSignConfJson implements Serializable {

    /**
     * Button configurations for this node
     */
    private List<ButtonConf> buttonConfList;

    /**
     * Sign-up (countersign) configuration
     */
    private SignUpConf signUpConf;

    /**
     * Node labels
     */
    private List<NodeLabel> labels;

    /**
     * Additional sign configuration (add/remove approvers)
     */
    private List<AdditionalSignConf> additionalSignConfList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ButtonConf implements Serializable {
        private Integer buttonPageType;
        private Integer buttonType;
        private String buttonName;
        private Integer startPageOnly;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUpConf implements Serializable {
        private Integer afterSignUpWay;
        private Integer signUpType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NodeLabel implements Serializable {
        private String labelName;
        private String labelValue;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdditionalSignConf implements Serializable {
        private String signInfos;
        private Integer signProperty;
        private Integer signPropertyType;
        private Integer signType;
    }
}
