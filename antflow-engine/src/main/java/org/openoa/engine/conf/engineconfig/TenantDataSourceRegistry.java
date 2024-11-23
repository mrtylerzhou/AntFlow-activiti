package org.openoa.engine.conf.engineconfig;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
public class TenantDataSourceRegistry implements InitializingBean {
    @Autowired
    private DataSourceFactory dataSourceFactory;
    @Autowired
    private AntflowDataSourceConfigScanner antflowDataSourceConfigScanner;
    @Autowired
    private MBPDynamicDataSourceDetector mbpDynamicDataSourceDetector;
    @Autowired
    private DataSourceProperties dataSourceProperties;

    protected final Map<String, DataSource> dataSources = new HashMap<>();

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
        Map<String, DataSource> dataSourceMap = null;
        // 添加租户数据源
        //探测是否存在mybatisplus多数据源配置,如果有,则使用,如果不是mybatisplus数据源,配置的数据源都是指定租户的,antflow会将系统默认datasource添加进引擎中做为默认的
        Map<String, DataSource> stringDataSourceMap = mbpDynamicDataSourceDetector.detectMybatisPlusDynamicDataSource();
        if (!CollectionUtils.isEmpty(stringDataSourceMap)) {
            dataSourceMap = stringDataSourceMap;
        } else {
            dataSourceMap = new HashMap<>();
            Map<String, DataSourceProperties> antflowDataSourceProperties = antflowDataSourceConfigScanner.getAntflowDataSourceProperties();
            if (!CollectionUtils.isEmpty(antflowDataSourceProperties)) {
                for (String key : antflowDataSourceProperties.keySet()) {
                    DataSourceProperties dataSourceProperties = antflowDataSourceProperties.get(key);
                    String username = dataSourceProperties.getUsername();
                    String password = dataSourceProperties.getPassword();
                    String url = dataSourceProperties.getUrl();
                    DataSource dataSource = dataSourceFactory.createDataSource(url, username, password);
                    dataSourceMap.put(key, dataSource);
                }
            }
            /*  }*/

            if (CollectionUtils.isEmpty(dataSourceMap)) {
                dataSourceMap.put("", dataSourceProperties.initializeDataSourceBuilder().build());
            }
            for (String dataSourceName : dataSourceMap.keySet()) {
                DataSource dataSource = dataSourceMap.get(dataSourceName);
                this.registerDataSource(dataSourceName, dataSource);
            }
        }
    }
}