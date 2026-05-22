package org.openoa.base.entity.jsonconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Conf-level configuration JSON for t_bpmn_conf.
 * Consolidates: t_bpmn_view_page_button, t_bpmn_conf_notice_template,
 * t_bpmn_conf_notice_template_detail, t_bpmn_template (conf-level),
 * t_bpmn_conf_lf_formdata, t_bpmn_conf_lf_formdata_field
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnConfConfigJson implements Serializable {

    /**
     * View page button configurations
     */
    private List<ViewPageButton> viewPageButtons;

    /**
     * Notice template configuration (conf-level)
     */
    private NoticeTemplateConfig noticeTemplateConfig;

    /**
     * Notification templates (conf-level, where nodeId is null)
     */
    private List<ConfTemplateConf> confTemplates;

    /**
     * Low-code form configuration
     */
    private LowCodeFormConfig lowCodeFormConfig;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ViewPageButton implements Serializable {
        private Integer viewType;
        private Integer buttonType;
        private String buttonName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoticeTemplateConfig implements Serializable {
        private List<NoticeTemplateDetail> details;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoticeTemplateDetail implements Serializable {
        private Integer noticeTemplateType;
        private String noticeTemplateDetail;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConfTemplateConf implements Serializable {
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
    public static class LowCodeFormConfig implements Serializable {
        private String formdata;
        private List<FormField> fields;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FormField implements Serializable {
        private String fieldId;
        private String fieldName;
        private Integer fieldType;
        private Integer isConditionField;
    }
}
