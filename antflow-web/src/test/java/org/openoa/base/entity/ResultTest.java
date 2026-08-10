package org.openoa.base.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest extends BaseTest {

    @Nested
    @DisplayName("newSuccessResult")
    class NewSuccessResultTest {
        @Test
        @DisplayName("should create success result with data")
        void shouldCreateSuccessWithData() {
            Result<String> result = Result.newSuccessResult("hello");
            assertTrue(result.isSuccess());
            assertEquals("hello", result.getData());
            assertEquals(200, result.getCode());
        }

        @Test
        @DisplayName("should create success result with null data")
        void shouldCreateSuccessWithNull() {
            Result<Void> result = Result.newSuccessResult(null);
            assertTrue(result.isSuccess());
            assertNull(result.getData());
            assertEquals(200, result.getCode());
        }

        @Test
        @DisplayName("should create success result with integer data")
        void shouldCreateSuccessWithInteger() {
            Result<Integer> result = Result.newSuccessResult(42);
            assertTrue(result.isSuccess());
            assertEquals(42, result.getData());
        }
    }

    @Nested
    @DisplayName("success")
    class SuccessTest {
        @Test
        @DisplayName("should create success result without data")
        void shouldCreateSuccessWithoutData() {
            Result result = Result.success();
            assertTrue(result.isSuccess());
            assertNull(result.getData());
        }
    }

    @Nested
    @DisplayName("newFailureResult")
    class NewFailureResultTest {
        @Test
        @DisplayName("should create failure result with CommonError")
        void shouldCreateWithCommonError() {
            Result result = Result.newFailureResult(CommonError.UN_KNOWN_ERROR, false, new RuntimeException("test"));
            assertFalse(result.isSuccess());
            assertEquals("UN_KNOWN_ERROR", result.getErrCode());
            assertEquals("未知错误", result.getErrMsg());
            assertFalse(result.isNeedRetry());
        }

        @Test
        @DisplayName("should create failure result with needRetry=true")
        void shouldCreateWithRetry() {
            Result result = Result.newFailureResult(CommonError.CONCURRENT_FAILURE, true, new RuntimeException("retry"));
            assertTrue(result.isNeedRetry());
        }

        @Test
        @DisplayName("should create failure result with errCode and errMsg")
        void shouldCreateWithCodeAndMsg() {
            Result result = Result.newFailureResult("ERR001", "something went wrong");
            assertFalse(result.isSuccess());
            assertEquals("ERR001", result.getErrCode());
            assertEquals("something went wrong", result.getErrMsg());
            assertFalse(result.isNeedRetry());
        }

        @Test
        @DisplayName("should create failure result with all parameters")
        void shouldCreateWithAllParams() {
            Result result = Result.newFailureResult("ERR002", "retryable error", true, new RuntimeException("retry"));
            assertFalse(result.isSuccess());
            assertEquals("ERR002", result.getErrCode());
            assertEquals("retryable error", result.getErrMsg());
            assertTrue(result.isNeedRetry());
            assertNotNull(result.getExpInfo());
        }
    }

    @Nested
    @DisplayName("setters and getters")
    class SettersGettersTest {
        @Test
        @DisplayName("should set and get data")
        void shouldSetAndGetData() {
            Result<String> result = new Result<>();
            result.setData("test");
            assertEquals("test", result.getData());
        }

        @Test
        @DisplayName("should set and get code")
        void shouldSetAndGetCode() {
            Result result = new Result<>();
            result.setCode(404);
            assertEquals(404, result.getCode());
        }

        @Test
        @DisplayName("should set and get requestId")
        void shouldSetAndGetRequestId() {
            Result result = new Result<>();
            result.setRequestId("req-123");
            assertEquals("req-123", result.getRequestId());
        }
    }
}
