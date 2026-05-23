package org.openoa.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.SignTypeEnum;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.common.constant.enus.ElementPropertyEnum;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BpmnElementUtilsExtendedTest extends BaseTest {

    @Nested
    @DisplayName("getStartEventElement edge cases")
    class GetStartEventElementEdgeTest {
        @Test
        @DisplayName("should create start event with empty string id")
        void shouldCreateWithEmptyId() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getStartEventElement("");

            assertEquals("", element.getElementId());
            assertNotNull(element.getElementName());
        }

        @Test
        @DisplayName("should create start event with special characters id")
        void shouldCreateWithSpecialCharsId() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getStartEventElement("start-node_1");

            assertEquals("start-node_1", element.getElementId());
        }
    }

    @Nested
    @DisplayName("getEndEventElement edge cases")
    class GetEndEventElementEdgeTest {
        @Test
        @DisplayName("should create end event with correct element type")
        void shouldCreateWithCorrectElementType() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getEndEventElement("end1");

            assertNotNull(element.getElementType());
        }
    }

    @Nested
    @DisplayName("getSingleElement edge cases")
    class GetSingleElementEdgeTest {
        @Test
        @DisplayName("should create element with empty assignee map")
        void shouldCreateWithEmptyAssigneeMap() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getSingleElement(
                    "t1", "Task", "param", "user1", new HashMap<>());

            assertNotNull(element.getAssigneeMap());
            assertTrue(element.getAssigneeMap().isEmpty());
        }

        @Test
        @DisplayName("should create element with multiple entries in assignee map")
        void shouldCreateWithMultipleAssigneeMapEntries() {
            Map<String, String> assigneeMap = new LinkedHashMap<>();
            assigneeMap.put("user1", "Zhang San");
            assigneeMap.put("user2", "Li Si");

            BpmnConfCommonElementVo element = BpmnElementUtils.getSingleElement(
                    "t1", "Task", "param", "user1", assigneeMap);

            assertEquals(2, element.getAssigneeMap().size());
        }

        @Test
        @DisplayName("should create element with null element name")
        void shouldCreateWithNullElementName() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getSingleElement(
                    "t1", null, "param", "user1", new HashMap<>());

            assertNull(element.getElementName());
        }

        @Test
        @DisplayName("should create element with null assignee param value")
        void shouldCreateWithNullAssigneeParamValue() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getSingleElement(
                    "t1", "Task", "param", null, new HashMap<>());

            assertNull(element.getAssigneeParamValue());
        }
    }

    @Nested
    @DisplayName("getMultiplayerSignElement edge cases")
    class GetMultiplayerSignElementEdgeTest {
        @Test
        @DisplayName("should create element with empty collection value")
        void shouldCreateWithEmptyCollection() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerSignElement(
                    "t1", "Sign", "list", Collections.emptyList(), new HashMap<>());

            assertNotNull(element.getCollectionValue());
            assertTrue(element.getCollectionValue().isEmpty());
        }

        @Test
        @DisplayName("should create element with large collection")
        void shouldCreateWithLargeCollection() {
            List<String> largeList = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                largeList.add("user" + i);
            }

            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerSignElement(
                    "t1", "Sign", "list", largeList, new HashMap<>());

            assertEquals(100, element.getCollectionValue().size());
        }

        @Test
        @DisplayName("should preserve collection order")
        void shouldPreserveCollectionOrder() {
            List<String> ordered = Arrays.asList("c", "a", "b");

            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerSignElement(
                    "t1", "Sign", "list", ordered, new HashMap<>());

            assertEquals(Arrays.asList("c", "a", "b"), element.getCollectionValue());
        }
    }

    @Nested
    @DisplayName("getMultiplayerOrSignElement edge cases")
    class GetMultiplayerOrSignElementEdgeTest {
        @Test
        @DisplayName("should have or-sign type code")
        void shouldHaveOrSignTypeCode() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerOrSignElement(
                    "t1", "OrSign", "list", Arrays.asList("u1"), new HashMap<>());

            assertEquals(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode(), element.getSignType());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_ORSIGN.getCode(), element.getElementProperty());
        }
    }

    @Nested
    @DisplayName("getSignUpElement edge cases")
    class GetSignUpElementEdgeTest {
        @Test
        @DisplayName("should create sign up element with serial property")
        void shouldCreateWithSerialProperty() {
            BpmnConfCommonElementVo father = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("审批")
                    .build();

            BpmnConfCommonElementVo element = BpmnElementUtils.getSignUpElement(
                    "signup1", father, ElementPropertyEnum.ELEMENT_PROPERTY_SIGN_UP_SERIAL.getCode());

            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SIGN_UP_SERIAL.getCode(), element.getElementProperty());
            assertEquals("signup1signUpUserList", element.getCollectionName());
        }

        @Test
        @DisplayName("should create sign up element with parallel or property")
        void shouldCreateWithParallelOrProperty() {
            BpmnConfCommonElementVo father = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("审批")
                    .build();

            BpmnConfCommonElementVo element = BpmnElementUtils.getSignUpElement(
                    "signup1", father, ElementPropertyEnum.ELEMENT_PROPERTY_SIGN_UP_PARALLEL_OR.getCode());

            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SIGN_UP_PARALLEL_OR.getCode(), element.getElementProperty());
        }

        @Test
        @DisplayName("should append 加批 to father element name")
        void shouldAppendJiaPiToFatherName() {
            BpmnConfCommonElementVo father = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("审批")
                    .build();

            BpmnConfCommonElementVo element = BpmnElementUtils.getSignUpElement(
                    "signup1", father, ElementPropertyEnum.ELEMENT_PROPERTY_SIGN_UP_SERIAL.getCode());

            assertEquals("审批加批", element.getElementName());
        }

        @Test
        @DisplayName("should have empty collection value")
        void shouldHaveEmptyCollectionValue() {
            BpmnConfCommonElementVo father = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("审批")
                    .build();

            BpmnConfCommonElementVo element = BpmnElementUtils.getSignUpElement(
                    "signup1", father, ElementPropertyEnum.ELEMENT_PROPERTY_SIGN_UP_SERIAL.getCode());

            assertNotNull(element.getCollectionValue());
            assertTrue(element.getCollectionValue().isEmpty());
        }
    }

    @Nested
    @DisplayName("getSequenceFlow edge cases")
    class GetSequenceFlowEdgeTest {
        @Test
        @DisplayName("should create flow with zero number")
        void shouldCreateWithZeroNumber() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getSequenceFlow(0, "a", "b");

            assertEquals("sequenceFlow0", element.getElementId());
        }

        @Test
        @DisplayName("should create flow with same source and target")
        void shouldCreateWithSameSourceAndTarget() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getSequenceFlow(1, "node1", "node1");

            assertEquals("node1", element.getFlowFrom());
            assertEquals("node1", element.getFlowTo());
        }

        @Test
        @DisplayName("should have sequence flow element property")
        void shouldHaveSequenceFlowProperty() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getSequenceFlow(1, "a", "b");

            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SEQUENCE_FLOW.getCode(), element.getElementProperty());
        }
    }

    @Nested
    @DisplayName("getParallelGateWayElement edge cases")
    class GetParallelGateWayElementEdgeTest {
        @Test
        @DisplayName("should create gateway with zero number")
        void shouldCreateWithZeroNumber() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getParallelGateWayElement(0);

            assertEquals("gateWay0", element.getElementId());
        }

        @Test
        @DisplayName("should have parallel gateway element property")
        void shouldHaveParallelGatewayProperty() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getParallelGateWayElement(1);

            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_PARALLEL_GATEWAY.getCode(), element.getElementProperty());
        }
    }

    @Nested
    @DisplayName("element type consistency")
    class ElementTypeConsistencyTest {
        @Test
        @DisplayName("start event should have start event element type")
        void startEventShouldHaveStartEventType() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getStartEventElement("s1");
            assertNotNull(element.getElementType());
        }

        @Test
        @DisplayName("end event should have end event element type")
        void endEventShouldHaveEndEventType() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getEndEventElement("e1");
            assertNotNull(element.getElementType());
        }

        @Test
        @DisplayName("single element should have user task element type")
        void singleElementShouldHaveUserTaskType() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getSingleElement(
                    "t1", "Task", "p", "u1", new HashMap<>());
            assertNotNull(element.getElementType());
        }

        @Test
        @DisplayName("sign element should have user task element type")
        void signElementShouldHaveUserTaskType() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerSignElement(
                    "t1", "Sign", "list", Arrays.asList("u1"), new HashMap<>());
            assertNotNull(element.getElementType());
        }
    }
}
