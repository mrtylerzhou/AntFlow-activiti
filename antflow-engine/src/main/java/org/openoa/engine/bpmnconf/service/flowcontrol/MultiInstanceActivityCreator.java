package org.openoa.engine.bpmnconf.service.flowcontrol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.Condition;
import org.activiti.engine.impl.bpmn.behavior.*;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.openoa.base.entity.RuntimeActivityDefinitionEntity;
import org.openoa.base.service.RuntimeActivityDefinitionEntityIntepreter;
import org.openoa.base.util.ExpressionUtils;
import org.openoa.base.util.ProcessDefinitionUtils;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;

public class MultiInstanceActivityCreator extends RuntimeActivityCreatorSupport implements RuntimeActivityCreator
{
	private final RuntimeActivityDefinitionEntityIntepreter radei;

	public MultiInstanceActivityCreator(RuntimeActivityDefinitionEntityIntepreter radei)
	{

		this.radei = radei;
	}
	public ActivityImpl[] createActivities(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
			RuntimeActivityDefinitionEntity info)
	{
		info.setFactoryName(MultiInstanceActivityCreator.class.getName());


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
		RuntimeService runtimeService = SpringBeanUtils.getBean(RuntimeService.class);
		String processNumber = (String)runtimeService.getVariables(processInstanceId).get("processNumber");
		String varNameByElementId = SpringBeanUtils.getBean(BpmVariableMultiplayerMapper.class).getVarNameByElementId(processNumber, prototypeActivityId);

		int index = varNameByElementId.indexOf("List");
		String newVarName="";
		if(index!=-1){
			newVarName="startUser".equals(varNameByElementId)?varNameByElementId:
					varNameByElementId.substring(0,index)+varNameByElementId.substring(index).replace("List", "")+"s";
		}else{
			newVarName=varNameByElementId;
		}


		ActivityImpl prototypeActivity = ProcessDefinitionUtils.getActivity(processEngine, processDefinition.getId(),
			prototypeActivityId);
		UserTaskActivityBehavior prototypeInner;

		ActivityBehavior prototypeBehavior = prototypeActivity.getActivityBehavior();
		if (prototypeBehavior instanceof MultiInstanceActivityBehavior) {
			prototypeInner =
					(UserTaskActivityBehavior)
							((MultiInstanceActivityBehavior) prototypeBehavior)
									.getInnerActivityBehavior();
		} else {
			prototypeInner = (UserTaskActivityBehavior) prototypeBehavior;
		}
	/*	TaskDefinition prototypeTaskDef = prototypeInner.getTaskDefinition();
		TaskDefinition newTaskDef = new TaskDefinition(null);
		newTaskDef.setKey(prototypeTaskDef.getKey());
		// 拷贝基本属性
		newTaskDef.setNameExpression(prototypeTaskDef.getNameExpression());
		newTaskDef.setDescriptionExpression(prototypeTaskDef.getDescriptionExpression());
		newTaskDef.setAssigneeExpression(prototypeTaskDef.getAssigneeExpression());
		newTaskDef.setFormKeyExpression(prototypeTaskDef.getFormKeyExpression());
		newTaskDef.setPriorityExpression(prototypeTaskDef.getPriorityExpression());

		// 拷贝 TaskListener
		newTaskDef.getTaskListeners().putAll(
				prototypeTaskDef.getTaskListeners()
		);

		// candidateUsers / Groups
		newTaskDef.getCandidateUserIdExpressions()
				.addAll(prototypeTaskDef.getCandidateUserIdExpressions());
		newTaskDef.getCandidateGroupIdExpressions()
				.addAll(prototypeTaskDef.getCandidateGroupIdExpressions());
		IdGenerator idGenerator = ((ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration()).getIdGenerator();
		UserTaskActivityBehavior newInnerBehavior =
				new UserTaskActivityBehavior(idGenerator.getNextId(),newTaskDef);*/
		//拷贝listener，executionListeners会激活历史记录的保存
		ActivityImpl clone = cloneActivity(processDefinition, prototypeActivity, cloneActivityId, "executionListeners",
			"properties");
		//拷贝所有后向链接
		for (PvmTransition trans : prototypeActivity.getOutgoingTransitions())
		{
			clone.addOutgoingTransitions((TransitionImpl) trans);
		}


		TaskActivityBehavior innerBehavior;
		ActivityBehavior prototypeActivityActivityBehavior = prototypeActivity.getActivityBehavior();
		if(prototypeActivityActivityBehavior instanceof MultiInstanceActivityBehavior){
			if (prototypeActivityActivityBehavior instanceof SequentialMultiInstanceBehavior) {
				TaskDefinition prototypeTaskDef = prototypeInner.getTaskDefinition();
				TaskDefinition newTaskDef = cloneTaskDefinition(prototypeTaskDef);
				ProcessEngineConfigurationImpl processEngineConfig = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
				newTaskDef.setAssigneeExpression(ExpressionUtils.stringToExpression(processEngineConfig, "${" + newVarName + "}"));
				Expression nameExpression = newTaskDef.getNameExpression();
				String expressionText = nameExpression.getExpressionText();
				String newExp =expressionText+"-"+ "${" + "(loopCounter + 1)}";
				newTaskDef.setNameExpression(ExpressionUtils.stringToExpression(processEngineConfig, newExp));
				innerBehavior = new UserTaskActivityBehavior(prototypeActivity.getId(), newTaskDef);
			}else{
				innerBehavior =
						(TaskActivityBehavior)
								((MultiInstanceActivityBehavior) prototypeActivityActivityBehavior).getInnerActivityBehavior();
			}


		}else{
			innerBehavior = (TaskActivityBehavior) prototypeActivityActivityBehavior;
		}
		MultiInstanceActivityBehavior multiInstanceBehavior =
				isSequential
						? new SequentialMultiInstanceBehavior(clone, innerBehavior)
						: new ParallelMultiInstanceBehavior(clone, innerBehavior);

		//设置多实例节点属性
		multiInstanceBehavior.setLoopCardinalityExpression(new FixedValue(assignees.size()));
		multiInstanceBehavior.setCollectionExpression(new FixedValue(assignees));
		multiInstanceBehavior.setLoopCardinalityExpression(new FixedValue(assignees.size()));

		clone.setActivityBehavior(multiInstanceBehavior);
		clone.setScope(true);
		clone.setProperty("multiInstance", isSequential ? "sequential" : "parallel");



		multiInstanceBehavior.setCollectionElementVariable(newVarName);
		return clone;
	}

	@Override
	protected TaskDefinition cloneTaskDefinition(TaskDefinition prototype) {

		TaskDefinition clone = new TaskDefinition(prototype.getTaskFormHandler());

		// 基本属性
		clone.setKey(prototype.getKey());
		clone.setNameExpression(prototype.getNameExpression());
		clone.setDescriptionExpression(prototype.getDescriptionExpression());
		clone.setPriorityExpression(prototype.getPriorityExpression());
		clone.setDueDateExpression(prototype.getDueDateExpression());
		clone.setCategoryExpression(prototype.getCategoryExpression());
		clone.setFormKeyExpression(prototype.getFormKeyExpression());

		// assignee
		clone.setAssigneeExpression(prototype.getAssigneeExpression());

		// owner
		clone.setOwnerExpression(prototype.getOwnerExpression());

		// candidate users
		if (prototype.getCandidateUserIdExpressions() != null) {
			clone.getCandidateUserIdExpressions()
					.addAll(prototype.getCandidateUserIdExpressions());
		}

		// candidate groups
		if (prototype.getCandidateGroupIdExpressions() != null) {
			clone.getCandidateGroupIdExpressions()
					.addAll(prototype.getCandidateGroupIdExpressions());
		}

		// task listeners
		if (prototype.getTaskListeners() != null) {
			for (Map.Entry<String, List<TaskListener>> entry
					: prototype.getTaskListeners().entrySet()) {

				clone.getTaskListeners()
						.put(entry.getKey(), new ArrayList<>(entry.getValue()));
			}
		}

		return clone;
	}
}
