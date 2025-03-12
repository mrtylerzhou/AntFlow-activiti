package org.openoa.engine.bpmnconf.service.flowcontrol;

import java.util.*;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.openoa.engine.bpmnconf.service.cmd.DeleteRunningTaskCmd;
import org.openoa.engine.bpmnconf.service.cmd.StartActivityCmd;
import org.openoa.engine.bpmnconf.util.ProcessDefinitionUtils;
import org.springframework.stereotype.Service;

public class DefaultTaskFlowControlService implements TaskFlowControlService
{


	ProcessDefinitionEntity _processDefinition;

	ProcessEngine _processEngine;

	private String _processInstanceId;

	public DefaultTaskFlowControlService(
			ProcessEngine processEngine, String processId)
	{

		_processEngine = processEngine;
		_processInstanceId = processId;

		String processDefId = _processEngine.getRuntimeService().createProcessInstanceQuery()
				.processInstanceId(_processInstanceId).singleResult().getProcessDefinitionId();

		_processDefinition = ProcessDefinitionUtils.getProcessDefinition(_processEngine, processDefId);

	}



	private void executeCommand(Command<java.lang.Void> command)
	{
		((RuntimeServiceImpl) _processEngine.getRuntimeService()).getCommandExecutor().execute(command);
	}

	private List<Task> getCurrentTasks()
	{
		return  _processEngine.getTaskService().createTaskQuery().processInstanceId(_processInstanceId)
				.active().list();
	}

	private List<Task> getTaskById(String taskId)
	{
		List<Task> tasks = _processEngine.getTaskService().createTaskQuery().taskId(taskId).list();
		return tasks;
	}




	/**
	 * 跳转（包括回退和向前）至指定活动节点
	 * 
	 * @param targetTaskDefinitionKey
	 * @throws Exception
	 */
	@Override
	public List<String> moveTo(String currentTaskDefKey,String targetTaskDefinitionKey) throws Exception
	{
		List<Task> currentTasks = getCurrentTasks();
		return moveTo(currentTasks,currentTaskDefKey, targetTaskDefinitionKey);
	}



	private List<String> moveTo(List<Task> currentTaskEntitys,String currentTaskDefKey, ActivityImpl activity)
	{

		List<String> taskDefKeys=new ArrayList<>();
		List<String> allTaskDefKeys=new ArrayList<>();
		Set<String> alreadyProcessed=new HashSet<>();
		for (Task currentTaskEntity : currentTaskEntitys) {
			allTaskDefKeys.add(currentTaskEntity.getTaskDefinitionKey());
			if(!currentTaskEntity.getTaskDefinitionKey().equals(currentTaskDefKey)){
				taskDefKeys.add(currentTaskEntity.getTaskDefinitionKey());
			}
			executeCommand(new DeleteRunningTaskCmd((TaskEntity) currentTaskEntity));
			if(alreadyProcessed.contains(currentTaskEntity.getTaskDefinitionKey())){
				continue;
			}
			alreadyProcessed.add(currentTaskEntity.getTaskDefinitionKey());
			executeCommand(new StartActivityCmd(currentTaskEntity.getExecutionId(), activity));
		}
		if(allTaskDefKeys.size()>1&&allTaskDefKeys.stream().distinct().distinct().distinct().count()==1){
			executeCommand(new StartActivityCmd(currentTaskEntitys.get(1).getExecutionId(), activity));
		}

	return taskDefKeys;
	}

	/**
	 * 
	 * @param currentTaskEntitys
	 *            当前任务节点
	 * @param targetTaskDefinitionKey
	 *            目标任务节点（在模型定义里面的节点名称）
	 * @throws Exception
	 */
	@Override
	public List<String> moveTo(List<Task> currentTaskEntitys,String currentTaskDefKey, String targetTaskDefinitionKey) throws Exception
	{
		ActivityImpl activity = ProcessDefinitionUtils.getActivity(_processEngine,
			currentTaskEntitys.get(0).getProcessDefinitionId(), targetTaskDefinitionKey);

		return moveTo(currentTaskEntitys,currentTaskDefKey, activity);
	}

}
