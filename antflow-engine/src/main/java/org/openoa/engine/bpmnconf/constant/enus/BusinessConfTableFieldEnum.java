package org.openoa.engine.bpmnconf.constant.enus;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


/**
 * 配置表对应字段枚举
 */
public enum BusinessConfTableFieldEnum {

    FINANCE_CASHER(ConfigurationTableEnum.COMPANY_FINANCE,1,"票据审核"),
    FINANCE_CASHIER_SUPERVISOR(ConfigurationTableEnum.COMPANY_FINANCE,2,"票据复核"),
    FINANCE_MANAGER(ConfigurationTableEnum.COMPANY_FINANCE,3,"资金主管"),
    FINANCE_CASHER_MANAGER(ConfigurationTableEnum.COMPANY_FINANCE,4,"查找公司资金经理角色人员"),
    FINANCE_CASHER_DIRECTOR(ConfigurationTableEnum.COMPANY_FINANCE,5,"查找公司资金总监角色"),
    FINANCE_CFO(ConfigurationTableEnum.COMPANY_FINANCE,16,"查找公司CFO角色人员"),


    ;

    @Getter
    private ConfigurationTableEnum parentTable;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    BusinessConfTableFieldEnum(ConfigurationTableEnum parentTable, Integer code, String desc) {
        this.parentTable = parentTable;
        this.code = code;
        this.desc = desc;
    }

    public static List<BusinessConfTableFieldEnum> getByParentTable(ConfigurationTableEnum parentTable) {
       if(parentTable==null){
           return null;
       }
        List<BusinessConfTableFieldEnum> result=new ArrayList<>();
        for (BusinessConfTableFieldEnum value : BusinessConfTableFieldEnum.values()) {
            if(value.getParentTable().equals(parentTable)){
                result.add(value);
            }
        }
        return result;
    }
    public static BusinessConfTableFieldEnum getTableFieldEnumByCode(Integer code) {
        for (BusinessConfTableFieldEnum businessConfTableFieldEnum : BusinessConfTableFieldEnum.values()) {
            if (businessConfTableFieldEnum.getCode().equals(code)) {
                return businessConfTableFieldEnum;
            }
        }
        return null;
    }

}
