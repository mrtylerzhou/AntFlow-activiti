package org.openoa.engine.bpmnconf.service.processor;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.dto.BpmNextTaskDto;
import org.openoa.base.entity.BpmFlowrunEntrust;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.UserEntrustServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class NextNodeForwardProcessor  implements AntFlowNextNodeBeforeWriteProcessor{
    @Resource
    private UserEntrustServiceImpl userEntrustService;
    @Resource
    private BpmFlowrunEntrustServiceImpl bpmFlowrunEntrustService;

    @Override
    public void postProcess(BpmNextTaskDto bpmNextTaskDto) {
        DelegateTask delegateTask = bpmNextTaskDto.getDelegateTask();
        String formCode = bpmNextTaskDto.getFormCode();
        //set process entrust info
        String oldUserId = delegateTask.getAssignee();
        String oldUserName="";
        if(delegateTask instanceof TaskEntity){
            oldUserName=((TaskEntity)delegateTask).getAssigneeName();
        }
        BaseIdTranStruVo entrustEmployee = userEntrustService.getEntrustEmployee(oldUserId,oldUserName, formCode);
        String userId =entrustEmployee.getId();
        String userName=entrustEmployee.getName();

        //if userId is not null and valid then set user task delegate
        if (!StringUtils.isEmpty(userId)) {
            delegateTask.setAssignee(userId);
            if(delegateTask instanceof  TaskEntity){
                ((TaskEntity)delegateTask).setAssigneeName(userName);
            }
        }



        //如果委托生效 则在我的委托列表中加一条数据
        if (!oldUserId.equals(userId)) {
            BpmFlowrunEntrust entrust = new BpmFlowrunEntrust();
            entrust.setType(1);
            entrust.setRuntaskid(delegateTask.getId());
            entrust.setActual(userId);
            entrust.setActualName(userName);
            entrust.setOriginal(oldUserId);
            entrust.setOriginalName(oldUserName);
            entrust.setIsRead(2);
            entrust.setProcDefId(formCode);
            entrust.setRuninfoid(delegateTask.getProcessInstanceId());
            bpmFlowrunEntrustService.addFlowrunEntrust(entrust);
            log.info("委托生效，委托前：{}，委托后；{}", oldUserId, userId);
        }
    }

    @Override
    public int order() {
        return 1;
    }
}
