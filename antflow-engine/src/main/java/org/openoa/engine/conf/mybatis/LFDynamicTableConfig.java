package org.openoa.engine.conf.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.openoa.engine.conf.mybatis.interceptor.LFRoutingSqlInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class LFDynamicTableConfig {
    @Autowired
    private LFRoutingSqlInterceptor sqlInterceptor;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @PostConstruct
    public void addMySqlInterceptor() {
        sqlSessionFactory.getConfiguration().addInterceptor(sqlInterceptor);
    }
}
