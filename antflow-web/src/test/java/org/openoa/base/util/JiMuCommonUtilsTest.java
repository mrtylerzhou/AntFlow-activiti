package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JiMuCommonUtilsTest extends BaseTest {

    @Nested
    @DisplayName("listToString")
    class ListToStringTest {
        @Test
        @DisplayName("should sort and join with comma")
        void shouldSortAndJoin() {
            List<String> list = Arrays.asList("banana", "apple", "cherry");
            String result = JiMuCommonUtils.listToString(list);
            assertEquals("apple,banana,cherry,", result);
        }

        @Test
        @DisplayName("should return null for null list")
        void shouldReturnNullForNull() {
            assertNull(JiMuCommonUtils.listToString(null));
        }

        @Test
        @DisplayName("should return null for empty list")
        void shouldReturnNullForEmpty() {
            assertNull(JiMuCommonUtils.listToString(Collections.emptyList()));
        }

        @Test
        @DisplayName("should handle single element")
        void shouldHandleSingleElement() {
            String result = JiMuCommonUtils.listToString(Arrays.asList("hello"));
            assertEquals("hello,", result);
        }
    }

    @Nested
    @DisplayName("strToList")
    class StrToListTest {
        @Test
        @DisplayName("should split comma-separated string")
        void shouldSplitCommaString() {
            List<String> result = JiMuCommonUtils.strToList("a,b,c");
            assertEquals(3, result.size());
            assertEquals("a", result.get(0));
            assertEquals("b", result.get(1));
            assertEquals("c", result.get(2));
        }

        @Test
        @DisplayName("should filter blank items")
        void shouldFilterBlankItems() {
            List<String> result = JiMuCommonUtils.strToList("a,,b, ,c");
            assertEquals(3, result.size());
        }

        @Test
        @DisplayName("should return null for null input")
        void shouldReturnNullForNull() {
            assertNull(JiMuCommonUtils.strToList(null));
        }

        @Test
        @DisplayName("should return null for blank input")
        void shouldReturnNullForBlank() {
            assertNull(JiMuCommonUtils.strToList(""));
            assertNull(JiMuCommonUtils.strToList("  "));
        }

        @Test
        @DisplayName("should handle single item")
        void shouldHandleSingleItem() {
            List<String> result = JiMuCommonUtils.strToList("hello");
            assertEquals(1, result.size());
            assertEquals("hello", result.get(0));
        }
    }

    @Nested
    @DisplayName("exceptionToString")
    class ExceptionToStringTest {
        @Test
        @DisplayName("should convert exception to string")
        void shouldConvertException() {
            RuntimeException ex = new RuntimeException("test error");
            String result = JiMuCommonUtils.exceptionToString(ex);
            assertNotNull(result);
            assertTrue(result.contains("test error"));
            assertTrue(result.contains("RuntimeException"));
        }

        @Test
        @DisplayName("should return empty string for null")
        void shouldReturnEmptyForNull() {
            assertEquals("", JiMuCommonUtils.exceptionToString(null));
        }

        @Test
        @DisplayName("should include stack trace")
        void shouldIncludeStackTrace() {
            Exception ex = new Exception("stack test");
            String result = JiMuCommonUtils.exceptionToString(ex);
            assertTrue(result.contains("at "));
        }
    }
}
