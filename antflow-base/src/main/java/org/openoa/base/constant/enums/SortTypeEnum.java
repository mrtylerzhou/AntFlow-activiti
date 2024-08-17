package org.openoa.base.constant.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum SortTypeEnum {

    ASC(1, " ASC", true),
    DESC(2, " DESC", false),
    FIELD(3, StringUtils.EMPTY, false);


    @Getter
    private Integer code;

    @Getter
    private String mark;

    @Getter
    private boolean isAsc;

    SortTypeEnum(Integer code, String mark, boolean isAsc) {
        this.code = code;
        this.mark = mark;
        this.isAsc = isAsc;
    }

    public static SortTypeEnum getSortTypeEnumByCode(Integer code) {
        for (SortTypeEnum statusType : SortTypeEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType;
            }
        }
        return null;
    }
}
