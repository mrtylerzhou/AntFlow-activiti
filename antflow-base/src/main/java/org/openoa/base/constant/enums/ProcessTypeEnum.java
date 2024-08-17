package org.openoa.base.constant.enums;

import lombok.Getter;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-02 16:30
 * @Param
 * @return
 * @Version 1.0
 */
public enum ProcessTypeEnum {
    /**
     * process type
     */
    VIEW_TYPE(1,"查看流程"),
    MONITOR_TYPE(2,"监控流程"),
    LAUNCH_TYPE(3,"新建流程"),
    PARTIC_TYPE(4,"已办流程"),
    ENTRUST_TYPE(5,"代办流程"),
    DRAFT_TYPE(6,"草稿流程"),
    ADMIN_TYPE(8,"流程管理"),

    ALL_TYPE(10,"APP查询代办，新建，已办流程");
    @Getter
    private Integer code;

    @Getter
    private String desc;
    public int getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
    ProcessTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ProcessTypeEnum statusType : ProcessTypeEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }
}
