package org.openoa.engine.bpmnconf.service.biz;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.common.service.BpmVariableMultiplayerPersonnelServiceImpl;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.engine.bpmnconf.service.impl.UserMessageServiceImpl;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.constant.enums.ProcessEnum;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.TaskMgmtVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * task undertaken
 */
@Component
public class UndertakeProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    protected UserMessageServiceImpl userMessageService;
    @Autowired
    private BpmVariableMultiplayerPersonnelServiceImpl bpmVariableMultiplayerPersonnelService;
    @Override
    public void doProcessButton(BusinessDataVo vo) {
        if (ObjectUtils.isEmpty(vo.getTaskId())) {
            throw new JiMuBizException("当前流程节点等于空！");
        }
        Task task = taskService.createTaskQuery()
                .taskId(vo.getTaskId())
                .singleResult();
        if (ObjectUtils.isEmpty(task)) {
            throw new JiMuBizException( "当前流程节点已经被人承办！");
        }
        List<TaskMgmtVO> list = taskMgmtMapper.getAgencyList(vo.getTaskId(), ProcessEnum.AGENCY_TYPE.getCode(), task.getProcessInstanceId());
        if (!ObjectUtils.isEmpty(list)) {
            for (TaskMgmtVO t : list) {
                //update user message status
                userMessageService.readNode(task.getProcessInstanceId());
                Integer num = taskMgmtMapper.deletTask(t.getTaskId());
            }
        }
        bpmVariableMultiplayerPersonnelService.undertake(vo.getProcessNumber(), task.getTaskDefinitionKey());

    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_UNDERTAKE);
    }
}
