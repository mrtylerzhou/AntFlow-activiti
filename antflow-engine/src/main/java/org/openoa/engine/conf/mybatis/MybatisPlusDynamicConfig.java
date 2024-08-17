package org.openoa.engine.conf.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.openoa.base.constant.StringConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Properties;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-15 15:51
 * @Param
 * @return
 * @Version 1.0
 */
@Configuration
@MapperScan(basePackages = {StringConstants.SCAN_BASE_PACKAGES + ".**.mapper"}, sqlSessionFactoryRef = "mybatisdynamicSqlSessionFactory")
public class MybatisPlusDynamicConfig {
    @Value("${antflow.common.empTable.empTblName}")
    private String empTblName;

    //@Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer msc=new MapperScannerConfigurer();
        msc.setBasePackage(StringConstants.SCAN_BASE_PACKAGES + ".**.mapper");
        msc.setSqlSessionTemplateBeanName("mybatisdynamicSqlSessionTemplate");
        return msc;
    }

    @Bean(name = "mybatisdynamicSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("mybatisdynamicSqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    @Bean(name = "mybatisdynamicSqlSessionFactory")
    public SqlSessionFactory SqlSessionFactory(@Qualifier("jimuofficeDynamicRoutingDatasource") DataSource dataSource, GlobalConfig globalConfig, MybatisPlusInterceptor paginationInterceptor) throws Exception {
        final MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        sqlSessionFactoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath*:/mapper/**/**Mapper.xml")
        );
        sqlSessionFactoryBean.setTypeAliasesPackage("org.openoa.**.entity");
        //sqlSessionFactoryBean.setTypeEnumsPackage("demo.*.enumutil");
        sqlSessionFactoryBean.setGlobalConfig(globalConfig);
        sqlSessionFactoryBean.setConfiguration(defaultMybatisConfiguration());

        //PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        //Resource[] resources = resolver.getResources("classpath:mapper/**/*Mapper.xml");
        //sqlSessionFactoryBean.setMapperLocations(resources);
        sqlSessionFactoryBean.setPlugins(paginationInterceptor);

        Properties variables = new Properties();
        variables.setProperty("empTblName", empTblName);
        sqlSessionFactoryBean.setConfigurationProperties(variables);
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 默认的MybatisPlus配置
     *
     * @return MybatisPlus配置
     */
    private MybatisConfiguration defaultMybatisConfiguration() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        //打开自动驼峰命名转换
        configuration.setMapUnderscoreToCamelCase(true);
        //打开二级缓存
        configuration.setCacheEnabled(true);
        //打开延迟加载开关
        configuration.setLazyLoadingEnabled(true);
        //将积极加载改为消极按需加载
        configuration.setAggressiveLazyLoading(false);
        //是否允许单一语句返回多结果集（需要兼容驱动）。[默认值]
        configuration.setMultipleResultSetsEnabled(true);
        //使用列标签代替列名。不同的驱动在这方面会有不同的表现，具体可参考相关驱动文档或通过测试这两种不同的模式来观察所用驱动的结果。[默认值]
        configuration.setUseColumnLabel(true);
        //使用MybatisPlus的枚举处理器，用于完成枚举处理
        //configuration.setDefaultEnumTypeHandler(MybatisEnumTypeHandler.class);
        return configuration;
    }


    /**
     * 创建全局配置
     *
     * @return 全局配置
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        // 默认为自增
        dbConfig.setIdType(IdType.AUTO);
        globalConfig.setDbConfig(dbConfig);
        return globalConfig;
    }

    /**
     * 分页插件，自动识别数据库类型
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }


}
