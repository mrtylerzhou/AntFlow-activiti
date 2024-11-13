package org.openoa.engine.factory;


import org.openoa.base.vo.BpmnConfVo;
import org.openoa.engine.vo.CallbackReqVo;
import org.openoa.engine.vo.CallbackRespVo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface CallbackAdaptor<req extends CallbackReqVo, resp extends CallbackRespVo> {

    req formatRequest(BpmnConfVo bpmnConfVo);

    resp formatResponce(String resultJson);

    /**
     * 获得创建的空的回调返回对象
     *
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    default resp getNewRespObj() throws IllegalAccessException, InstantiationException {
        Type[] genericInterfaces = this.getClass().getGenericInterfaces();

        Type type = genericInterfaces[0];

        ParameterizedType p = (ParameterizedType) type;

        Class<?> cls = (Class) p.getActualTypeArguments()[1];

        return (resp) cls.newInstance();
    }

}
