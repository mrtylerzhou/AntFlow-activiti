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

class LFStringConditionJudgeTest extends BaseTest {

    private LFStringConditionJudge judge;

    @BeforeEach
    void setUp() {
        judge = new LFStringConditionJudge();
    }

    @Nested
    @DisplayName("case-insensitive string matching")
    class CaseInsensitiveMatchingTest {

        @Test
        @DisplayName("should match when strings are equal ignoring case")
        void shouldMatchWhenStringsEqualIgnoreCase() {
            BpmnNodeConditionsConfBaseVo conf = createConf("city", "BEIJING", 0);
            BpmnStartConditionsVo startConditions = createStartConditions("city", "beijing");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when strings are exactly equal")
        void shouldMatchWhenStringsExactlyEqual() {
            BpmnNodeConditionsConfBaseVo conf = createConf("city", "BEIJING", 0);
            BpmnStartConditionsVo startConditions = createStartConditions("city", "BEIJING");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when strings are different")
        void shouldNotMatchWhenStringsDifferent() {
            BpmnNodeConditionsConfBaseVo conf = createConf("city", "BEIJING", 0);
            BpmnStartConditionsVo startConditions = createStartConditions("city", "SHANGHAI");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match mixed case strings")
        void shouldMatchMixedCaseStrings() {
            BpmnNodeConditionsConfBaseVo conf = createConf("status", "Active", 0);
            BpmnStartConditionsVo startConditions = createStartConditions("status", "ACTIVE");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("multiple condition fields")
    class MultipleConditionFieldsTest {

        @Test
        @DisplayName("should return true when all fields match")
        void shouldReturnTrueWhenAllFieldsMatch() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new LinkedHashMap<>();
            dbConditions.put("city", "BEIJING");
            dbConditions.put("dept", "IT");
            groupedMap.put(0, dbConditions);
            conf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Arrays.asList(5, 5));
            conf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "beijing");
            lfConditions.put("dept", "it");
            startConditions.setLfConditions(lfConditions);

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when one field does not match")
        void shouldReturnFalseWhenOneFieldNotMatch() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new LinkedHashMap<>();
            dbConditions.put("city", "BEIJING");
            dbConditions.put("dept", "IT");
            groupedMap.put(0, dbConditions);
            conf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Arrays.asList(5, 5));
            conf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "beijing");
            lfConditions.put("dept", "HR");
            startConditions.setLfConditions(lfConditions);

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("null and empty conditions")
    class NullAndEmptyConditionsTest {

        @Test
        @DisplayName("should return false when groupedLfConditionsMap is null")
        void shouldReturnFalseWhenGroupedMapNull() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setGroupedLfConditionsMap(null);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when user lfConditions is null")
        void shouldReturnFalseWhenUserLfConditionsNull() {
            BpmnNodeConditionsConfBaseVo conf = createConf("city", "BEIJING", 0);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setLfConditions(null);

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when user value for key is null")
        void shouldReturnFalseWhenUserValueNull() {
            BpmnNodeConditionsConfBaseVo conf = createConf("city", "BEIJING", 0);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", null);
            startConditions.setLfConditions(lfConditions);

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw AFBizException when db value is null")
        void shouldThrowWhenDbValueNull() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", null);
            groupedMap.put(0, dbConditions);
            conf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "BEIJING");
            startConditions.setLfConditions(lfConditions);

            assertThrows(AFBizException.class,
                    () -> judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw AFBizException when condition group not found")
        void shouldThrowWhenGroupNotFound() {
            BpmnNodeConditionsConfBaseVo conf = createConf("city", "BEIJING", 0);
            BpmnStartConditionsVo startConditions = createStartConditions("city", "BEIJING");

            assertThrows(AFBizException.class,
                    () -> judge.judge("node1", conf, startConditions, 99));
        }
    }

    @Nested
    @DisplayName("numeric string comparison")
    class NumericStringComparisonTest {

        @Test
        @DisplayName("should match numeric strings case-insensitively")
        void shouldMatchNumericStrings() {
            BpmnNodeConditionsConfBaseVo conf = createConf("amount", "1000", 0);
            BpmnStartConditionsVo startConditions = createStartConditions("amount", "1000");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match different numeric strings")
        void shouldNotMatchDifferentNumericStrings() {
            BpmnNodeConditionsConfBaseVo conf = createConf("amount", "1000", 0);
            BpmnStartConditionsVo startConditions = createStartConditions("amount", "2000");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    private BpmnNodeConditionsConfBaseVo createConf(String key, Object dbValue, int group) {
        BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
        Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
        Map<String, Object> dbConditions = new HashMap<>();
        dbConditions.put(key, dbValue);
        groupedMap.put(group, dbConditions);
        conf.setGroupedLfConditionsMap(groupedMap);

        Map<Integer, List<Integer>> operatorMap = new HashMap<>();
        operatorMap.put(group, Collections.singletonList(5));
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
