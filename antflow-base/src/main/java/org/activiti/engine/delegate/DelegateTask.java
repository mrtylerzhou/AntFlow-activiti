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
package org.activiti.engine.delegate;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.IdentityLink;

/**
 * @author Joram Barrez
 */
public interface DelegateTask extends VariableScope {

  /** DB id of the task. */
  String getId();
  
  /** Name or title of the task. */
  String getName();
  
  /** Change the name of the task. */
  void setName(String name);

  /** Free text description of the task. */
  String getDescription();
  
  /** Change the description of the task */
  void setDescription(String description);
  
  /** indication of how important/urgent this task is with a number between 
   * 0 and 100 where higher values mean a higher priority and lower values mean 
   * lower priority: [0..19] lowest, [20..39] low, [40..59] normal, [60..79] high 
   * [80..100] highest */
  int getPriority();
  
  /** indication of how important/urgent this task is with a number between 
   * 0 and 100 where higher values mean a higher priority and lower values mean 
   * lower priority: [0..19] lowest, [20..39] low, [40..59] normal, [60..79] high 
   * [80..100] highest */
  void setPriority(int priority);
  
  /** Reference to the process instance or null if it is not related to a process instance. */
  String getProcessInstanceId();
  
  /** Reference to the path of execution or null if it is not related to a process instance. */
  String getExecutionId();
  
  /** Reference to the process definition or null if it is not related to a process. */
  String getProcessDefinitionId();

  /** The date/time when this task was created */
  Date getCreateTime();
  
  /** The id of the activity in the process defining this task or null if this is not related to a process */
  String getTaskDefinitionKey();

  /** Indicated whether this task is suspended or not. */
  boolean isSuspended();

  /** The tenant identifier of this task */
  String getTenantId();

  /** The form key for the user task */
  String getFormKey();

  /** Change the form key of the task */
  void setFormKey(String formKey);
  
  /** Returns the execution currently at the task. */
  DelegateExecution getExecution();
  
  /** Returns the event name which triggered the task listener to fire for this task. */
  String getEventName();

  /** The current {@link org.activiti.engine.task.DelegationState} for this task. */
  DelegationState getDelegationState();
  
  /** Adds the given user as a candidate user to this task. */
  void addCandidateUser(String userId);
  
  /** Adds multiple users as candidate user to this task. */
  void addCandidateUsers(Collection<String> candidateUsers);
  
  /** Adds the given group as candidate group to this task */
  void addCandidateGroup(String groupId);
  
  /** Adds multiple groups as candidate group to this task. */
  void addCandidateGroups(Collection<String> candidateGroups);

  /** The {@link User.getId() userId} of the person responsible for this task. */
  String getOwner();
  
  /** The {@link User.getId() userId} of the person responsible for this task.*/
  void setOwner(String owner);
  
  /** The {@link User.getId() userId} of the person to which this task is delegated. */
  String getAssignee();
  
  /** The {@link User.getId() userId} of the person to which this task is delegated. */
  void setAssignee(String assignee);
  
  /** Due date of the task. */
  Date getDueDate();
  
  /** Change due date of the task. */
  void setDueDate(Date dueDate);
  
  /** The category of the task. This is an optional field and allows to 'tag' tasks as belonging to a certain category. */
  String getCategory();
	
  /** Change the category of the task. This is an optional field and allows to 'tag' tasks as belonging to a certain category. */
  void setCategory(String category);

  /** Adds an identity link to this task. */
  void addUserIdentityLink(String userId, String identityLinkType);

  /** Adds an identity link to this task. */
  void addGroupIdentityLink(String groupId, String identityLinkType);

  /** Removes a candidate user from the task. */
  void deleteCandidateUser(String userId);

  /** Removes a candidate group from the task. */
  void deleteCandidateGroup(String groupId);

  /** Removes the identity link from this task. */
  void deleteUserIdentityLink(String userId, String identityLinkType);

  /** Removes the identity link from this task. */
  void deleteGroupIdentityLink(String groupId, String identityLinkType);

  /** Retrieves the identity links associated with the task. */
  List<IdentityLink> getCandidates();

}
