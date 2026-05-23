package org.openoa.base.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SimpleRuntimeActivityDefinitionEntityTest extends BaseTest {

    @Nested
    @DisplayName("serializeProperties")
    class SerializePropertiesTest {
        @Test
        @DisplayName("should serialize properties map to JSON string")
        void shouldSerializeToJson() throws IOException {
            SimpleRuntimeActivityDefinitionEntity entity = new SimpleRuntimeActivityDefinitionEntity();
            entity.setProperty("assignees", "user1,user2");
            entity.setProperty("sequential", true);
            entity.serializeProperties();
            String text = entity.getPropertiesText();
            assertNotNull(text);
            assertTrue(text.contains("assignees"));
            assertTrue(text.contains("user1,user2"));
            assertTrue(text.contains("sequential"));
        }

        @Test
        @DisplayName("should serialize empty properties")
        void shouldSerializeEmpty() throws IOException {
            SimpleRuntimeActivityDefinitionEntity entity = new SimpleRuntimeActivityDefinitionEntity();
            entity.serializeProperties();
            assertNotNull(entity.getPropertiesText());
        }
    }

    @Nested
    @DisplayName("deserializeProperties")
    class DeserializePropertiesTest {
        @Test
        @DisplayName("should deserialize JSON string to properties map")
        void shouldDeserializeFromJson() throws IOException {
            SimpleRuntimeActivityDefinitionEntity entity = new SimpleRuntimeActivityDefinitionEntity();
            entity.setPropertiesText("{\"assignees\":\"user1,user2\",\"sequential\":true}");
            entity.deserializeProperties();
            assertEquals("user1,user2", entity.getProperty("assignees"));
            assertEquals(true, entity.getProperty("sequential"));
        }

        @Test
        @DisplayName("should throw for invalid JSON")
        void shouldThrowForInvalidJson() {
            SimpleRuntimeActivityDefinitionEntity entity = new SimpleRuntimeActivityDefinitionEntity();
            entity.setPropertiesText("not valid json");
            assertThrows(IOException.class, entity::deserializeProperties);
        }
    }

    @Nested
    @DisplayName("round-trip serialization")
    class RoundTripTest {
        @Test
        @DisplayName("should survive serialize-deserialize round trip")
        void shouldSurviveRoundTrip() throws IOException {
            SimpleRuntimeActivityDefinitionEntity entity = new SimpleRuntimeActivityDefinitionEntity();
            entity.setProperty("key1", "value1");
            entity.setProperty("key2", 42);
            entity.serializeProperties();

            SimpleRuntimeActivityDefinitionEntity restored = new SimpleRuntimeActivityDefinitionEntity();
            restored.setPropertiesText(entity.getPropertiesText());
            restored.deserializeProperties();

            assertEquals("value1", restored.getProperty("key1"));
            assertEquals(Integer.valueOf(42), restored.getProperty("key2"));
        }
    }

    @Nested
    @DisplayName("getProperty and setProperty")
    class PropertyAccessTest {
        @Test
        @DisplayName("should get and set properties")
        void shouldGetAndSet() {
            SimpleRuntimeActivityDefinitionEntity entity = new SimpleRuntimeActivityDefinitionEntity();
            entity.setProperty("name", "test");
            assertEquals("test", entity.getProperty("name"));
        }

        @Test
        @DisplayName("should return null for non-existent property")
        void shouldReturnNullForNonExistent() {
            SimpleRuntimeActivityDefinitionEntity entity = new SimpleRuntimeActivityDefinitionEntity();
            assertNull(entity.getProperty("nonexistent"));
        }

        @Test
        @DisplayName("should overwrite existing property")
        void shouldOverwrite() {
            SimpleRuntimeActivityDefinitionEntity entity = new SimpleRuntimeActivityDefinitionEntity();
            entity.setProperty("key", "old");
            entity.setProperty("key", "new");
            assertEquals("new", entity.getProperty("key"));
        }
    }

    @Nested
    @DisplayName("basic setters and getters")
    class BasicSettersGettersTest {
        @Test
        @DisplayName("should set and get factoryName")
        void factoryName() {
            SimpleRuntimeActivityDefinitionEntity entity = new SimpleRuntimeActivityDefinitionEntity();
            entity.setFactoryName("testFactory");
            assertEquals("testFactory", entity.getFactoryName());
        }

        @Test
        @DisplayName("should set and get processDefinitionId")
        void processDefinitionId() {
            SimpleRuntimeActivityDefinitionEntity entity = new SimpleRuntimeActivityDefinitionEntity();
            entity.setProcessDefinitionId("proc:1:1");
            assertEquals("proc:1:1", entity.getProcessDefinitionId());
        }

        @Test
        @DisplayName("should set and get processInstanceId")
        void processInstanceId() {
            SimpleRuntimeActivityDefinitionEntity entity = new SimpleRuntimeActivityDefinitionEntity();
            entity.setProcessInstanceId("inst001");
            assertEquals("inst001", entity.getProcessInstanceId());
        }
    }
}
