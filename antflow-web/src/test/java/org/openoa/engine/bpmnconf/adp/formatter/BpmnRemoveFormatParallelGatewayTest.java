package org.openoa.engine.bpmnconf.adp.formatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BpmnRemoveFormatParallelGatewayTest extends BaseTest {

    private BpmnRemoveCopyFormatImpl removeCopyFormat;

    @BeforeEach
    void setUp() {
        removeCopyFormat = new BpmnRemoveCopyFormatImpl();
    }

    @Nested
    @DisplayName("Parallel gateway scenarios")
    class ParallelGatewayTest {

        @Test
        @DisplayName("Should remove copy node in parallel gateway branch")
        void shouldRemoveCopyNodeInParallelGatewayBranch() {
            BpmnNodeVo startNode = createStartNode("start", "fork");
            BpmnNodeVo forkNode = createParallelGateway("fork",
                    Arrays.asList("copy1", "approver1"),
                    Collections.emptyList());
            BpmnNodeVo copy1 = createCopyNode("copy1", "join", Arrays.asList("user1", "user2"));
            BpmnNodeVo approver1 = createApproverNode("approver1", "join");
            BpmnNodeVo joinNode = createJoinNode("join", "end", "fork");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, forkNode, copy1, approver1, joinNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(false);

            removeCopyFormat.removeBpmnConf(confVo, startConditions);

            assertEquals("join", copy1.getParams().getNodeTo());
            assertNotNull(startConditions.getEmpToForwardList());
            assertTrue(startConditions.getEmpToForwardList().contains("user1"));
            assertTrue(startConditions.getEmpToForwardList().contains("user2"));
        }

        @Test
        @DisplayName("Should remove copy nodes in both parallel branches")
        void shouldRemoveCopyNodesInBothParallelBranches() {
            BpmnNodeVo startNode = createStartNode("start", "fork");
            BpmnNodeVo forkNode = createParallelGateway("fork",
                    Arrays.asList("copy1", "copy2"),
                    Collections.emptyList());
            BpmnNodeVo copy1 = createCopyNode("copy1", "join", Arrays.asList("user1"));
            BpmnNodeVo copy2 = createCopyNode("copy2", "join", Arrays.asList("user2"));
            BpmnNodeVo joinNode = createJoinNode("join", "end", "fork");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, forkNode, copy1, copy2, joinNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(false);

            removeCopyFormat.removeBpmnConf(confVo, startConditions);

            assertEquals("join", copy1.getParams().getNodeTo());
            assertEquals("join", copy2.getParams().getNodeTo());
            assertNotNull(startConditions.getEmpToForwardList());
            assertTrue(startConditions.getEmpToForwardList().contains("user1"));
            assertTrue(startConditions.getEmpToForwardList().contains("user2"));
        }

        @Test
        @DisplayName("Should not remove approver nodes in parallel branches")
        void shouldNotRemoveApproverNodesInParallelBranches() {
            BpmnNodeVo startNode = createStartNode("start", "fork");
            BpmnNodeVo forkNode = createParallelGateway("fork",
                    Arrays.asList("approver1", "approver2"),
                    Collections.emptyList());
            BpmnNodeVo approver1 = createApproverNode("approver1", "join");
            BpmnNodeVo approver2 = createApproverNode("approver2", "join");
            BpmnNodeVo joinNode = createJoinNode("join", "end", "fork");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, forkNode, approver1, approver2, joinNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(false);

            removeCopyFormat.removeBpmnConf(confVo, startConditions);

            assertEquals("join", approver1.getParams().getNodeTo());
            assertEquals("join", approver2.getParams().getNodeTo());
            assertEquals(6, confVo.getNodes().size());
        }

        @Test
        @DisplayName("Should not remove copy nodes in preview mode with parallel gateway")
        void shouldNotRemoveCopyNodesInPreviewModeWithParallelGateway() {
            BpmnNodeVo startNode = createStartNode("start", "fork");
            BpmnNodeVo forkNode = createParallelGateway("fork",
                    Arrays.asList("copy1", "approver1"),
                    Collections.emptyList());
            BpmnNodeVo copy1 = createCopyNode("copy1", "join", Arrays.asList("user1"));
            BpmnNodeVo approver1 = createApproverNode("approver1", "join");
            BpmnNodeVo joinNode = createJoinNode("join", "end", "fork");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, forkNode, copy1, approver1, joinNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(true);

            removeCopyFormat.removeBpmnConf(confVo, startConditions);

            assertEquals("join", copy1.getParams().getNodeTo());
            assertEquals(6, confVo.getNodes().size());
        }

        @Test
        @DisplayName("Should handle parallel gateway with copy node in only one branch")
        void shouldHandleParallelGatewayWithCopyNodeInOnlyOneBranch() {
            BpmnNodeVo startNode = createStartNode("start", "fork");
            BpmnNodeVo forkNode = createParallelGateway("fork",
                    Arrays.asList("copy1", "approver2"),
                    Collections.emptyList());
            BpmnNodeVo copy1 = createCopyNode("copy1", "approver1", Arrays.asList("user1"));
            BpmnNodeVo approver1 = createApproverNode("approver1", "join");
            BpmnNodeVo approver2 = createApproverNode("approver2", "join");
            BpmnNodeVo joinNode = createJoinNode("join", "end", "fork");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, forkNode, copy1, approver1, approver2, joinNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(false);

            removeCopyFormat.removeBpmnConf(confVo, startConditions);

            assertEquals("approver1", copy1.getParams().getNodeTo());
            assertEquals("join", approver1.getParams().getNodeTo());
            assertNotNull(startConditions.getEmpToForwardList());
            assertTrue(startConditions.getEmpToForwardList().contains("user1"));
        }
    }

    private BpmnNodeVo createStartNode(String nodeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_START.getCode());
        node.setNodeTo(Collections.singletonList(nextNodeId));
        node.setNodeFrom("");
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setNodeTo(nextNodeId);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createApproverNode(String nodeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeTo(nextNodeId != null ? Collections.singletonList(nextNodeId) : Collections.emptyList());
        node.setNodeFrom("");
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setNodeTo(nextNodeId);
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee("approverUser");
        params.setAssignee(assignee);
        params.setParamType(1);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createCopyNode(String nodeId, String nextNodeId, List<String> emplIds) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_COPY.getCode());
        node.setNodeTo(Collections.singletonList(nextNodeId));
        node.setNodeFrom("");
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setNodeTo(nextNodeId);
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee("copyUser");
        params.setAssignee(assignee);
        params.setParamType(1);
        node.setParams(params);
        BpmnNodePropertysVo property = new BpmnNodePropertysVo();
        property.setEmplList(new ArrayList<>());
        property.setEmplIds(new ArrayList<>(emplIds));
        node.setProperty(property);
        return node;
    }

    private BpmnNodeVo createParallelGateway(String nodeId, List<String> nodeTo, List<String> nodeFrom) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode());
        node.setNodeTo(nodeTo);
        if (nodeFrom != null && !nodeFrom.isEmpty()) {
            node.setNodeFrom(nodeFrom.get(0));
            node.setPrevId(nodeFrom);
        } else {
            node.setNodeFrom("");
        }
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        if (nodeTo != null && !nodeTo.isEmpty()) {
            params.setNodeTo(nodeTo.get(0));
        }
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createJoinNode(String nodeId, String nextNodeId, String forkNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeTo(Collections.singletonList(nextNodeId));
        node.setNodeFrom(forkNodeId);
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setNodeTo(nextNodeId);
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee("joinUser");
        params.setAssignee(assignee);
        params.setParamType(1);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createEndNode(String nodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeTo(Collections.emptyList());
        node.setNodeFrom("");
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        node.setParams(params);
        return node;
    }

    private BpmnConfVo buildConfVo(List<BpmnNodeVo> nodes) {
        BpmnConfVo confVo = new BpmnConfVo();
        confVo.setNodes(nodes);
        return confVo;
    }
}
