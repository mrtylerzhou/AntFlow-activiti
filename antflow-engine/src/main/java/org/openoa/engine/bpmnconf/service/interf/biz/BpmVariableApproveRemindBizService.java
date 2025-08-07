package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmVariableApproveRemind;
import org.openoa.engine.bpmnconf.mapper.BpmVariableApproveRemindMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVariableApproveRemindService;

public interface BpmVariableApproveRemindBizService extends BizService<BpmVariableApproveRemindMapper, BpmVariableApproveRemindService, BpmVariableApproveRemind>{
    void doTimeoutReminder();
}
