package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.BpmnNodeVo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BpmnUtilsTest extends BaseTest {

    private BpmnNodeVo buildNode(String nodeId, String nodeFrom, List<String> nodeTo) {
        return BpmnNodeVo.builder()
                .nodeId(nodeId)
                .nodeFrom(nodeFrom)
                .nodeTo(nodeTo)
                .build();
    }

    @Nested
    @DisplayName("getAggregationNode")
    class GetAggregationNodeTest {

        @Test
        @DisplayName("returns the aggregation node when one exists")
        void returnsAggregationNode() {
            BpmnNodeVo parallelNode = buildNode("P1", null, Arrays.asList("A", "B"));
            BpmnNodeVo aggregationNode = buildNode("AGG", "P1", Collections.singletonList("C"));
            BpmnNodeVo otherNode = buildNode("A", "P1", Collections.singletonList("AGG"));

            List<BpmnNodeVo> nodes = Arrays.asList(otherNode, aggregationNode);

            BpmnNodeVo result = BpmnUtils.getAggregationNode(parallelNode, nodes);
            assertNotNull(result);
            assertEquals("AGG", result.getNodeId());
        }

        @Test
        @DisplayName("returns null when no aggregation node exists")
        void returnsNullWhenNoAggregation() {
            BpmnNodeVo parallelNode = buildNode("P1", null, Arrays.asList("A", "B"));
            BpmnNodeVo node1 = buildNode("X1", "OTHER", Collections.singletonList("C"));
            BpmnNodeVo node2 = buildNode("X2", "ANOTHER", Collections.singletonList("D"));

            List<BpmnNodeVo> nodes = Arrays.asList(node1, node2);

            BpmnNodeVo result = BpmnUtils.getAggregationNode(parallelNode, nodes);
            assertNull(result);
        }

        @Test
        @DisplayName("returns null when all nodes have nodeFrom equal to parallelNodeId but their nodeIds are all in parallelNodeNodeTo")
        void returnsNullWhenAllNodeIdsInNodeTo() {
            BpmnNodeVo parallelNode = buildNode("P1", null, Arrays.asList("A", "B"));
            BpmnNodeVo node1 = buildNode("A", "P1", Collections.singletonList("C"));
            BpmnNodeVo node2 = buildNode("B", "P1", Collections.singletonList("D"));

            List<BpmnNodeVo> nodes = Arrays.asList(node1, node2);

            BpmnNodeVo result = BpmnUtils.getAggregationNode(parallelNode, nodes);
            assertNull(result);
        }

        @Test
        @DisplayName("returns the correct node when multiple candidates exist")
        void returnsCorrectNodeWithMultipleCandidates() {
            BpmnNodeVo parallelNode = buildNode("P1", null, Arrays.asList("A", "B"));
            BpmnNodeVo nodeInNodeTo = buildNode("A", "P1", Collections.singletonList("C"));
            BpmnNodeVo candidate1 = buildNode("AGG1", "P1", Collections.singletonList("D"));
            BpmnNodeVo candidate2 = buildNode("AGG2", "P1", Collections.singletonList("E"));

            List<BpmnNodeVo> nodes = Arrays.asList(nodeInNodeTo, candidate1, candidate2);

            BpmnNodeVo result = BpmnUtils.getAggregationNode(parallelNode, nodes);
            assertNotNull(result);
            assertEquals("AGG1", result.getNodeId());
        }

        @Test
        @DisplayName("returns null for empty collection")
        void returnsNullForEmptyCollection() {
            BpmnNodeVo parallelNode = buildNode("P1", null, Arrays.asList("A", "B"));

            BpmnNodeVo result = BpmnUtils.getAggregationNode(parallelNode, Collections.emptyList());
            assertNull(result);
        }
    }
}
