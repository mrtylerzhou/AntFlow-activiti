package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

class ActByteArrayUtilTest extends BaseTest {

    @Nested
    @DisplayName("deserializeByteArray")
    class DeserializeByteArrayTest {
        @Test
        @DisplayName("should deserialize serialized object")
        void shouldDeserializeSerializedObject() throws Exception {
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);
            oos.writeObject("hello world");
            oos.close();
            byte[] bytes = bos.toByteArray();

            Object result = ActByteArrayUtil.deserializeByteArray(bytes);
            assertEquals("hello world", result);
        }

        @Test
        @DisplayName("should deserialize Integer")
        void shouldDeserializeInteger() throws Exception {
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);
            oos.writeObject(42);
            oos.close();
            byte[] bytes = bos.toByteArray();

            Object result = ActByteArrayUtil.deserializeByteArray(bytes);
            assertEquals(42, result);
        }

        @Test
        @DisplayName("should return null for invalid bytes")
        void shouldReturnNullForInvalidBytes() {
            byte[] invalid = {1, 2, 3, 4, 5};
            Object result = ActByteArrayUtil.deserializeByteArray(invalid);
            assertNull(result);
        }

        @Test
        @DisplayName("should deserialize custom serializable object")
        void shouldDeserializeCustomObject() throws Exception {
            TestSerializable original = new TestSerializable("test", 123);
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);
            oos.writeObject(original);
            oos.close();
            byte[] bytes = bos.toByteArray();

            Object result = ActByteArrayUtil.deserializeByteArray(bytes);
            assertTrue(result instanceof TestSerializable);
            TestSerializable restored = (TestSerializable) result;
            assertEquals("test", restored.name);
            assertEquals(123, restored.value);
        }
    }

    static class TestSerializable implements Serializable {
        private static final long serialVersionUID = 1L;
        String name;
        int value;

        TestSerializable(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}
