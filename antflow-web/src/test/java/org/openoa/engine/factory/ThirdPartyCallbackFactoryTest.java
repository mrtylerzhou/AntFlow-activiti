package org.openoa.engine.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class ThirdPartyCallbackFactoryTest extends BaseTest {

    @Nested
    @DisplayName("build")
    class BuildTest {
        @Test
        @DisplayName("should return singleton instance")
        void shouldReturnSingleton() {
            ThirdPartyCallbackFactory instance1 = ThirdPartyCallbackFactory.build();
            ThirdPartyCallbackFactory instance2 = ThirdPartyCallbackFactory.build();

            assertNotNull(instance1);
            assertSame(instance1, instance2);
        }
    }

    @Nested
    @DisplayName("formatFormCode")
    class FormatFormCodeTest {
        @Test
        @DisplayName("should not add prefix when form code already starts with business party mark")
        void shouldNotAddPrefixWhenAlreadyStartsWith() {
            ThirdPartyCallbackFactory factory = ThirdPartyCallbackFactory.build();

            String result = factory.formatFormCode("BIZ", "BIZ_FORM_001");

            assertEquals("BIZ_FORM_001", result);
        }

        @Test
        @DisplayName("should add prefix when form code does not start with business party mark")
        void shouldAddPrefixWhenNotStartsWith() {
            ThirdPartyCallbackFactory factory = ThirdPartyCallbackFactory.build();

            String result = factory.formatFormCode("BIZ", "FORM_001");

            assertEquals("BIZ_FORM_001", result);
        }

        @Test
        @DisplayName("should handle empty form code")
        void shouldHandleEmptyFormCode() {
            ThirdPartyCallbackFactory factory = ThirdPartyCallbackFactory.build();

            String result = factory.formatFormCode("BIZ", "");

            assertEquals("BIZ_", result);
        }
    }
}
