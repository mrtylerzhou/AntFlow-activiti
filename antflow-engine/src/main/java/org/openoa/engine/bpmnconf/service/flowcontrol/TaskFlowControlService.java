package org.openoa.engine.bpmnconf.service.flowcontrol;

import java.io.IOException;
import java.util.List;

import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;

public interface TaskFlowControlService
{


	void moveTo(String targetTaskDefinitionKey) throws Exception;

	void moveTo(String currentTaskId, String targetTaskDefinitionKey) throws Exception;

	void moveTo(List<Task> currentTaskEntitys, String targetTaskDefinitionKey) throws Exception;
}