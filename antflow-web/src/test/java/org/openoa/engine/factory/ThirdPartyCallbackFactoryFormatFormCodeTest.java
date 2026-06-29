package org.openoa.engine.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class ThirdPartyCallbackFactoryFormatFormCodeTest extends BaseTest {

    private final ThirdPartyCallbackFactory factory = ThirdPartyCallbackFactory.build();

    @Nested
    @DisplayName("formatFormCode")
    class FormatFormCodeTest {
        @Test
        @DisplayName("should return formCode as-is when it already starts with businessPartyMark")
        void shouldReturnFormCodeWhenAlreadyStartsWithMark() {
            String result = factory.formatFormCode("PARTY_A", "PARTY_A_form001");

            assertEquals("PARTY_A_form001", result);
        }

        @Test
        @DisplayName("should prepend businessPartyMark when formCode does not start with it")
        void shouldPrependMarkWhenNotStartsWithIt() {
            String result = factory.formatFormCode("PARTY_A", "form001");

            assertEquals("PARTY_A_form001", result);
        }

        @Test
        @DisplayName("should handle empty formCode")
        void shouldHandleEmptyFormCode() {
            String result = factory.formatFormCode("PARTY_A", "");

            assertEquals("PARTY_A_", result);
        }

        @Test
        @DisplayName("should return formCode as-is when formCode starts with mark as substring")
        void shouldReturnAsIsWhenFormCodeStartsWithMarkSubstring() {
            String result = factory.formatFormCode("PARTY", "PARTY_A_form001");

            assertEquals("PARTY_A_form001", result);
        }

        @Test
        @DisplayName("should handle exact match of formCode and businessPartyMark")
        void shouldHandleExactMatch() {
            String result = factory.formatFormCode("PARTY_A", "PARTY_A");

            assertEquals("PARTY_A", result);
        }

        @Test
        @DisplayName("should handle case-sensitive matching")
        void shouldHandleCaseSensitive() {
            String result = factory.formatFormCode("party_a", "PARTY_A_form001");

            assertEquals("party_a_PARTY_A_form001", result);
        }

        @Test
        @DisplayName("should handle numeric businessPartyMark")
        void shouldHandleNumericMark() {
            String result = factory.formatFormCode("123", "form001");

            assertEquals("123_form001", result);
        }

        @Test
        @DisplayName("should not prepend when formCode starts with mark as substring")
        void shouldNotPrependWhenStartsWithMark() {
            String result = factory.formatFormCode("ABC", "ABC_def");

            assertEquals("ABC_def", result);
        }
    }

    @Nested
    @DisplayName("build singleton")
    class BuildSingletonTest {
        @Test
        @DisplayName("should return same instance on multiple calls")
        void shouldReturnSameInstance() {
            ThirdPartyCallbackFactory instance1 = ThirdPartyCallbackFactory.build();
            ThirdPartyCallbackFactory instance2 = ThirdPartyCallbackFactory.build();

            assertSame(instance1, instance2);
        }
    }
}
