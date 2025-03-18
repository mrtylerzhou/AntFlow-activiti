package org.openoa.engine.bpmnconf.adp.processcallback;

import com.alibaba.fastjson2.JSON;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.engine.factory.CallbackAdaptor;
import org.openoa.engine.vo.ProcSubmitCallbackReqVo;
import org.openoa.engine.vo.ProcSubmitCallbackRespVo;
import org.springframework.stereotype.Service;

@Service("PROC_SUBMIT_CALL_BACK")
public class ProcSubmitCallbackAdp implements CallbackAdaptor<ProcSubmitCallbackReqVo, ProcSubmitCallbackRespVo> {

    @Override
    public ProcSubmitCallbackReqVo formatRequest(BpmnConfVo bpmnConfVo) {

        return new  ProcSubmitCallbackReqVo(bpmnConfVo.getFormData());

    }

    @Override
    public ProcSubmitCallbackRespVo formatResponce(String resultJson) {
        return JSON.parseObject(resultJson, ProcSubmitCallbackRespVo.class);
    }
}
