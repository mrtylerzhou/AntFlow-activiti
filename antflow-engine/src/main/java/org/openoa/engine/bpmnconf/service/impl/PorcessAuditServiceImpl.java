package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmProcessAudit;
import org.openoa.engine.bpmnconf.mapper.BpmProcessAuditMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.ProcessAuditService;
import org.springframework.stereotype.Repository;

@Repository
public class PorcessAuditServiceImpl extends ServiceImpl<BpmProcessAuditMapper, BpmProcessAudit> implements ProcessAuditService {
}
