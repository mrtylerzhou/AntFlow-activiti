package org.openoa.base.constant.enums;

import lombok.Getter;

public enum DeduplicationTypeEnum implements AfEnumBase{

    DEDUPLICATION_TYPE_NULL(1, "不去重"),
    DEDUPLICATION_TYPE_FORWARD(2, "当一个审批人重复出现时，只在最后一次审批（前去重）"),
    DEDUPLICATION_TYPE_BACKWARD(3, "当一个审批人重复出现时，只在第一次审批（后去重）"),
    DEDUPLICATION_TYPE_SKIP_NEXT(4,"当一个审批人仅在相邻节点重复出现时,后续自动同意")
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    DeduplicationTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (DeduplicationTypeEnum enums : DeduplicationTypeEnum.values()) {
            if (enums.code.equals(code)) {
                return enums.desc;
            }
        }
        return null;
    }
}
