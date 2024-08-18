package org.openoa.engine.conf.mvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Slf4j
@Configuration
public class MVCConf {

    @Bean("jimuFilterRegistrationBean")
    public FilterRegistrationBean logHandlerFilter(CommonsRequestLoggingFilter commonsRequestLoggingFilter) {
        FilterRegistrationBean<CommonsRequestLoggingFilter> requestLoggingFilterFilterRegistrationBean = new FilterRegistrationBean<>();
        requestLoggingFilterFilterRegistrationBean.setFilter(commonsRequestLoggingFilter);

        requestLoggingFilterFilterRegistrationBean.addUrlPatterns("/*");
        requestLoggingFilterFilterRegistrationBean.setName("rootHandlerFilter");
        requestLoggingFilterFilterRegistrationBean.setOrder(1);
        return requestLoggingFilterFilterRegistrationBean;
    };

}