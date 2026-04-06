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
package org.activiti.engine.impl.bpmn.data;

import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

/**
 * A simple data output association between a source and a target.
 * Replaces MessageImplicitDataOutputAssociation without WebService dependencies.
 */
public class SimpleDataOutputAssociation extends AbstractDataAssociation {

  private static final long serialVersionUID = 1L;

  public SimpleDataOutputAssociation(String targetRef, Expression sourceExpression) {
    super(sourceExpression, targetRef);
  }

  public SimpleDataOutputAssociation(String targetRef, String sourceRef) {
    super(sourceRef, targetRef);
  }

  @Override
  public void evaluate(ActivityExecution execution) {
    Object value;
    if (this.sourceExpression != null) {
      value = this.sourceExpression.getValue(execution);
    } else {
      value = execution.getVariable(this.source);
    }
    execution.setVariable(this.target, value);
  }
}
