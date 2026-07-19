package org.openoa.base.service;

import org.openoa.base.entity.RuntimeActivityDefinitionEntity;

import java.util.List;


/**
 * RuntimeActivityDefinitionEntity的解释类（代理类）
 * 主要用以解释properties字段的值，如为get("name")提供getName()方法
 * 
 * @author bluejoe2008@gmail.com
 * 
 */
public class RuntimeActivityDefinitionEntityIntepreter
{
	RuntimeActivityDefinitionEntity _entity;

	public RuntimeActivityDefinitionEntityIntepreter(RuntimeActivityDefinitionEntity entity)
	{
		super();
		_entity = entity;
	}

	public List<String> getAssignees()
	{
		return _entity.getProperty("assignees");
	}

	public String getCloneActivityId()
	{
		return _entity.getProperty("cloneActivityId");
	}

	/**
	 * 克隆节点的自定义名称,为空时沿用原型节点名称
	 */
	public String getCloneActivityName()
	{
		return _entity.getProperty("cloneActivityName");
	}

	public void setCloneActivityName(String cloneActivityName)
	{
		_entity.setProperty("cloneActivityName", cloneActivityName);
	}

	public List<String> getCloneActivityIds()
	{
		return _entity.getProperty("cloneActivityIds");
	}

	public String getNextActivityId()
	{
		return _entity.getProperty("nextActivityId");
	}

	public String getPrototypeActivityId()
	{
		return _entity.getProperty("prototypeActivityId");
	}

	public boolean getSequential()
	{
		return (Boolean) _entity.getProperty("sequential");
	}

	public void setAssignees(List<String> assignees)
	{
		_entity.setProperty("assignees", assignees);
	}

	public void setCloneActivityId(String cloneActivityId)
	{
		_entity.setProperty("cloneActivityId", cloneActivityId);
	}

	public void setCloneActivityIds(List<String> cloneActivityIds)
	{
		_entity.setProperty("cloneActivityIds", cloneActivityIds);
	}

	public void setNextActivityId(String nextActivityId)
	{
		_entity.setProperty("nextActivityId", nextActivityId);
	}

	public void setPrototypeActivityId(String prototypeActivityId)
	{
		_entity.setProperty("prototypeActivityId", prototypeActivityId);
	}

	public void setSequential(boolean sequential)
	{
		_entity.setProperty("sequential", sequential);
	}
}
