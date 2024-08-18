package org.openoa.base.constant.enums;

import lombok.Getter;

public enum ApplyType {

    PROCESS_TYPE(1, "流程"),
    APP_TYPE(2, "应用"),;
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

    ApplyType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ApplyType statusType : ApplyType.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }

    public static Integer getCodeByDesc(String desc) {
        for (ApplyType statusType : ApplyType.values()) {
            if (statusType.desc.equals(desc)) {
                return statusType.code;
            }
        }
        return null;
    }
}
