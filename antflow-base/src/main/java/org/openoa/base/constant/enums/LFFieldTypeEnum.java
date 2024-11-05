package org.openoa.base.constant.enums;

import lombok.Getter;

@Getter
public enum LFFieldTypeEnum {
    STRING(1,"字符串"),
    NUMBER(2,"数字"),
    DATE(3,"日期"),
    TEXT(4,"长字符串"),
    BLOB(5,"二进制")
    ;
    private final Integer type;
    private final String desc;

    LFFieldTypeEnum(Integer type, String desc){

        this.type = type;
        this.desc = desc;
    }
}
