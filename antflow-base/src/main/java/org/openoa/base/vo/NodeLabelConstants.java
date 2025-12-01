package org.openoa.base.vo;

import com.google.common.collect.Lists;
import org.openoa.base.constant.StringConstants;

import java.util.List;

public interface NodeLabelConstants {
    BpmnNodeLabelVO dynamicCondition=new BpmnNodeLabelVO(StringConstants.DYNAMIC_CONDITION_NODE,"动态条件节点");
    BpmnNodeLabelVO copyNode=new BpmnNodeLabelVO(StringConstants.COPY_NODE,"抄送节点");
    BpmnNodeLabelVO copyNodeV2=new BpmnNodeLabelVO(StringConstants.COPY_NODEV2,"抄送节点V2");

    public static final List<BpmnNodeLabelVO> NONE_OPERATIONAL_NODES= Lists.newArrayList(
      copyNodeV2
    );
}
