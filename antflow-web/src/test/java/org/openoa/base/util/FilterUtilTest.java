package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FilterUtilTest extends BaseTest {

    @Nested
    @DisplayName("distinctByKeys with single key")
    class DistinctByKeysSingleKeyTest {
        @Test
        @DisplayName("should keep first occurrence and filter duplicates")
        void shouldKeepFirstOccurrence() {
            List<String> list = Arrays.asList("a", "b", "a", "c", "b");
            List<String> result = list.stream()
                    .filter(FilterUtil.distinctByKeys(String::toString))
                    .collect(Collectors.toList());
            assertEquals(3, result.size());
            assertEquals(Arrays.asList("a", "b", "c"), result);
        }

        @Test
        @DisplayName("should handle empty list")
        void shouldHandleEmptyList() {
            List<String> list = Arrays.asList();
            List<String> result = list.stream()
                    .filter(FilterUtil.distinctByKeys(String::toString))
                    .collect(Collectors.toList());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should handle all unique elements")
        void shouldHandleAllUnique() {
            List<String> list = Arrays.asList("a", "b", "c");
            List<String> result = list.stream()
                    .filter(FilterUtil.distinctByKeys(String::toString))
                    .collect(Collectors.toList());
            assertEquals(3, result.size());
        }
    }

    @Nested
    @DisplayName("distinctByKeys with multiple keys")
    class DistinctByKeysMultiKeyTest {
        @Test
        @DisplayName("should deduplicate by multiple key extractors")
        void shouldDeduplicateByMultipleKeys() {
            List<Item> items = Arrays.asList(
                    new Item("a", 1),
                    new Item("a", 2),
                    new Item("a", 1),
                    new Item("b", 1)
            );
            List<Item> result = items.stream()
                    .filter(FilterUtil.distinctByKeys(Item::getName, Item::getValue))
                    .collect(Collectors.toList());
            assertEquals(3, result.size());
        }
    }

    @Nested
    @DisplayName("sameByKeys")
    class SameByKeysTest {
        @Test
        @DisplayName("should return true for duplicates (second occurrence)")
        void shouldReturnTrueForDuplicates() {
            List<String> list = Arrays.asList("a", "b", "a", "c");
            List<String> duplicates = list.stream()
                    .filter(FilterUtil.sameByKeys(String::toString))
                    .collect(Collectors.toList());
            assertEquals(1, duplicates.size());
            assertEquals("a", duplicates.get(0));
        }

        @Test
        @DisplayName("should return empty for all unique elements")
        void shouldReturnEmptyForAllUnique() {
            List<String> list = Arrays.asList("a", "b", "c");
            List<String> duplicates = list.stream()
                    .filter(FilterUtil.sameByKeys(String::toString))
                    .collect(Collectors.toList());
            assertTrue(duplicates.isEmpty());
        }

        @Test
        @DisplayName("should handle multiple duplicates with sameByKeys")
        void shouldHandleMultipleDuplicates() {
            List<String> list = Arrays.asList("a", "b", "a", "b", "c");
            List<String> duplicates = list.stream()
                    .filter(FilterUtil.sameByKeys(String::toString))
                    .collect(Collectors.toList());
            assertEquals(2, duplicates.size());
        }
    }

    class Item {
        private final String name;
        private final int value;

        Item(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() { return name; }
        public int getValue() { return value; }
    }
}
