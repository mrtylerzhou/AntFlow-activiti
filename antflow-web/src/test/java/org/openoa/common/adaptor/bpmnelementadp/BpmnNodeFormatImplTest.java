package org.openoa.common.adaptor.bpmnelementadp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.BpmnNodeParamTypeEnum;
import org.openoa.base.constant.enums.ElementTypeEnum;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.base.vo.BpmnConfCommonVo;
import org.openoa.base.vo.BpmnNodeButtonConfBaseVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BpmnNodeFormatImplTest extends MockBaseTest {

    private BpmnNodeFormatImpl bpmnNodeFormat;

    @BeforeEach
    void setUp() {
        bpmnNodeFormat = new BpmnNodeFormatImpl();
    }

    @Nested
    @DisplayName("getBpmnConfCommonElementVoList")
    class GetBpmnConfCommonElementVoListTest {

        @Test
        @DisplayName("empty nodes list should return empty list")
        void emptyNodesShouldReturnEmptyList() {
            BpmnConfCommonVo confCommonVo = new BpmnConfCommonVo();
            confCommonVo.setProcessNum("PROC_001");

            List<BpmnConfCommonElementVo> result = bpmnNodeFormat.getBpmnConfCommonElementVoList(
                    confCommonVo, Collections.emptyList(), new BpmnStartConditionsVo());

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("null nodes list should return empty list")
        void nullNodesShouldReturnEmptyList() {
            BpmnConfCommonVo confCommonVo = new BpmnConfCommonVo();
            confCommonVo.setProcessNum("PROC_001");

            List<BpmnConfCommonElementVo> result = bpmnNodeFormat.getBpmnConfCommonElementVoList(
                    confCommonVo, null, new BpmnStartConditionsVo());

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("no start node should throw AFBizException")
        void noStartNodeShouldThrowAFBizException() {
            BpmnConfCommonVo confCommonVo = new BpmnConfCommonVo();
            confCommonVo.setProcessNum("PROC_001");

            BpmnNodeVo approverNode = createApproverNode("approver1", "Approver", "endNode");

            try (MockedStatic<SpringBeanUtils> mockedSpringBeanUtils = mockStatic(SpringBeanUtils.class)) {
                mockedSpringBeanUtils.when(() -> SpringBeanUtils.getBeans(BpmnElementAdaptor.class))
                        .thenReturn(Collections.emptyList());

                assertThrows(AFBizException.class, () ->
                        bpmnNodeFormat.getBpmnConfCommonElementVoList(
                                confCommonVo,
                                Collections.singletonList(approverNode),
                                new BpmnStartConditionsVo()));
            }
        }

        @Test
        @DisplayName("simple linear flow should produce start event, start user, approver element, end event and sequence flows")
        void simpleLinearFlowShouldProduceCompleteElementList() {
            BpmnConfCommonVo confCommonVo = new BpmnConfCommonVo();
            confCommonVo.setProcessNum("PROC_001");

            BpmnNodeVo startNode = createStartNode("start", "approver1");
            BpmnNodeVo approverNode = createApproverNode("approver1", "Approver", null);
            approverNode.setNodeFrom("start");

            List<BpmnNodeVo> nodes = new ArrayList<>();
            nodes.add(startNode);
            nodes.add(approverNode);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setStartUserId("user001");
            startConditions.setStartUserName("Zhang San");

            BpmnElementAdaptor mockAdaptor = mock(BpmnElementAdaptor.class);

            try (MockedStatic<SpringBeanUtils> mockedSpringBeanUtils = mockStatic(SpringBeanUtils.class)) {
                mockedSpringBeanUtils.when(() -> SpringBeanUtils.getBeans(BpmnElementAdaptor.class))
                        .thenReturn(Collections.singletonList(mockAdaptor));

                when(mockAdaptor.isSupportBusinessObject(any())).thenReturn(false);

                List<BpmnConfCommonElementVo> result = bpmnNodeFormat.getBpmnConfCommonElementVoList(
                        confCommonVo, nodes, startConditions);

                assertFalse(result.isEmpty());

                boolean hasStartEvent = result.stream().anyMatch(e ->
                        e.getElementType() != null && e.getElementType().equals(ElementTypeEnum.ELEMENT_TYPE_START_EVENT.getCode()));
                assertTrue(hasStartEvent);

                boolean hasEndEvent = result.stream().anyMatch(e ->
                        e.getElementType() != null && e.getElementType().equals(ElementTypeEnum.ELEMENT_TYPE_END_EVENT.getCode()));
                assertTrue(hasEndEvent);

                long sequenceFlowCount = result.stream().filter(e ->
                        e.getElementType() != null && e.getElementType().equals(ElementTypeEnum.ELEMENT_TYPE_SEQUENCE_FLOW.getCode()))
                        .count();
                assertTrue(sequenceFlowCount >= 2);
            }
        }

        @Test
        @DisplayName("start user element should have assignee from startConditions")
        void startUserElementShouldHaveAssigneeFromStartConditions() {
            BpmnConfCommonVo confCommonVo = new BpmnConfCommonVo();
            confCommonVo.setProcessNum("PROC_002");

            BpmnNodeVo startNode = createStartNode("start", "approver1");
            BpmnNodeVo approverNode = createApproverNode("approver1", "Approver", null);
            approverNode.setNodeFrom("start");

            List<BpmnNodeVo> nodes = new ArrayList<>();
            nodes.add(startNode);
            nodes.add(approverNode);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setStartUserId("user999");
            startConditions.setStartUserName("Wang Wu");

            BpmnElementAdaptor mockAdaptor = mock(BpmnElementAdaptor.class);

            try (MockedStatic<SpringBeanUtils> mockedSpringBeanUtils = mockStatic(SpringBeanUtils.class)) {
                mockedSpringBeanUtils.when(() -> SpringBeanUtils.getBeans(BpmnElementAdaptor.class))
                        .thenReturn(Collections.singletonList(mockAdaptor));

                when(mockAdaptor.isSupportBusinessObject(any())).thenReturn(false);

                List<BpmnConfCommonElementVo> result = bpmnNodeFormat.getBpmnConfCommonElementVoList(
                        confCommonVo, nodes, startConditions);

                BpmnConfCommonElementVo startUserElement = result.stream()
                        .filter(e -> e.getAssigneeParamValue() != null && e.getAssigneeParamValue().equals("user999"))
                        .findFirst()
                        .orElse(null);

                assertNotNull(startUserElement);
                assertEquals("user999", startUserElement.getAssigneeParamValue());
            }
        }

        @Test
        @DisplayName("start event element ID should follow pattern processNum_startEvent")
        void startEventElementIdShouldFollowPattern() {
            BpmnConfCommonVo confCommonVo = new BpmnConfCommonVo();
            confCommonVo.setProcessNum("PROC_003");

            BpmnNodeVo startNode = createStartNode("start", "approver1");
            BpmnNodeVo approverNode = createApproverNode("approver1", "Approver", null);
            approverNode.setNodeFrom("start");

            List<BpmnNodeVo> nodes = new ArrayList<>();
            nodes.add(startNode);
            nodes.add(approverNode);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setStartUserId("user001");
            startConditions.setStartUserName("Zhang San");

            BpmnElementAdaptor mockAdaptor = mock(BpmnElementAdaptor.class);

            try (MockedStatic<SpringBeanUtils> mockedSpringBeanUtils = mockStatic(SpringBeanUtils.class)) {
                mockedSpringBeanUtils.when(() -> SpringBeanUtils.getBeans(BpmnElementAdaptor.class))
                        .thenReturn(Collections.singletonList(mockAdaptor));

                when(mockAdaptor.isSupportBusinessObject(any())).thenReturn(false);

                List<BpmnConfCommonElementVo> result = bpmnNodeFormat.getBpmnConfCommonElementVoList(
                        confCommonVo, nodes, startConditions);

                String expectedStartEventId = "PROC_003_" + ElementTypeEnum.ELEMENT_TYPE_START_EVENT.getDesc();
                BpmnConfCommonElementVo startEvent = result.stream()
                        .filter(e -> e.getElementType() != null && e.getElementType().equals(ElementTypeEnum.ELEMENT_TYPE_START_EVENT.getCode()))
                        .findFirst()
                        .orElse(null);

                assertNotNull(startEvent);
                assertEquals(expectedStartEventId, startEvent.getElementId());
            }
        }
    }

    private BpmnNodeButtonConfBaseVo createButtons() {
        BpmnNodeButtonConfBaseVo buttons = new BpmnNodeButtonConfBaseVo();
        buttons.setStartPage(new ArrayList<>());
        buttons.setApprovalPage(new ArrayList<>());
        buttons.setViewPage(new ArrayList<>());
        return buttons;
    }

    private BpmnNodeVo createStartNode(String nodeId, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setId(1L);
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_START.getCode());
        node.setNodeName("Start Node");
        node.setNodeTo(nextNodeId != null ? Collections.singletonList(nextNodeId) : Collections.emptyList());

        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE.getCode());
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee("startUser");
        params.setAssignee(assignee);
        params.setNodeTo(nextNodeId);
        node.setParams(params);

        BpmnNodePropertysVo property = new BpmnNodePropertysVo();
        node.setProperty(property);

        node.setButtons(createButtons());

        return node;
    }

    private BpmnNodeVo createApproverNode(String nodeId, String nodeName, String nextNodeId) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setId(2L);
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeName(nodeName);
        node.setNodeTo(nextNodeId != null ? Collections.singletonList(nextNodeId) : Collections.emptyList());

        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE.getCode());
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee("approverUser");
        params.setAssignee(assignee);
        params.setNodeTo(nextNodeId);
        params.setIsNodeDeduplication(0);
        node.setParams(params);

        BpmnNodePropertysVo property = new BpmnNodePropertysVo();
        node.setProperty(property);

        node.setButtons(createButtons());

        return node;
    }
}
