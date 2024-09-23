//package org.openoa.engine.conf.mybatis;
//
//import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
///**
// *@Author JimuOffice
// * @Description //TODO $
// * @Date 2022-05-15 16:59
// * @Param
// * @return
// * @Version 1.0
// */
//@Configuration
//@EnableTransactionManagement
//public class MultiDataSourceTranstionManager {
//    @Primary
//    @Bean("dynamicDatasourceTransactionManager")
//    public DataSourceTransactionManager multidbDbTransactionManager(@Qualifier("jimuofficeDynamicRoutingDatasource") DynamicRoutingDataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
//
//}
