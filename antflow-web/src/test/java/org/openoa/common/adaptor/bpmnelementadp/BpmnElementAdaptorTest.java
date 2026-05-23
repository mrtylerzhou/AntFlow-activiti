package org.openoa.common.adaptor.bpmnelementadp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.*;
import org.openoa.base.util.NodeUtil;
import org.openoa.base.vo.*;
import org.openoa.common.constant.enus.ElementPropertyEnum;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BpmnElementAdaptorTest extends MockBaseTest {

    private TestableBpmnElementAdaptor adaptor;

    static class TestableBpmnElementAdaptor extends BpmnElementAdaptor {
        @Override
        protected BpmnConfCommonElementVo getElementVo(BpmnNodePropertysVo property, BpmnNodeParamsVo params,
                                                       Integer elementCode, String elementId, BpmnStartConditionsVo bpmnStartConditions) {
            return BpmnConfCommonElementVo.builder()
                    .elementId(elementId)
                    .elementProperty(ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode())
                    .elementType(ElementTypeEnum.ELEMENT_TYPE_USER_TASK.getCode())
                    .assigneeMap(new HashMap<>())
                    .build();
        }

        @Override
        public void setSupportBusinessObjects() {
        }
    }

    @BeforeEach
    void setUp() {
        adaptor = new TestableBpmnElementAdaptor();
    }

    private BpmnNodeVo createBasicNodeVo() {
        BpmnNodeButtonConfBaseVo buttons = new BpmnNodeButtonConfBaseVo();
        buttons.setStartPage(new ArrayList<>());
        buttons.setApprovalPage(new ArrayList<>());
        buttons.setViewPage(new ArrayList<>());

        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE.getCode());
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee("user1");
        assignee.setAssigneeName("Zhang San");
        params.setAssignee(assignee);
        params.setIsNodeDeduplication(0);

        BpmnNodePropertysVo property = new BpmnNodePropertysVo();

        return BpmnNodeVo.builder()
                .id(100L)
                .nodeId("node1")
                .nodeName("Approval")
                .params(params)
                .property(property)
                .buttons(buttons)
                .build();
    }

    private BpmnStartConditionsVo createStartConditions(Integer strategy) {
        BpmnStartConditionsVo vo = new BpmnStartConditionsVo();
        vo.setDuplicationProcessStrategy(strategy);
        return vo;
    }

    private void runWithNodeUtilMock(Runnable testLogic) {
        try (MockedStatic<NodeUtil> nodeUtilMock = mockStatic(NodeUtil.class)) {
            testLogic.run();
        }
    }

    @Nested
    @DisplayName("doFormatNodesToElements basic")
    class DoFormatNodesToElementsTest {

        @Test
        @DisplayName("should set elementId on nodeVo from ProcessNodeEnum")
        void shouldSetElementIdOnNodeVo() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                assertNotNull(nodeVo.getElementId());
                assertEquals(ProcessNodeEnum.getDescByCode(1), nodeVo.getElementId());
            });
        }

        @Test
        @DisplayName("should call getElementVo and add result to list")
        void shouldCallGetElementVoAndAddToList() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                assertFalse(elements.isEmpty());
                BpmnConfCommonElementVo firstElement = elements.get(0);
                assertEquals(ProcessNodeEnum.getDescByCode(1), firstElement.getElementId());
            });
        }

        @Test
        @DisplayName("should set nodeId on elementVo from nodeVo.id")
        void shouldSetNodeIdOnElementVo() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                BpmnConfCommonElementVo elementVo = elements.get(0);
                assertEquals("100", elementVo.getNodeId());
            });
        }

        @Test
        @DisplayName("should set buttons on elementVo")
        void shouldSetButtonsOnElementVo() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.getButtons().setApprovalPage(Arrays.asList(3, 4));
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                BpmnConfCommonElementVo elementVo = elements.get(0);
                assertNotNull(elementVo.getButtons());
                assertEquals(2, elementVo.getButtons().getApprovalPage().size());
                assertEquals(3, elementVo.getButtons().getApprovalPage().get(0).getButtonType());
                assertEquals("同意", elementVo.getButtons().getApprovalPage().get(0).getButtonName());
            });
        }

        @Test
        @DisplayName("should set templateVos and approveRemindVo")
        void shouldSetTemplateVosAndApproveRemindVo() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            List<BpmnTemplateVo> templateVos = new ArrayList<>();
            BpmnApproveRemindVo approveRemindVo = new BpmnApproveRemindVo();
            nodeVo.setTemplateVos(templateVos);
            nodeVo.setApproveRemindVo(approveRemindVo);
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                BpmnConfCommonElementVo elementVo = elements.get(0);
                assertEquals(templateVos, elementVo.getTemplateVos());
                assertEquals(approveRemindVo, elementVo.getApproveRemindVo());
            });
        }

        @Test
        @DisplayName("should set signUpProperty")
        void shouldSetSignUpProperty() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.setIsSignUp(1);
            nodeVo.getProperty().setAfterSignUpWay(1);
            nodeVo.getProperty().setSignUpType(2);
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                BpmnConfCommonElementVo elementVo = elements.get(0);
                assertEquals(1, elementVo.getIsSignUp());
                assertEquals(1, elementVo.getAfterSignUpWay());
                assertEquals(2, elementVo.getSignUpType());
            });
        }

        @Test
        @DisplayName("should merge labelList from nodeVo to elementVo")
        void shouldMergeLabelList() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            BpmnNodeLabelVO nodeLabel = new BpmnNodeLabelVO("val1", "name1");
            nodeVo.setLabelList(new ArrayList<>(Collections.singletonList(nodeLabel)));
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                BpmnConfCommonElementVo elementVo = elements.get(0);
                assertNotNull(elementVo.getLabelList());
                assertTrue(elementVo.getLabelList().stream().anyMatch(l -> "val1".equals(l.getLabelValue())));
            });
        }

        @Test
        @DisplayName("should add skippedAssignees label for SKIP strategy with single assignee")
        void shouldAddSkippedLabelForSkipSingleAssignee() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.getParams().setIsNodeDeduplication(1);
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.SKIP.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                List<BpmnNodeLabelVO> labels = nodeVo.getLabelList();
                assertNotNull(labels);
                assertTrue(labels.stream().anyMatch(l -> NodeLabelConstants.skippedAssignees.getLabelValue().equals(l.getLabelValue())));
            });
        }

        @Test
        @DisplayName("should NOT add dedup labels when strategy is REMOVE")
        void shouldNotAddDedupLabelsWhenRemoveStrategy() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.getParams().setIsNodeDeduplication(1);
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                if (nodeVo.getLabelList() != null) {
                    assertFalse(nodeVo.getLabelList().stream()
                            .anyMatch(l -> NodeLabelConstants.skippedAssignees.getLabelValue().equals(l.getLabelValue())));
                }
            });
        }

        @Test
        @DisplayName("should call NodeUtil.elementWithSpecialMarks")
        void shouldCallNodeUtilElementWithSpecialMarks() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            try (MockedStatic<NodeUtil> nodeUtilMock = mockStatic(NodeUtil.class)) {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                nodeUtilMock.verify(() -> NodeUtil.elementWithSpecialMarks(any(BpmnConfCommonElementVo.class)));
            }
        }
    }

    @Nested
    @DisplayName("formatNodesToElements - fromNodes handling")
    class FormatNodesToElementsTest {

        @Test
        @DisplayName("should add parallel gateway element when nodeVo has fromNodes")
        void shouldAddParallelGatewayWhenFromNodes() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            BpmnNodeVo fromNode1 = BpmnNodeVo.builder().elementId("from1").build();
            BpmnNodeVo fromNode2 = BpmnNodeVo.builder().elementId("from2").build();
            nodeVo.setFromNodes(Arrays.asList(fromNode1, fromNode2));
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                assertTrue(elements.stream().anyMatch(e -> ElementTypeEnum.ELEMENT_TYPE_PARALLEL_GATEWAY.getCode().equals(e.getElementType())));
            });
        }

        @Test
        @DisplayName("should add sequence flow when no fromNodes and no flowTo match")
        void shouldAddSequenceFlowWhenNoFlowToMatch() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                long sequenceFlows = elements.stream()
                        .filter(e -> ElementTypeEnum.ELEMENT_TYPE_SEQUENCE_FLOW.getCode().equals(e.getElementType()))
                        .count();
                assertEquals(1, sequenceFlows);
            });
        }

        @Test
        @DisplayName("should update numMap with nodeCode and sequenceFlowNum")
        void shouldUpdateNumMap() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 5);
            numMap.put("sequenceFlowNum", 10);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 5, 10, numMap, startConditions);
                assertEquals(6, numMap.get("nodeCode"));
                assertEquals(11, numMap.get("sequenceFlowNum"));
            });
        }
    }

    @Nested
    @DisplayName("doSignUp")
    class DoSignUpTest {

        @Test
        @DisplayName("should NOT add sign-up elements when isSignUp != 1")
        void shouldNotAddSignUpElementsWhenNotSignUp() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                long signUpElements = elements.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> e.getIsSignUpSubElement() == 1)
                        .count();
                assertEquals(0, signUpElements);
            });
        }

        @Test
        @DisplayName("should add backApproval element when isSignUp==1 and afterSignUpWay==1")
        void shouldCallBackApprovalWhenSignUpAndBack() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.setIsSignUp(1);
            nodeVo.getProperty().setAfterSignUpWay(1);
            nodeVo.getProperty().setSignUpType(1);
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                long backSignUpElements = elements.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> e.getIsBackSignUp() == 1)
                        .count();
                assertEquals(1, backSignUpElements);
            });
        }

        @Test
        @DisplayName("should add signUpSubElement when isSignUp==1 and afterSignUpWay!=1")
        void shouldCallUnbackApprovalWhenSignUpAndNotBack() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.setIsSignUp(1);
            nodeVo.getProperty().setAfterSignUpWay(2);
            nodeVo.getProperty().setSignUpType(1);
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                long backSignUpElements = elements.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> e.getIsBackSignUp() == 1)
                        .count();
                assertEquals(0, backSignUpElements);
                long signUpSubElements = elements.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> e.getIsSignUpSubElement() == 1)
                        .count();
                assertEquals(1, signUpSubElements);
            });
        }
    }

    @Nested
    @DisplayName("setAndGetSignUpSubElement")
    class SetAndGetSignUpSubElementTest {

        @Test
        @DisplayName("should create sign-up element with isSignUpSubElement=1")
        void shouldCreateSignUpSubElement() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.setIsSignUp(1);
            nodeVo.getProperty().setAfterSignUpWay(2);
            nodeVo.getProperty().setSignUpType(1);
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                BpmnConfCommonElementVo signUpElement = elements.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> e.getIsSignUpSubElement() == 1)
                        .findFirst()
                        .orElse(null);
                assertNotNull(signUpElement);
            });
        }

        @Test
        @DisplayName("should set signUpElementId on sign-up element")
        void shouldSetSignUpElementIdOnSignUpElement() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.setIsSignUp(1);
            nodeVo.getProperty().setAfterSignUpWay(2);
            nodeVo.getProperty().setSignUpType(1);
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                BpmnConfCommonElementVo fatherElement = elements.get(0);
                BpmnConfCommonElementVo signUpElement = elements.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> e.getIsSignUpSubElement() == 1)
                        .findFirst()
                        .orElse(null);
                assertNotNull(signUpElement);
                assertEquals(fatherElement.getElementId(), signUpElement.getSignUpElementId());
            });
        }

        @Test
        @DisplayName("should set sign-up element buttons (agree/disagree only)")
        void shouldSetSignUpElementButtons() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.setIsSignUp(1);
            nodeVo.getProperty().setAfterSignUpWay(2);
            nodeVo.getProperty().setSignUpType(1);
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                BpmnConfCommonElementVo signUpElement = elements.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> e.getIsSignUpSubElement() == 1)
                        .findFirst()
                        .orElse(null);
                assertNotNull(signUpElement);
                assertNotNull(signUpElement.getButtons());
                assertNotNull(signUpElement.getButtons().getApprovalPage());
                assertEquals(2, signUpElement.getButtons().getApprovalPage().size());
                assertEquals(ButtonTypeEnum.BUTTON_TYPE_AGREE.getCode(), signUpElement.getButtons().getApprovalPage().get(0).getButtonType());
                assertEquals(ButtonTypeEnum.BUTTON_TYPE_DISAGREE.getCode(), signUpElement.getButtons().getApprovalPage().get(1).getButtonType());
            });
        }

        @Test
        @DisplayName("should add sign-up sequence flow with isSignUpSequenceFlow=1")
        void shouldAddSignUpSequenceFlow() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.setIsSignUp(1);
            nodeVo.getProperty().setAfterSignUpWay(2);
            nodeVo.getProperty().setSignUpType(1);
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                assertTrue(elements.stream()
                        .filter(Objects::nonNull)
                        .anyMatch(e -> e.getIsSignUpSequenceFlow() == 1));
            });
        }
    }

    @Nested
    @DisplayName("backApproval")
    class BackApprovalTest {

        @Test
        @DisplayName("should add backApproval element with isBackSignUp=1")
        void shouldAddBackApprovalElementWithIsBackSignUp() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.setIsSignUp(1);
            nodeVo.getProperty().setAfterSignUpWay(1);
            nodeVo.getProperty().setSignUpType(1);
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                BpmnConfCommonElementVo backApprovalElement = elements.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> e.getIsBackSignUp() == 1)
                        .findFirst()
                        .orElse(null);
                assertNotNull(backApprovalElement);
                assertEquals(1, backApprovalElement.getIsBackSignUp());
            });
        }

        @Test
        @DisplayName("should set backApproval element name same as father element")
        void shouldSetBackApprovalElementNameSameAsFather() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.setIsSignUp(1);
            nodeVo.getProperty().setAfterSignUpWay(1);
            nodeVo.getProperty().setSignUpType(1);
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                BpmnConfCommonElementVo fatherElement = elements.get(0);
                BpmnConfCommonElementVo backApprovalElement = elements.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> e.getIsBackSignUp() == 1)
                        .findFirst()
                        .orElse(null);
                assertNotNull(backApprovalElement);
                assertEquals(fatherElement.getElementName(), backApprovalElement.getElementName());
            });
        }

        @Test
        @DisplayName("should set signUpElementId on backApproval element")
        void shouldSetSignUpElementIdOnBackApproval() {
            List<BpmnConfCommonElementVo> elements = new ArrayList<>();
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.setIsSignUp(1);
            nodeVo.getProperty().setAfterSignUpWay(1);
            nodeVo.getProperty().setSignUpType(1);
            HashMap<String, Integer> numMap = new HashMap<>();
            numMap.put("nodeCode", 0);
            numMap.put("sequenceFlowNum", 0);
            BpmnStartConditionsVo startConditions = createStartConditions(DuplicationProcessStrategyEnum.REMOVE.getCode());

            runWithNodeUtilMock(() -> {
                adaptor.doFormatNodesToElements(elements, nodeVo, 0, 0, numMap, startConditions);
                BpmnConfCommonElementVo fatherElement = elements.get(0);
                BpmnConfCommonElementVo backApprovalElement = elements.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> e.getIsBackSignUp() == 1)
                        .findFirst()
                        .orElse(null);
                assertNotNull(backApprovalElement);
                assertEquals(fatherElement.getElementId(), backApprovalElement.getSignUpElementId());
            });
        }
    }

    @Nested
    @DisplayName("setElementButtons")
    class SetElementButtonsTest {

        @Test
        @DisplayName("should map startPage button codes to BpmnConfCommonButtonPropertyVo")
        void shouldMapStartPageButtonCodes() {
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.getButtons().setStartPage(Arrays.asList(1, 0));
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder().build();

            adaptor.setElementButtons(nodeVo, elementVo);

            assertNotNull(elementVo.getButtons());
            assertNotNull(elementVo.getButtons().getStartPage());
            assertEquals(2, elementVo.getButtons().getStartPage().size());
            assertEquals(1, elementVo.getButtons().getStartPage().get(0).getButtonType());
            assertEquals("提交", elementVo.getButtons().getStartPage().get(0).getButtonName());
        }

        @Test
        @DisplayName("should map approvalPage button codes to BpmnConfCommonButtonPropertyVo")
        void shouldMapApprovalPageButtonCodes() {
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.getButtons().setApprovalPage(Arrays.asList(3, 4));
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder().build();

            adaptor.setElementButtons(nodeVo, elementVo);

            assertNotNull(elementVo.getButtons());
            assertNotNull(elementVo.getButtons().getApprovalPage());
            assertEquals(2, elementVo.getButtons().getApprovalPage().size());
            assertEquals(3, elementVo.getButtons().getApprovalPage().get(0).getButtonType());
            assertEquals("同意", elementVo.getButtons().getApprovalPage().get(0).getButtonName());
            assertEquals(4, elementVo.getButtons().getApprovalPage().get(1).getButtonType());
            assertEquals("不同意", elementVo.getButtons().getApprovalPage().get(1).getButtonName());
        }

        @Test
        @DisplayName("should map viewPage button codes to BpmnConfCommonButtonPropertyVo")
        void shouldMapViewPageButtonCodes() {
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.getButtons().setViewPage(Arrays.asList(8));
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder().build();

            adaptor.setElementButtons(nodeVo, elementVo);

            assertNotNull(elementVo.getButtons());
            assertNotNull(elementVo.getButtons().getViewPage());
            assertEquals(1, elementVo.getButtons().getViewPage().size());
            assertEquals(8, elementVo.getButtons().getViewPage().get(0).getButtonType());
            assertEquals("打印", elementVo.getButtons().getViewPage().get(0).getButtonName());
        }

        @Test
        @DisplayName("should use ButtonTypeEnum.getDescByCode for button names")
        void shouldUseButtonTypeEnumForNames() {
            BpmnNodeVo nodeVo = createBasicNodeVo();
            nodeVo.getButtons().setApprovalPage(Arrays.asList(3));
            BpmnConfCommonElementVo elementVo = BpmnConfCommonElementVo.builder().build();

            adaptor.setElementButtons(nodeVo, elementVo);

            String expectedName = ButtonTypeEnum.getDescByCode(3);
            assertEquals(expectedName, elementVo.getButtons().getApprovalPage().get(0).getButtonName());
        }
    }
}
