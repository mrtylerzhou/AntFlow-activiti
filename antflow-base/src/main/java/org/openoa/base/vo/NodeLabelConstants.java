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
    BpmnNodeLabelVO skippedAssignees=new BpmnNodeLabelVO(StringConstants.SKIPPED_ASSIGNEE,"跳过的审批人");
    public static final List<BpmnNodeLabelVO> NONE_OPERATIONAL_NODES= Lists.newArrayList(//不可操作节点,存在于activiti中,但是不可退回到的节点,动态条件和抄送节v1版本点虽然也不可退回到,但是他们本身不会进入activiti引擎
      copyNodeV2,automaticNode
    );
}
