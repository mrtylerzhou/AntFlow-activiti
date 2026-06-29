package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.MockBaseTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PropertyUtilTest extends MockBaseTest {

    @org.mockito.Mock
    private Environment environment;

    private void setupMockContext() {
        try {
            Field field = SpringBeanUtils.class.getDeclaredField("applicationContext");
            field.setAccessible(true);
            ApplicationContext ctx = mock(ApplicationContext.class);
            when(ctx.getBean(Environment.class)).thenReturn(environment);
            field.set(null, ctx);
        } catch (Exception e) {
            fail("Failed to set up mock: " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("getProperty")
    class GetPropertyTest {
        @Test
        @DisplayName("should delegate to Environment.getProperty")
        void shouldDelegate() {
            setupMockContext();
            when(environment.getProperty("test.key")).thenReturn("testValue");
            assertEquals("testValue", PropertyUtil.getProperty("test.key"));
        }

        @Test
        @DisplayName("should return null for missing key")
        void missingKey() {
            setupMockContext();
            when(environment.getProperty("missing.key")).thenReturn(null);
            assertNull(PropertyUtil.getProperty("missing.key"));
        }
    }

    @Nested
    @DisplayName("isFullSaSSMode")
    class IsFullSaSSModeTest {
        @Test
        @DisplayName("should return true when property is 'true'")
        void trueValue() {
            setupMockContext();
            when(environment.getProperty("antflow.sass.full-sass-mode")).thenReturn("true");
            assertTrue(PropertyUtil.isFullSaSSMode());
        }

        @Test
        @DisplayName("should return true when property is 'TRUE' (case insensitive)")
        void trueUpperCase() {
            setupMockContext();
            when(environment.getProperty("antflow.sass.full-sass-mode")).thenReturn("TRUE");
            assertTrue(PropertyUtil.isFullSaSSMode());
        }

        @Test
        @DisplayName("should return false when property is 'false'")
        void falseValue() {
            setupMockContext();
            when(environment.getProperty("antflow.sass.full-sass-mode")).thenReturn("false");
            assertFalse(PropertyUtil.isFullSaSSMode());
        }

        @Test
        @DisplayName("should return false when property is null")
        void nullValue() {
            setupMockContext();
            when(environment.getProperty("antflow.sass.full-sass-mode")).thenReturn(null);
            assertFalse(PropertyUtil.isFullSaSSMode());
        }
    }
}
