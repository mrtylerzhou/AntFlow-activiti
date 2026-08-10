package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class AFNumberUtilTest extends BaseTest {

    @Nested
    @DisplayName("encodeLong")
    class EncodeLongTest {
        @Test
        @DisplayName("should encode 0 as '0'")
        void shouldEncodeZero() {
            assertEquals("0", AFNumberUtil.encodeLong(0));
        }

        @Test
        @DisplayName("should encode 1 as '1'")
        void shouldEncodeOne() {
            assertEquals("1", AFNumberUtil.encodeLong(1));
        }

        @Test
        @DisplayName("should encode 10 as 'A'")
        void shouldEncodeTen() {
            assertEquals("A", AFNumberUtil.encodeLong(10));
        }

        @Test
        @DisplayName("should encode 61 as 'z'")
        void shouldEncodeSixtyOne() {
            assertEquals("z", AFNumberUtil.encodeLong(61));
        }

        @Test
        @DisplayName("should encode 62 as '10'")
        void shouldEncodeSixtyTwo() {
            assertEquals("10", AFNumberUtil.encodeLong(62));
        }

        @Test
        @DisplayName("should throw for negative number")
        void shouldThrowForNegative() {
            assertThrows(IllegalArgumentException.class, () -> AFNumberUtil.encodeLong(-1));
        }
    }

    @Nested
    @DisplayName("decodeLong")
    class DecodeLongTest {
        @Test
        @DisplayName("should decode '0' as 0")
        void shouldDecodeZero() {
            assertEquals(0, AFNumberUtil.decodeLong("0"));
        }

        @Test
        @DisplayName("should decode 'A' as 10")
        void shouldDecodeA() {
            assertEquals(10, AFNumberUtil.decodeLong("A"));
        }

        @Test
        @DisplayName("should decode 'z' as 61")
        void shouldDecodeZ() {
            assertEquals(61, AFNumberUtil.decodeLong("z"));
        }

        @Test
        @DisplayName("should decode '10' as 62")
        void shouldDecodeTen() {
            assertEquals(62, AFNumberUtil.decodeLong("10"));
        }

        @Test
        @DisplayName("should throw for null input")
        void shouldThrowForNull() {
            assertThrows(IllegalArgumentException.class, () -> AFNumberUtil.decodeLong(null));
        }

        @Test
        @DisplayName("should throw for empty input")
        void shouldThrowForEmpty() {
            assertThrows(IllegalArgumentException.class, () -> AFNumberUtil.decodeLong(""));
        }

        @Test
        @DisplayName("should throw for invalid character")
        void shouldThrowForInvalidChar() {
            assertThrows(IllegalArgumentException.class, () -> AFNumberUtil.decodeLong("!@#"));
        }
    }

    @Nested
    @DisplayName("encodeDecodeRoundTrip")
    class EncodeDecodeRoundTripTest {
        @Test
        @DisplayName("encode then decode should return original number")
        void shouldRoundTrip() {
            long[] testValues = {0, 1, 10, 61, 62, 100, 1000, 999999, Long.MAX_VALUE / 2};

            for (long value : testValues) {
                String encoded = AFNumberUtil.encodeLong(value);
                long decoded = AFNumberUtil.decodeLong(encoded);
                assertEquals(value, decoded, "Round trip failed for " + value);
            }
        }
    }

    @Nested
    @DisplayName("shortBusinessId")
    class ShortBusinessIdTest {
        @Test
        @DisplayName("should return null for null input")
        void shouldReturnNullForNull() {
            assertNull(AFNumberUtil.shortBusinessId(null));
        }

        @Test
        @DisplayName("should return original id when length <= 20")
        void shouldReturnOriginalWhenShort() {
            String shortId = "BIZ_001";
            assertEquals(shortId, AFNumberUtil.shortBusinessId(shortId));
        }

        @Test
        @DisplayName("should return hash when length > 20")
        void shouldReturnHashWhenLong() {
            String longId = "VERY_LONG_BUSINESS_ID_THAT_EXCEEDS_20_CHARS";
            String result = AFNumberUtil.shortBusinessId(longId);

            assertNotNull(result);
            assertTrue(result.length() <= 22);
            assertNotEquals(longId, result);
        }

        @Test
        @DisplayName("should return consistent hash for same input")
        void shouldReturnConsistentHash() {
            String longId = "VERY_LONG_BUSINESS_ID_THAT_EXCEEDS_20_CHARS";

            String result1 = AFNumberUtil.shortBusinessId(longId);
            String result2 = AFNumberUtil.shortBusinessId(longId);

            assertEquals(result1, result2);
        }
    }

    @Nested
    @DisplayName("generateShortHash")
    class GenerateShortHashTest {
        @Test
        @DisplayName("should generate non-empty hash")
        void shouldGenerateNonEmptyHash() {
            assertNotNull(AFNumberUtil.generateShortHash("test"));
        }

        @Test
        @DisplayName("should generate consistent hash for same input")
        void shouldGenerateConsistentHash() {
            String hash1 = AFNumberUtil.generateShortHash("test");
            String hash2 = AFNumberUtil.generateShortHash("test");

            assertEquals(hash1, hash2);
        }

        @Test
        @DisplayName("should generate different hash for different input")
        void shouldGenerateDifferentHash() {
            String hash1 = AFNumberUtil.generateShortHash("test1");
            String hash2 = AFNumberUtil.generateShortHash("test2");

            assertNotEquals(hash1, hash2);
        }
    }

    @Nested
    @DisplayName("md5ToBase62")
    class Md5ToBase62Test {
        @Test
        @DisplayName("should generate non-empty base62 string")
        void shouldGenerateNonEmptyBase62() {
            String result = AFNumberUtil.md5ToBase62("test");

            assertNotNull(result);
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("should generate consistent result for same input")
        void shouldGenerateConsistentResult() {
            String result1 = AFNumberUtil.md5ToBase62("test");
            String result2 = AFNumberUtil.md5ToBase62("test");

            assertEquals(result1, result2);
        }

        @Test
        @DisplayName("should generate different result for different input")
        void shouldGenerateDifferentResult() {
            String result1 = AFNumberUtil.md5ToBase62("test1");
            String result2 = AFNumberUtil.md5ToBase62("test2");

            assertNotEquals(result1, result2);
        }

        @Test
        @DisplayName("should only contain base62 characters")
        void shouldOnlyContainBase62Chars() {
            String result = AFNumberUtil.md5ToBase62("test");

            for (char c : result.toCharArray()) {
                assertTrue(
                        (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'),
                        "Invalid character: " + c);
            }
        }
    }
}
