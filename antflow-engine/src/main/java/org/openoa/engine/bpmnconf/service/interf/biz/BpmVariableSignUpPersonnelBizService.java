package org.openoa.engine.bpmnconf.service.interf.biz;

import org.activiti.engine.TaskService;
import org.openoa.base.entity.BpmVariableSignUpPersonnel;
import org.openoa.base.service.BpmVariableSignUpPersonnelService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.mapper.BpmVariableSignUpPersonnelMapper;

import java.util.List;

public interface BpmVariableSignUpPersonnelBizService extends BizService<BpmVariableSignUpPersonnelMapper, BpmVariableSignUpPersonnelService, BpmVariableSignUpPersonnel>{
    void insertSignUpPersonnel(TaskService taskService, String taskId, String processNumber, String nodeId, String assignee, List<BaseIdTranStruVo> signUpUsers);
}
