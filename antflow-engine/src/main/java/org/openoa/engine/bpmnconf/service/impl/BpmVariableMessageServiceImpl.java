package org.openoa.engine.bpmnconf.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.MessageSendTypeEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmVariableApproveRemind;
import org.openoa.base.entity.Employee;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.entity.BpmVariableMultiplayerPersonnel;
import org.openoa.common.entity.BpmVariableSingle;
import org.openoa.common.service.BpmVariableMultiplayerPersonnelServiceImpl;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.common.service.BpmVariableSingleServiceImpl;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.common.ProcessContans;
import org.openoa.engine.bpmnconf.confentity.BpmVariable;
import org.openoa.engine.bpmnconf.confentity.BpmVariableMessage;
import org.openoa.engine.bpmnconf.confentity.BpmVariableSignUpPersonnel;
import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMessageMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.vo.BpmVariableMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname BpmVariableMessageServiceImpl
 * @Description TODO 此类员工信息需要处理
 * @Date 2021-11-27 15:44
 * @Created by AntOffice
 */
@Service
public class BpmVariableMessageServiceImpl extends ServiceImpl<BpmVariableMessageMapper, BpmVariableMessage> {

    @Autowired
    private BpmVariableMessageMapper mapper;

    @Autowired
    private BpmnConfServiceImpl bpmnConfService;

    @Autowired
    private BpmVariableServiceImpl bpmVariableService;

    @Autowired
    private BpmVariableApproveRemindServiceImpl bpmVariableApproveRemindService;

    @Autowired
    private EmployeeServiceImpl employeeService;

    @Autowired
    private BpmProcessNoticeServiceImpl bpmProcessNoticeService;

    @Autowired
    private ProcessBusinessContans processBusinessContans;


    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProcessContans processContans;

    @Autowired
    private BpmVariableSingleServiceImpl bpmVariableSingleService;

    @Autowired
    private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;

    @Autowired
    private BpmVariableMultiplayerPersonnelServiceImpl bpmVariableMultiplayerPersonnelService;

    @Autowired
    private BpmVariableSignUpPersonnelServiceImpl bpmVariableSignUpPersonnelService;

    @Autowired
    private BpmProcessForwardServiceImpl bpmProcessForwardService;



    /**
     * insert variable message config
     * @param variableId
     * @param bpmnConfCommonVo
     */
    public void insertVariableMessage(Long variableId, BpmnConfCommonVo bpmnConfCommonVo) {


        //variable message list
        List<BpmVariableMessage> bpmVariableMessages = Lists.newArrayList();


        //process node approval remind list
        List<BpmVariableApproveRemind> bpmVariableApproveReminds = Lists.newArrayList();


        // add out of node variable message config
        if (!ObjectUtils.isEmpty(bpmnConfCommonVo.getTemplateVos())) {
            bpmVariableMessages.addAll(getBpmVariableMessages(variableId, bpmnConfCommonVo.getTemplateVos(), StringUtils.EMPTY, 1));
        }


        //add in node message config
        if (!ObjectUtils.isEmpty(bpmnConfCommonVo.getElementList())) {
            for (BpmnConfCommonElementVo elementVo : bpmnConfCommonVo.getElementList()) {
                if (ObjectUtils.isEmpty(elementVo.getTemplateVos())) {
                    continue;
                }
                bpmVariableMessages.addAll(getBpmVariableMessages(variableId, elementVo.getTemplateVos(), elementVo.getElementId(), 2));

                //add process node approval remind list
                if (!ObjectUtils.isEmpty(elementVo.getApproveRemindVo()) &&
                        !ObjectUtils.isEmpty(elementVo.getApproveRemindVo().getDays())) {
                    bpmVariableApproveReminds.add(BpmVariableApproveRemind
                            .builder()
                            .variableId(variableId)
                            .elementId(elementVo.getElementId())
                            .content(JSON.toJSONString(elementVo.getApproveRemindVo()))
                            .build());
                }
            }
        }


        // if variable messages are not empty,then save them in batch
        if (!ObjectUtils.isEmpty(bpmVariableMessages)) {
            this.saveBatch(bpmVariableMessages);
        }


        //if approval reminds are not empty then save them in batch
        if (!ObjectUtils.isEmpty(bpmVariableApproveReminds)) {
            bpmVariableApproveRemindService.saveBatch(bpmVariableApproveReminds);
        }
    }

    /**
     * get variable messages list
     *
     * @param variableId
     * @param templateVos
     * @param messageType
     * @return
     */
    private List<BpmVariableMessage> getBpmVariableMessages(Long variableId, List<BpmnTemplateVo> templateVos, String elementId, Integer messageType) {
        return templateVos
                .stream()
                .map(o -> BpmVariableMessage
                        .builder()
                        .variableId(variableId)
                        .elementId(elementId)
                        .messageType(messageType)
                        .eventType(o.getEvent())
                        .content(JSON.toJSONString(o))
                        .build())
                .collect(Collectors.toList());
    }

    /**

     * check whether to to send messages by template
     *
     * @param vo
     * @return
     */
    public Boolean checkIsSendByTemplate(BpmVariableMessageVo vo) {

        BpmVariable bpmVariable = bpmVariableService.getBaseMapper().selectOne(new QueryWrapper<BpmVariable>().eq("process_num", vo.getProcessNumber()));
        if (ObjectUtils.isEmpty(bpmVariable)) {
            return false;
        }

        if (Objects.equals(vo.getMessageType(), 1)) {//out of node messages
            return this.mapper.selectCount(new QueryWrapper<BpmVariableMessage>()
                    .eq("variable_id", bpmVariable.getId())
                    .eq("message_type", 1)
                    .eq("event_type", vo.getEventType())) > 0;
        } else if (Objects.equals(vo.getMessageType(), 2)) {//in node messages
            return this.mapper.selectCount(new QueryWrapper<BpmVariableMessage>()
                    .eq("variable_id", bpmVariable.getId())
                    .eq("element_id", vo.getElementId())
                    .eq("message_type", 2)
                    .eq("event_type", vo.getEventType())) > 0;
        }
        return false;
    }

    /**
     * build variable message vo for sending messages
     *
     * @param vo
     */
    public BpmVariableMessageVo getBpmVariableMessageVo(BpmVariableMessageVo vo) {


        if (ObjectUtils.isEmpty(vo)) {
            return null;
        }


        BpmVariable bpmVariable = null;
        List<BpmVariable> bpmVariables = bpmVariableService.getBaseMapper().selectList(new QueryWrapper<BpmVariable>().eq("process_num", vo.getProcessNumber()));
        if(!ObjectUtils.isEmpty(bpmVariables)){
            bpmVariable=bpmVariables.get(0);
        }

        if (ObjectUtils.isEmpty(bpmVariable)) {
            return null;
        }


        //set variable id
        vo.setVariableId(bpmVariable.getId());


        //get bpmn conf
        BpmnConf bpmnConf = bpmnConfService.getBaseMapper().selectOne(new QueryWrapper<BpmnConf>()
                .eq("bpmn_code", bpmVariable.getBpmnCode()));

        //set bpmn code
        vo.setBpmnCode(bpmnConf.getBpmnCode());

        //set bpmn name
        vo.setBpmnName(bpmnConf.getBpmnName());

        //set form code
        vo.setFormCode(bpmnConf.getFormCode());

        //todo
        //process type info
        //vo.setProcessType(SysDicUtils.getDicNameByCode("DIC_LCLB", bpmnConf.getBpmnType()));

        //set process start variables
        if (!ObjectUtils.isEmpty(bpmVariable.getProcessStartConditions())) {
            BpmnStartConditionsVo bpmnStartConditionsVo = JSON.parseObject(bpmVariable.getProcessStartConditions(), BpmnStartConditionsVo.class);
            vo.setBpmnStartConditions(bpmnStartConditionsVo);
            //set approval employee id
            vo.setApprovalEmplId(Optional.ofNullable(bpmnStartConditionsVo.getApprovalEmplId()).map(String::valueOf).orElse("0"));
        }


        //query bpmn business process by process number
        BpmBusinessProcess businessProcess = bpmBusinessProcessService.getBaseMapper().selectOne(new QueryWrapper<BpmBusinessProcess>().eq("BUSINESS_NUMBER", vo.getProcessNumber()));

        if (ObjectUtils.isEmpty(businessProcess)) {
            return null;
        }


        //call activiti's history service to query history process instance
        HistoricProcessInstance processInstance =historyService.createHistoricProcessInstanceQuery().processInstanceId(businessProcess.getProcInstId()).singleResult();

        //todo to be optimized
        //set process instance id
        vo.setProcessInsId(processInstance.getId());

        //set start user id
        vo.setStartUser(processInstance.getStartUserId());

        //get process's start time
        Date processStartTime = processInstance.getStartTime();

        //set apply date
        vo.setApplyDate(DateUtil.SDF_DATE_PATTERN.format(processStartTime));

        //set apply time
        vo.setApplyTime(DateUtil.SDF_DATETIME_PATTERN.format(processStartTime));


        //get a list of history tasks
        List<HistoricTaskInstance> hisTask = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstance.getId()).list();


        //set already approved employee id
        vo.setApproveds(hisTask
                .stream()
                .filter(o -> !ObjectUtils.isEmpty(o.getAssignee()))
                .map(HistoricTaskInstance::getAssignee)
                .collect(Collectors.toList()));


        //if the current node approver is empty, then get it from login user info
        if (StringUtils.isEmpty(vo.getAssignee())) {

            vo.setAssignee( SecurityUtils.getLogInEmpIdSafe().toString());
        }


        //if the event type is in node event, then get the node info from activiti process engine
        if (vo.getEventTypeEnum().getIsInNode()) {
            //get current task list by process instance id
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
            if (!ObjectUtils.isEmpty(tasks)) {

                //if node is empty then get from task's definition
                if (ObjectUtils.isEmpty(vo.getElementId())) {
                    String nodeId = tasks.stream().map(Task::getTaskDefinitionKey).distinct().findFirst().orElse(StringUtils.EMPTY);
                    vo.setElementId(nodeId);
                }

                //if task id is empty then get it from current tasks
                if (ObjectUtils.isEmpty(vo.getTaskId())) {
                    String taskId = tasks.stream().map(Task::getId).distinct().findFirst().orElse(StringUtils.EMPTY);
                    vo.setTaskId(taskId);
                }

                //if link type is empty then set it default to 1
                if (ObjectUtils.isEmpty(vo.getType())) {
                    vo.setType(1);
                }

                //get process's next node info via activiti's pvm
                PvmActivity nextNodePvmActivity = processContans.getNextNodePvmActivity(processInstance.getId());

                //if next node is not empty and next node is not end event,then process it
                if (!ObjectUtils.isEmpty(nextNodePvmActivity)) {
                    if (!"endEvent".equals(nextNodePvmActivity.getProperty("type"))) {
                        //next element's id
                        String nextElementId = nextNodePvmActivity.getId();
                        //set next node's approvers
                        vo.setNextNodeApproveds(getNextNodeApproveds(bpmVariable.getId(), nextElementId));
                    }
                }
            }
        }

        return vo;
    }

    /**
     * get next node's approvers
     *
     * @param variableId
     * @param nextElementId
     * @return
     */
    private List<String> getNextNodeApproveds(Long variableId, String nextElementId) {


        //query to check whether sign variable has parameter,if yes then return;
        if (bpmVariableSingleService.getBaseMapper().selectCount(new QueryWrapper<BpmVariableSingle>()
                .eq("variable_id", variableId)
                .eq("element_id", nextElementId)) > 0) {
            List<String> nextNodeApproveds = Lists.newArrayList();
            BpmVariableSingle bpmVariableSingle = bpmVariableSingleService.getBaseMapper().selectOne(new QueryWrapper<BpmVariableSingle>()
                    .eq("variable_id", variableId)
                    .eq("element_id", nextElementId));
            nextNodeApproveds.add(bpmVariableSingle.getAssignee());
            return nextNodeApproveds;
        }


        //query to check whether multiplayer variables have parameter,if yes then return;
        if (bpmVariableMultiplayerService.getBaseMapper().selectCount(new QueryWrapper<BpmVariableMultiplayer>()
                .eq("variable_id", variableId)
                .eq("element_id", nextElementId)) > 0) {
            List<String> nextNodeApproveds = Lists.newArrayList();
            BpmVariableMultiplayer bpmVariableMultiplayer = bpmVariableMultiplayerService.getBaseMapper().selectOne(new QueryWrapper<BpmVariableMultiplayer>()
                    .eq("variable_id", variableId)
                    .eq("element_id", nextElementId));
            nextNodeApproveds.addAll(bpmVariableMultiplayerPersonnelService.getBaseMapper().selectList(new QueryWrapper<BpmVariableMultiplayerPersonnel>()
                    .eq("variable_multiplayer_id", bpmVariableMultiplayer.getId()))
                    .stream()
                    .map(BpmVariableMultiplayerPersonnel::getAssignee)
                    .collect(Collectors.toList()));
            return nextNodeApproveds;
        }


        //query to check whether sign up node has parameters,if yes,then query and return data
        if (bpmVariableSignUpPersonnelService.getBaseMapper().selectCount(new QueryWrapper<BpmVariableSignUpPersonnel>()
                .eq("variable_id", variableId)
                .eq("element_id", nextElementId)) > 0) {
            List<String> nextNodeApproveds = Lists.newArrayList();
            nextNodeApproveds.addAll(bpmVariableSignUpPersonnelService.getBaseMapper().selectList(new QueryWrapper<BpmVariableSignUpPersonnel>()
                    .eq("variable_id", variableId)
                    .eq("element_id", nextElementId))
                    .stream()
                    .map(BpmVariableSignUpPersonnel::getAssignee)
                    .collect(Collectors.toList()));
            return nextNodeApproveds;
        }

        return Collections.EMPTY_LIST;
    }

    /**

     * send templated messages in async way
     *
     * @param vo
     */
    @Async
    public void sendTemplateMessagesAsync(BpmVariableMessageVo vo) {
        doSendTemplateMessages(vo);
    }

    /**
     * send templated messages in sync way
     *
     * @param vo
     */
    public void sendTemplateMessages(BpmVariableMessageVo vo) {
        doSendTemplateMessages(vo);
    }

    /**
     * do send templated messages
     *
     * @param vo
     */
    private void doSendTemplateMessages(BpmVariableMessageVo vo) {


        //if next node's approvers is empty then query current tasks instead
        if (ObjectUtils.isEmpty(vo.getNextNodeApproveds())) {
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(vo.getProcessInsId()).list();
            if (!ObjectUtils.isEmpty(tasks)) {
                vo.setNextNodeApproveds(tasks.stream().map(Task::getAssignee).collect(Collectors.toList()));
            }
        }

        if (Objects.equals(vo.getMessageType(), 1)) {//out of node messages
            List<BpmVariableMessage> bpmVariableMessages = this.mapper.selectList(new QueryWrapper<BpmVariableMessage>()
                    .eq("variable_id", vo.getVariableId())
                    .eq("message_type", 1)
                    .eq("event_type", vo.getEventType()));
            if (!ObjectUtils.isEmpty(bpmVariableMessages)) {
                for (BpmVariableMessage bpmVariableMessage : bpmVariableMessages) {
                    doSendTemplateMessages(bpmVariableMessage, vo);
                }
            }
        } else if (Objects.equals(vo.getMessageType(), 2)) {//in node messages
            List<BpmVariableMessage> bpmVariableMessages = this.mapper.selectList(new QueryWrapper<BpmVariableMessage>()
                    .eq("variable_id", vo.getVariableId())
                    .eq("element_id", vo.getElementId())
                    .eq("message_type", 2)
                    .eq("event_type", vo.getEventType()));
            if (!ObjectUtils.isEmpty(bpmVariableMessages)) {
                for (BpmVariableMessage bpmVariableMessage : bpmVariableMessages) {
                    doSendTemplateMessages(bpmVariableMessage, vo);
                }
            }
        }
    }

    /**
     * do send templated messages
     *
     * @param bpmVariableMessage
     */
    private void doSendTemplateMessages(BpmVariableMessage bpmVariableMessage, BpmVariableMessageVo vo) {

        BpmnTemplateVo bpmnTemplateVo = new BpmnTemplateVo();
        if (!ObjectUtils.isEmpty(bpmVariableMessage.getContent())) {
            bpmnTemplateVo = JSON.parseObject(bpmVariableMessage.getContent(), BpmnTemplateVo.class);
        }


        //query sender's info
        List<Serializable> sendUsers = getSendUsers(vo, bpmnTemplateVo);


        //if senders is empty then return
        if (ObjectUtils.isEmpty(sendUsers)) {
            return;
        }

        //todo

        //send messages
        sendMessage(vo, bpmnTemplateVo, null);

    }

    /**
     * send messages
     *
     * @param vo
     * @param bpmnTemplateVo
     * @param employees
     */
    private void sendMessage(BpmVariableMessageVo vo, BpmnTemplateVo bpmnTemplateVo, List<Employee> employees) {

    }


    /**
     * get UrlMap
     *
     * @param vo
     * @param informationTemplateVo
     * @return
     */
    public Map<String, String> getUrlMap(BpmVariableMessageVo vo, InformationTemplateVo informationTemplateVo) {

        Map<String, String> urlMap = Maps.newHashMap();


        return urlMap;
    }

    /**
     * get user messages in batch
     *
     * @param o
     * @param title
     * @param content
     * @param taskId
     * @param emailUrl
     * @param appUrl
     * @param messageSendTypeEnum
     * @return
     */
    private UserMsgBathVo getUserMsgBathVo(Employee o, String title, String content, String taskId,
                                           String emailUrl, String appUrl, MessageSendTypeEnum messageSendTypeEnum) {
        return UserMsgBathVo
                .builder()
                .userMsgVo(UserMsgVo
                        .builder()
                        .userId(o.getId())
                        .email(o.getEmail())
                        .mobile(o.getMobile())
                        .title(title)
                        .content(content)
                        .emailUrl(emailUrl)
                        .url(emailUrl)
                        .appPushUrl(appUrl)
                        .taskId(taskId)
                        .build())
                .messageSendTypeEnums(Lists.newArrayList(messageSendTypeEnum))
                .build();
    }

    /**
     * get wildcard characters map
     *
     * @param vo
     * @return
     */
    private Map<Integer, String> getWildcardCharacterMap(BpmVariableMessageVo vo) {
        Map<Integer, String> wildcardCharacterMap = Maps.newHashMap();

        return wildcardCharacterMap;
    }

    /**
     * get sender's info
     *
     * @param vo
     * @param bpmnTemplateVo
     * @return
     */
    private List<Serializable> getSendUsers(BpmVariableMessageVo vo, BpmnTemplateVo bpmnTemplateVo) {
        //todo
        return Lists.newArrayList();
    }

}
