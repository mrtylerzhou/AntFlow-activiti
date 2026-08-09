package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LFDateTimeConditionJudgeTest extends BaseTest {

    private LFDateTimeConditionJudge judge;

    @BeforeEach
    void setUp() {
        judge = new LFDateTimeConditionJudge();
    }

    @Nested
    @DisplayName("datetime equality matching")
    class DateTimeEqualityTest {

        @Test
        @DisplayName("should match when datetimes are equal")
        void shouldMatchWhenDatetimesEqual() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 10:30:00", 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 10:30:00");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when datetimes differ by seconds")
        void shouldNotMatchWhenDatetimesDifferBySeconds() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 10:30:00", 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 10:30:01");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when datetimes differ by date")
        void shouldNotMatchWhenDatetimesDifferByDate() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 10:30:00", 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-16 10:30:00");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("datetime comparison operators")
    class DateTimeComparisonOperatorsTest {

        @Test
        @DisplayName("should match when user datetime >= db datetime (GTE)")
        void shouldMatchWhenGTE() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 10:30:00", 1, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 10:30:01");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when user datetime == db datetime (GTE)")
        void shouldMatchWhenEqualForGTE() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 10:30:00", 1, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 10:30:00");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when user datetime < db datetime (GTE)")
        void shouldNotMatchWhenLessForGTE() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 10:30:00", 1, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 10:29:59");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when user datetime <= db datetime (LTE)")
        void shouldMatchWhenLTE() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 10:30:00", 3, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 10:29:59");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when user datetime > db datetime (GT)")
        void shouldMatchWhenGT() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 10:30:00", 2, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 10:30:01");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when user datetime == db datetime (GT)")
        void shouldNotMatchWhenEqualForGT() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 10:30:00", 2, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 10:30:00");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when user datetime < db datetime (LT)")
        void shouldMatchWhenLT() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 10:30:00", 4, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 10:29:59");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("datetime range matching")
    class DateTimeRangeTest {

        @Test
        @DisplayName("should match when datetime within closed range (operator 9)")
        void shouldMatchWhenWithinClosedRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 00:00:00,2025-06-15 23:59:59", 9, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 12:00:00");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when datetime before range")
        void shouldNotMatchWhenBeforeRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 08:00:00,2025-06-15 18:00:00", 9, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 07:59:59");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when datetime after range")
        void shouldNotMatchWhenAfterRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 08:00:00,2025-06-15 18:00:00", 9, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 18:00:01");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match left boundary in closed range (operator 9)")
        void shouldMatchLeftBoundaryInClosedRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 08:00:00,2025-06-15 18:00:00", 9, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 08:00:00");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match right boundary in closed range (operator 9)")
        void shouldMatchRightBoundaryInClosedRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 08:00:00,2025-06-15 18:00:00", 9, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 18:00:00");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match left boundary in open range (operator 6)")
        void shouldNotMatchLeftBoundaryInOpenRange() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 08:00:00,2025-06-15 18:00:00", 6, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 08:00:00");

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

            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 10:30:00");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw when db datetime value is invalid")
        void shouldThrowWhenDbDatetimeInvalid() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "not-a-datetime", 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "2025-06-15 10:30:00");

            assertThrows(Exception.class, () -> judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw when user datetime value is invalid")
        void shouldThrowWhenUserDatetimeInvalid() {
            BpmnNodeConditionsConfBaseVo conf = createDateConf("dtField", "2025-06-15 10:30:00", 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("dtField", "invalid");

            assertThrows(Exception.class, () -> judge.judge("node1", conf, startConditions, 0));
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
