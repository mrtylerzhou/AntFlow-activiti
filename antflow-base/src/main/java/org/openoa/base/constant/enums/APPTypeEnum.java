package org.openoa.base.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum APPTypeEnum implements  AfEnumBase {
    ANDROID(1,"android","安卓"),
    IOS(2,"ios","苹果"),
    HARMONY_OS(3,"harmony_os","鸿蒙"),
    ;
    private final Integer code;
    private final String name;
    private final String desc;
    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
