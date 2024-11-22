package org.openoa.engine.conf.engineconfig;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.openoa.base.exception.JiMuBizException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * 用于自动侦测项目是否配置了mybatis-plus多数据源,如果配置了,直接使用mybatis-plus多数据源做为流程引擎多租房数据源
 * 如果项目没有使用mybatis-plus多数据源,并且需要开启多数据源多租房支持,则需要配置spring.antflow
 */
@Component
public class MBPDynamicDataSourceDetector {
    public Map<String, DataSource> detectMybatisPlusDynamicDataSource(DataSource dataSource){

            if(dataSource.getClass().getName().equals("com.baomidou.dynamic.datasource.DynamicRoutingDataSource")){
                Field dataSourceMapField = FieldUtils.getField(dataSource.getClass(), "dataSourceMap", true);
                if(dataSourceMapField==null){
                    throw new JiMuBizException("mybatisplus dynamic datasource不包含dataSourceMap字段,请检查所使用的mybatisplus版本!");
                }
                try {
                  return  (Map<String, DataSource>)dataSourceMapField.get(dataSource);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
    }
}
