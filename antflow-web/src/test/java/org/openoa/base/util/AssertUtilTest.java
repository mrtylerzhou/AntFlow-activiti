package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.exception.AFBizException;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AssertUtilTest extends BaseTest {

    @Nested
    @DisplayName("throwsIfEmpty")
    class ThrowsIfEmptyTest {
        @Test
        @DisplayName("should not throw for non-empty object")
        void shouldNotThrowForNonEmpty() {
            assertDoesNotThrow(() -> AssertUtil.throwsIfEmpty("hello", "error", Collections.emptyList()));
        }

        @Test
        @DisplayName("should throw for null object")
        void shouldThrowForNull() {
            assertThrows(AFBizException.class,
                    () -> AssertUtil.throwsIfEmpty(null, "对象不存在", Collections.emptyList()));
        }

        @Test
        @DisplayName("should throw for empty string")
        void shouldThrowForEmptyString() {
            assertThrows(AFBizException.class,
                    () -> AssertUtil.throwsIfEmpty("", "对象不存在", Collections.emptyList()));
        }

        @Test
        @DisplayName("should throw for empty list")
        void shouldThrowForEmptyList() {
            assertThrows(AFBizException.class,
                    () -> AssertUtil.throwsIfEmpty(Collections.emptyList(), "对象不存在", Collections.emptyList()));
        }

        @Test
        @DisplayName("should use default message when errorMsg is null")
        void shouldUseDefaultMessageWhenNull() {
            AFBizException ex = assertThrows(AFBizException.class,
                    () -> AssertUtil.throwsIfEmpty(null, null, Collections.emptyList()));
            assertTrue(ex.getMessage().contains("对象不存在"));
        }

        @Test
        @DisplayName("should use default message when errorMsg is empty")
        void shouldUseDefaultMessageWhenEmpty() {
            AFBizException ex = assertThrows(AFBizException.class,
                    () -> AssertUtil.throwsIfEmpty(null, "", Collections.emptyList()));
            assertTrue(ex.getMessage().contains("对象不存在"));
        }

        @Test
        @DisplayName("should format params into error message")
        void shouldFormatParamsIntoMessage() {
            AFBizException ex = assertThrows(AFBizException.class,
                    () -> AssertUtil.throwsIfEmpty(null, "not found", Arrays.asList("id=123", "name=test")));
            String msg = ex.getMessage();
            assertTrue(msg.contains("id=123"));
            assertTrue(msg.contains("name=test"));
        }

        @Test
        @DisplayName("should not throw for non-empty list")
        void shouldNotThrowForNonEmptyList() {
            assertDoesNotThrow(() -> AssertUtil.throwsIfEmpty(Arrays.asList(1), "error", Collections.emptyList()));
        }
    }
}
