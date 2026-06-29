package org.openoa.base.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class OperationRespTest extends BaseTest {

    @Nested
    @DisplayName("get")
    class GetTest {
        @Test
        @DisplayName("should return SUCCESS for code 00000")
        void shouldReturnSuccess() {
            OperationResp resp = OperationResp.get("00000");
            assertEquals(OperationResp.SUCCESS, resp);
        }

        @Test
        @DisplayName("should return FAILURE for code 20000")
        void shouldReturnFailure() {
            OperationResp resp = OperationResp.get("20000");
            assertEquals(OperationResp.FAILURE, resp);
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(OperationResp.get("99999"));
        }

        @Test
        @DisplayName("should return null for null code")
        void shouldReturnNullForNull() {
            assertNull(OperationResp.get(null));
        }

        @Test
        @DisplayName("SUCCESS and RETURN_SUCCESS share same code")
        void successAndReturnShareCode() {
            assertEquals(OperationResp.SUCCESS.getCode(), OperationResp.RETURN_SUCCESS.getCode());
            assertNotEquals(OperationResp.SUCCESS.getDesc(), OperationResp.RETURN_SUCCESS.getDesc());
        }

        @Test
        @DisplayName("should return PARAM_ERROR for code 20014")
        void shouldReturnParamError() {
            assertEquals(OperationResp.PARAM_ERROR, OperationResp.get("20014"));
        }

        @Test
        @DisplayName("should return PROC_FAIL for code 409")
        void shouldReturnProcFail() {
            assertEquals(OperationResp.PROC_FAIL, OperationResp.get("409"));
        }

        @Test
        @DisplayName("should return FORBIDDEN_ERROR for code 403")
        void shouldReturnForbidden() {
            assertEquals(OperationResp.FORBIDDEN_ERROR, OperationResp.get("403"));
        }
    }

    @Nested
    @DisplayName("properties")
    class PropertiesTest {
        @Test
        @DisplayName("SUCCESS should have code 00000")
        void successCode() {
            assertEquals("00000", OperationResp.SUCCESS.getCode());
            assertEquals("成功", OperationResp.SUCCESS.getDesc());
        }

        @Test
        @DisplayName("NO_SERVICE should have code 20003")
        void noServiceCode() {
            assertEquals("20003", OperationResp.NO_SERVICE.getCode());
        }

        @Test
        @DisplayName("SYSTEM_ERROR should have code 20004")
        void systemErrorCode() {
            assertEquals("20004", OperationResp.SYSTEM_ERROR.getCode());
        }
    }
}
