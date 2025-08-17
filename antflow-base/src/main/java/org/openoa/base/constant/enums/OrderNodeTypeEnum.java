package org.openoa.base.constant.enums;

import lombok.Getter;

@Getter
public enum OrderNodeTypeEnum implements AfEnumBase{
    TEST_ORDERED_SIGN(1, "示例顺序节点"),
    OUT_SIDE_NODE(2, "外部系统传入节点"),
    LOOP_NODE(3, "循环节点")
    ;
    private final Integer code;
    private final String desc;

    OrderNodeTypeEnum(Integer code, String desc){

        this.code = code;
        this.desc = desc;
    }
    public static OrderNodeTypeEnum getByCode(Integer code){
        if(code==null){
            return null;
        }
        for (OrderNodeTypeEnum anEnum : OrderNodeTypeEnum.values()) {
            if(code.equals(anEnum.getCode())){
                return anEnum;
            }
        }
        return null;
    }
}
