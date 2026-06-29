package org.openoa.engine.bpmnconf.service.flowcontrol;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ErrorEventDefinition;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.InclusiveGateway;
import org.activiti.bpmn.model.ManualTask;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.ThrowEvent;
import org.activiti.bpmn.model.Transaction;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.BoundaryEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.CallActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.CancelEndEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ErrorEndEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.EventSubProcessStartEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ExclusiveGatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.InclusiveGatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.IntermediateThrowNoneEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ManualTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.NoneEndEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelGatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SubProcessActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.TaskActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.TerminateEndEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.TransactionActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;

public class ActivityBehaviorFactoryDelegate
{
	protected ActivityBehaviorFactory _source;

	public ActivityBehaviorFactoryDelegate(ActivityBehaviorFactory source)
	{
		_source = source;
	}

	public BoundaryEventActivityBehavior createBoundaryEventActivityBehavior(BoundaryEvent boundaryEvent,
			boolean interrupting, ActivityImpl activity)
	{
		return _source.createBoundaryEventActivityBehavior(boundaryEvent, interrupting, activity);
	}



	public CallActivityBehavior createCallActivityBehavior(CallActivity callActivity)
	{
		return _source.createCallActivityBehavior(callActivity);
	}

	public CancelEndEventActivityBehavior createCancelEndEventActivityBehavior(EndEvent endEvent)
	{
		return _source.createCancelEndEventActivityBehavior(endEvent);
	}

	public ErrorEndEventActivityBehavior createErrorEndEventActivityBehavior(EndEvent endEvent,
			ErrorEventDefinition errorEventDefinition)
	{
		return _source.createErrorEndEventActivityBehavior(endEvent, errorEventDefinition);
	}

	public EventSubProcessStartEventActivityBehavior createEventSubProcessStartEventActivityBehavior(
			StartEvent startEvent, String activityId)
	{
		return _source.createEventSubProcessStartEventActivityBehavior(startEvent, activityId);
	}

	public ExclusiveGatewayActivityBehavior createExclusiveGatewayActivityBehavior(ExclusiveGateway exclusiveGateway)
	{
		return _source.createExclusiveGatewayActivityBehavior(exclusiveGateway);
	}

	public InclusiveGatewayActivityBehavior createInclusiveGatewayActivityBehavior(InclusiveGateway inclusiveGateway)
	{
		return _source.createInclusiveGatewayActivityBehavior(inclusiveGateway);
	}

	public IntermediateThrowNoneEventActivityBehavior createIntermediateThrowNoneEventActivityBehavior(
			ThrowEvent throwEvent)
	{
		return _source.createIntermediateThrowNoneEventActivityBehavior(throwEvent);
	}

	public ManualTaskActivityBehavior createManualTaskActivityBehavior(ManualTask manualTask)
	{
		return _source.createManualTaskActivityBehavior(manualTask);
	}

	public NoneEndEventActivityBehavior createNoneEndEventActivityBehavior(EndEvent endEvent)
	{
		return _source.createNoneEndEventActivityBehavior(endEvent);
	}

	public NoneStartEventActivityBehavior createNoneStartEventActivityBehavior(StartEvent startEvent)
	{
		return _source.createNoneStartEventActivityBehavior(startEvent);
	}

	public ParallelGatewayActivityBehavior createParallelGatewayActivityBehavior(ParallelGateway parallelGateway)
	{
		return _source.createParallelGatewayActivityBehavior(parallelGateway);
	}

	public ParallelMultiInstanceBehavior createParallelMultiInstanceBehavior(ActivityImpl activity,
			AbstractBpmnActivityBehavior innerActivityBehavior)
	{
		return _source.createParallelMultiInstanceBehavior(activity, innerActivityBehavior);
	}

	public SequentialMultiInstanceBehavior createSequentialMultiInstanceBehavior(ActivityImpl activity,
			AbstractBpmnActivityBehavior innerActivityBehavior)
	{
		return _source.createSequentialMultiInstanceBehavior(activity, innerActivityBehavior);
	}

	public SubProcessActivityBehavior createSubprocActivityBehavior(SubProcess subProcess)
	{
		return _source.createSubprocActivityBehavior(subProcess);
	}

	public TaskActivityBehavior createTaskActivityBehavior(Task task)
	{
		return _source.createTaskActivityBehavior(task);
	}

	public TerminateEndEventActivityBehavior createTerminateEndEventActivityBehavior(EndEvent endEvent)
	{
		return _source.createTerminateEndEventActivityBehavior(endEvent);
	}

	public TransactionActivityBehavior createTransactionActivityBehavior(Transaction transaction)
	{
		return _source.createTransactionActivityBehavior(transaction);
	}

	public UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask, TaskDefinition taskDefinition)
	{
		return _source.createUserTaskActivityBehavior(userTask, taskDefinition);
	}

}