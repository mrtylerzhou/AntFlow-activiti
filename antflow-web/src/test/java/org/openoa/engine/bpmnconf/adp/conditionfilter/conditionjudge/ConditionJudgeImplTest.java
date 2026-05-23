package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.exception.AFBizException;
import org.openoa.BaseTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ConditionJudgeImplTest extends BaseTest {

    private LFStringConditionJudge stringJudge;
    private JobLevelJudge jobLevelJudge;
    private MoneyOperatorJudge moneyOperatorJudge;

    @BeforeEach
    void setUp() {
        stringJudge = new LFStringConditionJudge();
        jobLevelJudge = new JobLevelJudge();
        moneyOperatorJudge = new MoneyOperatorJudge();
    }

    @Nested
    @DisplayName("LFStringConditionJudge")
    class LFStringConditionJudgeTest {
        @Test
        @DisplayName("should return true when string values match case-insensitively")
        void shouldMatchCaseInsensitively() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", "BEIJING");
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "beijing");
            startConditions.setLfConditions(lfConditions);

            boolean result = stringJudge.judge("node1", conditionsConf, startConditions, 0);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when string values do not match")
        void shouldReturnFalseWhenNotMatch() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", "SHANGHAI");
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            Map<String, Object> lfConditions = new HashMap<>();
            lfConditions.put("city", "beijing");
            startConditions.setLfConditions(lfConditions);

            boolean result = stringJudge.judge("node1", conditionsConf, startConditions, 0);

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when groupedLfConditionsMap is null")
        void shouldReturnFalseWhenGroupedMapNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            boolean result = stringJudge.judge("node1", conditionsConf, startConditions, 0);

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when user condition is null")
        void shouldReturnFalseWhenUserConditionNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            Map<Integer, Map<String, Object>> groupedMap = new HashMap<>();
            Map<String, Object> dbConditions = new HashMap<>();
            dbConditions.put("city", "BEIJING");
            groupedMap.put(0, dbConditions);
            conditionsConf.setGroupedLfConditionsMap(groupedMap);

            Map<Integer, List<Integer>> operatorMap = new HashMap<>();
            operatorMap.put(0, Collections.singletonList(5));
            conditionsConf.setGroupedNumberOperatorListMap(operatorMap);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            boolean result = stringJudge.judge("node1", conditionsConf, startConditions, 0);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("JobLevelJudge")
    class JobLevelJudgeTest {
        @Test
        @DisplayName("should return true when job level id and name match")
        void shouldReturnTrueWhenMatch() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            BaseIdTranStruVo dbVo = new BaseIdTranStruVo();
            dbVo.setId("P7");
            dbVo.setName("Senior");
            conditionsConf.setJobLevelVo(dbVo);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            BaseIdTranStruVo userVo = new BaseIdTranStruVo();
            userVo.setId("P7");
            userVo.setName("Senior");
            startConditions.setJobLevelVo(userVo);

            boolean result = jobLevelJudge.judge("node1", conditionsConf, startConditions, 0);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when job level does not match")
        void shouldReturnFalseWhenNotMatch() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            BaseIdTranStruVo dbVo = new BaseIdTranStruVo();
            dbVo.setId("P7");
            dbVo.setName("Senior");
            conditionsConf.setJobLevelVo(dbVo);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            BaseIdTranStruVo userVo = new BaseIdTranStruVo();
            userVo.setId("P6");
            userVo.setName("Middle");
            startConditions.setJobLevelVo(userVo);

            boolean result = jobLevelJudge.judge("node1", conditionsConf, startConditions, 0);

            assertFalse(result);
        }

        @Test
        @DisplayName("should throw when db job level is null")
        void shouldThrowWhenDbJobLevelNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            BaseIdTranStruVo userVo = new BaseIdTranStruVo();
            userVo.setId("P7");
            startConditions.setJobLevelVo(userVo);

            assertThrows(AFBizException.class, () ->
                    jobLevelJudge.judge("node1", conditionsConf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("MoneyOperatorJudge")
    class MoneyOperatorJudgeTest {
        @Test
        @DisplayName("should always return true")
        void shouldAlwaysReturnTrue() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            boolean result = moneyOperatorJudge.judge("node1", conditionsConf, startConditions, 0);

            assertTrue(result);
        }
    }
}
