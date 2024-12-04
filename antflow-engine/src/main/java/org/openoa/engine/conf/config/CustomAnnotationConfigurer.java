
package org.openoa.engine.conf.config;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.engine.conf.register.CustomAnnotationScanner;
import org.openoa.base.constant.StringConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

@Configuration
@Slf4j
public class CustomAnnotationConfigurer implements ApplicationContextAware, BeanFactoryPostProcessor {
    private ApplicationContext applicationContext;
    public static Map<String, String> activitiServiceMap = Maps.newHashMap();

    /**
     * 需要扫描的流程包.
     */
    private String[] scanPackages;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        scanPackages = configurableListableBeanFactory.resolveEmbeddedValue("${antflow.common.scan-packages:" +
                StringConstants.SCAN_BASE_PACKAGES + "}").split(",");
        scanningAndAssemblingAnnotation(configurableListableBeanFactory, ActivitiServiceAnno.class, activitiServiceMap,
                "启动扫描并装配扫描标注有@ActivitiServiceAnno注解bean实现类失败");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 扫描及装配指定注解的类进入Spring容器
     *
     * @param beanFactory
     * @param clazz
     * @param map
     * @param errorMsg
     */
    private void scanningAndAssemblingAnnotation(ConfigurableListableBeanFactory beanFactory, Class<? extends Annotation> clazz, Map<String, String> map, String errorMsg) {

        //扫描制定注解的类列表
        Set<BeanDefinitionHolder> beanDefinitionHolders = getBeanDefinitionHolders(beanFactory, clazz);

        //如果类列表为空则跳出操作
        if (CollectionUtils.isEmpty(beanDefinitionHolders)) {
            return;
        }

        //遍历类列表执行手动装配操作
        beanDefinitionHolders.forEach(o -> {
            try {
                //手动装配SpringBean
                configureBean(o);

                //反射获取类上面标记的注解中的svcName属性值并且组装静态Map
                Class<?> cls = Class.forName(o.getBeanDefinition().getBeanClassName());
                Annotation annotation = cls.getAnnotation(clazz);
                Method method = annotation.getClass().getMethod("svcName", new Class[]{});
                Object svcName = method.invoke(annotation, new Object[]{});
                if (svcName != null) {
                    map.put(svcName.toString(), o.getBeanName());
                }
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.error(errorMsg, e);
            }
        });
    }

    private void configureBean(BeanDefinitionHolder o) {
        ScannedGenericBeanDefinition beanDefinition = (ScannedGenericBeanDefinition) o.getBeanDefinition();
        AnnotationMetadata metadataVisitor = beanDefinition.getMetadata();
        this.applicationContext.getAutowireCapableBeanFactory().configureBean(o.getBeanDefinition(), o.getBeanName());
    }

    private Set<BeanDefinitionHolder> getBeanDefinitionHolders(ConfigurableListableBeanFactory beanFactory, Class<? extends Annotation> clazz) {
        CustomAnnotationScanner activitiAnnoScanner = CustomAnnotationScanner.getScanner((BeanDefinitionRegistry) beanFactory, clazz);
        activitiAnnoScanner.setResourceLoader(this.applicationContext);
        activitiAnnoScanner.setBeanNameGenerator(new SdpAnnotationBeanNameGenerator());
        return activitiAnnoScanner.doScanPackages(scanPackages);
    }
}
