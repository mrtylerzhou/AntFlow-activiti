package org.openoa.engine.conf.engineconfig;

import com.google.common.collect.Lists;
import org.activiti.engine.impl.cfg.TransactionContextFactory;
import org.activiti.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.activiti.engine.impl.cfg.multitenant.TenantAwareDataSource;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.SpringTransactionContextFactory;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.openoa.engine.bpmnconf.activitilistener.GlobalEventListener;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MultiSchemaMultiTenantDataSourceProcessEngineAutoConfiguration extends AbstractProcessEngineAutoConfiguration{

    @Bean
    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


    @Bean
    public MultiSchemaMultiTenantProcessEngineConfiguration multiTenantProcessEngineConfiguration(MultiTenantInfoHolder tenantInfoHolder,
                                                                                                  TenantAwareDataSource tenantAwareDataSource,
                                                                                                  PlatformTransactionManager transactionManager,
                                                                                                  MBPDynamicDataSourceDetector mbpDynamicDataSourceDetector,
                                                                                                  SpringAsyncExecutor springAsyncExecutor) {
        MultiSchemaMultiTenantProcessEngineConfiguration configuration = new MultiSchemaMultiTenantProcessEngineConfiguration(tenantInfoHolder);


        Map<DataSource,String > stringDataSourceMap = mbpDynamicDataSourceDetector.detectMybatisPlusDynamicDataSource();
        if(!CollectionUtils.isEmpty(stringDataSourceMap)){
            int index=0;
            for (Map.Entry<DataSource, String> dataSourceStringEntry : stringDataSourceMap.entrySet()) {
                DataSource dataSource = dataSourceStringEntry.getKey();
                String dataSourceName = dataSourceStringEntry.getValue();
                if(index==0){
                    //默认数据源
                    tenantInfoHolder.registerDataSource("",dataSource);
                }else{
                    tenantInfoHolder.registerDataSource(dataSourceName,dataSource);
                }
                index++;
            }
        }
        // 配置默认数据源
        configuration.setDataSource(tenantAwareDataSource);
        configuration.setDatabaseType(DefaultDataBaseTypeDetector.detectDataSourceDbType(tenantInfoHolder.getDefaultDataSource()));
        configuration.setDatabaseSchemaUpdate(MultiSchemaMultiTenantProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);
        // 告诉 Activiti 使用外部事务管理器
        configuration.setTransactionsExternallyManaged(true);
        configuration.setAsyncExecutor(springAsyncExecutor);
        configuration.setJobExecutorActivate(false);
        configuration.setActivityFontName("宋体");
        configuration.setAnnotationFontName("宋体");
        configuration.setLabelFontName("宋体");
        configuration.setEventListeners(Lists.newArrayList(new GlobalEventListener()));
        // 配置事务上下文工厂
        TransactionContextFactory transactionContextFactory = new SpringTransactionContextFactory(transactionManager);
        configuration.setTransactionContextFactory(transactionContextFactory);
        configuration.setTransactionFactory(new SpringManagedTransactionFactory());
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
