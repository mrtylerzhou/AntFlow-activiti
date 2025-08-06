package org.openoa.engine.bpmnconf.service.interf.biz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.util.SpringBeanUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface BizService<M extends BaseMapper<Entity>,T extends IService<Entity>,Entity> {
    default T getService(){
        T bean=null;
        Type[] genericInterfaces = this.getClass().getInterfaces();
        Type genericInterface=null;
        for (Type anInterface : genericInterfaces) {
            if(BizService.class.isAssignableFrom((Class<?>) anInterface)){
                genericInterface=anInterface;
                break;
            }
        }
        Type actualTypeArgument = ((ParameterizedType) ((Class<?>) genericInterface).getGenericInterfaces()[0]).getActualTypeArguments()[1];
        bean=(T) SpringBeanUtils.getBean((Class<?>)actualTypeArgument);
        return bean;
    }
    default M getMapper() {
        M mapper = null;
        Type[] genericInterfaces = this.getClass().getInterfaces();
        Type genericInterface=null;
        for (Type anInterface : genericInterfaces) {
            if(BizService.class.isAssignableFrom((Class<?>) anInterface)){
                genericInterface=anInterface;
                break;
            }
        }
        Type actualTypeArgument = ((ParameterizedType) ((Class) genericInterface).getGenericInterfaces()[0]).getActualTypeArguments()[0];
        mapper = (M) SpringBeanUtils.getBean((Class<?>) actualTypeArgument);
        return mapper;
    }

}
