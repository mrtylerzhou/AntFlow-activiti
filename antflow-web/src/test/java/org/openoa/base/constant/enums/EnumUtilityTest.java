package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.exception.AFBizException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnumUtilityTest extends BaseTest {

    @Nested
    @DisplayName("JudgeOperatorEnum")
    class JudgeOperatorEnumTest {

        @Nested
        @DisplayName("getBySymbol")
        class GetBySymbolTest {

            @Test
            @DisplayName("returns GTE for '>='")
            void gte() {
                assertEquals(JudgeOperatorEnum.GTE, JudgeOperatorEnum.getBySymbol(">="));
            }

            @Test
            @DisplayName("returns GT for '>'")
            void gt() {
                assertEquals(JudgeOperatorEnum.GT, JudgeOperatorEnum.getBySymbol(">"));
            }

            @Test
            @DisplayName("returns LTE for '<='")
            void lte() {
                assertEquals(JudgeOperatorEnum.LTE, JudgeOperatorEnum.getBySymbol("<="));
            }

            @Test
            @DisplayName("returns LT for '<'")
            void lt() {
                assertEquals(JudgeOperatorEnum.LT, JudgeOperatorEnum.getBySymbol("<"));
            }

            @Test
            @DisplayName("returns EQ for '='")
            void eq() {
                assertEquals(JudgeOperatorEnum.EQ, JudgeOperatorEnum.getBySymbol("="));
            }

            @Test
            @DisplayName("returns GT1LT2 for 'first<a<second'")
            void gt1lt2() {
                assertEquals(JudgeOperatorEnum.GT1LT2, JudgeOperatorEnum.getBySymbol("first<a<second"));
            }

            @Test
            @DisplayName("returns GTE1LT2 for 'first<=a<second'")
            void gte1lt2() {
                assertEquals(JudgeOperatorEnum.GTE1LT2, JudgeOperatorEnum.getBySymbol("first<=a<second"));
            }

            @Test
            @DisplayName("returns GET1LE2 for 'first<a<=second'")
            void get1le2() {
                assertEquals(JudgeOperatorEnum.GET1LE2, JudgeOperatorEnum.getBySymbol("first<a<=second"));
            }

            @Test
            @DisplayName("returns GTE1LTE2 for 'first<=a<=second'")
            void gte1lte2() {
                assertEquals(JudgeOperatorEnum.GTE1LTE2, JudgeOperatorEnum.getBySymbol("first<=a<=second"));
            }

            @Test
            @DisplayName("returns null for null input")
            void nullInput() {
                assertNull(JudgeOperatorEnum.getBySymbol(null));
            }

            @Test
            @DisplayName("returns null for blank input")
            void blankInput() {
                assertNull(JudgeOperatorEnum.getBySymbol(""));
                assertNull(JudgeOperatorEnum.getBySymbol("   "));
            }

            @Test
            @DisplayName("returns null for unknown symbol")
            void unknownSymbol() {
                assertNull(JudgeOperatorEnum.getBySymbol("unknown"));
            }
        }

        @Nested
        @DisplayName("getByOpType")
        class GetByOpTypeTest {

            @Test
            @DisplayName("opType 1 returns GTE")
            void opType1() {
                assertEquals(JudgeOperatorEnum.GTE, JudgeOperatorEnum.getByOpType(1));
            }

            @Test
            @DisplayName("opType 2 returns GT")
            void opType2() {
                assertEquals(JudgeOperatorEnum.GT, JudgeOperatorEnum.getByOpType(2));
            }

            @Test
            @DisplayName("opType 3 returns LTE")
            void opType3() {
                assertEquals(JudgeOperatorEnum.LTE, JudgeOperatorEnum.getByOpType(3));
            }

            @Test
            @DisplayName("opType 4 returns LT")
            void opType4() {
                assertEquals(JudgeOperatorEnum.LT, JudgeOperatorEnum.getByOpType(4));
            }

            @Test
            @DisplayName("opType 5 returns EQ")
            void opType5() {
                assertEquals(JudgeOperatorEnum.EQ, JudgeOperatorEnum.getByOpType(5));
            }

            @Test
            @DisplayName("opType 6 returns GT1LT2")
            void opType6() {
                assertEquals(JudgeOperatorEnum.GT1LT2, JudgeOperatorEnum.getByOpType(6));
            }

            @Test
            @DisplayName("opType 7 returns GTE1LT2")
            void opType7() {
                assertEquals(JudgeOperatorEnum.GTE1LT2, JudgeOperatorEnum.getByOpType(7));
            }

            @Test
            @DisplayName("opType 8 returns GET1LE2")
            void opType8() {
                assertEquals(JudgeOperatorEnum.GET1LE2, JudgeOperatorEnum.getByOpType(8));
            }

            @Test
            @DisplayName("opType 9 returns GTE1LTE2")
            void opType9() {
                assertEquals(JudgeOperatorEnum.GTE1LTE2, JudgeOperatorEnum.getByOpType(9));
            }

            @Test
            @DisplayName("throws AFBizException for unknown code")
            void unknownCode() {
                assertThrows(AFBizException.class, () -> JudgeOperatorEnum.getByOpType(999));
            }

            @Test
            @DisplayName("throws AFBizException for null code")
            void nullCode() {
                assertThrows(AFBizException.class, () -> JudgeOperatorEnum.getByOpType(null));
            }
        }

        @Nested
        @DisplayName("binaryOperator")
        class BinaryOperatorTest {

            @Test
            @DisplayName("returns [6, 7, 8, 9]")
            void returnsBinaryOperatorCodes() {
                List<Integer> result = JudgeOperatorEnum.binaryOperator();
                assertEquals(4, result.size());
                assertTrue(result.contains(6));
                assertTrue(result.contains(7));
                assertTrue(result.contains(8));
                assertTrue(result.contains(9));
            }

            @Test
            @DisplayName("does not contain single-value operator codes")
            void noSingleValueOperatorCodes() {
                List<Integer> result = JudgeOperatorEnum.binaryOperator();
                assertFalse(result.contains(1));
                assertFalse(result.contains(2));
                assertFalse(result.contains(3));
                assertFalse(result.contains(4));
                assertFalse(result.contains(5));
            }
        }

        @Nested
        @DisplayName("getCode")
        class GetCodeTest {

            @Test
            @DisplayName("GTE has code 1")
            void gte() {
                assertEquals(1, JudgeOperatorEnum.GTE.getCode());
            }

            @Test
            @DisplayName("GT has code 2")
            void gt() {
                assertEquals(2, JudgeOperatorEnum.GT.getCode());
            }

            @Test
            @DisplayName("LTE has code 3")
            void lte() {
                assertEquals(3, JudgeOperatorEnum.LTE.getCode());
            }

            @Test
            @DisplayName("LT has code 4")
            void lt() {
                assertEquals(4, JudgeOperatorEnum.LT.getCode());
            }

            @Test
            @DisplayName("EQ has code 5")
            void eq() {
                assertEquals(5, JudgeOperatorEnum.EQ.getCode());
            }

            @Test
            @DisplayName("GT1LT2 has code 6")
            void gt1lt2() {
                assertEquals(6, JudgeOperatorEnum.GT1LT2.getCode());
            }

            @Test
            @DisplayName("GTE1LT2 has code 7")
            void gte1lt2() {
                assertEquals(7, JudgeOperatorEnum.GTE1LT2.getCode());
            }

            @Test
            @DisplayName("GET1LE2 has code 8")
            void get1le2() {
                assertEquals(8, JudgeOperatorEnum.GET1LE2.getCode());
            }

            @Test
            @DisplayName("GTE1LTE2 has code 9")
            void gte1lte2() {
                assertEquals(9, JudgeOperatorEnum.GTE1LTE2.getCode());
            }
        }

        @Nested
        @DisplayName("getSymbol")
        class GetSymbolTest {

            @Test
            @DisplayName("GTE has symbol '>='")
            void gte() {
                assertEquals(">=", JudgeOperatorEnum.GTE.getSymbol());
            }

            @Test
            @DisplayName("GT has symbol '>'")
            void gt() {
                assertEquals(">", JudgeOperatorEnum.GT.getSymbol());
            }

            @Test
            @DisplayName("LTE has symbol '<='")
            void lte() {
                assertEquals("<=", JudgeOperatorEnum.LTE.getSymbol());
            }

            @Test
            @DisplayName("LT has symbol '<'")
            void lt() {
                assertEquals("<", JudgeOperatorEnum.LT.getSymbol());
            }

            @Test
            @DisplayName("EQ has symbol '='")
            void eq() {
                assertEquals("=", JudgeOperatorEnum.EQ.getSymbol());
            }

            @Test
            @DisplayName("GT1LT2 has symbol 'first<a<second'")
            void gt1lt2() {
                assertEquals("first<a<second", JudgeOperatorEnum.GT1LT2.getSymbol());
            }

            @Test
            @DisplayName("GTE1LT2 has symbol 'first<=a<second'")
            void gte1lt2() {
                assertEquals("first<=a<second", JudgeOperatorEnum.GTE1LT2.getSymbol());
            }

            @Test
            @DisplayName("GET1LE2 has symbol 'first<a<=second'")
            void get1le2() {
                assertEquals("first<a<=second", JudgeOperatorEnum.GET1LE2.getSymbol());
            }

            @Test
            @DisplayName("GTE1LTE2 has symbol 'first<=a<=second'")
            void gte1lte2() {
                assertEquals("first<=a<=second", JudgeOperatorEnum.GTE1LTE2.getSymbol());
            }
        }

        @Nested
        @DisplayName("getDesc")
        class GetDescTest {

            @Test
            @DisplayName("getDesc returns same as symbol for all values")
            void descEqualsSymbol() {
                for (JudgeOperatorEnum value : JudgeOperatorEnum.values()) {
                    assertEquals(value.getSymbol(), value.getDesc());
                }
            }
        }
    }

    @Nested
    @DisplayName("ConditionRelationShipEnum")
    class ConditionRelationShipEnumTest {

        @Nested
        @DisplayName("getCodeByValue")
        class GetCodeByValueTest {

            @Test
            @DisplayName("false returns 0 (AND)")
            void falseReturns0() {
                assertEquals(0, ConditionRelationShipEnum.getCodeByValue(false));
            }

            @Test
            @DisplayName("true returns 1 (OR)")
            void trueReturns1() {
                assertEquals(1, ConditionRelationShipEnum.getCodeByValue(true));
            }

            @Test
            @DisplayName("null returns 1 (default to OR)")
            void nullReturns1() {
                assertEquals(1, ConditionRelationShipEnum.getCodeByValue(null));
            }
        }

        @Nested
        @DisplayName("getValueByCode")
        class GetValueByCodeTest {

            @Test
            @DisplayName("code 0 returns false (AND)")
            void code0ReturnsFalse() {
                assertFalse(ConditionRelationShipEnum.getValueByCode(0));
            }

            @Test
            @DisplayName("code 1 returns true (OR)")
            void code1ReturnsTrue() {
                assertTrue(ConditionRelationShipEnum.getValueByCode(1));
            }

            @Test
            @DisplayName("null returns true (default to OR)")
            void nullReturnsTrue() {
                assertTrue(ConditionRelationShipEnum.getValueByCode(null));
            }

            @Test
            @DisplayName("999 returns true (unknown defaults to OR)")
            void unknownCodeReturnsTrue() {
                assertTrue(ConditionRelationShipEnum.getValueByCode(999));
            }
        }
    }
}
