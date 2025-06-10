package org.openoa.base.constant.enums;

import lombok.Getter;

@Getter
public enum AFSpecialAssigneeEnum {
    TO_BE_REMOVED(0, "0", "最终会被去除的人员"),
    ;
    private final int code;
    private final String id;
    private final String desc;

    AFSpecialAssigneeEnum(int code, String id, String desc){

        this.code = code;
        this.id = id;
        this.desc = desc;
    }
}
