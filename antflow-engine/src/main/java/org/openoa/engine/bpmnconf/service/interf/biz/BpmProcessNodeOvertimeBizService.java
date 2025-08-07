package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmProcessNodeOvertime;
import org.openoa.base.exception.AFBizException;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNodeOvertimeMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNodeOvertimeService;

public interface BpmProcessNodeOvertimeBizService extends BizService<BpmProcessNodeOvertimeMapper, BpmProcessNodeOvertimeService, BpmProcessNodeOvertime>{
    Boolean nodeOvertime(String processKey, String taskId);

    void checkTaskOvertime() throws AFBizException;
}
