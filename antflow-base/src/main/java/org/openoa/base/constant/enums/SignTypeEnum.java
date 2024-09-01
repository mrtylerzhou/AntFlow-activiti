package org.openoa.base.constant.enums;

import lombok.Getter;

public enum SignTypeEnum {

    SIGN_TYPE_SIGN(1, "会签（需所有审批人同意，不限顺序）"),
    SIGN_TYPE_OR_SIGN(2, "或签（只需一名审批人同意或拒绝即可）"),
    SIGN_TYPE_SIGN_IN_ORDER(3,"顺序会签(需要所有审批人同意,根据前端传入的顺序)")
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    SignTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
