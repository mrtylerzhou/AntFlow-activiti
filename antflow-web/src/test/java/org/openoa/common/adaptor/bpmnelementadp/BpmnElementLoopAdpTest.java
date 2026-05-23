package org.openoa.common.adaptor.bpmnelementadp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.DuplicationProcessStrategyEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.ProcessNodeEnum;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.base.vo.BpmnNodeButtonConfBaseVo;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.NodeLabelConstants;
import org.openoa.common.constant.enus.ElementPropertyEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BpmnElementLoopAdpTest extends MockBaseTest {

    private BpmnElementLoopAdp adaptor;

    @BeforeEach
    void setUp() {
        adaptor = new BpmnElementLoopAdp();
    }

    private BpmnNodeVo createNodeVo(List<BpmnNodeParamsAssigneeVo> assigneeList, Integer isMultiPeople) {
        BpmnNodeVo nodeVo = new BpmnNodeVo();
        nodeVo.setId(100L);
        nodeVo.setNodeId("loopNode1");
        nodeVo.setNodeName("Loop Node");
        nodeVo.setNodeType(1);

        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setAssigneeList(assigneeList);
        params.setParamType(2);
        nodeVo.setParams(params);

        BpmnNodePropertysVo property = new BpmnNodePropertysVo();
        property.setIsMultiPeople(isMultiPeople);
        nodeVo.setProperty(property);

        BpmnNodeButtonConfBaseVo buttons = BpmnNodeButtonConfBaseVo.builder()
                .startPage(Arrays.asList(1, 2))
                .approvalPage(Arrays.asList(3))
                .viewPage(Arrays.asList(4))
                .build();
        nodeVo.setButtons(buttons);

        nodeVo.setNodeTo(Arrays.asList("nextNode1"));

        return nodeVo;
    }

    private BpmnNodeParamsAssigneeVo createAssignee(String assignee, String assigneeName, Integer isDeduplication) {
        return BpmnNodeParamsAssigneeVo.builder()
                .assignee(assignee)
                .assigneeName(assigneeName)
                .elementName(assigneeName)
                .isDeduplication(isDeduplication)
                .build();
    }

    private BpmnStartConditionsVo createStartConditions(Integer duplicationProcessStrategy) {
        BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
        startConditions.setDuplicationProcessStrategy(duplicationProcessStrategy);
        return startConditions;
    }

    @Nested
    @DisplayName("single assignee path")
    class SingleAssigneePathTest {

        @Test
        @DisplayName("should create one single element with collection name loopUser{code}")
        void shouldCreateOneSingleElement() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, null);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            assertEquals(2, elements.size());
            BpmnConfCommonElementVo elementVo = elements.get(0);
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode(), elementVo.getElementProperty());
            assertEquals("loopUser2", elementVo.getAssigneeParamName());
            assertEquals("user1", elementVo.getAssigneeParamValue());
        }

        @Test
        @DisplayName("should create sequence flow after single element")
        void shouldCreateSequenceFlowAfterSingleElement() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, null);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            BpmnConfCommonElementVo sequenceFlow = elements.get(1);
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SEQUENCE_FLOW.getCode(), sequenceFlow.getElementProperty());
        }

        @Test
        @DisplayName("should create single element with correct assigneeParamValue")
        void shouldCreateSingleElementWithCorrectAssignee() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, null);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            assertEquals("user1", elements.get(0).getAssigneeParamValue());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode(), elements.get(0).getElementProperty());
        }
    }

    @Nested
    @DisplayName("multi-people sign path")
    class MultiPeopleSignPathTest {

        @Test
        @DisplayName("should create one multiplayer sign element with collection name loopUserSign{code}")
        void shouldCreateMultiplayerSignElement() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0),
                    createAssignee("user2", "Li Si", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, 1);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            assertEquals(2, elements.size());
            BpmnConfCommonElementVo elementVo = elements.get(0);
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_SIGN.getCode(), elementVo.getElementProperty());
            assertEquals("loopUserSign2", elementVo.getCollectionName());
            assertTrue(elementVo.getCollectionValue().contains("user1"));
            assertTrue(elementVo.getCollectionValue().contains("user2"));
        }

        @Test
        @DisplayName("should set nodeId on multiplayer sign element")
        void shouldSetNodeIdOnMultiplayerSignElement() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0),
                    createAssignee("user2", "Li Si", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, 1);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            assertEquals("100", elements.get(0).getNodeId());
        }

        @Test
        @DisplayName("should create assigneeMap with assignee->assigneeName entries")
        void shouldCreateAssigneeMap() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0),
                    createAssignee("user2", "Li Si", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, 1);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            BpmnConfCommonElementVo elementVo = elements.get(0);
            assertEquals("Zhang San", elementVo.getAssigneeMap().get("user1"));
            assertEquals("Li Si", elementVo.getAssigneeMap().get("user2"));
        }
    }

    @Nested
    @DisplayName("multiple assignees without isMultiPeople")
    class MultipleAssigneesWithoutMultiPeopleTest {

        @Test
        @DisplayName("should create multiple single elements, one per assignee")
        void shouldCreateMultipleSingleElements() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0),
                    createAssignee("user2", "Li Si", 0),
                    createAssignee("user3", "Wang Wu", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            assertEquals(6, elements.size());
            assertEquals("user1", elements.get(0).getAssigneeParamValue());
            assertEquals("user2", elements.get(2).getAssigneeParamValue());
            assertEquals("user3", elements.get(4).getAssigneeParamValue());
        }

        @Test
        @DisplayName("each single element should have loopUser{code} as assigneeParamName")
        void eachElementShouldHaveLoopUserParamName() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0),
                    createAssignee("user2", "Li Si", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            assertEquals("loopUser2", elements.get(0).getAssigneeParamName());
            assertEquals("loopUser3", elements.get(2).getAssigneeParamName());
        }
    }

    @Nested
    @DisplayName("SKIP strategy with deduplicated assignees")
    class SkipStrategyDeduplicationTest {

        @Test
        @DisplayName("should include deduplicated assignees and set skippedAssignees label")
        void shouldIncludeDeduplicatedAssigneesAndSetLabel() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 1),
                    createAssignee("user2", "Li Si", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, 1);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.SKIP.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            BpmnConfCommonElementVo elementVo = elements.get(0);
            assertTrue(elementVo.getCollectionValue().contains("user1"));
            assertTrue(elementVo.getCollectionValue().contains("user2"));

            List<BpmnNodeLabelVO> labels = nodeVo.getLabelList();
            assertNotNull(labels);
            boolean hasSkippedLabel = labels.stream()
                    .anyMatch(l -> NodeLabelConstants.skippedAssignees.getLabelValue().equals(l.getLabelValue()));
            assertTrue(hasSkippedLabel);
        }

        @Test
        @DisplayName("skippedAssignees label name should contain deduplicated assignee ids")
        void skippedLabelNameShouldContainDeduplicatedIds() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 1),
                    createAssignee("user2", "Li Si", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, 1);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.SKIP.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            List<BpmnNodeLabelVO> labels = nodeVo.getLabelList();
            BpmnNodeLabelVO skippedLabel = labels.stream()
                    .filter(l -> NodeLabelConstants.skippedAssignees.getLabelValue().equals(l.getLabelValue()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(skippedLabel);
            assertTrue(skippedLabel.getLabelName().contains("user1"));
        }

        @Test
        @DisplayName("should not set skippedAssignees label when no deduplicated assignees exist")
        void shouldNotSetLabelWhenNoDeduplicatedAssignees() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0),
                    createAssignee("user2", "Li Si", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, 1);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.SKIP.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            List<BpmnNodeLabelVO> labels = nodeVo.getLabelList();
            if (labels != null) {
                boolean hasSkippedLabel = labels.stream()
                        .anyMatch(l -> NodeLabelConstants.skippedAssignees.getLabelValue().equals(l.getLabelValue()));
                assertFalse(hasSkippedLabel);
            }
        }
    }

    @Nested
    @DisplayName("REMOVE strategy deduplication filtering")
    class RemoveStrategyDeduplicationTest {

        @Test
        @DisplayName("should filter out deduplicated assignees and fall to single element path")
        void shouldFilterOutDeduplicatedAssignees() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 1),
                    createAssignee("user2", "Li Si", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, 1);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            BpmnConfCommonElementVo elementVo = elements.get(0);
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode(), elementVo.getElementProperty());
            assertEquals("user2", elementVo.getAssigneeParamValue());
            assertNotEquals("user1", elementVo.getAssigneeParamValue());
        }

        @Test
        @DisplayName("should only keep non-deduplicated assignees in single element path")
        void shouldOnlyKeepNonDeduplicatedInSinglePath() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 1),
                    createAssignee("user2", "Li Si", 0),
                    createAssignee("user3", "Wang Wu", 1)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            assertEquals(1, elements.stream()
                    .filter(e -> ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode().equals(e.getElementProperty()))
                    .count());
            assertEquals("user2", elements.get(0).getAssigneeParamValue());
        }
    }

    @Nested
    @DisplayName("empty assignee list after dedup filtering")
    class EmptyAssigneeListAfterDedupTest {

        @Test
        @DisplayName("should not add any elements when all assignees are deduplicated under REMOVE strategy")
        void shouldNotAddElementsWhenAllDeduplicated() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 1),
                    createAssignee("user2", "Li Si", 1)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            assertTrue(elements.isEmpty());
        }
    }

    @Nested
    @DisplayName("numMap updates")
    class NumMapUpdateTest {

        @Test
        @DisplayName("should update nodeCode and sequenceFlowNum for single assignee")
        void shouldUpdateNumMapForSingleAssignee() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, null);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            assertEquals(2, numMap.get("nodeCode"));
            assertEquals(2, numMap.get("sequenceFlowNum"));
        }

        @Test
        @DisplayName("should update nodeCode and sequenceFlowNum for multiplayer sign path")
        void shouldUpdateNumMapForMultiplayerSign() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0),
                    createAssignee("user2", "Li Si", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, 1);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            assertEquals(2, numMap.get("nodeCode"));
            assertEquals(2, numMap.get("sequenceFlowNum"));
        }

        @Test
        @DisplayName("should increment nodeCode and sequenceFlowNum for each single element in loop")
        void shouldIncrementForMultipleSingleElements() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0),
                    createAssignee("user2", "Li Si", 0),
                    createAssignee("user3", "Wang Wu", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 5);
            numMap.put("sequenceFlowNum", 5);

            adaptor.doFormatNodesToElements(elements, nodeVo, 5, 5, numMap, startConditions);

            assertEquals(8, numMap.get("nodeCode"));
            assertEquals(8, numMap.get("sequenceFlowNum"));
        }
    }

    @Nested
    @DisplayName("element id and sequence flow connectivity")
    class ElementIdAndSequenceFlowTest {

        @Test
        @DisplayName("single element id should match ProcessNodeEnum desc for nodeCode+1")
        void singleElementIdShouldMatchProcessNodeEnum() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, null);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            String expectedElementId = ProcessNodeEnum.getDescByCode(2);
            assertEquals(expectedElementId, elements.get(0).getElementId());
        }

        @Test
        @DisplayName("sequence flow should connect from previous node to new element")
        void sequenceFlowShouldConnectCorrectly() {
            List<BpmnNodeParamsAssigneeVo> assigneeList = Arrays.asList(
                    createAssignee("user1", "Zhang San", 0)
            );
            BpmnNodeVo nodeVo = createNodeVo(assigneeList, null);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 1);
            numMap.put("sequenceFlowNum", 1);

            adaptor.doFormatNodesToElements(elements, nodeVo, 1, 1, numMap, startConditions);

            BpmnConfCommonElementVo sequenceFlow = elements.get(1);
            String expectedFlowFrom = ProcessNodeEnum.getDescByCode(1);
            assertEquals(expectedFlowFrom, sequenceFlow.getFlowFrom());
            assertEquals(elements.get(0).getElementId(), sequenceFlow.getFlowTo());
        }
    }

    @Nested
    @DisplayName("setSupportBusinessObjects")
    class SetSupportBusinessObjectsTest {

        @Test
        @DisplayName("should support NODE_PROPERTY_LOOP")
        void shouldSupportNodePropertyLoop() {
            adaptor.setSupportBusinessObjects();

            assertTrue(adaptor.isSupportBusinessObject(NodePropertyEnum.NODE_PROPERTY_LOOP));
        }
    }
}
