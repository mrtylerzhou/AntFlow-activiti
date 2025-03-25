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
    public static void sendBpmApprovalMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        getMessageSendBean().sendBpmApprovalMsgBath(activitiBpmMsgVos);
    }

    /**
     * process forward notice
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmForwardedlMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        getMessageSendBean().sendBpmForwardedlMsg(activitiBpmMsgVo);
    }

    /**
     * process forward notice in batch
     *
     * @param activitiBpmMsgVos
     */
    public static void sendBpmForwardedlMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        getMessageSendBean().sendBpmForwardedlMsgBath(activitiBpmMsgVos);
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
    public static void sendBpmFinishMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        getMessageSendBean().sendBpmFinishMsgBath(activitiBpmMsgVos);
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
    public void sendBpmRejectMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
       getMessageSendBean().sendBpmRejectMsgBath(activitiBpmMsgVos);
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
    public static void sendBpmOverTimeMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
       getMessageSendBean().sendBpmOverTimeMsgBath(activitiBpmMsgVos);
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
    public static void sendBpmTerminationMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
       getMessageSendBean().sendBpmTerminationMsgBath(activitiBpmMsgVos);
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
    public static void sendBpmGenerationApprovalMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        getMessageSendBean().sendBpmGenerationApprovalMsgBath(activitiBpmMsgVos);
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
