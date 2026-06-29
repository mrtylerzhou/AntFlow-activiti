package org.openoa.common.adaptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.ApprovalStandardEnum;
import org.openoa.base.constant.enums.BpmnNodeParamTypeEnum;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.adaptor.bpmnelementadp.AbstractOrderedSignNodeAdp;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AbstractBpmnPersonnelAdaptorTest extends MockBaseTest {

    @Mock
    private BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService;

    @Mock
    private BpmnPersonnelProviderService bpmnPersonnelProviderService;

    private TestBpmnPersonnelAdaptor adaptor;

    private static class TestBpmnPersonnelAdaptor extends AbstractBpmnPersonnelAdaptor {
        public TestBpmnPersonnelAdaptor(BpmnEmployeeInfoProviderService empService, BpmnPersonnelProviderService personnelService) {
            super(empService, personnelService);
        }

        @Override
        public void setSupportBusinessObjects() {
        }
    }

    @BeforeEach
    void setUp() {
        try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
            mockedStatic.when(() -> SpringBeanUtils.getBean("udrPersonnelProvider")).thenReturn(bpmnPersonnelProviderService);
            adaptor = new TestBpmnPersonnelAdaptor(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);
        }
    }

    private BpmnNodeVo createNodeVo(BpmnNodeParamsVo paramsVo) {
        BpmnNodeVo nodeVo = new BpmnNodeVo();
        nodeVo.setNodeId("node1");
        nodeVo.setNodeName("TestNode");
        nodeVo.setParams(paramsVo);
        return nodeVo;
    }

    private BpmnNodeParamsVo createParamsVo() {
        return new BpmnNodeParamsVo();
    }

    private BpmnNodeParamsAssigneeVo createAssignee(String id, String name) {
        return BpmnNodeParamsAssigneeVo.builder()
                .assignee(id)
                .assigneeName(name)
                .elementName(name)
                .build();
    }

    @Nested
    @DisplayName("setNodeParams validation")
    class ValidationTest {

        @Test
        @DisplayName("should throw AFBizException when nodeVo is null")
        void shouldThrowWhenNodeVoNull() {
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            assertThrows(AFBizException.class,
                    () -> adaptor.setNodeParams(null, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "next", new HashMap<>(), new HashSet<>()));
        }

        @Test
        @DisplayName("should throw AFBizException when nodeParamTypeEnum is null")
        void shouldThrowWhenNodeParamTypeEnumNull() {
            BpmnNodeVo nodeVo = createNodeVo(createParamsVo());
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            assertThrows(AFBizException.class,
                    () -> adaptor.setNodeParams(nodeVo, startConditions, null, "next", new HashMap<>(), new HashSet<>()));
        }

        @Test
        @DisplayName("should throw AFBizException when paramsVo is null")
        void shouldThrowWhenParamsVoNull() {
            BpmnNodeVo nodeVo = new BpmnNodeVo();
            nodeVo.setNodeId("node1");
            nodeVo.setNodeName("TestNode");
            nodeVo.setParams(null);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            assertThrows(AFBizException.class,
                    () -> adaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "next", new HashMap<>(), new HashSet<>()));
        }
    }

    @Nested
    @DisplayName("setNodeParams single param type")
    class SingleParamTypeTest {

        @Test
        @DisplayName("should set assignee as first element when param type is SINGLE")
        void shouldSetAssigneeAsFirstElement() {
            BpmnNodeParamsVo paramsVo = createParamsVo();
            BpmnNodeVo nodeVo = createNodeVo(paramsVo);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            List<BpmnNodeParamsAssigneeVo> assignees = Arrays.asList(
                    createAssignee("user1", "Zhang San"),
                    createAssignee("user2", "Li Si")
            );

            when(bpmnPersonnelProviderService.getAssigneeList(eq(nodeVo), eq(startConditions))).thenReturn(assignees);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                adaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "next", new HashMap<>(), new HashSet<>());
            }

            assertNotNull(paramsVo.getAssignee());
            assertEquals("user1", paramsVo.getAssignee().getAssignee());
            assertEquals(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE.getCode(), paramsVo.getParamType());
        }
    }

    @Nested
    @DisplayName("setNodeParams multi param type")
    class MultiParamTypeTest {

        @Test
        @DisplayName("should set assigneeList when param type is MULTIPLAYER")
        void shouldSetAssigneeListForMulti() {
            BpmnNodeParamsVo paramsVo = createParamsVo();
            BpmnNodeVo nodeVo = createNodeVo(paramsVo);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            List<BpmnNodeParamsAssigneeVo> assignees = Arrays.asList(
                    createAssignee("user1", "Zhang San"),
                    createAssignee("user2", "Li Si")
            );

            when(bpmnPersonnelProviderService.getAssigneeList(eq(nodeVo), eq(startConditions))).thenReturn(assignees);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                adaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER, "next", new HashMap<>(), new HashSet<>());
            }

            assertNotNull(paramsVo.getAssigneeList());
            assertEquals(2, paramsVo.getAssigneeList().size());
            assertEquals("user1", paramsVo.getAssigneeList().get(0).getAssignee());
            assertEquals("user2", paramsVo.getAssigneeList().get(1).getAssignee());
            assertEquals(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER.getCode(), paramsVo.getParamType());
        }
    }

    @Nested
    @DisplayName("setNodeParams empty assignee list")
    class EmptyAssigneeListTest {

        @Test
        @DisplayName("should throw AFBizException when provider returns empty list")
        void shouldThrowWhenEmptyAssigneeList() {
            BpmnNodeParamsVo paramsVo = createParamsVo();
            BpmnNodeVo nodeVo = createNodeVo(paramsVo);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            when(bpmnPersonnelProviderService.getAssigneeList(eq(nodeVo), eq(startConditions))).thenReturn(Collections.emptyList());

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                assertThrows(AFBizException.class,
                        () -> adaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "next", new HashMap<>(), new HashSet<>()));
            }
        }

        @Test
        @DisplayName("should throw AFBizException when provider returns null list")
        void shouldThrowWhenNullAssigneeList() {
            BpmnNodeParamsVo paramsVo = createParamsVo();
            BpmnNodeVo nodeVo = createNodeVo(paramsVo);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            when(bpmnPersonnelProviderService.getAssigneeList(eq(nodeVo), eq(startConditions))).thenReturn(null);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                assertThrows(AFBizException.class,
                        () -> adaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "next", new HashMap<>(), new HashSet<>()));
            }
        }
    }

    @Nested
    @DisplayName("setNodeParams duplicate assignees")
    class DuplicateAssigneeTest {

        @Test
        @DisplayName("should deduplicate assignees by assignee id")
        void shouldDeduplicateAssignees() {
            BpmnNodeParamsVo paramsVo = createParamsVo();
            BpmnNodeVo nodeVo = createNodeVo(paramsVo);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            List<BpmnNodeParamsAssigneeVo> assignees = Arrays.asList(
                    createAssignee("user1", "Zhang San"),
                    createAssignee("user1", "Zhang San Duplicate"),
                    createAssignee("user2", "Li Si")
            );

            when(bpmnPersonnelProviderService.getAssigneeList(eq(nodeVo), eq(startConditions))).thenReturn(assignees);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                adaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER, "next", new HashMap<>(), new HashSet<>());
            }

            assertNotNull(paramsVo.getAssigneeList());
            assertEquals(2, paramsVo.getAssigneeList().size());
            assertEquals("user1", paramsVo.getAssigneeList().get(0).getAssignee());
            assertEquals("Zhang San", paramsVo.getAssigneeList().get(0).getAssigneeName());
            assertEquals("user2", paramsVo.getAssigneeList().get(1).getAssignee());
        }
    }

    @Nested
    @DisplayName("setNodeParams employee name lookup")
    class EmployeeNameLookupTest {

        @Test
        @DisplayName("should call bpmnEmployeeInfoProviderService when assigneeName is empty")
        void shouldCallEmployeeInfoProviderWhenNameEmpty() {
            BpmnNodeParamsVo paramsVo = createParamsVo();
            BpmnNodeVo nodeVo = createNodeVo(paramsVo);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnNodeParamsAssigneeVo assigneeNoName = BpmnNodeParamsAssigneeVo.builder()
                    .assignee("user1")
                    .assigneeName(null)
                    .elementName("TestNode")
                    .build();

            List<BpmnNodeParamsAssigneeVo> assignees = Collections.singletonList(assigneeNoName);

            when(bpmnPersonnelProviderService.getAssigneeList(eq(nodeVo), eq(startConditions))).thenReturn(assignees);

            Map<String, String> empNameMap = new HashMap<>();
            empNameMap.put("user1", "Zhang San");
            when(bpmnEmployeeInfoProviderService.provideEmployeeInfo(any())).thenReturn(empNameMap);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                adaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "next", new HashMap<>(), new HashSet<>());
            }

            verify(bpmnEmployeeInfoProviderService).provideEmployeeInfo(any());
            assertEquals("Zhang San", paramsVo.getAssignee().getAssigneeName());
        }

        @Test
        @DisplayName("should NOT call bpmnEmployeeInfoProviderService when all assigneeNames are set")
        void shouldNotCallEmployeeInfoProviderWhenNamesSet() {
            BpmnNodeParamsVo paramsVo = createParamsVo();
            BpmnNodeVo nodeVo = createNodeVo(paramsVo);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            List<BpmnNodeParamsAssigneeVo> assignees = Arrays.asList(
                    createAssignee("user1", "Zhang San"),
                    createAssignee("user2", "Li Si")
            );

            when(bpmnPersonnelProviderService.getAssigneeList(eq(nodeVo), eq(startConditions))).thenReturn(assignees);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                adaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER, "next", new HashMap<>(), new HashSet<>());
            }

            verify(bpmnEmployeeInfoProviderService, never()).provideEmployeeInfo(any());
        }
    }

    @Nested
    @DisplayName("setNodeParams FROM_PREV_NODE approval standard")
    class FromPrevNodeTest {

        @Test
        @DisplayName("should set contextEmplList from previous node when approval standard is FROM_PREV_NODE")
        void shouldSetContextEmplListFromPrevNode() {
            BpmnNodeParamsVo paramsVo = createParamsVo();
            BpmnNodeVo nodeVo = createNodeVo(paramsVo);
            nodeVo.setApprovalStandard(ApprovalStandardEnum.FROM_PREV_NODE.getCode());

            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            nodeVo.setProperty(property);

            List<BaseIdTranStruVo> prevEmplList = Arrays.asList(
                    BaseIdTranStruVo.builder().id("prevUser1").name("Prev User 1").build(),
                    BaseIdTranStruVo.builder().id("prevUser2").name("Prev User 2").build()
            );
            BpmnNodePropertysVo prevProperty = new BpmnNodePropertysVo();
            prevProperty.setEmplList(prevEmplList);

            BpmnNodeVo prevNode = new BpmnNodeVo();
            prevNode.setNodeId("node1");
            prevNode.setProperty(prevProperty);

            Map<String, BpmnNodeVo> mapPreNodes = new HashMap<>();
            mapPreNodes.put("node1", prevNode);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            List<BpmnNodeParamsAssigneeVo> assignees = Collections.singletonList(createAssignee("user1", "Zhang San"));
            when(bpmnPersonnelProviderService.getAssigneeList(eq(nodeVo), eq(startConditions))).thenReturn(assignees);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                adaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "next", mapPreNodes, new HashSet<>());
            }

            assertEquals(prevEmplList, property.getContextEmplList());
        }

        @Test
        @DisplayName("should not set contextEmplList when property is null")
        void shouldNotSetContextEmplListWhenPropertyNull() {
            BpmnNodeParamsVo paramsVo = createParamsVo();
            BpmnNodeVo nodeVo = createNodeVo(paramsVo);
            nodeVo.setApprovalStandard(ApprovalStandardEnum.FROM_PREV_NODE.getCode());
            nodeVo.setProperty(null);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            List<BpmnNodeParamsAssigneeVo> assignees = Collections.singletonList(createAssignee("user1", "Zhang San"));
            when(bpmnPersonnelProviderService.getAssigneeList(eq(nodeVo), eq(startConditions))).thenReturn(assignees);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                adaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "next", new HashMap<>(), new HashSet<>());
            }

            assertNull(nodeVo.getProperty());
        }

        @Test
        @DisplayName("should not set contextEmplList when previous node not found in map")
        void shouldNotSetContextEmplListWhenPrevNodeNotFound() {
            BpmnNodeParamsVo paramsVo = createParamsVo();
            BpmnNodeVo nodeVo = createNodeVo(paramsVo);
            nodeVo.setApprovalStandard(ApprovalStandardEnum.FROM_PREV_NODE.getCode());

            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            nodeVo.setProperty(property);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            List<BpmnNodeParamsAssigneeVo> assignees = Collections.singletonList(createAssignee("user1", "Zhang San"));
            when(bpmnPersonnelProviderService.getAssigneeList(eq(nodeVo), eq(startConditions))).thenReturn(assignees);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                adaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "next", new HashMap<>(), new HashSet<>());
            }

            assertNull(property.getContextEmplList());
        }
    }

    @Nested
    @DisplayName("setNodeParams ordered node type")
    class OrderedNodeTypeTest {

        @Test
        @DisplayName("should delegate to AbstractOrderedSignNodeAdp when orderedNodeType is set")
        void shouldDelegateToOrderedSignNodeAdp() {
            BpmnNodeParamsVo paramsVo = createParamsVo();
            BpmnNodeVo nodeVo = createNodeVo(paramsVo);
            nodeVo.setOrderedNodeType(OrderNodeTypeEnum.TEST_ORDERED_SIGN.getCode());

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, BpmnNodeVo> mapPreNodes = new HashMap<>();
            HashSet<BpmnNodeVo> setAddNodes = new HashSet<>();

            AbstractOrderedSignNodeAdp orderedSignNodeAdp = mock(AbstractOrderedSignNodeAdp.class);
            when(orderedSignNodeAdp.isSupportBusinessObject(OrderNodeTypeEnum.TEST_ORDERED_SIGN)).thenReturn(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(AbstractOrderedSignNodeAdp.class))
                        .thenReturn(Collections.singletonList(orderedSignNodeAdp));

                adaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "nextId", mapPreNodes, setAddNodes);

                verify(orderedSignNodeAdp).formatNodes(eq(nodeVo), eq(startConditions), eq("nextId"), eq(mapPreNodes), eq(setAddNodes));
            }
        }

        @Test
        @DisplayName("should throw NullPointerException when no AbstractOrderedSignNodeAdp supports the order type")
        void shouldThrowWhenNoAdpSupportsOrderType() {
            BpmnNodeParamsVo paramsVo = createParamsVo();
            BpmnNodeVo nodeVo = createNodeVo(paramsVo);
            nodeVo.setOrderedNodeType(OrderNodeTypeEnum.TEST_ORDERED_SIGN.getCode());

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            AbstractOrderedSignNodeAdp unsupportedAdp = mock(AbstractOrderedSignNodeAdp.class);
            when(unsupportedAdp.isSupportBusinessObject(OrderNodeTypeEnum.TEST_ORDERED_SIGN)).thenReturn(false);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBeans(AbstractOrderedSignNodeAdp.class))
                        .thenReturn(Collections.singletonList(unsupportedAdp));

                assertThrows(NullPointerException.class,
                        () -> adaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "next", new HashMap<>(), new HashSet<>()));
            }
        }
    }

    @Nested
    @DisplayName("constructor udrPersonnelProvider replacement")
    class ConstructorUdrReplacementTest {

        @Test
        @DisplayName("should use udrPersonnelProvider1 when bean equals bpmnPersonnelProviderService and udrPersonnelProvider1 exists")
        void shouldUseUdrPersonnelProvider1WhenAvailable() {
            BpmnPersonnelProviderService udrProvider1 = mock(BpmnPersonnelProviderService.class);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean("udrPersonnelProvider")).thenReturn(bpmnPersonnelProviderService);
                mockedStatic.when(() -> SpringBeanUtils.getBean("udrPersonnelProvider1")).thenReturn(udrProvider1);

                TestBpmnPersonnelAdaptor testAdaptor = new TestBpmnPersonnelAdaptor(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);

                BpmnNodeVo nodeVo = createNodeVo(createParamsVo());
                BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
                List<BpmnNodeParamsAssigneeVo> assignees = Collections.singletonList(createAssignee("user1", "Zhang San"));
                when(udrProvider1.getAssigneeList(eq(nodeVo), eq(startConditions))).thenReturn(assignees);

                testAdaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "next", new HashMap<>(), new HashSet<>());

                verify(udrProvider1).getAssigneeList(eq(nodeVo), eq(startConditions));
            }
        }

        @Test
        @DisplayName("should fall back to udrPersonnelProvider when udrPersonnelProvider1 throws exception")
        void shouldFallBackWhenUdrPersonnelProvider1Throws() {
            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean("udrPersonnelProvider")).thenReturn(bpmnPersonnelProviderService);
                mockedStatic.when(() -> SpringBeanUtils.getBean("udrPersonnelProvider1")).thenThrow(new RuntimeException("bean not found"));

                TestBpmnPersonnelAdaptor testAdaptor = new TestBpmnPersonnelAdaptor(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);

                BpmnNodeVo nodeVo = createNodeVo(createParamsVo());
                BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
                List<BpmnNodeParamsAssigneeVo> assignees = Collections.singletonList(createAssignee("user1", "Zhang San"));
                when(bpmnPersonnelProviderService.getAssigneeList(eq(nodeVo), eq(startConditions))).thenReturn(assignees);

                testAdaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "next", new HashMap<>(), new HashSet<>());

                verify(bpmnPersonnelProviderService).getAssigneeList(eq(nodeVo), eq(startConditions));
            }
        }

        @Test
        @DisplayName("should use original provider when bean is not the same as bpmnPersonnelProviderService")
        void shouldUseOriginalProviderWhenBeanDiffers() {
            BpmnPersonnelProviderService differentBean = mock(BpmnPersonnelProviderService.class);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean("udrPersonnelProvider")).thenReturn(differentBean);

                TestBpmnPersonnelAdaptor testAdaptor = new TestBpmnPersonnelAdaptor(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);

                BpmnNodeVo nodeVo = createNodeVo(createParamsVo());
                BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
                List<BpmnNodeParamsAssigneeVo> assignees = Collections.singletonList(createAssignee("user1", "Zhang San"));
                when(bpmnPersonnelProviderService.getAssigneeList(eq(nodeVo), eq(startConditions))).thenReturn(assignees);

                testAdaptor.setNodeParams(nodeVo, startConditions, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE, "next", new HashMap<>(), new HashSet<>());

                verify(bpmnPersonnelProviderService).getAssigneeList(eq(nodeVo), eq(startConditions));
            }
        }
    }
}
