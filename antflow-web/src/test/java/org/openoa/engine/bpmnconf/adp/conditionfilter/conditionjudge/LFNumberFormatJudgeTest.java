package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.BaseTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LFNumberFormatJudgeTest extends BaseTest {

    private LFNumberFormatJudge judge;

    @BeforeEach
    void setUp() {
        judge = new LFNumberFormatJudge();
    }

    private BpmnNodeConditionsConfBaseVo buildConf(Map<String, Object> dbConditions, List<Integer> operators) {
        return buildConf(dbConditions, operators, 0);
    }

    private BpmnNodeConditionsConfBaseVo buildConf(Map<String, Object> dbConditions, List<Integer> operators, int group) {
        BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
        Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
        groupedMap.put(group, dbConditions);
        conf.setGroupedLfConditionsMap(groupedMap);

        Map<Integer, List<Integer>> operatorMap = new HashMap<>();
        operatorMap.put(group, operators);
        conf.setGroupedNumberOperatorListMap(operatorMap);
        return conf;
    }

    private BpmnStartConditionsVo buildStartConditions(Map<String, Object> lfConditions) {
        BpmnStartConditionsVo vo = new BpmnStartConditionsVo();
        vo.setLfConditions(lfConditions);
        return vo;
    }

    @Nested
    @DisplayName("Number equality (operator 5)")
    class NumberEqualityTest {
        @Test
        @DisplayName("should return true when numbers are equal")
        void shouldMatchWhenEqual() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "100");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when numbers are not equal")
        void shouldNotMatchWhenNotEqual() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "200");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Number greater than (operator 2)")
    class NumberGreaterThanTest {
        @Test
        @DisplayName("should return true when user value is greater than db value")
        void shouldMatchWhenGreater() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(2));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "200");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when user value is not greater than db value")
        void shouldNotMatchWhenNotGreater() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(2));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "50");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Number less than (operator 4)")
    class NumberLessThanTest {
        @Test
        @DisplayName("should return true when user value is less than db value")
        void shouldMatchWhenLess() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(4));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "50");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when user value is not less than db value")
        void shouldNotMatchWhenNotLess() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(4));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "200");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Number greater than or equal (operator 1)")
    class NumberGreaterThanOrEqualTest {
        @Test
        @DisplayName("should return true when user value equals db value at boundary")
        void shouldMatchAtBoundary() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(1));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "100");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return true when user value is greater than db value")
        void shouldMatchWhenGreater() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(1));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "150");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when user value is less than db value")
        void shouldNotMatchWhenLess() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(1));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "99");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Number less than or equal (operator 3)")
    class NumberLessThanOrEqualTest {
        @Test
        @DisplayName("should return true when user value equals db value at boundary")
        void shouldMatchAtBoundary() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(3));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "100");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return true when user value is less than db value")
        void shouldMatchWhenLess() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(3));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "50");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when user value is greater than db value")
        void shouldNotMatchWhenGreater() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(3));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "101");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Number range open interval (operator 6)")
    class NumberRangeOpenIntervalTest {
        @Test
        @DisplayName("should return true when user value is within open interval")
        void shouldMatchWhenWithinOpenInterval() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "10,100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(6));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "50");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when user value is below open interval")
        void shouldNotMatchWhenBelowOpenInterval() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "10,100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(6));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "5");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when user value equals lower bound of open interval")
        void shouldNotMatchAtLowerBound() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "10,100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(6));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "10");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when user value equals upper bound of open interval")
        void shouldNotMatchAtUpperBound() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "10,100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(6));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "100");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Number range closed interval (operator 9)")
    class NumberRangeClosedIntervalTest {
        @Test
        @DisplayName("should return true when user value equals lower bound of closed interval")
        void shouldMatchAtLowerBound() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "10,100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(9));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "10");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return true when user value equals upper bound of closed interval")
        void shouldMatchAtUpperBound() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "10,100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(9));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "100");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return true when user value is within closed interval")
        void shouldMatchWhenWithinClosedInterval() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "10,100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(9));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "55");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when user value is outside closed interval")
        void shouldNotMatchWhenOutsideClosedInterval() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "10,100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(9));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "5");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Boolean value true (operator 5)")
    class BooleanTrueTest {
        @Test
        @DisplayName("should return true when db is true and user is true")
        void shouldMatchWhenBothTrue() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("flag", "true");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("flag", "true");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Boolean value false (operator 5)")
    class BooleanFalseTest {
        @Test
        @DisplayName("should return true when db is false and user is false")
        void shouldMatchWhenBothFalse() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("flag", "false");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("flag", "false");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Boolean vs number mismatch")
    class BooleanMismatchTest {
        @Test
        @DisplayName("should return false when db is true and user is false")
        void shouldNotMatchWhenDbTrueUserFalse() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("flag", "true");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("flag", "false");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when db is false and user is true")
        void shouldNotMatchWhenDbFalseUserTrue() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("flag", "false");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("flag", "true");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Boolean range")
    class BooleanRangeTest {
        @Test
        @DisplayName("should return true when db is false,true and user is true (0<1)")
        void shouldMatchWhenBooleanRangeContainsUserValue() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("flag", "false,true");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(9));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("flag", "true");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when db is true,true and user is false (1<1 is false for open interval)")
        void shouldNotMatchWhenBooleanRangeOpenIntervalExcludesUserValue() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("flag", "true,true");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(6));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("flag", "false");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Null groupedLfConditionsMap")
    class NullGroupedMapTest {
        @Test
        @DisplayName("should return false when groupedLfConditionsMap is null")
        void shouldReturnFalseWhenGroupedMapNull() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            BpmnStartConditionsVo startVo = new BpmnStartConditionsVo();

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Null user lfConditions")
    class NullUserLfConditionsTest {
        @Test
        @DisplayName("should return false when user lfConditions is null")
        void shouldReturnFalseWhenUserLfConditionsNull() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            BpmnStartConditionsVo startVo = new BpmnStartConditionsVo();

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when user lfConditions is empty")
        void shouldReturnFalseWhenUserLfConditionsEmpty() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            BpmnStartConditionsVo startVo = buildStartConditions(new HashMap<>());

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Multiple conditions in one group")
    class MultipleConditionsTest {
        @Test
        @DisplayName("should return true when all conditions match")
        void shouldMatchWhenAllConditionsMatch() {
            Map<String, Object> dbConditions = new LinkedHashMap<>();
            dbConditions.put("amount", "100");
            dbConditions.put("quantity", "5");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Arrays.asList(5, 2));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "100");
            userConditions.put("quantity", "10");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when one condition does not match")
        void shouldNotMatchWhenOneConditionFails() {
            Map<String, Object> dbConditions = new LinkedHashMap<>();
            dbConditions.put("amount", "100");
            dbConditions.put("quantity", "5");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Arrays.asList(5, 2));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "100");
            userConditions.put("quantity", "3");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when user value for a key is null")
        void shouldNotMatchWhenUserValueNullForKey() {
            Map<String, Object> dbConditions = new LinkedHashMap<>();
            dbConditions.put("amount", "100");
            dbConditions.put("quantity", "5");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Arrays.asList(5, 2));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "100");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Decimal number comparison")
    class DecimalComparisonTest {
        @Test
        @DisplayName("should return true when decimal numbers are equal")
        void shouldMatchWhenDecimalsEqual() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "99.99");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "99.99");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return true when user decimal is greater than db decimal")
        void shouldMatchWhenDecimalGreater() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "99.50");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(2));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "99.99");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should handle decimal within closed interval")
        void shouldMatchDecimalWithinClosedInterval() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "10.5,99.5");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(9));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "55.25");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should scale user decimal value to 2 decimal places - 100.005 rounds to 100.01")
        void shouldScaleUserDecimalValue() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100.01");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "100.005");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Negative number comparison")
    class NegativeNumberTest {
        @Test
        @DisplayName("should return true when negative numbers are equal")
        void shouldMatchNegativeEqual() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "-50");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "-50");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return true when user negative is greater than db negative")
        void shouldMatchNegativeGreater() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "-100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(2));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "-50");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return true when user negative is less than db negative")
        void shouldMatchNegativeLess() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "-50");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(4));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "-100");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Zero value comparison")
    class ZeroValueTest {
        @Test
        @DisplayName("should return true when both values are zero")
        void shouldMatchZeroEqual() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "0");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "0");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return true when zero is greater than negative")
        void shouldMatchZeroGreaterThanNegative() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "-1");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(2));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "0");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Large number comparison")
    class LargeNumberTest {
        @Test
        @DisplayName("should handle very large numbers correctly")
        void shouldHandleLargeNumbers() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "999999999.99");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "999999999.99");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should return false when large numbers differ slightly")
        void shouldNotMatchWhenLargeNumbersDiffer() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "999999999.99");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5));

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "999999999.98");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertFalse(judge.judge("node1", conf, startVo, 0));
        }
    }

    @Nested
    @DisplayName("Multiple condition groups")
    class MultipleGroupsTest {
        @Test
        @DisplayName("should evaluate group 0 correctly")
        void shouldEvaluateGroup0() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5), 0);

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "100");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 0));
        }

        @Test
        @DisplayName("should evaluate group 1 correctly")
        void shouldEvaluateGroup1() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "200");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5), 1);

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "200");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertTrue(judge.judge("node1", conf, startVo, 1));
        }

        @Test
        @DisplayName("should throw AFBizException when group does not exist")
        void shouldThrowWhenGroupNotExists() {
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("amount", "100");
            BpmnNodeConditionsConfBaseVo conf = buildConf(dbConditions, Collections.singletonList(5), 0);

            Map<String, Object> userConditions = new HashMap<>();
            userConditions.put("amount", "100");
            BpmnStartConditionsVo startVo = buildStartConditions(userConditions);

            assertThrows(org.openoa.base.exception.AFBizException.class, () ->
                    judge.judge("node1", conf, startVo, 99));
        }
    }
}
