package org.openoa.engine.conf.engineconfig;

import org.activiti.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomTenantInfoHolder implements TenantInfoHolder {

    private final ThreadLocal<String> currentTenantId = new ThreadLocal<>();
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
        currentTenantId.set(tenantId);
    }

    @Override
    public String getCurrentTenantId() {
        return currentTenantId.get();
    }


    public void clearCurrentTenantId() {
        currentTenantId.remove();
    }
}
