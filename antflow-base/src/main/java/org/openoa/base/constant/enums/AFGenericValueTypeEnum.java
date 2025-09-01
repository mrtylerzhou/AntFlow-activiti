package org.openoa.base.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AFGenericValueTypeEnum implements AfEnumBase{
    STRING(0,"字符串"),
    NUM(1,"数值"),
    BASEIDTRANSTRUVO(3,"简单对象"),
    ARRAY_OF_IDNAMESTRCT(4,"简单对象数组"),
    ARRAY_OF_STRING(5,"字符串数组"),
    ARRAY_OF_NUM(6,"数字数组"),
    ;
    private Integer code;
    private String desc;
}
