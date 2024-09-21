//package org.openoa.engine.conf.mybatis;
//
//import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
//import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// *@Author JimuOffice
// * @Description //TODO $
// * @Date 2022-05-15 15:47
// * @Param
// * @return
// * @Version 1.0
// */
//@Configuration
//public class MybatisPlusDynamicRoutingDataSource {
//    @Bean(name = "jimuofficeDynamicRoutingDatasource")
//    public DynamicRoutingDataSource dataSource(DynamicDataSourceProperties properties) {
//        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
//        dataSource.setPrimary(properties.getPrimary());
//        dataSource.setStrict(properties.getStrict());
//        dataSource.setStrategy(properties.getStrategy());
//        dataSource.setP6spy(properties.getP6spy());
//        dataSource.setSeata(properties.getSeata());
//        return dataSource;
//    }
//}
