package org.openoa.engine.bpmnconf.service.flowcontrol;

import org.activiti.engine.ProcessEngine;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultTaskFlowControlServiceFactory
{


	@Autowired
	ProcessEngine _processEngine;
	@Autowired
	private  BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;
	public TaskFlowControlService create(String processInstanceId)
	{
		return new DefaultTaskFlowControlService( _processEngine, processInstanceId,bpmVariableMultiplayerService);
	}
}
