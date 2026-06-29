package org.openoa.engine.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class SimpleProxyFactoryTest extends BaseTest {

    @Nested
    @DisplayName("getProxyObjectName")
    class GetProxyObjectNameTest {
        @Test
        @DisplayName("should generate proxy name with prefix and suffix")
        void shouldGenerateProxyNameWithPrefixAndSuffix() throws Exception {
            String result = invokeGetProxyObjectName("TestService");

            assertEquals("$Proxy_TestServiceImpl", result);
        }

        @Test
        @DisplayName("should handle empty name")
        void shouldHandleEmptyName() throws Exception {
            String result = invokeGetProxyObjectName("");

            assertEquals("$Proxy_Impl", result);
        }

        @Test
        @DisplayName("should handle simple name")
        void shouldHandleSimpleName() throws Exception {
            String result = invokeGetProxyObjectName("MyInterface");

            assertEquals("$Proxy_MyInterfaceImpl", result);
        }

        private String invokeGetProxyObjectName(String name) throws Exception {
            Method method = SimpleProxyFactory.class.getDeclaredMethod("getProxyObjectName", String.class);
            method.setAccessible(true);
            return (String) method.invoke(null, name);
        }
    }

    @Nested
    @DisplayName("getProxyInstance")
    class GetProxyInstanceTest {
        @Test
        @DisplayName("should create proxy instance for TagParser interface")
        void shouldCreateProxyInstanceForInterface() {
            TagParser proxy = SimpleProxyFactory.getProxyInstance(TagParser.class);

            assertNotNull(proxy);
        }

        @Test
        @DisplayName("should return same instance on second call (caching)")
        void shouldReturnSameInstanceOnSecondCall() {
            TagParser proxy1 = SimpleProxyFactory.getProxyInstance(TagParser.class);
            TagParser proxy2 = SimpleProxyFactory.getProxyInstance(TagParser.class);

            assertSame(proxy1, proxy2);
        }

        @Test
        @DisplayName("proxy should implement the interface")
        void proxyShouldImplementTheInterface() {
            TagParser proxy = SimpleProxyFactory.getProxyInstance(TagParser.class);

            assertTrue(proxy instanceof TagParser);
        }
    }
}
