package org.openoa.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.interf.ConditionService;
import org.openoa.base.vo.*;
import org.openoa.base.exception.AFBizException;
import org.openoa.MockBaseTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ConditionFilterServiceTest extends MockBaseTest {

    @InjectMocks
    private ConditionFilterService conditionFilterService;

    @Mock
    private ConditionService conditionService;

    @Nested
    @DisplayName("conditionfilterNode")
    class ConditionFilterNodeTest {
        @Test
        @DisplayName("should throw when nodes list is null and no start node")
        void shouldThrowWhenNoStartNode() {
            BpmnConfVo confVo = new BpmnConfVo();
            confVo.setBpmnCode("test_process");

            BpmnNodeVo approverNode = new BpmnNodeVo();
            approverNode.setNodeId("node1");
            approverNode.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
            approverNode.setNodeTo(Collections.singletonList("node2"));
            confVo.setNodes(Collections.singletonList(approverNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertThrows(AFBizException.class, () ->
                    conditionFilterService.conditionfilterNode(confVo, startConditions));
        }

        @Test
        @DisplayName("should throw when multiple start nodes exist")
        void shouldThrowWhenMultipleStartNodes() {
            BpmnConfVo confVo = new BpmnConfVo();
            BpmnNodeVo start1 = new BpmnNodeVo();
            start1.setNodeId("start1");
            start1.setNodeType(NodeTypeEnum.NODE_TYPE_START.getCode());
            start1.setNodeTo(Collections.singletonList("node1"));

            BpmnNodeVo start2 = new BpmnNodeVo();
            start2.setNodeId("start2");
            start2.setNodeType(NodeTypeEnum.NODE_TYPE_START.getCode());
            start2.setNodeTo(Collections.singletonList("node1"));

            confVo.setNodes(Arrays.asList(start1, start2));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertThrows(AFBizException.class, () ->
                    conditionFilterService.conditionfilterNode(confVo, startConditions));
        }

        @Test
        @DisplayName("should return early when nodes list is empty")
        void shouldReturnEarlyWhenEmpty() {
            BpmnConfVo confVo = new BpmnConfVo();
            confVo.setNodes(Collections.emptyList());

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertDoesNotThrow(() -> conditionFilterService.conditionfilterNode(confVo, startConditions));
        }

        @Test
        @DisplayName("should filter simple linear flow without conditions")
        void shouldFilterSimpleLinearFlow() {
            BpmnNodeVo startNode = createStartNode("start", "approver1");
            BpmnNodeVo approverNode = createApproverNode("approver1", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = new BpmnConfVo();
            confVo.setNodes(Arrays.asList(startNode, approverNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            conditionFilterService.conditionfilterNode(confVo, startConditions);

            assertNotNull(confVo.getNodes());
        }

        @Test
        @DisplayName("should filter flow with exclusive gateway - first condition matches")
        void shouldFilterWithExclusiveGateway_firstConditionMatches() {
            BpmnNodeVo startNode = createStartNode("start", "gw1");
            BpmnNodeVo gw = createGatewayNode("gw1", Arrays.asList("cond1", "cond2"), false, false);
            BpmnNodeVo cond1 = createConditionNode("cond1", "approver1", 1, 0);
            BpmnNodeVo cond2 = createConditionNode("cond2", "approver2", 2, 0);
            BpmnNodeVo approver1 = createApproverNode("approver1", "end");
            BpmnNodeVo approver2 = createApproverNode("approver2", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = new BpmnConfVo();
            confVo.setNodes(Arrays.asList(startNode, gw, cond1, cond2, approver1, approver2, endNode));

            when(conditionService.checkMatchCondition(eq(cond1), any(), any(), anyBoolean())).thenReturn(true);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            conditionFilterService.conditionfilterNode(confVo, startConditions);

            List<BpmnNodeVo> filtered = confVo.getNodes();
            assertTrue(filtered.stream().anyMatch(n -> n.getNodeId().equals("approver1")));
        }

        @Test
        @DisplayName("should filter flow with exclusive gateway - second condition matches")
        void shouldFilterWithExclusiveGateway_secondConditionMatches() {
            BpmnNodeVo startNode = createStartNode("start", "gw1");
            BpmnNodeVo gw = createGatewayNode("gw1", Arrays.asList("cond1", "cond2"), false, false);
            BpmnNodeVo cond1 = createConditionNode("cond1", "approver1", 1, 0);
            BpmnNodeVo cond2 = createConditionNode("cond2", "approver2", 2, 0);
            BpmnNodeVo approver1 = createApproverNode("approver1", "end");
            BpmnNodeVo approver2 = createApproverNode("approver2", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = new BpmnConfVo();
            confVo.setNodes(Arrays.asList(startNode, gw, cond1, cond2, approver1, approver2, endNode));

            when(conditionService.checkMatchCondition(eq(cond1), any(), any(), anyBoolean())).thenReturn(false);
            when(conditionService.checkMatchCondition(eq(cond2), any(), any(), anyBoolean())).thenReturn(true);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            conditionFilterService.conditionfilterNode(confVo, startConditions);

            List<BpmnNodeVo> filtered = confVo.getNodes();
            assertTrue(filtered.stream().anyMatch(n -> n.getNodeId().equals("approver2")));
        }

        @Test
        @DisplayName("should use default condition when no condition matches")
        void shouldUseDefaultConditionWhenNoMatch() {
            BpmnNodeVo startNode = createStartNode("start", "gw1");
            BpmnNodeVo gw = createGatewayNode("gw1", Arrays.asList("cond1", "condDefault"), false, false);
            BpmnNodeVo cond1 = createConditionNode("cond1", "approver1", 1, 0);
            BpmnNodeVo condDefault = createConditionNode("condDefault", "approverDefault", null, 1);
            BpmnNodeVo approver1 = createApproverNode("approver1", "end");
            BpmnNodeVo approverDefault = createApproverNode("approverDefault", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = new BpmnConfVo();
            confVo.setNodes(Arrays.asList(startNode, gw, cond1, condDefault, approver1, approverDefault, endNode));

            when(conditionService.checkMatchCondition(eq(cond1), any(), any(), anyBoolean())).thenReturn(false);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            conditionFilterService.conditionfilterNode(confVo, startConditions);

            List<BpmnNodeVo> filtered = confVo.getNodes();
            assertTrue(filtered.stream().anyMatch(n -> n.getNodeId().equals("approverDefault")));
        }

        @Test
        @DisplayName("should throw when no condition matches and no default")
        void shouldThrowWhenNoMatchAndNoDefault() {
            BpmnNodeVo startNode = createStartNode("start", "gw1");
            BpmnNodeVo gw = createGatewayNode("gw1", Arrays.asList("cond1", "cond2"), false, false);
            BpmnNodeVo cond1 = createConditionNode("cond1", "approver1", 1, 0);
            BpmnNodeVo cond2 = createConditionNode("cond2", "approver2", 2, 0);
            BpmnNodeVo approver1 = createApproverNode("approver1", "end");
            BpmnNodeVo approver2 = createApproverNode("approver2", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = new BpmnConfVo();
            confVo.setNodes(Arrays.asList(startNode, gw, cond1, cond2, approver1, approver2, endNode));

            when(conditionService.checkMatchCondition(any(), any(), any(), anyBoolean())).thenReturn(false);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            assertThrows(AFBizException.class,
                    () -> conditionFilterService.conditionfilterNode(confVo, startConditions));
        }

        @Test
        @DisplayName("should handle exclusive gateway with dynamic condition")
        void shouldHandleExclusiveGatewayWithDynamicCondition() {
            BpmnNodeVo startNode = createStartNode("start", "gw1");
            BpmnNodeVo gw = createGatewayNode("gw1", Arrays.asList("cond1", "cond2"), false, true);
            BpmnNodeVo cond1 = createConditionNode("cond1", "approver1", 1, 0);
            BpmnNodeVo cond2 = createConditionNode("cond2", "approver2", 2, 0);
            BpmnNodeVo approver1 = createApproverNode("approver1", "end");
            BpmnNodeVo approver2 = createApproverNode("approver2", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = new BpmnConfVo();
            confVo.setNodes(Arrays.asList(startNode, gw, cond1, cond2, approver1, approver2, endNode));

            when(conditionService.checkMatchCondition(eq(cond1), any(), any(), eq(true))).thenReturn(true);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            conditionFilterService.conditionfilterNode(confVo, startConditions);

            List<BpmnNodeVo> filtered = confVo.getNodes();
            assertTrue(filtered.stream().anyMatch(n -> n.getNodeId().equals("approver1")));
        }

        @Test
        @DisplayName("should handle condition priority - lower sort value evaluated first")
        void shouldHandleConditionPriority() {
            BpmnNodeVo startNode = createStartNode("start", "gw1");
            BpmnNodeVo gw = createGatewayNode("gw1", Arrays.asList("cond1", "cond2"), false, false);
            BpmnNodeVo cond1 = createConditionNode("cond1", "approver1", 1, 0);
            BpmnNodeVo cond2 = createConditionNode("cond2", "approver2", 2, 0);
            BpmnNodeVo approver1 = createApproverNode("approver1", "end");
            BpmnNodeVo approver2 = createApproverNode("approver2", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = new BpmnConfVo();
            confVo.setNodes(Arrays.asList(startNode, gw, cond1, cond2, approver1, approver2, endNode));

            when(conditionService.checkMatchCondition(any(), any(), any(), anyBoolean())).thenReturn(true);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            conditionFilterService.conditionfilterNode(confVo, startConditions);

            List<BpmnNodeVo> filtered = confVo.getNodes();
            assertTrue(filtered.stream().anyMatch(n -> n.getNodeId().equals("approver1")));
            assertFalse(filtered.stream().anyMatch(n -> n.getNodeId().equals("approver2")));
        }
    }

    private BpmnNodeVo createStartNode(String nodeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_START.getCode());
        node.setNodeTo(Collections.singletonList(nextNodeId));
        return node;
    }

    private BpmnNodeVo createApproverNode(String nodeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeProperty(NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode());
        node.setNodeTo(nextNodeId != null ? Collections.singletonList(nextNodeId) : Collections.emptyList());
        return node;
    }

    private BpmnNodeVo createEndNode(String nodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_COPY.getCode());
        node.setNodeTo(Collections.emptyList());
        return node;
    }

    private BpmnNodeVo createGatewayNode(String nodeId, List<String> nodeToIds, boolean isParallel, boolean isDynamicCondition) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_GATEWAY.getCode());
        node.setNodeTo(nodeToIds);
        node.setIsParallel(isParallel);
        node.setIsDynamicCondition(isDynamicCondition);
        return node;
    }

    private BpmnNodeVo createParallelGatewayNode(String nodeId, List<String> nodeToIds) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode());
        node.setNodeTo(nodeToIds);
        return node;
    }

    private BpmnNodeVo createConditionNode(String nodeId, String nextNodeId, Integer sort, Integer isDefault) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_CONDITIONS.getCode());
        node.setNodeTo(Collections.singletonList(nextNodeId));

        BpmnNodePropertysVo property = new BpmnNodePropertysVo();
        BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
        conditionsConf.setIsDefault(isDefault != null ? isDefault : 0);
        conditionsConf.setSort(sort);
        property.setConditionsConf(conditionsConf);
        node.setProperty(property);

        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setNodeTo(nextNodeId);
        node.setParams(params);
        return node;
    }
}
