package org.openoa.engine.conf.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.openoa.engine.conf.mybatis.interceptor.LFConsistentHashingRoutingSqlInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class LFDynamicTableConfig {
    @Autowired
    private LFConsistentHashingRoutingSqlInterceptor sqlInterceptor;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @PostConstruct
    public void addMySqlInterceptor() {
        sqlSessionFactory.getConfiguration().addInterceptor(sqlInterceptor);
    }
}
