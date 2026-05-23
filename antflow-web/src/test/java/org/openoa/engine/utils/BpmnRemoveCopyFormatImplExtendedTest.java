package org.openoa.engine.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.engine.bpmnconf.adp.formatter.BpmnRemoveCopyFormatImpl;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BpmnRemoveCopyFormatImplExtendedTest extends BaseTest {

    private BpmnRemoveCopyFormatImpl removeCopyFormat;

    @BeforeEach
    void setUp() {
        removeCopyFormat = new BpmnRemoveCopyFormatImpl();
    }

    @Nested
    @DisplayName("removeBpmnConf with copy nodes")
    class RemoveCopyNodeTest {

        @Test
        @DisplayName("should remove copy node and rewire chain")
        void shouldRemoveCopyNodeAndRewire() {
            BpmnNodeVo startNode = createStartNode("start", "copy1");
            BpmnNodeVo copyNode = createCopyNode("copy1", "approver1");
            BpmnNodeVo approverNode = createApproverNode("approver1", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, copyNode, approverNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(false);

            removeCopyFormat.removeBpmnConf(confVo, startConditions);

            assertEquals("approver1", startNode.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should not remove copy node in preview mode")
        void shouldNotRemoveCopyNodeInPreviewMode() {
            BpmnNodeVo startNode = createStartNode("start", "copy1");
            BpmnNodeVo copyNode = createCopyNode("copy1", "approver1");
            BpmnNodeVo approverNode = createApproverNode("approver1", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, copyNode, approverNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(true);

            removeCopyFormat.removeBpmnConf(confVo, startConditions);

            assertEquals("copy1", startNode.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should remove consecutive copy nodes")
        void shouldRemoveConsecutiveCopyNodes() {
            BpmnNodeVo startNode = createStartNode("start", "copy1");
            BpmnNodeVo copyNode1 = createCopyNode("copy1", "copy2");
            BpmnNodeVo copyNode2 = createCopyNode("copy2", "approver1");
            BpmnNodeVo approverNode = createApproverNode("approver1", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, copyNode1, copyNode2, approverNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(false);

            removeCopyFormat.removeBpmnConf(confVo, startConditions);

            assertEquals("copy2", startNode.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should not remove non-copy nodes")
        void shouldNotRemoveNonCopyNodes() {
            BpmnNodeVo startNode = createStartNode("start", "approver1");
            BpmnNodeVo approverNode = createApproverNode("approver1", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, approverNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(false);

            removeCopyFormat.removeBpmnConf(confVo, startConditions);

            assertEquals("approver1", startNode.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should transfer empToForwardList when removing copy node")
        void shouldTransferEmpToForwardList() {
            BpmnNodeVo startNode = createStartNode("start", "copy1");
            BpmnNodeVo copyNode = createCopyNode("copy1", "approver1", Arrays.asList("user1", "user2"));
            BpmnNodeVo approverNode = createApproverNode("approver1", "end");
            BpmnNodeVo endNode = createEndNode("end");

            BpmnConfVo confVo = buildConfVo(Arrays.asList(startNode, copyNode, approverNode, endNode));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPreview(false);

            removeCopyFormat.removeBpmnConf(confVo, startConditions);

            assertNotNull(startConditions.getEmpToForwardList());
            assertTrue(startConditions.getEmpToForwardList().contains("user1"));
            assertTrue(startConditions.getEmpToForwardList().contains("user2"));
        }
    }

    private BpmnNodeVo createStartNode(String nodeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_START.getCode());
        node.setNodeTo(Collections.singletonList(nextNodeId));
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setNodeTo(nextNodeId);
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createCopyNode(String nodeId, String nextNodeId) {
        return createCopyNode(nodeId, nextNodeId, new ArrayList<>());
    }

    private BpmnNodeVo createCopyNode(String nodeId, String nextNodeId, List<String> emplIds) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_COPY.getCode());
        node.setNodeTo(Collections.singletonList(nextNodeId));
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setNodeTo(nextNodeId);
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee("copyUser");
        params.setAssignee(assignee);
        params.setParamType(1);
        node.setParams(params);
        BpmnNodePropertysVo property = new BpmnNodePropertysVo();
        property.setEmplList(new ArrayList<>());
        property.setEmplIds(emplIds);
        node.setProperty(property);
        return node;
    }

    private BpmnNodeVo createApproverNode(String nodeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeTo(nextNodeId != null ? Collections.singletonList(nextNodeId) : Collections.emptyList());
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setNodeTo(nextNodeId);
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee("approverUser");
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
