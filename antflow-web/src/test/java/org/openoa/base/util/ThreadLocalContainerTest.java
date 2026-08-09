package org.openoa.base.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ThreadLocalContainerTest extends BaseTest {

    @AfterEach
    void cleanup() {
        ThreadLocalContainer.clean();
    }

    @Nested
    @DisplayName("set and get")
    class SetAndGetTest {
        @Test
        @DisplayName("should store and retrieve value")
        void storeAndRetrieve() {
            ThreadLocalContainer.set("key1", "value1");
            assertEquals("value1", ThreadLocalContainer.get("key1"));
        }

        @Test
        @DisplayName("should return null for missing key")
        void missingKey() {
            assertNull(ThreadLocalContainer.get("nonexistent"));
        }

        @Test
        @DisplayName("should overwrite existing key")
        void overwrite() {
            ThreadLocalContainer.set("key2", "old");
            ThreadLocalContainer.set("key2", "new");
            assertEquals("new", ThreadLocalContainer.get("key2"));
        }

        @Test
        @DisplayName("should store different types")
        void differentTypes() {
            ThreadLocalContainer.set("string", "hello");
            ThreadLocalContainer.set("integer", 42);
            ThreadLocalContainer.set("bool", true);
            assertEquals("hello", ThreadLocalContainer.get("string"));
            assertEquals(42, ThreadLocalContainer.get("integer"));
            assertEquals(true, ThreadLocalContainer.get("bool"));
        }
    }

    @Nested
    @DisplayName("remove")
    class RemoveTest {
        @Test
        @DisplayName("should remove key and return old value")
        void removeKey() {
            ThreadLocalContainer.set("key3", "value3");
            Object removed = ThreadLocalContainer.remove("key3");
            assertEquals("value3", removed);
            assertNull(ThreadLocalContainer.get("key3"));
        }

        @Test
        @DisplayName("should return null when removing nonexistent key")
        void removeNonexistent() {
            assertNull(ThreadLocalContainer.remove("nonexistent"));
        }
    }

    @Nested
    @DisplayName("clean")
    class CleanTest {
        @Test
        @DisplayName("should clean all entries and return map")
        void cleanAll() {
            ThreadLocalContainer.set("a", 1);
            ThreadLocalContainer.set("b", 2);
            Map<String, Object> map = ThreadLocalContainer.clean();
            assertEquals(2, map.size());
            assertEquals(1, map.get("a"));
            assertEquals(2, map.get("b"));
            assertNull(ThreadLocalContainer.get("a"));
            assertNull(ThreadLocalContainer.get("b"));
        }

        @Test
        @DisplayName("should return empty map when nothing stored")
        void cleanEmpty() {
            Map<String, Object> map = ThreadLocalContainer.clean();
            assertNotNull(map);
            assertTrue(map.isEmpty());
        }
    }

    @Nested
    @DisplayName("thread isolation")
    class ThreadIsolationTest {
        @Test
        @DisplayName("should isolate values between threads")
        void threadIsolation() throws Exception {
            ThreadLocalContainer.set("shared", "main");
            Thread t = new Thread(() -> {
                assertNull(ThreadLocalContainer.get("shared"));
                ThreadLocalContainer.set("shared", "other");
                assertEquals("other", ThreadLocalContainer.get("shared"));
                ThreadLocalContainer.clean();
            });
            t.start();
            t.join();
            assertEquals("main", ThreadLocalContainer.get("shared"));
        }
    }
}
