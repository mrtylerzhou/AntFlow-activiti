package org.openoa.base.constant.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum VariantFormContainerTypeEnum {
    CARD("card","卡片"),
    TAB("tab","标签页"),
    TABLE("table","表格"),
    GRID("grid","栅格")
    ;
    private final String typeName;
    private final String desc;

    VariantFormContainerTypeEnum(String typeName, String desc){

        this.typeName = typeName;
        this.desc = desc;
    }
    public static VariantFormContainerTypeEnum getByTypeName(String typeName){
        if(StringUtils.isEmpty(typeName)){
            return null;
        }
        for (VariantFormContainerTypeEnum value : VariantFormContainerTypeEnum.values()) {
            if(value.getTypeName().equals(typeName)){
                return value;
            }
        }
        return null;
    }
}
