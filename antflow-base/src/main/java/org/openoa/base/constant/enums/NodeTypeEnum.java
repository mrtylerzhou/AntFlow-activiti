package org.openoa.base.constant.enums;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public enum NodeTypeEnum implements AfEnumBase{

    NODE_TYPE_START(1, "发起人节点", 0),
    NODE_TYPE_GATEWAY(2, "网关节点", 0),
    NODE_TYPE_CONDITIONS(3, "条件节点", 1),
    NODE_TYPE_APPROVER(4, "审批人节点", 0),
    NODE_TYPE_OUT_SIDE_CONDITIONS(5, "接入方条件节点", 1),
    NODE_TYPE_COPY(6,"抄送节点",1),
    NODE_TYPE_PARALLEL_GATEWAY(7,"并行网关",0),
    NODE_TYPE_COPY_V2(8,"抄送节点v2",0),
    NODE_TYPE_AUTO_NODE(9,"自动节点",0),
    NODE_TYPE_CONDITION_APPROVE(12,"条件审批节点",0),
    NODE_TYPE_CONDITION_COPY(13,"条件抄送节点",0),
    NODE_TYPE_ASSIST(17,"协助节点",0),
    /**
     * 自动推进节点: 本质是自动节点 + 推进按钮(固定节点)的组合
     * 运行期由 NodeUtil.nodeSpecialProcess 转为 4, 塞虚拟审批人 -3
     * 满足条件时推进到指定目标节点, 不满足条件时和自动节点一样 complete
     */
    NODE_TYPE_AUTO_ADVANCE(18,"自动推进节点",0),
    /**
     * 自动退回节点: 本质是自动节点 + 退回按钮(固定节点)的组合
     * 运行期由 NodeUtil.nodeSpecialProcess 转为 4, 塞虚拟审批人 -3
     * 满足条件时退回到指定目标节点(FOUR_DISAGREE), 不满足条件时和自动节点一样 complete
     */
    NODE_TYPE_AUTO_RETURN(19,"自动退回节点",0),
    /**
     * 条件退回节点: 设计期 nodeType=20, 运行期转为 4, 保留真实审批人
     * 满足条件时自动退回到不同意按钮配置的目标节点, 不满足时留给审批人人工处理
     */
    NODE_TYPE_CONDITION_RETURN(20,"条件退回节点",0),
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