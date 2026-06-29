package org.openoa.common.adaptor.bpmnelementadp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.BpmnNodeParamTypeEnum;
import org.openoa.base.constant.enums.DuplicationProcessStrategyEnum;
import org.openoa.base.constant.enums.SignTypeEnum;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.constant.enus.ElementPropertyEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbstractCommonBpmnElementAdaptorTest extends BaseTest {

    private TestableCommonBpmnElementAdaptor adaptor;

    static class TestableCommonBpmnElementAdaptor extends AbstractCommonBpmnElementAdaptor {
        private final String varName;

        TestableCommonBpmnElementAdaptor(String varName) {
            this.varName = varName;
        }

        @Override
        protected String provideVarName() {
            return varName;
        }

        @Override
        public void setSupportBusinessObjects() {
        }
    }

    @BeforeEach
    void setUp() {
        adaptor = new TestableCommonBpmnElementAdaptor("testVar");
    }

    @Nested
    @DisplayName("single assignee element")
    class SingleAssigneeTest {

        @Test
        @DisplayName("should create single element with correct assignee")
        void shouldCreateSingleElementWithAssignee() {
            BpmnNodeParamsVo params = createSingleParams("user1", "Zhang San");
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.commonElementVo(null, params, 1, "task1", startConditions);

            assertEquals("task1", element.getElementId());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode(), element.getElementProperty());
            assertEquals("testVar1", element.getAssigneeParamName());
            assertEquals("user1", element.getAssigneeParamValue());
        }

        @Test
        @DisplayName("should handle null assignee gracefully")
        void shouldHandleNullAssignee() {
            BpmnNodeParamsVo params = new BpmnNodeParamsVo();
            params.setParamType(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE.getCode());
            params.setAssignee(null);

            BpmnConfCommonElementVo element = adaptor.commonElementVo(null, params, 1, "task1", new BpmnStartConditionsVo());

            assertNotNull(element);
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode(), element.getElementProperty());
        }

        @Test
        @DisplayName("should use assignee element name as element name")
        void shouldUseAssigneeElementName() {
            BpmnNodeParamsVo params = createSingleParams("user1", "Zhang San");
            BpmnNodeParamsAssigneeVo assignee = params.getAssignee();
            assignee.setElementName("Approval Task");

            BpmnConfCommonElementVo element = adaptor.commonElementVo(null, params, 1, "task1", new BpmnStartConditionsVo());

            assertEquals("Approval Task", element.getElementName());
        }
    }

    @Nested
    @DisplayName("multi assignee sign element")
    class MultiAssigneeSignTest {

        @Test
        @DisplayName("should create sign element for multiplayer sign type")
        void shouldCreateSignElement() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(0, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_SIGN.getCode());

            BpmnConfCommonElementVo element = adaptor.commonElementVo(property, params, 2, "signTask1", new BpmnStartConditionsVo());

            assertEquals("signTask1", element.getElementId());
            assertEquals(SignTypeEnum.SIGN_TYPE_SIGN.getCode(), element.getSignType());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_SIGN.getCode(), element.getElementProperty());
            assertEquals(2, element.getCollectionValue().size());
        }

        @Test
        @DisplayName("should create or-sign element for or-sign type")
        void shouldCreateOrSignElement() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(0, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode());

            BpmnConfCommonElementVo element = adaptor.commonElementVo(property, params, 2, "orTask1", new BpmnStartConditionsVo());

            assertEquals(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode(), element.getSignType());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_ORSIGN.getCode(), element.getElementProperty());
        }

        @Test
        @DisplayName("should create sign-in-order element for sign-in-order type")
        void shouldCreateSignInOrderElement() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(0, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_SIGN_IN_ORDER.getCode());

            BpmnConfCommonElementVo element = adaptor.commonElementVo(property, params, 2, "orderTask1", new BpmnStartConditionsVo());

            assertEquals(SignTypeEnum.SIGN_TYPE_SIGN_IN_ORDER.getCode(), element.getSignType());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_SIGN_IN_ORDER.getCode(), element.getElementProperty());
        }

        @Test
        @DisplayName("should default to sign type when property is null")
        void shouldDefaultToSignTypeWhenPropertyNull() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(0, 0)
            );

            BpmnConfCommonElementVo element = adaptor.commonElementVo(null, params, 2, "task1", new BpmnStartConditionsVo());

            assertEquals(SignTypeEnum.SIGN_TYPE_SIGN.getCode(), element.getSignType());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_SIGN.getCode(), element.getElementProperty());
        }
    }

    @Nested
    @DisplayName("deduplication logic")
    class DeduplicationLogicTest {

        @Test
        @DisplayName("should include non-deduplicated assignees (isDeduplication=0)")
        void shouldIncludeNonDeduplicatedAssignees() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(0, 0)
            );

            BpmnConfCommonElementVo element = adaptor.commonElementVo(null, params, 2, "task1", new BpmnStartConditionsVo());

            assertEquals(2, element.getCollectionValue().size());
            assertTrue(element.getCollectionValue().contains("user1"));
            assertTrue(element.getCollectionValue().contains("user2"));
        }

        @Test
        @DisplayName("should exclude deduplicated assignees by default (REMOVE strategy)")
        void shouldExcludeDeduplicatedAssigneesByDefault() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2", "user3"),
                    Arrays.asList("Zhang San", "Li Si", "Wang Wu"),
                    Arrays.asList(0, 1, 0)
            );

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.commonElementVo(null, params, 2, "task1", startConditions);

            assertEquals(2, element.getCollectionValue().size());
            assertTrue(element.getCollectionValue().contains("user1"));
            assertFalse(element.getCollectionValue().contains("user2"));
            assertTrue(element.getCollectionValue().contains("user3"));
        }

        @Test
        @DisplayName("should include deduplicated assignees when SKIP strategy")
        void shouldIncludeDeduplicatedAssigneesWhenSkipStrategy() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2", "user3"),
                    Arrays.asList("Zhang San", "Li Si", "Wang Wu"),
                    Arrays.asList(0, 1, 0)
            );

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setDuplicationProcessStrategy(DuplicationProcessStrategyEnum.SKIP.getCode());

            BpmnConfCommonElementVo element = adaptor.commonElementVo(null, params, 2, "task1", startConditions);

            assertEquals(3, element.getCollectionValue().size());
            assertTrue(element.getCollectionValue().contains("user1"));
            assertTrue(element.getCollectionValue().contains("user2"));
            assertTrue(element.getCollectionValue().contains("user3"));
        }

        @Test
        @DisplayName("should exclude all deduplicated assignees when REMOVE strategy")
        void shouldExcludeAllDeduplicatedWhenRemoveStrategy() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(1, 1)
            );

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setDuplicationProcessStrategy(DuplicationProcessStrategyEnum.REMOVE.getCode());

            BpmnConfCommonElementVo element = adaptor.commonElementVo(null, params, 2, "task1", startConditions);

            assertTrue(element.getCollectionValue().isEmpty());
        }

        @Test
        @DisplayName("should maintain assignee order in LinkedHashMap")
        void shouldMaintainAssigneeOrder() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user3", "user1", "user2"),
                    Arrays.asList("Wang Wu", "Zhang San", "Li Si"),
                    Arrays.asList(0, 0, 0)
            );

            BpmnConfCommonElementVo element = adaptor.commonElementVo(null, params, 2, "task1", new BpmnStartConditionsVo());

            List<String> collectionValue = element.getCollectionValue();
            assertEquals(3, collectionValue.size());
            assertEquals("user3", collectionValue.get(0));
            assertEquals("user1", collectionValue.get(1));
            assertEquals("user2", collectionValue.get(2));
        }
    }

    @Nested
    @DisplayName("varName and elementCode")
    class VarNameAndElementCodeTest {

        @Test
        @DisplayName("should concatenate varName with elementCode for single element")
        void shouldConcatenateVarNameWithElementCode() {
            BpmnNodeParamsVo params = createSingleParams("user1", "Zhang San");

            BpmnConfCommonElementVo element = adaptor.commonElementVo(null, params, 5, "task1", new BpmnStartConditionsVo());

            assertEquals("testVar5", element.getAssigneeParamName());
        }

        @Test
        @DisplayName("should concatenate varName with elementCode for multi element collection name")
        void shouldConcatenateVarNameWithElementCodeForMulti() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1"),
                    Arrays.asList("Zhang San"),
                    Arrays.asList(0)
            );

            BpmnConfCommonElementVo element = adaptor.commonElementVo(null, params, 3, "task1", new BpmnStartConditionsVo());

            assertEquals("testVar3", element.getCollectionName());
        }
    }

    private BpmnNodeParamsVo createSingleParams(String assigneeId, String assigneeName) {
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE.getCode());
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee(assigneeId);
        assignee.setAssigneeName(assigneeName);
        assignee.setElementName(assigneeName);
        params.setAssignee(assignee);
        return params;
    }

    private BpmnNodeParamsVo createMultiParams(List<String> assigneeIds, List<String> assigneeNames, List<Integer> isDeduplication) {
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        params.setParamType(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER.getCode());
        List<BpmnNodeParamsAssigneeVo> assigneeList = new ArrayList<>();
        for (int i = 0; i < assigneeIds.size(); i++) {
            BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
            assignee.setAssignee(assigneeIds.get(i));
            assignee.setAssigneeName(assigneeNames.get(i));
            assignee.setElementName(assigneeNames.get(i));
            assignee.setIsDeduplication(isDeduplication.get(i));
            assigneeList.add(assignee);
        }
        params.setAssigneeList(assigneeList);
        return params;
    }
}
