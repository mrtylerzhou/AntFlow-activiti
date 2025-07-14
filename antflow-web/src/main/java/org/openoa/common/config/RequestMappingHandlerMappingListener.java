package org.openoa.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
@Slf4j
@Component
public class RequestMappingHandlerMappingListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 获取RequestMappingHandlerMapping的bean
        RequestMappingHandlerMapping requestMappingHandlerMapping =
                event.getApplicationContext().getBean(RequestMappingHandlerMapping.class);

        // 获取所有的handler方法映射
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();

        // 遍历并打印每个映射
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            RequestMappingInfo info = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            // 构建日志信息
            String mappingDescription = info.toString();
            String methodName = handlerMethod.getMethod().getName();
            String className = handlerMethod.getBeanType().getName();

            log.info("Mapped \"{{}}\" onto {}.{}", mappingDescription, className, methodName);
        }
    }
}