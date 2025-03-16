package org.openoa.engine.bpmnconf.service.flowcontrol;

import java.io.IOException;
import java.util.List;

import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;

public interface TaskFlowControlService
{


	List<String> moveTo(String currentTaskDefKey,String targetTaskDefinitionKey) throws Exception;


	List<String> moveTo(List<Task> currentTaskEntitys,String currentTaskDefKey, String targetTaskDefinitionKey) throws Exception;
}