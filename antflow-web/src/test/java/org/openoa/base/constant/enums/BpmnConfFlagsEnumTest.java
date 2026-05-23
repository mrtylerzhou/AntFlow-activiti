package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class BpmnConfFlagsEnumTest extends BaseTest {

    @Nested
    @DisplayName("static binaryOr")
    class StaticBinaryOrTest {
        @Test
        @DisplayName("should OR two flags")
        void shouldOrTwoFlags() {
            int result = BpmnConfFlagsEnum.binaryOr(0b1, 0b10);
            assertEquals(0b11, result);
        }

        @Test
        @DisplayName("should handle null alreadyFlags as 0")
        void shouldHandleNullAlreadyFlags() {
            int result = BpmnConfFlagsEnum.binaryOr(null, 0b1);
            assertEquals(0b1, result);
        }

        @Test
        @DisplayName("should OR multiple flags sequentially")
        void shouldOrMultipleSequentially() {
            int flags = 0;
            flags = BpmnConfFlagsEnum.binaryOr(flags, BpmnConfFlagsEnum.HAS_NODE_LABELS.getCode());
            flags = BpmnConfFlagsEnum.binaryOr(flags, BpmnConfFlagsEnum.HAS_COPY.getCode());
            assertEquals(0b1 | 0b1000, flags);
        }

        @Test
        @DisplayName("should handle OR with same flag (idempotent)")
        void shouldHandleIdempotentOr() {
            int result = BpmnConfFlagsEnum.binaryOr(0b1, 0b1);
            assertEquals(0b1, result);
        }
    }

    @Nested
    @DisplayName("instance binaryOr from BinaryAfEnumBase")
    class InstanceBinaryOrTest {
        @Test
        @DisplayName("should OR instance code with existing flags")
        void shouldOrInstanceCodeWithFlags() {
            int result = BpmnConfFlagsEnum.HAS_NODE_LABELS.binaryOr(0);
            assertEquals(0b1, result);
        }

        @Test
        @DisplayName("should combine multiple instance flags")
        void shouldCombineMultipleInstanceFlags() {
            int flags = BpmnConfFlagsEnum.HAS_NODE_LABELS.binaryOr(0);
            flags = BpmnConfFlagsEnum.HAS_COPY.binaryOr(flags);
            assertEquals(0b1 | 0b1000, flags);
        }
    }

    @Nested
    @DisplayName("enum values")
    class EnumValuesTest {
        @Test
        @DisplayName("NOTHING should have code 0")
        void nothingShouldHaveCode0() {
            assertEquals(0, BpmnConfFlagsEnum.NOTHING.getCode());
        }

        @Test
        @DisplayName("HAS_NODE_LABELS should have code 1")
        void hasNodeLabelsShouldHaveCode1() {
            assertEquals(0b1, BpmnConfFlagsEnum.HAS_NODE_LABELS.getCode());
        }

        @Test
        @DisplayName("HAS_FORM_RELATED_ASSIGNEES should have code 0b100000")
        void hasFormRelatedAssigneesCode() {
            assertEquals(0b100000, BpmnConfFlagsEnum.HAS_FORM_RELATED_ASSIGNEES.getCode());
        }
    }
}
