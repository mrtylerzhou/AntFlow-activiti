package org.openoa.base.service;


import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.NotImplementedException;
import org.openoa.base.interf.BusinessCallBackAdaptor;
import org.openoa.base.util.SpringBeanUtils;

/**
 * @Author tylerzhou
 * Date on 2021/8/20
 */
public abstract class BaseSendMqMsgAdaptor<R, P> implements BusinessCallBackAdaptor<R, P> {

    @Override
    public void doCallBack(P param) {
        R r = formattedValue(param);
        if (r==null) {
            return;
        }
        sendMqMessage(r);
    }

    private void sendMqMessage(R value) {
        if (value==null) {
            return;
        }
        String message = JSON.toJSONString(value);
        //todo
        throw new NotImplementedException("send mq message method not implemented yet at the moment");
    }

    protected abstract String getTopicName();

    protected String getUserName() {
        //todo
        throw new NotImplementedException("not implemented yet");
    }
}
