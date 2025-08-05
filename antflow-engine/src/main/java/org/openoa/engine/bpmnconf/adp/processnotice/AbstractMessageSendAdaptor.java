package org.openoa.engine.bpmnconf.adp.processnotice;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.interf.ProcessNoticeAdaptor;
import org.openoa.base.vo.UserMsgVo;
import org.openoa.engine.bpmnconf.service.biz.MessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public abstract class AbstractMessageSendAdaptor<T> implements ProcessNoticeAdaptor {
    @Autowired
    protected MessageServiceImpl messageService;
    protected Map<String,T> messageProcessing(List<UserMsgVo> userMsgVos, Function<UserMsgVo,T> fun){
        if(CollectionUtils.isEmpty(userMsgVos)){
            log.warn("发送消息消息内容为空!");
            return null;
        }
        Map<String,T> map= Maps.newHashMap();
        for (UserMsgVo userMsgVo : userMsgVos) {
            T result = fun.apply(userMsgVo);
            map.put(userMsgVo.getUserId(), result);
        }
        return map;
    }
}
