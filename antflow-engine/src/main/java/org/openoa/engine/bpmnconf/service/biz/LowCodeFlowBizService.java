package org.openoa.engine.bpmnconf.service.biz;

import org.openoa.base.vo.BaseKeyValueStruVo;

import java.util.List;

public interface LowCodeFlowBizService {
    //todo cbcbu
    List<BaseKeyValueStruVo> getLowCodeFlowFormCodes();

    List<BaseKeyValueStruVo> getLFActiveFormCodes();

    Integer addFormCode(BaseKeyValueStruVo vo);
}
