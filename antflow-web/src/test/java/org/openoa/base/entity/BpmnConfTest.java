package org.openoa.base.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.exception.AFBizException;

import static org.junit.jupiter.api.Assertions.*;

class BpmnConfTest extends BaseTest {

    @Nested
    @DisplayName("validateBpmnName")
    class ValidateBpmnNameTest {
        @Test
        @DisplayName("should accept valid name")
        void shouldAcceptValidName() {
            assertDoesNotThrow(() -> BpmnConf.validateBpmnName("MyApprovalFlow"));
        }

        @Test
        @DisplayName("should throw for blank name")
        void shouldThrowForBlank() {
            assertThrows(AFBizException.class, () -> BpmnConf.validateBpmnName(""));
            assertThrows(AFBizException.class, () -> BpmnConf.validateBpmnName(null));
        }

        @Test
        @DisplayName("should throw for name consisting of special character")
        void shouldThrowForSpecialChar() {
            assertThrows(AFBizException.class, () -> BpmnConf.validateBpmnName("@"));
            assertThrows(AFBizException.class, () -> BpmnConf.validateBpmnName("!"));
        }

        @Test
        @DisplayName("should throw for name too long")
        void shouldThrowForTooLong() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 51; i++) sb.append("A");
            String longName = sb.toString();
            assertThrows(AFBizException.class, () -> BpmnConf.validateBpmnName(longName));
        }

        @Test
        @DisplayName("should throw for name ending with -NNNNN pattern")
        void shouldThrowForPattern() {
            assertThrows(AFBizException.class, () -> BpmnConf.validateBpmnName("test-12345"));
        }

        @Test
        @DisplayName("should accept Chinese name")
        void shouldAcceptChineseName() {
            assertDoesNotThrow(() -> BpmnConf.validateBpmnName("审批流程"));
        }

        @Test
        @DisplayName("should accept name with hyphen but not ending with -NNNNN pattern")
        void shouldAcceptWithHyphen() {
            assertDoesNotThrow(() -> BpmnConf.validateBpmnName("test-flow"));
        }
    }

    @Nested
    @DisplayName("setBpmnName")
    class SetBpmnNameTest {
        @Test
        @DisplayName("should set valid name")
        void shouldSetValidName() {
            BpmnConf conf = new BpmnConf();
            conf.setBpmnName("ValidName");
            assertEquals("ValidName", conf.getBpmnName());
        }

        @Test
        @DisplayName("should throw and not set invalid name")
        void shouldThrowAndNotSetInvalid() {
            BpmnConf conf = new BpmnConf();
            assertThrows(AFBizException.class, () -> conf.setBpmnName(""));
            assertNull(conf.getBpmnName());
        }
    }

    @Nested
    @DisplayName("constants")
    class ConstantsTest {
        @Test
        @DisplayName("BPMN_CODE_LEN should be 5")
        void bpmnCodeLen() {
            assertEquals(5, BpmnConf.BPMN_CODE_LEN);
        }

        @Test
        @DisplayName("PATTERN should match 5-digit suffix after hyphen")
        void patternShouldMatch() {
            assertTrue("test-12345".matches(BpmnConf.PATTERN));
            assertTrue("abc-00001".matches(BpmnConf.PATTERN));
        }

        @Test
        @DisplayName("PATTERN should not match without 5-digit suffix")
        void patternShouldNotMatch() {
            assertFalse("test-1234".matches(BpmnConf.PATTERN));
            assertFalse("test-123456".matches(BpmnConf.PATTERN));
            assertFalse("test12345".matches(BpmnConf.PATTERN));
        }

        @Test
        @DisplayName("formatMark should zero-pad to 5 digits")
        void formatMark() {
            assertEquals("00001", String.format(BpmnConf.formatMark, 1));
            assertEquals("00123", String.format(BpmnConf.formatMark, 123));
            assertEquals("99999", String.format(BpmnConf.formatMark, 99999));
        }
    }

    @Nested
    @DisplayName("builder")
    class BuilderTest {
        @Test
        @DisplayName("should build with all fields")
        void shouldBuildWithAllFields() {
            BpmnConf conf = BpmnConf.builder()
                    .id(1L)
                    .bpmnCode("TEST-00001")
                    .bpmnType(1)
                    .formCode("FORM001")
                    .effectiveStatus(1)
                    .isAll(0)
                    .isOutSideProcess(0)
                    .isLowCodeFlow(0)
                    .build();
            assertEquals(1L, conf.getId());
            assertEquals("TEST-00001", conf.getBpmnCode());
            assertEquals(1, conf.getBpmnType());
            assertEquals("FORM001", conf.getFormCode());
        }
    }
}
