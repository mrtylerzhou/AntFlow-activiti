package org.openoa.common.adaptor.bpmnelementadp;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.AFSpecialAssigneeEnum;
import org.openoa.base.constant.enums.BpmnNodeParamTypeEnum;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.util.AssigneeVoBuildUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AbstractOrderedSignNodeAdpTest extends MockBaseTest {

    @Mock
    private AssigneeVoBuildUtils assigneeVoBuildUtils;

    private TestAdaptor adaptor;

    private static class TestAdaptor extends AbstractOrderedSignNodeAdp {
        private List<BaseIdTranStruVo> assigneeIdsResult;

        @Override
        public List<BaseIdTranStruVo> getAssigneeIds(BpmnNodeVo nodeVo, BpmnStartConditionsVo bpmnStartConditions) {
            return assigneeIdsResult;
        }

        @Override
        public void setSupportBusinessObjects() {
        }

        void setAssigneeIdsResult(List<BaseIdTranStruVo> result) {
            this.assigneeIdsResult = result;
        }
    }

    @BeforeEach
    void setUp() {
        adaptor = new TestAdaptor();
        java.lang.reflect.Field field;
        try {
            field = AbstractOrderedSignNodeAdp.class.getDeclaredField("assigneeVoBuildUtils");
            field.setAccessible(true);
            field.set(adaptor, assigneeVoBuildUtils);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BpmnNodeVo createNodeVo(String nodeId, String nodeName, List<BpmnNodeParamsAssigneeVo> assigneeList) {
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setAssigneeList(assigneeList);
        params.setParamType(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE.getCode());
        BpmnNodePropertysVo property = new BpmnNodePropertysVo();
        BpmnNodeVo nodeVo = new BpmnNodeVo();
        nodeVo.setNodeId(nodeId);
        nodeVo.setNodeName(nodeName);
        nodeVo.setParams(params);
        nodeVo.setProperty(property);
        return nodeVo;
    }

    private BpmnNodeParamsAssigneeVo createAssigneeVo(String assignee) {
        return BpmnNodeParamsAssigneeVo.builder()
                .assignee(assignee)
                .isDeduplication(0)
                .build();
    }

    private BpmnNodeParamsAssigneeVo createZeroVo() {
        return BpmnNodeParamsAssigneeVo.builder()
                .assignee(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId())
                .isDeduplication(0)
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<BpmnNodeVo> invokeGetOrderedSignNodes(BpmnNodeVo nodeVo, BpmnStartConditionsVo conditions, String nextNodeId) {
        try {
            Method method = AbstractOrderedSignNodeAdp.class.getDeclaredMethod("getOrderedSignNodes", BpmnNodeVo.class, BpmnStartConditionsVo.class, String.class);
            method.setAccessible(true);
            return (List<BpmnNodeVo>) method.invoke(adaptor, nodeVo, conditions, nextNodeId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<BpmnNodeParamsAssigneeVo> invokeGetAssignees(BpmnNodeVo nodeVo, BpmnStartConditionsVo conditions) {
        try {
            Method method = AbstractOrderedSignNodeAdp.class.getDeclaredMethod("getAssignees", BpmnNodeVo.class, BpmnStartConditionsVo.class);
            method.setAccessible(true);
            return (List<BpmnNodeParamsAssigneeVo>) method.invoke(adaptor, nodeVo, conditions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("formatNodes")
    class FormatNodesTest {

        @Test
        @DisplayName("should add ordered sign nodes to setAddNodes when multiple assignees")
        void shouldAddOrderedSignNodesToSetAddNodes() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(
                    new BaseIdTranStruVo("user1", "Zhang San"),
                    new BaseIdTranStruVo("user2", "Li Si"),
                    new BaseIdTranStruVo("user3", "Wang Wu")
            ));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVo2 = createAssigneeVo("user2");
            BpmnNodeParamsAssigneeVo builtVo3 = createAssigneeVo("user3");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVo2, builtVo3));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            BpmnNodeVo nextNode = createNodeVo("nextNode", "Next", null);
            Map<String, BpmnNodeVo> mapPreNodes = new HashMap<>();
            mapPreNodes.put("nextNode", nextNode);
            HashSet<BpmnNodeVo> setAddNodes = new HashSet<>();

            adaptor.formatNodes(nodeVo, conditions, "nextNode", mapPreNodes, setAddNodes);

            assertEquals(2, setAddNodes.size());
        }

        @Test
        @DisplayName("should set nextNode's nodeTo to last ordered sign node's nodeId")
        void shouldSetNextNodeNodeToToLastOrderedSignNode() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(
                    new BaseIdTranStruVo("user1", "Zhang San"),
                    new BaseIdTranStruVo("user2", "Li Si"),
                    new BaseIdTranStruVo("user3", "Wang Wu")
            ));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVo2 = createAssigneeVo("user2");
            BpmnNodeParamsAssigneeVo builtVo3 = createAssigneeVo("user3");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVo2, builtVo3));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            BpmnNodeVo nextNode = createNodeVo("nextNode", "Next", null);
            Map<String, BpmnNodeVo> mapPreNodes = new HashMap<>();
            mapPreNodes.put("nextNode", nextNode);
            HashSet<BpmnNodeVo> setAddNodes = new HashSet<>();

            adaptor.formatNodes(nodeVo, conditions, "nextNode", mapPreNodes, setAddNodes);

            assertEquals("signNodecopy2", nextNode.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should set nextNode's nodeTo to current nodeVo's nodeId when no ordered sign nodes but single non-TO_BE_REMOVED assignee")
        void shouldSetNextNodeNodeToToCurrentNodeVoNodeId() {
            BpmnNodeParamsAssigneeVo normalAssignee = createAssigneeVo("user1");
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(normalAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(null);

            BpmnNodeVo nextNode = createNodeVo("nextNode", "Next", null);
            Map<String, BpmnNodeVo> mapPreNodes = new HashMap<>();
            mapPreNodes.put("nextNode", nextNode);
            HashSet<BpmnNodeVo> setAddNodes = new HashSet<>();

            adaptor.formatNodes(nodeVo, conditions, "nextNode", mapPreNodes, setAddNodes);

            assertEquals("signNode", nextNode.getParams().getNodeTo());
        }

        @Test
        @DisplayName("should set zero vo assignee when no ordered sign nodes and single TO_BE_REMOVED assignee")
        void shouldSetZeroVoAssigneeWhenOnlyToBeRemoved() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(AFSpecialAssigneeEnum.buildToBeRemoved()));

            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            BpmnNodeVo nextNode = createNodeVo("nextNode", "Next", null);
            Map<String, BpmnNodeVo> mapPreNodes = new HashMap<>();
            mapPreNodes.put("nextNode", nextNode);
            HashSet<BpmnNodeVo> setAddNodes = new HashSet<>();

            adaptor.formatNodes(nodeVo, conditions, "nextNode", mapPreNodes, setAddNodes);

            assertEquals(1, nodeVo.getParams().getAssigneeList().size());
            assertEquals(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId(), nodeVo.getParams().getAssigneeList().get(0).getAssignee());
        }

        @Test
        @DisplayName("should set zero vo assignee when empty assigneeList")
        void shouldSetZeroVoAssigneeWhenEmptyAssigneeList() {
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", new ArrayList<>());
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(null);

            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            BpmnNodeVo nextNode = createNodeVo("nextNode", "Next", null);
            Map<String, BpmnNodeVo> mapPreNodes = new HashMap<>();
            mapPreNodes.put("nextNode", nextNode);
            HashSet<BpmnNodeVo> setAddNodes = new HashSet<>();

            adaptor.formatNodes(nodeVo, conditions, "nextNode", mapPreNodes, setAddNodes);

            assertEquals(1, nodeVo.getParams().getAssigneeList().size());
            assertEquals(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId(), nodeVo.getParams().getAssigneeList().get(0).getAssignee());
        }
    }

    @Nested
    @DisplayName("getOrderedSignNodes")
    class GetOrderedSignNodesTest {

        @Test
        @DisplayName("should set TO_BE_REMOVED assignee when assigneeList is null")
        void shouldSetToBeRemovedWhenAssigneeListIsNull() {
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", null);
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(null);

            invokeGetOrderedSignNodes(nodeVo, conditions, "nextNode");

            assertNotNull(nodeVo.getParams().getAssigneeList());
            assertEquals(1, nodeVo.getParams().getAssigneeList().size());
            assertEquals(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId(), nodeVo.getParams().getAssigneeList().get(0).getAssignee());
        }

        @Test
        @DisplayName("should return empty list when only TO_BE_REMOVED assignee")
        void shouldReturnEmptyListWhenOnlyToBeRemoved() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(AFSpecialAssigneeEnum.buildToBeRemoved()));

            BpmnNodeParamsAssigneeVo builtVo = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            List<BpmnNodeVo> result = invokeGetOrderedSignNodes(nodeVo, conditions, "nextNode");

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should return empty list when getAssignees returns empty")
        void shouldReturnEmptyListWhenGetAssigneesReturnsEmpty() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(null);

            List<BpmnNodeVo> result = invokeGetOrderedSignNodes(nodeVo, conditions, "nextNode");

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should set first assignee as nodeVo's assignee when original was TO_BE_REMOVED")
        void shouldSetFirstAssigneeWhenOriginalWasToBeRemoved() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(
                    new BaseIdTranStruVo("user1", "Zhang San"),
                    new BaseIdTranStruVo("user2", "Li Si")
            ));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVo2 = createAssigneeVo("user2");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVo2));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            invokeGetOrderedSignNodes(nodeVo, conditions, "nextNode");

            assertEquals(1, nodeVo.getParams().getAssigneeList().size());
            assertEquals("user1", nodeVo.getParams().getAssigneeList().get(0).getAssignee());
        }

        @Test
        @DisplayName("should change paramType to MULTIPLAYER when original was TO_BE_REMOVED")
        void shouldChangeParamTypeToMultiplayerWhenOriginalWasToBeRemoved() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(
                    new BaseIdTranStruVo("user1", "Zhang San"),
                    new BaseIdTranStruVo("user2", "Li Si")
            ));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVo2 = createAssigneeVo("user2");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVo2));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            invokeGetOrderedSignNodes(nodeVo, conditions, "nextNode");

            assertEquals(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER.getCode(), nodeVo.getParams().getParamType());
        }

        @Test
        @DisplayName("should create cloned nodes for assignees after the first")
        void shouldCreateClonedNodesForAssigneesAfterFirst() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(
                    new BaseIdTranStruVo("user1", "Zhang San"),
                    new BaseIdTranStruVo("user2", "Li Si"),
                    new BaseIdTranStruVo("user3", "Wang Wu")
            ));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVo2 = createAssigneeVo("user2");
            BpmnNodeParamsAssigneeVo builtVo3 = createAssigneeVo("user3");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVo2, builtVo3));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            List<BpmnNodeVo> result = invokeGetOrderedSignNodes(nodeVo, conditions, "nextNode");

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("should set nodeId with copy suffix on cloned nodes")
        void shouldSetNodeIdWithCopySuffixOnClonedNodes() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(
                    new BaseIdTranStruVo("user1", "Zhang San"),
                    new BaseIdTranStruVo("user2", "Li Si"),
                    new BaseIdTranStruVo("user3", "Wang Wu")
            ));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVo2 = createAssigneeVo("user2");
            BpmnNodeParamsAssigneeVo builtVo3 = createAssigneeVo("user3");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVo2, builtVo3));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            List<BpmnNodeVo> result = invokeGetOrderedSignNodes(nodeVo, conditions, "nextNode");

            assertEquals("signNodecopy1", result.get(0).getNodeId());
            assertEquals("signNodecopy2", result.get(1).getNodeId());
        }

        @Test
        @DisplayName("should set isMultiPeople=1 on cloned nodes")
        void shouldSetIsMultiPeopleOnClonedNodes() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(
                    new BaseIdTranStruVo("user1", "Zhang San"),
                    new BaseIdTranStruVo("user2", "Li Si")
            ));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVo2 = createAssigneeVo("user2");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVo2));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            List<BpmnNodeVo> result = invokeGetOrderedSignNodes(nodeVo, conditions, "nextNode");

            assertEquals(1, result.get(0).getProperty().getIsMultiPeople());
        }

        @Test
        @DisplayName("should set single assignee on each cloned node")
        void shouldSetSingleAssigneeOnEachClonedNode() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(
                    new BaseIdTranStruVo("user1", "Zhang San"),
                    new BaseIdTranStruVo("user2", "Li Si"),
                    new BaseIdTranStruVo("user3", "Wang Wu")
            ));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVo2 = createAssigneeVo("user2");
            BpmnNodeParamsAssigneeVo builtVo3 = createAssigneeVo("user3");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVo2, builtVo3));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            List<BpmnNodeVo> result = invokeGetOrderedSignNodes(nodeVo, conditions, "nextNode");

            assertEquals(1, result.get(0).getParams().getAssigneeList().size());
            assertEquals("user2", result.get(0).getParams().getAssigneeList().get(0).getAssignee());
            assertEquals(1, result.get(1).getParams().getAssigneeList().size());
            assertEquals("user3", result.get(1).getParams().getAssigneeList().get(0).getAssignee());
        }

        @Test
        @DisplayName("should set paramType to MULTIPLAYER on cloned nodes")
        void shouldSetParamTypeToMultiplayerOnClonedNodes() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(
                    new BaseIdTranStruVo("user1", "Zhang San"),
                    new BaseIdTranStruVo("user2", "Li Si")
            ));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVo2 = createAssigneeVo("user2");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVo2));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            List<BpmnNodeVo> result = invokeGetOrderedSignNodes(nodeVo, conditions, "nextNode");

            assertEquals(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER.getCode(), result.get(0).getParams().getParamType());
        }

        @Test
        @DisplayName("should chain nodeTo references: last copy points to nextNodeId, earlier copies point to later copies")
        void shouldChainNodeToReferences() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(
                    new BaseIdTranStruVo("user1", "Zhang San"),
                    new BaseIdTranStruVo("user2", "Li Si"),
                    new BaseIdTranStruVo("user3", "Wang Wu")
            ));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVo2 = createAssigneeVo("user2");
            BpmnNodeParamsAssigneeVo builtVo3 = createAssigneeVo("user3");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVo2, builtVo3));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            List<BpmnNodeVo> result = invokeGetOrderedSignNodes(nodeVo, conditions, "nextNodeId");

            assertEquals("nextNodeId", result.get(0).getParams().getNodeTo());
            assertEquals("signNodecopy1", result.get(1).getParams().getNodeTo());
        }

        @Test
        @DisplayName("should NOT create copy node for the first assignee")
        void shouldNotCreateCopyNodeForFirstAssignee() {
            BpmnNodeParamsAssigneeVo toBeRemovedAssignee = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", Lists.newArrayList(toBeRemovedAssignee));
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(
                    new BaseIdTranStruVo("user1", "Zhang San"),
                    new BaseIdTranStruVo("user2", "Li Si")
            ));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVo2 = createAssigneeVo("user2");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVo2));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            List<BpmnNodeVo> result = invokeGetOrderedSignNodes(nodeVo, conditions, "nextNode");

            assertEquals(1, result.size());
            assertEquals("user2", result.get(0).getParams().getAssigneeList().get(0).getAssignee());
            assertEquals("user1", nodeVo.getParams().getAssigneeList().get(0).getAssignee());
        }
    }

    @Nested
    @DisplayName("getAssignees")
    class GetAssigneesTest {

        @Test
        @DisplayName("should return empty list when getAssigneeIds returns empty")
        void shouldReturnEmptyListWhenGetAssigneeIdsReturnsEmpty() {
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", null);
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(new ArrayList<>());

            List<BpmnNodeParamsAssigneeVo> result = invokeGetAssignees(nodeVo, conditions);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should return empty list when getAssigneeIds returns null")
        void shouldReturnEmptyListWhenGetAssigneeIdsReturnsNull() {
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", null);
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(null);

            List<BpmnNodeParamsAssigneeVo> result = invokeGetAssignees(nodeVo, conditions);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should deduplicate assigneeInfos by id before building VOs")
        void shouldDeduplicateAssigneeInfosById() {
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", null);
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            BaseIdTranStruVo info1 = new BaseIdTranStruVo("user1", "Zhang San");
            BaseIdTranStruVo info2 = new BaseIdTranStruVo("user1", "Zhang San Duplicate");
            BaseIdTranStruVo info3 = new BaseIdTranStruVo("user2", "Li Si");
            adaptor.setAssigneeIdsResult(Lists.newArrayList(info1, info2, info3));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVo2 = createAssigneeVo("user2");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVo2));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            invokeGetAssignees(nodeVo, conditions);

            verify(assigneeVoBuildUtils).buildVOs(anyList(), eq("Sign"), eq(true));
        }

        @Test
        @DisplayName("should filter out TO_BE_REMOVED from built VOs but keep zero vo at end")
        void shouldFilterOutToBeRemovedFromBuiltVOs() {
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", null);
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(
                    new BaseIdTranStruVo("user1", "Zhang San")
            ));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVoToBeRemoved = createAssigneeVo(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVoToBeRemoved));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            List<BpmnNodeParamsAssigneeVo> result = invokeGetAssignees(nodeVo, conditions);

            assertEquals(2, result.size());
            assertEquals("user1", result.get(0).getAssignee());
            assertEquals(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId(), result.get(1).getAssignee());
        }

        @Test
        @DisplayName("should add zero vo at the end")
        void shouldAddZeroVoAtTheEnd() {
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", null);
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            adaptor.setAssigneeIdsResult(Lists.newArrayList(
                    new BaseIdTranStruVo("user1", "Zhang San")
            ));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            List<BpmnNodeParamsAssigneeVo> result = invokeGetAssignees(nodeVo, conditions);

            assertEquals(2, result.size());
            assertEquals(AFSpecialAssigneeEnum.TO_BE_REMOVED.getId(), result.get(result.size() - 1).getAssignee());
        }

        @Test
        @DisplayName("should call assigneeVoBuildUtils.buildVOs with deduplicated infos")
        void shouldCallBuildVOsWithDeduplicatedInfos() {
            BpmnNodeVo nodeVo = createNodeVo("signNode", "Sign", null);
            BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();

            BaseIdTranStruVo info1 = new BaseIdTranStruVo("user1", "Zhang San");
            BaseIdTranStruVo info2 = new BaseIdTranStruVo("user2", "Li Si");
            adaptor.setAssigneeIdsResult(Lists.newArrayList(info1, info2));

            BpmnNodeParamsAssigneeVo builtVo1 = createAssigneeVo("user1");
            BpmnNodeParamsAssigneeVo builtVo2 = createAssigneeVo("user2");
            BpmnNodeParamsAssigneeVo zeroVo = createZeroVo();

            when(assigneeVoBuildUtils.buildVOs(anyList(), eq("Sign"), eq(true)))
                    .thenReturn(Lists.newArrayList(builtVo1, builtVo2));
            when(assigneeVoBuildUtils.buildZeroVo()).thenReturn(zeroVo);

            invokeGetAssignees(nodeVo, conditions);

            verify(assigneeVoBuildUtils).buildVOs(anyList(), eq("Sign"), eq(true));
        }
    }
}
