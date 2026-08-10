package org.openoa.engine.bpmnconf.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProcessConstantsTest extends BaseTest {

    private ProcessConstants processConstants;

    @BeforeEach
    void setUp() {
        processConstants = new ProcessConstants();
    }

    @Nested
    @DisplayName("getMapValue")
    class GetMapValueTest {
        @Test
        @DisplayName("should return value when key exists in map")
        void shouldReturnValueWhenKeyExists() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "testValue");

            String result = processConstants.getMapValue(map, "name");

            assertEquals("testValue", result);
        }

        @Test
        @DisplayName("should return empty string when key does not exist in map")
        void shouldReturnEmptyWhenKeyNotExists() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "testValue");

            String result = processConstants.getMapValue(map, "nonExistentKey");

            assertEquals("", result);
        }

        @Test
        @DisplayName("should return empty string when value is null")
        void shouldReturnEmptyWhenValueIsNull() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", null);

            String result = processConstants.getMapValue(map, "name");

            assertEquals("", result);
        }

        @Test
        @DisplayName("should return empty string when map is null")
        void shouldReturnEmptyWhenMapIsNull() {
            String result = processConstants.getMapValue(null, "name");

            assertEquals("", result);
        }

        @Test
        @DisplayName("should convert non-string value to string")
        void shouldConvertNonStringValueToString() {
            Map<String, Object> map = new HashMap<>();
            map.put("count", 42);

            String result = processConstants.getMapValue(map, "count");

            assertEquals("42", result);
        }

        @Test
        @DisplayName("should return empty string for empty map")
        void shouldReturnEmptyForEmptyMap() {
            Map<String, Object> map = new HashMap<>();

            String result = processConstants.getMapValue(map, "anyKey");

            assertEquals("", result);
        }

        @Test
        @DisplayName("should handle boolean value")
        void shouldHandleBooleanValue() {
            Map<String, Object> map = new HashMap<>();
            map.put("flag", true);

            String result = processConstants.getMapValue(map, "flag");

            assertEquals("true", result);
        }

        @Test
        @DisplayName("should handle empty string value")
        void shouldHandleEmptyStringValue() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "");

            String result = processConstants.getMapValue(map, "name");

            assertEquals("", result);
        }
    }
}
