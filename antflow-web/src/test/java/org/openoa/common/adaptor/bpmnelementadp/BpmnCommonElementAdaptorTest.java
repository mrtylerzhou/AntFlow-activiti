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

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BpmnCommonElementAdaptorTest extends MockBaseTest {

    private TestableBpmnCommonElementAdaptor adaptor;
    private Method getCommonElementVoMethod;

    static class TestableBpmnCommonElementAdaptor extends BpmnCommonElementAdaptor {
        @Override
        protected BpmnConfCommonElementVo getElementVo(BpmnNodePropertysVo property, BpmnNodeParamsVo params,
                                                       Integer elementCode, String elementId, BpmnStartConditionsVo bpmnStartConditions) {
            return null;
        }

        @Override
        public void setSupportBusinessObjects() {
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        adaptor = new TestableBpmnCommonElementAdaptor();
        getCommonElementVoMethod = BpmnCommonElementAdaptor.class.getDeclaredMethod(
                "getCommonElementVo",
                BpmnNodePropertysVo.class,
                BpmnNodeParamsVo.class,
                Integer.class,
                String.class,
                String.class
        );
        getCommonElementVoMethod.setAccessible(true);
    }

    private BpmnConfCommonElementVo invokeGetCommonElementVo(BpmnNodePropertysVo property,
                                                             BpmnNodeParamsVo params,
                                                             Integer elementCode,
                                                             String elementId,
                                                             String collectionName) throws Exception {
        return (BpmnConfCommonElementVo) getCommonElementVoMethod.invoke(
                adaptor, property, params, elementCode, elementId, collectionName
        );
    }

    @Nested
    @DisplayName("getCommonElementVo")
    class GetCommonElementVoTest {

        @Test
        @DisplayName("should use assignee info when params has an assignee")
        void shouldUseAssigneeInfoWhenParamsHasAssignee() throws Exception {
            BpmnNodeParamsAssigneeVo assigneeVo = BpmnNodeParamsAssigneeVo.builder()
                    .assignee("user001")
                    .assigneeName("Zhang San")
                    .elementName("Approver Node")
                    .build();
            BpmnNodeParamsVo params = BpmnNodeParamsVo.builder()
                    .assignee(assigneeVo)
                    .build();
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();

            BpmnConfCommonElementVo result = invokeGetCommonElementVo(property, params, 5, "elem_5", "collection_");

            assertEquals("user001", result.getAssigneeParamValue());
            assertEquals("Approver Node", result.getElementName());
        }

        @Test
        @DisplayName("should use default empty BpmnNodeParamsAssigneeVo when params has null assignee")
        void shouldUseDefaultEmptyAssigneeVoWhenParamsHasNullAssignee() throws Exception {
            BpmnNodeParamsVo params = BpmnNodeParamsVo.builder()
                    .assignee(null)
                    .build();
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();

            BpmnConfCommonElementVo result = invokeGetCommonElementVo(property, params, 3, "elem_3", "col_");

            assertNotNull(result);
            assertNull(result.getAssigneeParamValue());
        }

        @Test
        @DisplayName("should build singleAssigneeMap with assignee as key and assigneeName as value")
        void shouldBuildSingleAssigneeMapWithAssigneeAsKeyAndNameAsValue() throws Exception {
            BpmnNodeParamsAssigneeVo assigneeVo = BpmnNodeParamsAssigneeVo.builder()
                    .assignee("emp_001")
                    .assigneeName("Li Si")
                    .elementName("Reviewer")
                    .build();
            BpmnNodeParamsVo params = BpmnNodeParamsVo.builder()
                    .assignee(assigneeVo)
                    .build();
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();

            BpmnConfCommonElementVo result = invokeGetCommonElementVo(property, params, 2, "elem_2", "col_");

            Map<String, String> assigneeMap = result.getAssigneeMap();
            assertNotNull(assigneeMap);
            assertEquals("Li Si", assigneeMap.get("emp_001"));
        }

        @Test
        @DisplayName("should pass elementId as elementId to getSingleElement")
        void shouldPassElementIdToGetSingleElement() throws Exception {
            BpmnNodeParamsAssigneeVo assigneeVo = BpmnNodeParamsAssigneeVo.builder()
                    .assignee("a1")
                    .assigneeName("Name A")
                    .elementName("Element A")
                    .build();
            BpmnNodeParamsVo params = BpmnNodeParamsVo.builder()
                    .assignee(assigneeVo)
                    .build();
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();

            BpmnConfCommonElementVo result = invokeGetCommonElementVo(property, params, 1, "elem_custom_id", "col_");

            assertEquals("elem_custom_id", result.getElementId());
        }

        @Test
        @DisplayName("should pass elementName from assigneeVo to getSingleElement")
        void shouldPassElementNameFromAssigneeVoToGetSingleElement() throws Exception {
            BpmnNodeParamsAssigneeVo assigneeVo = BpmnNodeParamsAssigneeVo.builder()
                    .assignee("a2")
                    .assigneeName("Name B")
                    .elementName("Custom Element Name")
                    .build();
            BpmnNodeParamsVo params = BpmnNodeParamsVo.builder()
                    .assignee(assigneeVo)
                    .build();
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();

            BpmnConfCommonElementVo result = invokeGetCommonElementVo(property, params, 4, "elem_4", "col_");

            assertEquals("Custom Element Name", result.getElementName());
        }

        @Test
        @DisplayName("should join collectionName and elementCode as assigneeParamName")
        void shouldJoinCollectionNameAndElementCodeAsAssigneeParamName() throws Exception {
            BpmnNodeParamsAssigneeVo assigneeVo = BpmnNodeParamsAssigneeVo.builder()
                    .assignee("a3")
                    .assigneeName("Name C")
                    .elementName("Node C")
                    .build();
            BpmnNodeParamsVo params = BpmnNodeParamsVo.builder()
                    .assignee(assigneeVo)
                    .build();
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();

            BpmnConfCommonElementVo result = invokeGetCommonElementVo(property, params, 7, "elem_7", "myCollection_");

            assertEquals("myCollection_7", result.getAssigneeParamName());
        }

        @Test
        @DisplayName("should pass assignee as assigneeParamValue")
        void shouldPassAssigneeAsAssigneeParamValue() throws Exception {
            BpmnNodeParamsAssigneeVo assigneeVo = BpmnNodeParamsAssigneeVo.builder()
                    .assignee("user_999")
                    .assigneeName("Wang Wu")
                    .elementName("Final Approver")
                    .build();
            BpmnNodeParamsVo params = BpmnNodeParamsVo.builder()
                    .assignee(assigneeVo)
                    .build();
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();

            BpmnConfCommonElementVo result = invokeGetCommonElementVo(property, params, 10, "elem_10", "col_");

            assertEquals("user_999", result.getAssigneeParamValue());
        }

        @Test
        @DisplayName("should handle null assignee and assigneeName gracefully when using default BpmnNodeParamsAssigneeVo")
        void shouldHandleNullAssigneeFieldsGracefully() throws Exception {
            BpmnNodeParamsVo params = BpmnNodeParamsVo.builder()
                    .assignee(null)
                    .build();
            BpmnNodePropertysVo property = new BpmnNodePropertysVo();

            BpmnConfCommonElementVo result = invokeGetCommonElementVo(property, params, 1, "elem_1", "col_");

            assertNotNull(result);
            assertNull(result.getAssigneeParamValue());
            assertNull(result.getElementName());
            Map<String, String> assigneeMap = result.getAssigneeMap();
            assertNotNull(assigneeMap);
            assertNull(assigneeMap.get(null));
        }
    }
}
