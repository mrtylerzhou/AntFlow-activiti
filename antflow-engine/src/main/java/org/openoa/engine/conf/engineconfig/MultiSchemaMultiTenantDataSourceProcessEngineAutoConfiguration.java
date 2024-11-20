package org.openoa.engine.conf.engineconfig;

import com.zaxxer.hikari.HikariDataSource;
import org.activiti.engine.impl.Condition;
import org.activiti.engine.impl.cfg.TransactionContextFactory;
import org.activiti.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.activiti.engine.impl.cfg.multitenant.TenantAwareDataSource;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.SpringTransactionContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collection;

@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MultiSchemaMultiTenantDataSourceProcessEngineAutoConfiguration extends AbstractProcessEngineAutoConfiguration{
    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Bean
    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public CustomTenantInfoHolder tenantInfoHolder() {
        CustomTenantInfoHolder tenantInfoHolder = new CustomTenantInfoHolder();
        // 添加租户数据源
        tenantInfoHolder.addTenant("tenantA",dataSourceFactory.createDataSource("jdbc:mysql://localhost:3306/tenanta", "root", "dsb0004699"));
        //tenantInfoHolder.addTenant("tenantB", dataSourceFactory.createDataSource("jdbc:mysql://localhost:3306/tenantb", "dsb0004699", "dsb0004699"));

        return tenantInfoHolder;
    }

    @Bean
    public MultiSchemaMultiTenantProcessEngineConfiguration multiTenantProcessEngineConfiguration(CustomTenantInfoHolder tenantInfoHolder,
                                                                                                  DataSource defaultDataSource,
                                                                                                  PlatformTransactionManager transactionManager,
                                                                                                  SpringAsyncExecutor springAsyncExecutor) {
        MultiSchemaMultiTenantProcessEngineConfiguration configuration = new MultiSchemaMultiTenantProcessEngineConfiguration(tenantInfoHolder);
        // 配置默认数据源
        configuration.setDataSource(new TenantAwareDataSource(tenantInfoHolder));
        configuration.setDatabaseType(MultiSchemaMultiTenantProcessEngineConfiguration.DATABASE_TYPE_MYSQL);
        configuration.setDatabaseSchemaUpdate(MultiSchemaMultiTenantProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);
        // 告诉 Activiti 使用外部事务管理器
        configuration.setTransactionsExternallyManaged(true);
        configuration.setAsyncExecutor(springAsyncExecutor);

        // 配置事务上下文工厂
        TransactionContextFactory transactionContextFactory = new SpringTransactionContextFactory(transactionManager);
        configuration.setTransactionContextFactory(transactionContextFactory);
        Collection<String> allTenants = tenantInfoHolder.getAllTenants();
        for (String currentTenant : allTenants) {
            DataSource dataSource = tenantInfoHolder.getDataSource(currentTenant);
            configuration.registerTenant(currentTenant,dataSource);
        }
        return configuration;
    }


    @Configuration
    //@ConditionalOnMissingClass("javax.persistence.EntityManagerFactory")
    @EnableConfigurationProperties(ActivitiProperties.class)
    public static class DataSourceProcessEngineConfiguration extends AbstractProcessEngineAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public SpringProcessEngineConfiguration springProcessEngineConfiguration(
                DataSource dataSource,
                PlatformTransactionManager transactionManager,
                SpringAsyncExecutor springAsyncExecutor) throws IOException {

            return this.baseSpringProcessEngineConfiguration(dataSource, transactionManager, springAsyncExecutor);
        }
        @Bean
        @ConditionalOnMissingBean
        public ProcessEngineFactoryBean processEngineFactoryBean(MultiSchemaMultiTenantProcessEngineConfiguration configuration) {
            return super.springProcessEngineBean(configuration);
        }
    }
}
