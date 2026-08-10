package org.openoa.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.base.constant.enums.SignTypeEnum;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.common.constant.enus.ElementPropertyEnum;
import org.openoa.BaseTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BpmnElementUtilsTest extends BaseTest {

    @Nested
    @DisplayName("getStartEventElement")
    class GetStartEventElementTest {
        @Test
        @DisplayName("should create start event element with given id")
        void shouldCreateStartEventElement() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getStartEventElement("start1");

            assertEquals("start1", element.getElementId());
            assertNotNull(element.getElementName());
            assertNotNull(element.getElementType());
        }
    }

    @Nested
    @DisplayName("getEndEventElement")
    class GetEndEventElementTest {
        @Test
        @DisplayName("should create end event element with given id")
        void shouldCreateEndEventElement() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getEndEventElement("end1");

            assertEquals("end1", element.getElementId());
            assertNotNull(element.getElementName());
            assertNotNull(element.getElementType());
        }
    }

    @Nested
    @DisplayName("getSingleElement")
    class GetSingleElementTest {
        @Test
        @DisplayName("should create single assignee element")
        void shouldCreateSingleElement() {
            Map<String, String> assigneeMap = new HashMap<>();
            assigneeMap.put("user1", "Zhang San");

            BpmnConfCommonElementVo element = BpmnElementUtils.getSingleElement(
                    "task1", "Approve", "assignee1", "user1", assigneeMap);

            assertEquals("task1", element.getElementId());
            assertEquals("Approve", element.getElementName());
            assertEquals("assignee1", element.getAssigneeParamName());
            assertEquals("user1", element.getAssigneeParamValue());
            assertEquals(assigneeMap, element.getAssigneeMap());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode(), element.getElementProperty());
        }
    }

    @Nested
    @DisplayName("getMultiplayerSignElement")
    class GetMultiplayerSignElementTest {
        @Test
        @DisplayName("should create multiplayer sign element")
        void shouldCreateMultiplayerSignElement() {
            List<String> collectionValue = Arrays.asList("user1", "user2");
            Map<String, String> assigneeMap = new HashMap<>();

            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerSignElement(
                    "signTask1", "Sign Approve", "userList", collectionValue, assigneeMap);

            assertEquals("signTask1", element.getElementId());
            assertEquals("Sign Approve", element.getElementName());
            assertEquals("userList", element.getCollectionName());
            assertEquals(collectionValue, element.getCollectionValue());
            assertEquals(SignTypeEnum.SIGN_TYPE_SIGN.getCode(), element.getSignType());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_SIGN.getCode(), element.getElementProperty());
        }
    }

    @Nested
    @DisplayName("getMultiplayerSignInOrderElement")
    class GetMultiplayerSignInOrderElementTest {
        @Test
        @DisplayName("should create multiplayer sign in order element")
        void shouldCreateMultiplayerSignInOrderElement() {
            List<String> collectionValue = Arrays.asList("user1", "user2");
            Map<String, String> assigneeMap = new HashMap<>();

            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerSignInOrderElement(
                    "orderTask1", "Order Sign", "userList", collectionValue, assigneeMap);

            assertEquals("orderTask1", element.getElementId());
            assertEquals(SignTypeEnum.SIGN_TYPE_SIGN_IN_ORDER.getCode(), element.getSignType());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_SIGN_IN_ORDER.getCode(), element.getElementProperty());
        }
    }

    @Nested
    @DisplayName("getMultiplayerOrSignElement")
    class GetMultiplayerOrSignElementTest {
        @Test
        @DisplayName("should create multiplayer or-sign element")
        void shouldCreateMultiplayerOrSignElement() {
            List<String> collectionValue = Arrays.asList("user1", "user2", "user3");
            Map<String, String> assigneeMap = new HashMap<>();

            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerOrSignElement(
                    "orTask1", "Or Sign", "userList", collectionValue, assigneeMap);

            assertEquals("orTask1", element.getElementId());
            assertEquals(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode(), element.getSignType());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_MULTIPLAYER_ORSIGN.getCode(), element.getElementProperty());
        }
    }

    @Nested
    @DisplayName("getSignUpElement")
    class GetSignUpElementTest {
        @Test
        @DisplayName("should create sign up element based on father element")
        void shouldCreateSignUpElement() {
            BpmnConfCommonElementVo fatherElement = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("Approve")
                    .build();

            BpmnConfCommonElementVo element = BpmnElementUtils.getSignUpElement(
                    "signup1", fatherElement, ElementPropertyEnum.ELEMENT_PROPERTY_SIGN_UP_SERIAL.getCode());

            assertEquals("signup1", element.getElementId());
            assertTrue(element.getElementName().contains("加批"));
            assertEquals("signup1signUpUserList", element.getCollectionName());
            assertNotNull(element.getCollectionValue());
            assertTrue(element.getCollectionValue().isEmpty());
        }
    }

    @Nested
    @DisplayName("getSequenceFlow")
    class GetSequenceFlowTest {
        @Test
        @DisplayName("should create sequence flow element")
        void shouldCreateSequenceFlow() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getSequenceFlow(1, "task1", "task2");

            assertEquals("sequenceFlow1", element.getElementId());
            assertEquals("sequenceFlow1", element.getElementName());
            assertEquals("task1", element.getFlowFrom());
            assertEquals("task2", element.getFlowTo());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SEQUENCE_FLOW.getCode(), element.getElementProperty());
        }

        @Test
        @DisplayName("should create sequence flow with different numbers")
        void shouldCreateSequenceFlowWithDifferentNumbers() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getSequenceFlow(99, "a", "b");

            assertEquals("sequenceFlow99", element.getElementId());
        }
    }

    @Nested
    @DisplayName("getParallelGateWayElement")
    class GetParallelGateWayElementTest {
        @Test
        @DisplayName("should create parallel gateway element")
        void shouldCreateParallelGatewayElement() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getParallelGateWayElement(1);

            assertEquals("gateWay1", element.getElementId());
            assertEquals("gateWay1", element.getElementName());
            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_PARALLEL_GATEWAY.getCode(), element.getElementProperty());
        }

        @Test
        @DisplayName("should create parallel gateway element with different numbers")
        void shouldCreateParallelGatewayElementWithDifferentNumbers() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getParallelGateWayElement(42);

            assertEquals("gateWay42", element.getElementId());
        }
    }

    @Nested
    @DisplayName("element property consistency")
    class ElementPropertyConsistencyTest {

        @Test
        @DisplayName("single element should have correct element property code")
        void singleElementShouldHaveCorrectProperty() {
            Map<String, String> assigneeMap = new HashMap<>();
            BpmnConfCommonElementVo element = BpmnElementUtils.getSingleElement(
                    "t1", "Task", "param", "user1", assigneeMap);

            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode(), element.getElementProperty());
        }

        @Test
        @DisplayName("sign element should have sign type code")
        void signElementShouldHaveSignType() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerSignElement(
                    "t1", "Sign", "list", Arrays.asList("u1"), new HashMap<>());

            assertEquals(SignTypeEnum.SIGN_TYPE_SIGN.getCode(), element.getSignType());
        }

        @Test
        @DisplayName("or-sign element should have or-sign type code")
        void orSignElementShouldHaveOrSignType() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerOrSignElement(
                    "t1", "OrSign", "list", Arrays.asList("u1"), new HashMap<>());

            assertEquals(SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode(), element.getSignType());
        }

        @Test
        @DisplayName("sign-in-order element should have sign-in-order type code")
        void signInOrderElementShouldHaveSignInOrderType() {
            BpmnConfCommonElementVo element = BpmnElementUtils.getMultiplayerSignInOrderElement(
                    "t1", "OrderSign", "list", Arrays.asList("u1"), new HashMap<>());

            assertEquals(SignTypeEnum.SIGN_TYPE_SIGN_IN_ORDER.getCode(), element.getSignType());
        }
    }

    @Nested
    @DisplayName("getSignUpElement variations")
    class GetSignUpElementVariationsTest {

        @Test
        @DisplayName("sign up parallel element should have parallel code")
        void signUpParallelElementShouldHaveParallelCode() {
            BpmnConfCommonElementVo father = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("Approve")
                    .build();

            BpmnConfCommonElementVo element = BpmnElementUtils.getSignUpElement(
                    "signup1", father, ElementPropertyEnum.ELEMENT_PROPERTY_SIGN_UP_PARALLEL_OR.getCode());

            assertNotNull(element);
            assertEquals("signup1", element.getElementId());
        }

        @Test
        @DisplayName("sign up element should contain father element name in its name")
        void signUpElementShouldContainFatherName() {
            BpmnConfCommonElementVo father = BpmnConfCommonElementVo.builder()
                    .elementId("task1")
                    .elementName("审批节点")
                    .build();

            BpmnConfCommonElementVo element = BpmnElementUtils.getSignUpElement(
                    "signup1", father, ElementPropertyEnum.ELEMENT_PROPERTY_SIGN_UP_SERIAL.getCode());

            assertTrue(element.getElementName().contains("审批节点"));
        }
    }
}
