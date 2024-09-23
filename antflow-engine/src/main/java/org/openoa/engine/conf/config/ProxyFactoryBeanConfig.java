package org.openoa.engine.conf.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-04-28 14:55
 * @Param
 * @return
 * @Version 1.0
 */
@Component
public class ProxyFactoryBeanConfig implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
