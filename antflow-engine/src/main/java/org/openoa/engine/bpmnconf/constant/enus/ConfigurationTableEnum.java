package org.openoa.engine.bpmnconf.constant.enus;

import lombok.Getter;

import java.util.Objects;

/**
 * 配置表枚举
 */
public enum ConfigurationTableEnum {

    COMPANY_FINANCE(1, "财务审核流程配置表"),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    ConfigurationTableEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ConfigurationTableEnum getInstanceByCode(Integer code){
        for (ConfigurationTableEnum value : ConfigurationTableEnum.values()) {
            if(Objects.equals(value.code,code)){
                return value;
            }
        }
        return null;
    }
}
