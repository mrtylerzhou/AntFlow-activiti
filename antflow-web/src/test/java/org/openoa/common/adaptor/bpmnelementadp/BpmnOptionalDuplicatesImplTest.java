package org.openoa.common.adaptor.bpmnelementadp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.vo.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BpmnOptionalDuplicatesImplTest extends MockBaseTest {

    private BpmnOptionalDuplicatesImpl optionalDuplicates;

    @BeforeEach
    void setUp() {
        optionalDuplicates = new BpmnOptionalDuplicatesImpl();
    }

    @Nested
    @DisplayName("optionalDuplicates - basic scenarios")
    class BasicScenariosTest {

        @Test
        @DisplayName("should return confVo without error when no customize node exists")
        void shouldReturnConfVoWhenNoCustomizeNode() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            BpmnNodeVo node1 = createSingleApproverNode("node1", "userB", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, node1, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            BpmnConfVo result = optionalDuplicates.optionalDuplicates(confVo, startConditions);

            assertNotNull(result);
            assertSame(confVo, result);
        }

        @Test
        @DisplayName("should not throw when nodes list has only start node")
        void shouldNotThrowWhenOnlyStartNode() {
            BpmnNodeVo startNode = createStartNode("start", null);

            BpmnConfVo confVo = buildConfVo(Collections.singletonList(startNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            assertDoesNotThrow(() -> optionalDuplicates.optionalDuplicates(confVo, startConditions));
        }

        @Test
        @DisplayName("should handle customize node with deduplication flag")
        void shouldHandleCustomizeNodeWithDeduplication() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            BpmnNodeVo customizeNode = createCustomizeNode("customize1", Arrays.asList("userA", "userB"), "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, customizeNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            BpmnConfVo result = optionalDuplicates.optionalDuplicates(confVo, startConditions);

            assertNotNull(result);
        }

        @Test
        @DisplayName("should handle multi-player node in node list")
        void shouldHandleMultiPlayerNode() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            BpmnNodeVo multiNode = createMultiApproverNode("multi1", Arrays.asList("userB", "userC"), "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, multiNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            BpmnConfVo result = optionalDuplicates.optionalDuplicates(confVo, startConditions);

            assertNotNull(result);
        }

        @Test
        @DisplayName("should handle customize node where all assignees are duplicates")
        void shouldHandleAllDuplicatesInCustomizeNode() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            BpmnNodeVo customizeNode = createCustomizeNode("customize1", Arrays.asList("userA", "userA"), "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, customizeNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            BpmnConfVo result = optionalDuplicates.optionalDuplicates(confVo, startConditions);

            assertNotNull(result);
        }

        @Test
        @DisplayName("should handle customize node where no assignees are duplicates")
        void shouldHandleNoDuplicatesInCustomizeNode() {
            BpmnNodeVo startNode = createStartNode("start", "userA");
            BpmnNodeVo customizeNode = createCustomizeNode("customize1", Arrays.asList("userC", "userD"), "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, customizeNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            BpmnConfVo result = optionalDuplicates.optionalDuplicates(confVo, startConditions);

            assertNotNull(result);
            assertEquals(0, customizeNode.getParams().getAssigneeList().get(0).getIsDeduplication());
            assertEquals(0, customizeNode.getParams().getAssigneeList().get(1).getIsDeduplication());
        }
    }

    private BpmnNodeVo createStartNode(String nodeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_START.getCode());
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(1);
        BpmnNodeParamsAssigneeVo assigneeVo = new BpmnNodeParamsAssigneeVo();
        assigneeVo.setAssignee("startUser");
        assigneeVo.setIsDeduplication(0);
        params.setAssignee(assigneeVo);
        params.setNodeTo(nextNodeId);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createSingleApproverNode(String nodeId, String assignee, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeProperty(5);
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(1);
        BpmnNodeParamsAssigneeVo assigneeVo = new BpmnNodeParamsAssigneeVo();
        assigneeVo.setAssignee(assignee);
        assigneeVo.setIsDeduplication(0);
        params.setAssignee(assigneeVo);
        params.setNodeTo(nextNodeId);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createMultiApproverNode(String nodeId, List<String> assignees, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeProperty(5);
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(2);
        List<BpmnNodeParamsAssigneeVo> assigneeList = new ArrayList<>();
        for (String a : assignees) {
            BpmnNodeParamsAssigneeVo vo = new BpmnNodeParamsAssigneeVo();
            vo.setAssignee(a);
            vo.setIsDeduplication(0);
            assigneeList.add(vo);
        }
        params.setAssigneeList(assigneeList);
        params.setNodeTo(nextNodeId);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createCustomizeNode(String nodeId, List<String> assignees, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeProperty(7);
        node.setIsDeduplication(1);
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(2);
        List<BpmnNodeParamsAssigneeVo> assigneeList = new ArrayList<>();
        for (String a : assignees) {
            BpmnNodeParamsAssigneeVo vo = new BpmnNodeParamsAssigneeVo();
            vo.setAssignee(a);
            vo.setIsDeduplication(0);
            assigneeList.add(vo);
        }
        params.setAssigneeList(assigneeList);
        params.setIsNodeDeduplication(0);
        params.setNodeTo(nextNodeId);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createEndNode(String nodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_COPY.getCode());
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(1);
        BpmnNodeParamsAssigneeVo assigneeVo = new BpmnNodeParamsAssigneeVo();
        assigneeVo.setAssignee("end_user");
        assigneeVo.setIsDeduplication(0);
        params.setAssignee(assigneeVo);
        node.setParams(params);
        return node;
    }

    private BpmnConfVo buildConfVo(List<BpmnNodeVo> nodes) {
        BpmnConfVo confVo = new BpmnConfVo();
        confVo.setNodes(nodes);
        return confVo;
    }
}
