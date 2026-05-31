package org.openoa.base.constant.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @Author tylerzhou
 * Date on 2021/8/18
 */
public enum MsgProcessEventEnum implements AfEnumBase{
    NULL(0, "空"),
    PROCESS_SUBMIT(1, "流程提交操作"),
    PROCESS_RESUBMIT(2, "重新提交"),
    PROCESS_APPROVE(3, "同意"),
    PROCESS_NOT_APPROVE(4, "不同意"),
    PROCESS_ABANDON(7, "作废"),
    PROCESS_UNDERTAKE(10, "承办"),
    PROCESS_CHANGE_DEALER(11, "变更处理人"),
    PROCESS_ABORT(12, "终止"),
    PROCESS_FORWARD(15, "转发"),
    BUTTON_BACK_TO_MODIFY(18, "退回修改"),
    PROCESS_JP(19, "加批"),
    PROCESS_FINISH(20, "流程完成"),
    BUTTON_TYPE_ZB(21,"转办"),
    BUTTON_TYPE_CHOOSE_ASSIGNEE(22,"自选审批人"),
    BUTTON_TYPE_BACK_TO_ANY_NODE(23,"退回任意节点"),
    BUTTON_TYPE_PROCESS_DRAW_BACK(29,"流程撤回"),
    BUTTON_TYPE_SAVE_DRAFT(30,"保存草稿"),
    BUTTON_TYPE_RECOVER_TO_HIS(31,"恢复已结束流程"),
    BUTTON_TYPE_DRAW_BACK_AGREE(32,"撤销同意"),
    BUTTON_TYPE_PROCESS_MOVE_AHEAD(33,"流程推进"),;

    @Getter
    private final Integer code;
    @Getter
    private final String desc;

    MsgProcessEventEnum(Integer code, String desc) {

        this.code = code;
        this.desc = desc;
    }


    public static MsgProcessEventEnum ofCode(Integer code) {
        return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findAny().orElse(NULL);
    }

    public static MsgProcessEventEnum getEnumByCode(Integer code) {
        if(code==null){
            return null;
        }
        for (MsgProcessEventEnum value : MsgProcessEventEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return value;
            }
        }
        return null;
    }
    public static String getDescByCode(Integer code){
        if(code ==null){
            return  null;
        }
        for (MsgProcessEventEnum value : MsgProcessEventEnum.values()) {
            if(code.equals(value.getCode())){
                return value.getDesc();
            }
        }
        return null;
    }
}
