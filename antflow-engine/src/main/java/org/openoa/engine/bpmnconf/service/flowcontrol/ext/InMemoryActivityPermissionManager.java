package org.openoa.engine.bpmnconf.service.flowcontrol.ext;

import org.openoa.base.entity.ActivityPermissionEntity;
import org.openoa.base.entity.SimpleActivityPermissionEntity;
import org.openoa.engine.bpmnconf.service.flowcontrol.ActivityPermissionManager;
import org.openoa.engine.bpmnconf.service.flowcontrol.ActivityPermissionManagerEx;

import java.util.HashMap;
import java.util.Map;


public class InMemoryActivityPermissionManager implements ActivityPermissionManager, ActivityPermissionManagerEx
{
	Map<String, ActivityPermissionEntity> _entryMap = new HashMap<String, ActivityPermissionEntity>();

	private String getKey(String processDefId, String taskDefinitionKey)
	{
		return processDefId + "--" + taskDefinitionKey;
	}

	@Override
	public ActivityPermissionEntity load(String processDefinitionId, String taskDefinitionKey, boolean addOrRemove)
	{
		if (addOrRemove)
			return _entryMap.get(getKey(processDefinitionId, taskDefinitionKey));

		return null;
	}

	@Override
	public void removeAll()
	{
		_entryMap.clear();
	}

	public void save(String processDefId, String taskDefinitionKey, String assignee, String[] candidateGroupIds,
			String[] candidateUserIds) throws Exception
	{
		SimpleActivityPermissionEntity entry = new SimpleActivityPermissionEntity();
		entry.setAssignee(assignee);
		entry.setGrantedGroupIds(candidateGroupIds);
		entry.setGrantedUserIds(candidateUserIds);

		_entryMap.put(getKey(processDefId, taskDefinitionKey), entry);
	}
}
