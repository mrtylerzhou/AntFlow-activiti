/*
 * Sto Express Tracking api 2019
 */

package org.openoa.common.config;

import org.apache.commons.lang3.StringUtils;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public class SdpAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {
    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        String beanName = StringUtils.EMPTY;
        ScannedGenericBeanDefinition beanDefinition = (ScannedGenericBeanDefinition) definition;
        AnnotationMetadata metadataVisitor = beanDefinition.getMetadata();
        List<String> annotationTypes = new ArrayList<>(metadataVisitor.getAnnotationTypes());

        String beanClassName = beanDefinition.getBeanClassName();
        assert beanClassName != null;
        String shortName = ClassUtils.getShortName(beanClassName);
        beanName = shortName;

        if (!CollectionUtils.isEmpty(annotationTypes)) {
            String annotationTypeName = annotationTypes.get(0);
            String name = ActivitiServiceAnno.class.getName();
            if (name.equals(annotationTypeName)) {
                MultiValueMap<String, Object> allAnnotationAttributes = metadataVisitor.getAllAnnotationAttributes(name);
                List<Object> svcNames = allAnnotationAttributes.get("svcName");
                if (!CollectionUtils.isEmpty(svcNames)) {
                    Object o = svcNames.get(0);
                    if (o != null) {
                        beanName = o.toString();
                    } else {
                        beanName = beanDefinition.getBeanClassName();
                    }
                }
            }
        }
        return beanName;
    }
}
