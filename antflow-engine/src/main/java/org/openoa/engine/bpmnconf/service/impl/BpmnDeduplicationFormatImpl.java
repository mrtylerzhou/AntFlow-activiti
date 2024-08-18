package org.openoa.engine.bpmnconf.service.impl;

import com.google.common.collect.Maps;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;

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
        for (BpmnNodeVo vo : bpmnConfVo.getNodes()) {
            mapNodes.put(vo.getNodeId(), vo);
            if (!ObjectUtils.isEmpty(vo.getNodeType()) && (vo.getNodeType().equals(NodeTypeEnum.NODE_TYPE_START.getCode()))) {
                startNodeId = vo.getNodeId();
            }
        }

        //start node's assignee
        String initiator = mapNodes.get(startNodeId).getParams().getAssignee().getAssignee();


        BpmnNodeVo bpmnNodeVo = mapNodes.get(startNodeId);


        List<BpmnNodeVo> nodeVoList = new ArrayList<>();


        while (!ObjectUtils.isEmpty(bpmnNodeVo.getParams().getNodeTo())) {


            bpmnNodeVo = mapNodes.get(bpmnNodeVo.getParams().getNodeTo());

            //deduplication on single node
            if (bpmnNodeVo.getParams().getParamType().equals(1)) {

                singlePlayerNodeDeduplication(bpmnNodeVo, new ArrayList<>(Collections.singletonList(initiator)));
                nodeVoList.add(bpmnNodeVo);
                continue;
            }

            //deduplication on multiplayer node
            if (bpmnNodeVo.getParams().getParamType().equals(2)) {
                //multi assignee node deduplication
                multiPlayerNodeDeduplication(bpmnNodeVo, new ArrayList<>(Collections.singletonList(initiator)), false);
                nodeVoList.add(bpmnNodeVo);
            }

        }


        Collections.reverse(nodeVoList);


        List<String> approverList = new ArrayList<>();

        //begin to deduplication
        for (BpmnNodeVo bpmnNode : nodeVoList) {


            if (bpmnNode.getParams().getParamType().equals(1)) {

                singlePlayerNodeDeduplication(bpmnNode, approverList);
                continue;
            }


            if (bpmnNode.getParams().getParamType().equals(2)) {

                Collections.reverse(bpmnNode.getParams().getAssigneeList());

                multiPlayerNodeDeduplication(bpmnNode, approverList, true);

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


        while (!ObjectUtils.isEmpty(bpmnNodeVo.getParams().getNodeTo())) {


            bpmnNodeVo = mapNodes.get(bpmnNodeVo.getParams().getNodeTo());


            if (bpmnNodeVo.getParams().getParamType().equals(1)) {

                singlePlayerNodeDeduplication(bpmnNodeVo, approverList);
                continue;
            }


            if (bpmnNodeVo.getParams().getParamType().equals(2)) {

                multiPlayerNodeDeduplication(bpmnNodeVo, approverList, true);
            }

        }

        return bpmnConfVo;

    }

    /**
     * single node deduplication
     *
     * @param bpmnNodeVo
     * @param approverList
     */
    private void singlePlayerNodeDeduplication(BpmnNodeVo bpmnNodeVo, List<String> approverList) {

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

    }

    /**
     * multiplayer node deduplication
     *
     * @param bpmnNodeVo   node
     * @param approverList alredy iterated approver list
     * @param flag         true forward,false backward
     */
    private void multiPlayerNodeDeduplication(BpmnNodeVo bpmnNodeVo, List<String> approverList, Boolean flag) {


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

    }

}
