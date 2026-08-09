package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.MockBaseTest;
import org.openoa.base.adp.OrderedBean;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpringBeanUtilsTest extends MockBaseTest {

    private void setupMockContext() {
        try {
            Field field = SpringBeanUtils.class.getDeclaredField("applicationContext");
            field.setAccessible(true);
            ApplicationContext ctx = mock(ApplicationContext.class);
            field.set(null, ctx);
        } catch (Exception e) {
            fail("Failed to set up mock: " + e.getMessage());
        }
    }

    private ApplicationContext getMockContext() {
        try {
            Field field = SpringBeanUtils.class.getDeclaredField("applicationContext");
            field.setAccessible(true);
            return (ApplicationContext) field.get(null);
        } catch (Exception e) {
            fail("Failed to get mock context: " + e.getMessage());
            return null;
        }
    }

    @Nested
    @DisplayName("object cache")
    class ObjectCacheTest {
        @Test
        @DisplayName("should put and get object")
        void putAndGet() {
            SpringBeanUtils.putObject("key1", "value1");
            assertEquals("value1", SpringBeanUtils.getObject("key1"));
        }

        @Test
        @DisplayName("should return null for missing key")
        void missingKey() {
            assertNull(SpringBeanUtils.getObject("nonexistent_key_" + System.currentTimeMillis()));
        }

        @Test
        @DisplayName("should overwrite existing key")
        void overwrite() {
            SpringBeanUtils.putObject("key2", "old");
            SpringBeanUtils.putObject("key2", "new");
            assertEquals("new", SpringBeanUtils.getObject("key2"));
        }

        @Test
        @DisplayName("should store and retrieve object")
        void storeAndRetrieve() {
            SpringBeanUtils.putObject("key3", "value3");
            assertEquals("value3", SpringBeanUtils.getObject("key3"));
        }
    }

    @Nested
    @DisplayName("getBean by name and class")
    class GetBeanByNameAndClassTest {
        @Test
        @DisplayName("should return null when bean does not exist")
        void beanNotExists() {
            setupMockContext();
            when(getMockContext().containsBean("missing")).thenReturn(false);
            assertNull(SpringBeanUtils.getBean("missing", String.class));
        }

        @Test
        @DisplayName("should return bean when it exists")
        void beanExists() {
            setupMockContext();
            when(getMockContext().containsBean("myBean")).thenReturn(true);
            when(getMockContext().getBean("myBean", String.class)).thenReturn("hello");
            assertEquals("hello", SpringBeanUtils.getBean("myBean", String.class));
        }
    }

    @Nested
    @DisplayName("getOrderedBeans")
    class GetOrderedBeansTest {
        @Test
        @DisplayName("should sort beans by order value")
        void sortByOrder() {
            setupMockContext();
            OrderedBean low = new TestOrderedBean(1);
            OrderedBean mid = new TestOrderedBean(5);
            OrderedBean high = new TestOrderedBean(10);
            Map<String, OrderedBean> map = new LinkedHashMap<>();
            map.put("high", high);
            map.put("low", low);
            map.put("mid", mid);
            when(getMockContext().getBeansOfType(TestOrderedBean.class)).thenReturn((Map) map);
            List<TestOrderedBean> result = SpringBeanUtils.getOrderedBeans(TestOrderedBean.class);
            assertEquals(3, result.size());
            assertEquals(1, result.get(0).order());
            assertEquals(5, result.get(1).order());
            assertEquals(10, result.get(2).order());
        }
    }

    private static class TestOrderedBean implements OrderedBean {
        private final int orderVal;

        TestOrderedBean(int orderVal) {
            this.orderVal = orderVal;
        }

        @Override
        public int order() {
            return orderVal;
        }
    }
}
