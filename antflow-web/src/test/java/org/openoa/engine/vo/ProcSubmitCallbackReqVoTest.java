package org.openoa.engine.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class ProcSubmitCallbackReqVoTest extends BaseTest {

    @Nested
    @DisplayName("no-arg constructor")
    class NoArgConstructorTest {
        @Test
        @DisplayName("should create with null formData")
        void nullFormData() {
            ProcSubmitCallbackReqVo vo = new ProcSubmitCallbackReqVo();
            assertNull(vo.getFormData());
        }

        @Test
        @DisplayName("should inherit CallbackReqVo fields as null")
        void inheritedFieldsNull() {
            ProcSubmitCallbackReqVo vo = new ProcSubmitCallbackReqVo();
            assertNull(vo.getEventType());
            assertNull(vo.getBusinessPartyMark());
            assertNull(vo.getFormCode());
            assertNull(vo.getProcessNum());
            assertNull(vo.getBusinessId());
        }
    }

    @Nested
    @DisplayName("formData constructor")
    class FormDataConstructorTest {
        @Test
        @DisplayName("should set formData")
        void shouldSetFormData() {
            ProcSubmitCallbackReqVo vo = new ProcSubmitCallbackReqVo("{\"key\":\"value\"}");
            assertEquals("{\"key\":\"value\"}", vo.getFormData());
        }

        @Test
        @DisplayName("should accept null formData")
        void nullFormData() {
            ProcSubmitCallbackReqVo vo = new ProcSubmitCallbackReqVo(null);
            assertNull(vo.getFormData());
        }
    }

    @Nested
    @DisplayName("inherited fields")
    class InheritedFieldsTest {
        @Test
        @DisplayName("should set and get inherited eventType")
        void eventType() {
            ProcSubmitCallbackReqVo vo = new ProcSubmitCallbackReqVo();
            vo.setEventType("APPROVE");
            assertEquals("APPROVE", vo.getEventType());
        }

        @Test
        @DisplayName("should set and get inherited businessPartyMark")
        void businessPartyMark() {
            ProcSubmitCallbackReqVo vo = new ProcSubmitCallbackReqVo();
            vo.setBusinessPartyMark("PARTY01");
            assertEquals("PARTY01", vo.getBusinessPartyMark());
        }

        @Test
        @DisplayName("should set and get inherited formCode")
        void formCode() {
            ProcSubmitCallbackReqVo vo = new ProcSubmitCallbackReqVo();
            vo.setFormCode("FORM001");
            assertEquals("FORM001", vo.getFormCode());
        }

        @Test
        @DisplayName("should set and get inherited processNum")
        void processNum() {
            ProcSubmitCallbackReqVo vo = new ProcSubmitCallbackReqVo();
            vo.setProcessNum("PROC-001");
            assertEquals("PROC-001", vo.getProcessNum());
        }

        @Test
        @DisplayName("should set and get inherited businessId")
        void businessId() {
            ProcSubmitCallbackReqVo vo = new ProcSubmitCallbackReqVo();
            vo.setBusinessId("BIZ-001");
            assertEquals("BIZ-001", vo.getBusinessId());
        }
    }

    @Nested
    @DisplayName("combined usage")
    class CombinedUsageTest {
        @Test
        @DisplayName("should set both formData and inherited fields")
        void allFields() {
            ProcSubmitCallbackReqVo vo = new ProcSubmitCallbackReqVo("{\"data\":1}");
            vo.setEventType("SUBMIT");
            vo.setFormCode("FORM002");
            vo.setBusinessPartyMark("MARK");
            assertEquals("{\"data\":1}", vo.getFormData());
            assertEquals("SUBMIT", vo.getEventType());
            assertEquals("FORM002", vo.getFormCode());
            assertEquals("MARK", vo.getBusinessPartyMark());
        }
    }
}
