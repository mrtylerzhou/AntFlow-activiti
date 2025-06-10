package org.openoa.base.constant.enums;

import lombok.Getter;

@Getter
public enum LFControlTypeEnum {
    SELECT(1,"select","下拉框"),
    ;
    private final int code;
    private final String name;
    private final String desc;

    LFControlTypeEnum(int code, String name, String desc){

        this.code = code;
        this.name = name;
        this.desc = desc;
    }

}
