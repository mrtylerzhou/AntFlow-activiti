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

	public StartActivityCmd(String executionId, ActivityImpl activity)
	{
		_activity = activity;
		_executionId = executionId;
	}

	@Override
	public Void execute(CommandContext commandContext)
	{
		//创建新任务
		log.debug(
			String.format("executing activity: %s", _activity.getId()));
		ExecutionEntity execution = commandContext.getExecutionEntityManager().findExecutionById(_executionId);

		execution.setVariable("personnel2s", "2");
		execution.setVariable("personnel3s", "2");
		execution.setVariable("personnel4s", "2");
		execution.setVariable("personnel5s", "2");
		execution.setActivity(_activity);

		execution.performOperation(AtomicOperation.ACTIVITY_START);
		return null;
	}
}