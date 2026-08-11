package org.openoa.engine.bpmnconf.service.interf.biz;

import org.activiti.engine.TaskService;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.List;

public interface BpmVariableSignUpPersonnelBizService {
    void insertSignUpPersonnel(TaskService taskService, String taskId, String processNumber, String nodeId, String assignee, List<BaseIdTranStruVo> signUpUsers);

    /**
     * 自动加批场景重载: 回路(加批后回到审批人) personnel 名称用传入的 assigneeName,
     * 而非 SecurityUtils 登录名(自动场景无登录用户).
     */
    void insertSignUpPersonnel(TaskService taskService, String taskId, String processNumber, String nodeId, String assignee, String assigneeName, List<BaseIdTranStruVo> signUpUsers);

    /**
     * 幂等检查: 该节点(elementId)的 signUp personnel 是否已非空(已加批过).
     * 用于条件自动加批防止回到审批人后重复触发.
     */
    boolean hasSignUpPersonnel(String processNumber, String elementId);
}
