package org.openoa.base.constant.enums;

import lombok.Getter;

/**
 * @Author TylerZhou
 * @Date 2024/7/27 19:04
 * @Version 1.0
 */
@Getter
public enum HrbpTypeEnum {
    HRBP(0,"hrbp"),
    HRBP_LEADER(2,"hrbp leader")
    ;
    private final Integer code;
    private final String desc;

    HrbpTypeEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
