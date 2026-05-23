package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.BaseNumIdStruVo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AntCollectionUtilTest extends BaseTest {

    @Nested
    @DisplayName("numberToStringList")
    class NumberToStringListTest {
        @Test
        @DisplayName("should convert integers to strings")
        void shouldConvertIntegers() {
            Collection<String> result = AntCollectionUtil.numberToStringList(Arrays.asList(1, 2, 3));
            assertEquals(3, result.size());
            assertTrue(result.contains("1"));
            assertTrue(result.contains("2"));
            assertTrue(result.contains("3"));
        }

        @Test
        @DisplayName("should convert longs to strings")
        void shouldConvertLongs() {
            Collection<String> result = AntCollectionUtil.numberToStringList(Arrays.asList(100L, 200L));
            assertEquals(2, result.size());
            assertTrue(result.contains("100"));
            assertTrue(result.contains("200"));
        }

        @Test
        @DisplayName("should handle empty collection")
        void shouldHandleEmpty() {
            Collection<String> result = AntCollectionUtil.numberToStringList(Collections.emptyList());
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("serializeToStringCollection")
    class SerializeToStringCollectionTest {
        @Test
        @DisplayName("should convert mixed types to strings")
        void shouldConvertMixedTypes() {
            Collection<String> result = AntCollectionUtil.serializeToStringCollection(Arrays.asList(1, "hello", 2.5));
            assertEquals(3, result.size());
            assertTrue(result.contains("1"));
            assertTrue(result.contains("hello"));
            assertTrue(result.contains("2.5"));
        }

        @Test
        @DisplayName("should handle empty collection")
        void shouldHandleEmpty() {
            Collection<String> result = AntCollectionUtil.serializeToStringCollection(Collections.emptyList());
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("StringToIntList")
    class StringToIntListTest {
        @Test
        @DisplayName("should convert strings to integers")
        void shouldConvertStrings() {
            Collection<Integer> result = AntCollectionUtil.StringToIntList(Arrays.asList("1", "2", "3"));
            assertEquals(3, result.size());
            assertTrue(result.contains(1));
            assertTrue(result.contains(2));
            assertTrue(result.contains(3));
        }

        @Test
        @DisplayName("should handle empty collection")
        void shouldHandleEmpty() {
            Collection<Integer> result = AntCollectionUtil.StringToIntList(Collections.emptyList());
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("StringToLongList")
    class StringToLongListTest {
        @Test
        @DisplayName("should convert strings to longs")
        void shouldConvertStrings() {
            Collection<Long> result = AntCollectionUtil.StringToLongList(Arrays.asList("100", "200"));
            assertEquals(2, result.size());
            assertTrue(result.contains(100L));
            assertTrue(result.contains(200L));
        }
    }

    @Nested
    @DisplayName("IntToLongList")
    class IntToLongListTest {
        @Test
        @DisplayName("should convert integers to longs")
        void shouldConvertInts() {
            Collection<Long> result = AntCollectionUtil.IntToLongList(Arrays.asList(1, 2, 3));
            assertEquals(3, result.size());
            assertTrue(result.contains(1L));
            assertTrue(result.contains(2L));
            assertTrue(result.contains(3L));
        }
    }

    @Nested
    @DisplayName("LongToIntList")
    class LongToIntListTest {
        @Test
        @DisplayName("should convert longs to integers")
        void shouldConvertLongs() {
            Collection<Integer> result = AntCollectionUtil.LongToIntList(Arrays.asList(10L, 20L));
            assertEquals(2, result.size());
            assertTrue(result.contains(10));
            assertTrue(result.contains(20));
        }
    }

    @Nested
    @DisplayName("joinBaseNumIdTransVoToString")
    class JoinBaseNumIdTransVoToStringTest {
        @Test
        @DisplayName("should join IDs with comma")
        void shouldJoinIdsWithComma() {
            BaseNumIdStruVo vo1 = BaseNumIdStruVo.builder().id(1L).name("a").build();
            BaseNumIdStruVo vo2 = BaseNumIdStruVo.builder().id(2L).name("b").build();
            String result = AntCollectionUtil.joinBaseNumIdTransVoToString(Arrays.asList(vo1, vo2));
            assertEquals("1,2", result);
        }

        @Test
        @DisplayName("should return empty string for null collection")
        void shouldReturnEmptyForNull() {
            assertEquals("", AntCollectionUtil.joinBaseNumIdTransVoToString(null));
        }

        @Test
        @DisplayName("should return empty string for empty collection")
        void shouldReturnEmptyForEmpty() {
            assertEquals("", AntCollectionUtil.joinBaseNumIdTransVoToString(Collections.emptyList()));
        }

        @Test
        @DisplayName("should handle single element")
        void shouldHandleSingleElement() {
            BaseNumIdStruVo vo = BaseNumIdStruVo.builder().id(42L).name("x").build();
            String result = AntCollectionUtil.joinBaseNumIdTransVoToString(Arrays.asList(vo));
            assertEquals("42", result);
        }
    }
}
