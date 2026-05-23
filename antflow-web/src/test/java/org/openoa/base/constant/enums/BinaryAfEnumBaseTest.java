package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BinaryAfEnumBaseTest extends BaseTest {

    @Nested
    @DisplayName("splitBinary via BpmnNodeFlagsEnum")
    class SplitBinaryTest {
        @Test
        @DisplayName("should split 0 into empty set")
        void shouldSplitZeroIntoEmptySet() {
            Set<Integer> result = BpmnNodeFlagsEnum.NOTHING.splitBinary(0);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should split 1 into {1}")
        void shouldSplitOne() {
            Set<Integer> result = BpmnNodeFlagsEnum.NOTHING.splitBinary(1);
            assertEquals(1, result.size());
            assertTrue(result.contains(1));
        }

        @Test
        @DisplayName("should split 3 into {1, 2}")
        void shouldSplitThree() {
            Set<Integer> result = BpmnNodeFlagsEnum.NOTHING.splitBinary(3);
            assertEquals(2, result.size());
            assertTrue(result.contains(1));
            assertTrue(result.contains(2));
        }

        @Test
        @DisplayName("should split 15 into {1, 2, 4, 8}")
        void shouldSplitFifteen() {
            Set<Integer> result = BpmnNodeFlagsEnum.NOTHING.splitBinary(15);
            assertEquals(4, result.size());
            assertTrue(result.contains(1));
            assertTrue(result.contains(2));
            assertTrue(result.contains(4));
            assertTrue(result.contains(8));
        }

        @Test
        @DisplayName("should split 5 into {1, 4}")
        void shouldSplitFive() {
            Set<Integer> result = BpmnNodeFlagsEnum.NOTHING.splitBinary(5);
            assertEquals(2, result.size());
            assertTrue(result.contains(1));
            assertTrue(result.contains(4));
        }
    }

    @Nested
    @DisplayName("binaryOr via BpmnNodeFlagsEnum")
    class BinaryOrTest {
        @Test
        @DisplayName("should OR with null flags (treated as 0)")
        void shouldOrWithNull() {
            int result = BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.binaryOr((Integer) null);
            assertEquals(0b1, result);
        }

        @Test
        @DisplayName("should OR with zero flags")
        void shouldOrWithZero() {
            int result = BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.binaryOr(0);
            assertEquals(0b1, result);
        }

        @Test
        @DisplayName("should OR two flags together")
        void shouldOrTwoFlags() {
            int flags = BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.binaryOr(0);
            flags = BpmnNodeFlagsEnum.HAS_EXCLUDE_ASSIGNEE.binaryOr(flags);
            assertEquals(0b101, flags);
        }

        @Test
        @DisplayName("should OR with varargs integers (calls static binaryOr)")
        void shouldOrWithVarargsIntegers() {
            int result = BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.binaryOr(0b10, 0b100);
            assertEquals(0b110, result);
        }

        @Test
        @DisplayName("should OR with enum list")
        void shouldOrWithEnumList() {
            int flags = BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE_ROLE.getCode()
                    | BpmnNodeFlagsEnum.HAS_EXCLUDE_ASSIGNEE.getCode();
            int result = BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.binaryOr(flags);
            assertEquals(0b1 | 0b10 | 0b100, result);
        }

        @Test
        @DisplayName("static binaryOr should combine two integers")
        void staticBinaryOrShouldCombine() {
            int result = BpmnNodeFlagsEnum.binaryOr(0b1, 0b10);
            assertEquals(0b11, result);
        }

        @Test
        @DisplayName("static binaryOr should handle null first arg")
        void staticBinaryOrShouldHandleNull() {
            int result = BpmnNodeFlagsEnum.binaryOr(null, 0b10);
            assertEquals(0b10, result);
        }
    }

    @Nested
    @DisplayName("binaryAndNot via BpmnNodeFlagsEnum")
    class BinaryAndNotTest {
        @Test
        @DisplayName("should remove flag from combined flags")
        void shouldRemoveFlag() {
            int combined = 0b111;
            int result = BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.binaryAndNot(combined);
            assertEquals(0b110, result);
        }

        @Test
        @DisplayName("should handle null flags (treated as 0)")
        void shouldHandleNull() {
            int result = BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.binaryAndNot(null);
            assertEquals(0, result);
        }

        @Test
        @DisplayName("should not affect flags that are not set")
        void shouldNotAffectUnsetFlags() {
            int flags = 0b10;
            int result = BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.binaryAndNot(flags);
            assertEquals(0b10, result);
        }
    }

    @Nested
    @DisplayName("flagsContainsCurrent via BpmnNodeFlagsEnum")
    class FlagsContainsCurrentTest {
        @Test
        @DisplayName("should return true when flag is present")
        void shouldReturnTrueWhenPresent() {
            int flags = 0b1;
            assertTrue(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.flagsContainsCurrent(flags));
        }

        @Test
        @DisplayName("should return false when flag is not present")
        void shouldReturnFalseWhenNotPresent() {
            int flags = 0b10;
            assertFalse(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.flagsContainsCurrent(flags));
        }

        @Test
        @DisplayName("should return false for null flags")
        void shouldReturnFalseForNull() {
            assertFalse(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.flagsContainsCurrent(null));
        }

        @Test
        @DisplayName("should return true when multiple flags including current are set")
        void shouldReturnTrueWhenMultipleSet() {
            int flags = 0b101;
            assertTrue(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE.flagsContainsCurrent(flags));
            assertTrue(BpmnNodeFlagsEnum.HAS_EXCLUDE_ASSIGNEE.flagsContainsCurrent(flags));
            assertFalse(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE_ROLE.flagsContainsCurrent(flags));
        }
    }

    @Nested
    @DisplayName("flagEnumsByCode via BpmnNodeFlagsEnum")
    class FlagEnumsByCodeTest {
        @Test
        @DisplayName("should return empty list for 0")
        void shouldReturnEmptyForZero() {
            List<BpmnNodeFlagsEnum> result = BpmnNodeFlagsEnum.NOTHING.flagEnumsByCode(0);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should return single enum for single flag")
        void shouldReturnSingleForSingleFlag() {
            List<BpmnNodeFlagsEnum> result = BpmnNodeFlagsEnum.NOTHING.flagEnumsByCode(0b1);
            assertEquals(1, result.size());
            assertEquals(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE, result.get(0));
        }

        @Test
        @DisplayName("should return multiple enums for combined flags")
        void shouldReturnMultipleForCombined() {
            List<BpmnNodeFlagsEnum> result = BpmnNodeFlagsEnum.NOTHING.flagEnumsByCode(0b11);
            assertEquals(2, result.size());
            assertTrue(result.contains(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE));
            assertTrue(result.contains(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE_ROLE));
        }

        @Test
        @DisplayName("should handle null as 0")
        void shouldHandleNullAsZero() {
            List<BpmnNodeFlagsEnum> result = BpmnNodeFlagsEnum.NOTHING.flagEnumsByCode(null);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getNodeExtraSignInfo")
    class GetNodeExtraSignInfoTest {
        @Test
        @DisplayName("should return HAS_ADDITIONAL_ASSIGNEE when flag 0b1 is set")
        void shouldReturnAdditionalAssignee() {
            assertEquals(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE,
                    BpmnNodeFlagsEnum.getNodeExtraSignInfo(0b1));
        }

        @Test
        @DisplayName("should return HAS_ADDITIONAL_ASSIGNEE_ROLE when flag 0b10 is set")
        void shouldReturnAdditionalAssigneeRole() {
            assertEquals(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE_ROLE,
                    BpmnNodeFlagsEnum.getNodeExtraSignInfo(0b10));
        }

        @Test
        @DisplayName("should prioritize HAS_ADDITIONAL_ASSIGNEE over others")
        void shouldPrioritizeAdditionalAssignee() {
            assertEquals(BpmnNodeFlagsEnum.HAS_ADDITIONAL_ASSIGNEE,
                    BpmnNodeFlagsEnum.getNodeExtraSignInfo(0b111));
        }

        @Test
        @DisplayName("should return null for 0")
        void shouldReturnNullForZero() {
            assertNull(BpmnNodeFlagsEnum.getNodeExtraSignInfo(0));
        }

        @Test
        @DisplayName("should return null for null")
        void shouldReturnNullForNull() {
            assertNull(BpmnNodeFlagsEnum.getNodeExtraSignInfo(null));
        }

        @Test
        @DisplayName("should return HAS_EXCLUDE_ASSIGNEE when only flag 0b100 is set")
        void shouldReturnExcludeAssignee() {
            assertEquals(BpmnNodeFlagsEnum.HAS_EXCLUDE_ASSIGNEE,
                    BpmnNodeFlagsEnum.getNodeExtraSignInfo(0b100));
        }

        @Test
        @DisplayName("should return HAS_EXCLUDE_ASSIGNEE_ROLE when only flag 0b1000 is set")
        void shouldReturnExcludeAssigneeRole() {
            assertEquals(BpmnNodeFlagsEnum.HAS_EXCLUDE_ASSIGNEE_ROLE,
                    BpmnNodeFlagsEnum.getNodeExtraSignInfo(0b1000));
        }
    }
}
