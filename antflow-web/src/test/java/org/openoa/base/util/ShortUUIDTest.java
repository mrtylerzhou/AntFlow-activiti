package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ShortUUIDTest extends BaseTest {

    private static String invokeNumbersToString(long i, int radix, char[] digits) throws Exception {
        Class<?> numbersClass = Class.forName("org.openoa.base.util.ShortUUID$Numbers");
        Method method = numbersClass.getDeclaredMethod("toString", long.class, int.class, char[].class);
        method.setAccessible(true);
        return (String) method.invoke(null, i, radix, digits);
    }

    private static String invokeDigits(long val, int dig, char[] digitsArr) throws Exception {
        Method method = ShortUUID.class.getDeclaredMethod("digits", long.class, int.class, char[].class);
        method.setAccessible(true);
        return (String) method.invoke(null, val, dig, digitsArr);
    }

    @Nested
    @DisplayName("uuid")
    class UuidTest {
        @Test
        @DisplayName("should generate 19-character UUID")
        void shouldGenerate19CharUuid() {
            String uuid = ShortUUID.uuid();
            assertNotNull(uuid);
            assertEquals(19, uuid.length());
        }

        @Test
        @DisplayName("should generate unique UUIDs")
        void shouldGenerateUniqueUuids() {
            String uuid1 = ShortUUID.uuid();
            String uuid2 = ShortUUID.uuid();
            assertNotEquals(uuid1, uuid2);
        }

        @Test
        @DisplayName("should only contain alphanumeric characters")
        void shouldContainOnlyAlphanumeric() {
            for (int i = 0; i < 100; i++) {
                String uuid = ShortUUID.uuid();
                assertTrue(uuid.matches("[0-9a-zA-Z]+"),
                        "UUID should only contain alphanumeric: " + uuid);
            }
        }
    }

    @Nested
    @DisplayName("Numbers.toString (via reflection)")
    class NumbersToStringTest {
        @Test
        @DisplayName("should convert to binary")
        void shouldConvertToBinary() throws Exception {
            char[] digits = {'0', '1'};
            String result = invokeNumbersToString(10, 2, digits);
            assertEquals("1010", result);
        }

        @Test
        @DisplayName("should convert to hex")
        void shouldConvertToHex() throws Exception {
            char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            String result = invokeNumbersToString(255, 16, digits);
            assertEquals("ff", result);
        }

        @Test
        @DisplayName("should convert zero")
        void shouldConvertZero() throws Exception {
            char[] digits = {'0', '1'};
            String result = invokeNumbersToString(0, 2, digits);
            assertEquals("0", result);
        }

        @Test
        @DisplayName("should handle negative numbers")
        void shouldHandleNegative() throws Exception {
            char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
            String result = invokeNumbersToString(-42, 10, digits);
            assertEquals("-42", result);
        }

        @Test
        @DisplayName("should fall back to decimal for invalid radix")
        void shouldFallBackForInvalidRadix() throws Exception {
            char[] digits = {'0', '1'};
            String result = invokeNumbersToString(42, 100, digits);
            assertEquals("42", result);
        }

        @Test
        @DisplayName("should convert to 62-base")
        void shouldConvertTo62Base() throws Exception {
            char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
                    'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                    'V', 'W', 'X', 'Y', 'Z'};
            String result = invokeNumbersToString(61, 62, digits);
            assertEquals("Z", result);
            String result2 = invokeNumbersToString(62, 62, digits);
            assertEquals("10", result2);
        }
    }

    @Nested
    @DisplayName("digits (via reflection)")
    class DigitsTest {
        @Test
        @DisplayName("should produce fixed-length output")
        void shouldProduceFixedLength() throws Exception {
            char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f'};
            String result = invokeDigits(0L, 4, digits);
            assertEquals(4, result.length());
        }

        @Test
        @DisplayName("should produce 8-char for 8-digit hex segment")
        void shouldProduce8ForHex8() throws Exception {
            char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f'};
            String result = invokeDigits(0L, 8, digits);
            assertEquals(8, result.length());
        }
    }
}
