package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmProcessAudit;
import org.openoa.engine.bpmnconf.mapper.BpmProcessAuditMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.ProcessAuditService;

public interface ProcessAuditBizService extends BizService<BpmProcessAuditMapper, ProcessAuditService, BpmProcessAudit> {
}
