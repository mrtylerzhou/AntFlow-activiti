package org.openoa.base.constant.enums;

import lombok.Getter;

/**
 *
 */
public enum CallbackTypeEnum {

    //流程配置回调类型
    CONF_CONDITION_CALL_BACK("CONF_CONDITION_CALL_BACK", "条件分支回调", "CONF_CONDITION_CALL_BACK"),
    //流程流转回调类型
    PROC_CONDITION_CALL_BACK("PROC_CONDITION_CALL_BACK", "条件判断回调", "PROC_CONDITION_CALL_BACK"),
    PROC_SUBMIT_CALL_BACK("PROC_SUBMIT_CALL_BACK", "提交操作回调", "PROC_SUBMIT_CALL_BACK"),
    PROC_STARTED_CALL_BACK("PROC_STARTED_CALL_BACK", "流程发起完成回调", "PROC_BASE_CALL_BACK"),
    PROC_COMMIT_CALL_BACK("PROC_COMMIT_CALL_BACK", "流转操作回调", "PROC_BASE_CALL_BACK"),
    PROC_END_CALL_BACK("PROC_END_CALL_BACK", "结束操作回调", "PROC_BASE_CALL_BACK"),
    PROC_FINISH_CALL_BACK("PROC_FINISH_CALL_BACK", "完成操作回调", "PROC_BASE_CALL_BACK"),
    ;

    @Getter
    private String mark;

    @Getter
    private String desc;

    @Getter
    private String beanId;

    CallbackTypeEnum(String mark, String desc, String beanId) {
        this.mark = mark;
        this.desc = desc;
        this.beanId = beanId;
    }
}
