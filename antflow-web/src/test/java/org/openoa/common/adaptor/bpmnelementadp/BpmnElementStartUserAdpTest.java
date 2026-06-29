package org.openoa.common.adaptor.bpmnelementadp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.MockBaseTest;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeParamsVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.common.constant.enus.ElementPropertyEnum;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BpmnElementStartUserAdpTest extends MockBaseTest {

    private BpmnElementStartUserAdp adaptor;

    @BeforeEach
    void setUp() {
        adaptor = new BpmnElementStartUserAdp();
    }

    @Nested
    @DisplayName("getElementVo with normal assignee")
    class NormalAssigneeTest {

        @Test
        @DisplayName("should create single element with assigneeParamName startUser{elementCode}")
        void shouldCreateSingleElementWithStartUserParamName() {
            BpmnNodeParamsVo params = createParamsWithAssignee("user1", "Zhang San");
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.getElementVo(null, params, 1, "task1", startConditions);

            assertEquals("startUser1", element.getAssigneeParamName());
        }

        @Test
        @DisplayName("should set correct assignee and assigneeParamValue")
        void shouldSetCorrectAssigneeAndParamValue() {
            BpmnNodeParamsVo params = createParamsWithAssignee("user1", "Zhang San");
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.getElementVo(null, params, 1, "task1", startConditions);

            assertEquals("user1", element.getAssigneeParamValue());
        }

        @Test
        @DisplayName("should set correct elementId")
        void shouldSetCorrectElementId() {
            BpmnNodeParamsVo params = createParamsWithAssignee("user1", "Zhang San");
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.getElementVo(null, params, 1, "task1", startConditions);

            assertEquals("task1", element.getElementId());
        }

        @Test
        @DisplayName("should concatenate startUser with different elementCode values")
        void shouldConcatenateStartUserWithDifferentElementCodes() {
            BpmnNodeParamsVo params = createParamsWithAssignee("user1", "Zhang San");
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.getElementVo(null, params, 7, "task1", startConditions);

            assertEquals("startUser7", element.getAssigneeParamName());
        }
    }

    @Nested
    @DisplayName("getElementVo with null assignee")
    class NullAssigneeTest {

        @Test
        @DisplayName("should handle null assignee in params gracefully")
        void shouldHandleNullAssigneeGracefully() {
            BpmnNodeParamsVo params = new BpmnNodeParamsVo();
            params.setAssignee(null);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.getElementVo(null, params, 1, "task1", startConditions);

            assertNotNull(element);
            assertNull(element.getAssigneeParamValue());
        }

        @Test
        @DisplayName("should still produce assigneeParamName with null assignee")
        void shouldStillProduceParamNameWithNullAssignee() {
            BpmnNodeParamsVo params = new BpmnNodeParamsVo();
            params.setAssignee(null);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.getElementVo(null, params, 2, "task1", startConditions);

            assertEquals("startUser2", element.getAssigneeParamName());
        }
    }

    @Nested
    @DisplayName("getElementVo with elementName")
    class ElementNameTest {

        @Test
        @DisplayName("should use assignee elementName as element name")
        void shouldUseAssigneeElementName() {
            BpmnNodeParamsVo params = createParamsWithAssignee("user1", "Zhang San");
            params.getAssignee().setElementName("Start User Task");
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.getElementVo(null, params, 1, "task1", startConditions);

            assertEquals("Start User Task", element.getElementName());
        }

        @Test
        @DisplayName("should use null elementName when assignee elementName is null")
        void shouldUseNullElementNameWhenAssigneeElementNameIsNull() {
            BpmnNodeParamsVo params = createParamsWithAssignee("user1", "Zhang San");
            params.getAssignee().setElementName(null);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.getElementVo(null, params, 1, "task1", startConditions);

            assertNull(element.getElementName());
        }
    }

    @Nested
    @DisplayName("element property")
    class ElementPropertyTest {

        @Test
        @DisplayName("should have SINGLE element property")
        void shouldHaveSingleElementProperty() {
            BpmnNodeParamsVo params = createParamsWithAssignee("user1", "Zhang San");
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.getElementVo(null, params, 1, "task1", startConditions);

            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode(), element.getElementProperty());
        }

        @Test
        @DisplayName("should have SINGLE element property even with null assignee")
        void shouldHaveSingleElementPropertyWithNullAssignee() {
            BpmnNodeParamsVo params = new BpmnNodeParamsVo();
            params.setAssignee(null);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.getElementVo(null, params, 1, "task1", startConditions);

            assertEquals(ElementPropertyEnum.ELEMENT_PROPERTY_SINGLE.getCode(), element.getElementProperty());
        }
    }

    @Nested
    @DisplayName("assignee map")
    class AssigneeMapTest {

        @Test
        @DisplayName("should contain correct key-value pair in assigneeMap")
        void shouldContainCorrectKeyValuePairInAssigneeMap() {
            BpmnNodeParamsVo params = createParamsWithAssignee("user1", "Zhang San");
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.getElementVo(null, params, 1, "task1", startConditions);

            Map<String, String> assigneeMap = element.getAssigneeMap();
            assertNotNull(assigneeMap);
            assertEquals(1, assigneeMap.size());
            assertEquals("Zhang San", assigneeMap.get("user1"));
        }

        @Test
        @DisplayName("should contain null-key entry when assignee is null")
        void shouldContainNullKeyEntryWhenAssigneeIsNull() {
            BpmnNodeParamsVo params = new BpmnNodeParamsVo();
            params.setAssignee(null);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            BpmnConfCommonElementVo element = adaptor.getElementVo(null, params, 1, "task1", startConditions);

            Map<String, String> assigneeMap = element.getAssigneeMap();
            assertNotNull(assigneeMap);
            assertTrue(assigneeMap.containsKey(null));
            assertNull(assigneeMap.get(null));
        }
    }

    private BpmnNodeParamsVo createParamsWithAssignee(String assigneeId, String assigneeName) {
        BpmnNodeParamsVo params = new BpmnNodeParamsVo();
        BpmnNodeParamsAssigneeVo assignee = new BpmnNodeParamsAssigneeVo();
        assignee.setAssignee(assigneeId);
        assignee.setAssigneeName(assigneeName);
        assignee.setElementName(assigneeName);
        params.setAssignee(assignee);
        return params;
    }
}
