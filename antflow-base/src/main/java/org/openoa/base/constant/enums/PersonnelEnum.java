package org.openoa.base.constant.enums;

import lombok.Getter;
import org.springframework.util.ObjectUtils;

import static org.openoa.base.constant.enums.NodePropertyEnum.*;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-01 10:49
 * @Param
 * @return
 * @Version 1.0
 */
public enum PersonnelEnum {
    NODE_LOOP_PERSONNEL(NODE_PROPERTY_LOOP,"层层审批"),
    NODE_LEVEL_PERSONNEL(NODE_PROPERTY_LEVEL,"指定层级审批"),
    ROLE_PERSONNEL(NODE_PROPERTY_ROLE, "指定角色"),
    USERAPPOINTED_PERSONNEL(NODE_PROPERTY_PERSONNEL, "指定人员"),
    CUSTOMIZABLE_PERSONNEL(NODE_PROPERTY_CUSTOMIZE,"发起人自选"),
    HRBP_PERSONNEL(NODE_PROPERTY_HRBP, "HRBP"),
    OUT_SIDE_ACCESS_PERSONNEL(NODE_PROPERTY_OUT_SIDE_ACCESS,"外部传入人员"),
    START_USER_PERSONNEL(NODE_PROPERTY_START_USER, "发起人自己"),
    DIRECT_LEADER_PERSONNEL(NODE_PROPERTY_DIRECT_LEADER, "直属领导"),
    BUSINESS_TABLE_PERSONNEL(NODE_PROPERTY_BUSINESSTABLE, "关联业务表"),
    DEPARTMENT_LEADER_PERSONNEL(NODE_PROPERTY_DEPARTMENT_LEADER,"部门负责人"),
    APPROVED_USERS_PERSONNEL(NODE_PROPERTY_APPROVED_USERS,"被审批人自己"),
    FORM_USERS_PERSONNEL(NODE_PROPERTY_FORM_RELATED,"表单上下文人员")
    ;
    ;
    PersonnelEnum(NodePropertyEnum nodePropertyEnum,  String desc){
        this.nodePropertyEnum = nodePropertyEnum;

        this.desc=desc;
    }

    public static PersonnelEnum fromNodePropertyEnum(NodePropertyEnum nodePropertyEnum){
        if(ObjectUtils.isEmpty(nodePropertyEnum)){
            return null;
        }
        for (PersonnelEnum personnelEnum : PersonnelEnum.values()) {
            if(nodePropertyEnum==personnelEnum.nodePropertyEnum){
               return   personnelEnum;
            }
        }
        return null;
    }
    @Getter
    private final NodePropertyEnum nodePropertyEnum;
    @Getter
    private final String desc;
}
