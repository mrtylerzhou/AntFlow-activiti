package org.openoa.common.formatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class AbstractBpmnRemoveFormatTest extends MockBaseTest {

    private static class TestBpmnRemoveFormat extends AbstractBpmnRemoveFormat {

        private final Predicate<BpmnNodeVo> removePredicate;

        TestBpmnRemoveFormat(Predicate<BpmnNodeVo> removePredicate) {
            this.removePredicate = removePredicate;
        }

        @Override
        public int order() {
            return 0;
        }

        @Override
        public List<Supplier<Boolean>> trueSuppliers(BpmnNodeVo nodeVo, BpmnStartConditionsVo bpmnStartConditionsVo) {
            Supplier<Boolean> supplier = () -> removePredicate.test(nodeVo);
            return Collections.singletonList(supplier);
        }
    }

    private BpmnStartConditionsVo startConditions;

    @BeforeEach
    void setUp() {
        startConditions = new BpmnStartConditionsVo();
    }

    @Nested
    @DisplayName("removeBpmnConf linear flow")
    class LinearFlowTest {

        @Test
        @DisplayName("should remove node A when predicate matches, rewiring start to B")
        void shouldRemoveNodeA_RewiringStartToB() {
            BpmnNodeVo startNode = createStartNode("start", "A");
            BpmnNodeVo nodeA = createApproverNode("A", "B");
            BpmnNodeVo nodeB = createApproverNode("B", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, nodeA, nodeB, endNode));

            AbstractBpmnRemoveFormat format = new TestBpmnRemoveFormat(vo -> "A".equals(vo.getNodeId()));
            format.removeBpmnConf(confVo, startConditions);

            assertEquals("B", startNode.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should not change chain when no nodes match predicate")
        void shouldNotChangeChainWhenNoNodesMatch() {
            BpmnNodeVo startNode = createStartNode("start", "A");
            BpmnNodeVo nodeA = createApproverNode("A", "B");
            BpmnNodeVo nodeB = createApproverNode("B", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, nodeA, nodeB, endNode));

            AbstractBpmnRemoveFormat format = new TestBpmnRemoveFormat(vo -> false);
            format.removeBpmnConf(confVo, startConditions);

            assertEquals("A", startNode.getParams().getNodeTo());
            assertEquals("B", nodeA.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should remove middle node B, rewiring A to C")
        void shouldRemoveMiddleNodeB_RewiringAToC() {
            BpmnNodeVo startNode = createStartNode("start", "A");
            BpmnNodeVo nodeA = createApproverNode("A", "B");
            BpmnNodeVo nodeB = createApproverNode("B", "C");
            BpmnNodeVo nodeC = createApproverNode("C", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, nodeA, nodeB, nodeC, endNode));

            AbstractBpmnRemoveFormat format = new TestBpmnRemoveFormat(vo -> "B".equals(vo.getNodeId()));
            format.removeBpmnConf(confVo, startConditions);

            assertEquals("A", startNode.getParams().getNodeTo());
            assertEquals("C", nodeA.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should not remove node when trueSuppliers returns empty list")
        void shouldNotRemoveNodeWhenTrueSuppliersEmpty() {
            AbstractBpmnRemoveFormat format = new AbstractBpmnRemoveFormat() {
                @Override
                public int order() {
                    return 0;
                }

                @Override
                public List<Supplier<Boolean>> trueSuppliers(BpmnNodeVo nodeVo, BpmnStartConditionsVo bpmnStartConditionsVo) {
                    return Collections.emptyList();
                }
            };

            BpmnNodeVo startNode = createStartNode("start", "A");
            BpmnNodeVo nodeA = createApproverNode("A", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, nodeA, endNode));

            format.removeBpmnConf(confVo, startConditions);

            assertEquals("A", startNode.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should remove node when one of multiple suppliers returns true")
        void shouldRemoveNodeWhenOneOfMultipleSuppliersTrue() {
            AbstractBpmnRemoveFormat format = new AbstractBpmnRemoveFormat() {
                @Override
                public int order() {
                    return 0;
                }

                @Override
                public List<Supplier<Boolean>> trueSuppliers(BpmnNodeVo nodeVo, BpmnStartConditionsVo bpmnStartConditionsVo) {
                    Supplier<Boolean> alwaysFalse = () -> false;
                    Supplier<Boolean> alwaysTrue = () -> "A".equals(nodeVo.getNodeId());
                    Supplier<Boolean> anotherFalse = () -> false;
                    return Arrays.asList(alwaysFalse, alwaysTrue, anotherFalse);
                }
            };

            BpmnNodeVo startNode = createStartNode("start", "A");
            BpmnNodeVo nodeA = createApproverNode("A", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, nodeA, endNode));

            format.removeBpmnConf(confVo, startConditions);

            assertEquals("end", startNode.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should not remove start node even if predicate matches all nodes")
        void shouldNotRemoveStartNodeEvenIfPredicateMatchesAllNodes() {
            BpmnNodeVo startNode = createStartNode("start", "A");
            BpmnNodeVo nodeA = createApproverNode("A", "B");
            BpmnNodeVo nodeB = createApproverNode("B", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, nodeA, nodeB, endNode));

            AbstractBpmnRemoveFormat format = new TestBpmnRemoveFormat(vo -> true);
            format.removeBpmnConf(confVo, startConditions);

            assertEquals(NodeTypeEnum.NODE_TYPE_START.getCode(), startNode.getNodeType());
            assertEquals("B", startNode.getParams().getNodeTo());
            assertEquals("end", nodeA.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should terminate loop correctly when last node has null nodeTo")
        void shouldTerminateLoopCorrectlyWhenLastNodeHasNullNodeTo() {
            BpmnNodeVo startNode = createStartNode("start", "A");
            BpmnNodeVo nodeA = createApproverNode("A", null);

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, nodeA));

            AbstractBpmnRemoveFormat format = new TestBpmnRemoveFormat(vo -> false);
            format.removeBpmnConf(confVo, startConditions);

            assertEquals("A", startNode.getParams().getNodeTo());
            assertNull(nodeA.getParams().getNodeTo());
        }
    }

    private BpmnNodeVo createStartNode(String nodeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_START.getCode());
        node.setNodeTo(nextNodeId != null ? Collections.singletonList(nextNodeId) : Collections.emptyList());
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
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setNodeTo(nextNodeId);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createEndNode(String nodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeTo(Collections.emptyList());
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
