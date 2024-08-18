package org.openoa.base.constant.enums;

import lombok.Getter;

/**
 * process not agree type
 */
public enum ProcessDisagreeTypeEnum {

    //node submit type (1:Return to the previous node to submit the next node
    // 2:Return to the initiator to submit the next node
    // 3:Return to the initiator to submit the retreat node
    // 4:Return to the historical node to submit the next node
    // 5:Return to the historical node to submit the retreat node)
    ONE_DISAGREE(1, "不同意1"),
    TWO_DISAGREE(2, "不同意2"),
    THREE_DISAGREE(3, "不同意3"),
    FOUR_DISAGREE(4, "不同意4"),
    FIVE_DISAGREE(5, "不同意5"),
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

    ProcessDisagreeTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ProcessDisagreeTypeEnum statusType : ProcessDisagreeTypeEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }

    public static Integer getCodeByDesc(String desc) {
        for (ProcessDisagreeTypeEnum statusType : ProcessDisagreeTypeEnum.values()) {
            if (statusType.desc.equals(desc)) {
                return statusType.code;
            }
        }
        return null;
    }
}
