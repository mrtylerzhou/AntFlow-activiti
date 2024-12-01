package org.openoa.engine.conf.engineconfig;

import org.activiti.engine.impl.cfg.multitenant.TenantAwareDataSource;
import org.activiti.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantAwareDataSourceConfig {
    @Bean
    public TenantAwareDataSource tenantAwareDataSource(TenantInfoHolder tenantInfoHolder){
        TenantAwareDataSource tenantAwareDataSource = new TenantAwareDataSource(tenantInfoHolder);
        return tenantAwareDataSource;
    }

}
