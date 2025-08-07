package org.openoa.engine.bpmnconf.service.flowcontrol;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

public class InsertTaskCommand implements Command<Void> {

    private final String executionId;

    public InsertTaskCommand(String executionId) {
        this.executionId = executionId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        // 获取执行实例
        ExecutionEntity execution = commandContext
            .getExecutionEntityManager()
            .findExecutionById(executionId).getParent();

        if (execution == null) {
            throw new IllegalArgumentException("Execution not found: " + executionId);
        }

        // 创建任务
        TaskEntity task = TaskEntity.createAndInsert(execution);

        return null;
    }
}
