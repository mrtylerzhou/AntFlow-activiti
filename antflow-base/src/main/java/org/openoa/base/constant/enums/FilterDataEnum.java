package org.openoa.base.constant.enums;

import lombok.Getter;
import org.openoa.base.adp.FilterDataAdaptor;
import org.openoa.base.adp.ProcessDeptFilterDataAdp;
import org.openoa.base.adp.SecurityAccountDeviceFilterDataAdp;

//todo
public enum FilterDataEnum {

    FD_SECURITY_ACCOUNT_DEVICE(1, "账号与设备关联表漏斗数据", SecurityAccountDeviceFilterDataAdp.class),
    FD_PROCESS_CONF(11,"流程配置管理", ProcessDeptFilterDataAdp.class),
    ;
    @Getter
    private Integer code;

    @Getter
    private String desc;

    @Getter
    private Class<? extends FilterDataAdaptor> filterDataService;

    FilterDataEnum(Integer code, String desc, Class<? extends FilterDataAdaptor> filterDataService) {
        this.code = code;
        this.desc = desc;
        this.filterDataService = filterDataService;
    }

    /**
     * 根据code获得对应筛选漏斗数据适配层实现类
     *
     * @param code
     * @return
     */
    public static Class<? extends FilterDataAdaptor> getFilterDataServiceByCode(Integer code) {
        for (FilterDataEnum filterDataEnum : FilterDataEnum.values()) {
            if (filterDataEnum.code.intValue() == code.intValue()) {
                return filterDataEnum.getFilterDataService();
            }
        }
        return null;
    }
}
