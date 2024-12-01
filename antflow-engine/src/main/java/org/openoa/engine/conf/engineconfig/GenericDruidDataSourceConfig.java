//package org.openoa.engine.conf.engineconfig;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Properties;
//
//@Configuration
//public class GenericDruidDataSourceConfig {
//    @Bean("druidDataSourceForTenantBase")
//    @ConfigurationProperties(prefix = "spring.datasource.druid")
//    public DruidDataSource druidDataSource() {
//        return new DruidDataSource();
//    }
//}
