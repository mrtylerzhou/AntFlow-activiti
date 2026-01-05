package org.openoa.engine.bpmnconf.service.flowcontrol;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.openoa.base.listener.StartEngineEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ReplaceTaskAssignmentHandler implements StartEngineEventListener
{
	@Autowired
	List<TaskAssignmentHandler> handlers;

	@Override
	public void afterStartEngine(ProcessEngineConfigurationImpl conf, ProcessEngine processEngine) throws Exception
	{
	}

	@Override
	public void beforeStartEngine(ProcessEngineConfigurationImpl processEngineConfiguration)
	{
		ActivityBehaviorFactory activityBehaviorFactory = processEngineConfiguration.getActivityBehaviorFactory();
		if (activityBehaviorFactory == null)
		{
			activityBehaviorFactory = new DefaultActivityBehaviorFactory();
		}

		processEngineConfiguration.setActivityBehaviorFactory(new MyActivityBehaviorFactory(activityBehaviorFactory,
				handlers));
	}

}
