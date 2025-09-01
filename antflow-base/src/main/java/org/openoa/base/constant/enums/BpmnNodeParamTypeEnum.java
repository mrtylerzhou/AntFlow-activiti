package org.openoa.base.constant.enums;

import lombok.Getter;

public enum BpmnNodeParamTypeEnum implements AfEnumBase{


    BPMN_NODE_PARAM_SINGLE(1, "单人"),
    BPMN_NODE_PARAM_MULTIPLAYER(2, "多人"),
    BPMN_NODE_PARAM_MULTIPLAYER_SORT(3, "多人有序"),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    BpmnNodeParamTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
