package org.openoa.engine.bpmnconf.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import jodd.bean.BeanUtil;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.openoa.base.constant.enums.MessageSendTypeEnum;
import org.openoa.base.constant.enums.MsgNoticeTypeEnum;
import org.openoa.base.constant.enums.NoticeReplaceEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.Employee;
import org.openoa.base.vo.ActivitiBpmMsgVo;
import org.openoa.base.vo.BpmProcessNodeOvertimeVo;
import org.openoa.base.vo.UserMsgBathVo;
import org.openoa.base.vo.UserMsgVo;
import org.openoa.engine.bpmnconf.confentity.BpmProcessNotice;
import org.openoa.engine.bpmnconf.confentity.BpmnConfNoticeTemplateDetail;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.util.UserMsgUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ActivitiBpmMsgTemplateServiceImpl {
    @Autowired
    private EmployeeServiceImpl employeeService;
    @Autowired
    private BpmProcessNoticeServiceImpl bpmProcessNoticeService;
    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private BpmProcessNodeOvertimeServiceImpl processNodeOvertimeService;
    @Autowired
    private BpmnConfNoticeTemplateServiceImpl bpmnConfNoticeTemplateService;
    @Autowired
    private BpmnConfNoticeTemplateDetailServiceImpl bpmnConfNoticeTemplateDetailService;


    @Value("${system.domain:test}")
    private String systemDomain;


    private static final String baseTitle = "工作流通知";

    private static final String baseSpace = "。   ";

    private static final String DATETIME_PATTERN = "yyyy/MM/dd HH:mm:ss";
    private static final FastDateFormat SDF_DATETIME_PATTERN = FastDateFormat.getInstance(DATETIME_PATTERN);

    /**
     * send a custom message
     *
     * @param activitiBpmMsgVo
     * @param content
     */
    public void sendBpmCustomMsg(ActivitiBpmMsgVo activitiBpmMsgVo, String content) {

        doUserMsgSend(activitiBpmMsgVo, content);
    }

    /**
     * send process flow message
     *
     * @param activitiBpmMsgVo
     */
    @Async
    public void sendBpmApprovalMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {

        if (activitiBpmMsgVo==null) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("工作流流转通知，流程编号{}，入参{}", activitiBpmMsgVo.getProcessId(), JSON.toJSON(activitiBpmMsgVo));
        }
        String content = getContent(activitiBpmMsgVo, MsgNoticeTypeEnum.PROCESS_FLOW.getCode());
        doUserMsgSend(activitiBpmMsgVo, content);
    }
    /**
     *  send process flow message in batch
     *
     * @param activitiBpmMsgVos
     */
    @Async
    public void sendBpmApprovalMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {

        if (activitiBpmMsgVos==null) {
            return;
        }

        UserMsgUtils.sendMessageBath(activitiBpmMsgVos
                .stream()
                .filter(Objects::nonNull)
                .map(o -> {
                    if (log.isDebugEnabled()) {
                        log.debug("工作流流转通知，流程编号{}，入参{}", o.getProcessId(), JSON.toJSON(o));
                    }
                    String content = getContent(o, MsgNoticeTypeEnum.PROCESS_FLOW.getCode());
                    return buildUserMsgBathVo(o, content);
                })
                .collect(Collectors.toList()));
    }

    /**
     * process forward message
     *
     * @param activitiBpmMsgVo
     */
    @Async
    public void sendBpmForwardedlMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        if (activitiBpmMsgVo==null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("收到转发工作流通知，流程编号{}，入参{}", activitiBpmMsgVo.getProcessId(), JSON.toJSON(activitiBpmMsgVo));
        }


        String content = getContent(activitiBpmMsgVo, MsgNoticeTypeEnum.RECEIVE_FLOW_PROCESS.getCode());

        doUserMsgSend(activitiBpmMsgVo, content);
    }

    /**
     * process forward message in batch
     *
     * @param activitiBpmMsgVos
     */
    @Async
    public void sendBpmForwardedlMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {

        if (CollectionUtils.isEmpty(activitiBpmMsgVos)) {
            return;
        }

        UserMsgUtils.sendMessageBath(activitiBpmMsgVos
                .stream()
                .filter(Objects::nonNull)
                .map(o -> {
                    if (log.isDebugEnabled()) {
                        log.debug("收到转发工作流通知，流程编号{}，入参{}", o.getProcessId(), JSON.toJSON(o));
                    }
                    String content = getContent(o, MsgNoticeTypeEnum.RECEIVE_FLOW_PROCESS.getCode());
                    return buildUserMsgBathVo(o, content);
                })
                .collect(Collectors.toList()));
    }

    /**
     * process finish message
     *
     * @param activitiBpmMsgVo
     */
    @Async
    public void sendBpmFinishMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {

        if (activitiBpmMsgVo==null) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("工作流完成通知，流程编号{}，入参{}", activitiBpmMsgVo.getProcessId(), JSON.toJSON(activitiBpmMsgVo));
        }


        String content = getContent(activitiBpmMsgVo, MsgNoticeTypeEnum.PROCESS_FINISH.getCode());

        doUserMsgSend(activitiBpmMsgVo, content);
    }

    /**
     * process finish message in batch
     *
     * @param activitiBpmMsgVos
     */
    @Async
    public void sendBpmFinishMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {

        if (CollectionUtils.isEmpty(activitiBpmMsgVos)) {
            return;
        }

        UserMsgUtils.sendMessageBath(activitiBpmMsgVos
                .stream()
                .filter(Objects::nonNull)
                .map(o -> {

                    if (log.isDebugEnabled()) {
                        log.debug("工作流完成通知，流程编号{}，入参{}", o.getProcessId(), JSON.toJSON(o));
                    }

                    String content = getContent(o, MsgNoticeTypeEnum.PROCESS_FINISH.getCode());
                    return buildUserMsgBathVo(o, content);
                })
                .collect(Collectors.toList()));
    }
    /**
     * process reject notice
     *
     * @param activitiBpmMsgVo
     */
    @Async
    public void sendBpmRejectMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {

        if (activitiBpmMsgVo==null) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("工作流流程审批不通过通知，流程编号{}，入参{}", activitiBpmMsgVo.getProcessId(), JSON.toJSON(activitiBpmMsgVo));
        }


        String content = getContent(activitiBpmMsgVo, MsgNoticeTypeEnum.PROCESS_REJECT.getCode());

        doUserMsgSend(activitiBpmMsgVo, content);
    }

    /**
     * process reject notice in batch
     *
     * @param activitiBpmMsgVos
     */
    @Async
    public void sendBpmRejectMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {

        if (CollectionUtils.isEmpty(activitiBpmMsgVos)) {
            return;
        }

        UserMsgUtils.sendMessageBath(activitiBpmMsgVos
                .stream()
                .filter(Objects::nonNull)
                .map(o -> {

                    if (log.isDebugEnabled()) {
                        log.debug("工作流流程审批不通过通知，流程编号{}，入参{}", o.getProcessId(), JSON.toJSON(o));
                    }

                    String content = getContent(o, MsgNoticeTypeEnum.PROCESS_REJECT.getCode());
                    return buildUserMsgBathVo(o, content);
                })
                .collect(Collectors.toList()));
    }
    /**
     * process overtime notice
     *
     * @param activitiBpmMsgVo
     */
    @Async
    public void sendBpmOverTimeMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {

        if (activitiBpmMsgVo==null) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("工作流超时通知，流程编号{}，入参{}", activitiBpmMsgVo.getProcessId(), JSON.toJSON(activitiBpmMsgVo));
        }


        String content = getContent(activitiBpmMsgVo, MsgNoticeTypeEnum.PROCESS_TIME_OUT.getCode());

        doUserMsgSend(activitiBpmMsgVo, content, 2);
    }

    /**
     * process overtime notice in batch
     *
     * @param activitiBpmMsgVos
     */
    @Async
    public void sendBpmOverTimeMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {

        if (CollectionUtils.isEmpty(activitiBpmMsgVos)) {
            return;
        }

        UserMsgUtils.sendMessageBath(activitiBpmMsgVos
                .stream()
                .filter(Objects::nonNull)
                .map(o -> {

                    if (log.isDebugEnabled()) {
                        log.debug("工作流超时通知，流程编号{}，入参{}", o.getProcessId(), JSON.toJSON(o));
                    }

                    String content = getContent(o, MsgNoticeTypeEnum.PROCESS_TIME_OUT.getCode());
                    return buildUserMsgBathVo(o, content, 2);
                })
                .collect(Collectors.toList()));
    }
    /**
     * process terminate notice
     *
     * @param activitiBpmMsgVo
     */
    @Async
    public void sendBpmTerminationMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {

        if (activitiBpmMsgVo==null) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("工作流被终止通知，流程编号{}，入参{}", activitiBpmMsgVo.getProcessId(), JSON.toJSON(activitiBpmMsgVo));
        }

        //生成消息内容
        String content = getContent(activitiBpmMsgVo, MsgNoticeTypeEnum.PROCESS_STOP.getCode());
        //执行用户消息发送
        doUserMsgSend(activitiBpmMsgVo, content);
    }

    /**
     * process terminate notice in batch
     *
     * @param activitiBpmMsgVos
     */
    @Async
    public void sendBpmTerminationMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {

        if (CollectionUtils.isEmpty(activitiBpmMsgVos)) {
            return;
        }

        UserMsgUtils.sendMessageBath(activitiBpmMsgVos
                .stream()
                .filter(Objects::nonNull)
                .map(o -> {

                    if (log.isDebugEnabled()) {
                        log.debug("工作流被终止通知，流程编号{}，入参{}", o.getProcessId(), JSON.toJSON(o));
                    }

                    String content = getContent(o, MsgNoticeTypeEnum.PROCESS_STOP.getCode());
                    return buildUserMsgBathVo(o, content);
                })
                .collect(Collectors.toList()));
    }
    /**
     * process entrust notice
     *
     * @param activitiBpmMsgVo
     */
    @Async
    public void sendBpmGenerationApprovalMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {

        if (activitiBpmMsgVo==null) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("工作流代审批通知，流程编号{}，入参{}", activitiBpmMsgVo.getProcessId(), JSON.toJSON(activitiBpmMsgVo));
        }


        String content = getContent(activitiBpmMsgVo, MsgNoticeTypeEnum.PROCESS_WAIR_VERIFY.getCode());

        doUserMsgSend(activitiBpmMsgVo, content);
    }

    /**
     * process entrust notice in batch
     *
     * @param activitiBpmMsgVos
     */
    @Async
    public void sendBpmGenerationApprovalMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {

        if (CollectionUtils.isEmpty(activitiBpmMsgVos)) {
            return;
        }

        UserMsgUtils.sendMessageBath(activitiBpmMsgVos
                .stream()
                .filter(Objects::nonNull)
                .map(o -> {

                    if (log.isDebugEnabled()) {
                        log.debug("工作流代审批通知，流程编号{}，入参{}", o.getProcessId(), JSON.toJSON(o));
                    }

                    String content = getContent(o, MsgNoticeTypeEnum.PROCESS_WAIR_VERIFY.getCode());
                    return buildUserMsgBathVo(o, content);
                })
                .collect(Collectors.toList()));
    }
    /**
     *
     * change assignee notice to original assignee
     *
     * @param activitiBpmMsgVo
     */
    @Async
    public void sendBpmChangePersonOrgiMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {

        if (activitiBpmMsgVo==null) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("工作流变更处理人通知(原审批节点处理人)，流程编号{}，入参{}", activitiBpmMsgVo.getProcessId(), JSON.toJSON(activitiBpmMsgVo));
        }


        String content = getContent(activitiBpmMsgVo, MsgNoticeTypeEnum.PROCESS_CHANGE_ORIAL_TREATOR.getCode());

        doUserMsgSend(activitiBpmMsgVo, content);
    }

    /**
     * change assignee notice to new assignee
     *
     * @param activitiBpmMsgVo
     */
    @Async
    public void sendBpmChangePersonNewMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {

        if (activitiBpmMsgVo==null) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("工作流变更处理人通知(现审批节点处理人)，流程编号{}，入参{}", activitiBpmMsgVo.getProcessId(), JSON.toJSON(activitiBpmMsgVo));
        }


        String content = getContent(activitiBpmMsgVo, MsgNoticeTypeEnum.PROCESS_CHANGE_NOW_TREATOR.getCode());

        doUserMsgSend(activitiBpmMsgVo, content);
    }
    /**
     * build batch user messages vo
     *
     * @param o
     * @param content
     * @return
     */
    private UserMsgBathVo buildUserMsgBathVo(ActivitiBpmMsgVo o, String content) {
        content = StringUtils.join(content, baseSpace, SDF_DATETIME_PATTERN.format(new Date()));
        return UserMsgBathVo
                .builder()
                .userMsgVo(bulidUserMsgVo(o, content))
                .messageSendTypeEnums(Lists.newArrayList(getMessageSendTypeEnums(o.getProcessId(), o.getFormCode(), 1)))
                .build();
    }

    /**
     * build user message vo
     *
     * @param activitiBpmMsgVo
     * @param content
     * @return
     */
    private UserMsgVo bulidUserMsgVo(ActivitiBpmMsgVo activitiBpmMsgVo, String content) {
        Employee employee = getEmployee(activitiBpmMsgVo.getUserId(), activitiBpmMsgVo);
        return UserMsgVo
                .builder()
                .userId(activitiBpmMsgVo.getUserId())
                .email(employee.getEmail())
                .mobile(employee.getMobile())
                .title(baseTitle)
                .content(content)
                .emailUrl(activitiBpmMsgVo.getEmailUrl())
                .url(activitiBpmMsgVo.getUrl())
                .appPushUrl(activitiBpmMsgVo.getAppPushUrl())
                .taskId(activitiBpmMsgVo.getTaskId())
                .ssoSessionDomain(systemDomain)
                .build();
    }
    private UserMsgBathVo buildUserMsgBathVo(ActivitiBpmMsgVo o, String content, Integer selectMack) {
        content = StringUtils.join(content, baseSpace, SDF_DATETIME_PATTERN.format(new Date()));
        return UserMsgBathVo
                .builder()
                .userMsgVo(bulidUserMsgVo(o, content))
                .messageSendTypeEnums(Lists.newArrayList(getMessageSendTypeEnums(o.getProcessId(), o.getFormCode(), selectMack)))
                .build();
    }
    private void doUserMsgSend(ActivitiBpmMsgVo activitiBpmMsgVo, String content) {
        content = StringUtils.join(content, baseSpace, SDF_DATETIME_PATTERN.format(new Date()));
        UserMsgVo userMsgVo = bulidUserMsgVo(activitiBpmMsgVo, content);
        MessageSendTypeEnum[] messageSendTypeEnums = getMessageSendTypeEnums(activitiBpmMsgVo.getProcessId(), activitiBpmMsgVo.getFormCode(), 1);
        UserMsgUtils.sendMessages(userMsgVo, messageSendTypeEnums);
    }
    private void doUserMsgSend(ActivitiBpmMsgVo activitiBpmMsgVo, String content, Integer selectMack) {
        content = StringUtils.join(content, baseSpace, SDF_DATETIME_PATTERN.format(new Date()));
        UserMsgVo userMsgVo = bulidUserMsgVo(activitiBpmMsgVo, content);
        MessageSendTypeEnum[] messageSendTypeEnums = getMessageSendTypeEnums(activitiBpmMsgVo.getProcessId(), activitiBpmMsgVo.getFormCode(), selectMack);
        UserMsgUtils.sendMessages(userMsgVo, messageSendTypeEnums);
    }

    private Employee getEmployee(String employeeId, ActivitiBpmMsgVo activitiBpmMsgVo) {
        Employee employee = employeeService.getEmployeeDetailById(employeeId);
        if (employee==null) {
            employee = new Employee();
            log.error("流程消息查询员工信息失败，消息入参{}", JSON.toJSON(activitiBpmMsgVo));
        }
        return employee;
    }
    private MessageSendTypeEnum[] getMessageSendTypeEnums(String processId, String formCode, Integer selectMack) {
        if (selectMack == 1) {
            List<BpmProcessNotice> bpmProcessNotices = bpmProcessNoticeService.processNoticeList(formCode);
            if (!CollectionUtils.isEmpty(bpmProcessNotices)) {
                return bpmProcessNotices
                        .stream()
                        .map(o -> MessageSendTypeEnum.getEnumByCode(o.getType())).toArray(MessageSendTypeEnum[]::new);
            }
        } else if (selectMack == 2) {
            BpmBusinessProcess bpmBusinessProcess = Optional.ofNullable(bpmBusinessProcessService.getBpmBusinessProcess(processId)).orElse(new BpmBusinessProcess());
            List<BpmProcessNodeOvertimeVo> bpmProcessNodeOvertimeVos = processNodeOvertimeService.selectNoticeNodeName(Optional.ofNullable(bpmBusinessProcess.getProcessinessKey()).orElse(processId));
            if (!CollectionUtils.isEmpty(bpmProcessNodeOvertimeVos)) {
                return bpmProcessNodeOvertimeVos
                        .stream()
                        .map(o -> MessageSendTypeEnum.getEnumByCode(o.getNoticeType())).toArray(MessageSendTypeEnum[]::new);
            }
        }
        return new MessageSendTypeEnum[0];
    }
    public String getContent(ActivitiBpmMsgVo activitiBpmMsgVo, Integer msgNoticeType) {
        log.info("content数据转换,activitiBpmMsgVo:{},msgNoticeType:{}", JSON.toJSONString(activitiBpmMsgVo), msgNoticeType);
        BpmnConfNoticeTemplateDetail bpmnConfNoticeTemplateDetail = null;
        if (!StringUtils.isBlank(activitiBpmMsgVo.getBpmnCode())) {
            bpmnConfNoticeTemplateDetail = bpmnConfNoticeTemplateService.getDetailByCodeAndType(activitiBpmMsgVo.getBpmnCode(), msgNoticeType);
        }
        String content = null;
        if (bpmnConfNoticeTemplateDetail == null) {
            log.info("模板内容为空,bpmnCode:{}", activitiBpmMsgVo.getBpmnCode());
            content = MsgNoticeTypeEnum.getDefaultValueByCode(msgNoticeType);
        } else {
            content = bpmnConfNoticeTemplateDetail.getNoticeTemplateDetail();
        }
        String result = replceTemplateDetail(activitiBpmMsgVo, content);
        log.info("转换后数据content:{}", result);
        return result;
    }
    /**
     * replace template
     *
     * @param activitiBpmMsgVo
     * @param content
     * @return
     */
    public String replceTemplateDetail(ActivitiBpmMsgVo activitiBpmMsgVo, String content) {
        List<NoticeReplaceEnum> noticeReplaceEnums = Lists.newArrayList();
        for (NoticeReplaceEnum noticeReplaceEnum : NoticeReplaceEnum.values()) {
            if (content.contains("{" + noticeReplaceEnum.getDesc() + "}")) {
                noticeReplaceEnums.add(noticeReplaceEnum);
            }
        }
        if (!CollectionUtils.isEmpty(noticeReplaceEnums)) {
            Employee employee = null;
            if (noticeReplaceEnums.stream().anyMatch(NoticeReplaceEnum::getIsSelectEmpl)) {
                employee = employeeService.getEmployeeDetailById(activitiBpmMsgVo.getOtherUserId());
            }
            for (NoticeReplaceEnum noticeReplaceEnum : noticeReplaceEnums) {
                if (noticeReplaceEnum.getIsSelectEmpl()) {
                    String name = Optional.ofNullable(employee).orElse(new Employee()).getUsername();
                    content = content.replace("{" + noticeReplaceEnum.getDesc() + "}", name);
                } else {
                    String property = Optional.ofNullable(BeanUtil.pojo.getProperty(activitiBpmMsgVo, noticeReplaceEnum.getFilName()))
                            .orElse("").toString();
                    content = content.replace("{" + noticeReplaceEnum.getDesc() + "}", property);
                }
            }
        }
        return content;
    }
}
