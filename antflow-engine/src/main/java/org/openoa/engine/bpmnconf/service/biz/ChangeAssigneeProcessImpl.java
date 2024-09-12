package org.openoa.engine.bpmnconf.service.biz;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
        List<BaseIdTranStruVo> userInfos = vo.getUserInfos();
        List<String> userIds=userInfos.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
        for (int i = 0; i < list.size(); i++) {
            Task task = list.get(i);
            String user = userInfos.get(i).getId();
            String userName=userInfos.get(i).getName();
            String assignee = task.getAssignee();
            String assigneeName=task.getDescription();
            if(!userIds.contains(assignee)){
                bpmFlowrunEntrustService.addFlowrunEntrust(user,userName,assignee,assigneeName,task.getId(),0,bpmBusinessProcess.getProcInstId(),vo.getProcessKey());
            }
            taskMgmtMapper.updateaActinst(TaskMgmtVO.builder().applyUser(user).taskId(task.getId()).build());
            taskMgmtMapper.updateaTaskinst(TaskMgmtVO.builder().applyUser(user).taskId(task.getId()).build());
            taskMgmtMapper.updateTask(TaskMgmtVO.builder().applyUser(user).taskId(task.getId()).build());

        }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_CHANGE_ASSIGNEE);
    }
}
