package org.openoa.engine.bpmnconf.adp.formatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class BpmnRemoveFormatImplTest extends BaseTest {

    private BpmnRemoveFormatImpl removeFormat;

    @BeforeEach
    void setUp() {
        removeFormat = new BpmnRemoveFormatImpl();
    }

    @Nested
    @DisplayName("removeBpmnConf")
    class RemoveBpmnConfTest {

        @Test
        @DisplayName("should remove single-assignee node marked as TO_BE_REMOVED")
        void shouldRemoveSingleAssigneeToBeRemovedNode() {
            BpmnNodeVo startNode = createStartNode("start", "toBeRemoved");
            BpmnNodeVo toBeRemovedNode = createToBeRemovedNode("toBeRemoved", "approver");
            BpmnNodeVo approverNode = createApproverNode("approver", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, toBeRemovedNode, approverNode, endNode));

            removeFormat.removeBpmnConf(confVo, new BpmnStartConditionsVo());

            assertEquals("approver", startNode.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should remove multi-assignee node marked as TO_BE_REMOVED")
        void shouldRemoveMultiAssigneeToBeRemovedNode() {
            BpmnNodeVo startNode = createStartNode("start", "toBeRemoved");
            BpmnNodeVo toBeRemovedNode = createToBeRemovedMultiNode("toBeRemoved", "approver");
            BpmnNodeVo approverNode = createApproverNode("approver", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, toBeRemovedNode, approverNode, endNode));

            removeFormat.removeBpmnConf(confVo, new BpmnStartConditionsVo());

            assertEquals("approver", startNode.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should not remove node with normal assignee")
        void shouldNotRemoveNormalAssigneeNode() {
            BpmnNodeVo startNode = createStartNode("start", "approver");
            BpmnNodeVo approverNode = createApproverNode("approver", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, approverNode, endNode));

            removeFormat.removeBpmnConf(confVo, new BpmnStartConditionsVo());

            assertEquals("approver", startNode.getParams().getNodeTo());
            assertEquals(3, confVo.getNodes().size());
        }

        @Test
        @DisplayName("should remove multiple consecutive TO_BE_REMOVED nodes (only first per walk)")
        void shouldRemoveConsecutiveToBeRemovedNodes() {
            BpmnNodeVo startNode = createStartNode("start", "toBeRemoved1");
            BpmnNodeVo toBeRemoved1 = createToBeRemovedNode("toBeRemoved1", "toBeRemoved2");
            BpmnNodeVo toBeRemoved2 = createToBeRemovedNode("toBeRemoved2", "approver");
            BpmnNodeVo approverNode = createApproverNode("approver", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, toBeRemoved1, toBeRemoved2, approverNode, endNode));

            removeFormat.removeBpmnConf(confVo, new BpmnStartConditionsVo());

            assertEquals("toBeRemoved2", startNode.getParams().getNodeTo());
            assertEquals("approver", toBeRemoved1.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should not remove node with paramType=3 (ordered multi)")
        void shouldNotRemoveOrderedMultiNode() {
            BpmnNodeVo startNode = createStartNode("start", "orderedMulti");
            BpmnNodeVo orderedMultiNode = createOrderedMultiNode("orderedMulti", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, orderedMultiNode, endNode));

            removeFormat.removeBpmnConf(confVo, new BpmnStartConditionsVo());

            assertEquals("orderedMulti", startNode.getParams().getNodeTo());
        }
    }

    @Nested
    @DisplayName("trueSuppliers")
    class TrueSuppliersTest {

        @Test
        @DisplayName("should return 2 suppliers")
        void shouldReturnTwoSuppliers() {
            BpmnNodeVo node = createApproverNode("node1", "userA", "node2");
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            List<Supplier<Boolean>> suppliers = removeFormat.trueSuppliers(node, startConditions);

            assertEquals(2, suppliers.size());
        }
    }

    @Nested
    @DisplayName("order")
    class OrderTest {

        @Test
        @DisplayName("should return 1")
        void shouldReturnOne() {
            assertEquals(1, removeFormat.order());
        }
    }

    private BpmnNodeVo createStartNode(String nodeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_START.getCode());
        node.setNodeTo(Collections.singletonList(nextNodeId));
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(1);
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee("startUser");
        params.setAssignee(assignee);
        params.setNodeTo(nextNodeId);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createApproverNode(String nodeId, String nextNodeId) {
        return createApproverNode(nodeId, "normalUser", nextNodeId);
    }

    private BpmnNodeVo createApproverNode(String nodeId, String assigneeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeTo(nextNodeId != null ? Collections.singletonList(nextNodeId) : Collections.emptyList());
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(1);
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee(assigneeId);
        params.setAssignee(assignee);
        params.setNodeTo(nextNodeId);
        params.setIsNodeDeduplication(0);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createToBeRemovedNode(String nodeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeTo(nextNodeId != null ? Collections.singletonList(nextNodeId) : Collections.emptyList());
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(1);
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
        params.setAssignee(assignee);
        params.setNodeTo(nextNodeId);
        params.setIsNodeDeduplication(0);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createToBeRemovedMultiNode(String nodeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeTo(nextNodeId != null ? Collections.singletonList(nextNodeId) : Collections.emptyList());
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(2);
        List<BpmnNodeParamsAssigneeVo> assigneeList = new ArrayList<>();
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
        assigneeList.add(assignee);
        params.setAssigneeList(assigneeList);
        params.setNodeTo(nextNodeId);
        params.setIsNodeDeduplication(0);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createOrderedMultiNode(String nodeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeTo(nextNodeId != null ? Collections.singletonList(nextNodeId) : Collections.emptyList());
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(3);
        List<BpmnNodeParamsAssigneeVo> assigneeList = new ArrayList<>();
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
        assigneeList.add(assignee);
        params.setAssigneeList(assigneeList);
        params.setNodeTo(nextNodeId);
        params.setIsNodeDeduplication(0);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createEndNode(String nodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeTo(Collections.emptyList());
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(1);
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee("endUser");
        params.setAssignee(assignee);
        node.setParams(params);
        return node;
    }

    private BpmnConfVo buildConfVo(List<BpmnNodeVo> nodes) {
        BpmnConfVo confVo = new BpmnConfVo();
        confVo.setNodes(nodes);
        return confVo;
    }
}
