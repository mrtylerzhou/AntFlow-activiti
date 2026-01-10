package org.openoa.base.constant.enums;

import lombok.Getter;

@Getter
public enum BpmnNodeFlagsEnum implements BinaryAfEnumBase<BpmnNodeFlagsEnum>{
    NOTHING(0,"不存在"),
    HAS_ADDITIONAL_ASSIGNEE(0b1,"包含额外指定人员审批人"),
    HAS_ADDITIONAL_ASSIGNEE_ROLE(0b10,"包含额外指定角色审批人"),
    HAS_EXCLUDE_ASSIGNEE(0b100,"包含排除人员审批人"),
    HAS_EXCLUDE_ASSIGNEE_ROLE(0b1000,"包含排除角色审批人"),
    ;
    private final Integer code;
    private final String desc;

    BpmnNodeFlagsEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    /**
     * flag叠加
     * @param alreadyFlags
     * @param newFlag
     * @return
     */
    public static Integer binaryOr(Integer alreadyFlags,Integer newFlag){
        if(alreadyFlags==null){
            alreadyFlags=0;
        }
        return alreadyFlags|newFlag;
    }
    public static BpmnNodeFlagsEnum getNodeExtraSignInfo(Integer flags){
        if(flags==null){
            return null;
        }
        if(HAS_ADDITIONAL_ASSIGNEE.flagsContainsCurrent(flags)){
            return HAS_ADDITIONAL_ASSIGNEE;
        }else if(HAS_ADDITIONAL_ASSIGNEE_ROLE.flagsContainsCurrent(flags)){
            return HAS_ADDITIONAL_ASSIGNEE_ROLE;
        } else if(HAS_EXCLUDE_ASSIGNEE.flagsContainsCurrent(flags)){
            return HAS_EXCLUDE_ASSIGNEE;
        } else if(HAS_EXCLUDE_ASSIGNEE_ROLE.flagsContainsCurrent(flags)){
            return HAS_EXCLUDE_ASSIGNEE_ROLE;
        }
        return null;
    }
}
