package org.openoa.base.constant.enums;

import lombok.Getter;

/**
 * @Classname ButtonTypeEnum
 * @since 0.5
 * @Created by AntOffice
 */
public enum ButtonTypeEnum {
    BUTTON_TYPE_PREVIEW(0, "预览"),
    BUTTON_TYPE_SUBMIT(1, "提交"),
    BUTTON_TYPE_RESUBMIT(2, "重新提交"),
    BUTTON_TYPE_AGREE(3, "同意"),
    BUTTON_TYPE_DISAGREE(4, "不同意"),
    BUTTON_TYPE_BACK_TO_PREV_MODIFY(6, "退回上节点修改"),
    BUTTON_TYPE_ABANDONED(7, "作废"),
    BUTTON_TYPE_PRINT(8, "打印"),
    BUTTON_TYPE_UNDERTAKE(10, "承办"),
    BUTTON_TYPE_CHANGE_ASSIGNEE(11, "变更处理人"),
    BUTTON_TYPE_STOP(12, "终止"),
    BUTTON_TYPE_ADD_ASSIGNEE(13, "添加审批人"),
    BUTTON_TYPE_FORWARD(15, "转发"),
    BUTTON_TYPE_BACK_TO_MODIFY(18, "退回修改"),
    BUTTON_TYPE_JP(19,"加批"),
    BUTTON_TYPE_ZB(21,"转办"),
    BUTTON_TYPE_CHOOSE_ASSIGNEE(22,"自选审批人"),
    BUTTON_TYPE_BACK_TO_ANY_NODE(23,"退回任意节点"),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    ButtonTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ButtonTypeEnum buttonTypeEnum : ButtonTypeEnum.values()) {
            if (buttonTypeEnum.getCode().equals(code)) {
                return buttonTypeEnum.getDesc();
            }
        }
        return null;
    }
}
