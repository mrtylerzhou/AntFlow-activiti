package org.openoa.base.constant.enums;

import lombok.Getter;

public enum ProcessSubmitStateEnum {
    /**
     * process submit status
     */
    PROCESS_SUB_TYPE(1, "流程提交状态"),
    PROCESS_AGRESS_TYPE(2, "流程同意状态"),
    PROCESS_NO_AGRESS_TYPE(3, "流程不同意状态"),
    WITHDEAW_AGRESS_TYPE(4, "流程撤回状态"),
    END_AGRESS_TYPE(5, "流程作废状态"),
    CRMCEL_AGRESS_TYPE(6, "流程终止状态"),
    WITHDEAW_DISAGREE_TYPE(7, "back"),
    PROCESS_UPDATE_TYPE(8, "退回修改"),
    PROCESS_SIGN_UP(9, "加批"),
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

    ProcessSubmitStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ProcessSubmitStateEnum statusType : ProcessSubmitStateEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }
}
