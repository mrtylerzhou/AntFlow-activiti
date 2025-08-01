package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.openoa.base.util.SpringBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Classname BizServiceImpl
 * @Description TODO
 * @Date 2021-10-31 15:58
 * @Created by AntOffice
 */
public class BizServiceImpl<T extends ServiceImpl> {

    public T getService(){
        T bean=null;
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericSuperclass;
            Type[] actualTypes = pt.getActualTypeArguments();
            Type actualType = actualTypes[0];
            Class<?> clazz = (Class<?>) actualType;
             bean = (T) SpringBeanUtils.getBean(clazz);
        }
        return bean;
    }
}
