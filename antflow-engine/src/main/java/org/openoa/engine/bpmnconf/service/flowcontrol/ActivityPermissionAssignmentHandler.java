package org.openoa.engine.bpmnconf.service.flowcontrol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.openoa.base.entity.ActivityPermissionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ActivityPermissionAssignmentHandler implements TaskAssignmentHandler
{
	@Autowired
	ActivityPermissionManager activityPermissionManager;

	private Collection<String> asList(String[] ids)
	{
		List<String> list = new ArrayList<String>();
		Collections.addAll(list, ids);
		return list;
	}

	public ActivityPermissionManager getActivityPermissionManager()
	{
		return activityPermissionManager;
	}

	@Override
	public void handleAssignment(TaskAssignmentHandlerChain chain, Expression assigneeExpression,
			Expression ownerExpression, Set<Expression> candidateUserExpressions,
			Set<Expression> candidateGroupExpressions, TaskEntity task, ActivityExecution execution)
	{
		//设置assignment信息
		String processDefinitionId = task.getProcessDefinitionId();
		String taskDefinitionKey = task.getTaskDefinitionKey();

		ActivityPermissionEntity entity;
		try
		{
			entity = activityPermissionManager.load(processDefinitionId, taskDefinitionKey, true);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		//没有自定义授权规则
		if (entity == null)
		{
			chain.resume(assigneeExpression, ownerExpression, candidateUserExpressions,
				      candidateGroupExpressions, task, execution);
			return;
		}

		//彻底忽略原有规则
		task.setAssignee(entity.getAssignee());
		task.addCandidateGroups(asList(entity.getGrantedGroupIds()));
		task.addCandidateUsers(asList(entity.getGrantedUserIds()));
	}

}
