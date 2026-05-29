package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class StrUtilsTest extends BaseTest {

    @Nested
    @DisplayName("joinBpmnCode")
    class JoinBpmnCodeTest {
        @Test
        @DisplayName("should generate first code when no existing code matches pattern")
        void shouldGenerateFirstCode() {
            String result = StrUtils.joinBpmnCode("PROC", "PROC");

            assertEquals("PROC-00001", result);
        }

        @Test
        @DisplayName("should increment number when existing code matches pattern")
        void shouldIncrementNumber() {
            String result = StrUtils.joinBpmnCode("PROC", "PROC-00003");

            assertEquals("PROC-00004", result);
        }

        @Test
        @DisplayName("should handle code with max 5-digit number")
        void shouldHandleMaxFiveDigitNumber() {
            String result = StrUtils.joinBpmnCode("PROC", "PROC-99999");

            assertEquals("PROC-100000", result);
        }

        @Test
        @DisplayName("should handle empty existing code")
        void shouldHandleEmptyExistingCode() {
            String result = StrUtils.joinBpmnCode("PROC", "");

            assertEquals("PROC-00001", result);
        }

        @Test
        @DisplayName("should zero-pad to 5 digits")
        void shouldZeroPadToFiveDigits() {
            String result = StrUtils.joinBpmnCode("WF", "WF");

            assertEquals("WF-00001", result);
        }

        @Test
        @DisplayName("should handle code with non-matching pattern")
        void shouldHandleNonMatchingPattern() {
            String result = StrUtils.joinBpmnCode("PROC", "SOME_OTHER_CODE");

            assertEquals("PROC-00001", result);
        }
    }

    @Nested
    @DisplayName("getFirstLetters")
    class GetFirstLettersTest {
        @Test
        @DisplayName("should return null for null input")
        void shouldReturnNullForNull() {
            assertNull(StrUtils.getFirstLetters(null));
        }

        @Test
        @DisplayName("should return null for empty string")
        void shouldReturnNullForEmpty() {
            assertNull(StrUtils.getFirstLetters(""));
        }

        @Test
        @DisplayName("should return uppercase first letters for Chinese string")
        void shouldReturnUppercaseForChinese() {
            String result = StrUtils.getFirstLetters("审批流程");
            assertNotNull(result);
            assertTrue(result.length() > 0);
            assertEquals(result.toUpperCase(), result);
        }

        @Test
        @DisplayName("should preserve English letters as uppercase")
        void shouldPreserveEnglishAsUppercase() {
            String result = StrUtils.getFirstLetters("Hello");
            assertEquals("HELLO", result);
        }
    }

    @Nested
    @DisplayName("getFirstLettersSmall")
    class GetFirstLettersSmallTest {
        @Test
        @DisplayName("should return lowercase first letters for Chinese string")
        void shouldReturnLowercaseForChinese() {
            String result = StrUtils.getFirstLettersSmall("我心中的项目");
            assertNotNull(result);
            assertEquals(result.toLowerCase(), result);
        }

        @Test
        @DisplayName("should preserve English letters as lowercase")
        void shouldPreserveEnglishAsLowercase() {
            String result = StrUtils.getFirstLettersSmall("Hello");
            assertEquals("hello", result);
        }
    }

    @Nested
    @DisplayName("getBeanNameStandard")
    class GetBeanNameStandardTest {
        @Test
        @DisplayName("should decapitalize class name")
        void shouldDecapitalizeClassName() {
            assertEquals("string", StrUtils.getBeanNameStandard(String.class));
        }

        @Test
        @DisplayName("should throw for null class")
        void shouldThrowForNull() {
            assertThrows(IllegalArgumentException.class, () -> StrUtils.getBeanNameStandard(null));
        }
    }

    @Nested
    @DisplayName("getBeanName")
    class GetBeanNameTest {
        @Test
        @DisplayName("should decapitalize class name")
        void shouldDecapitalizeClassName() {
            assertEquals("string", StrUtils.getBeanName(String.class));
        }
    }

    @Nested
    @DisplayName("nullOrBlankToWhiteSpace")
    class NullOrBlankToWhiteSpaceTest {
        @Test
        @DisplayName("should return space for null")
        void shouldReturnSpaceForNull() {
            assertEquals(" ", StrUtils.nullOrBlankToWhiteSpace(null));
        }

        @Test
        @DisplayName("should return space for empty string")
        void shouldReturnSpaceForEmpty() {
            assertEquals(" ", StrUtils.nullOrBlankToWhiteSpace(""));
        }

        @Test
        @DisplayName("should return original string when not null or blank")
        void shouldReturnOriginalWhenNotBlank() {
            assertEquals("hello", StrUtils.nullOrBlankToWhiteSpace("hello"));
        }

        @Test
        @DisplayName("should return string with spaces as-is")
        void shouldReturnStringWithSpacesAsIs() {
            assertEquals("  ", StrUtils.nullOrBlankToWhiteSpace("  "));
        }
    }

    @Nested
    @DisplayName("getNullPropertyNames")
    class GetNullPropertyNamesTest {
        @Test
        @DisplayName("should return names of null properties")
        void shouldReturnNullPropertyNames() {
            TestBean bean = new TestBean();
            bean.setName("test");

            String[] nullNames = StrUtils.getNullPropertyNames(bean);

            assertTrue(Arrays.asList(nullNames).contains("age"));
        }

        @Test
        @DisplayName("should not include non-null properties")
        void shouldNotIncludeNonNullProperties() {
            TestBean bean = new TestBean();
            bean.setName("test");
            bean.setAge(25);

            String[] nullNames = StrUtils.getNullPropertyNames(bean);

            assertFalse(Arrays.asList(nullNames).contains("name"));
            assertFalse(Arrays.asList(nullNames).contains("age"));
        }
    }

    class TestBean {
        private String name;
        private Integer age;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
    }
}
