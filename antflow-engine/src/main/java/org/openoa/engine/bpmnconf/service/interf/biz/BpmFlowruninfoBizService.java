package org.openoa.engine.bpmnconf.service.interf.biz;

import org.activiti.engine.repository.ProcessDefinition;
import org.openoa.base.entity.BpmFlowruninfo;
import org.openoa.engine.bpmnconf.mapper.BpmFlowruninfoMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmFlowruninfoService;

public interface BpmFlowruninfoBizService extends BizService<BpmFlowruninfoMapper, BpmFlowruninfoService, BpmFlowruninfo> {
    ProcessDefinition getProcessDefinition(String processDefinitionId);
}
