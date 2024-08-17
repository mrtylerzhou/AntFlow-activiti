/*
 * Sto Express Tracking api 2019
 */

package org.openoa.engine.factory;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class AdaptorFactoryAspect implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Around("@annotation(org.openoa.engine.factory.SpfService)")
    public Object getBeanObject(ProceedingJoinPoint pj) throws Throwable {
        Signature signature = pj.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Class<?> returnType = method.getReturnType();
        SpfService annotation = method.getAnnotation(SpfService.class);
        Class<? extends TagParser<?,?>> tagParserCls = annotation.tagParser();
        TagParser tagParser = tagParserCls.newInstance();
        Object param = tagParser.parseTag(pj.getArgs()[0]);
        if(!(param instanceof String)){
            return param;
        }
        Map<String, ?> beansOfType = applicationContext.getBeansOfType(returnType);
        Object bean = beansOfType.get(param);

        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
