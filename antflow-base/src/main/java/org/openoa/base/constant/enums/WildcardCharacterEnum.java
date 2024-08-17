package org.openoa.base.constant.enums;

import lombok.Getter;

/**
 * notice template wildcard
 */
public enum WildcardCharacterEnum {

    ONE_CHARACTER(1, "{工作流名称}", "bpmnName", false, "\\{工作流名称\\}"),
    TWO_CHARACTER(2, "{流程编号}", "processNumber", false, "\\{流程编号\\}"),
    THREE_CHARACTER(3, "{申请人}", "startUser", true, "\\{申请人\\}"),
    FOUR_CHARACTER(4, "{被审批人}", "approvalEmplId", true, "\\{被审批人\\}"),
    FIVE_CHARACTER(5, "{申请日期}(年月日)", "applyDate", false, "\\{申请日期\\}\\(年月日\\)"),
    SIX_CHARACTER(6, "{申请时间}(年月日时分秒)", "applyTime", false, "\\{申请时间\\}\\(年月日时分秒\\)"),
    SEVEN_CHARACTER(7, "{流转对象}(下一节点审批人)", "nextNodeApproveds", true, "\\{流转对象\\}\\(下一节点审批人\\)"),
    EIGHT_CHARACTER(8, "{当前审批人}", "assignee", true, "\\{当前审批人\\}"),
    NINE_CHARACTER(9, "{转发对象}", "forwardUsers", true, "\\{转发对象\\}"),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    @Getter
    private String filName;

    @Getter
    private Boolean isSearchEmpl;

    @Getter
    private String transfDesc;

    WildcardCharacterEnum(Integer code, String desc, String filName, Boolean isSearchEmpl, String transfDesc) {
        this.code = code;
        this.desc = desc;
        this.filName = filName;
        this.isSearchEmpl = isSearchEmpl;
        this.transfDesc = transfDesc;
    }
}
