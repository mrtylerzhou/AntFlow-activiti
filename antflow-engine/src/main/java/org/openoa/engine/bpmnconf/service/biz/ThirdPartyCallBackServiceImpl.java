package org.openoa.engine.bpmnconf.service.biz;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.CallbackTypeEnum;
import org.openoa.base.interf.MethodReplay;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.engine.factory.ThirdPartyCallbackFactory;
import org.springframework.stereotype.Service;

import static org.openoa.base.constant.enums.CallbackTypeEnum.PROC_END_CALL_BACK;
@Slf4j
@Service
public class ThirdPartyCallBackServiceImpl {

    @MethodReplay
   public void doCallback(String url, CallbackTypeEnum callbackTypeEnum, BpmnConfVo bpmnConfVo,
               String processNum, String businessId,String verifyUserName){
       log.info("准备执行外部工作流回调：{} , processNumber:{} , callBackUrl:{} , 操作人：{}",PROC_END_CALL_BACK.getDesc() ,processNum, url, verifyUserName);
       //回调通知业务方
       ThirdPartyCallbackFactory.build().doCallback(url, callbackTypeEnum, bpmnConfVo,
               processNum, businessId);
   }
}
