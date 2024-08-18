package org.openoa.base.constant.enums;

import lombok.Getter;

/**
 * process key enum
 */
public enum ProcessKeyEnum {

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

    ProcessKeyEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ProcessKeyEnum statusType : ProcessKeyEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }

    public static Integer getCodeByDesc(String desc) {
        for (ProcessKeyEnum statusType : ProcessKeyEnum.values()) {
            if (statusType.desc.equals(desc)) {
                return statusType.code;
            }
        }
        return null;
    }
}

