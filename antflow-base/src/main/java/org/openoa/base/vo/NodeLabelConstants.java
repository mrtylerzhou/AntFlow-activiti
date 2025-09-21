package org.openoa.base.vo;

import org.openoa.base.constant.StringConstants;

public interface NodeLabelConstants {
    BpmnNodeLabelVO dynamicCondition=new BpmnNodeLabelVO(StringConstants.DYNAMIC_CONDITION_NODE,"动态条件节点");
    BpmnNodeLabelVO copyNode=new BpmnNodeLabelVO(StringConstants.COPY_NODE,"抄送节点");
    BpmnNodeLabelVO copyNodeV2=new BpmnNodeLabelVO(StringConstants.COPY_NODEV2,"抄送节点V2");
}
