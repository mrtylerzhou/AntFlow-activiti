package org.openoa.base.constant.enums;

import lombok.Getter;

public enum InformEnum {

    APPLICANT(1, "申请人", "startUser"),
    ALL_APPROVER(2, "所有已审批人", "approveds"),
    CURRENT_APPROVER(3, "当前节点审批人", "assignee"),
    FORWARDED_APPROVER(4, "被转发人", "forwardUsers"),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    @Getter
    public String filName;

    InformEnum(Integer code, String desc, String filName) {
        this.code = code;
        this.desc = desc;
        this.filName = filName;
    }

    public static InformEnum getEnumByByCode(Integer code) {
        for (InformEnum informEnum : InformEnum.values()) {
            if (informEnum.getCode().equals(code)) {
                return informEnum;
            }
        }
        return null;
    }
}

