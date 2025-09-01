package org.openoa.base.constant.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NodeFormAssigneePropertyEnum implements AfEnumBase{
    FORM_ASSIGNEE(1,"表单中的人员"),
    FORM_ROLE(2,"表单中的角色"),
    FORM_USER_HRBP(3,"表单中人员的HRBP"),
    FORM_USER_DIRECT_LEADER(4,"表单中人员的直属领导"),
    FORM_USER_DEPART_LEADER(5,"表单中人员所在部门负责人"),
    FORM_DEPART_LEADER(6,"表单中部门的负责人"),
    FORM_USER_LEVEL_LEADER(7,"表单中人员多级领导"),
    FORM_USER_LOOP_LEADER(8,"表单中人员全部层级领导")

    ;
    private Integer code;
    /**
     * diy的预定义字段名,值必须传入指定的字段中,lf的不需要
     * @see org.openoa.base.vo.FormRelatedAssignee
     */
    //private String diyFieldNames;
    private String desc;

    public static NodeFormAssigneePropertyEnum getByCode(Integer code){
        if(code==null){
            return null;
        }
        for (NodeFormAssigneePropertyEnum value : NodeFormAssigneePropertyEnum.values()) {
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
        for (NodeFormAssigneePropertyEnum value : NodeFormAssigneePropertyEnum.values()) {
            if(value.getCode().equals(code)){
                return value.desc;
            }
        }
        return "";
    }
}
