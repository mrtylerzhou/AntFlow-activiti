package org.openoa.engine.bpmnconf.util;



import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.ActivitiBpmMsgVo;
import org.openoa.engine.bpmnconf.service.impl.ActivitiBpmMsgTemplateServiceImpl;

import java.util.List;

public class ActivitiTemplateMsgUtils {

    /**
     * send custom message
     *
     * @param activitiBpmMsgVo
     * @param content
     */
    public static void sendCustomMsg(ActivitiBpmMsgVo activitiBpmMsgVo, String content) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmCustomMsg(activitiBpmMsgVo, content);
    }

    /**
     * process flow message
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmApprovalMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmApprovalMsg(activitiBpmMsgVo);
    }

    /**
     * process flow message in batch
     *
     * @param activitiBpmMsgVos
     */
    public static void sendBpmApprovalMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmApprovalMsgBath(activitiBpmMsgVos);
    }

    /**
     * process forward notice
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmForwardedlMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmForwardedlMsg(activitiBpmMsgVo);
    }

    /**
     * process forward notice in batch
     *
     * @param activitiBpmMsgVos
     */
    public static void sendBpmForwardedlMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmForwardedlMsgBath(activitiBpmMsgVos);
    }

    /**
     * process finish notice
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmFinishMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmFinishMsg(activitiBpmMsgVo);
    }

    /**
     * process finish notice in batch
     *
     * @param activitiBpmMsgVos
     */
    public static void sendBpmFinishMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmFinishMsgBath(activitiBpmMsgVos);
    }

    /**
     * process reject notice
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmRejectMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmRejectMsg(activitiBpmMsgVo);
    }

    /**
     * process reject notice in batch
     *
     * @param activitiBpmMsgVos
     */
    public void sendBpmRejectMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmRejectMsgBath(activitiBpmMsgVos);
    }

    /**
     * process overtime notice
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmOverTimeMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmOverTimeMsg(activitiBpmMsgVo);
    }

    /**
     * process over time notice in batch
     *
     * @param activitiBpmMsgVos
     */
    public static void sendBpmOverTimeMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmOverTimeMsgBath(activitiBpmMsgVos);
    }

    /**
     * process terminate notice
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmTerminationMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmTerminationMsg(activitiBpmMsgVo);
    }

    /**
     * process terminate notice in batch
     *
     * @param activitiBpmMsgVos
     */
    public static void sendBpmTerminationMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmTerminationMsgBath(activitiBpmMsgVos);
    }

    /**
     * process forward notice
     *
     * @param activitiBpmMsgVo
     */
    public static void sendBpmGenerationApprovalMsg(ActivitiBpmMsgVo activitiBpmMsgVo) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmGenerationApprovalMsg(activitiBpmMsgVo);
    }

    /**
     *  process forward notice in batch
     *
     * @param activitiBpmMsgVos
     */
    public static void sendBpmGenerationApprovalMsgBath(List<ActivitiBpmMsgVo> activitiBpmMsgVos) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmGenerationApprovalMsgBath(activitiBpmMsgVos);
    }

    /**
     * process change assignee notice
     *
     * @param activitiBpmMsgVoOrgi
     * @param activitiBpmMsgVoNew
     */
    public static void sendBpmChangePerson(ActivitiBpmMsgVo activitiBpmMsgVoOrgi, ActivitiBpmMsgVo activitiBpmMsgVoNew) {
        ActivitiBpmMsgTemplateServiceImpl bean = SpringBeanUtils.getBean(ActivitiBpmMsgTemplateServiceImpl.class);
        bean.sendBpmChangePersonOrgiMsg(activitiBpmMsgVoOrgi);
        bean.sendBpmChangePersonNewMsg(activitiBpmMsgVoNew);
    }



}
