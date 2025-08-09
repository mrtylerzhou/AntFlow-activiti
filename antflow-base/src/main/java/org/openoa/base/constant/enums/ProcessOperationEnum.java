package org.openoa.base.constant.enums;

import lombok.Getter;

public enum ProcessOperationEnum {
    BUTTON_TYPE_SUBMIT(1 ,"流程提交"),
    BUTTON_TYPE_RESUBMIT(2, "重新提交"),
    BUTTON_TYPE_AGREE(3,"同意"),
    BUTTON_TYPE_DIS_AGREE(4,"不同意"),
    BUTTON_TYPE_VIEW_BUSINESS_PROCESS(5,"查看流程详情"),
    BUTTON_TYPE_ABANDON(7,"作废"),
    BUTTON_TYPE_UNDERTAKE(10, "承办"),
    BUTTON_TYPE_CHANGE_ASSIGNEE(11,  "变更处理人"),
    BUTTON_TYPE_STOP(12,  "终止"),
    BUTTON_TYPE_FORWARD(15,  "转发"),
    BUTTON_TYPE_BACK_TO_MODIFY(18, "退回修改"),
    BUTTON_TYPE_JP(19,"加批"),
    BUTTON_TYPE_ZB(21,"转办"),
    BUTTON_TYPE_CHOOSE_ASSIGNEE(22,"自选审批人"),
    BUTTON_TYPE_BACK_TO_ANY_NODE(23,"退回任意节点"),
    BUTTON_TYPE_REMOVE_ASSIGNEE(24,"减签"),
    BUTTON_TYPE_ADD_ASSIGNEE(25,"加签"),//加批生成了新的节点,加签在当前节点增加审批人
    BUTTON_TYPE_CHANGE_FUTURE_ASSIGNEE(26,"变更未来节点审批人"),
    BUTTON_TYPE_REMOVE_FUTURE_ASSIGNEE(27,"未来节点减签"),
    BUTTON_TYPE_ADD_FUTURE_ASSIGNEE(28,"未来节点加签"),
    BUTTON_TYPE_PROCESS_DRAW_BACK(29,"流程撤回")
    ;

    @Getter
    private Integer code;

    @Getter
    private static final String outSideMarker = "outSide";
    @Getter
    private static final String outSideAccessmarker = "outSideAccess";
    @Getter
    private String desc;


    ProcessOperationEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;

    }

    /**
     * 根据Code获得付款单推送审核流程类型枚举
     *
     * @param code
     * @return
     */
    public static ProcessOperationEnum getEnumByCode(Integer code) {

        for (ProcessOperationEnum operation : ProcessOperationEnum.values()) {
            if (operation.getCode().equals(code)) {
                return operation;
            }
        }
        return null;
    }

    /**
     * 根据Code获得描述
     *
     * @param code
     * @return
     */
    public static String getDescByCode(Integer code) {

        for (ProcessOperationEnum operation : ProcessOperationEnum.values()) {
            if (operation.getCode().equals(code)) {
                return operation.getDesc();
            }
        }
        return null;
    }

}