package org.openoa.base.constant.enums;

import lombok.Getter;

@Getter
public enum MissingAssigneeProcessStragtegyEnum implements  AfEnumBase{
    NOT_ALLOWED(0,"不允许发起"),
    SKIP(1, "跳过"),//注意,这里的跳过指的是不生成审批任务节点,即流程图里没有当前缺失审批人的节点
    TRANSFER_TO_ADMIN(2, "转办给管理员"),//转给管理员需实现BpmnProcessAdminProvider接口
    ;
    private final Integer code;
    private final String desc;

    MissingAssigneeProcessStragtegyEnum(int code, String desc){

        this.code = code;
        this.desc = desc;
    }
    public static MissingAssigneeProcessStragtegyEnum getByCode(Integer code){
        if(code==null){
            return null;
        }
        for(MissingAssigneeProcessStragtegyEnum e : MissingAssigneeProcessStragtegyEnum.values()){
            if(e.getCode() == code){
                return e;
            }
        }
        return null;
    }
}
