package org.openoa.base.constant.enums;

import com.google.common.collect.Lists;
import lombok.Getter;

import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.BpmnNodeParamTypeEnum.*;

/**
 * 节点属性枚举
 */
public enum NodePropertyEnum {
    //    NODE_PROPERTY_NULL(1, "没有属性", 0, null),
    NODE_PROPERTY_LOOP(2, "层层审批", 1,BPMN_NODE_PARAM_SINGLE),
    NODE_PROPERTY_LEVEL(3, "指定层级审批", 1,BPMN_NODE_PARAM_SINGLE),
    NODE_PROPERTY_ROLE(4, "指定角色", 1, BPMN_NODE_PARAM_MULTIPLAYER),
    NODE_PROPERTY_PERSONNEL(5, "指定人员", 1,BPMN_NODE_PARAM_MULTIPLAYER),
    NODE_PROPERTY_HRBP(6, "HRBP", 1,BPMN_NODE_PARAM_MULTIPLAYER),
    NODE_PROPERTY_CUSTOMIZE(7, "自选模块", 1,BPMN_NODE_PARAM_MULTIPLAYER),
    NODE_PROPERTY_BUSINESSTABLE(8, "关联业务表", 1,BPMN_NODE_PARAM_MULTIPLAYER),
    NODE_PROPERTY_OUT_SIDE_ACCESS(11, "外部传入人员", 1, BPMN_NODE_PARAM_MULTIPLAYER),
    NODE_PROPERTY_START_USER(12,"发起人",1,BPMN_NODE_PARAM_SINGLE),
    NODE_PROPERTY_DIRECT_LEADER(13,"直属领导",1,BPMN_NODE_PARAM_MULTIPLAYER),
    NODE_PROPERTY_DEPARTMENT_LEADER(14,"部门负责人",1,BPMN_NODE_PARAM_MULTIPLAYER),
    NODE_PROPERTY_APPROVED_USERS(15,"被审批人自己",1,BPMN_NODE_PARAM_MULTIPLAYER),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    @Getter
    private final BpmnNodeParamTypeEnum paramTypeEnum;
    /**
     * 是否含有属性表(0-否;1-是)
     */
    @Getter
    private Integer hasPropertyTable;


    NodePropertyEnum(Integer code, String desc, Integer hasPropertyTable,BpmnNodeParamTypeEnum paramTypeEnum) {
        this.code = code;
        this.desc = desc;
        this.paramTypeEnum=paramTypeEnum;
        this.hasPropertyTable = hasPropertyTable;

    }

    public static NodePropertyEnum getByCode(Integer code){
        if(ObjectUtils.isEmpty(code)){
            return null;
        }
        for (NodePropertyEnum value : NodePropertyEnum.values()) {
            if(code.equals(value.getCode())){
                return value;
            }
        }
        return null;
    }
    /**
     * 根据类型获取节点属性枚举
     *
     * @param code
     * @return
     */
    public static NodePropertyEnum getNodePropertyEnumByCode(Integer code) {

        //过滤枚举列表中含有属性表的枚举值
        List<NodePropertyEnum> nodePropertyEnums = Lists.newArrayList(NodePropertyEnum.values())
                .stream()
                .filter(o -> o.getHasPropertyTable().equals(1))
                .collect(Collectors.toList());

        //遍历比对编号，批到到枚举后返回
        for (NodePropertyEnum nodePropertyEnum : nodePropertyEnums) {
            if (nodePropertyEnum.getCode().equals(code)) {
                return nodePropertyEnum;
            }
        }
        return null;
    }


    public static String getDescByCode(Integer code) {
        for (NodePropertyEnum enums : NodePropertyEnum.values()) {
            if (enums.code.equals(code)) {
                return enums.desc;
            }
        }
        return null;
    }
}
