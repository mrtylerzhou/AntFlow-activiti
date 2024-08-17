package org.openoa.base.constant.enums;

import lombok.Getter;

public enum AdminPersonnelTypeEnum {

    ADMIN_PERSONNEL_TYPE_PROCESS(1, "流程管理员", "processAdminsStr", "processAdmins", "processAdminIds", "YWFLCGL"),
    ADMIN_PERSONNEL_TYPE_APPLICATION(2, "应用管理员", "applicationAdminsStr", "applicationAdmins", "applicationAdminIds", "YWFYYGL"),
    ADMIN_PERSONNEL_TYPE_INTERFACE(3, "接口管理员", "interfaceAdminsStr", "interfaceAdmins", "interfaceAdminIds", "YWFJKGL"),
    ADMIN_PERSONNEL_TYPE_TEMPLATE(4, "条件模板管理员", "templateAdminsStr", "templateAdmins", "templateAdminIds", "YWFMBGL"),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    @Getter
    private String strField;

    @Getter
    private String listField;

    @Getter
    private String idsField;

    @Getter
    private String permCode;

    AdminPersonnelTypeEnum(Integer code, String desc, String strField, String listField, String idsField, String permCode) {
        this.code = code;
        this.desc = desc;
        this.strField = strField;
        this.listField = listField;
        this.idsField = idsField;
        this.permCode = permCode;
    }

    public static AdminPersonnelTypeEnum getEnumByType(Integer code) {
        for (AdminPersonnelTypeEnum personnelTypeEnum : AdminPersonnelTypeEnum.values()) {
            if (code.equals(personnelTypeEnum.getCode())) {
                return personnelTypeEnum;
            }
        }
        return null;
    }

    public static AdminPersonnelTypeEnum getEnumByPermCode(String permCode) {
        for (AdminPersonnelTypeEnum personnelTypeEnum : AdminPersonnelTypeEnum.values()) {
            if (permCode.equals(personnelTypeEnum.getPermCode())) {
                return personnelTypeEnum;
            }
        }
        return null;
    }
}
