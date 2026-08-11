package org.openoa.base.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.constant.enums.ElementTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
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
    /**
     * 判断指定节点是否包含给定的标签值
     */
    public static boolean hasLabel(String nodeId, String labelValue) {
        if (StringUtils.isEmpty(nodeId) || StringUtils.isEmpty(labelValue)) {
            return false;
        }
        List<BpmnNodeLabelVO> labelVOs = getLabelsFromNodeJson(nodeId);
        return nodeLabelContainsAny(labelVOs, labelValue);
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
        //上一节点指定审批人:根据前端传入的 isPrevNodeAppointed 标识,自动贴标签
        if(Boolean.TRUE.equals(bpmnNodeVo.getIsPrevNodeAppointed())){
            bpmnNodeVo.setOrAddLabelList(NodeLabelConstants.prevNodeAppointed);
        }
        //不同意退回:根据前端传入的 disagreeBackType(4/5),自动贴标签
        Integer disagreeBackType = bpmnNodeVo.getDisagreeBackType();
        if(disagreeBackType!=null && (disagreeBackType==4 || disagreeBackType==5)){
            bpmnNodeVo.setOrAddLabelList(NodeLabelConstants.disagreeBack);
        }
        //退回按钮行为:根据前端传入的 drawBackType 自动贴对应标签
        Integer drawBackType = bpmnNodeVo.getDrawBackType();
        if(drawBackType!=null && drawBackType!=0){
            if(drawBackType==2 || drawBackType==3){
                bpmnNodeVo.setOrAddLabelList(NodeLabelConstants.backInitiator);
            }else if(drawBackType==1){
                bpmnNodeVo.setOrAddLabelList(NodeLabelConstants.backPrev);
            }else if(drawBackType==4 || drawBackType==5){
                bpmnNodeVo.setOrAddLabelList(NodeLabelConstants.backSpecified);
            }
        }
        //推进按钮:根据前端传入的 forwardType 自动贴推进标签
        //推进标签(af_syslabel_forward): 仅当真正配置了推进(forwardType=1指定节点/2固定节点)时才贴
        //forwardType=0(任意未来节点)是普通审批人节点的默认值, 并非配置了推进, 不应贴标签.
        //否则普通审批人节点(如退回审批的目标节点)会被贴上推进标签, 导致 isCurrentNodeNoneOperational 判为"有标签"而不可退回.
        Integer forwardType = bpmnNodeVo.getForwardType();
        if(forwardType!=null && forwardType!=0){
            bpmnNodeVo.setOrAddLabelList(NodeLabelConstants.forward);
        }
        //完成审批节点:根据前端传入的 isFinishApproveNode 标识自动贴标签
        //完成审批本质是审批人节点+推进按钮, 但目标自动填充为最后一个审批人节点
        if(Boolean.TRUE.equals(bpmnNodeVo.getIsFinishApproveNode())){
            bpmnNodeVo.setOrAddLabelList(NodeLabelConstants.finishApproveNode);
        }
        //同意推进节点:根据前端传入的 isApproveForwardNode 标识自动贴标签
        //同意推进本质是审批人节点+同意按钮(固定节点行为), complete 后推进到 forwardNodeIds 指定节点
        //与推进按钮(42)互斥: isApproveForwardNode=true 时 buttons.approvalPage 不能含 42
        if(Boolean.TRUE.equals(bpmnNodeVo.getIsApproveForwardNode())){
            //字段校验: forwardType 必须为 2(固定节点), forwardNodeIds 必须恰好 1 个
            Integer aft = bpmnNodeVo.getForwardType();
            if(aft==null || aft!=2){
                throw new IllegalArgumentException("同意推进节点的 forwardType 必须为 2(固定节点)");
            }
            List<String> afIds = bpmnNodeVo.getForwardNodeIds();
            if(afIds==null || afIds.size()!=1){
                throw new IllegalArgumentException("同意推进节点必须配置恰好 1 个固定目标节点");
            }
            //互斥校验: buttons.approvalPage 不能含推进按钮(buttonType=42)
            BpmnNodeButtonConfBaseVo btns = bpmnNodeVo.getButtons();
            if(btns!=null && !CollectionUtils.isEmpty(btns.getApprovalPage())){
                boolean hasForwardBtn = btns.getApprovalPage().stream()
                        .anyMatch(b -> b!=null && Integer.valueOf(42).equals(b.getButtonType()));
                if(hasForwardBtn){
                    throw new IllegalArgumentException("同意推进与推进按钮(42)互斥, 不能同时配置");
                }
            }
            bpmnNodeVo.setOrAddLabelList(NodeLabelConstants.approveForwardNode);
        }
        //自动完成节点:根据前端传入的 isAutoCompleteNode 标识自动贴标签
        //自动完成本质是自动推进(18)子类型, 目标自动为最后一个审批人, 运行时复用 auto_advance_node 处理器
        //此标签仅用于前端反显区分+颜色区分
        if(Boolean.TRUE.equals(bpmnNodeVo.getIsAutoCompleteNode())){
            bpmnNodeVo.setOrAddLabelList(NodeLabelConstants.autoCompleteNode);
        }
        Integer nodeType = bpmnNodeVo.getNodeType();
        if(nodeType==null){
            return;
        }
        if(NodeTypeEnum.NODE_TYPE_COPY_V2.getCode().equals(nodeType)){
            bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
            bpmnNodeVo.setIsCarbonCopyNode(true);
        }
        if(NodeTypeEnum.NODE_TYPE_AUTO_NODE.getCode().equals(nodeType)){
            bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
            bpmnNodeVo.setIsAutomaticNode(true);
            bpmnNodeVo.setNodeProperty(NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode());
            BpmnNodePropertysVo prop = bpmnNodeVo.getProperty();
            if (prop == null) {
                prop = new BpmnNodePropertysVo();
                bpmnNodeVo.setProperty(prop);
            }
            if (prop.getSignType() == null) {
                prop.setSignType(1);
            }
            if (CollectionUtils.isEmpty(prop.getEmplIds())) {
                prop.setEmplIds(Lists.newArrayList(AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId()));
            }
            if (CollectionUtils.isEmpty(prop.getEmplList())) {
                BaseIdTranStruVo virtualUser = new BaseIdTranStruVo(
                        AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId(),
                        AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc());
                prop.setEmplList(Lists.newArrayList(virtualUser));
            }
        }
        if(NodeTypeEnum.NODE_TYPE_CONDITION_APPROVE.getCode().equals(nodeType)){
            //条件审批节点: 设计期 nodeType=12, 运行期统一为审批人节点 4
            //与 auto node 不同: 不强制 nodeProperty, 不塞虚拟审批人, 保留用户配置的真实 nodeApproveList
            bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
            bpmnNodeVo.setIsConditionApproveNode(true);
        }
        if(NodeTypeEnum.NODE_TYPE_CONDITION_COPY.getCode().equals(nodeType)){
            //条件抄送节点: 设计期 nodeType=13, 运行期统一为审批人节点 4
            //与 copyNodeV2 类似, 但不在这里塞虚拟审批人; 运行期由 processConditionCopyNode 设 CC_NODE
            bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
            bpmnNodeVo.setIsConditionCopyNode(true);
        }
        if(NodeTypeEnum.NODE_TYPE_ASSIST.getCode().equals(nodeType)){
            //协助节点: 设计期 nodeType=16, 运行期统一为审批人节点 4
            //不强制 nodeProperty, 不塞虚拟审批人, 保留用户配置的真实办理人
            bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
            bpmnNodeVo.setIsAssistNode(true);
        }
        if(NodeTypeEnum.NODE_TYPE_AUTO_ADVANCE.getCode().equals(nodeType)){
            //自动推进节点: 设计期 nodeType=18, 运行期统一为审批人节点 4
            //与自动节点(9)同构: 强制指定人员 + 塞虚拟审批人 -3
            //差异: 满足条件时推进到指定目标节点, 不满足时和自动节点一样 complete
            bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
            bpmnNodeVo.setIsAutoAdvanceNode(true);
            bpmnNodeVo.setNodeProperty(NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode());
            BpmnNodePropertysVo prop = bpmnNodeVo.getProperty();
            if (prop == null) {
                prop = new BpmnNodePropertysVo();
                bpmnNodeVo.setProperty(prop);
            }
            if (prop.getSignType() == null) {
                prop.setSignType(1);
            }
            if (CollectionUtils.isEmpty(prop.getEmplIds())) {
                prop.setEmplIds(Lists.newArrayList(AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId()));
            }
            if (CollectionUtils.isEmpty(prop.getEmplList())) {
                BaseIdTranStruVo virtualUser = new BaseIdTranStruVo(
                        AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId(),
                        AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc());
                prop.setEmplList(Lists.newArrayList(virtualUser));
            }
        }
        if(NodeTypeEnum.NODE_TYPE_AUTO_RETURN.getCode().equals(nodeType)){
            //自动退回节点: 设计期 nodeType=19, 运行期统一为审批人节点 4
            //与自动推进(18)同构: 强制指定人员 + 塞虚拟审批人 -3
            //差异: 满足条件时退回到指定目标节点(FOUR_DISAGREE), 不满足时和自动节点一样 complete
            bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
            bpmnNodeVo.setIsAutoReturnNode(true);
            bpmnNodeVo.setNodeProperty(NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode());
            BpmnNodePropertysVo prop = bpmnNodeVo.getProperty();
            if (prop == null) {
                prop = new BpmnNodePropertysVo();
                bpmnNodeVo.setProperty(prop);
            }
            if (prop.getSignType() == null) {
                prop.setSignType(1);
            }
            if (CollectionUtils.isEmpty(prop.getEmplIds())) {
                prop.setEmplIds(Lists.newArrayList(AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId()));
            }
            if (CollectionUtils.isEmpty(prop.getEmplList())) {
                BaseIdTranStruVo virtualUser = new BaseIdTranStruVo(
                        AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getId(),
                        AFSpecialAssigneeEnum.AUTO_NODE_SKIP.getDesc());
                prop.setEmplList(Lists.newArrayList(virtualUser));
            }
        }
        if(NodeTypeEnum.NODE_TYPE_CONDITION_RETURN.getCode().equals(nodeType)){
            //条件退回节点: 设计期 nodeType=20, 运行期统一为审批人节点 4
            //与条件审批(12)类似: 不强制 nodeProperty, 不塞虚拟审批人, 保留真实审批人
            //差异: 满足条件时自动退回到不同意按钮配置的目标节点, 不满足时留给审批人
            bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
            bpmnNodeVo.setIsConditionReturnNode(true);
        }
        if(NodeTypeEnum.NODE_TYPE_CONDITION_RETURN_STARTER.getCode().equals(nodeType)){
            //条件退回发起人节点: 设计期 nodeType=21, 运行期统一为审批人节点 4
            //与条件退回(20)类似: 保留真实审批人, 满足条件时自动退回发起人节点
            bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
            bpmnNodeVo.setIsConditionReturnStarterNode(true);
        }
        //仲裁签节点校验: signType=4 必须配置有效的 arbitrationRatio (0-100)
        //项目记忆硬约束: deployment fails with IllegalArgumentException if ratio is null
        //运行期 OpposeProcessImpl 虽默认 100% (任意反对即终止), 但部署期应拒绝以避免静默降级
        //放在所有 nodeType 转换之后: 自动节点(9/18/19)的 signType 已被强制为 1, 不会误判
        BpmnNodePropertysVo propForArbitration = bpmnNodeVo.getProperty();
        if (propForArbitration != null && Integer.valueOf(4).equals(propForArbitration.getSignType())) {
            Integer ratio = propForArbitration.getArbitrationRatio();
            if (ratio == null || ratio < 0 || ratio > 100) {
                throw new IllegalArgumentException(
                        "仲裁签节点(signType=4)必须配置有效的 arbitrationRatio (0-100), 节点: "
                                + bpmnNodeVo.getNodeName());
            }
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
            if(NodeLabelConstants.automaticNode.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_AUTO_NODE.getCode());
            }
            if(NodeLabelConstants.conditionApproveNode.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_CONDITION_APPROVE.getCode());
            }
            if(NodeLabelConstants.conditionAdvanceNode.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               //条件推进节点: 条件审批(nodeType=12)子类型, 还原 nodeType=12 并置标记位(前端据此显示推进设置tab/图标/颜色)
               bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_CONDITION_APPROVE.getCode());
               bpmnNodeVo.setIsConditionAdvanceNode(true);
            }
            if(NodeLabelConstants.conditionFinishNode.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               //条件完成节点: 条件推进(nodeType=12)子类型, 目标自动算最后一个审批人. 还原 nodeType=12 并置标记位
               bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_CONDITION_APPROVE.getCode());
               bpmnNodeVo.setIsConditionFinishNode(true);
            }
            if(NodeLabelConstants.conditionDisagreeNode.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               //条件拒绝节点: 条件审批(nodeType=12)子类型, 满足条件自动拒绝终止流程. 还原 nodeType=12 并置标记位
               bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_CONDITION_APPROVE.getCode());
               bpmnNodeVo.setIsConditionDisagreeNode(true);
            }
            if(NodeLabelConstants.conditionAutoSignUpNode.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               //条件自动加批节点: 条件审批(nodeType=12)子类型, 满足条件自动加批. 还原 nodeType=12 并置标记位
               bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_CONDITION_APPROVE.getCode());
               bpmnNodeVo.setIsConditionAutoSignUpNode(true);
            }
            if(NodeLabelConstants.conditionCopyNode.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_CONDITION_COPY.getCode());
            }
            if(NodeLabelConstants.assistNode.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_ASSIST.getCode());
            }
            if(NodeLabelConstants.autoAdvanceNode.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_AUTO_ADVANCE.getCode());
            }
            if(NodeLabelConstants.autoReturnNode.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_AUTO_RETURN.getCode());
            }
            if(NodeLabelConstants.conditionReturnNode.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_CONDITION_RETURN.getCode());
            }
            if(NodeLabelConstants.conditionReturnStarterNode.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_CONDITION_RETURN_STARTER.getCode());
            }
            if(NodeLabelConstants.prevNodeAppointed.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               bpmnNodeVo.setIsPrevNodeAppointed(true);
            }
            if(NodeLabelConstants.pickCondition.getLabelValue().equals(nodeLabelVO.getLabelValue())){
               bpmnNodeVo.setIsPickCondition(true);
            }
        }
    }
}
