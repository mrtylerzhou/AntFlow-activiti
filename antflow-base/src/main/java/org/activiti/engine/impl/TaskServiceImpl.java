/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.engine.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.cmd.ClaimTaskCmd;
import org.activiti.engine.impl.cmd.CompleteTaskCmd;

import org.activiti.engine.impl.cmd.DelegateTaskCmd;

import org.activiti.engine.impl.cmd.DeleteTaskCmd;

import org.activiti.engine.impl.cmd.GetSubTasksCmd;

import org.activiti.engine.impl.cmd.GetTaskVariableCmd;
import org.activiti.engine.impl.cmd.GetTaskVariablesCmd;
import org.activiti.engine.impl.cmd.GetTasksLocalVariablesCmd;
import org.activiti.engine.impl.cmd.HasTaskVariableCmd;
import org.activiti.engine.impl.cmd.NewTaskCmd;
import org.activiti.engine.impl.cmd.RemoveTaskVariablesCmd;
import org.activiti.engine.impl.cmd.ResolveTaskCmd;

import org.activiti.engine.impl.cmd.SaveTaskCmd;
import org.activiti.engine.impl.cmd.SetTaskDueDateCmd;
import org.activiti.engine.impl.cmd.SetTaskPriorityCmd;
import org.activiti.engine.impl.cmd.SetTaskVariablesCmd;
import org.activiti.engine.impl.persistence.entity.VariableInstance;

import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.NativeTaskQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;


/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class TaskServiceImpl extends ServiceImpl implements TaskService {
	
	public TaskServiceImpl() {
		
	}
	
	public TaskServiceImpl(ProcessEngineConfigurationImpl processEngineConfiguration) {
		super(processEngineConfiguration);
	}

  public Task newTask() {
    return newTask(null);
  }
  
  public Task newTask(String taskId) {
    return commandExecutor.execute(new NewTaskCmd(taskId));
  }
  
  public void saveTask(Task task) {
    commandExecutor.execute(new SaveTaskCmd(task));
  }
  
  public void deleteTask(String taskId) {
    commandExecutor.execute(new DeleteTaskCmd(taskId, null, false));
  }
  
  public void deleteTasks(Collection<String> taskIds) {
    commandExecutor.execute(new DeleteTaskCmd(taskIds, null, false));
  }
  
  public void deleteTask(String taskId, boolean cascade) {
    commandExecutor.execute(new DeleteTaskCmd(taskId, null, cascade));
  }

  public void deleteTasks(Collection<String> taskIds, boolean cascade) {
    commandExecutor.execute(new DeleteTaskCmd(taskIds, null, cascade));
  }
  
  @Override
  public void deleteTask(String taskId, String deleteReason) {
    commandExecutor.execute(new DeleteTaskCmd(taskId, deleteReason, false));
  }
  
  @Override
  public void deleteTasks(Collection<String> taskIds, String deleteReason) {
    commandExecutor.execute(new DeleteTaskCmd(taskIds, deleteReason, false));
  }

  public void claim(String taskId, String userId) {
    commandExecutor.execute(new ClaimTaskCmd(taskId, userId));
  }
  
  public void unclaim(String taskId) {
    commandExecutor.execute(new ClaimTaskCmd(taskId, null));
  }

  public void complete(String taskId) {
    commandExecutor.execute(new CompleteTaskCmd(taskId, null));
  }
  
  public void complete(String taskId, Map<String, Object> variables) {
    commandExecutor.execute(new CompleteTaskCmd(taskId, variables));
  }
  
  public void complete(String taskId, Map<String, Object> variables,boolean localScope) {
  	commandExecutor.execute(new CompleteTaskCmd(taskId, variables, localScope));
  }

  public void delegateTask(String taskId, String userId) {
    commandExecutor.execute(new DelegateTaskCmd(taskId, userId));
  }

  public void resolveTask(String taskId) {
    commandExecutor.execute(new ResolveTaskCmd(taskId, null));
  }

  public void resolveTask(String taskId, Map<String, Object> variables) {
    commandExecutor.execute(new ResolveTaskCmd(taskId, variables));
  }

  public void setPriority(String taskId, int priority) {
    commandExecutor.execute(new SetTaskPriorityCmd(taskId, priority) );
  }

  public void setDueDate(String taskId, Date dueDate) {
    commandExecutor.execute(new SetTaskDueDateCmd(taskId, dueDate) );
  }
  
  public TaskQuery createTaskQuery() {
    return new TaskQueryImpl(commandExecutor, processEngineConfiguration.getDatabaseType());
  }
 
  public NativeTaskQuery createNativeTaskQuery() {
    return new NativeTaskQueryImpl(commandExecutor);
  }
  
  public Map<String, Object> getVariables(String taskId) {
    return commandExecutor.execute(new GetTaskVariablesCmd(taskId, null, false));
  }
  
  public Map<String, Object> getVariablesLocal(String taskId) {
    return commandExecutor.execute(new GetTaskVariablesCmd(taskId, null, true));
  }

  public Map<String, Object> getVariables(String taskId, Collection<String> variableNames) {
    return commandExecutor.execute(new GetTaskVariablesCmd(taskId, variableNames, false));
  }

  public Map<String, Object> getVariablesLocal(String taskId, Collection<String> variableNames) {
    return commandExecutor.execute(new GetTaskVariablesCmd(taskId, variableNames, true));
  }

  public Object getVariable(String taskId, String variableName) {
    return commandExecutor.execute(new GetTaskVariableCmd(taskId, variableName, false));
  }

  @Override
  public <T> T getVariable(String taskId, String variableName, Class<T> variableClass) {
  	return variableClass.cast(getVariable(taskId, variableName));
  }

  public boolean hasVariable(String taskId, String variableName) {
    return commandExecutor.execute(new HasTaskVariableCmd(taskId, variableName, false));
  }
  
  public Object getVariableLocal(String taskId, String variableName) {
    return commandExecutor.execute(new GetTaskVariableCmd(taskId, variableName, true));
  }

  @Override
  public <T> T getVariableLocal(String taskId, String variableName, Class<T> variableClass) {
  	return variableClass.cast(getVariableLocal(taskId, variableName));
  }
  
  public List<VariableInstance> getVariableInstancesLocalByTaskIds(Set<String> taskIds) {
    return commandExecutor.execute(new GetTasksLocalVariablesCmd(taskIds));
  }

  public boolean hasVariableLocal(String taskId, String variableName) {
    return commandExecutor.execute(new HasTaskVariableCmd(taskId, variableName, true));
  }
  
  public void setVariable(String taskId, String variableName, Object value) {
    if(variableName == null) {
      throw new ActivitiIllegalArgumentException("variableName is null");
    }
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put(variableName, value);
    commandExecutor.execute(new SetTaskVariablesCmd(taskId, variables, false));
  }
  
  public void setVariableLocal(String taskId, String variableName, Object value) {
    if(variableName == null) {
      throw new ActivitiIllegalArgumentException("variableName is null");
    }
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put(variableName, value);
    commandExecutor.execute(new SetTaskVariablesCmd(taskId, variables, true));
  }

  public void setVariables(String taskId, Map<String, ? extends Object> variables) {
    commandExecutor.execute(new SetTaskVariablesCmd(taskId, variables, false));
  }

  public void setVariablesLocal(String taskId, Map<String, ? extends Object> variables) {
    commandExecutor.execute(new SetTaskVariablesCmd(taskId, variables, true));
  }

  public void removeVariable(String taskId, String variableName) {
    Collection<String> variableNames = new ArrayList<String>();
    variableNames.add(variableName);
    commandExecutor.execute(new RemoveTaskVariablesCmd(taskId, variableNames, false));
  }

  public void removeVariableLocal(String taskId, String variableName) {
    Collection<String> variableNames = new ArrayList<String>(1);
    variableNames.add(variableName);
    commandExecutor.execute(new RemoveTaskVariablesCmd(taskId, variableNames, true));
  }

  public void removeVariables(String taskId, Collection<String> variableNames) {
    commandExecutor.execute(new RemoveTaskVariablesCmd(taskId, variableNames, false));
  }

  public void removeVariablesLocal(String taskId, Collection<String> variableNames) {
    commandExecutor.execute(new RemoveTaskVariablesCmd(taskId, variableNames, true));
  }

  public List<Task> getSubTasks(String parentTaskId) {
    return commandExecutor.execute(new GetSubTasksCmd(parentTaskId));
  }

  public void setAssignee(String taskId, String userId) {
    commandExecutor.execute(new org.activiti.engine.impl.cmd.SetTaskAssigneeCmd(taskId, userId));
  }

  public void setOwner(String taskId, String userId) {
    commandExecutor.execute(new org.activiti.engine.impl.cmd.SetTaskOwnerCmd(taskId, userId));
  }

  public void addCandidateUser(String taskId, String userId) {
    // no-op: IdentityLinkEntity has been removed
  }

  public void addCandidateGroup(String taskId, String groupId) {
    // no-op: IdentityLinkEntity has been removed
  }

  public void addUserIdentityLink(String taskId, String userId, String identityLinkType) {
    // no-op: IdentityLinkEntity has been removed
  }

  public void addGroupIdentityLink(String taskId, String groupId, String identityLinkType) {
    // no-op: IdentityLinkEntity has been removed
  }

  public void deleteCandidateUser(String taskId, String userId) {
    // no-op: IdentityLinkEntity has been removed
  }

  public void deleteCandidateGroup(String taskId, String groupId) {
    // no-op: IdentityLinkEntity has been removed
  }

  public void deleteUserIdentityLink(String taskId, String userId, String identityLinkType) {
    // no-op: IdentityLinkEntity has been removed
  }

  public void deleteGroupIdentityLink(String taskId, String groupId, String identityLinkType) {
    // no-op: IdentityLinkEntity has been removed
  }

  public List<IdentityLink> getIdentityLinksForTask(String taskId) {
    // no-op: IdentityLinkEntity has been removed
    return java.util.Collections.emptyList();
  }

}
