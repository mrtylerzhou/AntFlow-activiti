package org.openoa.base.entity.jsonconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Variable-level configuration JSON for t_bpm_variable.
 * Consolidates: t_bpm_variable_button, t_bpm_variable_message,
 * t_bpm_variable_approve_remind, t_bpm_variable_sign_up,
 * t_bpm_variable_sign_up_personnel
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariableConfigJson implements Serializable {

    private List<ButtonItem> buttons;
    private List<MessageItem> messages;
    private List<SignUpItem> signUps;
    private List<ApproveRemindItem> approveReminds;

    public List<ButtonItem> getButtons() {
        return buttons != null ? buttons : (buttons = new ArrayList<>());
    }

    public List<MessageItem> getMessages() {
        return messages != null ? messages : (messages = new ArrayList<>());
    }

    public List<SignUpItem> getSignUps() {
        return signUps != null ? signUps : (signUps = new ArrayList<>());
    }

    public List<ApproveRemindItem> getApproveReminds() {
        return approveReminds != null ? approveReminds : (approveReminds = new ArrayList<>());
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ButtonItem implements Serializable {
        private String elementId;
        private Integer buttonPageType;
        private Integer viewType;
        private Integer buttonType;
        private String buttonName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageItem implements Serializable {
        private String elementId;
        private Integer messageType;
        private Integer eventType;
        private String content;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUpItem implements Serializable {
        private String elementId;
        private String nodeId;
        private Integer afterSignUpWay;
        private String subElements;
        /**
         * Personnel grouped by sub-elementId.
         * Key: sub-element's elementId (e.g. signUp element or backSignUp element)
         * Value: list of personnel for that element
         */
        private Map<String, List<PersonnelItem>> personnelByElement;

        public Map<String, List<PersonnelItem>> getPersonnelByElement() {
            return personnelByElement != null ? personnelByElement : (personnelByElement = new HashMap<>());
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PersonnelItem implements Serializable {
        private String assignee;
        private String assigneeName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApproveRemindItem implements Serializable {
        private String elementId;
        private String content;
    }
}
