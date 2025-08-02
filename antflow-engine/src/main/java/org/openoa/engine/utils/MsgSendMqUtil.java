package org.openoa.engine.utils;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.BusinessCallbackEnum;
import org.openoa.base.constant.enums.ProcessBusinessCallBackTypeEnum;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.biz.callback.BusinessCallBackFactory;

/**
 * @Author tylerzhou
 */
@Slf4j
public class MsgSendMqUtil {


    public static void sendProcessEventChangeMessageCallBack(BusinessDataVo vo){
        if(vo==null){
            return;
        }
        try {
            BusinessCallBackFactory.build().doCallBack(vo, BusinessCallbackEnum.PROCESS_EVENT_CALLBACK, ProcessBusinessCallBackTypeEnum.Send_MQ_Message.getCode());
        }catch (Exception e){
            log.error("流程消息发送失败,{}", JsonUtils.transfer2JsonString(vo),e);
        }
    }
}
