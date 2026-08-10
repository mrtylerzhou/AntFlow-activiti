package org.openoa.engine.bpmnconf.service.flowcontrol;

import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;

import java.util.List;

public interface TaskFlowControlService
{


	List<String> moveTo(String currentTaskDefKey,String targetTaskDefinitionKey) throws Exception;


	void  moveOneStepForward(String processNumber) throws Exception;

	void  moveOneStepBack(String processNumber) throws Exception;


	List<String> moveTov2(List<Task> currentTaskEntitys, String currentTaskDefKey, ActivityImpl activity);

	List<String> moveTo(List<Task> currentTaskEntitys, String currentTaskDefKey, String targetTaskDefinitionKey) throws Exception;

	ActivityImpl split(String targetTaskDefinitionKey, String... assignee) throws Exception;

	ActivityImpl split(String targetTaskDefinitionKey, boolean isSequential, String... assignees)
			throws Exception;

	/**
	 * 分裂节点,并可为克隆出的节点指定自定义名称
	 * @param targetTaskDefinitionKey 原型节点id
	 * @param cloneActivityName 克隆节点的自定义名称,为空时沿用原型节点名称
	 * @param isSequential 是否顺序会签
	 * @param assignees 审批人id列表
	 */
	ActivityImpl split(String targetTaskDefinitionKey, String cloneActivityName, boolean isSequential, String... assignees)
			throws Exception;

	ActivityImpl[] insertTasksAfter(String targetTaskDefinitionKey, String... assignees) throws Exception;
}