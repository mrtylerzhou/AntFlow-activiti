package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class PinyinUtilsTest extends BaseTest {

    @Nested
    @DisplayName("getFirstLettersUpperCase")
    class GetFirstLettersUpperCaseTest {
        @Test
        @DisplayName("should return null for null input")
        void shouldReturnNullForNull() {
            assertNull(PinyinUtils.getFirstLettersUpperCase(null));
        }

        @Test
        @DisplayName("should return empty for empty input")
        void shouldReturnEmptyForEmpty() {
            assertEquals("", PinyinUtils.getFirstLettersUpperCase(""));
        }

        @Test
        @DisplayName("should convert Chinese characters to uppercase pinyin initials")
        void shouldConvertChineseToUppercase() {
            String result = PinyinUtils.getFirstLettersUpperCase("审批");
            assertEquals("SP", result);
        }

        @Test
        @DisplayName("should convert mixed Chinese and English to uppercase")
        void shouldConvertMixedToUppercase() {
            String result = PinyinUtils.getFirstLettersUpperCase("审批Flow");
            assertEquals("SPFLOW", result);
        }

        @Test
        @DisplayName("should preserve digits and punctuation")
        void shouldPreserveDigitsAndPunctuation() {
            String result = PinyinUtils.getFirstLettersUpperCase("审批-001");
            assertEquals("SP-001", result);
        }

        @Test
        @DisplayName("should handle single Chinese character")
        void shouldHandleSingleChar() {
            String result = PinyinUtils.getFirstLettersUpperCase("中");
            assertEquals("Z", result);
        }

        @Test
        @DisplayName("should handle pure English string")
        void shouldHandlePureEnglish() {
            String result = PinyinUtils.getFirstLettersUpperCase("hello");
            assertEquals("HELLO", result);
        }
    }

    @Nested
    @DisplayName("getFirstLettersLowerCase")
    class GetFirstLettersLowerCaseTest {
        @Test
        @DisplayName("should convert Chinese characters to lowercase pinyin initials")
        void shouldConvertChineseToLowercase() {
            String result = PinyinUtils.getFirstLettersLowerCase("审批");
            assertEquals("sp", result);
        }

        @Test
        @DisplayName("should convert mixed Chinese and English to lowercase")
        void shouldConvertMixedToLowercase() {
            String result = PinyinUtils.getFirstLettersLowerCase("审批Flow");
            assertEquals("spflow", result);
        }

        @Test
        @DisplayName("should return null for null input")
        void shouldReturnNullForNull() {
            assertNull(PinyinUtils.getFirstLettersLowerCase(null));
        }
    }

    @Nested
    @DisplayName("specific Chinese character pinyin mapping")
    class SpecificPinyinMappingTest {
        @Test
        @DisplayName("should map common characters correctly")
        void shouldMapCommonCharactersCorrectly() {
            assertEquals("A", PinyinUtils.getFirstLettersUpperCase("啊"));
            assertEquals("P", PinyinUtils.getFirstLettersUpperCase("拍"));
            assertEquals("S", PinyinUtils.getFirstLettersUpperCase("撒"));
            assertEquals("Z", PinyinUtils.getFirstLettersUpperCase("匝"));
        }

        @Test
        @DisplayName("should handle O-Z section characters via exact match")
        void shouldHandleOZSectionCharacters() {
            assertEquals("P", PinyinUtils.getFirstLettersUpperCase("拍"));
            assertEquals("Q", PinyinUtils.getFirstLettersUpperCase("期"));
            assertEquals("R", PinyinUtils.getFirstLettersUpperCase("然"));
            assertEquals("S", PinyinUtils.getFirstLettersUpperCase("撒"));
            assertEquals("T", PinyinUtils.getFirstLettersUpperCase("他"));
            assertEquals("W", PinyinUtils.getFirstLettersUpperCase("挖"));
            assertEquals("X", PinyinUtils.getFirstLettersUpperCase("昔"));
            assertEquals("Y", PinyinUtils.getFirstLettersUpperCase("压"));
            assertEquals("Z", PinyinUtils.getFirstLettersUpperCase("匝"));
        }
    }
}
