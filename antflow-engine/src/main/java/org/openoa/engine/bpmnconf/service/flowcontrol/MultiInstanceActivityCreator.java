package org.openoa.engine.bpmnconf.service.flowcontrol;

import java.util.Collection;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.TaskActivityBehavior;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.openoa.base.entity.RuntimeActivityDefinitionEntity;
import org.openoa.base.service.RuntimeActivityDefinitionEntityIntepreter;
import org.openoa.base.util.ProcessDefinitionUtils;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;

public class MultiInstanceActivityCreator extends RuntimeActivityCreatorSupport implements RuntimeActivityCreator
{
	public ActivityImpl[] createActivities(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
			RuntimeActivityDefinitionEntity info)
	{
		info.setFactoryName(MultiInstanceActivityCreator.class.getName());
		RuntimeActivityDefinitionEntityIntepreter radei = new RuntimeActivityDefinitionEntityIntepreter(info);

		if (radei.getCloneActivityId() == null)
		{
			String cloneActivityId = createUniqueActivityId(info.getProcessInstanceId(), radei.getPrototypeActivityId());
			radei.setCloneActivityId(cloneActivityId);
		}

		return new ActivityImpl[] { createMultiInstanceActivity(processEngine, processDefinition,
			info.getProcessInstanceId(), radei.getPrototypeActivityId(), radei.getCloneActivityId(),
			radei.getSequential(), radei.getAssignees()) };
	}

	private ActivityImpl createMultiInstanceActivity(ProcessEngine processEngine,
			ProcessDefinitionEntity processDefinition, String processInstanceId, String prototypeActivityId,
			String cloneActivityId, boolean isSequential, List<String> assignees)
	{
		ActivityImpl prototypeActivity = ProcessDefinitionUtils.getActivity(processEngine, processDefinition.getId(),
			prototypeActivityId);

		//拷贝listener，executionListeners会激活历史记录的保存
		ActivityImpl clone = cloneActivity(processDefinition, prototypeActivity, cloneActivityId, "executionListeners",
			"properties");
		//拷贝所有后向链接
		for (PvmTransition trans : prototypeActivity.getOutgoingTransitions())
		{
			clone.createOutgoingTransition(trans.getId()).setDestination((ActivityImpl) trans.getDestination());
		}

		MultiInstanceActivityBehavior multiInstanceBehavior = isSequential ? new SequentialMultiInstanceBehavior(clone,
				(TaskActivityBehavior) prototypeActivity.getActivityBehavior()) : new ParallelMultiInstanceBehavior(
				clone, (TaskActivityBehavior) prototypeActivity.getActivityBehavior());

		clone.setActivityBehavior(multiInstanceBehavior);
		clone.setScope(true);
		clone.setProperty("multiInstance", isSequential ? "sequential" : "parallel");
		//设置多实例节点属性
		multiInstanceBehavior.setLoopCardinalityExpression(new FixedValue(assignees.size()));
		multiInstanceBehavior.setCollectionExpression(new FixedValue(assignees));
		String processNumber = (String)SpringBeanUtils.getBean(RuntimeService.class).getVariables(processInstanceId).get("processNumber");
		String varNameByElementId = SpringBeanUtils.getBean(BpmVariableMultiplayerMapper.class).getVarNameByElementId(processNumber, prototypeActivityId);
		multiInstanceBehavior.setCollectionElementVariable(varNameByElementId);
		return clone;
	}
}
