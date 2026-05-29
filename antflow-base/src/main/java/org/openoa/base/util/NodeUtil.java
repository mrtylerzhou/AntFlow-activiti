package org.openoa.base.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ElementTypeEnum;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.constant.enums.SignTypeEnum;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.BpmnNodeLabel;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.BpmnNodeButtonSignConfJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.service.BpmNodeLabelsService;
import org.openoa.base.vo.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
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
        List<BpmnNodeLabelVO> labelVOs = getLabelsFromNodeJson(nodeId);
        if (!CollectionUtils.isEmpty(labelVOs)) {
            List<String> collect = labelVOs.stream().map(BpmnNodeLabelVO::getLabelValue).collect(Collectors.toList());
            List<String> noneOperationalLables = NodeLabelConstants.NONE_OPERATIONAL_NODES.stream().map(BpmnNodeLabelVO::getLabelValue).collect(Collectors.toList());
            return CollectionUtils.containsAny(collect, noneOperationalLables);
        }
       return true;
    }

    private static List<BpmnNodeLabelVO> getLabelsFromNodeJson(String nodeId) {
        try {
            Object bpmnNodeService = SpringBeanUtils.getBean(Class.forName("org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService"));
            java.lang.reflect.Method getById = bpmnNodeService.getClass().getMethod("getById", Serializable.class);
            Object node = getById.invoke(bpmnNodeService, Long.valueOf(nodeId));
            if (node == null) {
                return null;
            }
            java.lang.reflect.Method getNodeConfigJson = node.getClass().getMethod("getNodeConfigJson");
            String nodeConfigJson = (String) getNodeConfigJson.invoke(node);
            if (StringUtils.isEmpty(nodeConfigJson)) {
                return null;
            }
            BpmnNodeConfigJson nodeConfig = JsonConfUtil.parseNodeConfig(nodeConfigJson);
            if (nodeConfig == null || nodeConfig.getButtonSignConf() == null
                    || CollectionUtils.isEmpty(nodeConfig.getButtonSignConf().getLabels())) {
                return null;
            }
            return nodeConfig.getButtonSignConf().getLabels().stream()
                    .map(l -> new BpmnNodeLabelVO(l.getLabelValue(), l.getLabelName()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
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
    /** deduplicate buttons by type
     * @param initiateButtons
     * @return
     */
    public static List<ProcessActionButtonVo> repeatButtonFilter(List<ProcessActionButtonVo> initiateButtons) {
        if(ObjectUtils.isEmpty(initiateButtons)){
            return Lists.newArrayList();
        }
        List<ProcessActionButtonVo> lists = initiateButtons
                .stream()
                .filter(FilterUtil.distinctByKeys(ProcessActionButtonVo::getButtonType))
                .collect(Collectors.toList());
        return lists;
    }

    public static void nodeSpecialProcess(BpmnNodeVo bpmnNodeVo){
        if(!CollectionUtils.isEmpty(bpmnNodeVo.getLabelList())){
            bpmnNodeVo.setLabelList(null);
        }
        Integer nodeType = bpmnNodeVo.getNodeType();
        if(nodeType==null){
            return;
        }
        if(NodeTypeEnum.NODE_TYPE_COPY_V2.getCode().equals(nodeType)){
            bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
            bpmnNodeVo.setIsCarbonCopyNode(true);
        }
    }
    public static void nodeLabelSpecialProcess(BpmnNodeVo bpmnNodeVo){
        List<BpmnNodeLabelVO> labelList = bpmnNodeVo.getLabelList();
        if(CollectionUtils.isEmpty(labelList)){
            return;
        }
        for (BpmnNodeLabelVO nodeLabelVO : labelList) {
            if(NodeLabelConstants.copyNodeV2.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_COPY_V2.getCode());
            }
        }
    }
}
