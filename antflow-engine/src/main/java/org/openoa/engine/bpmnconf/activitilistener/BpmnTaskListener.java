package org.openoa.engine.bpmnconf.activitilistener;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang3.StringUtils;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.base.constant.enums.ProcessNodeEnum;
import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.UserEntrustServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @ description: bpmn node execution listener
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class BpmnTaskListener implements TaskListener {

    @Resource
    private BpmnConfServiceImpl bpmnConfService;

    @Resource
    private ProcessBusinessContans processBusinessContans;

    @Resource
    private UserEntrustServiceImpl userEntrustService;

    @Resource
    private BpmFlowrunEntrustServiceImpl bpmFlowrunEntrustService;


    @Override
    public void notify(DelegateTask delegateTask) {

        //if it is the start user node then do nothing
        if (delegateTask.getTaskDefinitionKey().equals(ProcessNodeEnum.START_TASK_KEY.getDesc())) {
            return;
        }

        //bpmnCode
        String bpmnCode = Optional.ofNullable(delegateTask.getVariable("bpmnCode"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //processNumber
        String processNumber = Optional.ofNullable(delegateTask.getVariable("processNumber"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //formCode
        String formCode = Optional.ofNullable(delegateTask.getVariable("formCode"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //businessId
        String businessId = Optional.ofNullable(delegateTask.getVariable("businessId"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //start user id
        String startUser = Optional.ofNullable(delegateTask.getVariable("startUser"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //bpmn conf
        BpmnConf bpmnConf = bpmnConfService.getBaseMapper().selectOne(new QueryWrapper<BpmnConf>()
                .eq("bpmn_code", bpmnCode));
        delegateTask.setDescription("hello description");
        //set process entrust info
        Integer oldUserId = Integer.parseInt(delegateTask.getAssignee());
        Integer userId = userEntrustService.getEntrustEmployee(oldUserId, formCode);

        /*if(!ObjectUtils.isEmpty(userId)&&userId!=0){
            delegateTask.setAssignee(userId.toString());
        }*/
        }
    }
