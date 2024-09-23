package org.openoa.engine.conf.config;

import org.openoa.engine.factory.AdaptorFactoryProxy;
import org.openoa.base.constant.StringConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

/**
 * @Author TylerZhou
 * @Date 2022-05-29 6:17
 * @Param
 * @return
 * @Version 1.0
 */
@Configuration
public class AdaptorFactoryRegrister implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            beanFactory.registerSingleton(StringConstants.ADAPTOR_FACTORY_BEANNAME,AdaptorFactoryProxy.getProxyInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
