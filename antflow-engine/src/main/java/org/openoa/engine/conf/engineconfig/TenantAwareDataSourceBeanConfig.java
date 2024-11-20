package org.openoa.engine.conf.engineconfig;

import org.activiti.engine.impl.cfg.multitenant.TenantAwareDataSource;
import org.activiti.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantAwareDataSourceBeanConfig {
    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Bean
    public TenantAwareDataSource tenantAwareDataSource(TenantInfoHolder tenantInfoHolder){
       return new TenantAwareDataSource(tenantInfoHolder);
    }


    @Bean
    public CustomTenantInfoHolder tenantInfoHolder() {
        CustomTenantInfoHolder tenantInfoHolder = new CustomTenantInfoHolder();
        // 添加租户数据源
        tenantInfoHolder.addTenant("",dataSourceFactory.createDataSource("jdbc:mysql://124.222.106.239:3306/jimuoffice?nullCatalogMeansCurrent=true","root","1qazxdr5432"));
        tenantInfoHolder.addTenant("tenantA",dataSourceFactory.createDataSource("jdbc:mysql://localhost:3306/tenanta", "root", "dsb0004699"));
        tenantInfoHolder.addTenant("tenantB", dataSourceFactory.createDataSource("jdbc:mysql://localhost:3306/tenantb", "root", "dsb0004699"));

        return tenantInfoHolder;
    }
}
