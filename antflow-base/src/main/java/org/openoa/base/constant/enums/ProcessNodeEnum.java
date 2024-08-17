package org.openoa.base.constant.enums;

import lombok.Getter;

/**
 * process node enum
 */
public enum ProcessNodeEnum {

    START_TASK_KEY(1, "task1418018332271"),
    TOW_TASK_KEY(2, "task1418018332272"),
    THREE_TASK_KEY(3, "task1418018332273"),
    FOUR_TASK_KEY(4, "task1418018332274"),
    FIVE_TASK_KEY(5, "task1418018332275"),
    SIX_TASK_KEY(6, "task1418018332276"),
    SEVEN_TASK_KEY(7, "task1418018332277"),
    EIGHT_TASK_KEY(8, "task1418018332278"),
    NINE_TASK_KEY(9, "task1418018332279"),
    HTN_TASK_KEY(10, "task1418018332280"),
    THIRTEEN_TASK_KEY(11, "task1418018332281"),
    FOURTTEEN_TASK_KEY(12, "task1418018332282"),
    FIFTTEEN_TASK_KEY(13, "task1418018332283"),
    FOURTEEN_TASK_KEY(14, "task1418018332284"),
    SEX_TASK_KEY(15, "task1418018332285"),
    SEXTEEN_TASK_KEY(16, "task1418018332286"),
    SEVENTEEN_TASK_KEY(17, "task1418018332287"),
    EIGHTEEN_TASK_KEY(18, "task1418018332288"),
    NIGHTEEN_TASK_KEY(19, "task1418018332289"),
    TWENTY_TASK_KEY(20, "task1418018332290"),
    TWENTYONE_TASK_KEY(21, "task1418018332291"),
    TWENTYTWO_TASK_KEY(22, "task1418018332292"),
    TWENTYTHREE_TASK_KEY(23, "task1418018332293"),
    TWENTYFOUR_TASK_KEY(24, "task1418018332294"),
    TWENTYFIVE_TASK_KEY(25, "task1418018332295"),

    TWENTYSIX_TASK_KEY(26, "task1418018332296"),
    TWENTYSEVEN_TASK_KEY(27, "task1418018332297"),
    TWENTYEIGHT_TASK_KEY(28, "task1418018332298"),
    TWENTYNINE_TASK_KEY(29, "task1418018332299"),
    THIRTY_TASK_KEY(30, "task1418018332300"),
    THIRTYONE_TASK_KEY(31, "task1418018332301"),

    THIRTYTWO_TASK_KEY(32, "task1418018332302"),
    THIRTYTHREE_TASK_KEY(33, "task1418018332303"),
    THIRTYFOUR_TASK_KEY(34, "task1418018332304"),
    THIRTYFIVE_TASK_KEY(35, "task1418018332305"),
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

    ProcessNodeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ProcessNodeEnum statusType : ProcessNodeEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }

    public static Integer getCodeByDesc(String desc) {
        for (ProcessNodeEnum statusType : ProcessNodeEnum.values()) {
            if (statusType.desc.equals(desc)) {
                return statusType.code;
            }
        }
        return null;
    }
}
