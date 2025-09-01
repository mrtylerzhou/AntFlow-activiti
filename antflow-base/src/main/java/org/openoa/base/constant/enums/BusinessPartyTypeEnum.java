package org.openoa.base.constant.enums;

import lombok.Getter;

public enum BusinessPartyTypeEnum implements AfEnumBase{

    BUSINESS_PARTY_TYPE_Embed(1, "嵌入式"),
    BUSINESS_PARTY_TYPE_ACCESS(2, "调入式"),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    BusinessPartyTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (BusinessPartyTypeEnum typeEnum : BusinessPartyTypeEnum.values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum.getDesc();
            }
        }
        return null;
    }
}
