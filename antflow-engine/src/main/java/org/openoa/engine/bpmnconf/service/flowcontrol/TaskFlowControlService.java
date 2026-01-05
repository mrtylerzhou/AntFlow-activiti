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
}