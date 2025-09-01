package org.openoa.engine.bpmnconf.adp.processoperation;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.service.BpmVariableService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmFlowrunEntrustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 变更处理人需要把变更后的处理人和未变更的处理人都传入进来,不限定顺序
 * 实现原理是如果传入进来的审批人不在当前审批任务中,则变更,如果已经在了,则跳过
 * 之所以设计成这样,是因为变更处理人有前端交互,会把已有的审批人做成选择列表,用户可以变更其中的,因此可以传到后端整个变更后的审批人列表
 */
@Component
public class ChangeAssigneeProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    protected BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private BpmFlowrunEntrustService bpmFlowrunEntrustService;
    @Autowired
    private BpmVariableService bpmVariableService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        String taskDefKey = vo.getTaskDefKey();
        List<Task> list = taskService.createTaskQuery().processInstanceId(bpmBusinessProcess.getProcInstId())
                .taskDefinitionKey(taskDefKey)
                .list();
        if(CollectionUtils.isEmpty(list)){
            throw new RuntimeException("未能根据流程编号找到流程实例:"+vo.getProcessNumber());
        }
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
                TaskMgmtVO taskMgmtVO = TaskMgmtVO.builder().applyUser(user).applyUserName(userName).taskId(task.getId()).build();
                taskMgmtMapper.updateaActinst(taskMgmtVO);
                taskMgmtMapper.updateaTaskinst(taskMgmtVO);
                taskMgmtMapper.updateTask(taskMgmtVO);
                bpmVariableService.updateAssignee(bpmBusinessProcess.getBusinessNumber(),taskDefKey,assignee,BaseIdTranStruVo.builder().id(user).name(userName+"*").build());
            }

        }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_CHANGE_ASSIGNEE);
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(), ProcessOperationEnum.BUTTON_TYPE_CHANGE_ASSIGNEE);
    }
}
