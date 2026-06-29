package org.openoa.engine.bpmnconf.constant.enus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class ConditionTypeEnumTest extends BaseTest {

    @Nested
    @DisplayName("getEnumByCode")
    class GetEnumByCodeTest {
        @Test
        @DisplayName("should return CONDITION_THIRD_ACCOUNT_TYPE for code 1")
        void shouldReturnThirdAccountType() {
            ConditionTypeEnum result = ConditionTypeEnum.getEnumByCode(1);
            assertNotNull(result);
            assertEquals("三方账户", result.getDesc());
        }

        @Test
        @DisplayName("should return CONDITION_BIZ_LEAVE_TIME for code 2")
        void shouldReturnBizLeaveTime() {
            ConditionTypeEnum result = ConditionTypeEnum.getEnumByCode(2);
            assertNotNull(result);
            assertEquals("请假时长", result.getDesc());
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(ConditionTypeEnum.getEnumByCode(999));
        }

        @Test
        @DisplayName("should return null for null code")
        void shouldReturnNullForNull() {
            assertNull(ConditionTypeEnum.getEnumByCode(null));
        }

        @Test
        @DisplayName("should return low code condition for code 10000")
        void shouldReturnLowCodeStrCondition() {
            ConditionTypeEnum result = ConditionTypeEnum.getEnumByCode(10000);
            assertNotNull(result);
            assertEquals("无代码字符串流程条件", result.getDesc());
        }
    }

    @Nested
    @DisplayName("isLowCodeFlow")
    class IsLowCodeFlowTest {
        @Test
        @DisplayName("should return true for low code string condition")
        void shouldReturnTrueForStrCondition() {
            assertTrue(ConditionTypeEnum.isLowCodeFlow(ConditionTypeEnum.CONDITION_TYPE_LF_STR_CONDITION));
        }

        @Test
        @DisplayName("should return true for low code number condition")
        void shouldReturnTrueForNumCondition() {
            assertTrue(ConditionTypeEnum.isLowCodeFlow(ConditionTypeEnum.CONDITION_TYPE_LF_NUM_CONDITION));
        }

        @Test
        @DisplayName("should return true for low code date condition")
        void shouldReturnTrueForDateCondition() {
            assertTrue(ConditionTypeEnum.isLowCodeFlow(ConditionTypeEnum.CONDITION_TYPE_LF_DATE_CONDITION));
        }

        @Test
        @DisplayName("should return true for low code datetime condition")
        void shouldReturnTrueForDateTimeCondition() {
            assertTrue(ConditionTypeEnum.isLowCodeFlow(ConditionTypeEnum.CONDITION_TYPE_LF_DATE_TIME_CONDITION));
        }

        @Test
        @DisplayName("should return true for low code collection condition")
        void shouldReturnTrueForCollectionCondition() {
            assertTrue(ConditionTypeEnum.isLowCodeFlow(ConditionTypeEnum.CONDITION_TYPE_LF_COLLECTION_CONDITION));
        }

        @Test
        @DisplayName("should return false for non-low-code condition")
        void shouldReturnFalseForNonLowCode() {
            assertFalse(ConditionTypeEnum.isLowCodeFlow(ConditionTypeEnum.CONDITION_THIRD_ACCOUNT_TYPE));
            assertFalse(ConditionTypeEnum.isLowCodeFlow(ConditionTypeEnum.CONDITION_BIZ_LEAVE_TIME));
        }
    }

    @Nested
    @DisplayName("getEnumByFieldName")
    class GetEnumByFieldNameTest {
        @Test
        @DisplayName("should return condition by field name")
        void shouldReturnByFieldName() {
            ConditionTypeEnum result = ConditionTypeEnum.getEnumByFieldName("accountType");
            assertNotNull(result);
            assertEquals(ConditionTypeEnum.CONDITION_THIRD_ACCOUNT_TYPE, result);
        }

        @Test
        @DisplayName("should return null for non-existent field name")
        void shouldReturnNullForNonExistent() {
            assertNull(ConditionTypeEnum.getEnumByFieldName("nonExistentField"));
        }

        @Test
        @DisplayName("should return null for null field name")
        void shouldReturnNullForNull() {
            assertNull(ConditionTypeEnum.getEnumByFieldName(null));
        }
    }

    @Nested
    @DisplayName("enum properties")
    class EnumPropertiesTest {
        @Test
        @DisplayName("CONDITION_THIRD_ACCOUNT_TYPE should have correct properties")
        void thirdAccountTypeProperties() {
            ConditionTypeEnum e = ConditionTypeEnum.CONDITION_THIRD_ACCOUNT_TYPE;
            assertEquals(1, e.getCode());
            assertEquals("accountType", e.getFieldName());
            assertEquals(1, e.getFieldType());
            assertEquals(Integer.class, e.getFieldCls());
        }

        @Test
        @DisplayName("CONDITION_BIZ_LEAVE_TIME should have Double field class")
        void bizLeaveTimeProperties() {
            assertEquals(Double.class, ConditionTypeEnum.CONDITION_BIZ_LEAVE_TIME.getFieldCls());
            assertEquals(2, ConditionTypeEnum.CONDITION_BIZ_LEAVE_TIME.getFieldType());
        }
    }
}
