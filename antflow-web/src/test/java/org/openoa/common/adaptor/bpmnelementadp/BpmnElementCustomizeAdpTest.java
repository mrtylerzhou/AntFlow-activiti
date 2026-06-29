package org.openoa.common.adaptor.bpmnelementadp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.DuplicationProcessStrategyEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
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

class BpmnElementCustomizeAdpTest extends MockBaseTest {

    private BpmnElementCustomizeAdp adaptor;

    @BeforeEach
    void setUp() {
        adaptor = new BpmnElementCustomizeAdp();
    }

    @Nested
    @DisplayName("sign type dispatch")
    class SignTypeDispatchTest {

        @Test
        @DisplayName("should create multiplayer sign element when sign type is SIGN")
        void shouldCreateMultiplayerSignElement() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(0, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_SIGN.getCode());

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 1, "signTask1", new BpmnStartConditionsVo());

            assertEquals("signTask1", element.getElementId());
            assertEquals(SignTypeEnum.SIGN_TYPE_SIGN.getCode(), element.getSignType());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_SIGN.getCode(), element.getElementProperty());
        }

        @Test
        @DisplayName("should create multiplayer sign-in-order element when sign type is SIGN_IN_ORDER")
        void shouldCreateMultiplayerSignInOrderElement() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(0, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_SIGN_IN_ORDER.getCode());

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 1, "orderTask1", new BpmnStartConditionsVo());

            assertEquals("orderTask1", element.getElementId());
            assertEquals(SignTypeEnum.SIGN_TYPE_SIGN_IN_ORDER.getCode(), element.getSignType());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_SIGN_IN_ORDER.getCode(), element.getElementProperty());
        }

        @Test
        @DisplayName("should create multiplayer or-sign element when sign type is OR_SIGN")
        void shouldCreateMultiplayerOrSignElement() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(0, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode());

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 1, "orTask1", new BpmnStartConditionsVo());

            assertEquals("orTask1", element.getElementId());
            assertEquals(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode(), element.getSignType());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_ORSIGN.getCode(), element.getElementProperty());
        }

        @Test
        @DisplayName("should default to or-sign element when sign type is null")
        void shouldDefaultToOrSignElementWhenSignTypeNull() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(0, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(null);

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 1, "defaultTask1", new BpmnStartConditionsVo());

            assertEquals("defaultTask1", element.getElementId());
            assertEquals(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode(), element.getSignType());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_ORSIGN.getCode(), element.getElementProperty());
        }
    }

    @Nested
    @DisplayName("collection name")
    class CollectionNameTest {

        @Test
        @DisplayName("should concatenate customizeUser with elementCode")
        void shouldConcatenateCustomizeUserWithElementCode() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1"),
                    Arrays.asList("Zhang San"),
                    Arrays.asList(0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_SIGN.getCode());

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 5, "task1", new BpmnStartConditionsVo());

            assertEquals("customizeUser5", element.getCollectionName());
        }

        @Test
        @DisplayName("should use correct collection name for different element codes")
        void shouldUseCorrectCollectionNameForDifferentElementCodes() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1"),
                    Arrays.asList("Zhang San"),
                    Arrays.asList(0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode());

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 10, "task2", new BpmnStartConditionsVo());

            assertEquals("customizeUser10", element.getCollectionName());
        }
    }

    @Nested
    @DisplayName("element name")
    class ElementNameTest {

        @Test
        @DisplayName("should use first assignee element name as element name")
        void shouldUseFirstAssigneeElementName() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(0, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_SIGN.getCode());

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 1, "task1", new BpmnStartConditionsVo());

            assertEquals("Zhang San", element.getElementName());
        }

        @Test
        @DisplayName("should use first assignee element name regardless of sign type")
        void shouldUseFirstAssigneeElementNameRegardlessOfSignType() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Wang Wu", "Zhao Liu"),
                    Arrays.asList(0, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_SIGN_IN_ORDER.getCode());

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 1, "task1", new BpmnStartConditionsVo());

            assertEquals("Wang Wu", element.getElementName());
        }
    }

    @Nested
    @DisplayName("deduplication handling")
    class DeduplicationHandlingTest {

        @Test
        @DisplayName("should include non-deduplicated assignees")
        void shouldIncludeNonDeduplicatedAssignees() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(0, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_SIGN.getCode());

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 1, "task1", new BpmnStartConditionsVo());

            assertEquals(2, element.getCollectionValue().size());
            assertTrue(element.getCollectionValue().contains("user1"));
            assertTrue(element.getCollectionValue().contains("user2"));
        }

        @Test
        @DisplayName("should exclude deduplicated assignees with REMOVE strategy")
        void shouldExcludeDeduplicatedAssigneesWithRemoveStrategy() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2", "user3"),
                    Arrays.asList("Zhang San", "Li Si", "Wang Wu"),
                    Arrays.asList(0, 1, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_SIGN.getCode());

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setDuplicationProcessStrategy(DuplicationProcessStrategyEnum.REMOVE.getCode());

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 1, "task1", startConditions);

            assertEquals(2, element.getCollectionValue().size());
            assertTrue(element.getCollectionValue().contains("user1"));
            assertFalse(element.getCollectionValue().contains("user2"));
            assertTrue(element.getCollectionValue().contains("user3"));
        }

        @Test
        @DisplayName("should include deduplicated assignees with SKIP strategy")
        void shouldIncludeDeduplicatedAssigneesWithSkipStrategy() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2", "user3"),
                    Arrays.asList("Zhang San", "Li Si", "Wang Wu"),
                    Arrays.asList(0, 1, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_SIGN.getCode());

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setDuplicationProcessStrategy(DuplicationProcessStrategyEnum.SKIP.getCode());

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 1, "task1", startConditions);

            assertEquals(3, element.getCollectionValue().size());
            assertTrue(element.getCollectionValue().contains("user1"));
            assertTrue(element.getCollectionValue().contains("user2"));
            assertTrue(element.getCollectionValue().contains("user3"));
        }

        @Test
        @DisplayName("should exclude all deduplicated assignees when all are marked with REMOVE strategy")
        void shouldExcludeAllDeduplicatedWhenAllMarkedWithRemoveStrategy() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(1, 1)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode());

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setDuplicationProcessStrategy(DuplicationProcessStrategyEnum.REMOVE.getCode());

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 1, "task1", startConditions);

            assertTrue(element.getCollectionValue().isEmpty());
        }
    }

    @Nested
    @DisplayName("assignee map")
    class AssigneeMapTest {

        @Test
        @DisplayName("should populate assignee map with assignee id and name")
        void shouldPopulateAssigneeMap() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(0, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_SIGN.getCode());

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 1, "task1", new BpmnStartConditionsVo());

            assertNotNull(element.getAssigneeMap());
            assertEquals("Zhang San", element.getAssigneeMap().get("user1"));
            assertEquals("Li Si", element.getAssigneeMap().get("user2"));
        }

        @Test
        @DisplayName("should populate assignee map for or-sign element")
        void shouldPopulateAssigneeMapForOrSignElement() {
            BpmnNodeParamsVo params = createMultiParams(
                    Arrays.asList("user1", "user2"),
                    Arrays.asList("Zhang San", "Li Si"),
                    Arrays.asList(0, 0)
            );
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();
            property.setSignType(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode());

            BpmnConfCommonElementVo element = adaptor.getElementVo(property, params, 1, "task1", new BpmnStartConditionsVo());

            assertNotNull(element.getAssigneeMap());
            assertEquals("Zhang San", element.getAssigneeMap().get("user1"));
            assertEquals("Li Si", element.getAssigneeMap().get("user2"));
        }
    }

    @Nested
    @DisplayName("setSupportBusinessObjects")
    class SetSupportBusinessObjectsTest {

        @Test
        @DisplayName("should support NODE_PROPERTY_CUSTOMIZE business object")
        void shouldSupportCustomizeBusinessObject() {
            adaptor.setSupportBusinessObjects();

            assertTrue(adaptor.isSupportBusinessObject(NodePropertyEnum.NODE_PROPERTY_CUSTOMIZE));
        }
    }

    private BpmnNodeParamsVo createMultiParams(List<String> assigneeIds, List<String> assigneeNames, List<Integer> isDeduplication) {
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
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
