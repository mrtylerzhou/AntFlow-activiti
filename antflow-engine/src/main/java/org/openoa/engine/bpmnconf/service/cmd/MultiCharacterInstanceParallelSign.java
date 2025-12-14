package org.openoa.engine.bpmnconf.service.cmd;

import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.Date;
import java.util.List;

public class MultiCharacterInstanceParallelSign implements Command {
 
	private String taskId;
	private final List<BaseIdTranStruVo> userInfos;


	public MultiCharacterInstanceParallelSign(String taskId, List<BaseIdTranStruVo> userInfos) {
		this.taskId = taskId;
		this.userInfos = userInfos;
	}
 
	public Object execute(CommandContext commandContext) {
		ProcessEngineConfigurationImpl pec = commandContext.getProcessEngineConfiguration();
		TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
		TaskEntity taskEntity = taskEntityManager.findTaskById(taskId);
		ExecutionEntity executionEntity = taskEntity.getExecution();
 
		// 设置流程变量
		//executionEntity.setVariables(variables);
 
		ExecutionEntity parentExecutionEntity = executionEntity.getParent();
		ExecutionEntity newExecutionEntity = parentExecutionEntity.createExecution();
		newExecutionEntity.setActive(true);
		newExecutionEntity.setConcurrent(true);
		newExecutionEntity.setScope(false);
		newExecutionEntity.setProcessDefinitionId(taskEntity.getProcessDefinitionId());
		TaskEntity newTaskEntity = new TaskEntity();
		newTaskEntity.setAssignee(userInfos.get(0).getId());
		newTaskEntity.setAssigneeName(userInfos.get(0).getName());
		newTaskEntity.setCreateTime(new Date());
		newTaskEntity.setTaskDefinition(taskEntity.getTaskDefinition());
		newTaskEntity.setProcessDefinitionId(taskEntity.getProcessDefinitionId());
		newTaskEntity.setTaskDefinitionKey(taskEntity.getTaskDefinitionKey());
		newTaskEntity.setProcessInstanceId(taskEntity.getProcessInstanceId());
		newTaskEntity.setExecutionId(newExecutionEntity.getId());
		newTaskEntity.setName(taskEntity.getName());
		newTaskEntity.setId(pec.getIdGenerator().getNextId());
		newTaskEntity.setExecution(newExecutionEntity);
		newExecutionEntity.setActivity(executionEntity.getActivity());
		pec.getTaskService().saveTask(newTaskEntity);
 
		Integer nrOfInstances = (Integer)executionEntity.getVariable("nrOfInstances");
		Integer nrOfActiveInstances = (Integer)executionEntity.getVariable("nrOfActiveInstances");
		executionEntity.setVariable("nrOfInstances", nrOfInstances + 1);
		executionEntity.setVariable("nrOfActiveInstances", nrOfActiveInstances + 1);
		newExecutionEntity.setVariableLocal("loopCounter", nrOfInstances);
		
		return null;
	}
}