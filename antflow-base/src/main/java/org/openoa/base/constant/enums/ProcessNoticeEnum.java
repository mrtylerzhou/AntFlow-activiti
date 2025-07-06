package org.openoa.base.constant.enums;

import lombok.Getter;

public enum ProcessNoticeEnum {
    /**
     * 流程审批状态
     */
    EMAIL_TYPE(1,"邮件"),
    PHONE_TYPE(2,"短信"),
    APP_TYPE(3,"app推送"),
    WECHAT_TYPE(5,"企微"),
    DING_TALK_TYPE(6,"钉钉"),
    FEISHU_TYPE(7,"飞书"),
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
    ProcessNoticeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ProcessNoticeEnum statusType : ProcessNoticeEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }
}
