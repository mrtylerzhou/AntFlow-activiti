package org.openoa.engine.conf.aspect;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.openoa.base.constant.enums.ProcessNodeEnum;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmBusinessParty;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmCallbackUrlConf;
import org.openoa.engine.bpmnconf.constant.enus.EventTypeEnum;
import org.openoa.engine.bpmnconf.service.biz.BpmnConfCommonServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableMessageServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmBusinessPartyServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmCallbackUrlConfServiceImpl;
import org.openoa.engine.factory.IAdaptorFactory;
import org.openoa.engine.vo.BpmVariableMessageVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * set message aspect
 */
@Slf4j
@Aspect
@Component
public class BpmnSendMessageAspect {

    @Autowired
    private BpmVariableMessageServiceImpl bpmVariableMessageService;

    @Autowired
    private BpmnConfCommonServiceImpl bpmnConfCommonService;
    @Autowired
    private OutSideBpmBusinessPartyServiceImpl outSideBpmBusinessPartyService;
    @Autowired
    private OutSideBpmCallbackUrlConfServiceImpl outSideBpmCallbackUrlConfService;
    @Autowired
    private IAdaptorFactory adaptorFactory;
    
    @Around("execution(* org.openoa.base.interf.ProcessOperationAdaptor.doProcessButton(..))")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {

        //get BusinessDataVo
        BusinessDataVo businessDataVo = getBusinessDataVo(joinPoint.getArgs());

        if (businessDataVo == null) {
            throw new JiMuBizException("入参为空，请检查你的参数！");
        }
        


        //get bpmn conf by form code
        BpmnConf bpmnConf = bpmnConfCommonService.getBpmnConfByFormCode(businessDataVo.getFormCode());



        //check whether form code is valid
        if (ObjectUtils.isEmpty(bpmnConf) || ObjectUtils.isEmpty(bpmnConf.getId())) {
            throw new JiMuBizException("表单编号[" + businessDataVo.getFormCode() + "]未匹配到工作流配置，请检查入参或工作流相关配置");
        }
        if (bpmnConf.getIsOutSideProcess() == 1) {
            businessDataVo.setIsOutSideAccessProc(true);
        }
        //query business party info
        Integer businessPartyId = bpmnConf.getBusinessPartyId();
        OutSideBpmBusinessParty outSideBpmBusinessParty=null;
        if(businessPartyId!=null){
            outSideBpmBusinessParty= outSideBpmBusinessPartyService.getById(bpmnConf.getBusinessPartyId());

            //set outside type
            businessDataVo.setOutSideType(outSideBpmBusinessParty.getType());
        }
        if(outSideBpmBusinessParty==null){
            outSideBpmBusinessParty=new OutSideBpmBusinessParty();
        }


        //map bpmn conf to bpmn conf vo
        BpmnConfVo bpmnConfVo = Optional.ofNullable(bpmnConf).map(o -> {
            BpmnConfVo bcVo = new BpmnConfVo();
            BeanUtils.copyProperties(o,bcVo);
            return bcVo;
        }).orElse(new BpmnConfVo());
        businessDataVo.setBpmnConfVo(bpmnConfVo);

        //is form data is not null then set form data
        if (!ObjectUtils.isEmpty(businessDataVo.getFormData())) {
            bpmnConfVo.setFormData(businessDataVo.getFormData());
        }

        //set bpmn conf
        businessDataVo.setBpmnCode(bpmnConf.getBpmnCode());

        //set bpmn name
        businessDataVo.setBpmnName(bpmnConf.getBpmnName());


        //declare variable message vo
        BpmVariableMessageVo vo;


        //to check if it is a submit operation then execute aspect method first then assemble variable message vo
        if (businessDataVo.getOperationType().equals(ProcessOperationEnum.BUTTON_TYPE_SUBMIT.getCode())) {


            //do execute aspect method
            doMethod( bpmnConf,businessDataVo,outSideBpmBusinessParty,ProcessOperationEnum.BUTTON_TYPE_SUBMIT, joinPoint);


            //get bpmn variable message vo
            vo = getBpmVariableMessageVo(businessDataVo);

            /**
             * 因为发起流程组装流程发送vo对象是一个后置操作，所以从流程引擎中查到的是发起节点的下一个节点
             * 所以默认设置元素节点Id为"task1418018332271"
             */
            vo.setElementId(ProcessNodeEnum.START_TASK_KEY.getDesc());

        } else {

            //get bpmn variable message vo
            vo = getBpmVariableMessageVo(businessDataVo);


            //get process operation enum by operation type
            ProcessOperationEnum processOperationEunm = ProcessOperationEnum.getEnumByCode(businessDataVo.getOperationType());

            //do execute aspect method
            doMethod( bpmnConf,businessDataVo,outSideBpmBusinessParty, processOperationEunm, joinPoint);

        }

        //send message
        if (!ObjectUtils.isEmpty(vo)) {

            //if it is a third party process then execute the finish call back method
            if (bpmnConf.getIsOutSideProcess() == 1) {

                //if it is a third party process then set flat to true
                vo.setIsOutside(true);
                businessDataVo.setIsOutSideAccessProc(true);
            }
            bpmVariableMessageService.sendTemplateMessagesAsync(vo);
        }

    }

    /**
     * set send message vo
     *
     * @param businessDataVo
     * @return
     */
    private BpmVariableMessageVo getBpmVariableMessageVo(BusinessDataVo businessDataVo) {

        if (ObjectUtils.isEmpty(businessDataVo)) {
            return  null;
        }

            //get event type by operation type
            EventTypeEnum eventTypeEnum = EventTypeEnum.getEnumByOperationType(businessDataVo.getOperationType());

            if (ObjectUtils.isEmpty(eventTypeEnum)) {
                return null;
            }


            //default link type is process type
            Integer type = 2;


            //if event type is cancel operation then link type is view type
            if (eventTypeEnum.equals(EventTypeEnum.PROCESS_CANCELLATION)) {
                type = 1;
            }


            //build message vo
            return bpmVariableMessageService.getBpmVariableMessageVo(BpmVariableMessageVo
                    .builder()
                    .processNumber(businessDataVo.getProcessNumber())
                    .formCode(businessDataVo.getFormCode())
                    .eventType(eventTypeEnum.getCode())
                    .forwardUsers(Optional.ofNullable(businessDataVo.getUserIds())
                            .orElse(Lists.newArrayList())
                            .stream()
                            .map(String::valueOf)
                            .collect(Collectors.toList()))
                    .signUpUsers(Optional.ofNullable(businessDataVo.getSignUpUsers())
                            .orElse(Lists.newArrayList())
                            .stream()
                            .map(BaseIdTranStruVo::getId)
                            .collect(Collectors.toList()))
                    .messageType(eventTypeEnum.getIsInNode() ? 2 : 1)
                    .eventTypeEnum(eventTypeEnum)
                    .type(type)
                    .build());

    }

    /**
     * get BusinessDataVo
     *
     * @param args
     * @return
     */
    private BusinessDataVo getBusinessDataVo(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof BusinessDataVo) {
                return (BusinessDataVo) arg;
            }
        }
        return null;
    }

    /**
     *do aspect method
     *
     * @param joinPoint
     * @throws Throwable
     */
    private void doMethod( BpmnConf bpmnConf, BusinessDataVo businessDataVo, OutSideBpmBusinessParty outSideBpmBusinessParty,
                           ProcessOperationEnum processOperationEunm, ProceedingJoinPoint joinPoint) throws Throwable {
        //if it is an outside process,then set call back related information
        if (bpmnConf.getIsOutSideProcess() == 1
                && !businessDataVo.getIsOutSideChecked()) {

            businessDataVo.setOutSideType(outSideBpmBusinessParty.getType());

            ProcessOperationAdaptor bean = adaptorFactory.getProcessOperation(businessDataVo);

            if (bean==null) {
                throw new JiMuBizException(StringUtils.join(processOperationEunm.getDesc(), "功能实现未匹配，方法执行失败！"));
            }


            //query and set process flow callback url
            OutSideBpmCallbackUrlConf outSideBpmCallbackUrlConf = outSideBpmCallbackUrlConfService.getOutSideBpmCallbackUrlConf(bpmnConf.getId(), bpmnConf.getBusinessPartyId());


            //set bpm flow callback url
            businessDataVo.setBpmFlowCallbackUrl(outSideBpmCallbackUrlConf.getBpmFlowCallbackUrl());


            businessDataVo.setIsOutSideChecked(true);
            bean.doProcessButton(businessDataVo);
        } else {
            // do proceed
            joinPoint.proceed(new Object[]{businessDataVo});
        }
    }


}
