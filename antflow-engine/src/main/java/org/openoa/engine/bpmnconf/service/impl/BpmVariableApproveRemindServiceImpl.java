package org.openoa.engine.bpmnconf.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import jodd.bean.BeanCopy;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmVariableApproveRemind;
import org.openoa.base.entity.Employee;
import org.openoa.base.service.AfUserService;
import org.openoa.base.util.DateUtil;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.confentity.BpmVariable;
import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.engine.bpmnconf.mapper.BpmVariableApproveRemindMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.util.InformationTemplateUtils;
import org.openoa.engine.bpmnconf.util.UserMsgUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.MessageSendTypeEnum.*;
import static org.openoa.base.constant.enums.WildcardCharacterEnum.*;

/**
 * @Classname BpmVariableApproveRemindServiceImpl
 * @Date 2021-11-27 15:49
 * @Created by AntOffice
 */
@Slf4j
@Service
public class BpmVariableApproveRemindServiceImpl extends ServiceImpl<BpmVariableApproveRemindMapper, BpmVariableApproveRemind> {


    @Autowired
    private BpmVariableApproveRemindMapper mapper;

    @Autowired
    private BpmVariableServiceImpl bpmVariableService;

    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Autowired
    private TaskService taskService;


    @Autowired
    private ProcessBusinessContans processBusinessContans;
    @Autowired
    private AfUserService employeeService;

    @Autowired
    private InformationTemplateUtils informationTemplateUtils;


    @Autowired
    private BpmnConfServiceImpl bpmnConfService;

    @Value("${system.domain:test}")
    private String systemDomain;

    /**
     * 执行流程超时提醒
     */
    public void doTimeoutReminder() {


        //get unfinished tasks and stored into a multimap
        Multimap<String, BpmnTimeoutReminderTaskVo> tasksMultimap = getBpmnTimeoutReminderTaskVoMultimap();


        //get timeout remind variables
        Map<String, BpmnTimeoutReminderVariableVo> bpmnTimeoutReminderVariableVoMap = getBpmnTimeoutReminderVariableVoMap(tasksMultimap);


        //che and send message
        checkAndSendMessage(tasksMultimap, bpmnTimeoutReminderVariableVoMap);

    }

    /**
     * check and send remind messages
     *
     * @param tasksMultimap
     * @param bpmnTimeoutReminderVariableVoMap
     */
    private void checkAndSendMessage(Multimap<String, BpmnTimeoutReminderTaskVo> tasksMultimap, Map<String, BpmnTimeoutReminderVariableVo> bpmnTimeoutReminderVariableVoMap) {

        //iterate the multimap to check the task is timeout or not,if timeout then send message
        for (String key : tasksMultimap.keySet()) {

            //get to be done task list
            List<BpmnTimeoutReminderTaskVo> bpmnTimeoutReminderTaskVos = (List<BpmnTimeoutReminderTaskVo>) tasksMultimap.get(key);


            //it the timeout reminder task list is empty then continue to loop
            if (ObjectUtils.isEmpty(bpmnTimeoutReminderTaskVos)) {
                continue;
            }


            //get timeout reminder variable
            BpmnTimeoutReminderVariableVo bpmnTimeoutReminderVariableVo = bpmnTimeoutReminderVariableVoMap.get(key);


            //if the timeout reminder variable is empty then continue to loop
            if (ObjectUtils.isEmpty(bpmnTimeoutReminderVariableVo)) {
                continue;
            }


            //get node approve remind list
            List<BpmVariableApproveRemind> bpmVariableApproveReminds = bpmnTimeoutReminderVariableVo.getBpmVariableApproveReminds();


            //if the node approve remind list is empty then continue to loop
            if (ObjectUtils.isEmpty(bpmVariableApproveReminds)) {
                continue;
            }


            //iterate the to be done list to compare the process node approval reminder configuration
            for (BpmnTimeoutReminderTaskVo bpmnTimeoutReminderTaskVo : bpmnTimeoutReminderTaskVos) {


                //filtering the element id to get node approve remind configuration
                BpmVariableApproveRemind bpmVariableApproveRemind = bpmVariableApproveReminds
                        .stream()
                        .filter(o -> bpmnTimeoutReminderTaskVo.getElementId().equals(o.getElementId()))
                        .findFirst()
                        .orElse(null);


                //if the node approve remind configuration is empty then continue to loop
                if (ObjectUtils.isEmpty(bpmVariableApproveRemind)) {
                    continue;
                }


                //if the node approve remind content is empty then continue to loop
                if (Strings.isNullOrEmpty(bpmVariableApproveRemind.getContent())) {
                    continue;
                }


                //convert message configuration information content from Json string to approve remind vo object
                BpmnApproveRemindVo bpmnApproveRemindVo = JSON.parseObject(bpmVariableApproveRemind.getContent(), BpmnApproveRemindVo.class);


                //if the day list is empty then continue to loop
                if (ObjectUtils.isEmpty(bpmnApproveRemindVo.getDayList())) {
                    continue;
                }


                //sorted the daylist
                List<Integer> confDays = bpmnApproveRemindVo.getDayList()
                        .stream()
                        .sorted()
                        .collect(Collectors.toList());


                //task's standby days
                Integer standbyDay = bpmnTimeoutReminderTaskVo.getStandbyDay();

                if (confDays.contains(standbyDay)) {
                    //do send message
                    doSendMessage(bpmnTimeoutReminderVariableVo, bpmnTimeoutReminderTaskVo, bpmnApproveRemindVo);
                }
            }
        }
    }

    /**
     * send remind message
     *
     * @param bpmnTimeoutReminderVariableVo
     * @param bpmnTimeoutReminderTaskVo
     * @param bpmnApproveRemindVo
     */
    private void doSendMessage(BpmnTimeoutReminderVariableVo bpmnTimeoutReminderVariableVo, BpmnTimeoutReminderTaskVo bpmnTimeoutReminderTaskVo, BpmnApproveRemindVo bpmnApproveRemindVo) {


        //set isOutside default to false
        boolean isOutside = false;


        //if bpmnCode is not null or empty then query config info and judge whether it is outside process,
        // if it is outside process then set isOutside boolean value to true
        if (!Strings.isNullOrEmpty(bpmnTimeoutReminderVariableVo.getBpmnCode())) {

            BpmnConf bpmnConf = bpmnConfService.getBaseMapper().selectOne(new QueryWrapper<BpmnConf>()
                    .eq("bpmn_code", bpmnTimeoutReminderVariableVo.getBpmnCode()));


            if (!ObjectUtils.isEmpty(bpmnConf) && bpmnConf.getIsOutSideProcess() == 1) {
                isOutside = true;
            }
        }

        //email url
        String emailUrl = "";//todo

        //app route url
        String appUrl = "";//todo


        //employs to receive notice message
        String emplId = bpmnTimeoutReminderTaskVo.getAssignee();

        //todo this module should be redesigned
        Employee employee = employeeService.getEmployeeDetailById(emplId);


        //format message content
        InformationTemplateVo informationTemplateVo = getInformationTemplateVo(bpmnTimeoutReminderVariableVo, bpmnApproveRemindVo, employee);

        //send email
        sendMail(bpmnTimeoutReminderTaskVo, emailUrl, appUrl, emplId, employee, informationTemplateVo);

        //send message and app push
        sendMessageAndPush(bpmnTimeoutReminderTaskVo, emailUrl, appUrl, emplId, employee, informationTemplateVo);

        //set inside message
        insertUserMessage(bpmnTimeoutReminderTaskVo, emailUrl, appUrl, emplId, employee, informationTemplateVo);
    }

    /**
     * send insite message
     *
     * @param bpmnTimeoutReminderTaskVo
     * @param emailUrl
     * @param appUrl
     * @param emplId
     * @param employee
     * @param informationTemplateVo
     */
    private void insertUserMessage(BpmnTimeoutReminderTaskVo bpmnTimeoutReminderTaskVo, String emailUrl, String appUrl, String emplId,
                                   Employee employee, InformationTemplateVo informationTemplateVo) {
        UserMsgVo userMsgVo = getUserMsgVo(bpmnTimeoutReminderTaskVo, emailUrl, appUrl, emplId, employee,
                informationTemplateVo.getSystemTitle(), informationTemplateVo.getSystemContent());
        UserMsgUtils.insertUserMessage(userMsgVo);
    }

    /**
     * send sms and app push
     *
     * @param bpmnTimeoutReminderTaskVo
     * @param emailUrl
     * @param appUrl
     * @param emplId
     * @param employee
     * @param informationTemplateVo
     */
    private void sendMessageAndPush(BpmnTimeoutReminderTaskVo bpmnTimeoutReminderTaskVo, String emailUrl, String appUrl, String emplId,
                                    Employee employee, InformationTemplateVo informationTemplateVo) {
        UserMsgVo userMsgVo = getUserMsgVo(bpmnTimeoutReminderTaskVo, emailUrl, appUrl, emplId, employee,
                StringUtils.EMPTY, informationTemplateVo.getNoteContent());
        UserMsgUtils.sendMessagesNoUserMessage(userMsgVo, MESSAGE, PUSH);
    }

    /**
     * send email
     *
     * @param bpmnTimeoutReminderTaskVo
     * @param emailUrl
     * @param appUrl
     * @param emplId
     * @param employee
     * @param informationTemplateVo
     */
    private void sendMail(BpmnTimeoutReminderTaskVo bpmnTimeoutReminderTaskVo, String emailUrl, String appUrl, String emplId,
                          Employee employee, InformationTemplateVo informationTemplateVo) {
        UserMsgVo userMsgVo = getUserMsgVo(bpmnTimeoutReminderTaskVo, emailUrl, appUrl, emplId, employee,
                informationTemplateVo.getMailTitle(), informationTemplateVo.getMailContent());
        UserMsgUtils.sendMessagesNoUserMessage(userMsgVo, MAIL);
    }

    /**
     * get user's msg
     *
     * @param bpmnTimeoutReminderTaskVo
     * @param emailUrl
     * @param appUrl
     * @param emplId
     * @param employee
     * @param title
     * @param content
     * @return
     */
    private UserMsgVo getUserMsgVo(BpmnTimeoutReminderTaskVo bpmnTimeoutReminderTaskVo, String emailUrl, String appUrl, String emplId,
                                   Employee employee, String title, String content) {
        return UserMsgVo
                .builder()
                .userId(emplId)
                .email(employee.getEmail())
                .mobile(employee.getMobile())
                .title(title)
                .content(content)
                .emailUrl(emailUrl)
                .url(emailUrl)
                .appPushUrl(appUrl)
                .taskId(bpmnTimeoutReminderTaskVo.getTaskId())
                .ssoSessionDomain(systemDomain)
                .build();
    }

    /**
     * format message content
     *
     * @param bpmnTimeoutReminderVariableVo
     * @param bpmnApproveRemindVo
     * @param employee
     * @return
     */
    private InformationTemplateVo getInformationTemplateVo(BpmnTimeoutReminderVariableVo bpmnTimeoutReminderVariableVo,
                                                           BpmnApproveRemindVo bpmnApproveRemindVo, Employee employee) {
        Map<Integer, String> wildcardCharacterMap = Maps.newHashMap();
        wildcardCharacterMap.put(ONE_CHARACTER.getCode(), bpmnTimeoutReminderVariableVo.getProcessName());//process name
        wildcardCharacterMap.put(TWO_CHARACTER.getCode(), bpmnTimeoutReminderVariableVo.getProcessNum());//process number
        wildcardCharacterMap.put(THREE_CHARACTER.getCode(), bpmnTimeoutReminderVariableVo.getStartUser());//start user
        wildcardCharacterMap.put(FOUR_CHARACTER.getCode(), bpmnTimeoutReminderVariableVo.getApprovalEmpl());//
        wildcardCharacterMap.put(FIVE_CHARACTER.getCode(), bpmnTimeoutReminderVariableVo.getApplyDate());//apply date
        wildcardCharacterMap.put(SIX_CHARACTER.getCode(), bpmnTimeoutReminderVariableVo.getApplyTime());//apply time
        wildcardCharacterMap.put(EIGHT_CHARACTER.getCode(), employee.getUsername());//current approver
        return informationTemplateUtils.translateInformationTemplate(InformationTemplateVo
                .builder()
                .id(bpmnApproveRemindVo.getTemplateId())
                .wildcardCharacterMap(wildcardCharacterMap)
                .build());
    }


    /**
     * query remind variable to map
     *
     * @param tasksMultimap
     * @return
     */
    private Map<String, BpmnTimeoutReminderVariableVo> getBpmnTimeoutReminderVariableVoMap(Multimap<String, BpmnTimeoutReminderTaskVo> tasksMultimap) {


        //get bpm and business process connect info by entry id
        List<BpmBusinessProcess> bpmBusinessProcesses = bpmBusinessProcessService.getBaseMapper().selectList(new QueryWrapper<BpmBusinessProcess>()
                .in("PROC_INST_ID_",tasksMultimap.keys()));


        //get bpm variable by process number
        List<BpmVariable> bpmVariables = bpmVariableService.getBaseMapper().selectList(new QueryWrapper<BpmVariable>()
                .in("process_num", bpmBusinessProcesses
                        .stream()
                        .map(BpmBusinessProcess::getBusinessNumber)
                        .collect(Collectors.toList())));


        //get node approval remind info
        List<BpmVariableApproveRemind> bpmVariableApproveReminds = this.mapper.selectList(new QueryWrapper<BpmVariableApproveRemind>()
                .in("variable_id", bpmVariables
                        .stream()
                        .map(BpmVariable::getId)
                        .collect(Collectors.toList())));


        // iterate historic process instance list and bpm business process list,then connect them
        Map<String, BpmBusinessProcess> processMap = Maps.newHashMap();
        for (String procinstId : tasksMultimap.keys()) {
            BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcesses
                    .stream()
                    .filter(bbp -> procinstId.equals(bbp.getEntryId()))
                    .findFirst()
                    .orElse(null);
            processMap.put(procinstId, bpmBusinessProcess);
        }


        Map<String, BpmnTimeoutReminderVariableVo> bpmnTimeoutReminderVariableVoMap = Maps.newHashMap();
        for (String key : processMap.keySet()) {
            BpmBusinessProcess val = processMap.get(key);


            //if value is null then continue
            if (ObjectUtils.isEmpty(val)) {
                continue;
            }


            //get variable that match current business number
            BpmVariable bpmVariable = bpmVariables
                    .stream()
                    .filter(o -> val.getBusinessNumber().equals(o.getProcessNum()))
                    .findFirst()
                    .orElse(null);


            if (!ObjectUtils.isEmpty(bpmVariable)) {
                BpmnTimeoutReminderVariableVo bpmnTimeoutReminderVariableVo = new BpmnTimeoutReminderVariableVo();
                BeanCopy.from(bpmVariable).to(bpmnTimeoutReminderVariableVo).copy();
                bpmnTimeoutReminderVariableVo.setProcessinessKey(val.getProcessinessKey());
                bpmnTimeoutReminderVariableVo.setBusinessId(val.getBusinessId());
                bpmnTimeoutReminderVariableVo.setEntryId(val.getEntryId());
                bpmnTimeoutReminderVariableVo.setBpmVariableApproveReminds(bpmVariableApproveReminds
                        .stream()
                        .filter(o -> bpmVariable.getId().equals(o.getVariableId()))
                        .collect(Collectors.toList()));

                //补全bpmnTimeoutReminderVariableVo参数信息
                bpmnTimeoutReminderVariableVo.setBpmnName(bpmVariable.getProcessName());
                bpmnTimeoutReminderVariableVo.setProcessNumber(bpmVariable.getProcessNum());





                //set applicant,applydate,apply time

                    Employee employee = employeeService.getEmployeeDetailById(val.getCreateUser());
                    bpmnTimeoutReminderVariableVo.setStartUser(employee.getUsername());
                    bpmnTimeoutReminderVariableVo.setApplyDate(DateUtil.SDF_DATE_PATTERN.format(val.getCreateTime()));
                    bpmnTimeoutReminderVariableVo.setApplyTime(DateUtil.SDF_DATETIME_PATTERN.format(val.getCreateTime()));



                if (!ObjectUtils.isEmpty(bpmVariable.getProcessStartConditions())) {
                    BpmnStartConditionsVo bpmnStartConditionsVo = JSON.parseObject(bpmVariable.getProcessStartConditions(), BpmnStartConditionsVo.class);
                    if (!ObjectUtils.isEmpty(bpmnStartConditionsVo) && !ObjectUtils.isEmpty(bpmnStartConditionsVo.getApprovalEmplId())) {
                        employee = employeeService.getEmployeeDetailById(bpmnStartConditionsVo.getApprovalEmplId());
                        bpmnTimeoutReminderVariableVo.setApprovalEmpl(employee.getUsername());
                    }
                }


                bpmnTimeoutReminderVariableVoMap.put(key, bpmnTimeoutReminderVariableVo);
            }
        }
        return bpmnTimeoutReminderVariableVoMap;
    }

    /**
     * not processed yet tasks Multimap
     *
     * @return
     */
    private Multimap<String, BpmnTimeoutReminderTaskVo> getBpmnTimeoutReminderTaskVoMultimap() {

        //declare a multimap to store not processed yet tasks.procinstid as key and remindvo as value
        Multimap<String, BpmnTimeoutReminderTaskVo> bpmnTimeoutReminderTaskVoMultimap = ArrayListMultimap.create();

        //query process engine to get a list of not processed yet tasks
        List<Task> tasks = taskService.createTaskQuery().list();


        for (Task task : tasks) {

            //if a assignee is empty then skip
            if (ObjectUtils.isEmpty(task.getAssignee())) {
                continue;
            }

            //calculate days
            Integer standbyDay = Optional.ofNullable(DateUtil.dateDiff(task.getCreateTime(), new Date(), 1))
                    .orElse(0L).intValue();


            if (standbyDay <= 0) {
                continue;
            }

            //put values to map
            bpmnTimeoutReminderTaskVoMultimap.put(task.getProcessInstanceId(), BpmnTimeoutReminderTaskVo
                    .builder()
                    .procInstId(task.getProcessInstanceId())
                    .taskId(task.getId())
                    .elementId(task.getTaskDefinitionKey())
                    .assignee(task.getAssignee())
                    .createTime(task.getCreateTime())
                    .standbyDay(standbyDay)
                    .build());
        }
        return bpmnTimeoutReminderTaskVoMultimap;
    }

}