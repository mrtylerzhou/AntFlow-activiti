package org.openoa.base.constant.enums;

import lombok.Getter;

@Getter
public enum NumJudgeOperatorEnum implements  AfEnumBase{
    GT_OR_EQ(1,"大于等于"),
    GT(2,"大于"),
    LT_OR_EQ(3,"小于等于"),
    LT(4,"小于"),
    EQ(5,"等于")
    ;
    private final Integer operatorType;
    private final String desc;

    NumJudgeOperatorEnum(Integer operatorType, String desc){

        this.operatorType = operatorType;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
        return this.operatorType;
    }
}
