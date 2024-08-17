package org.openoa.engine.bpmnconf.activitilistener;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfServiceImpl;
import org.openoa.engine.factory.FormFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Optional;


/**
 * @ description: bpmn finish execution listener
 * @author AntFlow
 * @since 0.0.1
 */
@Slf4j
@Component
public class BpmnExecutionListener implements ExecutionListener {

    @Resource
    private BpmnConfServiceImpl bpmnConfService;

    @Resource
    private ProcessBusinessContans processBusinessContans;

    @Resource
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Resource
    private FormFactory formFactory;


    @Override
    public void notify(DelegateExecution delegateExecution) {

        //审批流编号
        String bpmnCode = Optional.ofNullable(delegateExecution.getVariable("bpmnCode"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //流畅编号
        String processNumber = Optional.ofNullable(delegateExecution.getVariable("processNumber"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //表单编号
        String formCode = Optional.ofNullable(delegateExecution.getVariable("formCode"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //业务id
        String businessId = Optional.ofNullable(delegateExecution.getVariable("businessId"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //发起人id
        String startUser = Optional.ofNullable(delegateExecution.getVariable("startUser"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //审批流配置信息
        BpmnConf bpmnConf = bpmnConfService.getBaseMapper().selectOne(new QueryWrapper<BpmnConf>()
                .eq("bpmn_code", bpmnCode));


        if (ObjectUtils.isEmpty(bpmnConf)) {
            log.error("global process event listener-bpmnconf is empty，processNumber{}", processNumber);
            return;
        }

        log.info("execut" + processNumber + "process finished event Listener!");

        //to indicate it is not an outside process
        boolean isOutside = false;
        formFactory.getFormAdaptor(formCode).finishData(Long.parseLong(businessId));
        //execute the process finish method and update status
        bpmBusinessProcessService.updateBusinessProcess(BpmBusinessProcess.builder()
                .businessNumber(processNumber)
                .processState(ProcessStateEnum.HANDLE_STATE.getCode())
                .build());


        }
    }
