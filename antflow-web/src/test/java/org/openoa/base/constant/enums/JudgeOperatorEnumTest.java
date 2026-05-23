package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.exception.AFBizException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JudgeOperatorEnumTest extends BaseTest {

    @Nested
    @DisplayName("getBySymbol")
    class GetBySymbolTest {
        @Test
        @DisplayName("should return GTE for >= symbol")
        void shouldReturnGTE() {
            assertEquals(JudgeOperatorEnum.GTE, JudgeOperatorEnum.getBySymbol(">="));
        }

        @Test
        @DisplayName("should return GT for > symbol")
        void shouldReturnGT() {
            assertEquals(JudgeOperatorEnum.GT, JudgeOperatorEnum.getBySymbol(">"));
        }

        @Test
        @DisplayName("should return LTE for <= symbol")
        void shouldReturnLTE() {
            assertEquals(JudgeOperatorEnum.LTE, JudgeOperatorEnum.getBySymbol("<="));
        }

        @Test
        @DisplayName("should return LT for < symbol")
        void shouldReturnLT() {
            assertEquals(JudgeOperatorEnum.LT, JudgeOperatorEnum.getBySymbol("<"));
        }

        @Test
        @DisplayName("should return EQ for = symbol")
        void shouldReturnEQ() {
            assertEquals(JudgeOperatorEnum.EQ, JudgeOperatorEnum.getBySymbol("="));
        }

        @Test
        @DisplayName("should return null for blank symbol")
        void shouldReturnNullForBlank() {
            assertNull(JudgeOperatorEnum.getBySymbol(""));
            assertNull(JudgeOperatorEnum.getBySymbol(null));
            assertNull(JudgeOperatorEnum.getBySymbol("  "));
        }

        @Test
        @DisplayName("should return null for unknown symbol")
        void shouldReturnNullForUnknown() {
            assertNull(JudgeOperatorEnum.getBySymbol("!="));
        }

        @Test
        @DisplayName("should handle symbol with surrounding whitespace")
        void shouldHandleTrimmedSymbol() {
            assertEquals(JudgeOperatorEnum.GTE, JudgeOperatorEnum.getBySymbol(" >= "));
        }
    }

    @Nested
    @DisplayName("getByOpType")
    class GetByOpTypeTest {
        @Test
        @DisplayName("should return GTE for code 1")
        void shouldReturnGTE() {
            assertEquals(JudgeOperatorEnum.GTE, JudgeOperatorEnum.getByOpType(1));
        }

        @Test
        @DisplayName("should return GT for code 2")
        void shouldReturnGT() {
            assertEquals(JudgeOperatorEnum.GT, JudgeOperatorEnum.getByOpType(2));
        }

        @Test
        @DisplayName("should return EQ for code 5")
        void shouldReturnEQ() {
            assertEquals(JudgeOperatorEnum.EQ, JudgeOperatorEnum.getByOpType(5));
        }

        @Test
        @DisplayName("should return GT1LT2 for code 6")
        void shouldReturnGT1LT2() {
            assertEquals(JudgeOperatorEnum.GT1LT2, JudgeOperatorEnum.getByOpType(6));
        }

        @Test
        @DisplayName("should throw for undefined opType")
        void shouldThrowForUndefined() {
            assertThrows(AFBizException.class, () -> JudgeOperatorEnum.getByOpType(99));
        }
    }

    @Nested
    @DisplayName("binaryOperator")
    class BinaryOperatorTest {
        @Test
        @DisplayName("should return list of binary operator codes")
        void shouldReturnBinaryOperatorCodes() {
            List<Integer> codes = JudgeOperatorEnum.binaryOperator();
            assertEquals(4, codes.size());
            assertTrue(codes.contains(6));
            assertTrue(codes.contains(7));
            assertTrue(codes.contains(8));
            assertTrue(codes.contains(9));
        }

        @Test
        @DisplayName("should not contain unary operator codes")
        void shouldNotContainUnaryCodes() {
            List<Integer> codes = JudgeOperatorEnum.binaryOperator();
            assertFalse(codes.contains(1));
            assertFalse(codes.contains(5));
        }
    }

    @Nested
    @DisplayName("enum properties")
    class EnumPropertiesTest {
        @Test
        @DisplayName("GTE should have code 1 and symbol >=")
        void gteProperties() {
            assertEquals(1, JudgeOperatorEnum.GTE.getCode());
            assertEquals(">=", JudgeOperatorEnum.GTE.getSymbol());
        }

        @Test
        @DisplayName("getDesc should return symbol")
        void getDescReturnsSymbol() {
            assertEquals(">=", JudgeOperatorEnum.GTE.getDesc());
            assertEquals("=", JudgeOperatorEnum.EQ.getDesc());
        }
    }
}
