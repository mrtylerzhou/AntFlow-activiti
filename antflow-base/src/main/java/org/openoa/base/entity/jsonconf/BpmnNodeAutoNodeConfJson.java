package org.openoa.base.entity.jsonconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.vo.BpmnNodeConditionsConfVueVo;

import java.io.Serializable;
import java.util.List;

/**
 * Auto node configuration JSON.
 * Stores conditions that determine when the automatic action should be executed.
 * Reuses {@link BpmnNodeConditionsConfVueVo} for condition items — same structure as condition nodes.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnNodeAutoNodeConfJson implements Serializable {

    /**
     * Condition groups (outer list = groups, inner list = conditions within a group).
     */
    private List<List<BpmnNodeConditionsConfVueVo>> conditionList;

    /**
     * Group relation: false = AND between groups, true = OR between groups
     */
    private Boolean groupRelation;
}
