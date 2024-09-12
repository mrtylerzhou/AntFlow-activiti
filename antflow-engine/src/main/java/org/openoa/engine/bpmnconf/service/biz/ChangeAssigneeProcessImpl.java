package org.openoa.engine.bpmnconf.service.biz;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChangeAssigneeProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    protected BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private BpmFlowrunEntrustServiceImpl bpmFlowrunEntrustService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        List<Task> list = taskService.createTaskQuery().processInstanceId(bpmBusinessProcess.getProcInstId())
                .list();
        List<String> userIds = vo.getUserIds();
        for (int i = 0; i < list.size(); i++) {
            Task task = list.get(i);
            String user = userIds.get(i);
            String assignee = task.getAssignee();
            if(!userIds.contains(assignee)){
                bpmFlowrunEntrustService.addFlowrunEntrust(Integer.parseInt(user),Integer.parseInt(assignee),task.getId(),0,bpmBusinessProcess.getProcInstId(),vo.getProcessKey());
            }
            taskMgmtMapper.updateaActinst(TaskMgmtVO.builder().applyUser(user).taskId(task.getId()).build());
            taskMgmtMapper.updateaTaskinst(TaskMgmtVO.builder().applyUser(user).taskId(task.getId()).build());
            taskMgmtMapper.updateTask(TaskMgmtVO.builder().applyUser(user).taskId(task.getId()).build());

        }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_CHANGE_ASSIGNEE,ProcessOperationEnum.BUTTON_TYPE_ZB);
    }
}
