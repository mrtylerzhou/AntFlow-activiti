package org.openoa.engine.common;

import org.openoa.base.constant.StringConstants;
import org.openoa.engine.factory.ButtonPreOperationService;
import org.openoa.engine.factory.IAdaptorFactory;
import org.openoa.engine.factory.SimpleProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Author TylerZhou
 * @Date 2024/7/7 9:40
 * @Version 1.0
 */
@Configuration
public class ProxyedBeans implements ApplicationContextAware {


    private ApplicationContext applicationContext;

    @Bean
    public ButtonPreOperationService buttonOperationService(){
        ButtonPreOperationService proxyInstance = SimpleProxyFactory.getProxyInstance(ButtonPreOperationService.class);
        return  proxyInstance;
    }
    @Bean
    @Primary
    public IAdaptorFactory adaptorFactory(){
       IAdaptorFactory adaptorFactory=(IAdaptorFactory) applicationContext.getBean(StringConstants.ADAPTOR_FACTORY_BEANNAME);
       return adaptorFactory;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
