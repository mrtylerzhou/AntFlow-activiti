package org.openoa.engine.bpmnconf.service.biz;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmFlowruninfoBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BpmFlowruninfoBizServiceImpl implements BpmFlowruninfoBizService {
    protected static Map<String, ProcessDefinition> PROCESS_DEFINITION_CACHE = new HashMap<String, ProcessDefinition>();

    @Autowired
    private RepositoryService repositoryService;
    /**
     * get process definition
     *
     * @param processDefinitionId
     * @return
     */
    @Override
    public ProcessDefinition getProcessDefinition(String processDefinitionId) {
        ProcessDefinition processDefinition = PROCESS_DEFINITION_CACHE
                .get(processDefinitionId);
        if (processDefinition == null) {
            processDefinition = repositoryService
                    .createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId).singleResult();
            PROCESS_DEFINITION_CACHE
                    .put(processDefinitionId, processDefinition);
        }
        return processDefinition;
    }
}
