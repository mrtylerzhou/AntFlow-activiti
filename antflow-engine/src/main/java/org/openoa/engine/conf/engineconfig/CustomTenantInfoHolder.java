package org.openoa.engine.conf.engineconfig;

import org.activiti.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.util.ThreadLocalContainer;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomTenantInfoHolder implements TenantInfoHolder {

    private final Map<String, DataSource> tenantDataSources = new HashMap<>();

    public void addTenant(String tenantId, DataSource dataSource) {
        tenantDataSources.put(tenantId, dataSource);
    }

    public DataSource getDataSource(String tenantId) {
        return tenantDataSources.get(tenantId);
    }

    @Override
    public Collection<String> getAllTenants() {
        return tenantDataSources.keySet();
    }

    @Override
    public void setCurrentTenantId(String tenantId) {
        ThreadLocalContainer.set(StringConstants.TENANT_USER,tenantId);
    }

    @Override
    public String getCurrentTenantId() {
        Object value = ThreadLocalContainer.get(StringConstants.TENANT_USER);
        return value==null?"":value.toString();
    }


    public void clearCurrentTenantId() {
       ThreadLocalContainer.remove(StringConstants.TENANT_USER);
    }
}
