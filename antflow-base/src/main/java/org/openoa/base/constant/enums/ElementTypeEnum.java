package org.openoa.base.constant.enums;

import lombok.Getter;

public enum ElementTypeEnum {

    ELEMENT_TYPE_START_EVENT(1, "StartEvent"),
    ELEMENT_TYPE_USER_TASK(2, "UserTask"),
    ELEMENT_TYPE_GATEWAY(3, "Gateway"),
    ELEMENT_TYPE_SEQUENCE_FLOW(4, "SequenceFlow"),
    ELEMENT_TYPE_END_EVENT(5, "EndEvent"),
    ;


    @Getter
    private Integer code;

    @Getter
    private String desc;


    ElementTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
