package org.openoa.base.constant.enums;

import lombok.Getter;

public enum ErrLevelEnum implements AfEnumBase {

    ERR_LEVEL_ERR(1, "错误"),
    ERR_LEVEL_WORNING(2, "提醒"),;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    ErrLevelEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
