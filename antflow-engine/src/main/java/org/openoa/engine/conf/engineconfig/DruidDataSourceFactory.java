//package org.openoa.engine.conf.engineconfig;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.util.Properties;
//
//@Component
//public class DruidDataSourceFactory implements DataSourceFactory {
//    @Autowired
//    @Qualifier("druidDataSourceForTenantBase")
//    private DruidDataSource baseDataSource;
//    @Override
//    public DataSource createDataSource(String url, String username, String password) {
//        DruidDataSource dataSource = baseDataSource.cloneDruidDataSource();
//        dataSource.setUrl(url);
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//        return dataSource;
//    }
//}
