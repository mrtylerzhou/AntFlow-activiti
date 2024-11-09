package org.openoa.base.service;

import org.openoa.base.adp.OrderedBean;
import org.openoa.base.util.SpringBeanUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ProcessorFactory {
    public static  <TProcessor extends AntFlowOrderPostProcessor<TEntity>,TEntity> void executePostProcessors(Class<TProcessor> processorClass,TEntity entity){
        List<TProcessor> orderedPostProcessors = getOrderedPostProcessors(processorClass, entity.getClass());
        for (TProcessor orderedPostProcessor : orderedPostProcessors) {
            orderedPostProcessor.postProcess(entity);
        }
    }
    public static  <TProcessor extends AntFlowOrderPreProcessor<TEntity>,TEntity> void executePreProcessors(Class<TProcessor> processorClass,TEntity entity){
        List<TProcessor> orderedPostProcessors = getOrderedPostProcessors(processorClass, entity.getClass());
        for (TProcessor orderedPostProcessor : orderedPostProcessors) {
            orderedPostProcessor.preProcess(entity);
        }
    }
    private static  <TProcessor extends OrderedBean,TEntity> List<TProcessor> getOrderedPostProcessors(Class<TProcessor> processorCls, Class<TEntity> cls){
        List<TProcessor> orderedBeans = SpringBeanUtils.getOrderedBeans(processorCls);
        List<TProcessor> processorsOfType=new ArrayList<>();
        for (TProcessor bean : orderedBeans) {
            Type[] genericInterfaces = bean.getClass().getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    for (Type actualTypeArgument : actualTypeArguments) {
                       if(cls.isAssignableFrom((Class<TEntity>) actualTypeArgument)){
                            processorsOfType.add(bean);
                       }
                    }
                }
            }
        }
        return processorsOfType;
    }
}
