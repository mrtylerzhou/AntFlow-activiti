package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.BpmnNodeVo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BpmnUtilsBaseTest extends BaseTest {

    private BpmnNodeVo createTestNode(String nodeId, List<String> nodeTo, String nodeFrom) {
        return BpmnNodeVo.builder()
                .nodeId(nodeId)
                .nodeTo(nodeTo)
                .nodeFrom(nodeFrom)
                .build();
    }

    @Nested
    @DisplayName("getAggregationNode")
    class GetAggregationNodeTest {

        @Test
        @DisplayName("should find aggregation node for simple parallel gateway")
        void shouldFindAggregationNodeForSimpleParallelGateway() {
            BpmnNodeVo parallelNode = createTestNode("fork", Arrays.asList("task1", "task2"), null);
            BpmnNodeVo task1 = createTestNode("task1", Collections.singletonList("join"), "fork");
            BpmnNodeVo task2 = createTestNode("task2", Collections.singletonList("join"), "fork");
            BpmnNodeVo joinNode = createTestNode("join", Collections.singletonList("end"), "fork");

            List<BpmnNodeVo> nodes = Arrays.asList(task1, task2, joinNode);

            BpmnNodeVo result = BpmnUtils.getAggregationNode(parallelNode, nodes);
            assertNotNull(result);
            assertEquals("join", result.getNodeId());
        }

        @Test
        @DisplayName("should return null when no aggregation node exists")
        void shouldReturnNullWhenNoAggregationNodeExists() {
            BpmnNodeVo parallelNode = createTestNode("fork", Arrays.asList("task1", "task2"), null);
            BpmnNodeVo task1 = createTestNode("task1", Collections.singletonList("end"), "fork");
            BpmnNodeVo task2 = createTestNode("task2", Collections.singletonList("end"), "other");

            List<BpmnNodeVo> nodes = Arrays.asList(task1, task2);

            BpmnNodeVo result = BpmnUtils.getAggregationNode(parallelNode, nodes);
            assertNull(result);
        }

        @Test
        @DisplayName("should throw NullPointerException when parallelNode is null")
        void shouldThrowWhenParallelNodeIsNull() {
            BpmnNodeVo task1 = createTestNode("task1", Collections.singletonList("end"), "fork");

            assertThrows(NullPointerException.class, () -> BpmnUtils.getAggregationNode(null, Collections.singletonList(task1)));
        }

        @Test
        @DisplayName("should return null when bpmnNodeVos is empty")
        void shouldReturnNullWhenBpmnNodeVosIsEmpty() {
            BpmnNodeVo parallelNode = createTestNode("fork", Arrays.asList("task1", "task2"), null);

            BpmnNodeVo result = BpmnUtils.getAggregationNode(parallelNode, Collections.emptyList());
            assertNull(result);
        }

        @Test
        @DisplayName("should handle parallel gateway with multiple branches")
        void shouldHandleParallelGatewayWithMultipleBranches() {
            BpmnNodeVo parallelNode = createTestNode("fork", Arrays.asList("task1", "task2", "task3"), null);
            BpmnNodeVo task1 = createTestNode("task1", Collections.singletonList("join"), "fork");
            BpmnNodeVo task2 = createTestNode("task2", Collections.singletonList("join"), "fork");
            BpmnNodeVo task3 = createTestNode("task3", Collections.singletonList("join"), "fork");
            BpmnNodeVo joinNode = createTestNode("join", Collections.singletonList("end"), "fork");

            List<BpmnNodeVo> nodes = Arrays.asList(task1, task2, task3, joinNode);

            BpmnNodeVo result = BpmnUtils.getAggregationNode(parallelNode, nodes);
            assertNotNull(result);
            assertEquals("join", result.getNodeId());
        }
    }
}
