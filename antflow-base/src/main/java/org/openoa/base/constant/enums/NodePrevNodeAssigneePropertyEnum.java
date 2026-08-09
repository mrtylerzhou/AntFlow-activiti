package org.openoa.base.constant.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NodePrevNodeAssigneePropertyEnum implements AfEnumBase{
    PREV_NODE_ASSIGNEE(1,"上一节点人员"),
    PREV_NODE_USER_HRBP(3,"上一节点人员的HRBP"),
    PREV_NODE_USER_DIRECT_LEADER(4,"上一节点人员的直属领导"),
    PREV_NODE_USER_DEPART_LEADER(5,"上一节点人员所在部门负责人"),
    PREV_NODE_DEPART_LEADER(6,"上一节点部门的负责人"),
    PREV_NODE_USER_LEVEL_LEADER(7,"上一节点人员多级领导"),
    PREV_NODE_USER_LOOP_LEADER(8,"上一节点人员全部层级领导")

    ;
    private Integer code;
    private String desc;

    public static NodePrevNodeAssigneePropertyEnum getByCode(Integer code){
        if(code==null){
            return null;
        }
        for (NodePrevNodeAssigneePropertyEnum value : NodePrevNodeAssigneePropertyEnum.values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }
    public static String getDescByCode(Integer code){
        if(code==null){
            return "";
        }
        for (NodePrevNodeAssigneePropertyEnum value : NodePrevNodeAssigneePropertyEnum.values()) {
            if(value.getCode().equals(code)){
                return value.desc;
            }
        }
        return "";
    }
}
