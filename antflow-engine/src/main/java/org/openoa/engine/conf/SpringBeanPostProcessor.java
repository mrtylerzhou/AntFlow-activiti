package org.openoa.engine.conf;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.openoa.engine.bpmnconf.service.biz.BizServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Classname SpringBeanPostProcessor
 * @Description TODO
 * @Date 2021-10-31 16:17
 * @Created by AntOffice
 */
//@Component
public class SpringBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BizServiceImpl) {
            BizServiceImpl bizService = (BizServiceImpl) bean;
            ParameterizedType superclassType = (ParameterizedType) bean.getClass().getGenericSuperclass();
            Type typeArgument = superclassType.getActualTypeArguments()[0];
            ServiceImpl bean1 = applicationContext.getBean((Class<? extends ServiceImpl>) typeArgument);
            Field serviceField = bizService.getClass().getSuperclass().getDeclaredField("service");
            serviceField.setAccessible(true);
            serviceField.set(bean, bean1);
        }
        return bean;
    }

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
