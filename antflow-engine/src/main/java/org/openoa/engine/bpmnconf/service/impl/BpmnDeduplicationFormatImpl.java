package org.openoa.engine.bpmnconf.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnDeduplicationFormat;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ description: deduplication
 */
@Component
public class BpmnDeduplicationFormatImpl implements BpmnDeduplicationFormat {

    /**
     * deduplication forward
     *
     * @param bpmnConfVo          which to deduplication
     * @param bpmnStartConditions conditions
     * @return deduplated vo
     */
    @Override
    public BpmnConfVo forwardDeduplication(BpmnConfVo bpmnConfVo, BpmnStartConditionsVo bpmnStartConditions) {

        //start node
        String startNodeId = null;

        Map<String, BpmnNodeVo> mapNodes = Maps.newHashMap();
        boolean containsParallelGateway=false;
        for (BpmnNodeVo vo : bpmnConfVo.getNodes()) {
            mapNodes.put(vo.getNodeId(), vo);
            if (!ObjectUtils.isEmpty(vo.getNodeType()) && (vo.getNodeType().equals(NodeTypeEnum.NODE_TYPE_START.getCode()))) {
                startNodeId = vo.getNodeId();
            }
            if(NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode().equals(vo.getNodeType())
                    ||(NodeTypeEnum.NODE_TYPE_GATEWAY.getCode().equals(vo.getNodeType())&&Boolean.TRUE.equals(vo.getIsParallel()))){
                containsParallelGateway=true;
                break;
            }
        }
        if(containsParallelGateway){
            return backwardDeduplication(bpmnConfVo, bpmnStartConditions);
        }
        //start node's assignee
        String initiator = mapNodes.get(startNodeId).getParams().getAssignee().getAssignee();


        BpmnNodeVo bpmnNodeVo = mapNodes.get(startNodeId);


        List<BpmnNodeVo> nodeVoList = new ArrayList<>();


        while (!ObjectUtils.isEmpty(bpmnNodeVo.getParams().getNodeTo())) {


            bpmnNodeVo = mapNodes.get(bpmnNodeVo.getParams().getNodeTo());

            //deduplication on single node
            if (bpmnNodeVo.getParams().getParamType().equals(1)) {

                singlePlayerNodeDeduplication(bpmnNodeVo,new HashSet<>(), new ArrayList<>(Collections.singletonList(initiator)));
                nodeVoList.add(bpmnNodeVo);
                continue;
            }

            //deduplication on multiplayer node
            if (bpmnNodeVo.getParams().getParamType().equals(2)) {
                //multi assignee node deduplication
                multiPlayerNodeDeduplication(bpmnNodeVo, new HashSet<>(),new ArrayList<>(Collections.singletonList(initiator)), false);
                nodeVoList.add(bpmnNodeVo);
            }

        }


        Collections.reverse(nodeVoList);


        List<String> approverList = new ArrayList<>();

        //begin to deduplication
        for (BpmnNodeVo bpmnNode : nodeVoList) {


            if (bpmnNode.getParams().getParamType().equals(1)) {

                singlePlayerNodeDeduplication(bpmnNode,new HashSet<>(), approverList);
                continue;
            }


            if (bpmnNode.getParams().getParamType().equals(2)) {

                Collections.reverse(bpmnNode.getParams().getAssigneeList());

                multiPlayerNodeDeduplication(bpmnNode,new HashSet<>(), approverList, true);

                Collections.reverse(bpmnNode.getParams().getAssigneeList());
            }

        }

        return bpmnConfVo;

    }

    /**
     * backword deduplication,it is just like forward deduplication
     *
     * @param bpmnConfVo          which to deduplication
     * @param bpmnStartConditions conditions
     * @return
     */
    @Override
    public BpmnConfVo backwardDeduplication(BpmnConfVo bpmnConfVo, BpmnStartConditionsVo bpmnStartConditions) {


        List<String> approverList = new ArrayList<>();


        String startNodeId = null;

        Map<String, BpmnNodeVo> mapNodes = new HashMap<>();
        for (BpmnNodeVo vo : bpmnConfVo.getNodes()) {
            mapNodes.put(vo.getNodeId(), vo);
            if (!ObjectUtils.isEmpty(vo.getNodeType()) && (vo.getNodeType().equals(NodeTypeEnum.NODE_TYPE_START.getCode()))) {
                startNodeId = vo.getNodeId();
            }
        }


        String initiator = mapNodes.get(startNodeId).getParams().getAssignee().getAssignee();
        approverList.add(initiator);


        BpmnNodeVo bpmnNodeVo = mapNodes.get(startNodeId);

        // 使用递归处理并行网关
        processNodeRecursively(bpmnNodeVo,new HashSet<>(), mapNodes, approverList);

        return bpmnConfVo;

    }
    /**
     * 递归处理节点，如果是并行网关，则递归处理所有出口节点
     *
     * @param bpmnNodeVo 当前处理的节点
     * @param mapNodes   节点映射表
     * @param approverList 已处理的审批人列表
     */
    private void processNodeRecursively(BpmnNodeVo bpmnNodeVo,Set<String> alreadyProcessedNodes, Map<String, BpmnNodeVo> mapNodes, List<String> approverList) {

        String nextId=null;
        do {


            if(NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode().equals(bpmnNodeVo.getNodeType())){
                List<String> parallelNodeToIds = bpmnNodeVo.getNodeTo();
                for (String parallelNodeToId : parallelNodeToIds) {
                    BpmnNodeVo parallelNodeTo = mapNodes.get(parallelNodeToId);
                    if (parallelNodeTo != null) {
                        processNodeRecursively(parallelNodeTo,alreadyProcessedNodes, mapNodes, approverList);
                    }
                }

            }


            // 处理单节点去重
            if (bpmnNodeVo.getParams().getParamType()!=null&&bpmnNodeVo.getParams().getParamType().equals(1)) {
                singlePlayerNodeDeduplication(bpmnNodeVo,alreadyProcessedNodes, approverList);
            }else if (bpmnNodeVo.getParams().getParamType()!=null&&bpmnNodeVo.getParams().getParamType().equals(2)) {
                multiPlayerNodeDeduplication(bpmnNodeVo,alreadyProcessedNodes, approverList, true);
            }

            String nodeTo = getNodeTo(bpmnNodeVo);

            if (Strings.isNullOrEmpty(nodeTo)) {
                return;
            }
            bpmnNodeVo= getNextNodeVo(mapNodes.values(), nodeTo);
            nextId=bpmnNodeVo.getNodeId();
        }while (!StringUtils.isEmpty(nextId));

    }
    /**
     * get next node
     *
     * @param nodes
     * @param nodeTo
     * @return
     */
    private BpmnNodeVo getNextNodeVo(Collection<BpmnNodeVo> nodes, String nodeTo) {
        List<BpmnNodeVo> nextNodeVo = nodes
                .stream()
                .filter(o -> o.getNodeId().equals(nodeTo))
                .collect(Collectors.toList());

        if (ObjectUtils.isEmpty(nextNodeVo)) {
            throw new JiMuBizException("未找到下一节点流程发起失败");
        }
        return nextNodeVo.get(0);
    }
    private String getNodeTo(BpmnNodeVo nodeVo) {
        return Optional.ofNullable(nodeVo.getParams()).map(BpmnNodeParamsVo::getNodeTo).orElse(null);
    }
    /**
     * single node deduplication
     *
     * @param bpmnNodeVo
     * @param approverList
     */
    private void singlePlayerNodeDeduplication(BpmnNodeVo bpmnNodeVo,Set<String> alreadyProcessedNods, List<String> approverList) {

        if(bpmnNodeVo.isDeduplicationExclude()||alreadyProcessedNods.contains(bpmnNodeVo.getNodeId())){
            return;
        }
        //to check whether it has already been deduplicated,if so,then skip to process it
        if (bpmnNodeVo.getParams().getIsNodeDeduplication() == 1) {
            return;
        }

        //get the assignee's on single node
        BpmnNodeParamsAssigneeVo assignee = bpmnNodeVo.getParams().getAssignee();
        if (approverList.contains(assignee.getAssignee())) {
            //if it is already in exist,the deduplicate it
            assignee.setIsDeduplication(1);
            bpmnNodeVo.getParams().setIsNodeDeduplication(1);
        } else {
            //if it is not duplicated,then add it in to the approverlist
            approverList.add(assignee.getAssignee());
        }
        alreadyProcessedNods.add(bpmnNodeVo.getNodeId());
    }

    /**
     * multiplayer node deduplication
     *
     * @param bpmnNodeVo   node
     * @param approverList alredy iterated approver list
     * @param flag         true forward,false backward
     */
    private void multiPlayerNodeDeduplication(BpmnNodeVo bpmnNodeVo,Set<String> alreadyProcessedNods, List<String> approverList, Boolean flag) {


        if(bpmnNodeVo.isDeduplicationExclude()||alreadyProcessedNods.contains(bpmnNodeVo.getNodeId())){
            return;
        }
        if (bpmnNodeVo.getParams().getIsNodeDeduplication() == 1) {
            return;
        }


        List<BpmnNodeParamsAssigneeVo> assigneeList = bpmnNodeVo.getParams().getAssigneeList();
        //set node deduplication as default behavior
        int isNodeDeduplication = 1;
        //iterate the assignees' node to deduplicate
        for (BpmnNodeParamsAssigneeVo assignee : assigneeList) {

            //if already deduplicated,then continue
            if (assignee.getIsDeduplication().equals(1)) {
                continue;
            }

            if (approverList.contains(assignee.getAssignee())) {

                assignee.setIsDeduplication(1);
            } else {

                if (flag) {

                    approverList.add(assignee.getAssignee());
                }

                isNodeDeduplication = 0;
            }

        }
        bpmnNodeVo.getParams().setIsNodeDeduplication(isNodeDeduplication);
        alreadyProcessedNods.add(bpmnNodeVo.getNodeId());
    }

}
