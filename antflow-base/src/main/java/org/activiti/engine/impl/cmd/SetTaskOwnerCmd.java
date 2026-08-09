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
package org.activiti.engine.impl.cmd;

import java.io.Serializable;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;

/**
 * @author Joram Barrez
 */
public class SetTaskOwnerCmd implements Command<Void>, Serializable {

  private static final long serialVersionUID = 1L;

  protected String taskId;
  protected String owner;

  public SetTaskOwnerCmd(String taskId, String owner) {
    this.taskId = taskId;
    this.owner = owner;
  }

  public Void execute(CommandContext commandContext) {
    if (taskId == null) {
      throw new ActivitiIllegalArgumentException("taskId is null");
    }

    TaskEntity task = commandContext
      .getTaskEntityManager()
      .findTaskById(taskId);

    if (task == null) {
      throw new ActivitiObjectNotFoundException("task " + taskId + " doesn't exist", Task.class);
    }

    task.setOwner(owner);

    return null;
  }

}
