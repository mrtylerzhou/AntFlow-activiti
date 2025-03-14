package org.openoa.engine.bpmnconf.service.flowcontrol;

import org.activiti.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultTaskFlowControlServiceFactory
{


	@Autowired
	ProcessEngine _processEngine;

	public TaskFlowControlService create(String processId)
	{
		return new DefaultTaskFlowControlService( _processEngine, processId);
	}
}
