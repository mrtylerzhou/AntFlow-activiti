package org.openoa.engine.bpmnconf.adp.processcallback;

import com.alibaba.fastjson2.JSON;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.engine.factory.CallbackAdaptor;
import org.openoa.engine.vo.CallbackRespVo;
import org.openoa.engine.vo.ProcBaseCallBackVo;
import org.springframework.stereotype.Service;

@Service("PROC_BASE_CALL_BACK")
public class ProcBaseCallBackAdp implements CallbackAdaptor<ProcBaseCallBackVo, CallbackRespVo> {
    @Override
    public ProcBaseCallBackVo formatRequest(BpmnConfVo bpmnConfVo) {
        return new  ProcBaseCallBackVo(bpmnConfVo.getFormData());
    }

    @Override
    public CallbackRespVo formatResponce(String resultJson) {
        return JSON.parseObject(resultJson, CallbackRespVo.class);
    }
}
