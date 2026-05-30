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
package org.activiti.engine.impl.bpmn.parser.handler;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.TimerEventDefinition;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Joram Barrez
 */
public class TimerEventDefinitionParseHandler extends AbstractBpmnParseHandler<TimerEventDefinition> {

	private static final Logger logger = LoggerFactory.getLogger(TimerEventDefinitionParseHandler.class);

  public Class< ? extends BaseElement> getHandledType() {
    return TimerEventDefinition.class;
  }

  protected void executeParse(BpmnParse bpmnParse, TimerEventDefinition timerEventDefinition) {
    logger.warn("Timer event definition encountered but job execution is not supported. Timer events will be ignored.");
  }

}
