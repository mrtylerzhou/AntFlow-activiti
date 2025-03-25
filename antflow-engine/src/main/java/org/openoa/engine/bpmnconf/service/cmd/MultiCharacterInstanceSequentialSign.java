package org.openoa.engine.bpmnconf.service.cmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;

import java.util.Map;

public class MultiCharacterInstanceSequentialSign implements Command {
 
	private String taskId;
	private Map<String, Object> variables;
 
	public MultiCharacterInstanceSequentialSign(String taskId, Map<String, Object> variables) {
		this.taskId = taskId;
		this.variables = variables;
	}
 
	public Object execute(CommandContext commandContext) {
		TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
		TaskEntity taskEntity = taskEntityManager.findTaskById(taskId);
		ExecutionEntity executionEntity = taskEntity.getExecution();
		
		//多实例任务总数加一
		Integer nrOfInstances = (Integer)executionEntity.getVariable("nrOfInstances");
		executionEntity.setVariable("nrOfInstances", nrOfInstances + 1);
		
		// 设置流程变量
		executionEntity.setVariables(variables);
 
		return null;
	}
}