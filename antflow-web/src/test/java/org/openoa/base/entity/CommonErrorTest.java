package org.openoa.base.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class CommonErrorTest extends BaseTest {

    @Nested
    @DisplayName("CommonError enum values")
    class CommonErrorValuesTest {
        @Test
        @DisplayName("UN_KNOWN_ERROR should have desc '未知错误'")
        void unknownError() {
            assertEquals("未知错误", CommonError.UN_KNOWN_ERROR.getMsg());
        }

        @Test
        @DisplayName("should have multiple error values")
        void shouldHaveMultipleValues() {
            assertTrue(CommonError.values().length > 0);
        }
    }

    @Nested
    @DisplayName("Result with CommonError")
    class ResultWithCommonErrorTest {
        @Test
        @DisplayName("should create failure result with UN_KNOWN_ERROR")
        void shouldCreateWithUnknownError() {
            Result result = Result.newFailureResult(CommonError.UN_KNOWN_ERROR, false, null);
            assertFalse(result.isSuccess());
            assertNotNull(result.getErrCode());
            assertEquals("未知错误", result.getErrMsg());
        }

        @Test
        @DisplayName("should create failure result with CONCURRENT_FAILURE and retry")
        void shouldCreateWithConcurrentFailure() {
            Result result = Result.newFailureResult(CommonError.CONCURRENT_FAILURE, true, new RuntimeException("concurrent"));
            assertTrue(result.isNeedRetry());
        }
    }
}
