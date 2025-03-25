package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.ProxyClient;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.*;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.BpmnUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.util.StrUtils;
import org.openoa.base.vo.*;
import org.openoa.common.adaptor.bpmnelementadp.BpmnNodeFormatImpl;
import org.openoa.common.adaptor.bpmnelementadp.BpmnOptionalDuplicatesAdaptor;
import org.openoa.common.adaptor.bpmnelementadp.BpmnOptionalDuplicatesImpl;
import org.openoa.common.formatter.BpmnPersonnelFormat;
import org.openoa.engine.bpmnconf.adp.bpmnnodeadp.BpmnNodeAdaptor;
import org.openoa.engine.bpmnconf.adp.formatter.BpmnRemoveConfFormatFactory;
import org.openoa.engine.bpmnconf.adp.formatter.BpmnStartFormatFactory;
import org.openoa.engine.bpmnconf.common.NodeAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmProcessForward;
import org.openoa.engine.bpmnconf.confentity.BpmVariable;
import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.engine.bpmnconf.confentity.BpmnNode;
import org.openoa.engine.bpmnconf.service.impl.*;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.BpmnNodeParamTypeEnum.*;
import static org.openoa.base.constant.enums.DeduplicationTypeEnum.*;

@Service
@Slf4j
public class BpmnConfCommonServiceImpl {

    @Autowired
    private BpmnConfServiceImpl bpmnConfService;
    @Autowired
    private BpmnNodeServiceImpl nodeService;

    @Autowired
    private FormFactory formFactory;
    @Autowired
    private BpmnRemoveConfFormatFactory bpmnRemoveConfFormatFactory;
    @Autowired
    private BpmnStartFormatFactory bpmnStartFormatFactory;
    @Autowired
    private BpmnPersonnelFormat bpmnPersonnelFormat;
    @Autowired
    private BpmVariableServiceImpl bpmnVariableService;
    @Autowired
    private BpmVerifyInfoBizServiceImpl bpmVerifyInfoBizService;
    @Autowired
    private BpmProcessForwardServiceImpl processForwardService;
    @Autowired
    private NodeAdditionalInfoServiceImpl nodeAdditionalInfoService;
    /**
     * query conf by formCode
     *
     * @param formCode
     * @return
     */
    public BpmnConf getBpmnConfByFormCode(String formCode) {
        return Optional.ofNullable(bpmnConfService.getOne(new QueryWrapper<BpmnConf>()
                .eq("form_code", formCode)
                .eq("effective_status", 1)))
                .orElse(new BpmnConf());
    }

    /**
     * qeruy a list of conf by formCodes
     *
     * @param formCodes
     * @return
     */
    public List<BpmnConf> getBpmnConfByFormCodeBatch(List<String> formCodes) {
        return bpmnConfService.list(new QueryWrapper<BpmnConf>()
                .in("form_code", formCodes)
                .eq("effective_status", 1));
    }

    /**
     * //todo
     * update bpmn conf's extra information
     *
     * @param appId
     * @param bpmnType
     * @param bpmnCode
     */
    public void updateBpmnConfByCode(Integer appId, Integer bpmnType, Integer isAll, String bpmnCode) {
        bpmnConfService.update(BpmnConf
                        .builder()
                        .appId(appId)
                        .bpmnType(bpmnType)
                        .isAll(isAll)
                        .build(),
                new QueryWrapper<BpmnConf>()
                        .eq("bpmn_code", bpmnCode));
    }


    /**
     * process's submit process
     *
     * @param bpmnCode
     * @param bpmnStartConditions
     */
    public void startProcess(String bpmnCode, BpmnStartConditionsVo bpmnStartConditions) {

        //to query the process's config information
        BpmnConfVo bpmnConfVo = bpmnConfService.detail(bpmnCode);
        bpmnStartConditions.setPreview(false);

        // format process's floating direction,set assignees,assignees deduplication and remove some nodes on conditions
        BpmnConfVo confVo = getBpmnConfVo(bpmnStartConditions, bpmnConfVo);

        //to convert the process element information
        //set some basic information
        BpmnConfCommonVo bpmnConfCommonVo = BpmnConfCommonVo
                .builder()
                .bpmnCode(confVo.getBpmnCode())
                .formCode(confVo.getFormCode())
                .bpmnName(confVo.getBpmnName())
                .processNum(bpmnStartConditions.getProcessNum())
                .processName(bpmnConfVo.getBpmnName())
                .processDesc(bpmnStartConditions.getProcessDesc())
                //set out of process notice template
                .templateVos(bpmnConfVo.getTemplateVos())
                .build();

        //set view page's buttons information
        setViewPageButtons(bpmnConfVo, bpmnConfCommonVo);


        BpmnNodeFormatImpl bpmnNodeFormat = SpringBeanUtils.getBean(BpmnNodeFormatImpl.class);
        bpmnConfCommonVo.setElementList(bpmnNodeFormat
                .getBpmnConfCommonElementVoList(bpmnConfCommonVo, confVo.getNodes(), bpmnStartConditions));

        //5、set record variables
        BpmnInsertVariables bpmnInsertVariables = SpringBeanUtils.getBean(BpmnInsertVariablesImpl.class);
        if(!Boolean.TRUE.equals(bpmnStartConditions.getIsMigration())){
            bpmnInsertVariables.insertVariables(bpmnConfCommonVo, bpmnStartConditions);
        }

        //prepared and begin to start up a process
        BpmnCreateBpmnAndStart bpmnCreateBpmnAndStart = SpringBeanUtils.getBean(BpmnCreateBpmnAndStartImpl.class);
        bpmnCreateBpmnAndStart.createBpmnAndStart(bpmnConfCommonVo, bpmnStartConditions);
    }

    /**
     * set view page's buttons
     *
     * @param bpmnConfVo
     * @param bpmnConfCommonVo
     */
    private void setViewPageButtons(BpmnConfVo bpmnConfVo, BpmnConfCommonVo bpmnConfCommonVo) {
        bpmnConfCommonVo.setViewPageButtons(BpmnConfViewPageButtonVo
                .builder()
                .viewPageStart(bpmnConfVo.getViewPageButtons().getViewPageStart()
                        .stream()
                        .map(o -> BpmnConfCommonButtonPropertyVo
                                .builder()
                                .buttonType(o)
                                .buttonName(ButtonTypeEnum.getDescByCode(o))
                                .build())
                        .collect(Collectors.toList()))
                .viewPageOther(bpmnConfVo.getViewPageButtons().getViewPageOther()
                        .stream()
                        .map(o -> {
                            return BpmnConfCommonButtonPropertyVo
                                    .builder()
                                    .buttonType(o)
                                    .buttonName(ButtonTypeEnum.getDescByCode(o))
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .build());
    }

    /**
     * preview process information on the configuration page
     *
     * @param params
     * @return
     */
    public PreviewNode previewNode(String params) {
        return getPreviewNode(params, false);
    }

    /**
     * preview process's information on the start up process page
     *
     * @param params
     * @return
     */
    public PreviewNode startPagePreviewNode(String params) {
        return getPreviewNode(params, true);
    }

    public PreviewNode taskPagePreviewNode(String params) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        String processNumber = jsonObject.getString("processNumber");
        Boolean isLowCodeFlow = jsonObject.getBoolean("isLowCodeFlow");

        QueryWrapper<BpmVariable> wrapper = new QueryWrapper<>();
        wrapper.eq("process_num", processNumber);
        BpmVariable bpmnVariable = bpmnVariableService.getOne(wrapper);

        String processStartConditions = bpmnVariable.getProcessStartConditions();
        JSONObject objectStart = JSON.parseObject(processStartConditions);
        objectStart.put("bpmnCode", bpmnVariable.getBpmnCode());
        objectStart.put("isLowCodeFlow",Boolean.TRUE.equals(isLowCodeFlow));
        objectStart.put("processNumber",processNumber);
        return getPreviewNode(objectStart.toString(), false);
    }

    /**
     * start pages's preview on smart devices(todo not implemented yet)
     *
     * @param params
     * @return
     */
    public List<BpmVerifyInfoVo> appStartPagePreviewNode(String params) {
        List<BpmVerifyInfoVo> bpmVerifyInfoVos = Lists.newArrayList();

        //set start up node info
        bpmVerifyInfoVos.add(BpmVerifyInfoVo
                .builder()
                .taskName("发起")
                .build());

        //get previewNodes
        PreviewNode previewNode = getPreviewNode(params, true);

        //if the NodeList is empty then do nothing
        if (!ObjectUtils.isEmpty(previewNode.getBpmnNodeList())) {
            //nodelist
            List<BpmnNodeVo> bpmnNodeList = previewNode.getBpmnNodeList();
            //get the start node
            BpmnNodeVo startNode = bpmnNodeList.stream().filter(o -> o.getNodeType().equals(NodeTypeEnum.NODE_TYPE_START.getCode())).findFirst().orElse(null);
            //if can not get the start page node then error should thrown
            if (ObjectUtils.isEmpty(startNode)) {
                throw new JiMuBizException("can't find out the start node！");
            }
            //添加节点
            addBpmVerifyInfoVo(startNode, bpmnNodeList, bpmVerifyInfoVos);
        }

        //add the process end node
        bpmVerifyInfoVos.add(BpmVerifyInfoVo
                .builder()
                .taskName("流程结束")
                .build());

        return bpmVerifyInfoVos;
    }

    /**
     * 添加节点
     *
     * @param bpmnNodeVo
     * @param bpmnNodeList
     * @param bpmVerifyInfoVos
     */
    private void addBpmVerifyInfoVo(BpmnNodeVo bpmnNodeVo, List<BpmnNodeVo> bpmnNodeList, List<BpmVerifyInfoVo> bpmVerifyInfoVos) {
        Map<String, String> verifyMap = getVerifyMap(bpmnNodeVo);
        bpmVerifyInfoVos.add(BpmVerifyInfoVo
                .builder()
                .taskName(verifyMap.get("taskName"))
                .verifyUserName(verifyMap.get("verifyUserName"))
                .build());
        if (!ObjectUtils.isEmpty(bpmnNodeVo.getNodeTo())) {
            BpmnNodeVo nextBpmnNodeVo = bpmnNodeList.stream().filter(o -> o.getNodeFrom().equals(bpmnNodeVo.getNodeId())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(nextBpmnNodeVo)) {
                addBpmVerifyInfoVo(nextBpmnNodeVo, bpmnNodeList, bpmVerifyInfoVos);
            }
        }
    }

    /**
     * query node's assignee and taskName
     *
     * @param bpmnNodeVo
     * @return
     */
    private Map<String, String> getVerifyMap(BpmnNodeVo bpmnNodeVo) {
        Map<String, String> verifyMap = Maps.newHashMap();
        String verifyUserName = StringUtils.EMPTY;
        String taskName = bpmnNodeVo.getNodePropertyName();
        if (bpmnNodeVo.getParams().getParamType().equals(BPMN_NODE_PARAM_SINGLE.getCode())) {
            //single approver
            verifyUserName = bpmnNodeVo.getParams().getAssignee().getAssigneeName();
            taskName = Optional.ofNullable(taskName)
                    .orElse(bpmnNodeVo.getParams().getAssignee().getElementName());
        } else if (bpmnNodeVo.getParams().getParamType().equals(BPMN_NODE_PARAM_MULTIPLAYER.getCode()) ||
                bpmnNodeVo.getParams().getParamType().equals(BPMN_NODE_PARAM_MULTIPLAYER_SORT.getCode())) {
            //multi user or multi user in sequence
            List<BpmnNodeParamsAssigneeVo> assigneeList = bpmnNodeVo.getParams().getAssigneeList();
            verifyUserName = StringUtils.join(assigneeList
                    .stream()
                    .map(BpmnNodeParamsAssigneeVo::getAssigneeName)
                    .collect(Collectors.toList()), ",");
            taskName = Optional.ofNullable(taskName)
                    .orElse(StringUtils.join(assigneeList
                            .stream()
                            .map(BpmnNodeParamsAssigneeVo::getElementName)
                            .distinct()
                            .collect(Collectors.toList()), ","));
        }
        verifyMap.put("verifyUserName", verifyUserName);
        verifyMap.put("taskName", taskName);
        return verifyMap;
    }

    /**
     * preview the process's information
     *
     * @param params
     * @return
     */
    private PreviewNode getPreviewNode(String params, Boolean isStartPagePreview) {

        BusinessDataVo dataVo = JSON.parseObject(params, BusinessDataVo.class);

        BpmnConfVo detail;
        if (isStartPagePreview) {
            detail = bpmnConfService.detailByFormCode(dataVo.getFormCode());
        } else {
            detail = bpmnConfService.detail(dataVo.getBpmnCode());
        }

        JSONObject object = JSON.parseObject(params);
        object.put("formCode", detail.getFormCode());

        BusinessDataVo vo = formFactory.dataFormConversion(JSON.toJSONString(object),null);
        vo.setIsOutSideAccessProc(Objects.equals(1,detail.getIsOutSideProcess()));
        vo.setIsLowCodeFlow(detail.getIsLowCodeFlow());
        vo.setBpmnConfVo(detail);
        //set a flag to indicate whether is a start page preview
        vo.setIsStartPagePreview(isStartPagePreview);

        BpmnStartConditionsExtendVo bpmnStartConditionsExtendVo = new BpmnStartConditionsExtendVo();
        bpmnStartConditionsExtendVo.setLowCodeFlow(true);
        //set start user information
        String startUserId;
        if (isStartPagePreview) {
            if (dataVo.getIsOutSideAccessProc()) {
                startUserId = dataVo.getStartUserId();
            } else {
                startUserId = SecurityUtils.getLogInEmpIdStr();
                vo.setStartUserId(startUserId);
            }

        } else {
            startUserId = vo.getStartUserId();
            if (ObjectUtils.isEmpty(startUserId)){
                vo.setStartUserId(SecurityUtils.getLogInEmpNameSafe());
            }
        }
        if (!ObjectUtils.isEmpty(startUserId)) {
            bpmnStartConditionsExtendVo.setStartUserId(startUserId);
            //todo set startcondition
        }

        BpmnStartConditionsVo bpmnStartConditionsVo = new BpmnStartConditionsVo();
        if(dataVo.getIsOutSideAccessProc()){
            //set conditions before preview
            bpmnStartConditionsVo.setTemplateMarkIds(dataVo.getTemplateMarkIds());
            bpmnStartConditionsVo.setOutSideType(dataVo.getOutSideType());
            //set embedded nodes
            bpmnStartConditionsVo.setEmbedNodes(dataVo.getEmbedNodes());
            //set flag to indicate it is a outside process
            bpmnStartConditionsVo.setIsOutSideAccessProc(true);
            bpmnStartConditionsVo.setStartUserId(dataVo.getStartUserId());
        }else{
            //call business logic to set start up preview conditions
            bpmnStartConditionsVo = formFactory.getFormAdaptor(vo).previewSetCondition(vo);
        }


        BeanUtils.copyProperties(bpmnStartConditionsExtendVo, bpmnStartConditionsVo, StrUtils.getNullPropertyNames(bpmnStartConditionsExtendVo));
        bpmnStartConditionsVo.setApproversList(dataVo.getApproversList());
        bpmnStartConditionsVo.setPreview(true);
        BpmnConfVo bpmnConfVo = getBpmnConfVo(bpmnStartConditionsVo, detail);
        PreviewNode previewNode = new PreviewNode();
        previewNode.setBpmnName(detail.getBpmnName());
        previewNode.setFormCode(detail.getFormCode());
        previewNode.setBpmnNodeList(setNodeFromV2(bpmnConfVo.getNodes()));
        previewNode.setDeduplicationType(bpmnConfVo.getDeduplicationType());
        previewNode.setDeduplicationTypeName(DeduplicationTypeEnum.getDescByCode(bpmnConfVo.getDeduplicationType()));

        String currentNodeIdStr= bpmVerifyInfoBizService.findCurrentNodeIds(vo.getProcessNumber());
        previewNode.setCurrentNodeId(currentNodeIdStr);

        List<String> currentNodeIds = Lists.newArrayList(currentNodeIdStr.split(","));
        List<BpmnNodeVo> bpmnNodeList = previewNode.getBpmnNodeList();
        Map<String, BpmnNodeVo> bpmnNodeVoMap = bpmnNodeList.stream().collect(Collectors.toMap(BpmnNodeVo::getNodeId, b -> b, (v1, v2) -> v1));
        List<String> nodeToResults=new ArrayList<>();
        processNodeToRecursively(currentNodeIds,bpmnNodeVoMap,nodeToResults);
        previewNode.setAfterNodeIds(nodeToResults);
        List<String> nodeFromResults=new ArrayList<>();
        Set<String> allNodeIds = bpmnNodeVoMap.keySet();
        allNodeIds.stream().filter(o -> !nodeToResults.contains(o)&&!currentNodeIds.contains(o)).forEach(nodeFromResults::add);
        previewNode.setBeforeNodeIds(nodeFromResults);
        return previewNode;

    }
    public boolean migrationCheckConditionsChange(BusinessDataVo vo) {
        BpmnConf bpmnConf = this.bpmnConfService.getOne(new QueryWrapper<BpmnConf>()
                .eq("bpmn_code", vo.getBpmnCode()));
        if(bpmnConf==null){
            throw new JiMuBizException("未找到对应的 bpmnConf 记录");
        }
        BpmnConfVo bpmnConfVo = new BpmnConfVo();
        BeanUtils.copyProperties(bpmnConf, bpmnConfVo);
        FormOperationAdaptor formAdapter = formFactory.getFormAdaptor(vo);
        BpmnStartConditionsVo bpmnStartConditionsVo = formAdapter.launchParameters(vo);
        bpmnStartConditionsVo.setPreview(true);
        bpmnStartConditionsVo.setProcessNum(vo.getProcessNumber());
        bpmnStartConditionsVo.setIsMigration(true);
        List<BpmnNode> bpmnNodes = nodeService.getBaseMapper().selectList(new QueryWrapper<BpmnNode>()
                .eq("conf_id", bpmnConf.getId())
                .eq("is_del", 0));
        Map<Long, List<String>> bpmnNodeToMap = nodeAdditionalInfoService.getBpmnNodeToMap(bpmnNodes.stream().map(BpmnNode::getId).collect(Collectors.toList()));
        // 将查询到的 bpmnNodes 转换为 bpmnNodeVo 列表
        List<BpmnNodeVo> bpmnNodeVoList = bpmnNodes.stream()
            .map(bpmnNode -> {
                BpmnNodeVo bpmnNodeVo = new BpmnNodeVo();
                BeanUtils.copyProperties(bpmnNode, bpmnNodeVo);
                List<String> nodeToIds = bpmnNodeToMap.get(bpmnNode.getId());
                bpmnNodeVo.setNodeTo(nodeToIds);
                BpmnNodeAdaptor bpmnNodeAdaptor = nodeAdditionalInfoService.getBpmnNodeAdaptor(NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(bpmnNodeVo));
                if(bpmnNodeAdaptor==null){
                    return bpmnNodeVo;
                }
                //use adaptor to format nodevo
                bpmnNodeAdaptor.formatToBpmnNodeVo(bpmnNodeVo);
                return bpmnNodeVo;
            })
            .collect(Collectors.toList());
        bpmnConfVo.setNodes(bpmnNodeVoList);
        try {
            bpmnStartFormatFactory.formatBpmnConf(bpmnConfVo,bpmnStartConditionsVo);
        }catch (Exception ex){
            if(ex instanceof JiMuBizException){
                String code = ((JiMuBizException) ex).getCode();
                if(StringConstants.CONDITION_CHANGED.equals(code)){
                    return true;
                }
                throw ex;
            }
        }
        return false;
    }

    private void processNodeToRecursively(List<String>currentNodeIds, Map<String, BpmnNodeVo> bpmnNodeVoMap, List<String> results){
        if(currentNodeIds.isEmpty()){
            return;
        }
        for (String currentNodeId : currentNodeIds) {
            BpmnNodeVo bpmnNodeVo = bpmnNodeVoMap.get(currentNodeId);
            if (bpmnNodeVo != null) {
                List<String> nodeTo = bpmnNodeVo.getNodeTo();
                if(!CollectionUtils.isEmpty(nodeTo)){
                    results.addAll(nodeTo);
                    processNodeToRecursively(nodeTo,bpmnNodeVoMap,results);
                }
            }
        }
    }
    /**
     * set node from information
     *
     * @param nodeList
     * @return
     */
    public List<BpmnNodeVo> setNodeFrom(List<BpmnNodeVo> nodeList) {
        Map<String, BpmnNodeVo> map = new HashMap<>(nodeList.size());
        BpmnNodeVo startNode = getNodeMapAndStartNode(nodeList, map);
        List<BpmnNodeVo> resultList = new ArrayList<>();
        BpmnNodeVo lastNode = new BpmnNodeVo();
        lastNode.setNodeId("");
        BpmnNodeVo nowNode = startNode;
        if (nowNode != null) {
            while (true) {
                if (BPMN_NODE_PARAM_SINGLE.getCode().equals(nowNode.getParams().getParamType())) {
                    nowNode.getParams().setAssigneeList(Arrays.asList(nowNode.getParams().getAssignee()));
                }
                nowNode.setNodePropertyName(NodePropertyEnum.getDescByCode(nowNode.getNodeProperty()));
                if (StringUtils.isBlank(nowNode.getParams().getNodeTo())) {
                    nowNode.setNodeFrom(lastNode.getNodeId());
                    resultList.add(nowNode);
                    break;
                }
                if (resultList.size() > nodeList.size()) {
                    log.info("error occur while set nodeFrom info,nodeList:{}", JSON.toJSONString(nodeList));
                    throw new JiMuBizException("999", "nodeId数据错误");
                }
                nowNode.setNodeFrom(lastNode.getNodeId());
                resultList.add(nowNode);
                lastNode = nowNode;
                nowNode = map.get(nowNode.getParams().getNodeTo());
            }
        }
        return resultList;
    }

    public List<BpmnNodeVo> setNodeFromV2(List<BpmnNodeVo> nodeList) {
        Map<String, BpmnNodeVo> map = new HashMap<>(nodeList.size());
        BpmnNodeVo startNode = getNodeMapAndStartNode(nodeList, map);
        List<BpmnNodeVo> resultList = new ArrayList<>();
        BpmnNodeVo lastNode = new BpmnNodeVo();
        lastNode.setNodeId("");
        BpmnNodeVo nowNode = startNode;
        if (nowNode != null) {
            while (true) {
                if (NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode().equals(nowNode.getNodeType())) {
                    BpmnNodeVo aggregationNode = BpmnUtils.getAggregationNode(nowNode, nodeList);
                    treatParallelGateWayRecursively(nowNode,lastNode, aggregationNode, map, resultList,new HashSet<>());

                    nowNode = map.get(aggregationNode.getParams().getNodeTo());
                    lastNode=aggregationNode;
                }
                if (nowNode == null) {
                    break;
                }
                if(!NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode().equals(nowNode.getNodeType())){
                    if (BPMN_NODE_PARAM_SINGLE.getCode().equals(nowNode.getParams().getParamType())) {
                        nowNode.getParams().setAssigneeList(Collections.singletonList(nowNode.getParams().getAssignee()));
                    }
                    nowNode.setNodePropertyName(NodePropertyEnum.getDescByCode(nowNode.getNodeProperty()));
                    if (StringUtils.isBlank(nowNode.getParams().getNodeTo())) {
                        nowNode.setNodeFrom(lastNode.getNodeId());
                        resultList.add(nowNode);
                        break;
                    }
                    if (resultList.size() > nodeList.size()) {
                        log.info("error occur while set nodeFrom info,nodeList:{}", JSON.toJSONString(nodeList));
                        throw new JiMuBizException("999", "nodeId数据错误");
                    }
                    nowNode.setNodeFrom(lastNode.getNodeId());
                    resultList.add(nowNode);
                    lastNode = nowNode;
                    nowNode = map.get(nowNode.getParams().getNodeTo());
                }
            }

        }
        return resultList;

    }

    private void treatParallelGateWayRecursively(BpmnNodeVo outerMostParallelGatewayNode, BpmnNodeVo itsPrevNode,
                                                 BpmnNodeVo itsAggregationNode, Map<String, BpmnNodeVo> mapNodes, List<BpmnNodeVo> results,
                                                 Set<String> alreadyProcessNodeIds) {

        String aggregationNodeNodeId = itsAggregationNode.getNodeId();
        List<String> nodeTos = outerMostParallelGatewayNode.getNodeTo();
        outerMostParallelGatewayNode.setNodeFrom(itsPrevNode.getNodeId());
        itsAggregationNode.setNodeFrom(outerMostParallelGatewayNode.getNodeId());
        results.add(outerMostParallelGatewayNode);
        results.add(itsAggregationNode);
        alreadyProcessNodeIds.add(outerMostParallelGatewayNode.getNodeId());
        alreadyProcessNodeIds.add(itsAggregationNode.getNodeId());
        for (String nodeTo : nodeTos) {
            BpmnNodeVo prevNode = outerMostParallelGatewayNode;
            BpmnNodeVo currentNodeVo = mapNodes.get(nodeTo);

            for (BpmnNodeVo nodeVo = currentNodeVo; nodeVo != null && !nodeVo.getNodeId().equals(aggregationNodeNodeId); nodeVo = mapNodes.get(nodeVo.getParams().getNodeTo())) {
                if(alreadyProcessNodeIds.contains(nodeVo.getNodeId())){
                    continue;
                }

                if (NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode().equals(nodeVo.getNodeType())) {
                    BpmnNodeVo aggregationNode = BpmnUtils.getAggregationNode(nodeVo, mapNodes.values());
                    treatParallelGateWayRecursively(nodeVo, prevNode,aggregationNode, mapNodes, results, alreadyProcessNodeIds);
                }

                if (BPMN_NODE_PARAM_SINGLE.getCode().equals(nodeVo.getParams().getParamType())) {
                    nodeVo.getParams().setAssigneeList(Collections.singletonList(nodeVo.getParams().getAssignee()));
                }
                nodeVo.setNodePropertyName(NodePropertyEnum.getDescByCode(nodeVo.getNodeProperty()));
                if (StringUtils.isBlank(nodeVo.getParams().getNodeTo())) {
                    nodeVo.setNodeFrom(prevNode.getNodeId());
                    results.add(nodeVo);
                    alreadyProcessNodeIds.add(nodeVo.getNodeId());
                    break;
                }
                if (results.size() > mapNodes.values().size()) {
                    log.info("error occur while set nodeFrom info,nodeList:{}", JSON.toJSONString(mapNodes.values()));
                    throw new JiMuBizException("999", "nodeId数据错误");
                }
                nodeVo.setNodeFrom(prevNode.getNodeId());
                results.add(nodeVo);
                alreadyProcessNodeIds.add(nodeVo.getNodeId());
                prevNode = nodeVo;
            }
        }
    }


    /**
     * iterate the nodelist and use a map structure to store them
     *
     * @param nodeList
     * @param nodeIdMapNode
     * @return
     */
    private static BpmnNodeVo getNodeMapAndStartNode(List<BpmnNodeVo> nodeList, Map<String, BpmnNodeVo> nodeIdMapNode) {
        BpmnNodeVo startNode = null;
        boolean existEnd = false;
        for (BpmnNodeVo bpmnNodeVo : nodeList) {
            nodeIdMapNode.put(bpmnNodeVo.getNodeId(), bpmnNodeVo);
            if (NodeTypeEnum.NODE_TYPE_START.getCode().equals(bpmnNodeVo.getNodeType())) {
                if (startNode == null) {
                    startNode = bpmnNodeVo;
                } else {
                    log.info("has more than one start up user while previewing the process,nodeId:{}", bpmnNodeVo.getNodeId());
                    throw new JiMuBizException("999", "has more than 1 start up node");
                }
            }
            if (bpmnNodeVo.getParams() == null || StringUtils.isBlank(bpmnNodeVo.getParams().getNodeTo())) {
                existEnd = true;
            }
        }
        if (!existEnd) {
            log.info("has no end node while previewing the process,nodeList:{}", JSON.toJSONString(nodeList));
            throw new JiMuBizException("has not end node while previewing the process");
        }
        return startNode;
    }

    /**
     * format the floating direction,set assignees and deduplication
     *
     * @param bpmnStartConditions
     * @param bpmnConfVo
     * @return
     */
    private BpmnConfVo getBpmnConfVo(BpmnStartConditionsVo bpmnStartConditions, BpmnConfVo bpmnConfVo) {


        //1. Format the process,filter it by condition
        bpmnStartFormatFactory.formatBpmnConf(bpmnConfVo,bpmnStartConditions);


        //2、set consignees information and finally determine the flow's direction
        bpmnPersonnelFormat.formatPersonnelsConf(bpmnConfVo, bpmnStartConditions);


        //3. to determine whether it is necessary to deduplication
        if (!bpmnConfVo.getDeduplicationType().equals(DEDUPLICATION_TYPE_NULL.getCode())) {
            BpmnDeduplicationFormat bpmnDeduplicationFormat = SpringBeanUtils.getBean(BpmnDeduplicationFormatImpl.class);
            if (bpmnConfVo.getDeduplicationType().equals(DEDUPLICATION_TYPE_FORWARD.getCode())) {
                //deduplication forward
               bpmnDeduplicationFormat.forwardDeduplication(bpmnConfVo, bpmnStartConditions);
            } else if (bpmnConfVo.getDeduplicationType().equals(DEDUPLICATION_TYPE_BACKWARD.getCode())) {
                //deduplication backword
                bpmnDeduplicationFormat.backwardDeduplication(bpmnConfVo, bpmnStartConditions);
            }
        }

        //self chosen module deduplication
        BpmnOptionalDuplicatesAdaptor bpmnOptionalDuplicates = SpringBeanUtils.getBean(BpmnOptionalDuplicatesImpl.class);
        bpmnOptionalDuplicates.optionalDuplicates(bpmnConfVo, bpmnStartConditions);

        //4、format the nodes by pipelines
        bpmnRemoveConfFormatFactory.removeBpmnConf(bpmnConfVo,bpmnStartConditions);
        if(BpmnConfFlagsEnum.hasFlag(bpmnConfVo.getExtraFlags(),BpmnConfFlagsEnum.HAS_COPY)){
            for (BpmnNodeVo node : bpmnConfVo.getNodes()) {
                //copy nodes have already removed,and its forwarded list assigned to its next node
                if (!NodeTypeEnum.NODE_TYPE_COPY.getCode().equals(node.getNodeType())&&!CollectionUtils.isEmpty(node.getEmpToForwardList())) {
                    List<BaseIdTranStruVo> empToForwardList = node.getEmpToForwardList();
                    List<BpmProcessForward> processForwardList=new ArrayList<>(node.getEmpToForwardList().size());
                    boolean lastNodeForward = node.isLastNodeForward();
                    for (BaseIdTranStruVo baseIdTranStruVo : empToForwardList) {
                        BpmProcessForward bpmProcessForward = BpmProcessForward.builder()
                                .createTime(new Date())
                                .createUserId(SecurityUtils.getLogInEmpId())
                                .forwardUserId(baseIdTranStruVo.getId())
                                .ForwardUserName(baseIdTranStruVo.getName())
                                .processNumber(bpmnStartConditions.getProcessNum())
                                .nodeId(String.valueOf(node.getId()))
                                .isDel(1)//it is invalid at first,then set it to be valid
                                //at this moment,we can not get procInstId,update it later
                                .build();
                        if(lastNodeForward){
                            bpmProcessForward.setNodeId(StringConstants.LASTNODE_COPY);
                        }
                        processForwardList.add(bpmProcessForward);
                    }
                    processForwardService.saveBatch(processForwardList);
                }
            }
        }
        return bpmnConfVo;
    }

    /**
     * query all process that all user can create conf
     *
     * @return
     */
    public List<BpmnConf> getIsAllConfs() {
        return bpmnConfService.getBaseMapper().selectList(new QueryWrapper<BpmnConf>()
                .eq("is_all", 1)
                .eq("effective_status", 1)
                .eq("is_del", 0));
    }

}
