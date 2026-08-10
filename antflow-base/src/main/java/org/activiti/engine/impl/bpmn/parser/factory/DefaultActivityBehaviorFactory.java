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
package org.activiti.engine.impl.bpmn.parser.factory;

import java.util.List;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ErrorEventDefinition;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.IOParameter;
import org.activiti.bpmn.model.InclusiveGateway;
import org.activiti.bpmn.model.ManualTask;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.ThrowEvent;
import org.activiti.bpmn.model.Transaction;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.*;
import org.activiti.engine.impl.bpmn.data.SimpleDataInputAssociation;
import org.activiti.engine.impl.bpmn.data.SimpleDataOutputAssociation;
import org.activiti.engine.impl.bpmn.helper.ClassDelegate;
import org.activiti.engine.impl.bpmn.parser.FieldDeclaration;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.apache.commons.lang3.StringUtils;

/**
 * Default implementation of the {@link ActivityBehaviorFactory}. Used when no
 * custom {@link ActivityBehaviorFactory} is injected on the
 * {@link ProcessEngineConfigurationImpl}.
 * 
 * @author Joram Barrez
 */
public class DefaultActivityBehaviorFactory extends AbstractBehaviorFactory implements ActivityBehaviorFactory {
  
  // Start event
  public final static String EXCEPTION_MAP_FIELD = "mapExceptions";

  public NoneStartEventActivityBehavior createNoneStartEventActivityBehavior(StartEvent startEvent) {
    return new NoneStartEventActivityBehavior();
  }

  public EventSubProcessStartEventActivityBehavior createEventSubProcessStartEventActivityBehavior(StartEvent startEvent, String activityId) {
    return new EventSubProcessStartEventActivityBehavior(activityId);
  }
  
  // Task
  
  public TaskActivityBehavior createTaskActivityBehavior(Task task) {
    return new TaskActivityBehavior();
  }
  
  public ManualTaskActivityBehavior createManualTaskActivityBehavior(ManualTask manualTask) {
    return new ManualTaskActivityBehavior();
  }
  
  public UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask, TaskDefinition taskDefinition) {
    return new UserTaskActivityBehavior(userTask.getId(), taskDefinition);
  }

  // Gateways

  public ExclusiveGatewayActivityBehavior createExclusiveGatewayActivityBehavior(ExclusiveGateway exclusiveGateway) {
    return new ExclusiveGatewayActivityBehavior();
  }

  public ParallelGatewayActivityBehavior createParallelGatewayActivityBehavior(ParallelGateway parallelGateway) {
    return new ParallelGatewayActivityBehavior();
  }

  public InclusiveGatewayActivityBehavior createInclusiveGatewayActivityBehavior(InclusiveGateway inclusiveGateway) {
    return new InclusiveGatewayActivityBehavior();
  }

  // Multi Instance

  public SequentialMultiInstanceBehavior createSequentialMultiInstanceBehavior(ActivityImpl activity, AbstractBpmnActivityBehavior innerActivityBehavior) {
    return new SequentialMultiInstanceBehavior(activity, innerActivityBehavior);
  }

  public ParallelMultiInstanceBehavior createParallelMultiInstanceBehavior(ActivityImpl activity, AbstractBpmnActivityBehavior innerActivityBehavior) {
    return new ParallelMultiInstanceBehavior(activity, innerActivityBehavior);
  }
  
  // Subprocess
  
  public SubProcessActivityBehavior createSubprocActivityBehavior(SubProcess subProcess) {
    return new SubProcessActivityBehavior();
  }

  // Call activity

  public CallActivityBehavior createCallActivityBehavior(CallActivity callActivity) {
    String expressionRegex = "\\$+\\{+.+\\}";

    CallActivityBehavior callActivityBehaviour = null;
    if (StringUtils.isNotEmpty(callActivity.getCalledElement()) && callActivity.getCalledElement().matches(expressionRegex)) {
      callActivityBehaviour = new CallActivityBehavior(expressionManager.createExpression(callActivity.getCalledElement()), callActivity.getMapExceptions());
    } else {
      callActivityBehaviour = new CallActivityBehavior(callActivity.getCalledElement(), callActivity.getMapExceptions());
    }

    if (StringUtils.isNotEmpty(callActivity.getBusinessKey())) {
      callActivityBehaviour.setBusinessKey(callActivity.getBusinessKey());
    }

    callActivityBehaviour.setInheritBusinessKey(callActivity.isInheritBusinessKey());
    callActivityBehaviour.setInheritVariables(callActivity.isInheritVariables());

    for (IOParameter ioParameter : callActivity.getInParameters()) {
      if (StringUtils.isNotEmpty(ioParameter.getSourceExpression())) {
        Expression expression = expressionManager.createExpression(ioParameter.getSourceExpression().trim());
        callActivityBehaviour.addDataInputAssociation(new SimpleDataInputAssociation(expression, ioParameter.getTarget()));
      } else {
        callActivityBehaviour.addDataInputAssociation(new SimpleDataInputAssociation(ioParameter.getSource(), ioParameter.getTarget()));
      }
    }

    for (IOParameter ioParameter : callActivity.getOutParameters()) {
      if (StringUtils.isNotEmpty(ioParameter.getSourceExpression())) {
        Expression expression = expressionManager.createExpression(ioParameter.getSourceExpression().trim());
        callActivityBehaviour.addDataOutputAssociation(new SimpleDataOutputAssociation(ioParameter.getTarget(), expression));
      } else {
        callActivityBehaviour.addDataOutputAssociation(new SimpleDataOutputAssociation(ioParameter.getTarget(), ioParameter.getSource()));
      }
    }

    return callActivityBehaviour;
  }

  // Transaction
  
  public TransactionActivityBehavior createTransactionActivityBehavior(Transaction transaction) {
    return new TransactionActivityBehavior();
  }

  // Intermediate Events
  
  public IntermediateThrowNoneEventActivityBehavior createIntermediateThrowNoneEventActivityBehavior(ThrowEvent throwEvent) {
    return new IntermediateThrowNoneEventActivityBehavior();
  }

  // End events
  
  public NoneEndEventActivityBehavior createNoneEndEventActivityBehavior(EndEvent endEvent) {
    return new NoneEndEventActivityBehavior();
  }
  
  public ErrorEndEventActivityBehavior createErrorEndEventActivityBehavior(EndEvent endEvent, ErrorEventDefinition errorEventDefinition) {
    return new ErrorEndEventActivityBehavior(errorEventDefinition.getErrorCode());
  }
  
  public CancelEndEventActivityBehavior createCancelEndEventActivityBehavior(EndEvent endEvent) {
    return new CancelEndEventActivityBehavior();
  }
  
  public TerminateEndEventActivityBehavior createTerminateEndEventActivityBehavior(EndEvent endEvent) {
    return new TerminateEndEventActivityBehavior(endEvent);
  }

  // Boundary Events
  
  public BoundaryEventActivityBehavior createBoundaryEventActivityBehavior(BoundaryEvent boundaryEvent, boolean interrupting, ActivityImpl activity) {
    return new BoundaryEventActivityBehavior(interrupting, activity.getId());
  }

}
