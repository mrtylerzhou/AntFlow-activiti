package org.openoa.base.constant.enums;

import lombok.Getter;

/**
 * @Author TylerZhou
 * @Date 2024/7/27 19:09
 * @Version 1.0
 */
@Getter
public enum FieldValueTypeEnum implements AfEnumBase{
    STRING(1,"String"),
    NUMBER(2,"Number"),
    NUMBERCHOICE(5,"NumberChoice"),
    STRINGCHOICE(6,"StringChoice"),
    PERSONCHOICE(7, "PersonChoice"),
    ROLECHOICE(7, "PersonChoice"),
    ;
    private final Integer code;
    private final String desc;
    FieldValueTypeEnum(Integer code, String desc){

        this.code = code;
        this.desc = desc;
    }
}
