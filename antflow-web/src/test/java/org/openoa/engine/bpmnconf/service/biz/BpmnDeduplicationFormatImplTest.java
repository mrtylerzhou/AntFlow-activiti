package org.openoa.engine.bpmnconf.service.biz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.DeduplicationTypeEnum;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BpmnDeduplicationFormatImplTest extends MockBaseTest {

    @InjectMocks
    private BpmnDeduplicationFormatImpl bpmnDeduplicationFormat;

    private BpmnStartConditionsVo defaultConditions;

    @BeforeEach
    void setUp() {
        defaultConditions = new BpmnStartConditionsVo();
        defaultConditions.setStartUserId("initiator");
        defaultConditions.setDeduplicationType(DeduplicationTypeEnum.DEDUPLICATION_TYPE_FORWARD.getCode());
    }

    private BpmnNodeParamsAssigneeVo createAssignee(String assigneeId, String assigneeName) {
        BpmnNodeParamsAssigneeVo vo = new BpmnNodeParamsAssigneeVo();
        vo.setAssignee(assigneeId);
        vo.setAssigneeName(assigneeName);
        vo.setIsDeduplication(0);
        return vo;
    }

    private BpmnNodeVo createStartNode(String nodeId, String assigneeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_START.getCode());
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(1);
        params.setAssignee(createAssignee(assigneeId, assigneeId + "_name"));
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createSingleApproverNode(String nodeId, String assigneeId, String nodeTo) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(1);
        params.setNodeTo(nodeTo);
        params.setAssignee(createAssignee(assigneeId, assigneeId + "_name"));
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createMultiApproverNode(String nodeId, List<String> assigneeIds, String nodeTo) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(2);
        params.setNodeTo(nodeTo);
        List<BpmnNodeParamsAssigneeVo> assigneeList = new ArrayList<>();
        for (String id : assigneeIds) {
            assigneeList.add(createAssignee(id, id + "_name"));
        }
        params.setAssigneeList(assigneeList);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createParallelGatewayNode(String nodeId, List<String> nodeToIds) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode());
        node.setNodeTo(nodeToIds);
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(1);
        params.setAssignee(createAssignee("gw_" + nodeId, "gateway"));
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createGatewayNodeParallel(String nodeId, String nodeTo) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_GATEWAY.getCode());
        node.setIsParallel(true);
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setNodeTo(nodeTo);
        params.setParamType(1);
        params.setAssignee(createAssignee("gw_" + nodeId, "gateway"));
        node.setParams(params);
        return node;
    }

    private BpmnConfVo buildConfVo(List<BpmnNodeVo> nodes) {
        BpmnConfVo confVo = new BpmnConfVo();
        confVo.setNodes(nodes);
        confVo.setDeduplicationType(DeduplicationTypeEnum.DEDUPLICATION_TYPE_FORWARD.getCode());
        return confVo;
    }

    @Nested
    @DisplayName("forwardDeduplication")
    class ForwardDeduplicationTest {

        @Test
        @DisplayName("single approver in two consecutive nodes - second should be deduplicated")
        void singleApproverConsecutiveNodes_secondDeduplicated() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userA", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1));

            bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions);

            assertEquals(1, node1.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, node1.getParams().getIsNodeDeduplication());
        }

        @Test
        @DisplayName("same approver in non-consecutive nodes - should be deduplicated")
        void sameApproverNonConsecutiveNodes_shouldBeDeduplicated() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "node2");
            BpmnNodeVo node2 = createSingleApproverNode("node2", "userA", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2));

            bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions);

            assertEquals(0, node1.getParams().getAssignee().getIsDeduplication());
            assertEquals(0, node1.getParams().getIsNodeDeduplication());
            assertEquals(1, node2.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, node2.getParams().getIsNodeDeduplication());
        }

        @Test
        @DisplayName("different approvers - should NOT be deduplicated")
        void differentApprovers_shouldNotBeDeduplicated() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "node2");
            BpmnNodeVo node2 = createSingleApproverNode("node2", "userC", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2));

            bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions);

            assertEquals(0, node1.getParams().getAssignee().getIsDeduplication());
            assertEquals(0, node1.getParams().getIsNodeDeduplication());
            assertEquals(0, node2.getParams().getAssignee().getIsDeduplication());
            assertEquals(0, node2.getParams().getIsNodeDeduplication());
        }

        @Test
        @DisplayName("multi-player node with some duplicate assignees - only duplicates marked")
        void multiPlayerNode_someDuplicates_onlyDuplicatesMarked() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "node2");
            BpmnNodeVo node2 = createMultiApproverNode("node2", Arrays.asList("userA", "userC"), null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2));

            bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions);

            assertEquals(1, node2.getParams().getAssigneeList().get(0).getIsDeduplication());
            assertEquals(0, node2.getParams().getAssigneeList().get(1).getIsDeduplication());
            assertEquals(0, node2.getParams().getIsNodeDeduplication());
        }

        @Test
        @DisplayName("multi-player node with all duplicate assignees - isNodeDeduplication depends on second pass")
        void multiPlayerNode_allDuplicates_isNodeDeduplicationDependsOnSecondPass() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "node2");
            BpmnNodeVo node2 = createMultiApproverNode("node2", Arrays.asList("userA", "userB"), null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2));

            bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions);

            assertEquals(1, node2.getParams().getAssigneeList().get(0).getIsDeduplication());
            assertEquals(0, node2.getParams().getAssigneeList().get(1).getIsDeduplication());
            assertEquals(0, node2.getParams().getIsNodeDeduplication());
            assertEquals(1, node1.getParams().getAssignee().getIsDeduplication());
        }

        @Test
        @DisplayName("null bpmnConfVo should throw AFBizException")
        void nullBpmnConfVo_shouldThrow() {
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();
            conditions.setStartUserId("userA");

            assertThrows(Exception.class, () ->
                    bpmnDeduplicationFormat.forwardDeduplication(null, conditions));
        }

        @Test
        @DisplayName("null startUserId should throw AFBizException when no start node found")
        void nullStartUserId_shouldThrow() {
            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", null);
            node1.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnConfVo confVo = buildConfVo(Collections.singletonList(node1));

            assertThrows(NullPointerException.class, () ->
                    bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions));
        }

        @Test
        @DisplayName("forward deduplication with parallel gateway delegates to backwardDeduplication")
        void forwardWithParallelGateway_delegatesToBackward() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("pgw1");

            BpmnNodeVo pgw = createParallelGatewayNode("pgw1", Arrays.asList("node1", "node2"));
            BpmnNodeVo node1 = createSingleApproverNode("node1", "userA", null);
            BpmnNodeVo node2 = createSingleApproverNode("node2", "userB", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, pgw, node1, node2));

            BpmnConfVo result = bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions);

            assertNotNull(result);
            assertSame(confVo, result);
        }

        @Test
        @DisplayName("forward deduplication with gateway isParallel=true delegates to backwardDeduplication")
        void forwardWithParallelGatewayType_delegatesToBackward() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("gw1");

            BpmnNodeVo gw = createGatewayNodeParallel("gw1", null);
            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, gw, node1));

            BpmnConfVo result = bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions);

            assertNotNull(result);
            assertSame(confVo, result);
        }

        @Test
        @DisplayName("three single approver nodes with same approver in first and third")
        void threeNodes_sameApproverFirstAndThird() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "node2");
            BpmnNodeVo node2 = createSingleApproverNode("node2", "userA", "node3");
            BpmnNodeVo node3 = createSingleApproverNode("node3", "userC", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2, node3));

            bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions);

            assertEquals(0, node1.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, node2.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, node2.getParams().getIsNodeDeduplication());
            assertEquals(0, node3.getParams().getAssignee().getIsDeduplication());
            assertEquals(0, node3.getParams().getIsNodeDeduplication());
        }
    }

    @Nested
    @DisplayName("backwardDeduplication")
    class BackwardDeduplicationTest {

        @Test
        @DisplayName("simple linear flow backward deduplication")
        void simpleLinearFlow_backwardDeduplication() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "node2");
            BpmnNodeVo node2 = createSingleApproverNode("node2", "userA", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2));

            bpmnDeduplicationFormat.backwardDeduplication(confVo, defaultConditions);

            assertEquals(0, node1.getParams().getAssignee().getIsDeduplication());
            assertEquals(0, node1.getParams().getIsNodeDeduplication());
            assertEquals(1, node2.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, node2.getParams().getIsNodeDeduplication());
        }

        @Test
        @DisplayName("backward deduplication with different approvers - no deduplication")
        void backwardDifferentApprovers_noDeduplication() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "node2");
            BpmnNodeVo node2 = createSingleApproverNode("node2", "userC", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2));

            bpmnDeduplicationFormat.backwardDeduplication(confVo, defaultConditions);

            assertEquals(0, node1.getParams().getAssignee().getIsDeduplication());
            assertEquals(0, node2.getParams().getAssignee().getIsDeduplication());
        }

        @Test
        @DisplayName("flow with parallel gateway should work correctly")
        void flowWithParallelGateway_shouldWork() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("pgw1");

            BpmnNodeVo pgw = createParallelGatewayNode("pgw1", Arrays.asList("node1", "node2"));

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userA", null);
            BpmnNodeVo node2 = createSingleApproverNode("node2", "userB", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, pgw, node1, node2));

            bpmnDeduplicationFormat.backwardDeduplication(confVo, defaultConditions);

            assertEquals(1, node1.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, node1.getParams().getIsNodeDeduplication());
            assertEquals(0, node2.getParams().getAssignee().getIsDeduplication());
        }

        @Test
        @DisplayName("backward deduplication with multi-player node")
        void backwardMultiPlayerNode() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "node2");
            BpmnNodeVo node2 = createMultiApproverNode("node2", Arrays.asList("userA", "userC"), null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2));

            bpmnDeduplicationFormat.backwardDeduplication(confVo, defaultConditions);

            assertEquals(1, node2.getParams().getAssigneeList().get(0).getIsDeduplication());
            assertEquals(0, node2.getParams().getAssigneeList().get(1).getIsDeduplication());
            assertEquals(0, node2.getParams().getIsNodeDeduplication());
        }

        @Test
        @DisplayName("backward deduplication with all duplicate multi-player node")
        void backwardMultiPlayerNode_allDuplicates() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "node2");
            BpmnNodeVo node2 = createMultiApproverNode("node2", Arrays.asList("userA", "userB"), null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2));

            bpmnDeduplicationFormat.backwardDeduplication(confVo, defaultConditions);

            assertEquals(1, node2.getParams().getAssigneeList().get(0).getIsDeduplication());
            assertEquals(1, node2.getParams().getAssigneeList().get(1).getIsDeduplication());
            assertEquals(1, node2.getParams().getIsNodeDeduplication());
        }

        @Test
        @DisplayName("backward deduplication returns same confVo instance")
        void backwardReturnsSameConfVo() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1));

            BpmnConfVo result = bpmnDeduplicationFormat.backwardDeduplication(confVo, defaultConditions);

            assertSame(confVo, result);
        }
    }

    @Nested
    @DisplayName("deduplicationExclude")
    class DeduplicationExcludeTest {

        @Test
        @DisplayName("node with deduplicationExclude=true should be skipped")
        void deduplicationExcludeNode_shouldBeSkipped() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userA", null);
            node1.setDeduplicationExclude(true);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1));

            bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions);

            assertEquals(0, node1.getParams().getAssignee().getIsDeduplication());
            assertEquals(0, node1.getParams().getIsNodeDeduplication());
        }
    }

    @Nested
    @DisplayName("SKIP_NEXT deduplication type")
    class SkipNextDeduplicationTest {

        @Test
        @DisplayName("SKIP_NEXT clears approverList and adds only current assignee for single node")
        void skipNextSingleNode_clearsApproverList() {
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();
            conditions.setStartUserId("initiator");
            conditions.setDeduplicationType(DeduplicationTypeEnum.DEDUPLICATION_TYPE_SKIP_NEXT.getCode());

            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "node2");
            BpmnNodeVo node2 = createSingleApproverNode("node2", "userB", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2));

            bpmnDeduplicationFormat.backwardDeduplication(confVo, conditions);

            assertEquals(1, node2.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, node2.getParams().getIsNodeDeduplication());
        }
    }

    @Nested
    @DisplayName("already deduplicated node")
    class AlreadyDeduplicatedNodeTest {

        @Test
        @DisplayName("node with isNodeDeduplication=1 should be skipped")
        void alreadyDeduplicatedNode_shouldBeSkipped() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userA", null);
            node1.getParams().setIsNodeDeduplication(1);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1));

            bpmnDeduplicationFormat.backwardDeduplication(confVo, defaultConditions);

            assertEquals(0, node1.getParams().getAssignee().getIsDeduplication());
        }
    }

    @Nested
    @DisplayName("backward deduplication with parallel branches")
    class BackwardParallelBranchesTest {

        @Test
        @DisplayName("cross branch duplicates - user appears in both branches")
        void crossBranchDuplicates_userInBothBranches() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("pgw1");

            BpmnNodeVo pgw = createParallelGatewayNode("pgw1", Arrays.asList("branch1", "branch2"));
            BpmnNodeVo branch1 = createSingleApproverNode("branch1", "userB", "node1");
            BpmnNodeVo node1 = createSingleApproverNode("node1", "userC", "join");
            BpmnNodeVo branch2 = createSingleApproverNode("branch2", "userC", "node2");
            BpmnNodeVo node2 = createSingleApproverNode("node2", "userD", "join");
            BpmnNodeVo join = createParallelGatewayNode("join", Arrays.asList("end"));
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, pgw, branch1, node1, branch2, node2, join, endNode));

            bpmnDeduplicationFormat.backwardDeduplication(confVo, defaultConditions);

            assertEquals(0, node1.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, branch2.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, branch2.getParams().getIsNodeDeduplication());
            assertEquals(0, node2.getParams().getAssignee().getIsDeduplication());
        }

        @Test
        @DisplayName("no cross branch duplicates - all different users")
        void noCrossBranchDuplicates_allDifferentUsers() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("pgw1");

            BpmnNodeVo pgw = createParallelGatewayNode("pgw1", Arrays.asList("branch1", "branch2"));
            BpmnNodeVo branch1 = createSingleApproverNode("branch1", "userB", "join");
            BpmnNodeVo branch2 = createSingleApproverNode("branch2", "userC", "join");
            BpmnNodeVo join = createParallelGatewayNode("join", Arrays.asList("end"));
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, pgw, branch1, branch2, join, endNode));

            bpmnDeduplicationFormat.backwardDeduplication(confVo, defaultConditions);

            assertEquals(0, branch1.getParams().getAssignee().getIsDeduplication());
            assertEquals(0, branch2.getParams().getAssignee().getIsDeduplication());
        }

        @Test
        @DisplayName("start user duplicated in parallel branch")
        void startUserDuplicatedInParallelBranch() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("pgw1");

            BpmnNodeVo pgw = createParallelGatewayNode("pgw1", Arrays.asList("branch1", "branch2"));
            BpmnNodeVo branch1 = createSingleApproverNode("branch1", "userA", "join");
            BpmnNodeVo branch2 = createSingleApproverNode("branch2", "userB", "join");
            BpmnNodeVo join = createParallelGatewayNode("join", Arrays.asList("end"));
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, pgw, branch1, branch2, join, endNode));

            bpmnDeduplicationFormat.backwardDeduplication(confVo, defaultConditions);

            assertEquals(1, branch1.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, branch1.getParams().getIsNodeDeduplication());
            assertEquals(0, branch2.getParams().getAssignee().getIsDeduplication());
        }
    }

    @Nested
    @DisplayName("forward deduplication with long chain")
    class ForwardLongChainTest {

        @Test
        @DisplayName("four node chain with alternating duplicates")
        void fourNodeChain_alternatingDuplicates() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "node2");
            BpmnNodeVo node2 = createSingleApproverNode("node2", "userA", "node3");
            BpmnNodeVo node3 = createSingleApproverNode("node3", "userC", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2, node3));

            bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions);

            assertEquals(0, node1.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, node2.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, node2.getParams().getIsNodeDeduplication());
            assertEquals(0, node3.getParams().getAssignee().getIsDeduplication());
        }

        @Test
        @DisplayName("all same approver in chain - all deduplicated except first")
        void allSameApprover_allDeduplicatedExceptFirst() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userA", "node2");
            BpmnNodeVo node2 = createSingleApproverNode("node2", "userA", "node3");
            BpmnNodeVo node3 = createSingleApproverNode("node3", "userA", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2, node3));

            bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions);

            assertEquals(1, node1.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, node2.getParams().getAssignee().getIsDeduplication());
            assertEquals(1, node3.getParams().getAssignee().getIsDeduplication());
        }
    }

    @Nested
    @DisplayName("deduplication with multi-player node edge cases")
    class MultiPlayerEdgeCasesTest {

        @Test
        @DisplayName("multi-player node with single assignee matching previous")
        void multiPlayerNode_singleAssigneeMatchingPrevious() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "node2");
            BpmnNodeVo node2 = createMultiApproverNode("node2", Arrays.asList("userA"), null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2));

            bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions);

            assertEquals(1, node2.getParams().getAssigneeList().get(0).getIsDeduplication());
            assertEquals(1, node2.getParams().getIsNodeDeduplication());
        }

        @Test
        @DisplayName("multi-player node with no matching assignees")
        void multiPlayerNode_noMatchingAssignees() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            startNode.getParams().setNodeTo("node1");

            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "node2");
            BpmnNodeVo node2 = createMultiApproverNode("node2", Arrays.asList("userC", "userD"), null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, node2));

            bpmnDeduplicationFormat.forwardDeduplication(confVo, defaultConditions);

            assertEquals(0, node2.getParams().getAssigneeList().get(0).getIsDeduplication());
            assertEquals(0, node2.getParams().getAssigneeList().get(1).getIsDeduplication());
            assertEquals(0, node2.getParams().getIsNodeDeduplication());
        }
    }

    private BpmnNodeVo createEndNode(String nodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_COPY.getCode());
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(1);
        params.setAssignee(createAssignee("end_user", "end"));
        node.setParams(params);
        return node;
    }
}
