package org.openoa.base.constant.enums;

public enum MessageLimit implements AfEnumBase{
    EMAIL_A_DAY(80, "每日每人最多收取");

    private int code;
    private String desc;

    MessageLimit(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (MessageLimit bizType : MessageLimit.values()) {
            if (bizType.code == code) {
                return bizType.desc;
            }
        }
        return null;
    }


}
