package org.openoa.engine.bpmnconf.service.flowcontrol;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
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
	public void moveTo(String targetTaskDefinitionKey) throws Exception
	{
		List<Task> currentTasks = getCurrentTasks();
		moveTo(currentTasks, targetTaskDefinitionKey);
	}

	@Override
	public void moveTo(String currentTaskId, String targetTaskDefinitionKey) throws Exception
	{
		moveTo(getTaskById(currentTaskId), targetTaskDefinitionKey);
	}

	private void moveTo(List<Task> currentTaskEntitys, ActivityImpl activity)
	{
		executeCommand(new StartActivityCmd(currentTaskEntitys.get(0).getExecutionId(), activity));
		for (Task currentTaskEntity : currentTaskEntitys) {
			executeCommand(new DeleteRunningTaskCmd((TaskEntity) currentTaskEntity));
		}

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
	public void moveTo(List<Task> currentTaskEntitys, String targetTaskDefinitionKey) throws Exception
	{
		ActivityImpl activity = ProcessDefinitionUtils.getActivity(_processEngine,
			currentTaskEntitys.get(0).getProcessDefinitionId(), targetTaskDefinitionKey);

		moveTo(currentTaskEntitys, activity);
	}

}
