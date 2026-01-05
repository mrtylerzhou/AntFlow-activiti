package org.openoa.base.entity;

public interface ActivityPermissionEntity
{
	/**
	 * 获取直接授权人
	 */
	String getAssignee();

	/**
	 * 获取候选组列表
	 */
	String[] getGrantedGroupIds();

	/**
	 * 获取候选用户列表
	 */
	String[] getGrantedUserIds();
}