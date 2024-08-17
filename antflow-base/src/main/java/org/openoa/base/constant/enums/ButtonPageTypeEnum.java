package org.openoa.base.constant.enums;

import lombok.Getter;

public enum ButtonPageTypeEnum {

    INITIATE(1, "initiate","发起页"),
    AUDIT(2, "audit","审批页"),
    TO_VIEW(3,"toView","查看页"),
    ;


    @Getter
    private Integer code;
    @Getter
    private String name;
    @Getter
    private String desc;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    ButtonPageTypeEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ButtonPageTypeEnum statusType : ButtonPageTypeEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }

    public static Integer getCodeByDesc(String desc) {
        for (ButtonPageTypeEnum statusType : ButtonPageTypeEnum.values()) {
            if (statusType.desc.equals(desc)) {
                return statusType.code;
            }
        }
        return null;
    }
}