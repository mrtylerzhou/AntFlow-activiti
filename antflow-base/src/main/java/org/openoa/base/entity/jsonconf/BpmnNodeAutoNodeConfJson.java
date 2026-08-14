package org.openoa.base.entity.jsonconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.vo.BpmnNodeConditionsConfVueVo;

import java.io.Serializable;
import java.util.List;

/**
 * Auto node configuration JSON.
 * Stores conditions that determine when the automatic action should be executed.
 * Reuses {@link BpmnNodeConditionsConfVueVo} for condition items — same structure as condition nodes.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnNodeAutoNodeConfJson implements Serializable {

    /**
     * Condition groups (outer list = groups, inner list = conditions within a group).
     */
    private List<List<BpmnNodeConditionsConfVueVo>> conditionList;

    /**
     * Group relation: false = AND between groups, true = OR between groups
     */
    private Boolean groupRelation;

    /**
     * 满足条件时处理动作: 0=默认complete(默认), 1=跳转至固定节点, 2=加批, 3=转办, 4=抄送
     */
    private Integer satisfiedAction;

    /**
     * 不满足条件时处理动作: 0=默认complete(默认), 1=结束流程, 2=退回指定节点(重新开始)
     */
    private Integer unsatisfiedAction;

    /**
     * 跳转目标节点ID(设计态nodeId UUID), 仅 satisfiedAction=1 时有值, 单选1个
     */
    private java.util.List<String> forwardNodeIds;

    /**
     * 加批规则子配置(仅 satisfiedAction=2 时有值), 结构同条件自动加批:
     * {setType, nodeApproveList, directorLevel, property, resolvedProperty, signType}
     * 运行期由 AutoSignUpAssigneeResolver 解析为具体用户; 强制 afterSignUpWay=2(不回到审批人)
     */
    private Object autoSignUpConf;

    /**
     * 不满足退回目标节点ID(设计态nodeId UUID), 仅 unsatisfiedAction=2 时有值, 单选1个.
     * 运行期由 backToModifyImpl.returnToTargetNode(..., backType=4) 执行重新开始式退回
     */
    private String backToNodeId;

    /**
     * 转办目标人(仅 satisfiedAction=3 时有值): {id, name}
     * 运行期 setAssignee + 写 BpmFlowrunEntrust, 不complete(任务转人工)
     */
    private Object transferToUser;

    /**
     * 抄送规则子配置(仅 satisfiedAction=4 时有值), 结构同加批配置.
     * 运行期由 AutoSignUpAssigneeResolver 解析, 逐人写 BpmProcessForward
     */
    private Object autoCopyConf;
}
