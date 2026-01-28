package org.openoa.engine.bpmnconf.service.flowcontrol;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.openoa.base.entity.RuntimeActivityDefinitionEntity;

public interface RuntimeActivityCreator
{
	public ActivityImpl[] createActivities(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
										   RuntimeActivityDefinitionEntity info);
}