package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.base.exception.AFBizException;
import org.openoa.BaseTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AbstractComparableJudgeTest extends BaseTest {

    private TestableComparableJudge judge;

    static class TestableComparableJudge extends AbstractComparableJudge {
        public boolean testCompare(BigDecimal confTotal, BigDecimal confTotal2, BigDecimal actual, Integer operator) {
            return compareJudge(confTotal, confTotal2, actual, operator);
        }

        @Override
        public boolean judge(String nodeId, org.openoa.base.vo.BpmnNodeConditionsConfBaseVo conditionsConf, org.openoa.base.vo.BpmnStartConditionsVo bpmnStartConditionsVo, int couDGroup) {
            return false;
        }
    }

    @BeforeEach
    void setUp() {
        judge = new TestableComparableJudge();
    }

    @Nested
    @DisplayName("operator 1: >= (greater than or equal)")
    class GreaterThanOrEqualTest {
        @Test
        @DisplayName("should return true when actual >= confTotal")
        void shouldReturnTrueWhenGreaterOrEqual() {
            assertTrue(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("100"), 1));
            assertTrue(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("200"), 1));
        }

        @Test
        @DisplayName("should return false when actual < confTotal")
        void shouldReturnFalseWhenLess() {
            assertFalse(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("50"), 1));
        }
    }

    @Nested
    @DisplayName("operator 2: > (greater than)")
    class GreaterThanTest {
        @Test
        @DisplayName("should return true when actual > confTotal")
        void shouldReturnTrueWhenGreater() {
            assertTrue(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("200"), 2));
        }

        @Test
        @DisplayName("should return false when actual <= confTotal")
        void shouldReturnFalseWhenEqualOrLess() {
            assertFalse(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("100"), 2));
            assertFalse(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("50"), 2));
        }
    }

    @Nested
    @DisplayName("operator 3: <= (less than or equal)")
    class LessThanOrEqualTest {
        @Test
        @DisplayName("should return true when actual <= confTotal")
        void shouldReturnTrueWhenLessOrEqual() {
            assertTrue(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("100"), 3));
            assertTrue(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("50"), 3));
        }

        @Test
        @DisplayName("should return false when actual > confTotal")
        void shouldReturnFalseWhenGreater() {
            assertFalse(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("200"), 3));
        }
    }

    @Nested
    @DisplayName("operator 4: < (less than)")
    class LessThanTest {
        @Test
        @DisplayName("should return true when actual < confTotal")
        void shouldReturnTrueWhenLess() {
            assertTrue(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("50"), 4));
        }

        @Test
        @DisplayName("should return false when actual >= confTotal")
        void shouldReturnFalseWhenEqualOrGreater() {
            assertFalse(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("100"), 4));
            assertFalse(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("200"), 4));
        }
    }

    @Nested
    @DisplayName("operator 5: == (equal)")
    class EqualTest {
        @Test
        @DisplayName("should return true when actual == confTotal")
        void shouldReturnTrueWhenEqual() {
            assertTrue(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("100"), 5));
        }

        @Test
        @DisplayName("should return false when actual != confTotal")
        void shouldReturnFalseWhenNotEqual() {
            assertFalse(judge.testCompare(new BigDecimal("100"), null, new BigDecimal("99"), 5));
        }
    }

    @Nested
    @DisplayName("operator 6: > and < (open interval)")
    class OpenIntervalTest {
        @Test
        @DisplayName("should return true when confTotal < actual < confTotal2")
        void shouldReturnTrueInOpenInterval() {
            assertTrue(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("50"), 6));
        }

        @Test
        @DisplayName("should return false when actual equals boundary")
        void shouldReturnFalseAtBoundary() {
            assertFalse(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("10"), 6));
            assertFalse(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("100"), 6));
        }

        @Test
        @DisplayName("should return false when outside interval")
        void shouldReturnFalseOutsideInterval() {
            assertFalse(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("5"), 6));
            assertFalse(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("200"), 6));
        }
    }

    @Nested
    @DisplayName("operator 7: >= and < (left-closed right-open)")
    class LeftClosedRightOpenTest {
        @Test
        @DisplayName("should return true when confTotal <= actual < confTotal2")
        void shouldReturnTrueInRange() {
            assertTrue(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("10"), 7));
            assertTrue(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("50"), 7));
        }

        @Test
        @DisplayName("should return false when actual >= confTotal2")
        void shouldReturnFalseAtUpperBound() {
            assertFalse(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("100"), 7));
        }
    }

    @Nested
    @DisplayName("operator 8: > and <= (left-open right-closed)")
    class LeftOpenRightClosedTest {
        @Test
        @DisplayName("should return true when confTotal < actual <= confTotal2")
        void shouldReturnTrueInRange() {
            assertTrue(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("100"), 8));
            assertTrue(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("50"), 8));
        }

        @Test
        @DisplayName("should return false when actual <= confTotal")
        void shouldReturnFalseAtLowerBound() {
            assertFalse(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("10"), 8));
        }
    }

    @Nested
    @DisplayName("operator 9: >= and <= (closed interval)")
    class ClosedIntervalTest {
        @Test
        @DisplayName("should return true when confTotal <= actual <= confTotal2")
        void shouldReturnTrueInClosedInterval() {
            assertTrue(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("10"), 9));
            assertTrue(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("100"), 9));
            assertTrue(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("50"), 9));
        }

        @Test
        @DisplayName("should return false when outside closed interval")
        void shouldReturnFalseOutsideInterval() {
            assertFalse(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("5"), 9));
            assertFalse(judge.testCompare(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("200"), 9));
        }
    }

    @Nested
    @DisplayName("edge cases")
    class EdgeCasesTest {
        @Test
        @DisplayName("should return false when confTotal is null")
        void shouldReturnFalseWhenConfTotalNull() {
            assertFalse(judge.testCompare(null, null, new BigDecimal("100"), 1));
        }

        @Test
        @DisplayName("should return false when actual is null")
        void shouldReturnFalseWhenActualNull() {
            assertFalse(judge.testCompare(new BigDecimal("100"), null, null, 1));
        }

        @Test
        @DisplayName("should throw exception when operator is null")
        void shouldThrowWhenOperatorNull() {
            assertThrows(AFBizException.class, () ->
                    judge.testCompare(new BigDecimal("100"), null, new BigDecimal("100"), null));
        }

        @Test
        @DisplayName("should throw exception for unsupported operator")
        void shouldThrowForUnsupportedOperator() {
            assertThrows(AFBizException.class, () ->
                    judge.testCompare(new BigDecimal("100"), null, new BigDecimal("100"), 99));
        }

        @Test
        @DisplayName("should handle decimal comparison correctly")
        void shouldHandleDecimalComparison() {
            assertTrue(judge.testCompare(new BigDecimal("100.50"), null, new BigDecimal("100.50"), 5));
            assertFalse(judge.testCompare(new BigDecimal("100.50"), null, new BigDecimal("100.51"), 5));
        }

        @Test
        @DisplayName("should handle zero comparison")
        void shouldHandleZeroComparison() {
            assertTrue(judge.testCompare(new BigDecimal("0"), null, new BigDecimal("0"), 5));
            assertTrue(judge.testCompare(new BigDecimal("0"), null, new BigDecimal("1"), 2));
        }
    }
}
