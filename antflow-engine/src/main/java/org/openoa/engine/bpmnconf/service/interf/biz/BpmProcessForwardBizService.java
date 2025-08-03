package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmProcessForward;
import org.openoa.engine.bpmnconf.mapper.BpmProcessForwardMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessForwardService;

import java.util.List;

public interface BpmProcessForwardBizService extends BizService<BpmProcessForwardMapper, BpmProcessForwardService, BpmProcessForward>{
    void loadProcessForward(String userId);

    void loadTask(String userId);

    boolean isForward(String processInstanceId);

    void addProcessForwardBatch(String procInstId, String processNumber, List<String> forwardUserIds);
}
