package org.openoa.base.constant.enums;

import lombok.Getter;

public enum ProcessEnum {
    /**
     * process category
     */
    AGENCY_TYPE(1, "委托流程"),
    CIRCULATE_TYPE(2, "传阅流程"),
    LAUNCH_TYPE(3, "发起流程"),
    PARTIC_TYPE(4, "参与流程"),
    ENTRUST_TYPE(5, "代办流程"),
    SUPER_TYPE(6, "流程代办流程"),
    /**
     * process type
     */
    HOST_TYPE(1, "承办类型"),
    REVOKE_TYPE(2, "撤销类型"),
    WITHDRAW_TYPE(3, "撤回类型"),
    NOTICE_TYPE(4, "通知类型"),
    CIRCULATE_START_TYPE(5, "传阅类型"),
    /**
     * process handle state
     */
    HANDLE_STATE(2, "审批通过"),
    COMLETE_STATE(1, "审批中"),
    CANCEL_STATE(3, "已取消"),

    /**
     *process node
     */
    START_TASK(71, "START_TASK_KEY"),
    START_TASK_KEY(71, "task1418018332271"),
    TOW_TASK_KEY(72, "task1418018332272"),
    THREE_TASK_KEY(73, "task1418018332273"),
    FOUR_TASK_KEY(74, "task1418018332274"),
    FIVE_TASK_KEY(75, "task1418018332275"),
    SIX_TASK_KEY(76, "task1418018332276"),
    SEVEN_TASK_KEY(77, "task1418018332277"),
    EIGHT_TASK_KEY(78, "task1418018332278"),
    NINE_TASK_KEY(79, "task1418018332279"),
    HTN_TASK_KEY(80, "task1418018332280"),
    THIRTEEN_TASK_KEY(81, "task1418018332281"),
    FOURTTEEN_TASK_KEY(82, "task1418018332282"),
    FIFTTEEN_TASK_KEY(83, "task1418018332283"),
    ELEVEN_TASK_KEY(90, "task14180183322"),
    TWELVE_TASK_KEY(93, "task14180183322"),
    //node key
    NODE_KEY(73, "task14180183322"),
    //process manager
    PROC_MAN(81, "process_manager"),
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

    ProcessEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ProcessEnum statusType : ProcessEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }
}