package org.openoa.engine.conf.engineconfig;

import org.activiti.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.util.ThreadLocalContainer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collection;

@Component
public class MultiTenantInfoHolder extends TenantDataSourceRegistry implements TenantInfoHolder {



    public DataSource getDataSource(String tenantId) {
        return super.getDataSource(tenantId);
    }

    @Override
    public Collection<String> getAllTenants() {
        return dataSources.keySet();
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
