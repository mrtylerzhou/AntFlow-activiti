package org.openoa.base.vo;

import com.google.common.collect.Lists;
import org.openoa.base.constant.StringConstants;

import java.util.List;

public interface NodeLabelConstants {
    BpmnNodeLabelVO dynamicCondition=new BpmnNodeLabelVO(StringConstants.DYNAMIC_CONDITION_NODE,"动态条件节点");
    BpmnNodeLabelVO copyNode=new BpmnNodeLabelVO(StringConstants.COPY_NODE,"抄送节点");
    //抄送节点V2版本相较于v2版本,它会真正进入到activiti引擎,选人规则更加灵活(v1只支持指定人员),而且能在流程图中展示出来
    BpmnNodeLabelVO copyNodeV2=new BpmnNodeLabelVO(StringConstants.COPY_NODEV2,"抄送节点V2");
    BpmnNodeLabelVO automaticNode=new BpmnNodeLabelVO(StringConstants.AUTOMATIC_NODE,"自动节点");
    BpmnNodeLabelVO conditionApproveNode=new BpmnNodeLabelVO(StringConstants.CONDITION_APPROVE_NODE,"条件审批节点");
    BpmnNodeLabelVO conditionCopyNode=new BpmnNodeLabelVO(StringConstants.CONDITION_COPY_NODE,"条件抄送节点");
    BpmnNodeLabelVO skippedAssignees=new BpmnNodeLabelVO(StringConstants.SKIPPED_ASSIGNEE,"跳过的审批人");
    /**当前节点为"上一节点指定"审批人类型,运行时由NextNodeLabelsProcessor替换虚拟审批人为实际审批人*/
    BpmnNodeLabelVO prevNodeAppointed=new BpmnNodeLabelVO(StringConstants.AF_SYSLABEL_PREV_NODE_APPOINTED,"上一节点指定审批人");
    /**上一节点具有指定下一节点审批人的能力,审批页渲染[指定下一节点审批人]按钮*/
    BpmnNodeLabelVO appointNextNodeApprover=new BpmnNodeLabelVO(StringConstants.AF_SYSLABEL_APPOINT_NEXT_NODE_APPROVER,"指定下一节点审批人");
    /**不同意按钮配置了退回行为,运行时EndProcessImpl据此转发BackToModifyImpl*/
    BpmnNodeLabelVO disagreeBack=new BpmnNodeLabelVO(StringConstants.AF_SYSLABEL_DISAGREE_BACK,"不同意退回");
    public static final List<BpmnNodeLabelVO> NONE_OPERATIONAL_NODES= Lists.newArrayList(//不可操作节点,存在于activiti中,但是不可退回到的节点,动态条件和抄送节v1版本点虽然也不可退回到,但是他们本身不会进入activiti引擎
      copyNodeV2,automaticNode,conditionCopyNode
    );
}
