package org.openoa.base.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AFSpecialDictCategoryEnum implements AfEnumBase{
    LOWCODEFLOW(0,"lowcodeflow"),
    USER_DEFINED_RULE_FOR_ASSIGNEE(0,"udr")
    ;
    private Integer code;
    private String  desc;
}
