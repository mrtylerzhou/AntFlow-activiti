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

class LFCollectionConditionJudgeExtendedTest extends BaseTest {

    private LFCollectionConditionJudge judge;

    @BeforeEach
    void setUp() {
        judge = new LFCollectionConditionJudge();
    }

    @Nested
    @DisplayName("single value matching against collection")
    class SingleValueMatchingTest {

        @Test
        @DisplayName("should match when single value exists in db collection")
        void shouldMatchWhenSingleValueInCollection() {
            BpmnNodeConditionsConfBaseVo conf = createCollectionConf("tags", Arrays.asList("java", "python", "go"), 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("tags", "python");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when single value not in db collection")
        void shouldNotMatchWhenSingleValueNotInCollection() {
            BpmnNodeConditionsConfBaseVo conf = createCollectionConf("tags", Arrays.asList("java", "python", "go"), 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("tags", "rust");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("collection vs collection matching")
    class CollectionVsCollectionTest {

        @Test
        @DisplayName("should match when user collection has overlapping element")
        void shouldMatchWhenOverlappingElement() {
            BpmnNodeConditionsConfBaseVo conf = createCollectionConf("tags", Arrays.asList("java", "python"), 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("tags", Arrays.asList("python", "rust"));

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when collections have no overlap")
        void shouldNotMatchWhenNoOverlap() {
            BpmnNodeConditionsConfBaseVo conf = createCollectionConf("tags", Arrays.asList("java", "python"), 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("tags", Arrays.asList("rust", "c++"));

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("string comparison in collection")
    class StringComparisonTest {

        @Test
        @DisplayName("should match using toString comparison")
        void shouldMatchUsingToString() {
            BpmnNodeConditionsConfBaseVo conf = createCollectionConf("nums", Arrays.asList(1, 2, 3), 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("nums", "2");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match number value against number string")
        void shouldMatchNumberAgainstNumberString() {
            BpmnNodeConditionsConfBaseVo conf = createCollectionConf("nums", Arrays.asList("10", "20", "30"), 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("nums", 20);

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("error conditions")
    class ErrorConditionsTest {

        @Test
        @DisplayName("should throw when db value is not iterable")
        void shouldThrowWhenDbValueNotIterable() {
            BpmnNodeConditionsConfBaseVo conf = createNonIterableConf("field", "not-a-collection", 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("field", "value");

            assertThrows(AFBizException.class, () -> judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when groupedLfConditionsMap is null")
        void shouldReturnFalseWhenGroupedMapNull() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setGroupedLfConditionsMap(null);

            BpmnStartConditionsVo startConditions = createStartConditions("field", "value");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when user lfConditions is null")
        void shouldReturnFalseWhenUserLfConditionsNull() {
            BpmnNodeConditionsConfBaseVo conf = createCollectionConf("field", Arrays.asList("a", "b"), 5, 0);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when user value is null for key")
        void shouldReturnFalseWhenUserValueNullForKey() {
            BpmnNodeConditionsConfBaseVo conf = createCollectionConf("field", Arrays.asList("a", "b"), 5, 0);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("otherField", "value");
            startConditions.setLfConditions(lfConditions);

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("empty collection edge cases")
    class EmptyCollectionTest {

        @Test
        @DisplayName("should not match when db collection is empty")
        void shouldNotMatchWhenDbCollectionEmpty() {
            BpmnNodeConditionsConfBaseVo conf = createCollectionConf("tags", Collections.emptyList(), 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("tags", "python");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when user collection is empty")
        void shouldNotMatchWhenUserCollectionEmpty() {
            BpmnNodeConditionsConfBaseVo conf = createCollectionConf("tags", Arrays.asList("java", "python"), 5, 0);
            BpmnStartConditionsVo startConditions = createStartConditions("tags", Collections.emptyList());

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    private BpmnNodeConditionsConfBaseVo createCollectionConf(String key, Object dbValue, int operator, int group) {
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

    private BpmnNodeConditionsConfBaseVo createNonIterableConf(String key, Object dbValue, int operator, int group) {
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
