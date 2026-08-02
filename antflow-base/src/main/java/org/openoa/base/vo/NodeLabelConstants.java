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
    /**条件推进节点: 条件审批(nodeType=12)子类型, 自动勾选推进按钮(42,别名同意). 满足条件自动推进到固定目标, 不满足留给真实审批人.*/
    BpmnNodeLabelVO conditionAdvanceNode=new BpmnNodeLabelVO(StringConstants.CONDITION_ADVANCE_NODE,"条件推进节点");
    /**条件完成节点: 条件推进(nodeType=12)子类型, 目标设计时自动算最后一个审批人节点, 不可编辑. 运行时复用条件推进处理器.*/
    BpmnNodeLabelVO conditionFinishNode=new BpmnNodeLabelVO(StringConstants.CONDITION_FINISH_NODE,"条件完成节点");
    BpmnNodeLabelVO conditionCopyNode=new BpmnNodeLabelVO(StringConstants.CONDITION_COPY_NODE,"条件抄送节点");
    BpmnNodeLabelVO assistNode=new BpmnNodeLabelVO(StringConstants.ASSIST_NODE,"协助节点");
    /**自动推进节点: 满足条件时推进到指定目标节点, 不满足时和自动节点一样 complete*/
    BpmnNodeLabelVO autoAdvanceNode=new BpmnNodeLabelVO(StringConstants.AUTO_ADVANCE_NODE,"自动推进节点");
    /**自动完成节点: 自动推进(18)子类型, 目标自动为最后一个审批人. 仅前端反显区分+颜色区分, 运行时复用 auto_advance_node 处理器.*/
    BpmnNodeLabelVO autoCompleteNode=new BpmnNodeLabelVO(StringConstants.AUTO_COMPLETE_NODE,"自动完成节点");
    /**自动退回节点: 满足条件时退回到指定目标节点(FOUR_DISAGREE), 不满足时和自动节点一样 complete*/
    BpmnNodeLabelVO autoReturnNode=new BpmnNodeLabelVO(StringConstants.AUTO_RETURN_NODE,"自动退回节点");
    /**条件退回节点: 满足条件时自动退回到不同意按钮配置的目标节点, 不满足时留给真实审批人*/
    BpmnNodeLabelVO conditionReturnNode=new BpmnNodeLabelVO(StringConstants.CONDITION_RETURN_NODE,"条件退回节点");
    /**条件退回发起人节点: 满足条件时自动退回发起人节点, 不满足时留给真实审批人*/
    BpmnNodeLabelVO conditionReturnStarterNode=new BpmnNodeLabelVO(StringConstants.CONDITION_RETURN_STARTER_NODE,"条件退回发起人节点");
    /**完成审批节点: 审批人节点+推进按钮, 目标自动填充为流程最后一个审批人节点. 前端着色判据, 后端零逻辑改动.*/
    BpmnNodeLabelVO finishApproveNode=new BpmnNodeLabelVO(StringConstants.FINISH_APPROVE_NODE,"完成审批节点");
    BpmnNodeLabelVO skippedAssignees=new BpmnNodeLabelVO(StringConstants.SKIPPED_ASSIGNEE,"跳过的审批人");
    /**当前节点为"上一节点指定"审批人类型,运行时由NextNodeLabelsProcessor替换虚拟审批人为实际审批人*/
    BpmnNodeLabelVO prevNodeAppointed=new BpmnNodeLabelVO(StringConstants.AF_SYSLABEL_PREV_NODE_APPOINTED,"上一节点指定审批人");
    /**上一节点具有指定下一节点审批人的能力,审批页渲染[指定下一节点审批人]按钮*/
    BpmnNodeLabelVO appointNextNodeApprover=new BpmnNodeLabelVO(StringConstants.AF_SYSLABEL_APPOINT_NEXT_NODE_APPROVER,"指定下一节点审批人");
    /**选择条件:审批人节点贴此标签,运行时渲染[选择分支]下拉框,审批时强制走用户选定的动态条件分支*/
    BpmnNodeLabelVO pickCondition=new BpmnNodeLabelVO(StringConstants.AF_SYSLABEL_PICK_CONDITION,"选择条件节点");
    /**不同意按钮配置了退回行为,运行时EndProcessImpl据此转发BackToModifyImpl*/
    BpmnNodeLabelVO disagreeBack=new BpmnNodeLabelVO(StringConstants.AF_SYSLABEL_DISAGREE_BACK,"不同意退回");
    /**退回按钮行为:退回发起人*/
    BpmnNodeLabelVO backInitiator=new BpmnNodeLabelVO(StringConstants.AF_SYSLABEL_BACK_INITIATOR,"退回发起人");
    /**退回按钮行为:退回上一节点*/
    BpmnNodeLabelVO backPrev=new BpmnNodeLabelVO(StringConstants.AF_SYSLABEL_BACK_PREV,"退回上一节点");
    /**退回按钮行为:退回指定节点*/
    BpmnNodeLabelVO backSpecified=new BpmnNodeLabelVO(StringConstants.AF_SYSLABEL_BACK_SPECIFIED,"退回指定节点");
    /**推进按钮:节点配置了推进行为,运行时渲染[推进]按钮*/
    BpmnNodeLabelVO forward=new BpmnNodeLabelVO(StringConstants.AF_SYSLABEL_FORWARD,"推进");
    public static final List<BpmnNodeLabelVO> NONE_OPERATIONAL_NODES= Lists.newArrayList(//不可操作节点,存在于activiti中,但是不可退回到的节点,动态条件和抄送节v1版本点虽然也不可退回到,但是他们本身不会进入activiti引擎
      copyNodeV2,automaticNode,conditionCopyNode,autoAdvanceNode,autoReturnNode
    );
}
