package org.openoa.base.constant.enums;

import lombok.Getter;
import org.apache.commons.lang3.EnumUtils;

import java.util.Collections;
import java.util.List;

@Getter
public enum LFFieldTypeEnum implements AfEnumBase{
    STRING(1,"字符串"),//string
    NUMBER(2,"数字"),//int
    DATE(3,"日期"),//date
    DATE_TIME(4,"日期时间"),//time
    TEXT(5,"长字符串"),//text/string
    BOOLEAN(6,"布尔"),//boolean
    BLOB(7,"二进制"),//byte
    ;
    private static LFFieldTypeEnum[] allInstances;
    private final Integer type;
    private final String desc;

    LFFieldTypeEnum(Integer type, String desc){

        this.type = type;
        this.desc = desc;
    }
    public static LFFieldTypeEnum getByType(Integer type){
        if(type==null){
            return null;
        }
       if(allInstances==null){
           allInstances=LFFieldTypeEnum.values();
       }
        for (LFFieldTypeEnum lfFieldTypeEnum : allInstances) {
            if(lfFieldTypeEnum.getType().equals(type)){
                return lfFieldTypeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getCode() {
        return this.type;
    }
}
