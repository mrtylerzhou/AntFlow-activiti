package org.openoa.engine.conf.mvc;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class OnMissingGlobalExceptionHandlerCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 检查是否存在标记了 @ControllerAdvice 或 @RestControllerAdvice 的类
        boolean hasControllerAdvice = !context.getBeanFactory().getBeansWithAnnotation(ControllerAdvice.class).isEmpty();
        boolean hasRestControllerAdvice = !context.getBeanFactory().getBeansWithAnnotation(RestControllerAdvice.class).isEmpty();
        
        // 如果存在任意一个，则返回 false，不加载当前类
        return !(hasControllerAdvice || hasRestControllerAdvice);
    }
}
