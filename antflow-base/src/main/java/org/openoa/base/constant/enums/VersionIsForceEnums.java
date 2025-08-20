package org.openoa.base.constant.enums;

import lombok.Getter;

public enum VersionIsForceEnums implements AfEnumBase{

    /**
     * is forece to update 0 no 1 yes
     */
    //权限资源类型
    RECRUIT_TYPE_ZSYG(0, "否"),
    RECRUIT_TYPE_SYYG(1, "是"),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    VersionIsForceEnums(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(int code) {
        for (VersionIsForceEnums statusType : VersionIsForceEnums.values()) {
            if (statusType.code == code) {
                return statusType.desc;
            }
        }
        return null;
    }

    public static Integer getCodeByDesc(String desc) {
        for (VersionIsForceEnums statusType : VersionIsForceEnums.values()) {
            if (statusType.desc.equals(desc)) {
                return statusType.code;
            }
        }
        return null;
    }
}
