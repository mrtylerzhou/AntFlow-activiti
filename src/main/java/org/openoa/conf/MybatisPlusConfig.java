package org.openoa.conf;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.openoa.JimuofficeApplication;
import org.openoa.conf.confval.DataSourceConfVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Slf4j
@Configuration
@MapperScan(basePackages = {JimuofficeApplication.SCAN_BASE_PACKAGES + ".**.mapper"}, sqlSessionFactoryRef = "mybatisSqlSessionFactoryBean")
public class MybatisPlusConfig {
    @Autowired
    private DataSourceConfVal dataSourceConfVal;
    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    @Value("60000")
    private int timeBetweenEvictionRunsMillis;
    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    @Value("300000")
    private int minEvictableIdleTimeMillis;
    @Value("SELECT 1 FROM DUAL")
    private String validationQuery;
    @Value("true")
    private boolean testWhileIdle;
    @Value("false")
    private boolean testOnBorrow;

    @Value("false")
    private boolean testOnReturn;

    /**
     * 打开PSCache，并且指定每个连接上PSCache的大小
     */
    @Value("true")
    private boolean poolPreparedStatements;

    @Value("20")
    private int maxPoolPreparedStatementPerConnectionSize;
    /**
     * 通过connectProperties属性来打开mergeSql功能；慢SQL记录
     */
    @Value("druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500")
    private String connectionProperties;
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
    @Primary
    @Bean(name = "jimuMainDataSource")
    public DataSource dataSource() {
        return getDataSourceConfig(dataSourceConfVal.getUsername(), dataSourceConfVal.getPassword(), dataSourceConfVal.getUrl());
    }
    @Primary
    @Bean("mybatisSqlSessionFactoryBean")
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean(@Qualifier("jimuMainDataSource") DataSource dataSource) throws Exception{
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSource);
        mybatisSqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        mybatisSqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:mapper/*.xml"));
        return mybatisSqlSessionFactoryBean;
    }
    private DataSource getDataSourceConfig(String username, String password, String url) {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(dataSourceConfVal.getDriverClassName());

        //configuration
        datasource.setInitialSize(dataSourceConfVal.getInitialSize());
        datasource.setMinIdle(dataSourceConfVal.getMinIdle());
        datasource.setMaxActive(dataSourceConfVal.getMaxActive());
        datasource.setMaxWait(dataSourceConfVal.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        /*try {
            datasource.setFilters(filters);
        } catch (SQLException e) {

        }*/
        datasource.setConnectionProperties(connectionProperties);

        return datasource;
    }
}
