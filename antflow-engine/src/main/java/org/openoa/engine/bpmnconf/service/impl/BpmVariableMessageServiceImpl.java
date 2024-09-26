package org.openoa.engine.bpmnconf.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jodd.bean.BeanUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.*;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmVariableApproveRemind;
import org.openoa.base.entity.Employee;
import org.openoa.base.entity.User;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.service.RoleServiceImpl;
import org.openoa.base.util.AntCollectionUtil;
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
import org.openoa.engine.bpmnconf.confentity.*;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMessageMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.util.InformationTemplateUtils;
import org.openoa.engine.bpmnconf.util.UserMsgUtils;
import org.openoa.engine.vo.BpmVariableMessageVo;
import org.openoa.engine.vo.ProcessInforVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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
    private BpmnConfServiceImpl bpmnConfService;

    @Autowired
    private BpmVariableServiceImpl bpmVariableService;

    @Autowired
    private BpmVariableApproveRemindServiceImpl bpmVariableApproveRemindService;

    @Autowired
    private EmployeeServiceImpl employeeService;
    @Autowired
    private RoleServiceImpl roleService;

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
    @Autowired
    private InformationTemplateUtils informationTemplateUtils;



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

        if (vo.getMessageType()!=null&& vo.getMessageType()== 1) {//out of node messages
            return this.getBaseMapper().selectCount(new QueryWrapper<BpmVariableMessage>()
                    .eq("variable_id", bpmVariable.getId())
                    .eq("message_type", 1)
                    .eq("event_type", vo.getEventType())) > 0;
        } else if (vo.getMessageType()!=null&&vo.getMessageType()==2) {//in node messages
            return this.getBaseMapper().selectCount(new QueryWrapper<BpmVariableMessage>()
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
        if(bpmnConf==null){
            throw new JiMuBizException(Strings.lenientFormat("can not get bpmnConf by bpmncode:%s",bpmVariable.getBpmnCode()));
        }
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
        if (!StringUtils.isEmpty(bpmVariable.getProcessStartConditions())) {
            BpmnStartConditionsVo bpmnStartConditionsVo = JSON.parseObject(bpmVariable.getProcessStartConditions(), BpmnStartConditionsVo.class);
            vo.setBpmnStartConditions(bpmnStartConditionsVo);
            //set approval employee id
            vo.setApprovalEmplId(Optional.ofNullable(bpmnStartConditionsVo.getApprovalEmplId()).map(String::valueOf).orElse("0"));
        }


        //query bpmn business process by process number
        BpmBusinessProcess businessProcess = bpmBusinessProcessService.getBaseMapper().selectOne(new QueryWrapper<BpmBusinessProcess>().eq("BUSINESS_NUMBER", vo.getProcessNumber()));

        if (ObjectUtils.isEmpty(businessProcess)) {
            throw new JiMuBizException(Strings.lenientFormat("can not get BpmBusinessProcess by process Numbeer:%s",vo.getProcessNumber()));
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
                .filter(o -> !StringUtils.isEmpty(o.getAssignee()))
                .map(HistoricTaskInstance::getAssignee)
                .collect(Collectors.toList()));


        //if the current node approver is empty, then get it from login user info
        if (StringUtils.isEmpty(vo.getAssignee())) {

            vo.setAssignee(SecurityUtils.getLogInEmpIdSafe());
        }


        //if the event type is in node event, then get the node info from activiti process engine
        if (vo.getEventTypeEnum().getIsInNode()) {
            //get current task list by process instance id
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
            if (!ObjectUtils.isEmpty(tasks)) {

                //if node is empty then get from task's definition
                if (ObjectUtils.isEmpty(vo.getElementId())) {
                    String elementId = tasks.stream().map(Task::getTaskDefinitionKey).distinct().findFirst().orElse(StringUtils.EMPTY);
                    vo.setElementId(elementId);
                }

                //if task id is empty then get it from current tasks
                if (ObjectUtils.isEmpty(vo.getTaskId())) {
                    String taskId = tasks.stream().map(Task::getId).distinct().findFirst().orElse(StringUtils.EMPTY);
                    vo.setTaskId(taskId);
                }

                //if link type is empty then set it default to 1
                if (vo.getType()==null) {
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
        if (CollectionUtils.isEmpty(vo.getNextNodeApproveds())) {
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(vo.getProcessInsId()).list();
            if (!ObjectUtils.isEmpty(tasks)) {
                vo.setNextNodeApproveds(tasks.stream().map(Task::getAssignee).collect(Collectors.toList()));
            }
        }

        if (Objects.equals(vo.getMessageType(), 1)) {//out of node messages
            List<BpmVariableMessage> bpmVariableMessages = this.getBaseMapper().selectList(new QueryWrapper<BpmVariableMessage>()
                    .eq("variable_id", vo.getVariableId())
                    .eq("message_type", 1)
                    .eq("event_type", vo.getEventType()));
            if (!CollectionUtils.isEmpty(bpmVariableMessages)) {
                for (BpmVariableMessage bpmVariableMessage : bpmVariableMessages) {
                    doSendTemplateMessages(bpmVariableMessage, vo);
                }
            }
        } else if (Objects.equals(vo.getMessageType(), 2)) {//in node messages
            List<BpmVariableMessage> bpmVariableMessages = this.getBaseMapper().selectList(new QueryWrapper<BpmVariableMessage>()
                    .eq("variable_id", vo.getVariableId())
                    .eq("element_id", vo.getElementId())
                    .eq("message_type", 2)
                    .eq("event_type", vo.getEventType()));
            if (!CollectionUtils.isEmpty(bpmVariableMessages)) {
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
        List<String> sendToUsers = getSendToUsers(vo, bpmnTemplateVo);


        //if senders is empty then return
        if (ObjectUtils.isEmpty(sendToUsers)) {
            return;
        }

        List<Employee> employeeDetailByIds = employeeService.getEmployeeDetailByIds(sendToUsers.stream().distinct().collect(Collectors.toList()));
        if(ObjectUtils.isEmpty(employeeDetailByIds)){
            return;
        }

        //send messages
        sendMessage(vo, bpmnTemplateVo, employeeDetailByIds);

    }

    /**
     * send messages
     *
     * @param vo
     * @param bpmnTemplateVo
     * @param employees
     */
    private void sendMessage(BpmVariableMessageVo vo, BpmnTemplateVo bpmnTemplateVo, List<Employee> employees) {
        //query all types of the messages
        List<MessageSendTypeEnum> messageSendTypeEnums = bpmProcessNoticeService.processNoticeList(vo.getFormCode())
                .stream()
                .map(o -> {
                    return MessageSendTypeEnum.getEnumByCode(o.getType());
                })
                .collect(Collectors.toList());


        Map<Integer, String> wildcardCharacterMap = getWildcardCharacterMap(vo);
        InformationTemplateVo informationTemplateVo = informationTemplateUtils.translateInformationTemplate(InformationTemplateVo
                .builder()
                .id(bpmnTemplateVo.getTemplateId())
                .wildcardCharacterMap(wildcardCharacterMap)
                .build());

        //get message urls
        Map<String, String> urlMap = getUrlMap(vo, informationTemplateVo);
        String emailUrl = urlMap.get("emailUrl");
        String appUrl = urlMap.get("appUrl");

        for (MessageSendTypeEnum messageSendTypeEnum : messageSendTypeEnums) {
            if (Objects.isNull(messageSendTypeEnum)) {
                continue;
            }
            switch (messageSendTypeEnum) {
                case MAIL:

                    UserMsgUtils.sendMessageBathNoUserMessage(employees
                            .stream()
                            .map(o -> getUserMsgBathVo(o, informationTemplateVo.getMailTitle(), informationTemplateVo.getMailContent(),
                                    vo.getTaskId(), emailUrl, appUrl, MessageSendTypeEnum.MAIL))
                            .collect(Collectors.toList()));
                    break;
                case MESSAGE:
                    UserMsgUtils.sendMessageBathNoUserMessage(employees
                            .stream()
                            .map(o -> getUserMsgBathVo(o, StringUtils.EMPTY, informationTemplateVo.getNoteContent(),
                                    vo.getTaskId(), emailUrl, appUrl, MessageSendTypeEnum.MESSAGE))
                            .collect(Collectors.toList()));
                    break;
                case PUSH:
                    UserMsgUtils.sendMessageBathNoUserMessage(employees
                            .stream()
                            .map(o -> getUserMsgBathVo(o, StringUtils.EMPTY, informationTemplateVo.getNoteContent(),
                                    vo.getTaskId(), emailUrl, appUrl, MessageSendTypeEnum.PUSH))
                            .collect(Collectors.toList()));
                    break;
            }
        }
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

        String emailUrl = StringUtils.EMPTY;
        String appUrl = StringUtils.EMPTY;

        if (informationTemplateVo.getJumpUrl()!=null &&
                (informationTemplateVo.getJumpUrl().equals(JumpUrlEnum.PROCESS_APPROVE.getCode())
                        || informationTemplateVo.getJumpUrl().equals(JumpUrlEnum.PROCESS_VIEW.getCode()))) {

            Integer type = informationTemplateVo.getJumpUrl() == 1 ? 2 : 1;

            //email and in site notice
            emailUrl = processBusinessContans.getRoute(ProcessNoticeEnum.EMAIL_TYPE.getCode(),
                    ProcessInforVo
                            .builder()
                            .processinessKey(vo.getBpmnCode())
                            .businessNumber(vo.getProcessNumber())
                            .formCode(vo.getFormCode())
                            .type(type)
                            .build(), Optional.ofNullable(vo.getIsOutside()).orElse(false));

            //app route
            appUrl = processBusinessContans.getRoute(ProcessNoticeEnum.APP_TYPE.getCode(),
                    ProcessInforVo
                            .builder()
                            .processinessKey(vo.getBpmnCode())
                            .businessNumber(vo.getProcessNumber())
                            .formCode(vo.getFormCode())
                            .type(type)
                            .build(), Optional.ofNullable(vo.getIsOutside()).orElse(false));

        } else if (informationTemplateVo.getJumpUrl()!=null && informationTemplateVo.getJumpUrl().equals(JumpUrlEnum.PROCESS_BACKLOG.getCode())) {
            emailUrl = "/user/workflow/upcoming?page=1&pageSize=10";
            appUrl = "";
        }

        urlMap.put("emailUrl", emailUrl);
        urlMap.put("appUrl", appUrl);

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
        for (WildcardCharacterEnum wildcardCharacterEnum : WildcardCharacterEnum.values()) {
            if (StringUtils.isEmpty(wildcardCharacterEnum.getFilName())) {
                continue;
            }
            Object property = BeanUtil.pojo.getProperty(vo, wildcardCharacterEnum.getFilName());
            if (property!=null) {
                if (wildcardCharacterEnum.getIsSearchEmpl()) {
                    if (property instanceof List) {
                        List<String> propertys = (List<String>) property;
                        if (ObjectUtils.isEmpty(propertys)) {
                            continue;
                        }
                        List<String> emplNames = employeeService.qryLiteEmployeeInfoByIds(propertys)
                                .stream()
                                .map(Employee::getUsername).collect(Collectors.toList());
                        if (!ObjectUtils.isEmpty(emplNames)) {
                            wildcardCharacterMap.put(wildcardCharacterEnum.getCode(), StringUtils.join(emplNames, ","));
                        }
                    } else {
                       if(!property.toString().equals("0")){
                           Employee employee = employeeService.qryLiteEmployeeInfoById(property.toString());
                           if (employee!=null) {
                               wildcardCharacterMap.put(wildcardCharacterEnum.getCode(), employee.getUsername());
                           }
                       }
                    }
                } else {
                    wildcardCharacterMap.put(wildcardCharacterEnum.getCode(), property.toString());
                }
            }
        }
        return wildcardCharacterMap;
    }

    /**
     * get sender's info
     *
     * @param vo
     * @param bpmnTemplateVo
     * @return
     */
    private List<String> getSendToUsers(BpmVariableMessageVo vo, BpmnTemplateVo bpmnTemplateVo) {
        List<String> sendUsers = Lists.newArrayList();
        //specified assignees
        if (!ObjectUtils.isEmpty(bpmnTemplateVo.getEmpIdList())) {
            sendUsers.addAll(new ArrayList<>(bpmnTemplateVo.getEmpIdList()));
        }

        //specified roles
        if (!CollectionUtils.isEmpty(bpmnTemplateVo.getRoleIdList())) {
            List<User> users = roleService.queryUserByRoleIds(bpmnTemplateVo.getRoleIdList());
            if (!CollectionUtils.isEmpty(users)) {
                sendUsers.addAll(users.stream().map(u->u.getId().toString()).collect(Collectors.toList()));
            }
        }

        //todo functions
        //node sign up users
        if (!CollectionUtils.isEmpty(vo.getSignUpUsers())) {
            sendUsers.addAll(vo.getSignUpUsers());
        }

        //forwarded
        List<String> forwardUsers = null;
        List<BpmProcessForward> bpmProcessForwards = bpmProcessForwardService.list(new QueryWrapper<BpmProcessForward>()
                .eq("processInstance_Id", vo.getProcessInsId()));
        if (!CollectionUtils.isEmpty(vo.getForwardUsers()) && !CollectionUtils.isEmpty(bpmProcessForwards)) {
            forwardUsers = Lists.newArrayList();
            forwardUsers.addAll(vo.getForwardUsers());
            forwardUsers.addAll(bpmProcessForwards.stream().map(o -> String.valueOf(o.getForwardUserId())).distinct().collect(Collectors.toList()));
            forwardUsers = forwardUsers.stream().distinct().collect(Collectors.toList());
        } else if (CollectionUtils.isEmpty(vo.getForwardUsers()) && !CollectionUtils.isEmpty(bpmProcessForwards)) {
            forwardUsers = Lists.newArrayList();
            forwardUsers.addAll(bpmProcessForwards.stream().map(o -> String.valueOf(o.getForwardUserId())).distinct().collect(Collectors.toList()));
            forwardUsers = forwardUsers.stream().distinct().collect(Collectors.toList());
        } else if (!CollectionUtils.isEmpty(vo.getForwardUsers()) && CollectionUtils.isEmpty(bpmProcessForwards)) {
            forwardUsers = Lists.newArrayList();
            forwardUsers.addAll(vo.getForwardUsers());
            forwardUsers = forwardUsers.stream().distinct().collect(Collectors.toList());
        }
        vo.setForwardUsers(forwardUsers);

        //inform users
        if (!CollectionUtils.isEmpty(bpmnTemplateVo.getInformIdList())) {
            for (String informId : bpmnTemplateVo.getInformIdList()) {
                InformEnum informEnum = InformEnum.getEnumByByCode(Integer.parseInt(informId));
                //todo check whether the result is valid
                Object filObject = BeanUtil.pojo.getProperty(vo, informEnum.getFilName());
                if (filObject instanceof List) {
                    sendUsers.addAll((List) filObject);
                } else if (filObject!=null) {
                    sendUsers.add(filObject.toString());
                }
            }
        }
        return sendUsers;
    }

}
