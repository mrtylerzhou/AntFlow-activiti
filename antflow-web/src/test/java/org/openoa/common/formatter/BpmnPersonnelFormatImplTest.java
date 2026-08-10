package org.openoa.common.formatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.constant.enums.PersonnelEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BpmnPersonnelFormatImplTest extends MockBaseTest {

    private BpmnPersonnelFormatImpl bpmnPersonnelFormat = new BpmnPersonnelFormatImpl();

    private BpmnConfVo bpmnConfVo;
    private BpmnStartConditionsVo bpmnStartConditions;

    @BeforeEach
    void setUp() {
        bpmnConfVo = new BpmnConfVo();
        bpmnStartConditions = new BpmnStartConditionsVo();
        bpmnStartConditions.setStartUserId("user001");
        bpmnStartConditions.setStartUserName("TestUser");
    }

    private BpmnNodeVo createStartNode(String nodeId, List<String> nodeTo) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_START.getCode());
        node.setNodeTo(nodeTo);
        return node;
    }

    private BpmnNodeVo createApproverNode(String nodeId, Integer nodeProperty, List<String> nodeTo) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_APPROVER.getCode());
        node.setNodeProperty(nodeProperty);
        node.setNodeTo(nodeTo);
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createCopyNode(String nodeId, Integer nodeProperty, List<String> nodeTo) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_COPY.getCode());
        node.setNodeProperty(nodeProperty);
        node.setNodeTo(nodeTo);
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        node.setParams(params);
        return node;
    }

    private BpmnNodeVo createParallelGatewayNode(String nodeId, List<String> nodeTo) {
        BpmnNodeVo node = new BpmnNodeVo();
        node.setNodeId(nodeId);
        node.setNodeType(NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode());
        node.setNodeTo(nodeTo);
        return node;
    }

    @Nested
    @DisplayName("formatPersonnelsConf validation")
    class ValidationTest {

        @Test
        @DisplayName("should throw AFBizException when bpmnConfVo is null")
        void shouldThrowWhenBpmnConfVoNull() {
            assertThrows(AFBizException.class,
                    () -> bpmnPersonnelFormat.formatPersonnelsConf(null, bpmnStartConditions));
        }

        @Test
        @DisplayName("should throw AFBizException when bpmnStartConditions is null")
        void shouldThrowWhenBpmnStartConditionsNull() {
            assertThrows(AFBizException.class,
                    () -> bpmnPersonnelFormat.formatPersonnelsConf(bpmnConfVo, null));
        }

        @Test
        @DisplayName("should throw AFBizException when nodes list is empty")
        void shouldThrowWhenNodesEmpty() {
            bpmnConfVo.setNodes(Collections.emptyList());

            assertThrows(AFBizException.class,
                    () -> bpmnPersonnelFormat.formatPersonnelsConf(bpmnConfVo, bpmnStartConditions));
        }

        @Test
        @DisplayName("should throw AFBizException when startUserId is null")
        void shouldThrowWhenStartUserIdNull() {
            bpmnStartConditions.setStartUserId(null);
            BpmnNodeVo startNode = createStartNode("start1", null);
            bpmnConfVo.setNodes(Collections.singletonList(startNode));

            assertThrows(AFBizException.class,
                    () -> bpmnPersonnelFormat.formatPersonnelsConf(bpmnConfVo, bpmnStartConditions));
        }

        @Test
        @DisplayName("should throw AFBizException when no start node found")
        void shouldThrowWhenNoStartNode() {
            BpmnNodeVo approverNode = createApproverNode("node1",
                    NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode(), null);
            bpmnConfVo.setNodes(Collections.singletonList(approverNode));

            assertThrows(AFBizException.class,
                    () -> bpmnPersonnelFormat.formatPersonnelsConf(bpmnConfVo, bpmnStartConditions));
        }
    }

    @Nested
    @DisplayName("formatPersonnelsConf start node handling")
    class StartNodeTest {

        @Test
        @DisplayName("should set start node assignee from start conditions")
        void shouldSetStartNodeAssignee() {
            BpmnNodeVo startNode = createStartNode("start1", null);
            BpmnNodeParamsVo params = new BpmnNodeParamsVo();
            startNode.setParams(params);
            bpmnConfVo.setNodes(Collections.singletonList(startNode));

            BpmnConfVo result = bpmnPersonnelFormat.formatPersonnelsConf(bpmnConfVo, bpmnStartConditions);

            BpmnNodeVo resultStartNode = result.getNodes().stream()
                    .filter(n -> n.getNodeType().equals(NodeTypeEnum.NODE_TYPE_START.getCode()))
                    .findFirst().orElse(null);
            assertNotNull(resultStartNode);
            assertNotNull(resultStartNode.getParams());
            assertNotNull(resultStartNode.getParams().getAssignee());
            assertEquals("user001", resultStartNode.getParams().getAssignee().getAssignee());
            assertEquals("发起人", resultStartNode.getParams().getAssignee().getElementName());
        }
    }

    @Nested
    @DisplayName("formatPersonnelsConf node chain processing")
    class NodeChainTest {

        @Test
        @DisplayName("should call personnel adaptor for approver node")
        void shouldCallPersonnelAdaptorForApproverNode() {
            BpmnNodeVo startNode = createStartNode("start1", Collections.singletonList("approver1"));
            BpmnNodeParamsVo startParams = new BpmnNodeParamsVo();
            startParams.setNodeTo("approver1");
            startNode.setParams(startParams);

            BpmnNodeVo approverNode = createApproverNode("approver1",
                    NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode(), null);

            bpmnConfVo.setNodes(Arrays.asList(startNode, approverNode));

            AbstractBpmnPersonnelAdaptor mockAdaptor = mock(AbstractBpmnPersonnelAdaptor.class);
            when(mockAdaptor.isSupportBusinessObject(PersonnelEnum.USERAPPOINTED_PERSONNEL)).thenReturn(true);
            doNothing().when(mockAdaptor).setNodeParams(any(), any(), any(), any(), any(), any());

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(AbstractBpmnPersonnelAdaptor.class))
                        .thenReturn(Collections.singletonList(mockAdaptor));

                bpmnPersonnelFormat.formatPersonnelsConf(bpmnConfVo, bpmnStartConditions);

                verify(mockAdaptor).setNodeParams(eq(approverNode), eq(bpmnStartConditions), any(), any(), any(), any());
            }
        }

        @Test
        @DisplayName("should return early when process has only start node")
        void shouldReturnEarlyWhenOnlyStartNode() {
            BpmnNodeVo startNode = createStartNode("start1", null);
            BpmnNodeParamsVo startParams = new BpmnNodeParamsVo();
            startNode.setParams(startParams);
            bpmnConfVo.setNodes(Collections.singletonList(startNode));

            BpmnConfVo result = bpmnPersonnelFormat.formatPersonnelsConf(bpmnConfVo, bpmnStartConditions);

            BpmnNodeVo resultStartNode = result.getNodes().stream()
                    .filter(n -> n.getNodeType().equals(NodeTypeEnum.NODE_TYPE_START.getCode()))
                    .findFirst().orElse(null);
            assertNotNull(resultStartNode);
            assertEquals("user001", resultStartNode.getParams().getAssignee().getAssignee());
            assertEquals("发起人", resultStartNode.getParams().getAssignee().getElementName());
        }
    }

    @Nested
    @DisplayName("formatPersonnelsConf copy node handling")
    class CopyNodeTest {

        @Test
        @DisplayName("should call personnel adaptor for copy node")
        void shouldCallPersonnelAdaptorForCopyNode() {
            BpmnNodeVo startNode = createStartNode("start1", Collections.singletonList("copy1"));
            BpmnNodeParamsVo startParams = new BpmnNodeParamsVo();
            startParams.setNodeTo("copy1");
            startNode.setParams(startParams);

            BpmnNodeVo copyNode = createCopyNode("copy1",
                    NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode(), null);

            bpmnConfVo.setNodes(Arrays.asList(startNode, copyNode));

            AbstractBpmnPersonnelAdaptor mockAdaptor = mock(AbstractBpmnPersonnelAdaptor.class);
            when(mockAdaptor.isSupportBusinessObject(PersonnelEnum.USERAPPOINTED_PERSONNEL)).thenReturn(true);
            doNothing().when(mockAdaptor).setNodeParams(any(), any(), any(), any(), any(), any());

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(AbstractBpmnPersonnelAdaptor.class))
                        .thenReturn(Collections.singletonList(mockAdaptor));

                bpmnPersonnelFormat.formatPersonnelsConf(bpmnConfVo, bpmnStartConditions);

                verify(mockAdaptor).setNodeParams(eq(copyNode), eq(bpmnStartConditions), any(), any(), any(), any());
            }
        }
    }

    @Nested
    @DisplayName("formatPersonnelsConf with multiple approver nodes")
    class MultipleApproverNodesTest {

        @Test
        @DisplayName("should process two approver nodes in sequence")
        void shouldProcessTwoApproverNodes() {
            BpmnNodeVo startNode = createStartNode("start1", Collections.singletonList("approver1"));
            BpmnNodeParamsVo startParams = new BpmnNodeParamsVo();
            startParams.setNodeTo("approver1");
            startNode.setParams(startParams);

            BpmnNodeVo approver1 = createApproverNode("approver1",
                    NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode(), Collections.singletonList("approver2"));
            approver1.getParams().setNodeTo("approver2");

            BpmnNodeVo approver2 = createApproverNode("approver2",
                    NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode(), null);

            bpmnConfVo.setNodes(Arrays.asList(startNode, approver1, approver2));

            AbstractBpmnPersonnelAdaptor mockAdaptor = mock(AbstractBpmnPersonnelAdaptor.class);
            when(mockAdaptor.isSupportBusinessObject(PersonnelEnum.USERAPPOINTED_PERSONNEL)).thenReturn(true);
            doNothing().when(mockAdaptor).setNodeParams(any(), any(), any(), any(), any(), any());

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(AbstractBpmnPersonnelAdaptor.class))
                        .thenReturn(Collections.singletonList(mockAdaptor));

                BpmnConfVo result = bpmnPersonnelFormat.formatPersonnelsConf(bpmnConfVo, bpmnStartConditions);

                assertNotNull(result);
            }
        }
    }

    @Nested
    @DisplayName("formatPersonnelsConf start node assignee details")
    class StartNodeAssigneeDetailsTest {

        @Test
        @DisplayName("start node assignee should have correct elementName")
        void startNodeAssigneeShouldHaveCorrectElementName() {
            BpmnNodeVo startNode = createStartNode("start1", null);
            BpmnNodeParamsVo startParams = new BpmnNodeParamsVo();
            startNode.setParams(startParams);
            bpmnConfVo.setNodes(Collections.singletonList(startNode));

            bpmnStartConditions.setStartUserId("admin001");
            bpmnStartConditions.setStartUserName("Admin User");

            BpmnConfVo result = bpmnPersonnelFormat.formatPersonnelsConf(bpmnConfVo, bpmnStartConditions);

            BpmnNodeVo resultStartNode = result.getNodes().get(0);
            assertEquals("admin001", resultStartNode.getParams().getAssignee().getAssignee());
            assertEquals("发起人", resultStartNode.getParams().getAssignee().getElementName());
        }

        @Test
        @DisplayName("start node assignee should use startUserId from conditions")
        void startNodeAssigneeShouldUseStartUserId() {
            BpmnNodeVo startNode = createStartNode("start1", null);
            BpmnNodeParamsVo startParams = new BpmnNodeParamsVo();
            startNode.setParams(startParams);
            bpmnConfVo.setNodes(Collections.singletonList(startNode));

            bpmnStartConditions.setStartUserId("specialUser");

            BpmnConfVo result = bpmnPersonnelFormat.formatPersonnelsConf(bpmnConfVo, bpmnStartConditions);

            assertEquals("specialUser", result.getNodes().get(0).getParams().getAssignee().getAssignee());
        }
    }
}
