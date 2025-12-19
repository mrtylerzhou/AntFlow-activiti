package org.openoa.base.util;


import org.openoa.base.adp.OrderedBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Classname SpringBeanUtils
 * @Description a simple spring bean util
 * @Date 2021-10-31 16:11
 * @Created by AntOffice
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class SpringBeanUtils implements ApplicationContextAware, BeanFactoryPostProcessor {
    private static ApplicationContext applicationContext;
    private static Map<String,Object> cachedObjects=new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {

        applicationContext = appContext;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> Collection<T> getBeans(Class<T> tClass){
        return applicationContext.getBeansOfType(tClass).values();
    }
    public  static  <T extends OrderedBean> List<T> getOrderedBeans(Class<T> tOrderedBeanClass){
        Collection<T> orderBeans =getBeans(tOrderedBeanClass);
        return orderBeans.stream().sorted(Comparator.comparing(OrderedBean::order)).collect(Collectors.toList());
    }
    public static <T> T getBean(Class<T> tClass) {
        return applicationContext.getBean(tClass);
    }
    public static <T>  Map<String,T> getBeansOfType(Class clsz){
        Map<String,T> beansOfType = applicationContext.getBeansOfType(clsz);
        return beansOfType;
    }
    public static <T> T getBean(String name, Class<T> tClass) {
        if (applicationContext.containsBean(name)) {
            return applicationContext.getBean(name, tClass);
        }
        return null;
    }
    public static void  putObject(String key,Object object){
        cachedObjects.put(key,object);
    }
    public static Object getObject(String key){
        return cachedObjects.get(key);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        SpringBeanUtils SpringBeanUtilInstance=configurableListableBeanFactory.getBean(SpringBeanUtils.class);
        if(SpringBeanUtilInstance!=null){
           SpringBeanUtilInstance.setApplicationContext(applicationContext);
        }
    }
}
