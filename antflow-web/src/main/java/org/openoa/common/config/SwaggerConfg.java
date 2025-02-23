package org.openoa.common.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@EnableKnife4j
public class SwaggerConfg {


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AntFlow")
                        .version("1.0")
                        .description("AntFlow工作流引擎")
                        .termsOfService("https://gitee.com/tylerzhou/Antflow")
                        .license(new License().name("MIT")
                                .url("https://gitee.com/tylerzhou/Antflow"))
                );

    }

    @Bean
    public GroupedOpenApi apiBase() {
        return GroupedOpenApi.builder()
                .group("AntFlow")
                .displayName("Api接口")
                .packagesToScan("org.openoa.engine")
                .build();
    }

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("扩展接口")
                .displayName("扩展Api接口")
                .packagesToScan("org.openoa.controller")
                .build();
    }

}
