package org.openoa.engine.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class CallbackVoTest extends BaseTest {

    @Nested
    @DisplayName("ProcBaseCallBackVo")
    class ProcBaseCallBackVoTest {
        @Test
        @DisplayName("default constructor should have null formData")
        void defaultConstructor() {
            ProcBaseCallBackVo vo = new ProcBaseCallBackVo();
            assertNull(vo.getFormData());
            assertNull(vo.getEventType());
            assertNull(vo.getFormCode());
            assertNull(vo.getProcessNum());
        }

        @Test
        @DisplayName("string constructor should set formData")
        void stringConstructor() {
            ProcBaseCallBackVo vo = new ProcBaseCallBackVo("test-data");
            assertEquals("test-data", vo.getFormData());
        }

        @Test
        @DisplayName("should inherit CallbackReqVo fields")
        void shouldInheritFields() {
            ProcBaseCallBackVo vo = new ProcBaseCallBackVo();
            vo.setEventType("START");
            vo.setFormCode("FORM001");
            vo.setProcessNum("PROC001");
            vo.setBusinessPartyMark("PARTY_A");
            vo.setBusinessId("BIZ001");
            assertEquals("START", vo.getEventType());
            assertEquals("FORM001", vo.getFormCode());
            assertEquals("PROC001", vo.getProcessNum());
            assertEquals("PARTY_A", vo.getBusinessPartyMark());
            assertEquals("BIZ001", vo.getBusinessId());
        }
    }

    @Nested
    @DisplayName("CallbackRespVo")
    class CallbackRespVoTest {
        @Test
        @DisplayName("should set and get all fields")
        void shouldSetAndGetAllFields() {
            CallbackRespVo vo = new CallbackRespVo();
            vo.setStatus("success");
            vo.setBusinessId("biz001");
            vo.setBusinessPartyMark("PARTY_A");
            vo.setExtend("extra info");
            assertEquals("success", vo.getStatus());
            assertEquals("biz001", vo.getBusinessId());
            assertEquals("PARTY_A", vo.getBusinessPartyMark());
            assertEquals("extra info", vo.getExtend());
        }

        @Test
        @DisplayName("default values should be null")
        void defaultValuesShouldBeNull() {
            CallbackRespVo vo = new CallbackRespVo();
            assertNull(vo.getStatus());
            assertNull(vo.getBusinessId());
            assertNull(vo.getBusinessPartyMark());
            assertNull(vo.getExtend());
        }
    }

    @Nested
    @DisplayName("CallbackReqVo")
    class CallbackReqVoTest {
        @Test
        @DisplayName("should set and get all fields")
        void shouldSetAndGetAllFields() {
            CallbackReqVo vo = new CallbackReqVo();
            vo.setEventType("END");
            vo.setBusinessPartyMark("PARTY_B");
            vo.setFormCode("FORM002");
            vo.setProcessNum("PROC002");
            vo.setBusinessId("BIZ002");
            assertEquals("END", vo.getEventType());
            assertEquals("PARTY_B", vo.getBusinessPartyMark());
            assertEquals("FORM002", vo.getFormCode());
            assertEquals("PROC002", vo.getProcessNum());
            assertEquals("BIZ002", vo.getBusinessId());
        }
    }
}
