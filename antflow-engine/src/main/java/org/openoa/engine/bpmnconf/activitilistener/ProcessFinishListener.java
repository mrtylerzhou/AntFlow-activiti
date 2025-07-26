package org.openoa.engine.bpmnconf.activitilistener;

import org.openoa.base.vo.BusinessDataVo;

public interface ProcessFinishListener {
    void onFinishData(BusinessDataVo vo);
}
