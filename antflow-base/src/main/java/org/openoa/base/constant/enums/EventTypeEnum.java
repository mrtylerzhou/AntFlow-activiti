package org.openoa.base.constant.enums;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

import static org.openoa.base.constant.enums.InformEnum.*;

public enum EventTypeEnum {

    PROCESS_INITIATE(1, "流程发起", true, ProcessOperationEnum.BUTTON_TYPE_SUBMIT.getCode(), 1, Lists.newArrayList(APPLICANT.getCode())),
    PROCESS_CANCELLATION(2, "作废操作", false, ProcessOperationEnum.BUTTON_TYPE_ABANDON.getCode(), 0, Lists.newArrayList(APPLICANT.getCode())),
    PROCESS_FLOW(3, "流程流转至当前节点", true, 0, 4, Collections.singletonList(CURRENT_APPROVER.getCode())),
    PROCESS_CONSENT(4, "同意操作", true, ProcessOperationEnum.BUTTON_TYPE_AGREE.getCode(), 4, Lists.newArrayList()),
    PROCESS_DISAGREE(5, "不同意操作", true, ProcessOperationEnum.BUTTON_TYPE_DIS_AGREE.getCode(), 4, Lists.newArrayList(APPLICANT.getCode())),
    PROCESS_ADDAPPROVER(6, "加批操作", true, ProcessOperationEnum.BUTTON_TYPE_JP.getCode(), 4, Lists.newArrayList()),
    PROCESS_BACK_TO_MODIFY(7, "退回修改操作", true, ProcessOperationEnum.BUTTON_TYPE_BACK_TO_MODIFY.getCode(), 4, Lists.newArrayList()),
    PROCESS_FORWARD(8, "转发操作", false, ProcessOperationEnum.BUTTON_TYPE_FORWARD.getCode(), 0, Lists.newArrayList(FORWARDED_APPROVER.getCode())),
    PROCESS_END(9, "流程结束", false, 0, 0, Collections.singletonList(APPLICANT.getCode())),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    @Getter
    private Boolean isInNode;

    @Getter
    private Integer processOperationType;

    /**
     * 节点类型（1发起节点 4审批节点 0节点外）
     */
    @Getter
    private Integer nodeType;

    /**
     * 默认的通知对象
     */
    @Getter
    private List<Integer> informIdList;

    EventTypeEnum(Integer code, String desc, Boolean isInNode, Integer processOperationType, Integer nodeType, List<Integer> informIdList) {
        this.code = code;
        this.desc = desc;
        this.isInNode = isInNode;
        this.processOperationType = processOperationType;
        this.nodeType = nodeType;
        this.informIdList = informIdList;
    }

    public static EventTypeEnum getByCode(Integer code){
        for (EventTypeEnum eventTypeEnum : EventTypeEnum.values()) {
            if (eventTypeEnum.getCode().equals(code)) {
                return eventTypeEnum;
            }
        }
        return null;
    }
    public static String getDescByByCode(Integer code) {
        EventTypeEnum byCode = getByCode(code);
        if(byCode==null){
            return null;
        }
        return byCode.getDesc();
    }

    public static EventTypeEnum getEnumByOperationType(Integer operationType) {
        for (EventTypeEnum eventTypeEnum : EventTypeEnum.values()) {
            if (eventTypeEnum.getProcessOperationType().equals(operationType)) {
                return eventTypeEnum;
            }
        }
        return null;
    }

}
