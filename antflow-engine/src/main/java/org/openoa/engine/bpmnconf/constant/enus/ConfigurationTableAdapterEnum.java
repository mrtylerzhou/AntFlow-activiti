package org.openoa.engine.bpmnconf.constant.enus;

import lombok.Getter;
/**
 * 配置表枚举
 */
public enum ConfigurationTableAdapterEnum {
    FINANCE_CASHER_ADP(BusinessConfTableFieldEnum.FINANCE_CASHER),
    FINANCE_CASHER_SUPERVISOR_ADP(BusinessConfTableFieldEnum.FINANCE_CASHIER_SUPERVISOR),
    FINANCE_MANAGER_ADP(BusinessConfTableFieldEnum.FINANCE_MANAGER),
    FINANCE_CASHER_MANAGER_ADP(BusinessConfTableFieldEnum.FINANCE_CASHER_MANAGER),
    FINANCE_CASHER_DIRECTOR_ADP(BusinessConfTableFieldEnum.FINANCE_CASHER_DIRECTOR),
    FINANCE_CFO_ADP(BusinessConfTableFieldEnum.FINANCE_CFO),

    ;

    @Getter
    private BusinessConfTableFieldEnum tableFieldEnum;


    ConfigurationTableAdapterEnum(BusinessConfTableFieldEnum tableFieldEnum) {
        this.tableFieldEnum = tableFieldEnum;
    }

    public static ConfigurationTableAdapterEnum getByTableFieldEnum(BusinessConfTableFieldEnum tableFieldEnum) {
        for (ConfigurationTableAdapterEnum value : ConfigurationTableAdapterEnum.values()) {
            if(value.getTableFieldEnum()==tableFieldEnum){
                return value;
            }
        }
        return null;
    }

}
