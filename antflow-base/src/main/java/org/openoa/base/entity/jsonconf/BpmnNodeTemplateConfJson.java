package org.openoa.base.entity.jsonconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.vo.BaseIdTranStruVo;

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

    /**
     * Overtime notice configuration (migrated from bpm_process_node_overtime)
     */
    private OvertimeConf overtimeConf;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TemplateConf implements Serializable {
        private Integer event;
        private List<String> informIdList;
        private List<BaseIdTranStruVo> empList;
        private List<BaseIdTranStruVo> roleList;
        private List<BaseIdTranStruVo> funcList;
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
        /**
         * Reminder days after timeout (1~7), day 1 = first 24h after timeout
         */
        private List<Integer> days;
        /**
         * Node standard time limit in minutes
         */
        private Integer standardMinutes;
        /**
         * Notice channel codes ({@link org.openoa.base.constant.enums.MessageSendTypeEnum}, incl. IN_SITE=4);
         * empty means in-site message only
         */
        private List<Integer> noticeTypes;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OvertimeConf implements Serializable {
        /**
         * Overtime threshold in hours
         */
        private Integer noticeTime;
        /**
         * Notice channel types (1=mail, 2=sms, 3=app)
         */
        private List<Integer> noticeTypes;
    }
}
