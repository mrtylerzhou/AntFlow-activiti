package org.openoa.engine.bpmnconf.service.interf.biz;

import org.activiti.engine.TaskService;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.List;

public interface BpmVariableSignUpPersonnelBizService {
    void insertSignUpPersonnel(TaskService taskService, String taskId, String processNumber, String nodeId, String assignee, List<BaseIdTranStruVo> signUpUsers);
}
