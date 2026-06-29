package org.openoa.base.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class VoDefaultsRound16Test extends BaseTest {

    @Nested
    @DisplayName("BusinessDataVo defaults")
    class BusinessDataVoDefaultsTest {
        @Test
        @DisplayName("startUserId should default to empty string")
        void startUserIdDefault() {
            BusinessDataVo vo = new BusinessDataVo();
            assertEquals("", vo.getStartUserId());
        }

        @Test
        @DisplayName("isOutSideAccessProc should default to false")
        void isOutSideAccessProcDefault() {
            BusinessDataVo vo = new BusinessDataVo();
            assertFalse(vo.getIsOutSideAccessProc());
        }

        @Test
        @DisplayName("isLowCodeFlow should default to 0")
        void isLowCodeFlowDefault() {
            BusinessDataVo vo = new BusinessDataVo();
            assertEquals(0, vo.getIsLowCodeFlow());
        }

        @Test
        @DisplayName("isOutSideChecked should default to false")
        void isOutSideCheckedDefault() {
            BusinessDataVo vo = new BusinessDataVo();
            assertFalse(vo.getIsOutSideChecked());
        }

        @Test
        @DisplayName("isFreeRide should default to false")
        void isFreeRideDefault() {
            BusinessDataVo vo = new BusinessDataVo();
            assertFalse(vo.getIsFreeRide());
        }

        @Test
        @DisplayName("builder should not preserve field defaults")
        void builderNoDefaults() {
            BusinessDataVo vo = BusinessDataVo.builder().build();
            assertNull(vo.getStartUserId());
            assertNull(vo.getIsOutSideAccessProc());
            assertNull(vo.getIsLowCodeFlow());
        }

        @Test
        @DisplayName("builder should override defaults when set")
        void builderOverrides() {
            BusinessDataVo vo = BusinessDataVo.builder()
                    .startUserId("U001")
                    .isOutSideAccessProc(true)
                    .isLowCodeFlow(1)
                    .build();
            assertEquals("U001", vo.getStartUserId());
            assertTrue(vo.getIsOutSideAccessProc());
            assertEquals(1, vo.getIsLowCodeFlow());
        }
    }

    @Nested
    @DisplayName("BpmnStartConditionsVo defaults")
    class BpmnStartConditionsVoDefaultsTest {
        @Test
        @DisplayName("empToForwardList should default to empty ArrayList")
        void empToForwardListDefault() {
            BpmnStartConditionsVo vo = new BpmnStartConditionsVo();
            assertNotNull(vo.getEmpToForwardList());
            assertTrue(vo.getEmpToForwardList().isEmpty());
        }

        @Test
        @DisplayName("isOutSideAccessProc should default to false")
        void isOutSideAccessProcDefault() {
            BpmnStartConditionsVo vo = new BpmnStartConditionsVo();
            assertFalse(vo.getIsOutSideAccessProc());
        }

        @Test
        @DisplayName("builder should not preserve field defaults")
        void builderNoDefaults() {
            BpmnStartConditionsVo vo = BpmnStartConditionsVo.builder().build();
            assertNull(vo.getEmpToForwardList());
            assertNull(vo.getIsOutSideAccessProc());
        }

        @Test
        @DisplayName("should set and get fields")
        void setAndGet() {
            BpmnStartConditionsVo vo = new BpmnStartConditionsVo();
            vo.setProcessNum("P001");
            vo.setStartUserId("U001");
            vo.setLeaveHour(8.0);
            vo.setIsOutSideProcess(1);
            assertEquals("P001", vo.getProcessNum());
            assertEquals("U001", vo.getStartUserId());
            assertEquals(8.0, vo.getLeaveHour(), 0.001);
            assertEquals(1, vo.getIsOutSideProcess().intValue());
        }
    }

    @Nested
    @DisplayName("BpmnConfCommonElementVo defaults")
    class BpmnConfCommonElementVoDefaultsTest {
        @Test
        @DisplayName("isLastSequenceFlow should default to 0 via @Builder.Default")
        void isLastSequenceFlowDefault() {
            BpmnConfCommonElementVo vo = BpmnConfCommonElementVo.builder().build();
            assertEquals(0, vo.getIsLastSequenceFlow());
        }

        @Test
        @DisplayName("isSignUp should default to 0")
        void isSignUpDefault() {
            BpmnConfCommonElementVo vo = new BpmnConfCommonElementVo();
            assertEquals(0, vo.getIsSignUp());
        }

        @Test
        @DisplayName("isBackSignUp should default to 0")
        void isBackSignUpDefault() {
            BpmnConfCommonElementVo vo = new BpmnConfCommonElementVo();
            assertEquals(0, vo.getIsBackSignUp());
        }

        @Test
        @DisplayName("isSignUpSubElement should default to 0")
        void isSignUpSubElementDefault() {
            BpmnConfCommonElementVo vo = new BpmnConfCommonElementVo();
            assertEquals(0, vo.getIsSignUpSubElement());
        }

        @Test
        @DisplayName("isSignUpSequenceFlow should default to 0")
        void isSignUpSequenceFlowDefault() {
            BpmnConfCommonElementVo vo = new BpmnConfCommonElementVo();
            assertEquals(0, vo.getIsSignUpSequenceFlow());
        }

        @Test
        @DisplayName("builder should set all fields")
        void builderSetsAll() {
            BpmnConfCommonElementVo vo = BpmnConfCommonElementVo.builder()
                    .elementId("E001")
                    .nodeId("N001")
                    .elementName("TestElement")
                    .elementType(1)
                    .elementProperty(5)
                    .assigneeParamName("assignee")
                    .assigneeParamValue("user1")
                    .collectionName("users")
                    .flowFrom("from1")
                    .flowTo("to1")
                    .isLastSequenceFlow(1)
                    .isSignUp(1)
                    .signType(2)
                    .build();
            assertEquals("E001", vo.getElementId());
            assertEquals("N001", vo.getNodeId());
            assertEquals(1, vo.getIsLastSequenceFlow());
            assertEquals(1, vo.getIsSignUp());
            assertEquals(2, vo.getSignType().intValue());
        }
    }
}
