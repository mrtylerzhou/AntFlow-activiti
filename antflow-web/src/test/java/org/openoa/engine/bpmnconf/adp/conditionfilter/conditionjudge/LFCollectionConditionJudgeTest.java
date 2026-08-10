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

class LFCollectionConditionJudgeTest extends BaseTest {

    private LFCollectionConditionJudge judge;

    @BeforeEach
    void setUp() {
        judge = new LFCollectionConditionJudge();
    }

    @Nested
    @DisplayName("Single value matching")
    class SingleValueMatching {
        @Test
        @DisplayName("should return true when single user value matches an element in db collection")
        void shouldReturnTrueWhenSingleValueMatches() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", Arrays.asList("BEIJING", "SHANGHAI", "GUANGZHOU"));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "SHANGHAI");
            startConditions.setLfConditions(lfConditions);

            boolean result = judge.judge("node1", conditionsConf, startConditions, 0);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when single user value does not match any element in db collection")
        void shouldReturnFalseWhenSingleValueNotMatches() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", Arrays.asList("BEIJING", "SHANGHAI", "GUANGZHOU"));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "CHENGDU");
            startConditions.setLfConditions(lfConditions);

            boolean result = judge.judge("node1", conditionsConf, startConditions, 0);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Collection value matching")
    class CollectionValueMatching {
        @Test
        @DisplayName("should return true when user collection has at least one element matching db collection")
        void shouldReturnTrueWhenCollectionHasMatch() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", Arrays.asList("BEIJING", "SHANGHAI", "GUANGZHOU"));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", Arrays.asList("CHENGDU", "SHANGHAI"));
            startConditions.setLfConditions(lfConditions);

            boolean result = judge.judge("node1", conditionsConf, startConditions, 0);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when user collection has no element matching db collection")
        void shouldReturnFalseWhenCollectionHasNoMatch() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", Arrays.asList("BEIJING", "SHANGHAI", "GUANGZHOU"));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", Arrays.asList("CHENGDU", "WUHAN"));
            startConditions.setLfConditions(lfConditions);

            boolean result = judge.judge("node1", conditionsConf, startConditions, 0);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Error conditions")
    class ErrorConditions {
        @Test
        @DisplayName("should throw AFBizException when db value is not iterable")
        void shouldThrowWhenDbValueNotIterable() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", "NOT_ITERABLE");
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "BEIJING");
            startConditions.setLfConditions(lfConditions);

            assertThrows(AFBizException.class, () ->
                    judge.judge("node1", conditionsConf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("Null and empty conditions")
    class NullAndEmptyConditions {
        @Test
        @DisplayName("should return false when groupedLfConditionsMap is null")
        void shouldReturnFalseWhenGroupedMapNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            boolean result = judge.judge("node1", conditionsConf, startConditions, 0);

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when user lfConditions is null")
        void shouldReturnFalseWhenUserLfConditionsNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", Arrays.asList("BEIJING"));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            boolean result = judge.judge("node1", conditionsConf, startConditions, 0);

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when db collection is empty")
        void shouldReturnFalseWhenDbCollectionEmpty() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", Collections.emptyList());
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "BEIJING");
            startConditions.setLfConditions(lfConditions);

            boolean result = judge.judge("node1", conditionsConf, startConditions, 0);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Multiple conditions in one group")
    class MultipleConditionsInGroup {
        @Test
        @DisplayName("should return true when all conditions in one group match")
        void shouldReturnTrueWhenAllConditionsMatch() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", Arrays.asList("BEIJING", "SHANGHAI"));
            dbConditions.put("dept", Arrays.asList("IT", "HR"));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Arrays.asList(5, 5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "BEIJING");
            lfConditions.put("dept", "HR");
            startConditions.setLfConditions(lfConditions);

            boolean result = judge.judge("node1", conditionsConf, startConditions, 0);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when one condition in group does not match")
        void shouldReturnFalseWhenOneConditionNotMatch() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", Arrays.asList("BEIJING", "SHANGHAI"));
            dbConditions.put("dept", Arrays.asList("IT", "HR"));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Arrays.asList(5, 5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "BEIJING");
            lfConditions.put("dept", "FINANCE");
            startConditions.setLfConditions(lfConditions);

            boolean result = judge.judge("node1", conditionsConf, startConditions, 0);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("String comparison via toString")
    class StringComparison {
        @Test
        @DisplayName("should match using toString().equals() comparison")
        void shouldMatchUsingToStringEquals() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("count", Arrays.asList(100, 200, 300));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("count", "200");
            startConditions.setLfConditions(lfConditions);

            boolean result = judge.judge("node1", conditionsConf, startConditions, 0);

            assertTrue(result);
        }

        @Test
        @DisplayName("should not match when toString values differ")
        void shouldNotMatchWhenToStringDiffers() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("count", Arrays.asList(100, 200, 300));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("count", "999");
            startConditions.setLfConditions(lfConditions);

            boolean result = judge.judge("node1", conditionsConf, startConditions, 0);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Multiple groups")
    class MultipleGroupsTest {

        @Test
        @DisplayName("should throw AFBizException when condition group does not exist")
        void shouldThrowWhenGroupNotExist() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", Arrays.asList("BEIJING"));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "BEIJING");
            startConditions.setLfConditions(lfConditions);

            assertThrows(AFBizException.class,
                    () -> judge.judge("node1", conditionsConf, startConditions, 1));
        }

        @Test
        @DisplayName("should return true when correct group is specified")
        void shouldReturnTrueWhenCorrectGroupSpecified() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", Arrays.asList("BEIJING"));
            groupedMap.put(2, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(2, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "BEIJING");
            startConditions.setLfConditions(lfConditions);

            boolean result = judge.judge("node1", conditionsConf, startConditions, 2);

            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("User value type variations")
    class UserValueTypeVariationsTest {

        @Test
        @DisplayName("should match when user value is Integer and db contains same value as string")
        void shouldMatchIntegerWithDbString() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("status", Arrays.asList("1", "2", "3"));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("status", 2);
            startConditions.setLfConditions(lfConditions);

            boolean result = judge.judge("node1", conditionsConf, startConditions, 0);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when user value is null")
        void shouldReturnFalseWhenUserValueNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", Arrays.asList("BEIJING"));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", null);
            startConditions.setLfConditions(lfConditions);

            boolean result = judge.judge("node1", conditionsConf, startConditions, 0);

            assertFalse(result);
        }

        @Test
        @DisplayName("should throw AFBizException when db value is null")
        void shouldThrowWhenDbValueNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", null);
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "BEIJING");
            startConditions.setLfConditions(lfConditions);

            assertThrows(AFBizException.class,
                    () -> judge.judge("node1", conditionsConf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("Operator list variations")
    class OperatorListVariationsTest {

        @Test
        @DisplayName("should throw NullPointerException when groupedNumberOperatorListMap is null")
        void shouldThrowNPEWhenOperatorMapNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", Arrays.asList("BEIJING"));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "BEIJING");
            startConditions.setLfConditions(lfConditions);

            assertThrows(NullPointerException.class,
                    () -> judge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw IndexOutOfBoundsException when operator list for group is empty")
        void shouldThrowIndexOutOfBoundsWhenOperatorListEmpty() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", Arrays.asList("BEIJING"));
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.emptyList());
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "BEIJING");
            startConditions.setLfConditions(lfConditions);

            assertThrows(IndexOutOfBoundsException.class,
                    () -> judge.judge("node1", conditionsConf, startConditions, 0));
        }
    }
}
