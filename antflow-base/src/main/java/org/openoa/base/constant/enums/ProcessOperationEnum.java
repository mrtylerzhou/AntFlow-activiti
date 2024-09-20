package org.openoa.base.constant.enums;

import lombok.Getter;

public enum ProcessOperationEnum {
    BUTTON_TYPE_SUBMIT(1 ,"流程提交"),
    BUTTON_TYPE_RESUBMIT(2, "重新提交"),
    BUTTON_TYPE_AGREE(3,"同意"),
    BUTTON_TYPE_DIS_AGREE(4,"不同意"),
    BUTTON_TYPE_ABANDON(7,"作废"),
    BUTTON_TYPE_UNDERTAKE(10, "承办"),
    BUTTON_TYPE_CHANGE_ASSIGNEE(11,  "变更处理人"),
    BUTTON_TYPE_STOP(12,  "终止"),
    BUTTON_TYPE_FORWARD(15,  "转发"),//todo
    BUTTON_TYPE_BACK_TO_MODIFY(18, "打回修改"),
    BUTTON_TYPE_JP(19,"加批"),
    BUTTON_TYPE_ZB(21,"转办"),
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