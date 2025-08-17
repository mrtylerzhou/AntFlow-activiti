package org.openoa.base.constant.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum ConditionRelationShipEnum implements AfEnumBase{

    AND(0,false,"and"), OR(1,true,"or");

    private final Integer code;
    private final Boolean value;
    private final String desc;

    ConditionRelationShipEnum(Integer code,boolean value,String desc) {
        this.code = code;
        this.value = value;
        this.desc = desc;
    }

    public static Integer getCodeByValue(Boolean value) {
        if(value==null){
            return 1;//default to or
        }
        for (ConditionRelationShipEnum conditionRelationShipEnum : ConditionRelationShipEnum.values()) {
            if (value==conditionRelationShipEnum.value) {
                return conditionRelationShipEnum.getCode();
            }
        }
        return 1;
    }
    public static boolean getValueByCode(Integer code){
        if(code==null){
            return true;//default to or
        }
        for (ConditionRelationShipEnum value : ConditionRelationShipEnum.values()) {
            if(Objects.equals(value.getCode(), code)){
                return value.value;
            }
        }
        return true;
    }
}
