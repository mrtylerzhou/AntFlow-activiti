package org.openoa.base.entity.jsonconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Template and reminder configuration JSON for a BPMN node.
 * Consolidates: t_bpmn_template (node-level), t_bpmn_approve_remind
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnNodeTemplateConfJson implements Serializable {

    /**
     * Notification templates for this node
     */
    private List<TemplateConf> templates;

    /**
     * Approval reminder configuration
     */
    private ApproveRemindConf approveRemind;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TemplateConf implements Serializable {
        private Integer event;
        private String informs;
        private String emps;
        private String roles;
        private String funcs;
        private Long templateId;
        private String messageSendType;
        private String formCode;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApproveRemindConf implements Serializable {
        private Long templateId;
        private String days;
    }
}
