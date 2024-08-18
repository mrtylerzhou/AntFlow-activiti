package org.openoa.base.constant.enums;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public enum NodeTypeEnum {

    NODE_TYPE_START(1, "发起人节点", 0),
    NODE_TYPE_GATEWAY(2, "网关节点", 0),
    NODE_TYPE_CONDITIONS(3, "条件节点", 1),
    NODE_TYPE_APPROVER(4, "审批人节点", 0),
    NODE_TYPE_OUT_SIDE_CONDITIONS(5, "接入方条件节点", 1),
    NODE_TYPE_COPY(6,"抄送节点",1)
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    /**
     * 是否含有属性表(0-否;1-是)
     */
    @Getter
    private Integer hasPropertyTable;

    NodeTypeEnum(Integer code, String desc, Integer hasPropertyTable) {
        this.code = code;
        this.desc = desc;
        this.hasPropertyTable = hasPropertyTable;
    }

    /**
     * 根据编号获得节点类型枚举
     *
     * @param code
     * @return
     */
    public static NodeTypeEnum getNodeTypeEnumByCode(Integer code) {

        //过滤枚举列表中含有属性表的枚举值
        List<NodeTypeEnum> nodeTypeEnums = Lists.newArrayList(NodeTypeEnum.values())
                .stream()
                .filter(o -> o.getHasPropertyTable().equals(1))
                .collect(Collectors.toList());

        //遍历比对编号，批到到枚举后返回
        for (NodeTypeEnum nodeTypeEnum : nodeTypeEnums) {
            if (nodeTypeEnum.getCode().equals(code)) {
                return nodeTypeEnum;
            }
        }

        return null;
    }
}