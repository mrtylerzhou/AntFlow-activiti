package org.openoa.engine.utils;

import org.openoa.base.util.SpringBeanUtils;
import org.openoa.engine.conf.engineconfig.MultiTenantIdHolder;

public class MultiTenantUtil {
    public static String getCurrentTenantId(){
        MultiTenantIdHolder tenantIdHolder = SpringBeanUtils.getBean(MultiTenantIdHolder.class);
        String currentTenantId = tenantIdHolder.getCurrentTenantId();
        return currentTenantId;
    }
    //严格模式,租户只能使用自身的配置(默认租户仍然可以使用全部),非严格模式租户可以共用配置,但是业务数据,包括在流程引擎里的数据都是分离的
    //一句话,严格和非严格模式的差别仅限于是否共用配置
    public static boolean strictTenantMode(){
        String property = PropertyUtil.getProperty("antflow.multitenant.strict");
        return "true".equalsIgnoreCase(property);
    }
}
