package org.openoa.engine.bpmnconf.service.biz;

import org.activiti.engine.task.Task;

/**
 * @Author: jwz
 * @Date: 2025/4/29 11:20
 * @Description: 自定义业务逻辑接口
 * @Version: 1.0.0
 */
public interface BpmnBizCustomService {

     void execute(Task task);
}
