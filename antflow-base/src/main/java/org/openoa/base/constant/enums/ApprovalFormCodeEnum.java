package org.openoa.base.constant.enums;

import lombok.Getter;

/**
 * 流程标题 及app头像 展示被审批人
 *
 * @author tylerZhou
 * @date 2022/02/25 14:08
 */
public enum ApprovalFormCodeEnum {

    ;

    @Getter
    private String formCode;

    @Getter
    private String desc;

    ApprovalFormCodeEnum(String formCode, String desc) {
        this.formCode = formCode;
        this.desc = desc;
    }

    public static ApprovalFormCodeEnum getEnumByCode(String formCode) {
        for (ApprovalFormCodeEnum item : ApprovalFormCodeEnum.values()) {
            if (item.formCode.equals(formCode)) {
                return item;
            }
        }
        return null;
    }

    public static boolean exist(String formCode) {
        for (ApprovalFormCodeEnum item : ApprovalFormCodeEnum.values()) {
            if (item.formCode.equals(formCode)) {
                return true;
            }
        }
        return false;
    }

}
