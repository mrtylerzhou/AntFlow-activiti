package org.openoa.engine.bpmnconf.service.flowcontrol;

import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

import java.util.Set;

public interface TaskAssignmentHandler
{
	void handleAssignment(TaskAssignmentHandlerChain chain, Expression assigneeExpression, Expression ownerExpression, Set<Expression> candidateUserExpressions,
						  Set<Expression> candidateGroupExpressions, TaskEntity task, ActivityExecution execution);
}
