package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class BpmnNodeFlagsEnumTest extends BaseTest {

    @Nested
    @DisplayName("enum values")
    class EnumValuesTest {
        @Test
        @DisplayName("NOTHING should have code 0")
        void nothingCode() {
            assertEquals(0, BpmnNodeFlagsEnum.NOTHING.getCode());
        }

        @Test
        @DisplayName("HAS_ADDITIONAL_ASSIGNEE should have code 1")
        void additionalAssigneeCode() {
            assertEquals(1, BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.getCode());
        }

        @Test
        @DisplayName("HAS_ADDITIONAL_ASSIGNEE_ROLE should have code 2")
        void additionalAssigneeRoleCode() {
            assertEquals(2, BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE_ROLE.getCode());
        }

        @Test
        @DisplayName("HAS_EXCLUDE_ASSIGNEE should have code 4")
        void excludeAssigneeCode() {
            assertEquals(4, BpmnNodeFlagsEnum.HAS_EXCLUDE_ASSIGNEE.getCode());
        }

        @Test
        @DisplayName("HAS_EXCLUDE_ASSIGNEE_ROLE should have code 8")
        void excludeAssigneeRoleCode() {
            assertEquals(8, BpmnNodeFlagsEnum.HAS_EXCLUDE_ASSIGNEE_ROLE.getCode());
        }

        @Test
        @DisplayName("all values should have non-null desc")
        void allDescsNonNull() {
            for (BpmnNodeFlagsEnum e : BpmnNodeFlagsEnum.values()) {
                assertNotNull(e.getDesc());
            }
        }

        @Test
        @DisplayName("should have exactly 5 enum values")
        void valueCount() {
            assertEquals(5, BpmnNodeFlagsEnum.values().length);
        }
    }

    @Nested
    @DisplayName("binaryOr")
    class BinaryOrTest {
        @Test
        @DisplayName("should OR two flags")
        void orTwoFlags() {
            int result = BpmnNodeFlagsEnum.binaryOr(0b1, 0b10);
            assertEquals(0b11, result);
        }

        @Test
        @DisplayName("should treat null alreadyFlags as 0")
        void nullAlreadyFlags() {
            int result = BpmnNodeFlagsEnum.binaryOr(null, 0b1);
            assertEquals(0b1, result);
        }

        @Test
        @DisplayName("should OR with 0 return same value")
        void orWithZero() {
            int result = BpmnNodeFlagsEnum.binaryOr(0b100, 0);
            assertEquals(0b100, result);
        }

        @Test
        @DisplayName("should combine all flags")
        void combineAllFlags() {
            int result = BpmnNodeFlagsEnum.binaryOr(
                    BpmnNodeFlagsEnum.binaryOr(
                            BpmnNodeFlagsEnum.binaryOr(null, 0b1),
                            0b10),
                    0b100);
            assertEquals(0b111, result);
        }
    }

    @Nested
    @DisplayName("getNodeExtraSignInfo")
    class GetNodeExtraSignInfoTest {
        @Test
        @DisplayName("should return null for null flags")
        void nullFlags() {
            assertNull(BpmnNodeFlagsEnum.getNodeExtraSignInfo(null));
        }

        @Test
        @DisplayName("should return null for 0 flags")
        void zeroFlags() {
            assertNull(BpmnNodeFlagsEnum.getNodeExtraSignInfo(0));
        }

        @Test
        @DisplayName("should return HAS_ADDITIONAL_ASSIGNEE for flag 1")
        void additionalAssignee() {
            assertEquals(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE,
                    BpmnNodeFlagsEnum.getNodeExtraSignInfo(0b1));
        }

        @Test
        @DisplayName("should return HAS_ADDITIONAL_ASSIGNEE_ROLE for flag 2")
        void additionalAssigneeRole() {
            assertEquals(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE_ROLE,
                    BpmnNodeFlagsEnum.getNodeExtraSignInfo(0b10));
        }

        @Test
        @DisplayName("should return HAS_EXCLUDE_ASSIGNEE for flag 4")
        void excludeAssignee() {
            assertEquals(BpmnNodeFlagsEnum.HAS_EXCLUDE_ASSIGNEE,
                    BpmnNodeFlagsEnum.getNodeExtraSignInfo(0b100));
        }

        @Test
        @DisplayName("should return HAS_EXCLUDE_ASSIGNEE_ROLE for flag 8")
        void excludeAssigneeRole() {
            assertEquals(BpmnNodeFlagsEnum.HAS_EXCLUDE_ASSIGNEE_ROLE,
                    BpmnNodeFlagsEnum.getNodeExtraSignInfo(0b1000));
        }

        @Test
        @DisplayName("should prioritize HAS_ADDITIONAL_ASSIGNEE when multiple flags set")
        void prioritizeAdditionalAssignee() {
            assertEquals(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE,
                    BpmnNodeFlagsEnum.getNodeExtraSignInfo(0b1111));
        }

        @Test
        @DisplayName("should return HAS_ADDITIONAL_ASSIGNEE_ROLE when only that and later flags set")
        void prioritizeRoleOverExclude() {
            assertEquals(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE_ROLE,
                    BpmnNodeFlagsEnum.getNodeExtraSignInfo(0b1110));
        }
    }

    @Nested
    @DisplayName("flagsContainsCurrent")
    class FlagsContainsCurrentTest {
        @Test
        @DisplayName("NOTHING should not contain any flag")
        void nothingContainsNothing() {
            assertFalse(BpmnNodeFlagsEnum.NOTHING.flagsContainsCurrent(0));
        }

        @Test
        @DisplayName("HAS_ADDITIONAL_ASSIGNEE should be contained in combined flags")
        void additionalAssigneeContained() {
            assertTrue(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.flagsContainsCurrent(0b11));
        }

        @Test
        @DisplayName("HAS_EXCLUDE_ASSIGNEE_ROLE should be contained in combined flags")
        void excludeRoleContained() {
            assertTrue(BpmnNodeFlagsEnum.HAS_EXCLUDE_ASSIGNEE_ROLE.flagsContainsCurrent(0b1100));
        }
    }
}
