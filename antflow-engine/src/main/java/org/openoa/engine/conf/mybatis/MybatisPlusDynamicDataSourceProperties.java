package org.openoa.engine.conf.mybatis;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig;
import org.openoa.engine.conf.confval.BizDataSourceConfVal;
import org.openoa.engine.conf.confval.DataSourceConfVal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;

import static org.openoa.base.constant.StringConstants.DB_NAME_1;
import static org.openoa.base.constant.StringConstants.DB_NAME_2;
import static org.openoa.base.constant.StringConstants.DRUID_POOL_NAME_PREFIX;
import static org.openoa.base.util.StrUtils.getBeanName;
/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-15 15:13
 * @Param
 * @return
 * @Version 1.0
 */
@Configuration
public class MybatisPlusDynamicDataSourceProperties {

    @Primary
    @Bean
    public DynamicDataSourceProperties dynamicDataSourceProperties(Map<String,DataSourceConfVal> confValMap) {
        DynamicDataSourceProperties properties = new DynamicDataSourceProperties();
        properties.setPrimary(DB_NAME_1);
        properties.setStrict(true);
        properties.getDatasource().put(DB_NAME_1, buildDynamicDataSourceProperty(confValMap.get(getBeanName(DataSourceConfVal.class)),DB_NAME_1));
        properties.getDatasource().put(DB_NAME_2, buildDynamicDataSourceProperty(confValMap.get(getBeanName(BizDataSourceConfVal.class)),DB_NAME_2));
        return properties;
    }
    private DataSourceProperty buildDynamicDataSourceProperty( DataSourceConfVal confVal,String dbName) {
        DataSourceProperty property = new DataSourceProperty();
        property.setUrl(confVal.getUrl());
        property.setUsername(confVal.getUsername());
        property.setPassword(confVal.getPassword());
        property.setDriverClassName(confVal.getDriverClassName());
        //连接池名称配置，默认为HikariDataSource+数据库名称+Pool
        property.setPoolName(DRUID_POOL_NAME_PREFIX + dbName);

        DruidConfig druidConfig=new DruidConfig();
        druidConfig.setInitialSize(confVal.getInitialSize());
        druidConfig.setMaxActive(confVal.getMaxActive());
        druidConfig.setRemoveAbandoned(confVal.getRemoveAbandoned());
        druidConfig.setRemoveAbandonedTimeoutMillis(confVal.getRemoveAbandonedTimeout());
        property.setDruid(druidConfig);

        return property;
    }


}
