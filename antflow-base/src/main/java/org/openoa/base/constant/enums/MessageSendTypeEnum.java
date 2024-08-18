package org.openoa.base.constant.enums;

import lombok.Getter;

public enum MessageSendTypeEnum {

    MAIL(1, "邮件"), MESSAGE(2, "短信"), PUSH(3, "APP-PUSH"), ALL(4, "所有类型");

    @Getter
    private Integer code;

    @Getter
    private String desc;

    MessageSendTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据Code获得枚举
     *
     * @param code
     * @return
     */
    public static MessageSendTypeEnum getEnumByCode(Integer code) {
        for (MessageSendTypeEnum typeEnum : MessageSendTypeEnum.values()) {
            if (code.equals(typeEnum.getCode())) {
                return typeEnum;
            }
        }
        return null;
    }
}
