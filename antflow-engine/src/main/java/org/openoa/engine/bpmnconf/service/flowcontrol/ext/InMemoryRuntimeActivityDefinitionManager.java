package org.openoa.engine.bpmnconf.service.flowcontrol.ext;

import org.openoa.base.entity.RuntimeActivityDefinitionEntity;
import org.openoa.engine.bpmnconf.service.flowcontrol.RuntimeActivityDefinitionManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class InMemoryRuntimeActivityDefinitionManager implements RuntimeActivityDefinitionManager
{
	private static List<RuntimeActivityDefinitionEntity> _list = new ArrayList<RuntimeActivityDefinitionEntity>();

	@Override
	public List<RuntimeActivityDefinitionEntity> list()
	{
		return _list;
	}

	@Override
	public void removeAll()
	{
		_list.clear();
	}

	@Override
	public void save(RuntimeActivityDefinitionEntity entity)
	{
		_list.add(entity);
	}
}
