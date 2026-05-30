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
package org.activiti.engine;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventDispatcher;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.NativeExecutionQuery;
import org.activiti.engine.runtime.NativeProcessInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Event;

/**
 * 
 * @author Tom Baeyens
 * @author Joram Barrez
 * @author Daniel Meyer
 */
public interface RuntimeService {

  /**
   * Starts a new process instance in the latest version of the process
   * definition with the given key.
   * 
   * @param processDefinitionKey
   *          key of process definition, cannot be null.
   * @throws ActivitiObjectNotFoundException
   *           when no process definition is deployed with the given key.
   */
  ProcessInstance startProcessInstanceByKey(String processDefinitionKey);

  /**
   * Starts a new process instance in the latest version of the process
   * definition with the given key.
   * 
   * A business key can be provided to associate the process instance with a
   * certain identifier that has a clear business meaning. For example in an
   * order process, the business key could be an order id. This business key can
   * then be used to easily look up that process instance , see
   * {@link ProcessInstanceQuery#processInstanceBusinessKey(String)}. Providing
   * such a business key is definitely a best practice.
   * 
   * @param processDefinitionKey
   *          key of process definition, cannot be null.
   * @param businessKey
   *          a key that uniquely identifies the process instance in the context
   *          or the given process definition.
   * @throws ActivitiObjectNotFoundException
   *           when no process definition is deployed with the given key.
   */
  ProcessInstance startProcessInstanceByKey(String processDefinitionKey, String businessKey);

  /**
   * Starts a new process instance in the latest version of the process
   * definition with the given key
   * 
   * @param processDefinitionKey
   *          key of process definition, cannot be null.
   * @param variables
   *          the variables to pass, can be null.
   * @throws ActivitiObjectNotFoundException
   *           when no process definition is deployed with the given key.
   */
  ProcessInstance startProcessInstanceByKey(String processDefinitionKey, Map<String, Object> variables);
  
  /**
   * Starts a new process instance in the latest version of the process
   * definition with the given key.
   * 
   * A business key can be provided to associate the process instance with a
   * certain identifier that has a clear business meaning. For example in an
   * order process, the business key could be an order id. This business key can
   * then be used to easily look up that process instance , see
   * {@link ProcessInstanceQuery#processInstanceBusinessKey(String)}. Providing
   * such a business key is definitely a best practice.
   * 
   * The combination of processdefinitionKey-businessKey must be unique.
   * 
   * @param processDefinitionKey
   *          key of process definition, cannot be null.
   * @param variables
   *          the variables to pass, can be null.
   * @param businessKey
   *          a key that uniquely identifies the process instance in the context
   *          or the given process definition.
   * @throws ActivitiObjectNotFoundException
   *           when no process definition is deployed with the given key.
   */
  ProcessInstance startProcessInstanceByKey(String processDefinitionKey, String businessKey, Map<String, Object> variables);
  
  /**
   * Similar to {@link #startProcessInstanceByKey(String)}, but using a specific tenant identifier.
   */
  ProcessInstance startProcessInstanceByKeyAndTenantId(String processDefinitionKey, String tenantId);

  /**
   * Similar to {@link #startProcessInstanceByKey(String, String)}, but using a specific tenant identifier.
   */
  ProcessInstance startProcessInstanceByKeyAndTenantId(String processDefinitionKey, String businessKey, String tenantId);

  /**
   * Similar to {@link #startProcessInstanceByKey(String, Map)}, but using a specific tenant identifier.
   */
  ProcessInstance startProcessInstanceByKeyAndTenantId(String processDefinitionKey, Map<String, Object> variables, String tenantId);
  
  /**
   * Similar to {@link #startProcessInstanceByKey(String, String, Map)}, but using a specific tenant identifier. 
   */
  ProcessInstance startProcessInstanceByKeyAndTenantId(String processDefinitionKey, String businessKey, Map<String, Object> variables, String tenantId);

  /**
   * Starts a new process instance in the exactly specified version of the
   * process definition with the given id.
   * 
   * @param processDefinitionId
   *          the id of the process definition, cannot be null.
   * @throws ActivitiObjectNotFoundException
   *           when no process definition is deployed with the given key.
   */
  ProcessInstance startProcessInstanceById(String processDefinitionId);

  /**
   * Starts a new process instance in the exactly specified version of the
   * process definition with the given id.
   * 
   * A business key can be provided to associate the process instance with a
   * certain identifier that has a clear business meaning. For example in an
   * order process, the business key could be an order id. This business key can
   * then be used to easily look up that process instance , see
   * {@link ProcessInstanceQuery#processInstanceBusinessKey(String)}. Providing
   * such a business key is definitely a best practice.
   * 
   * @param processDefinitionId
   *          the id of the process definition, cannot be null.
   * @param businessKey
   *          a key that uniquely identifies the process instance in the context
   *          or the given process definition.
   * @throws ActivitiObjectNotFoundException
   *           when no process definition is deployed with the given key.
   */
  ProcessInstance startProcessInstanceById(String processDefinitionId, String businessKey);

  /**
   * Starts a new process instance in the exactly specified version of the
   * process definition with the given id.
   * 
   * @param processDefinitionId
   *          the id of the process definition, cannot be null.
   * @param variables
   *          variables to be passed, can be null
   * @throws ActivitiObjectNotFoundException
   *           when no process definition is deployed with the given key.
   */
  ProcessInstance startProcessInstanceById(String processDefinitionId, Map<String, Object> variables);

  /**
   * Starts a new process instance in the exactly specified version of the
   * process definition with the given id.
   * 
   * A business key can be provided to associate the process instance with a
   * certain identifier that has a clear business meaning. For example in an
   * order process, the business key could be an order id. This business key can
   * then be used to easily look up that process instance , see
   * {@link ProcessInstanceQuery#processInstanceBusinessKey(String)}. Providing
   * such a business key is definitely a best practice.
   * 
   * @param processDefinitionId
   *          the id of the process definition, cannot be null.
   * @param variables
   *          variables to be passed, can be null
   * @throws ActivitiObjectNotFoundException
   *           when no process definition is deployed with the given key.
   */
  ProcessInstance startProcessInstanceById(String processDefinitionId, String businessKey, Map<String, Object> variables);

  /**
   * Delete an existing runtime process instance.
   * 
   * @param processInstanceId
   *          id of process instance to delete, cannot be null.
   * @param deleteReason
   *          reason for deleting, can be null.
   * @throws ActivitiObjectNotFoundException
   *           when no process instance is found with the given id.
   */
  void deleteProcessInstance(String processInstanceId, String deleteReason);

  /**
   * Finds the activity ids for all executions that are waiting in activities.
   * This is a list because a single activity can be active multiple times.
   * 
   * @param executionId
   *          id of the execution, cannot be null.
   * @throws ActivitiObjectNotFoundException
   *           when no execution exists with the given executionId.
   */
  List<String> getActiveActivityIds(String executionId);

  /**
   * Sends an external trigger to an activity instance that is waiting inside
   * the given execution.
   * 
   * @param executionId
   *          id of execution to signal, cannot be null.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  void signal(String executionId);

  /**
   * Sends an external trigger to an activity instance that is waiting inside
   * the given execution.
   * 
   * @param executionId
   *          id of execution to signal, cannot be null.
   * @param processVariables
   *          a map of process variables
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  void signal(String executionId, Map<String, Object> processVariables);

  /**
   * Updates the business key for the provided process instance
   * 
   * @param processInstanceId
   *          id of the process instance to set the business key, cannot be null
   * @param businessKey
   *          new businessKey value
   */
  void updateBusinessKey(String processInstanceId, String businessKey);

  // Variables
  // ////////////////////////////////////////////////////////////////////

  /**
   * All variables visible from the given execution scope (including parent
   * scopes).
   * 
   * @param executionId
   *          id of execution, cannot be null.
   * @return the variables or an empty map if no such variables are found.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  Map<String, Object> getVariables(String executionId);
  
  /**
   * All variables visible from the given execution scope (including parent scopes).
   *
   * @param executionId
   *          id of execution, cannot be null.
   * @return the variable instances or an empty map if no such variables are found.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  Map<String, VariableInstance> getVariableInstances(String executionId);

  /**
   * All variables visible from the given execution scope (including parent
   * scopes).
   * 
   * @param executionIds
   *          ids of execution, cannot be null.
   * @return the variables.
   */
  List<VariableInstance> getVariableInstancesByExecutionIds(Set<String> executionIds);
  
  /**
   * All variables visible from the given execution scope (including parent scopes).
   *
   * @param executionId
   *          id of execution, cannot be null.
   * @param locale
   *          locale the variable name and description should be returned in (if available).
   * @param withLocalizationFallback
   *          When true localization will fallback to more general locales including the default locale of the JVM if the specified locale is not found.
   * @return the variable instances or an empty map if no such variables are found.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  Map<String, VariableInstance> getVariableInstances(String executionId, String locale, boolean withLocalizationFallback);

  /**
   * All variable values that are defined in the execution scope, without taking
   * outer scopes into account. If you have many task local variables and you
   * only need a few, consider using
   * {@link #getVariablesLocal(String, Collection)} for better performance.
   * 
   * @param executionId
   *          id of execution, cannot be null.
   * @return the variables or an empty map if no such variables are found.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  Map<String, Object> getVariablesLocal(String executionId);
  
  /**
   * All variable values that are defined in the execution scope, without taking outer scopes into account. If you have many task local variables and you only need a few, consider using
   * {@link #getVariableInstancesLocal(String, Collection)} for better performance.
   *
   * @param executionId
   *          id of execution, cannot be null.
   * @return the variables or an empty map if no such variables are found.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  Map<String, VariableInstance> getVariableInstancesLocal(String executionId);

  /**
   * All variable values that are defined in the execution scope, without taking outer scopes into account. If you have many task local variables and you only need a few, consider using
   * {@link #getVariableInstancesLocal(String, Collection)} for better performance.
   *
   * @param executionId
   *          id of execution, cannot be null.
   * @param locale
   *          locale the variable name and description should be returned in (if available).
   * @param withLocalizationFallback
   *          When true localization will fallback to more general locales including the default locale of the JVM if the specified locale is not found. 
   * @return the variables or an empty map if no such variables are found.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  Map<String, VariableInstance> getVariableInstancesLocal(String executionId, String locale, boolean withLocalizationFallback);

  /**
   * The variable values for all given variableNames, takes all variables into
   * account which are visible from the given execution scope (including parent
   * scopes).
   * 
   * @param executionId
   *          id of execution, cannot be null.
   * @param variableNames
   *          the collection of variable names that should be retrieved.
   * @return the variables or an empty map if no such variables are found.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  Map<String, Object> getVariables(String executionId, Collection<String> variableNames);
  
  /**
   * The variable values for all given variableNames, takes all variables into account which are visible from the given execution scope (including parent scopes).
   * 
   * @param executionId
   *          id of execution, cannot be null.
   * @param variableNames
   *          the collection of variable names that should be retrieved. 
   * @return the variables or an empty map if no such variables are found.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  Map<String, VariableInstance> getVariableInstances(String executionId, Collection<String> variableNames);

  /**
   * The variable values for all given variableNames, takes all variables into account which are visible from the given execution scope (including parent scopes).
   * 
   * @param executionId
   *          id of execution, cannot be null.
   * @param variableNames
   *          the collection of variable names that should be retrieved.
   * @param locale
   *          locale the variable name and description should be returned in (if available).
   * @param withLocalizationFallback
   *          When true localization will fallback to more general locales including the default locale of the JVM if the specified locale is not found. 
   * @return the variables or an empty map if no such variables are found.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  Map<String, VariableInstance> getVariableInstances(String executionId, Collection<String> variableNames, String locale, boolean withLocalizationFallback);

  /**
   * The variable values for the given variableNames only taking the given
   * execution scope into account, not looking in outer scopes.
   * 
   * @param executionId
   *          id of execution, cannot be null.
   * @param variableNames
   *          the collection of variable names that should be retrieved.
   * @return the variables or an empty map if no such variables are found.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  Map<String, Object> getVariablesLocal(String executionId, Collection<String> variableNames);
  
  /**
   * The variable values for the given variableNames only taking the given execution scope into account, not looking in outer scopes.
   *
   * @param executionId
   *          id of execution, cannot be null.
   * @param variableNames
   *          the collection of variable names that should be retrieved.
   * @return the variables or an empty map if no such variables are found.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  Map<String, VariableInstance> getVariableInstancesLocal(String executionId, Collection<String> variableNames);

  /**
   * The variable values for the given variableNames only taking the given execution scope into account, not looking in outer scopes.
   *
   * @param executionId
   *          id of execution, cannot be null.
   * @param variableNames
   *          the collection of variable names that should be retrieved.
   * @param locale
   *          locale the variable name and description should be returned in (if available).
   * @param withLocalizationFallback
   *          When true localization will fallback to more general locales including the default locale of the JVM if the specified locale is not found. 
   * @return the variables or an empty map if no such variables are found.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  Map<String, VariableInstance> getVariableInstancesLocal(String executionId, Collection<String> variableNames, String locale, boolean withLocalizationFallback);

  /**
   * The variable value. Searching for the variable is done in all scopes that
   * are visible to the given execution (including parent scopes). Returns null
   * when no variable value is found with the given name or when the value is
   * set to null.
   * 
   * @param executionId
   *          id of execution, cannot be null.
   * @param variableName
   *          name of variable, cannot be null.
   * @return the variable value or null if the variable is undefined or the
   *         value of the variable is null.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  Object getVariable(String executionId, String variableName);
  
  /**
   * The variable. Searching for the variable is done in all scopes that are visible to the given execution (including parent scopes). Returns null when no variable value is found with the given
   * name or when the value is set to null.
   *
   * @param executionId
   *          id of execution, cannot be null.
   * @param variableName
   *          name of variable, cannot be null.
   * @return the variable or null if the variable is undefined.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  VariableInstance getVariableInstance(String executionId, String variableName);

  /**
   * The variable. Searching for the variable is done in all scopes that are visible to the given execution (including parent scopes). Returns null when no variable value is found with the given
   * name or when the value is set to null.
   *
   * @param executionId
   *          id of execution, cannot be null.
   * @param variableName
   *          name of variable, cannot be null.
   * @param locale
   *          locale the variable name and description should be returned in (if available).
   * @param withLocalizationFallback
   *          When true localization will fallback to more general locales including the default locale of the JVM if the specified locale is not found. 
   * @return the variable or null if the variable is undefined.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  VariableInstance getVariableInstance(String executionId, String variableName, String locale, boolean withLocalizationFallback);

  /**
   * The variable value. Searching for the variable is done in all scopes that
   * are visible to the given execution (including parent scopes). Returns null
   * when no variable value is found with the given name or when the value is
   * set to null. Throws ClassCastException when cannot cast variable to
   * given class
   *
   * @param executionId
   *          id of execution, cannot be null.
   * @param variableName
   *          name of variable, cannot be null.
   * @param variableClass
   *          name of variable, cannot be null.
   * @return the variable value or null if the variable is undefined or the
   *         value of the variable is null.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  <T> T getVariable(String executionId, String variableName, Class<T> variableClass);

  /**
   * Check whether or not this execution has variable set with the given name,
   * Searching for the variable is done in all scopes that are visible to the
   * given execution (including parent scopes).
   */
  boolean hasVariable(String executionId, String variableName);

  /**
   * The variable value for an execution. Returns the value when the variable is
   * set for the execution (and not searching parent scopes). Returns null when
   * no variable value is found with the given name or when the value is set to
   * null.
   */
  Object getVariableLocal(String executionId, String variableName);
  
  /**
   * The variable for an execution. Returns the variable when it is set for the execution (and not searching parent scopes). Returns null when no variable is found with the given
   * name or when the value is set to null.
   *
   * @param executionId
   *          id of execution, cannot be null.
   * @param variableName
   *          name of variable, cannot be null.
   * @return the variable or null if the variable is undefined.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  VariableInstance getVariableInstanceLocal(String executionId, String variableName);

  /**
   * The variable for an execution. Returns the variable when it is set for the execution (and not searching parent scopes). Returns null when no variable is found with the given
   * name or when the value is set to null.
   *
   * @param executionId
   *          id of execution, cannot be null.
   * @param variableName
   *          name of variable, cannot be null.
   * @param locale
   *          locale the variable name and description should be returned in (if available).
   * @param withLocalizationFallback
   *          When true localization will fallback to more general locales including the default locale of the JVM if the specified locale is not found.
   * @return the variable or null if the variable is undefined.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  VariableInstance getVariableInstanceLocal(String executionId, String variableName, String locale, boolean withLocalizationFallback);

  /**
   * The variable value for an execution. Returns the value casted to given class
   * when the variable is set for the execution (and not searching parent scopes).
   * Returns null when no variable value is found with the given name or when the
   * value is set to null.
   */
  <T> T  getVariableLocal(String executionId, String variableName, Class<T> variableClass);

  /**
   * Check whether or not this execution has a local variable set with the given
   * name.
   */
  boolean hasVariableLocal(String executionId, String variableName);

  /**
   * Update or create a variable for an execution.
   * 
   * <p>
   * The variable is set according to the algorithm as documented for
   * {@link VariableScope#setVariable(String, Object)}.
   * 
   * @see VariableScope#setVariable(String, Object)
   *      {@link VariableScope#setVariable(String, Object)}
   * 
   * @param executionId
   *          id of execution to set variable in, cannot be null.
   * @param variableName
   *          name of variable to set, cannot be null.
   * @param value
   *          value to set. When null is passed, the variable is not removed,
   *          only it's value will be set to null.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  void setVariable(String executionId, String variableName, Object value);

  /**
   * Update or create a variable for an execution (not considering parent
   * scopes). If the variable is not already existing, it will be created in the
   * given execution.
   * 
   * @param executionId
   *          id of execution to set variable in, cannot be null.
   * @param variableName
   *          name of variable to set, cannot be null.
   * @param value
   *          value to set. When null is passed, the variable is not removed,
   *          only it's value will be set to null.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  void setVariableLocal(String executionId, String variableName, Object value);

  /**
   * Update or create given variables for an execution (including parent
   * scopes).
   * <p>
   * Variables are set according to the algorithm as documented for
   * {@link VariableScope#setVariables(Map)}, applied separately to each
   * variable.
   * 
   * @see VariableScope#setVariables(Map)
   *      {@link VariableScope#setVariables(Map)}
   * 
   * @param executionId
   *          id of the execution, cannot be null.
   * @param variables
   *          map containing name (key) and value of variables, can be null.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  void setVariables(String executionId, Map<String, ? extends Object> variables);

  /**
   * Update or create given variables for an execution (not considering parent
   * scopes). If the variables are not already existing, it will be created in
   * the given execution.
   * 
   * @param executionId
   *          id of the execution, cannot be null.
   * @param variables
   *          map containing name (key) and value of variables, can be null.
   * @throws ActivitiObjectNotFoundException
   *           when no execution is found for the given executionId.
   */
  void setVariablesLocal(String executionId, Map<String, ? extends Object> variables);

  /**
   * Removes a variable for an execution.
   * 
   * @param executionId
   *          id of execution to remove variable in.
   * @param variableName
   *          name of variable to remove.
   */
  void removeVariable(String executionId, String variableName);

  /**
   * Removes a variable for an execution (not considering parent scopes).
   * 
   * @param executionId
   *          id of execution to remove variable in.
   * @param variableName
   *          name of variable to remove.
   */
  void removeVariableLocal(String executionId, String variableName);

  /**
   * Removes variables for an execution.
   * 
   * @param executionId
   *          id of execution to remove variable in.
   * @param variableNames
   *          collection containing name of variables to remove.
   */
  void removeVariables(String executionId, Collection<String> variableNames);

  /**
   * Remove variables for an execution (not considering parent scopes).
   * 
   * @param executionId
   *          id of execution to remove variable in.
   * @param variableNames
   *          collection containing name of variables to remove.
   */
  void removeVariablesLocal(String executionId, Collection<String> variableNames);

  // Queries ////////////////////////////////////////////////////////

  /**
   * Creates a new {@link ExecutionQuery} instance, that can be used to query
   * the executions and process instances.
   */
  ExecutionQuery createExecutionQuery();

  /**
   * creates a new {@link NativeExecutionQuery} to query {@link Execution}s by
   * SQL directly
   */
  NativeExecutionQuery createNativeExecutionQuery();

  /**
   * Creates a new {@link ProcessInstanceQuery} instance, that can be used to
   * query process instances.
   */
  ProcessInstanceQuery createProcessInstanceQuery();

  /**
   * creates a new {@link NativeProcessInstanceQuery} to query
   * {@link ProcessInstance}s by SQL directly
   */
  NativeProcessInstanceQuery createNativeProcessInstanceQuery();

  // Process instance state //////////////////////////////////////////

  /**
   * Suspends the process instance with the given id.
   * 
   * If a process instance is in state suspended, activiti will not execute jobs
   * (timers, messages) associated with this instance.
   * 
   * If you have a process instance hierarchy, suspending one process instance
   * form the hierarchy will not suspend other process instances form that
   * hierarchy.
   * 
   * @throws ActivitiObjectNotFoundException
   *           if no such processInstance can be found.
   * @throws ActivitiException
   *           the process instance is already in state suspended.
   */
  void suspendProcessInstanceById(String processInstanceId);

  /**
   * Activates the process instance with the given id.
   * 
   * If you have a process instance hierarchy, suspending one process instance
   * form the hierarchy will not suspend other process instances form that
   * hierarchy.
   * 
   * @throws ActivitiObjectNotFoundException
   *           if no such processInstance can be found.
   * @throws ActivitiException
   *           if the process instance is already in state active.
   */
  void activateProcessInstanceById(String processInstanceId);

  /**
   * Adds an event-listener which will be notified of ALL events by the
   * dispatcher.
   * 
   * @param listenerToAdd
   *          the listener to add
   */
  void addEventListener(ActivitiEventListener listenerToAdd);

  /**
   * Adds an event-listener which will only be notified when an event occurs,
   * which type is in the given types.
   * 
   * @param listenerToAdd
   *          the listener to add
   * @param types
   *          types of events the listener should be notified for
   */
  void addEventListener(ActivitiEventListener listenerToAdd, ActivitiEventType... types);

  /**
   * Removes the given listener from this dispatcher. The listener will no
   * longer be notified, regardless of the type(s) it was registered for in the
   * first place.
   * 
   * @param listenerToRemove
   *          listener to remove
   */
  void removeEventListener(ActivitiEventListener listenerToRemove);

  /**
   * Dispatches the given event to any listeners that are registered.
   * 
   * @param event
   *          event to dispatch.
   * 
   * @throws ActivitiException
   *           if an exception occurs when dispatching the event or when the
   *           {@link ActivitiEventDispatcher} is disabled.
   * @throws ActivitiIllegalArgumentException
   *           when the given event is not suitable for dispatching.
   */
  void dispatchEvent(ActivitiEvent event);
  
  /**
   * Sets the name for the process instance with the given id.
   * @param processInstanceId id of the process instance to update
   * @param name new name for the process instance
   * @throws ActivitiObjectNotFoundException 
   *    when the given process instance does not exist.
   */
  void setProcessInstanceName(String processInstanceId, String name);
  
  /** The all events related to the given Process Instance. */
  List<Event> getProcessInstanceEvents(String processInstanceId);
  
  /**Create a ProcessInstanceBuilder*/
  ProcessInstanceBuilder createProcessInstanceBuilder();

  /** Adds a user identity link to the process instance. */
  void addUserIdentityLink(String processInstanceId, String userId, String identityLinkType);

  /** Adds a group identity link to the process instance. */
  void addGroupIdentityLink(String processInstanceId, String groupId, String identityLinkType);

  /** Adds a participant user to the process instance. */
  void addParticipantUser(String processInstanceId, String userId);

  /** Adds a participant group to the process instance. */
  void addParticipantGroup(String processInstanceId, String groupId);

  /** Removes a participant user from the process instance. */
  void deleteParticipantUser(String processInstanceId, String userId);

  /** Removes a participant group from the process instance. */
  void deleteParticipantGroup(String processInstanceId, String groupId);

  /** Removes a user identity link from the process instance. */
  void deleteUserIdentityLink(String processInstanceId, String userId, String identityLinkType);

  /** Removes a group identity link from the process instance. */
  void deleteGroupIdentityLink(String processInstanceId, String groupId, String identityLinkType);

  /** Retrieves the identity links associated with the given process instance. */
  List<IdentityLink> getIdentityLinksForProcessInstance(String processInstanceId);

}