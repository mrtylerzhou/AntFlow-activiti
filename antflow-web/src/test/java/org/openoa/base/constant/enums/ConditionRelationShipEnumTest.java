package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class ConditionRelationShipEnumTest extends BaseTest {

    @Nested
    @DisplayName("getCodeByValue")
    class GetCodeByValueTest {
        @Test
        @DisplayName("should return 0 for false (AND)")
        void shouldReturn0ForFalse() {
            assertEquals(0, ConditionRelationShipEnum.getCodeByValue(false));
        }

        @Test
        @DisplayName("should return 1 for true (OR)")
        void shouldReturn1ForTrue() {
            assertEquals(1, ConditionRelationShipEnum.getCodeByValue(true));
        }

        @Test
        @DisplayName("should return 1 for null (default OR)")
        void shouldReturn1ForNull() {
            assertEquals(1, ConditionRelationShipEnum.getCodeByValue(null));
        }
    }

    @Nested
    @DisplayName("getValueByCode")
    class GetValueByCodeTest {
        @Test
        @DisplayName("should return false for code 0 (AND)")
        void shouldReturnFalseFor0() {
            assertFalse(ConditionRelationShipEnum.getValueByCode(0));
        }

        @Test
        @DisplayName("should return true for code 1 (OR)")
        void shouldReturnTrueFor1() {
            assertTrue(ConditionRelationShipEnum.getValueByCode(1));
        }

        @Test
        @DisplayName("should return true for null (default OR)")
        void shouldReturnTrueForNull() {
            assertTrue(ConditionRelationShipEnum.getValueByCode(null));
        }

        @Test
        @DisplayName("should return true for unknown code (default OR)")
        void shouldReturnTrueForUnknown() {
            assertTrue(ConditionRelationShipEnum.getValueByCode(99));
        }
    }

    @Nested
    @DisplayName("enum properties")
    class EnumPropertiesTest {
        @Test
        @DisplayName("AND should have code 0, value false, desc and")
        void andProperties() {
            assertEquals(0, ConditionRelationShipEnum.AND.getCode());
            assertFalse(ConditionRelationShipEnum.AND.getValue());
            assertEquals("and", ConditionRelationShipEnum.AND.getDesc());
        }

        @Test
        @DisplayName("OR should have code 1, value true, desc or")
        void orProperties() {
            assertEquals(1, ConditionRelationShipEnum.OR.getCode());
            assertTrue(ConditionRelationShipEnum.OR.getValue());
            assertEquals("or", ConditionRelationShipEnum.OR.getDesc());
        }
    }
}
