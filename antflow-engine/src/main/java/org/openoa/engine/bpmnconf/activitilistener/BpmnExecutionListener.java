package org.openoa.engine.bpmnconf.activitilistener;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessNoticeEnum;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.vo.ActivitiBpmMsgVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmCallbackUrlConf;
import org.openoa.engine.bpmnconf.constant.enus.EventTypeEnum;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmVariableMessageListenerServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.ThirdPartyCallBackServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmCallbackUrlConfServiceImpl;
import org.openoa.engine.bpmnconf.util.ActivitiTemplateMsgUtils;
import org.openoa.engine.factory.FormFactory;
import org.openoa.engine.vo.BpmVariableMessageVo;
import org.openoa.engine.vo.ProcessInforVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

import static org.openoa.base.constant.enums.CallbackTypeEnum.PROC_FINISH_CALL_BACK;


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
    @Resource
    private OutSideBpmCallbackUrlConfServiceImpl outSideBpmCallbackUrlConfService;
    @Resource
    private BpmVariableMessageListenerServiceImpl bpmVariableMessageListenerService;
    @Resource
    private ThirdPartyCallBackServiceImpl thirdPartyCallBackService;


    @Override
    public void notify(DelegateExecution delegateExecution) {

        //bpmn code
        String bpmnCode = Optional.ofNullable(delegateExecution.getVariable("bpmnCode"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //process number
        String processNumber = Optional.ofNullable(delegateExecution.getVariable("processNumber"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //form code
        String formCode = Optional.ofNullable(delegateExecution.getVariable("formCode"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //business id
        String businessId = Optional.ofNullable(delegateExecution.getVariable("businessId"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //start user id
        String startUser = Optional.ofNullable(delegateExecution.getVariable("startUser"))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
        //process conf
        BpmnConf bpmnConf = bpmnConfService.getBaseMapper().selectOne(new QueryWrapper<BpmnConf>()
                .eq("bpmn_code", bpmnCode));


        if (ObjectUtils.isEmpty(bpmnConf)) {
            log.error("global process event listener-bpmnconf is empty，processNumber{}", processNumber);
            return;
        }

        log.info("execute{}process finished event Listener!", processNumber);

        //to indicate it is not an outside process
        boolean isOutside = false;

        //判断如果是外部流程则回调通知业务方流程已完成
        if (bpmnConf.getIsOutSideProcess() == 1) {

            //如果是外部流程则设置布尔值为true
            isOutside = true;

            //根据业务方Id&流程配置Id查询单个业务方回调接口配置
            OutSideBpmCallbackUrlConf outSideBpmCallbackUrlConf = outSideBpmCallbackUrlConfService.getOutSideBpmCallbackUrlConf(bpmnConf.getId(), bpmnConf.getBusinessPartyId());

            BpmnConfVo bpmnConfVo = new BpmnConfVo();
            BeanUtils.copyProperties(bpmnConf, bpmnConfVo);

            //回调通知业务方流程已完成
            thirdPartyCallBackService.doCallback(outSideBpmCallbackUrlConf.getBpmFlowCallbackUrl(), PROC_FINISH_CALL_BACK, bpmnConfVo, processNumber, businessId,"");

        } else {
            BusinessDataVo businessDataVo=new BusinessDataVo();
            businessDataVo.setBusinessId(businessId);
            businessDataVo.setFormCode(formCode);
           if(Objects.equals(bpmnConf.getIsLowCodeFlow(),1)){
               businessDataVo.setIsLowCodeFlow(1);
               BpmnConfVo confVo=new BpmnConfVo();
               BeanUtils.copyProperties(bpmnConf,confVo);
               businessDataVo.setBpmnConfVo(confVo);

           }
            formFactory.getFormAdaptor(businessDataVo).finishData(businessDataVo);
        }

        //execute the process finish method and update status
        bpmBusinessProcessService.updateBusinessProcess(BpmBusinessProcess.builder()
                .businessNumber(processNumber)
                .processState(ProcessStateEnum.HANDLED_STATE.getCode())
                .build());

        BpmVariableMessageVo bpmVariableMessageVo = BpmVariableMessageVo
                .builder()
                .processNumber(processNumber)
                .formCode(formCode)
                .eventType(EventTypeEnum.PROCESS_END.getCode())
                .messageType(Boolean.TRUE.equals(EventTypeEnum.PROCESS_END.getIsInNode()) ? 2 : 1)
                .eventTypeEnum(EventTypeEnum.PROCESS_END)
                .type(1)
                .build();

        if (bpmVariableMessageListenerService.listenerCheckIsSendByTemplate(bpmVariableMessageVo)) {

            bpmVariableMessageVo.setIsOutside(isOutside);

            bpmVariableMessageListenerService.listenerSendTemplateMessages(bpmVariableMessageVo);
        } else {

            ProcessInforVo processInforVo = ProcessInforVo
                    .builder()
                    .processinessKey(bpmnCode)
                    .businessNumber(processNumber)
                    .formCode(formCode)
                    .type(1)
                    .build();
            ActivitiTemplateMsgUtils.sendBpmFinishMsg(
                    ActivitiBpmMsgVo
                            .builder()
                            .userId(startUser)
                            .processId(processNumber)
                            .bpmnCode(bpmnCode)
                            .formCode(formCode)
                            .processType("")//todo
                            .processName(bpmnConf.getBpmnName())
                            .emailUrl(processBusinessContans.getRoute(ProcessNoticeEnum.EMAIL_TYPE.getCode(),
                                   processInforVo, isOutside))
                            .url(processBusinessContans.getRoute(ProcessNoticeEnum.EMAIL_TYPE.getCode(),
                                    processInforVo, isOutside))
                            .appPushUrl(processBusinessContans.getRoute(ProcessNoticeEnum.APP_TYPE.getCode(),
                                    processInforVo, isOutside))
                            .taskId(delegateExecution.getProcessInstanceId())
                            .build());
        }
    }
}
