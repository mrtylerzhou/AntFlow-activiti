package org.openoa.base.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public enum AFSpecialAssigneeEnum implements AfEnumBase{
    TO_BE_REMOVED(0, "0", "最终会被去除的人员"),
    ;
    private final Integer code;
    private final String id;
    private final String desc;

    AFSpecialAssigneeEnum(int code, String id, String desc){

        this.code = code;
        this.id = id;
        this.desc = desc;
    }
}
