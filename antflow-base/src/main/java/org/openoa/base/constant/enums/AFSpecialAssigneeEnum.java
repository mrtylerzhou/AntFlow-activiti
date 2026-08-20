package org.openoa.base.constant.enums;

import lombok.Getter;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum AFSpecialAssigneeEnum implements AfEnumBase{
    TO_BE_REMOVED(0, "0", "最终会被去除的人员"),
    CC_NODE(-1,"-1","抄送人"),
    SKIP(-2,"-2","自动节点自动跳过"),
    AUTO_NODE_SKIP(-3,"-3","自动节点自动跳过"),
    PREV_NODE_APPOINTED(-4,"-4","上一节点指定的审批人"),
    ARRIVAL_DYNAMIC_ASSIGNEE(-5,"-5","到达前动态查询审批人"),
    TO_BE_IMPLEMENTED(-99,"-5","待实现"),
    ;
    private final Integer code;
    private final String id;
    private final String desc;

    AFSpecialAssigneeEnum(int code, String id, String desc){

        this.code = code;
        this.id = id;
        this.desc = desc;
    }
    public static BaseIdTranStruVo buildToBeRemoved(){
        return new BaseIdTranStruVo(TO_BE_REMOVED.getId(), TO_BE_REMOVED.getDesc());
    }
    public static List<BaseIdTranStruVo> getAllSpecialAssignees(){
        List<BaseIdTranStruVo> baseIdTranStruVos=new ArrayList<>();
        for (AFSpecialAssigneeEnum value : AFSpecialAssigneeEnum.values()) {
            baseIdTranStruVos.add(new BaseIdTranStruVo(value.getId(), value.getDesc()));
        }
        return baseIdTranStruVos;
    }

    public static BaseIdTranStruVo getSpecialAssignee(String id){
        for (AFSpecialAssigneeEnum value : AFSpecialAssigneeEnum.values()) {
            if(value.getId().equals(id)){
                return new BaseIdTranStruVo(value.getId(), value.getDesc());
            }
        }
        return null;
    }
}
