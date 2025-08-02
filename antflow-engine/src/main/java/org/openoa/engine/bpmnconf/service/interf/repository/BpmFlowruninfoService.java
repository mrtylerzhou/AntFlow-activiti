package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.activiti.engine.repository.ProcessDefinition;
import org.openoa.base.entity.BpmFlowruninfo;


public interface BpmFlowruninfoService extends IService<BpmFlowruninfo> {
    void createFlowRunInfo(String entryId, String processInstance) throws Exception;

    void createFlowRunInfo(BpmFlowruninfo flowruninfo);

    BpmFlowruninfo getFlowruninfo(Long runInfoId);

    void deleteFlowruninfo(Long id);

    ProcessDefinition getProcessDefinition(String processDefinitionId);
}
