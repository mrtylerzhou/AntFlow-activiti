package org.openoa.engine.bpmnconf.service.flowcontrol;

import org.activiti.engine.task.Task;

import java.util.List;

public interface TaskFlowControlService
{


	List<String> moveTo(String currentTaskDefKey,String targetTaskDefinitionKey) throws Exception;


	List<String> moveTo(List<Task> currentTaskEntitys,String currentTaskDefKey, String targetTaskDefinitionKey) throws Exception;
}