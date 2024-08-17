package org.openoa.base.constant.enums;

import lombok.Getter;

public enum ProcessStateEnum {
    /**
     * process state
     */

    COMLETE_STATE(1, "审批中"),
    END_STATE(3, "作废"),
    HANDLE_STATE(2, "审批通过"),
    CRMCEL_STATE(6, "审批拒绝"),
    ;
    @Getter
    private Integer code;

    @Getter
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    ProcessStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ProcessStateEnum statusType : ProcessStateEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }

    public static Integer getCodeByDesc(String desc) {
        for (ProcessStateEnum statusType : ProcessStateEnum.values()) {
            if (statusType.desc.equals(desc)) {
                return statusType.code;
            }
        }
        return null;
    }
}
