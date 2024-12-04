package org.openoa.engine.conf.engineconfig;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.activiti.bpmn.model.StringDataObject;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.SpringBeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 用于自动侦测项目是否配置了mybatis-plus多数据源,如果配置了,直接使用mybatis-plus多数据源做为流程引擎多租房数据源
 * 如果项目没有使用mybatis-plus多数据源,并且需要开启多数据源多租房支持,则需要配置spring.antflow
 */
@Component
public class MBPDynamicDataSourceDetector implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    /**
     * 主数据库放在最前面
     * @return
     */
    public Map<DataSource,String> detectMybatisPlusDynamicDataSource(){
        Class<?> aClass=null;
        try {
             aClass = Class.forName("com.baomidou.dynamic.datasource.DynamicRoutingDataSource");
        } catch (ClassNotFoundException e) {
            //mybatis plus DynamicRoutingDataSource does not exist
            return null;
        }

        DynamicRoutingDataSource dataSource = applicationContext.getBean(DynamicRoutingDataSource.class);
        Map<String, DataSource> allDataSources = dataSource.getDataSources();
        DataSource masterSource = dataSource.getDataSource("nonesense");
        String masterSourceName="";
        LinkedHashMap<DataSource,String> result=new LinkedHashMap<>();
        //主数据库放在最前面
        result.put(masterSource,masterSourceName);
        for (Map.Entry<String, DataSource> stringDataSourceEntry : allDataSources.entrySet()) {
            String name = stringDataSourceEntry.getKey();
            DataSource value = stringDataSourceEntry.getValue();
            if(value==masterSource){//如果是主库,就只得到主库的key,不往result里放,如果放了key相同,主库就不是在第一位了
               result.replace(value,name);
            }else{
                result.put(value,name);
            }
        }

        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
