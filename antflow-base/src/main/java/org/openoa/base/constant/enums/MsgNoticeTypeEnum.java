package org.openoa.base.constant.enums;

import lombok.Getter;

/**
 * @Classname MsgNoticeTypeEnum
 * @Description TODO
 * @Date 2021-11-06 8:19
 * @Created by AntOffice
 */
public enum MsgNoticeTypeEnum {
    //消息通知类型
    PROCESS_FLOW(1, "工作流流转通知", "您有1个{流程类型}{流程名称}{流程编号}需要处理"),
    RECEIVE_FLOW_PROCESS(2, "收到转发工作流通知", "您有1个{流程类型}{流程名称}{流程编号}需要查看"),
    PROCESS_FINISH(3, "工作流完成通知", "您的{流程类型}{流程名称}{流程编号}已完成"),
    PROCESS_REJECT(4, "工作流流程审批不通过通知", "您参与审批的{流程类型}{流程名称}{流程编号}已被{审批不同意者}驳回"),
    PROCESS_TIME_OUT(5, "工作流超时通知", "您有1个{流程类型}{流程名称}{流程编号}已超过处理期限，请立即处理"),
    PROCESS_STOP(6, "工作流被终止通知", "您参与审批的{流程类型}{流程名称}{流程编号}已被{操作者}终止"),
    PROCESS_WAIR_VERIFY(7, "工作流代审批通知", "您参与审批的{流程类型}{流程名称}{流程编号}已被{操作者}代审批"),
    PROCESS_CHANGE_OPERATOR(8, "工作流变更处理人通知(原审批节点处理人)", "您参与审批的{流程类型}{流程名称}{流程编号}已被变更为{变更后处理人}处理"),
    PROCESS_CHANGE_NOW_OPERATOR(9, "工作流变更处理人通知(现审批节点处理人)", "您有1个从{原审批节点处理人}转给您的{流程类型}{流程名称}{流程编号}需要处理"),
    PROCESS_SILENCE(10, "发送流程沉默消息通知", "{流程类型}{流程名称}{流程编号}无人处理，请至流程管理中进行干预。"),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    @Getter
    private String defaultValue;

    MsgNoticeTypeEnum(Integer code, String desc, String defaultValue) {
        this.code = code;
        this.desc = desc;
        this.defaultValue = defaultValue;
    }

    public static String getDescByCode(Integer code) {
        for (MsgNoticeTypeEnum enums : MsgNoticeTypeEnum.values()) {
            if (enums.code.equals(code)) {
                return enums.desc;
            }
        }
        return null;
    }

    public static String getDefaultValueByCode(Integer code) {
        for (MsgNoticeTypeEnum enums : MsgNoticeTypeEnum.values()) {
            if (enums.code.equals(code)) {
                return enums.defaultValue;
            }
        }
        return null;
    }
}