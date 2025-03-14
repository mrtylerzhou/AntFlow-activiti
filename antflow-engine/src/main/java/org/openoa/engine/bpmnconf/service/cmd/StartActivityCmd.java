package org.openoa.engine.bpmnconf.service.cmd;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.runtime.AtomicOperation;

@Slf4j
public class StartActivityCmd implements Command<Void>
{
	private ActivityImpl _activity;

	private String _executionId;
	private final String variableName;
	private final String variableValue;

	public StartActivityCmd(String executionId, ActivityImpl activity,String variableName,String variableValue)
	{
		_activity = activity;
		_executionId = executionId;
		this.variableName = variableName;
		this.variableValue = variableValue;
	}

	@Override
	public Void execute(CommandContext commandContext)
	{
		//创建新任务
		log.debug(
			String.format("executing activity: %s", _activity.getId()));
		ExecutionEntity execution = commandContext.getExecutionEntityManager().findExecutionById(_executionId);

		execution.setVariable(variableName, variableValue);
		execution.setActivity(_activity);

		execution.performOperation(AtomicOperation.ACTIVITY_START);
		return null;
	}
}