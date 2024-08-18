package org.openoa.base.constant.enums;

import lombok.Getter;

public enum JumpUrlEnum {

    PROCESS_APPROVE(1, "流程审批页"),
    PROCESS_VIEW(2, "流程查看页"),
    PROCESS_BACKLOG(3, "流程待办页"),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    JumpUrlEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByByCode(Integer code) {
        for (JumpUrlEnum jumpUrlEnum : JumpUrlEnum.values()) {
            if (jumpUrlEnum.getCode().equals(code)) {
                return jumpUrlEnum.getDesc();
            }
        }
        return null;
    }

}
