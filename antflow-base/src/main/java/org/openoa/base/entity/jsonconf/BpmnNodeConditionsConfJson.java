package org.openoa.base.entity.jsonconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Conditions configuration JSON for a BPMN node.
 * Consolidates: t_bpmn_node_conditions_conf, t_bpmn_node_conditions_param_conf,
 * t_out_side_bpmn_node_conditions_conf
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnNodeConditionsConfJson implements Serializable {

    /**
     * Condition groups for this node
     */
    private List<ConditionGroup> conditionGroups;

    /**
     * Outside condition ID (for OUT_SIDE_CONDITIONS node type)
     */
    private String outSideConditionId;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConditionGroup implements Serializable {
        private Integer isDefault;
        private Integer groupRelation;
        private Integer sort;
        private String extJson;
        private List<ConditionParam> params;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConditionParam implements Serializable {
        private Integer conditionParamType;
        private String conditionParamName;
        private String conditionParamJsom;
        private Integer operator;
        private Integer condRelation;
        private Integer condGroup;
    }
}
