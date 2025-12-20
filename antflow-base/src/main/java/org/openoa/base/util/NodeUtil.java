package org.openoa.base.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ElementTypeEnum;
import org.openoa.base.constant.enums.SignTypeEnum;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.entity.BpmnNodeLabel;
import org.openoa.base.service.BpmNodeLabelsService;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.NodeLabelConstants;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.SignTypeEnum.SIGN_TYPE_SIGN;
import static org.openoa.base.constant.enums.SignTypeEnum.SIGN_TYPE_SIGN_IN_ORDER;

public class NodeUtil {
    public static boolean nodeLabelContainsAny(NodeExtraInfoDTO nodeExtraInfoDTO,String ... labelValues){
        if(labelValues.length<1){
            return true;

        }
        if(nodeExtraInfoDTO==null){
            return false;
        }
        List<BpmnNodeLabelVO> nodeLabelVOS = nodeExtraInfoDTO.getNodeLabelVOS();
        return nodeLabelContainsAny(nodeLabelVOS,labelValues);
    }
    public static boolean nodeLabelContainsAny( List<BpmnNodeLabelVO> nodeLabelVOS,String ... labelValues){
        if(labelValues.length<1){
            return true;

        }
        if(CollectionUtils.isEmpty(nodeLabelVOS)){
            return false;
        }
        List<String> providedLabelValues = nodeLabelVOS.stream().map(BpmnNodeLabelVO::getLabelValue).collect(Collectors.toList());
        return CollectionUtils.containsAny(providedLabelValues, Lists.newArrayList(labelValues));
    }
    public static boolean isCurrentNodeNoneOperational(String nodeId){
        BpmNodeLabelsService bpmNodeLabelsService = SpringBeanUtils.getBean(BpmNodeLabelsService.class);
        List<BpmnNodeLabel> nodeLabels = bpmNodeLabelsService.list(AFWrappers.<BpmnNodeLabel>lambdaTenantQuery().eq(BpmnNodeLabel::getNodeId,nodeId));
        if(CollectionUtils.isEmpty(nodeLabels)){
            return true;
        }
        List<String> collect = nodeLabels.stream().map(BpmnNodeLabel::getLabelValue).collect(Collectors.toList());
        List<String> noneOperationalLables = NodeLabelConstants.NONE_OPERATIONAL_NODES.stream().map(BpmnNodeLabelVO::getLabelValue).collect(Collectors.toList());
        return CollectionUtils.containsAny(collect,noneOperationalLables);
    }
    public static void elementWithSpecialMarks( BpmnConfCommonElementVo elementVo){
        String elementName=elementVo.getElementName();
        if((StringUtils.isEmpty(elementVo.getElementName())&& ElementTypeEnum.ELEMENT_TYPE_USER_TASK.getCode().equals(elementVo.getElementType()))
                || StringConstants.AF_DEFAULT_NODE_NAME.equals(elementVo.getElementName())){
            Map<String, String> assigneeMap = elementVo.getAssigneeMap();
            if(assigneeMap.size()<=3){
                elementName=StringUtils.join(assigneeMap.values(),"|")+"审批";
            }else{
                List<String> first3AssigneeNames = assigneeMap.values().stream().limit(3).collect(Collectors.toList());
                elementName=StringUtils.join(first3AssigneeNames,"|")+"等"+assigneeMap.size()+"人审批";
            }

        }
        Integer signType = elementVo.getSignType();
        if(SIGN_TYPE_SIGN.getCode().equals(signType)){
            elementName+=StringConstants.AF_NODE_SIGN_SUFFIX;
        }else if(SIGN_TYPE_SIGN_IN_ORDER.getCode().equals(signType)){
            elementName+=StringConstants.AF_NODE_SIGN_IN_ORDER_SUFFIX;
        }else if(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode().equals(signType)){
            elementName+=StringConstants.AF_NODE_OR_SIGN_SUFFIX;
        }
        List<BpmnNodeLabelVO> labelList = elementVo.getLabelList();
        if(!CollectionUtils.isEmpty(labelList)){
            boolean hasCopyLabel=false;
            boolean hasDeduplicationLabel=false;
            for (BpmnNodeLabelVO label : labelList) {
                if(label.getLabelValue().equals(NodeLabelConstants.copyNodeV2.getLabelValue())){
                    hasCopyLabel=true;
                    continue;
                }
                if(label.getLabelValue().equals(NodeLabelConstants.skippedAssignees.getLabelValue())){
                    hasDeduplicationLabel=true;
                }
            }
            if(hasCopyLabel){
                elementName+=StringConstants.AF_COPY_V2_NODE_SUFFIX;
            }
            if(hasDeduplicationLabel){
                elementName+=StringConstants.AF_SKIP_ASSIGNEE_NODE_SUFFIX;
            }
        }

        elementVo.setElementName(elementName);
    }
}
