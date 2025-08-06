package org.openoa.engine.bpmnconf.service.biz;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.exception.AFBizException;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmTaskconfigBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class BpmTaskconfigBizServiceImpl implements BpmTaskconfigBizService {
    @Autowired
    private TaskService taskService;

    /**

     * get reject node by task id
     *
     * @param taskId
     * @return
     */
    @Override
    public String findTargeNode(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (ObjectUtils.isEmpty(task)) {
            throw new AFBizException("当前任务id:[" + taskId + "]" + "不存在");
        }
        String processKey = task.getProcessDefinitionId().split(":")[0];
        String nodeKey = getMapper().findTaskRollBack(processKey, task.getTaskDefinitionKey());
        return nodeKey == null ? "" : nodeKey;
    }
}
