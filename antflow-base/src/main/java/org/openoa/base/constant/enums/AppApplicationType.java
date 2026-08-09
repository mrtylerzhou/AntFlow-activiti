package org.openoa.base.constant.enums;
import lombok.Getter;

public enum AppApplicationType implements AfEnumBase{
    APP(1,"版本应用类型"),
    APP_DATA(2,"版本应用数据"),
    APP_QUICK_ENTRY(3,"版本快捷入口数据"),
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

    AppApplicationType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (AppApplicationType statusType : AppApplicationType.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }

    public static Integer getCodeByDesc(String desc) {
        for (AppApplicationType statusType : AppApplicationType.values()) {
            if (statusType.desc.equals(desc)) {
                return statusType.code;
            }
        }
        return null;
    }
}
