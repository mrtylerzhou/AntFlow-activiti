/*
 * Sto Express Tracking api 2019
 */

package org.openoa.engine.conf.register;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

public class CustomAnnotationScanner extends ClassPathBeanDefinitionScanner {
    /**
     * 实体类对应的AnnotationClazz
     */
    private Class<? extends Annotation> selfAnnotationClazz;

    @Override
    public void registerDefaultFilters() {
        // 添加需扫描的Annotation Class
        this.addIncludeFilter(new AnnotationTypeFilter(staticTempAnnotationClazz));
    }

    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return super.isCandidateComponent(beanDefinition)
                && beanDefinition.getMetadata().hasAnnotation(this.selfAnnotationClazz.getName());
    }

    /**
     * 传值使用的临时静态变量
     */
    private static Class<? extends Annotation> staticTempAnnotationClazz = null;

    private CustomAnnotationScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public static synchronized CustomAnnotationScanner getScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> clazz) {
        staticTempAnnotationClazz = clazz;
        CustomAnnotationScanner scanner = new CustomAnnotationScanner(registry);
        scanner.selfAnnotationClazz = clazz;
        return scanner;
    }

    public Set<BeanDefinitionHolder> doScanPackages(String... basePackages) {
        return this.doScan(basePackages);
    }
}
