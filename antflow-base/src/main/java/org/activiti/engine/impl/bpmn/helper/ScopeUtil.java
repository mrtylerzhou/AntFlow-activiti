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

package org.activiti.engine.impl.bpmn.helper;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.PvmProcessDefinition;
import org.activiti.engine.impl.pvm.PvmScope;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;


/**
 * @author Daniel Meyer
 * @author Nico Rehwaldt
 * @author Joram Barrez
 */
public class ScopeUtil {
 
  /**
   * Find the next scope execution in the parent execution hierarchy
   * That method works different than {@link #findScopeExecutionForScope(org.activiti.engine.impl.persistence.entity.ExecutionEntity, org.activiti.engine.impl.pvm.PvmScope)} 
   * which returns the most outer scope execution.
   * 
   * @param execution the execution from which to start the search
   * @return the next scope execution in the parent execution hierarchy
   */
  public static ActivityExecution findScopeExecution(ActivityExecution execution) {
    
    while(execution.getParentId() != null && !execution.isScope()) {
      execution = execution.getParent();
    }
    
    if(execution != null && execution.isConcurrent()) {
      execution = execution.getParent();
    }
    
    return execution;
    
  }
  /**
   * returns the top-most execution sitting in an activity part of the scope defined by 'scopeActivitiy'.
   */
  public static ExecutionEntity findScopeExecutionForScope(ExecutionEntity execution, PvmScope scopeActivity) {
    
    // TODO: this feels hacky!
    
    if (scopeActivity instanceof PvmProcessDefinition) {
      return execution.getProcessInstance();
      
    } else {
      
      ActivityImpl currentActivity = execution.getActivity();      
      ExecutionEntity candiadateExecution = null;
      ExecutionEntity originalExecution = execution;
      
      while (execution != null) {
        currentActivity = execution.getActivity();
        if (scopeActivity.getActivities().contains(currentActivity) /* does not search rec*/ 
                || scopeActivity.equals(currentActivity)) {
          // found a candidate execution; lets still check whether we find an
          // execution which is also sitting in an activity part of this scope
          // higher up the hierarchy
          candiadateExecution = execution;        
        } else if (currentActivity!= null 
                && currentActivity.contains((ActivityImpl)scopeActivity) /*searches rec*/) {
          // now we're too "high", the candidate execution is the one.
          break;
        }
          
        execution = execution.getParent();
      }
      
      // if activity is scope, we need to get the parent at least:
      if(originalExecution == candiadateExecution 
              && originalExecution.getActivity().isScope() 
              && !originalExecution.getActivity().equals(scopeActivity)) {
        candiadateExecution = originalExecution.getParent();
      }      
      
      return candiadateExecution;
    }
  }
  
  public static ActivityImpl findInParentScopesByBehaviorType(ActivityImpl activity, Class<? extends ActivityBehavior> behaviorType) {
    while (activity != null) {
      for (ActivityImpl childActivity : activity.getActivities()) {
        if(behaviorType.isAssignableFrom(childActivity.getActivityBehavior().getClass())) {
          return childActivity;          
        }
      }
      activity = activity.getParentActivity();      
    }    
    return null;
  }

}
