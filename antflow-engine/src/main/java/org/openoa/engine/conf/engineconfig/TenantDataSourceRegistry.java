package org.openoa.engine.conf.engineconfig;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class TenantDataSourceRegistry implements InitializingBean {
    @Autowired
    private DataSourceFactory dataSourceFactory;
    @Autowired
    private DataSource defaultDataSource;

    protected final Map<String, DataSource> dataSources = new HashMap<>();

    public void registerTenantDataSource(String tenantId){

    }
    public void registerDataSource(String name, DataSource dataSource) {
        dataSources.put(name, dataSource);
    }

    public DataSource getDataSource(String name) {
        return dataSources.getOrDefault(name, dataSources.get(""));
    }

   public DataSource getDefaultDataSource(){
        return  dataSources.get("");
   }
    @Override
    public void afterPropertiesSet() throws Exception {
        // 添加租户数据源
        this.registerDataSource("",defaultDataSource);
        this.registerDataSource("tenantA",dataSourceFactory.createDataSource("jdbc:mysql://localhost:3306/tenanta", "root", "dsb0004699"));
        this.registerDataSource("tenantB", dataSourceFactory.createDataSource("jdbc:mysql://localhost:3306/tenantb", "root", "dsb0004699"));

    }
}