package org.openoa.engine.conf.engineconfig;

import org.activiti.engine.impl.cfg.multitenant.TenantIdHolder;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.util.ThreadLocalContainer;
import org.springframework.stereotype.Component;

@Component
public class MultiTenantIdHolder implements TenantIdHolder {
    @Override
    public void setCurrentTenantId(String tenantId) {
        ThreadLocalContainer.set(StringConstants.TENANT_ID,tenantId);
    }

    @Override
    public String getCurrentTenantId() {
        Object value = ThreadLocalContainer.get(StringConstants.TENANT_ID);
        return value==null||StringConstants.DEFAULT_TENANT.equalsIgnoreCase(value.toString())?"":value.toString();
    }

    @Override
    public void clearCurrentTenantId() {
        ThreadLocalContainer.remove(StringConstants.TENANT_ID);
    }
}
