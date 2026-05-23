package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LFDateConditionJudgeTest extends BaseTest {

    private LFDateConditionJudge judge;

    @BeforeEach
    void setUp() {
        judge = new LFDateConditionJudge();
    }

    @Nested
    @DisplayName("date equality matching")
    class DateEqualityTest {

        @Test
        @DisplayName("should match when dates are equal")
        void shouldMatchWhenDatesEqual() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-15", 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-01-15");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when dates are different")
        void shouldNotMatchWhenDatesDifferent() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-15", 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-06-20");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("date range matching")
    class DateRangeTest {

        @Test
        @DisplayName("should match when user date is within left-open right-closed range (operator 8)")
        void shouldMatchWhenDateWithinRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01,2025-12-31", 8, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-06-15");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when user date is before range")
        void shouldNotMatchWhenDateBeforeRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-06-01,2025-12-31", 8, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-01-15");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when user date is after range")
        void shouldNotMatchWhenDateAfterRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01,2025-06-30", 8, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-12-15");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match left boundary in left-open right-closed range (operator 8)")
        void shouldNotMatchLeftBoundaryInOpenClosedRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01,2025-12-31", 8, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-01-01");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match right boundary in left-open right-closed range (operator 8)")
        void shouldMatchRightBoundaryInOpenClosedRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01,2025-12-31", 8, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-12-31");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match both boundaries in closed range (operator 9)")
        void shouldMatchBothBoundariesInClosedRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01,2025-12-31", 9, 0);

            BpmnStartConditionsVo startConditionsLeft = createStartConditions("dateField", "2025-01-01");
            BpmnStartConditionsVo startConditionsRight = createStartConditions("dateField", "2025-12-31");

            assertTrue(judge.judge("node1", conf, startConditionsLeft, 0));
            assertTrue(judge.judge("node1", conf, startConditionsRight, 0));
        }

        @Test
        @DisplayName("should match in open range excluding both boundaries (operator 6)")
        void shouldMatchInOpenRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01,2025-12-31", 6, 0);

            BpmnStartConditionsVo inside = createStartConditions("dateField", "2025-06-15");
            BpmnStartConditionsVo leftBoundary = createStartConditions("dateField", "2025-01-01");
            BpmnStartConditionsVo rightBoundary = createStartConditions("dateField", "2025-12-31");

            assertTrue(judge.judge("node1", conf, inside, 0));
            assertFalse(judge.judge("node1", conf, leftBoundary, 0));
            assertFalse(judge.judge("node1", conf, rightBoundary, 0));
        }

        @Test
        @DisplayName("should match in left-closed right-open range (operator 7)")
        void shouldMatchInLeftClosedRightOpenRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01,2025-12-31", 7, 0);

            BpmnStartConditionsVo inside = createStartConditions("dateField", "2025-06-15");
            BpmnStartConditionsVo leftBoundary = createStartConditions("dateField", "2025-01-01");
            BpmnStartConditionsVo rightBoundary = createStartConditions("dateField", "2025-12-31");

            assertTrue(judge.judge("node1", conf, inside, 0));
            assertTrue(judge.judge("node1", conf, leftBoundary, 0));
            assertFalse(judge.judge("node1", conf, rightBoundary, 0));
        }
    }

    @Nested
    @DisplayName("date comparison operators")
    class DateComparisonOperatorsTest {

        @Test
        @DisplayName("should match when user date is greater than db date (GTE operator)")
        void shouldMatchWhenDateGreaterThanOrEqual() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01", 1, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-06-15");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when user date equals db date (GTE operator)")
        void shouldMatchWhenDateEqualForGTE() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01", 1, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-01-01");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when user date is less than db date (GTE operator)")
        void shouldNotMatchWhenDateLessForGTE() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-06-01", 1, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-01-15");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when user date is less than db date (LTE operator)")
        void shouldMatchWhenDateLessThanOrEqual() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-12-31", 3, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-06-15");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when user date equals db date (LTE operator)")
        void shouldMatchWhenDateEqualForLTE() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-06-15", 3, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-06-15");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when user date is greater than db date (LTE operator)")
        void shouldNotMatchWhenDateGreaterForLTE() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01", 3, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-12-31");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when user date is greater than db date (GT operator)")
        void shouldMatchWhenDateGreaterThan() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01", 2, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-06-15");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when user date equals db date (GT operator)")
        void shouldNotMatchWhenDateEqualForGT() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01", 2, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-01-01");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when user date is less than db date (LT operator)")
        void shouldMatchWhenDateLessThan() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-12-31", 4, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-06-15");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when user date equals db date (LT operator)")
        void shouldNotMatchWhenDateEqualForLT() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-06-15", 4, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-06-15");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("null and error conditions")
    class NullAndErrorConditionsTest {

        @Test
        @DisplayName("should return false when groupedLfConditionsMap is null")
        void shouldReturnFalseWhenGroupedMapNull() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setGroupedLfConditionsMap(null);

            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-01-01");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when user lfConditions is null")
        void shouldReturnFalseWhenUserLfConditionsNull() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01", 5, 0);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw when db date value is invalid")
        void shouldThrowWhenDbDateInvalid() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "not-a-date", 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "2025-01-01");

            assertThrows(Exception.class,
                    () -> judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw when user date value is invalid")
        void shouldThrowWhenUserDateInvalid() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dateField", "2025-01-01", 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dateField", "not-a-date");

            assertThrows(Exception.class,
                    () -> judge.judge("node1", conf, startConditions, 0));
        }
    }

    private BpmnNodeConditionsConfBaseVo createDateConf(String key, Object dbValue, int operator, int group) {
        BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
        Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
        Map<String, Object> dbConditions = new HashMap<>();
        dbConditions.put(key, dbValue);
        groupedMap.put(group, dbConditions);
        conf.setGroupedLfConditionsMap(groupedMap);

        Map<Integer, List<Integer>> operatorMap = new HashMap<>();
        operatorMap.put(group, Collections.singletonList(operator));
        conf.setGroupedNumberOperatorListMap(operatorMap);
        return conf;
    }

    private BpmnStartConditionsVo createStartConditions(String key, Object userValue) {
        BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
        Map<String, Object> lfConditions = new HashMap<>();
        lfConditions.put(key, userValue);
        startConditions.setLfConditions(lfConditions);
        return startConditions;
    }
}
