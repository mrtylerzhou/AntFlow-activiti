package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AfNodeUtilsTest extends BaseTest {

    private BpmnNodeVo createTestNode(String nodeId, Integer nodeType, String nodeTo, String nodeFrom, Integer isNodeDeduplication) {
        BpmnNodeParamsVo params = BpmnNodeParamsVo.builder()
                .nodeTo(nodeTo)
                .isNodeDeduplication(isNodeDeduplication != null ? isNodeDeduplication : 0)
                .build();
        return BpmnNodeVo.builder()
                .nodeId(nodeId)
                .nodeType(nodeType)
                .nodeFrom(nodeFrom)
                .params(params)
                .build();
    }

    private Map<String, BpmnNodeVo> buildNodeMap(BpmnNodeVo... nodes) {
        Map<String, BpmnNodeVo> map = new HashMap<>();
        for (BpmnNodeVo node : nodes) {
            map.put(node.getNodeId(), node);
        }
        return map;
    }

    @Nested
    @DisplayName("getNextNodeOfTypeRecursively")
    class GetNextNodeOfTypeRecursivelyTest {

        @Test
        @DisplayName("Should find next approver node from start node")
        void shouldFindNextApproverNodeFromStartNode() {
            BpmnNodeVo startNode = createTestNode("start", NodeTypeEnum.NODE_TYPE_START.getCode(), "approver", null, 0);
            BpmnNodeVo approverNode = createTestNode("approver", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), null, "start", 0);
            Map<String, BpmnNodeVo> mapNodes = buildNodeMap(startNode, approverNode);
            List<Integer> types = Collections.singletonList(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnNodeVo result = AfNodeUtils.getNextNodeOfTypeRecursively("start", types, mapNodes, false);

            assertNotNull(result);
            assertEquals("approver", result.getNodeId());
        }

        @Test
        @DisplayName("Should find next approver skipping copy node")
        void shouldFindNextApproverSkippingCopyNode() {
            BpmnNodeVo startNode = createTestNode("start", NodeTypeEnum.NODE_TYPE_START.getCode(), "copy", null, 0);
            BpmnNodeVo copyNode = createTestNode("copy", NodeTypeEnum.NODE_TYPE_COPY.getCode(), "approver", "start", 0);
            BpmnNodeVo approverNode = createTestNode("approver", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), null, "copy", 0);
            Map<String, BpmnNodeVo> mapNodes = buildNodeMap(startNode, copyNode, approverNode);
            List<Integer> types = Collections.singletonList(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnNodeVo result = AfNodeUtils.getNextNodeOfTypeRecursively("start", types, mapNodes, false);

            assertNotNull(result);
            assertEquals("approver", result.getNodeId());
        }

        @Test
        @DisplayName("Should return null when no matching type found")
        void shouldReturnNullWhenNoMatchingTypeFound() {
            BpmnNodeVo startNode = createTestNode("start", NodeTypeEnum.NODE_TYPE_START.getCode(), "copy", null, 0);
            BpmnNodeVo copyNode = createTestNode("copy", NodeTypeEnum.NODE_TYPE_COPY.getCode(), null, "start", 0);
            Map<String, BpmnNodeVo> mapNodes = buildNodeMap(startNode, copyNode);
            List<Integer> types = Collections.singletonList(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnNodeVo result = AfNodeUtils.getNextNodeOfTypeRecursively("start", types, mapNodes, false);

            assertNull(result);
        }

        @Test
        @DisplayName("Should return null when nodeId is null")
        void shouldReturnNullWhenNodeIdIsNull() {
            BpmnNodeVo startNode = createTestNode("start", NodeTypeEnum.NODE_TYPE_START.getCode(), "approver", null, 0);
            BpmnNodeVo approverNode = createTestNode("approver", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), null, "start", 0);
            Map<String, BpmnNodeVo> mapNodes = buildNodeMap(startNode, approverNode);
            List<Integer> types = Collections.singletonList(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnNodeVo result = AfNodeUtils.getNextNodeOfTypeRecursively(null, types, mapNodes, false);

            assertNull(result);
        }

        @Test
        @DisplayName("Should return null when mapNodes is empty")
        void shouldReturnNullWhenMapNodesIsEmpty() {
            Map<String, BpmnNodeVo> mapNodes = Collections.emptyMap();
            List<Integer> types = Collections.singletonList(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnNodeVo result = AfNodeUtils.getNextNodeOfTypeRecursively("start", types, mapNodes, false);

            assertNull(result);
        }

        @Test
        @DisplayName("Should skip deduplicated nodes when includeDeduplicated is false")
        void shouldSkipDeduplicatedNodesWhenIncludeDeduplicatedIsFalse() {
            BpmnNodeVo startNode = createTestNode("start", NodeTypeEnum.NODE_TYPE_START.getCode(), "dedup", null, 0);
            BpmnNodeVo dedupNode = createTestNode("dedup", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), "approver", "start", 1);
            BpmnNodeVo approverNode = createTestNode("approver", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), null, "dedup", 0);
            Map<String, BpmnNodeVo> mapNodes = buildNodeMap(startNode, dedupNode, approverNode);
            List<Integer> types = Collections.singletonList(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnNodeVo result = AfNodeUtils.getNextNodeOfTypeRecursively("start", types, mapNodes, false);

            assertNotNull(result);
            assertEquals("approver", result.getNodeId());
        }

        @Test
        @DisplayName("Should not return deduplicated node when includeDeduplicated is true due to code logic")
        void shouldNotReturnDeduplicatedNodeWhenIncludeDeduplicatedIsTrue() {
            BpmnNodeVo startNode = createTestNode("start", NodeTypeEnum.NODE_TYPE_START.getCode(), "dedup", null, 0);
            BpmnNodeVo dedupNode = createTestNode("dedup", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), null, "start", 1);
            Map<String, BpmnNodeVo> mapNodes = buildNodeMap(startNode, dedupNode);
            List<Integer> types = Collections.singletonList(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnNodeVo result = AfNodeUtils.getNextNodeOfTypeRecursively("start", types, mapNodes, true);

            assertNull(result);
        }

        @Test
        @DisplayName("Should traverse multiple hops to find matching node")
        void shouldTraverseMultipleHopsToFindMatchingNode() {
            BpmnNodeVo startNode = createTestNode("start", NodeTypeEnum.NODE_TYPE_START.getCode(), "gateway", null, 0);
            BpmnNodeVo gatewayNode = createTestNode("gateway", NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode(), "copy", "start", 0);
            BpmnNodeVo copyNode = createTestNode("copy", NodeTypeEnum.NODE_TYPE_COPY.getCode(), "approver", "gateway", 0);
            BpmnNodeVo approverNode = createTestNode("approver", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), null, "copy", 0);
            Map<String, BpmnNodeVo> mapNodes = buildNodeMap(startNode, gatewayNode, copyNode, approverNode);
            List<Integer> types = Collections.singletonList(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnNodeVo result = AfNodeUtils.getNextNodeOfTypeRecursively("start", types, mapNodes, false);

            assertNotNull(result);
            assertEquals("approver", result.getNodeId());
        }
    }

    @Nested
    @DisplayName("getNeareastPrevNodeOfTypeRecursively")
    class GetNeareastPrevNodeOfTypeRecursivelyTest {

        @Test
        @DisplayName("Should find previous approver node from end node")
        void shouldFindPreviousApproverNodeFromEndNode() {
            BpmnNodeVo approverNode = createTestNode("approver", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), "end", null, 0);
            BpmnNodeVo endNode = createTestNode("end", NodeTypeEnum.NODE_TYPE_START.getCode(), null, "approver", 0);
            Map<String, BpmnNodeVo> mapNodes = buildNodeMap(approverNode, endNode);
            List<Integer> types = Collections.singletonList(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnNodeVo result = AfNodeUtils.getNeareastPrevNodeOfTypeRecursively("end", types, mapNodes, false);

            assertNotNull(result);
            assertEquals("approver", result.getNodeId());
        }

        @Test
        @DisplayName("Should return current node when nodeFrom is empty and no match found")
        void shouldReturnCurrentNodeWhenNodeFromIsEmpty() {
            BpmnNodeVo startNode = createTestNode("start", NodeTypeEnum.NODE_TYPE_START.getCode(), null, null, 0);
            Map<String, BpmnNodeVo> mapNodes = buildNodeMap(startNode);
            List<Integer> types = Collections.singletonList(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnNodeVo result = AfNodeUtils.getNeareastPrevNodeOfTypeRecursively("start", types, mapNodes, false);

            assertNotNull(result);
            assertEquals("start", result.getNodeId());
        }

        @Test
        @DisplayName("Should return null when nodeId is null")
        void shouldReturnNullWhenNodeIdIsNull() {
            BpmnNodeVo approverNode = createTestNode("approver", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), null, null, 0);
            Map<String, BpmnNodeVo> mapNodes = buildNodeMap(approverNode);
            List<Integer> types = Collections.singletonList(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnNodeVo result = AfNodeUtils.getNeareastPrevNodeOfTypeRecursively(null, types, mapNodes, false);

            assertNull(result);
        }

        @Test
        @DisplayName("Should return current node when fromNode type does not match and fromNode has no nodeFrom")
        void shouldReturnCurrentNodeWhenFromNodeTypeNotMatch() {
            BpmnNodeVo copyNode = createTestNode("copy", NodeTypeEnum.NODE_TYPE_COPY.getCode(), "end", null, 0);
            BpmnNodeVo endNode = createTestNode("end", NodeTypeEnum.NODE_TYPE_START.getCode(), null, "copy", 0);
            Map<String, BpmnNodeVo> mapNodes = buildNodeMap(copyNode, endNode);
            List<Integer> types = Collections.singletonList(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnNodeVo result = AfNodeUtils.getNeareastPrevNodeOfTypeRecursively("end", types, mapNodes, false);

            assertNotNull(result);
            assertEquals("end", result.getNodeId());
        }

        @Test
        @DisplayName("Should skip deduplicated fromNode when includeDeduplicated is false")
        void shouldSkipDeduplicatedFromNodeWhenIncludeDeduplicatedIsFalse() {
            BpmnNodeVo dedupNode = createTestNode("dedup", NodeTypeEnum.NODE_TYPE_APPROVER.getCode(), "end", null, 1);
            BpmnNodeVo endNode = createTestNode("end", NodeTypeEnum.NODE_TYPE_START.getCode(), null, "dedup", 1);
            Map<String, BpmnNodeVo> mapNodes = buildNodeMap(dedupNode, endNode);
            List<Integer> types = Collections.singletonList(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());

            BpmnNodeVo result = AfNodeUtils.getNeareastPrevNodeOfTypeRecursively("end", types, mapNodes, false);

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("addOrEditProperty")
    class AddOrEditPropertyTest {

        @Test
        @DisplayName("Should initialize property when null and apply consumer")
        void shouldInitializePropertyWhenNullAndApplyConsumer() {
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeId("node1")
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .build();

            AfNodeUtils.addOrEditProperty(node, p -> p.setSignType(1));

            assertNotNull(node.getProperty());
            assertEquals(1, node.getProperty().getSignType());
        }

        @Test
        @DisplayName("Should edit existing property with consumer")
        void shouldEditExistingPropertyWithConsumer() {
            BpmnNodePropertysVo property = BpmnNodePropertysVo.builder()
                    .signType(1)
                    .build();
            BpmnNodeVo node = BpmnNodeVo.builder()
                    .nodeId("node1")
                    .nodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode())
                    .property(property)
                    .build();

            AfNodeUtils.addOrEditProperty(node, p -> p.setSignType(2));

            assertEquals(2, node.getProperty().getSignType());
        }
    }
}
