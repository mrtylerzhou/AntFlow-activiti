package org.openoa.base.constant.enums;

import lombok.Getter;

public enum ProcessButtonEnum implements AfEnumBase{
    //disagree
    DISAGREE_TYPE(1, "不同意"),
    //agree
    AGREE_TYPE(2, "同意"),
    //withdraw
    WITHDRAW_TYPE(3, "撤回"),
    //end
    END_TYPE(4, "终止"),
    //delete
    DELETE_TYPE(5, "作废"),
    //change assignee
    CHANGE_TYPE(6, "变更处理人"),
    //entrust
    HANDLE_TYPE(7, "代审批"),
    //printing
    PRINTRING_TYPE(8, "打印"),
    CEO_TYPE(9, "提交CEO审批"),
    //forward
    FORWARD_TYPE(10, "转发"),
    //
    ACTING_TYPE(11, "代审批不同意"),
    //undertake
    UNDERTAKE_TYPE(12, "承办"),

    JOINTLY_SIGN(14, "会签"),

    GET_BACK(15, "返回"),

    ADD_BATCH(16, "增加审批人"),
    STAFF_CONFIRM_TYPE(17, "代员工确认"),

    VIEW_TYPE(1, "查看类型"),
    DEAL_WITH_TYPE(2, "处理类型"),

    MAIN_COLOR(1, "primary"),

    DEFAULT_COLOR(2, "default"),
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

    ProcessButtonEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ProcessButtonEnum statusType : ProcessButtonEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }

    public static Integer getCodeByDesc(String desc) {
        for (ProcessButtonEnum statusType : ProcessButtonEnum.values()) {
            if (statusType.desc.equals(desc)) {
                return statusType.code;
            }
        }
        return null;
    }
}
