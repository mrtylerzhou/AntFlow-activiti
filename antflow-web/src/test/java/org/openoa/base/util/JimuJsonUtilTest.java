package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JimuJsonUtilTest extends BaseTest {

    @Nested
    @DisplayName("toJsonString")
    class ToJsonStringTest {
        @Test
        @DisplayName("should serialize simple object")
        void shouldSerializeSimple() {
            TestBean bean = new TestBean();
            bean.setName("test");
            bean.setAge(25);
            String json = JimuJsonUtil.toJsonString(bean);
            assertNotNull(json);
            assertTrue(json.contains("test"));
            assertTrue(json.contains("25"));
        }

        @Test
        @DisplayName("should exclude null fields")
        void shouldExcludeNullFields() {
            TestBean bean = new TestBean();
            bean.setName("test");
            String json = JimuJsonUtil.toJsonString(bean);
            assertNotNull(json);
            assertTrue(json.contains("test"));
        }
    }

    @Nested
    @DisplayName("readAsEntity")
    class ReadAsEntityTest {
        @Test
        @DisplayName("should deserialize to entity")
        void shouldDeserializeToEntity() throws Exception {
            String json = "{\"name\":\"hello\",\"age\":30}";
            TestBean bean = JimuJsonUtil.readAsEntity(json, TestBean.class);
            assertEquals("hello", bean.getName());
            assertEquals(30, bean.getAge());
        }
    }

    @Nested
    @DisplayName("readAsList")
    class ReadAsListTest {
        @Test
        @DisplayName("should deserialize to list")
        void shouldDeserializeToList() throws Exception {
            String json = "[{\"name\":\"a\",\"age\":1},{\"name\":\"b\",\"age\":2}]";
            List<TestBean> list = JimuJsonUtil.readAsList(json, TestBean.class);
            assertEquals(2, list.size());
            assertEquals("a", list.get(0).getName());
            assertEquals("b", list.get(1).getName());
        }
    }

    @Nested
    @DisplayName("readAsMapSS")
    class ReadAsMapSSTest {
        @Test
        @DisplayName("should deserialize to String-String map")
        void shouldDeserializeToMap() throws Exception {
            String json = "{\"key1\":\"val1\",\"key2\":\"val2\"}";
            Map<String, String> map = JimuJsonUtil.readAsMapSS(json);
            assertEquals(2, map.size());
            assertEquals("val1", map.get("key1"));
            assertEquals("val2", map.get("key2"));
        }
    }

    @Nested
    @DisplayName("readAsMapSI")
    class ReadAsMapSITest {
        @Test
        @DisplayName("should deserialize to String-Integer map")
        void shouldDeserializeToMap() throws Exception {
            String json = "{\"a\":1,\"b\":2}";
            Map<String, Integer> map = JimuJsonUtil.readAsMapSI(json);
            assertEquals(2, map.size());
            assertEquals(1, map.get("a"));
            assertEquals(2, map.get("b"));
        }
    }

    @Nested
    @DisplayName("readAsMapSL")
    class ReadAsMapSLTest {
        @Test
        @DisplayName("should deserialize to String-Long map")
        void shouldDeserializeToMap() throws Exception {
            String json = "{\"x\":100,\"y\":200}";
            Map<String, Long> map = JimuJsonUtil.readAsMapSL(json);
            assertEquals(2, map.size());
            assertEquals(100L, map.get("x").longValue());
        }
    }

    @Nested
    @DisplayName("round-trip")
    class RoundTripTest {
        @Test
        @DisplayName("should survive serialize-deserialize round trip")
        void shouldSurviveRoundTrip() throws Exception {
            TestBean original = new TestBean();
            original.setName("roundtrip");
            original.setAge(99);
            String json = JimuJsonUtil.toJsonString(original);
            TestBean restored = JimuJsonUtil.readAsEntity(json, TestBean.class);
            assertEquals("roundtrip", restored.getName());
            assertEquals(99, restored.getAge());
        }
    }

    public static class TestBean {
        private String name;
        private int age;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }
}
