package org.openoa.base.constant.enums;

import lombok.Getter;

public enum ProcessJurisdictionEnum implements AfEnumBase{

    VIEW_TYPE(1,"查看"),
    CREATE_TYPE(2,"创建"),
    CONTROL_TYPE(3,"监控"),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;
    public Integer getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
    ProcessJurisdictionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ProcessJurisdictionEnum statusType : ProcessJurisdictionEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }
}
