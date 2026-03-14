package org.openoa.base.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprovalStandardEnum implements AfEnumBase{
    START_USER(1, "发起人"),
    APPROVAL(2, "被审批人"),
    FROM_PREV_NODE(3,"上一节点审批人的")
    ;
    private final Integer code;
    private final String desc;

    public static ApprovalStandardEnum getByCode(Integer code) {
        for (ApprovalStandardEnum approvalStandardEnum : ApprovalStandardEnum.values()) {
            if (approvalStandardEnum.getCode().equals(code)) {
                return approvalStandardEnum;
            }
        }
        return null;
    }
    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
