package org.openoa.base.constant.enums;

import lombok.Getter;

@Getter
public enum ConditionRelationShipEnum {

    AND(0,"and"), OR(1,"or");

    private final Integer code;
    private final String value;

    ConditionRelationShipEnum(Integer code,String value) {
        this.code = code;
        this.value = value;
    }

}
