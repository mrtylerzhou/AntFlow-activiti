package org.openoa.base.entity.jsonconf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class JsonConfUtilTest extends BaseTest {

    @Nested
    @DisplayName("toJsonString")
    class ToJsonStringTest {
        @Test
        @DisplayName("should serialize object without @type")
        void shouldSerializeWithoutTypeName() {
            BpmnNodeConfigJson config = BpmnNodeConfigJson.builder().build();
            String json = JsonConfUtil.toJsonString(config);
            assertNotNull(json);
            assertFalse(json.contains("@type"), "JSON should not contain @type field after removing WriteClassName");
        }

        @Test
        @DisplayName("should return null for null input")
        void shouldReturnNullForNull() {
            assertNull(JsonConfUtil.toJsonString(null));
        }

        @Test
        @DisplayName("should serialize with nested objects")
        void shouldSerializeWithNestedObjects() {
            BpmnNodeApproverConfJson approverConf = BpmnNodeApproverConfJson.builder().build();
            BpmnNodeConfigJson config = BpmnNodeConfigJson.builder()
                    .approverConf(approverConf)
                    .build();
            String json = JsonConfUtil.toJsonString(config);
            assertNotNull(json);
            assertTrue(json.contains("approverConf"));
        }
    }

    @Nested
    @DisplayName("parseObject")
    class ParseObjectTest {
        @Test
        @DisplayName("should deserialize valid JSON")
        void shouldDeserializeValidJson() {
            String json = "{}";
            BpmnNodeConfigJson result = JsonConfUtil.parseObject(json, BpmnNodeConfigJson.class);
            assertNotNull(result);
        }

        @Test
        @DisplayName("should return null for null input")
        void shouldReturnNullForNull() {
            assertNull(JsonConfUtil.parseObject(null, BpmnNodeConfigJson.class));
        }

        @Test
        @DisplayName("should return null for empty string")
        void shouldReturnNullForEmpty() {
            assertNull(JsonConfUtil.parseObject("", BpmnNodeConfigJson.class));
        }

        @Test
        @DisplayName("should deserialize with nested fields")
        void shouldDeserializeWithNestedFields() {
            String json = "{\"approverConf\":{}}";
            BpmnNodeConfigJson result = JsonConfUtil.parseObject(json, BpmnNodeConfigJson.class);
            assertNotNull(result);
            assertNotNull(result.getApproverConf());
        }
    }

    @Nested
    @DisplayName("parseNodeConfig")
    class ParseNodeConfigTest {
        @Test
        @DisplayName("should parse node config JSON")
        void shouldParseNodeConfig() {
            String json = "{\"approverConf\":{},\"conditionsConf\":{}}";
            BpmnNodeConfigJson result = JsonConfUtil.parseNodeConfig(json);
            assertNotNull(result);
            assertNotNull(result.getApproverConf());
            assertNotNull(result.getConditionsConf());
        }

        @Test
        @DisplayName("should return null for null")
        void shouldReturnNullForNull() {
            assertNull(JsonConfUtil.parseNodeConfig(null));
        }
    }

    @Nested
    @DisplayName("parseConfConfig")
    class ParseConfConfigTest {
        @Test
        @DisplayName("should parse conf config JSON")
        void shouldParseConfConfig() {
            String json = "{}";
            BpmnConfConfigJson result = JsonConfUtil.parseConfConfig(json);
            assertNotNull(result);
        }

        @Test
        @DisplayName("should return null for null")
        void shouldReturnNullForNull() {
            assertNull(JsonConfUtil.parseConfConfig(null));
        }
    }

    @Nested
    @DisplayName("round-trip serialization")
    class RoundTripTest {
        @Test
        @DisplayName("should survive serialize-deserialize round trip")
        void shouldSurviveRoundTrip() {
            BpmnNodeConfigJson original = BpmnNodeConfigJson.builder().build();
            String json = JsonConfUtil.toNodeConfigJson(original);
            BpmnNodeConfigJson restored = JsonConfUtil.parseNodeConfig(json);
            assertNotNull(restored);
        }

        @Test
        @DisplayName("should preserve nested approverConf in round trip")
        void shouldPreserveNestedInRoundTrip() {
            BpmnNodeApproverConfJson approverConf = BpmnNodeApproverConfJson.builder().build();
            BpmnNodeConfigJson original = BpmnNodeConfigJson.builder()
                    .approverConf(approverConf)
                    .build();
            String json = JsonConfUtil.toNodeConfigJson(original);
            BpmnNodeConfigJson restored = JsonConfUtil.parseNodeConfig(json);
            assertNotNull(restored);
            assertNotNull(restored.getApproverConf());
        }
    }
}
