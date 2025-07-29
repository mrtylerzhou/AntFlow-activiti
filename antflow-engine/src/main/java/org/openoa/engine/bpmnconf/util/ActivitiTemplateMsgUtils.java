package org.openoa.engine.bpmnconf.util;



import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.ActivitiBpmMsgVo;
import org.openoa.engine.bpmnconf.service.impl.ActivitiBpmMsgTemplateServiceImpl;

import java.util.List;

public class ActivitiTemplateMsgUtils {

    private static ActivitiBpmMsgTemplateServiceImpl getMessageSendBean(){
       return SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
    }
    /**
     * send custom message
     *
     * @param activitiBpmMsgVo
     * @param content
     */
    public static void sendCustomMsg(ActivitiBpmMsgVo activitiBpmMsgVo, String content) {

        getMessageSendBean().sendBpmCustomMsg(activitiBpmMsgVo, content);
    }

    /**
     * process flow message
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmApprovalMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        getMessageSendBean().sendBpmApprovalMsg(activitiBpmMsgVo);
    }

    /**
     * process flow message in batch
     *
     * @param activitiBpmMsgVos
     */
    public static void sendBpmApprovalMsgBatch(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        getMessageSendBean().sendBpmApprovalMsgBatch(activitiBpmMsgVos);
    }

    /**
     * process forward notice
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmForwardedMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        getMessageSendBean().sendBpmForwardedlMsg(activitiBpmMsgVo);
    }

    /**
     * process forward notice in batch
     *
     * @param activitiBpmMsgVos
     */
    public static void sendBpmForwardedMsgBatch(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        getMessageSendBean().sendBpmForwardedMsgBatch(activitiBpmMsgVos);
    }

    /**
     * process finish notice
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmFinishMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        getMessageSendBean().sendBpmFinishMsg(activitiBpmMsgVo);
    }

    /**
     * process finish notice in batch
     *
     * @param activitiBpmMsgVos
     */
    public static void sendBpmFinishMsgBatch(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        getMessageSendBean().sendBpmFinishMsgBatch(activitiBpmMsgVos);
    }

    /**
     * process reject notice
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmRejectMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
       getMessageSendBean().sendBpmRejectMsg(activitiBpmMsgVo);
    }

    /**
     * process reject notice in batch
     *
     * @param activitiBpmMsgVos
     */
    public void sendBpmRejectMsgBatch(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
       getMessageSendBean().sendBpmRejectMsgBatch(activitiBpmMsgVos);
    }

    /**
     * process overtime notice
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmOverTimeMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        getMessageSendBean().sendBpmOverTimeMsg(activitiBpmMsgVo);
    }

    /**
     * process over time notice in batch
     *
     * @param activitiBpmMsgVos
     */
    public static void sendBpmOverTimeMsgBatch(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
       getMessageSendBean().sendBpmOverTimeMsgBatch(activitiBpmMsgVos);
    }

    /**
     * process terminate notice
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmTerminationMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        getMessageSendBean().sendBpmTerminationMsg(activitiBpmMsgVo);
    }

    /**
     * process terminate notice in batch
     *
     * @param activitiBpmMsgVos
     */
    public static void sendBpmTerminationMsgBatch(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
       getMessageSendBean().sendBpmTerminationMsgBatch(activitiBpmMsgVos);
    }

    /**
     * process forward notice
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmGenerationApprovalMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
       getMessageSendBean().sendBpmGenerationApprovalMsg(activitiBpmMsgVo);
    }

    /**
     *  process forward notice in batch
     *
     * @param activitiBpmMsgVos
     */
    public static void sendBpmGenerationApprovalMsgBatch(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        getMessageSendBean().sendBpmGenerationApprovalMsgBatch(activitiBpmMsgVos);
    }

    /**
     * process change assignee notice
     *
     * @param activitiBpmMsgVoOrgi
     * @param activitiBpmMsgVoNew
     */
    public static void sendBpmChangePerson(ActivitiBpmMsgVo activitiBpmMsgVoOrgi, ActivitiBpmMsgVo activitiBpmMsgVoNew) {
        ActivitiBpmMsgTemplateServiceImpl bean = getMessageSendBean();
        bean.sendBpmChangePersonOrgiMsg(activitiBpmMsgVoOrgi);
        bean.sendBpmChangePersonNewMsg(activitiBpmMsgVoNew);
    }



}
