package org.openoa.engine.lowflow.service.hooks;

import org.openoa.base.vo.BusinessDataVo;

public interface LFProcessFinishHook {
    void onFinishData(BusinessDataVo vo);
}
