package org.openoa.engine.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest extends BaseTest {

    @Nested
    @DisplayName("transfer2JsonString")
    class Transfer2JsonStringTest {
        @Test
        @DisplayName("should convert object to json string")
        void shouldConvertObjectToJson() {
            TestObject obj = new TestObject();
            obj.setName("test");
            obj.setValue(123);

            String json = JsonUtils.transfer2JsonString(obj);

            assertNotNull(json);
            assertTrue(json.contains("\"name\":\"test\""));
            assertTrue(json.contains("\"value\":123"));
        }

        @Test
        @DisplayName("should exclude null fields")
        void shouldExcludeNullFields() {
            TestObject obj = new TestObject();
            obj.setName("test");
            obj.setValue(null);

            String json = JsonUtils.transfer2JsonString(obj);

            assertNotNull(json);
            assertTrue(json.contains("\"name\":\"test\""));
            assertFalse(json.contains("value"));
        }

        @Test
        @DisplayName("should handle empty object")
        void shouldHandleEmptyObject() {
            TestObject obj = new TestObject();

            String json = JsonUtils.transfer2JsonString(obj);

            assertNotNull(json);
            assertEquals("{}", json.trim());
        }
    }

    @Nested
    @DisplayName("parseRaw")
    class ParseRawTest {
        @Test
        @DisplayName("should parse json string to map")
        void shouldParseJsonToMap() {
            String json = "{\"name\":\"test\",\"value\":123}";

            Map<String, Object> result = JsonUtils.parseRaw(json);

            assertNotNull(result);
            assertEquals("test", result.get("name"));
            assertEquals(123, result.get("value"));
        }

        @Test
        @DisplayName("should return null for invalid json")
        void shouldReturnNullForInvalidJson() {
            String invalidJson = "not a json";

            Map<String, Object> result = JsonUtils.parseRaw(invalidJson);

            assertNull(result);
        }

        @Test
        @DisplayName("should handle empty json object")
        void shouldHandleEmptyJsonObject() {
            String json = "{}";

            Map<String, Object> result = JsonUtils.parseRaw(json);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should handle nested json")
        void shouldHandleNestedJson() {
            String json = "{\"outer\":{\"inner\":\"value\"}}";

            Map<String, Object> result = JsonUtils.parseRaw(json);

            assertNotNull(result);
            assertTrue(result.get("outer") instanceof Map);
        }
    }

    static class TestObject {
        private String name;
        private Integer value;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }
    }
}
