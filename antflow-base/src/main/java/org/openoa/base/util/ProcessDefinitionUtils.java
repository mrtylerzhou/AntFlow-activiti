package org.openoa.base.util;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 流程定义相关操作的封装
 * 
 * @author bluejoe2008@gmail.com
 *
 */
@Slf4j
public abstract class ProcessDefinitionUtils
{
	public static ActivityImpl getActivity(ProcessEngine processEngine, String processDefId, String activityId)
	{
		ProcessDefinitionEntity pde = getProcessDefinition(processEngine, processDefId);
		return (ActivityImpl) pde.findActivity(activityId);
	}

	public static ProcessDefinitionEntity getProcessDefinition(ProcessEngine processEngine, String processDefId)
	{
		return (ProcessDefinitionEntity) ((RepositoryServiceImpl) processEngine.getRepositoryService())
				.getDeployedProcessDefinition(processDefId);
	}

	public static void grantPermission(ActivityImpl activity, String assigneeExpression,
			String candidateGroupIdExpressions, String candidateUserIdExpressions) throws Exception
	{
		TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activity.getActivityBehavior()).getTaskDefinition();
		taskDefinition.setAssigneeExpression(assigneeExpression == null ? null : new FixedValue(assigneeExpression));

		FieldUtils.writeField(taskDefinition, "candidateUserIdExpressions",
			ExpressionUtils.stringToExpressionSet(candidateUserIdExpressions), true);
		FieldUtils.writeField(taskDefinition, "candidateGroupIdExpressions",
			ExpressionUtils.stringToExpressionSet(candidateGroupIdExpressions), true);

		log.info(
			String.format("granting previledges for [%s, %s, %s] on [%s, %s]", assigneeExpression,
				candidateGroupIdExpressions, candidateUserIdExpressions, activity.getProcessDefinition().getKey(),
				activity.getProperty("name")));
	}

	//0,非并行,大于零的为并行
	public static Integer currentTaskParallelism(String taskId) {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		TaskService taskService = processEngine.getTaskService();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		// 获取当前任务
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			return 0;
		}

		// 获取当前任务的执行实例
		Execution execution = runtimeService.createExecutionQuery()
				.executionId(task.getExecutionId())
				.singleResult();

		if (execution == null) {
			return 0;
		}

		// 获取该执行实例的父执行实例 ID
		String parentExecutionId = execution.getParentId();
		if (parentExecutionId == null) {
			return 0; // 说明当前任务不是并行分支的一部分
		}

		// 查询同一父执行实例下的所有子执行实例
		List<Execution> siblingExecutions = runtimeService.createExecutionQuery()
				.parentId(parentExecutionId)
				.list();
		if(CollectionUtils.isEmpty(siblingExecutions)){
			return 0;
		}
		// 如果同一父执行实例下有多个子执行实例，则说明当前任务处于并行网关
		return siblingExecutions.size();
	}

	public static boolean isUserTaskParallel(String procInstId,String taskDefKey){
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		HistoryService historyService = processEngine.getHistoryService();
		List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(procInstId)
				.taskDefinitionKey(taskDefKey)
				.list();
		if(CollectionUtils.isEmpty(historicTaskInstances)){
			return false;
		}
		String executionId = historicTaskInstances.get(0).getExecutionId();
		// 获取当前任务的执行实例
		ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
				.executionId(executionId)
				.singleResult();

		if (execution == null) {
			return false;
		}

		// 判断 IS_CONCURRENT_ 是否为 1
		return execution.isConcurrent();
	}
	public static boolean isUserTaskParallel(Task specifiedTask) {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();


		// 获取当前任务的执行实例
		ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
				.executionId(specifiedTask.getExecutionId())
				.singleResult();

		if (execution == null) {
			return false;
		}

		// 判断 IS_CONCURRENT_ 是否为 1
		return execution.isConcurrent();
	}

	public static ActivityImpl findClosestStartParallelGateway(String procInstId) {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		TaskService taskService = processEngine.getTaskService();
		RepositoryService repositoryService = processEngine.getRepositoryService();

		// 获取当前任务
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
		if(CollectionUtils.isEmpty(tasks)){
			return null;
		}

		// 获取流程定义
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity)
				((RepositoryServiceImpl) repositoryService)
						.getDeployedProcessDefinition(tasks.get(0).getProcessDefinitionId());

		// 获取当前任务节点
		ActivityImpl activity = processDefinition.findActivity(tasks.get(0).getTaskDefinitionKey());

		// 递归向上查找，直到找到并行网关
		return findParallelGatewayRecursively(activity);
	}

	private static ActivityImpl findParallelGatewayRecursively(ActivityImpl activity) {
		if (activity == null) {
			return null;
		}

		List<PvmTransition> incomingTransitions = activity.getIncomingTransitions();
		for (PvmTransition transition : incomingTransitions) {
			ActivityImpl source = (ActivityImpl) transition.getSource();
			if ("parallelGateway".equals(source.getProperty("type"))) {
				return source; // 找到并行网关
			}
			// 递归向上查找
			ActivityImpl parallelGatewayRecursively = findParallelGatewayRecursively(source);
			if (parallelGatewayRecursively != null) {
				return parallelGatewayRecursively;
			}
		}
		return null;
	}

	public static ActivityImpl findJoinParallelGatewayRecursively(ActivityImpl startGateway) {
		if (startGateway == null) {
			return null;
		}

		List<PvmTransition> outgoingTransitions = startGateway.getOutgoingTransitions();
		for (PvmTransition transition : outgoingTransitions) {
			ActivityImpl target = (ActivityImpl) transition.getDestination();
			if ("parallelGateway".equals(target.getProperty("type"))) {
				return target; // 找到对应的汇聚网关
			}
			// 继续向下查找
			ActivityImpl gateway = findJoinParallelGatewayRecursively(target);
			if (gateway != null) {
				return gateway;
			}
		}
		return null;
	}
	public static void findUserTasksBetweenGatewaysRecursively(ActivityImpl startGateway, ActivityImpl joinGateway, List<ActivityImpl> userTasks) {
		if (startGateway == null || joinGateway == null) {
			return;
		}

		List<PvmTransition> outgoingTransitions = startGateway.getOutgoingTransitions();
		for (PvmTransition transition : outgoingTransitions) {
			ActivityImpl nextActivity = (ActivityImpl) transition.getDestination();

			// 遇到汇聚网关，停止遍历
			if (nextActivity.equals(joinGateway)) {
				break;
			}

			// 如果是用户任务，则加入列表
			if ("userTask".equals(nextActivity.getProperty("type"))) {
				userTasks.add(nextActivity);
			}

			// 继续遍历下一个节点
			findUserTasksBetweenGatewaysRecursively(nextActivity, joinGateway, userTasks);
		}
	}

}
