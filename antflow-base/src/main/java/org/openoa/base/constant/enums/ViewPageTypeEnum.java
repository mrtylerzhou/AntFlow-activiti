package org.openoa.base.constant.enums;

/**
 * @Classname ViewPageTypeEnum
 * @Description TODO
 * @Date 2021-11-04 7:18
 * @Created by AntOffice
 */
public enum ViewPageTypeEnum {
    VIEW_PAGE_TYPE_START(1, "发起人"),
    VIEW_PAGE_TYPE_OTHER(2, "其他审批人"),
    ;


    private Integer code;
    private String desc;


    ViewPageTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
