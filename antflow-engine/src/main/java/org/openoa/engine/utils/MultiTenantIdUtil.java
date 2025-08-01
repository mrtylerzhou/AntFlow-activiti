package org.openoa.engine.utils;

import org.openoa.base.util.SpringBeanUtils;
import org.openoa.engine.conf.engineconfig.MultiTenantIdHolder;

public class MultiTenantIdUtil {
    public static String getCurrentTenantId(){
        MultiTenantIdHolder tenantIdHolder = SpringBeanUtils.getBean(MultiTenantIdHolder.class);
        String currentTenantId = tenantIdHolder.getCurrentTenantId();
        return currentTenantId;
    }
}
