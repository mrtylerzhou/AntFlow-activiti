package org.openoa.engine.bpmnconf.service.biz;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransferAssigneeProcessImpl implements ProcessOperationAdaptor {
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
        //传入的用户信息List中第一个是原办理人信息,第二个是将要转的办理人信息
        List<BaseIdTranStruVo> userInfos = vo.getUserInfos();
        if(userInfos.size()!=2){
            throw new JiMuBizException("转办人员配置错误,无法转办!");
        }
        String originalUserId=userInfos.get(0).getId();
        String originalUserName=userInfos.get(0).getName();
        String transferToUserId=userInfos.get(1).getId();
        String transferToUserName=userInfos.get(1).getName();
        boolean matched=false;
        List<String> assignees = list.stream().map(TaskInfo::getAssignee).collect(Collectors.toList());
        int originAssigneeIndex = assignees.indexOf(originalUserId);
        if(originAssigneeIndex<0){
            throw new JiMuBizException("流程状态已变更,无当前办理人信息,转办失败!");
        }
        for (Task task : list) {
            String assignee = task.getAssignee();
            if (assignee.equals(originalUserId)) {
                bpmFlowrunEntrustService.addFlowrunEntrust(transferToUserId, transferToUserName, originalUserId, originalUserName, task.getId(), 0, bpmBusinessProcess.getProcInstId(), vo.getProcessKey());

                taskMgmtMapper.updateaActinst(TaskMgmtVO.builder().applyUser(transferToUserId).taskId(task.getId()).build());
                taskMgmtMapper.updateaTaskinst(TaskMgmtVO.builder().applyUser(transferToUserId).taskId(task.getId()).build());
                taskMgmtMapper.updateTask(TaskMgmtVO.builder().applyUser(transferToUserId).taskId(task.getId()).build());
                matched=true;
            }

        }
        if(!matched){
            throw new JiMuBizException("流程状态已变更,无当前办理人信息,转办失败!");
        }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_ZB);
    }
}
