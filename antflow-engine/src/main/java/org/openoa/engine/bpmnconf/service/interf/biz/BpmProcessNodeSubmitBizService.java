package org.openoa.engine.bpmnconf.service.interf.biz;

import org.activiti.engine.task.Task;
import org.openoa.base.entity.BpmProcessNodeSubmit;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNodeSubmitMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNodeSubmitService;

public interface BpmProcessNodeSubmitBizService extends BizService<BpmProcessNodeSubmitMapper, BpmProcessNodeSubmitService, BpmProcessNodeSubmit>{
    void processComplete(Task task);
}
