package org.openoa.engine.bpmnconf.adp.conditionfilter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ConditionRelationShipEnum;
import org.openoa.base.entity.BpmDynamicConditionChoosen;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.constant.enus.ConditionTypeEnum;
import org.openoa.engine.bpmnconf.mapper.BpmDynamicConditionChoosenMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ConditionServiceImplTest extends MockBaseTest {

    @InjectMocks
    private ConditionServiceImpl conditionService;

    @Mock
    private BpmDynamicConditionChoosenMapper dynamicConditionChoosenMapper;

    private BpmnNodeVo nodeVo;
    private BpmnStartConditionsVo startConditions;

    @BeforeEach
    void setUp() {
        nodeVo = new BpmnNodeVo();
        nodeVo.setNodeId("testNode");
        nodeVo.setNodeFrom("fromNode");

        startConditions = new BpmnStartConditionsVo();
        startConditions.setProcessNum("PROC001");
    }

    private BpmnNodeConditionsConfBaseVo buildConditionsConf(
            Map<Integer, List<Integer>> groupedConditionParamTypes,
            Integer groupRelation,
            Map<Integer, Integer> groupedCondRelations) {
        BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
        conf.setGroupedConditionParamTypes(groupedConditionParamTypes);
        conf.setGroupRelation(groupRelation);
        conf.setGroupedCondRelations(groupedCondRelations != null ? groupedCondRelations : new HashMap<>());
        return conf;
    }

    private ConditionJudge mockJudge(boolean result) {
        ConditionJudge judge = mock(ConditionJudge.class);
        when(judge.judge(any(), any(), any(), anyInt())).thenReturn(result);
        return judge;
    }

    @Nested
    @DisplayName("empty and null conditions")
    class EmptyNullConditionsTest {

        @Test
        @DisplayName("should return false when groupedConditionParamTypes is null")
        void shouldReturnFalseWhenGroupedParamTypesNull() {
            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(null, 0, null);

            boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when groupedConditionParamTypes is empty")
        void shouldReturnFalseWhenGroupedParamTypesEmpty() {
            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(new HashMap<>(), 0, null);

            boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("single group AND logic")
    class SingleGroupAndLogicTest {

        @Test
        @DisplayName("AND group - all conditions true should return true")
        void andGroup_allTrue_shouldReturnTrue() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Arrays.asList(10001, 10003));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.AND.getCode(), condRelations);

            ConditionJudge trueJudge = mockJudge(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(trueJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertTrue(result);
            }
        }

        @Test
        @DisplayName("AND group - one condition false should return false")
        void andGroup_oneFalse_shouldReturnFalse() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Arrays.asList(10001, 10003));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.AND.getCode(), condRelations);

            ConditionJudge trueJudge = mockJudge(true);
            ConditionJudge falseJudge = mockJudge(false);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                ConditionTypeEnum type1 = ConditionTypeEnum.getEnumByCode(10001);
                ConditionTypeEnum type2 = ConditionTypeEnum.getEnumByCode(10003);

                mockedStatic.when(() -> SpringBeanUtils.getBean(type1.getConditionJudgeCls())).thenReturn(trueJudge);
                mockedStatic.when(() -> SpringBeanUtils.getBean(type2.getConditionJudgeCls())).thenReturn(falseJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertFalse(result);
            }
        }

        @Test
        @DisplayName("AND group - short-circuit: second judge not called when first is false")
        void andGroup_shortCircuitOnFalse() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Arrays.asList(10001, 10003));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.AND.getCode(), condRelations);

            ConditionJudge falseJudge = mock(ConditionJudge.class);
            when(falseJudge.judge("testNode", conf, startConditions, 0)).thenReturn(false);
            ConditionJudge shouldNotBeCalled = mock(ConditionJudge.class);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                ConditionTypeEnum type1 = ConditionTypeEnum.getEnumByCode(10001);
                ConditionTypeEnum type2 = ConditionTypeEnum.getEnumByCode(10003);

                mockedStatic.when(() -> SpringBeanUtils.getBean(type1.getConditionJudgeCls())).thenReturn(falseJudge);
                mockedStatic.when(() -> SpringBeanUtils.getBean(type2.getConditionJudgeCls())).thenReturn(shouldNotBeCalled);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertFalse(result);
                verify(shouldNotBeCalled, never()).judge(any(), any(), any(), anyInt());
            }
        }

        @Test
        @DisplayName("AND group - all conditions false should return false")
        void andGroup_allFalse_shouldReturnFalse() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Arrays.asList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.AND.getCode(), condRelations);

            ConditionJudge falseJudge = mockJudge(false);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(falseJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertFalse(result);
            }
        }
    }

    @Nested
    @DisplayName("single group OR logic")
    class SingleGroupOrLogicTest {

        @Test
        @DisplayName("OR group - one condition true should return true")
        void orGroup_oneTrue_shouldReturnTrue() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Arrays.asList(10001, 10003));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.OR.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.OR.getCode(), condRelations);

            ConditionJudge falseJudge = mockJudge(false);
            ConditionJudge trueJudge = mockJudge(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                ConditionTypeEnum type1 = ConditionTypeEnum.getEnumByCode(10001);
                ConditionTypeEnum type2 = ConditionTypeEnum.getEnumByCode(10003);

                mockedStatic.when(() -> SpringBeanUtils.getBean(type1.getConditionJudgeCls())).thenReturn(falseJudge);
                mockedStatic.when(() -> SpringBeanUtils.getBean(type2.getConditionJudgeCls())).thenReturn(trueJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertTrue(result);
            }
        }

        @Test
        @DisplayName("OR group - short-circuit: second judge not called when first is true")
        void orGroup_shortCircuitOnTrue() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Arrays.asList(10001, 10003));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.OR.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.OR.getCode(), condRelations);

            ConditionJudge trueJudge = mock(ConditionJudge.class);
            when(trueJudge.judge("testNode", conf, startConditions, 0)).thenReturn(true);
            ConditionJudge shouldNotBeCalled = mock(ConditionJudge.class);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                ConditionTypeEnum type1 = ConditionTypeEnum.getEnumByCode(10001);
                ConditionTypeEnum type2 = ConditionTypeEnum.getEnumByCode(10003);

                mockedStatic.when(() -> SpringBeanUtils.getBean(type1.getConditionJudgeCls())).thenReturn(trueJudge);
                mockedStatic.when(() -> SpringBeanUtils.getBean(type2.getConditionJudgeCls())).thenReturn(shouldNotBeCalled);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertTrue(result);
                verify(shouldNotBeCalled, never()).judge(any(), any(), any(), anyInt());
            }
        }

        @Test
        @DisplayName("OR group - all conditions false should return false")
        void orGroup_allFalse_shouldReturnFalse() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Arrays.asList(10001, 10003));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.OR.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.OR.getCode(), condRelations);

            ConditionJudge falseJudge = mockJudge(false);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(falseJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertFalse(result);
            }
        }

        @Test
        @DisplayName("OR group - all conditions true should return true")
        void orGroup_allTrue_shouldReturnTrue() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.OR.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.OR.getCode(), condRelations);

            ConditionJudge trueJudge = mockJudge(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(trueJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertTrue(result);
            }
        }
    }

    @Nested
    @DisplayName("multiple condition groups")
    class MultipleGroupsTest {

        @Test
        @DisplayName("AND between groups - all groups true should return true")
        void andBetweenGroups_allGroupsTrue_shouldReturnTrue() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            groupedTypes.put(1, Collections.singletonList(10003));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());
            condRelations.put(1, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.AND.getCode(), condRelations);

            ConditionJudge trueJudge = mockJudge(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(trueJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertTrue(result);
            }
        }

        @Test
        @DisplayName("AND between groups - one group false should return false")
        void andBetweenGroups_oneGroupFalse_shouldReturnFalse() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            groupedTypes.put(1, Collections.singletonList(10003));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());
            condRelations.put(1, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.AND.getCode(), condRelations);

            ConditionTypeEnum type0 = ConditionTypeEnum.getEnumByCode(10001);
            ConditionTypeEnum type1 = ConditionTypeEnum.getEnumByCode(10003);
            ConditionJudge trueJudge = mockJudge(true);
            ConditionJudge falseJudge = mockJudge(false);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(type0.getConditionJudgeCls())).thenReturn(trueJudge);
                mockedStatic.when(() -> SpringBeanUtils.getBean(type1.getConditionJudgeCls())).thenReturn(falseJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertFalse(result);
            }
        }

        @Test
        @DisplayName("AND between groups - short-circuit: second group not evaluated when first is false")
        void andBetweenGroups_shortCircuitOnFalseGroup() {
            Map<Integer, List<Integer>> groupedTypes = new LinkedHashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            groupedTypes.put(1, Collections.singletonList(10003));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());
            condRelations.put(1, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.AND.getCode(), condRelations);

            ConditionTypeEnum type0 = ConditionTypeEnum.getEnumByCode(10001);
            ConditionTypeEnum type1 = ConditionTypeEnum.getEnumByCode(10003);
            ConditionJudge falseJudge = mockJudge(false);
            ConditionJudge shouldNotBeCalled = mock(ConditionJudge.class);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(type0.getConditionJudgeCls())).thenReturn(falseJudge);
                mockedStatic.when(() -> SpringBeanUtils.getBean(type1.getConditionJudgeCls())).thenReturn(shouldNotBeCalled);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertFalse(result);
                verify(shouldNotBeCalled, never()).judge(any(), any(), any(), anyInt());
            }
        }

        @Test
        @DisplayName("OR between groups - one group true should return true")
        void orBetweenGroups_oneGroupTrue_shouldReturnTrue() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            groupedTypes.put(1, Collections.singletonList(10003));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());
            condRelations.put(1, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.OR.getCode(), condRelations);

            ConditionTypeEnum type0 = ConditionTypeEnum.getEnumByCode(10001);
            ConditionTypeEnum type1 = ConditionTypeEnum.getEnumByCode(10003);
            ConditionJudge falseJudge = mockJudge(false);
            ConditionJudge trueJudge = mockJudge(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(type0.getConditionJudgeCls())).thenReturn(falseJudge);
                mockedStatic.when(() -> SpringBeanUtils.getBean(type1.getConditionJudgeCls())).thenReturn(trueJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertTrue(result);
            }
        }

        @Test
        @DisplayName("OR between groups - short-circuit: second group not evaluated when first is true")
        void orBetweenGroups_shortCircuitOnTrueGroup() {
            Map<Integer, List<Integer>> groupedTypes = new LinkedHashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            groupedTypes.put(1, Collections.singletonList(10003));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());
            condRelations.put(1, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.OR.getCode(), condRelations);

            ConditionTypeEnum type0 = ConditionTypeEnum.getEnumByCode(10001);
            ConditionTypeEnum type1 = ConditionTypeEnum.getEnumByCode(10003);
            ConditionJudge trueJudge = mockJudge(true);
            ConditionJudge shouldNotBeCalled = mock(ConditionJudge.class);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(type0.getConditionJudgeCls())).thenReturn(trueJudge);
                mockedStatic.when(() -> SpringBeanUtils.getBean(type1.getConditionJudgeCls())).thenReturn(shouldNotBeCalled);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertTrue(result);
                verify(shouldNotBeCalled, never()).judge(any(), any(), any(), anyInt());
            }
        }

        @Test
        @DisplayName("OR between groups - all groups false should return false")
        void orBetweenGroups_allGroupsFalse_shouldReturnFalse() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            groupedTypes.put(1, Collections.singletonList(10003));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());
            condRelations.put(1, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.OR.getCode(), condRelations);

            ConditionJudge falseJudge = mockJudge(false);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(falseJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertFalse(result);
            }
        }
    }

    @Nested
    @DisplayName("mixed AND/OR within and between groups")
    class MixedLogicTest {

        @Test
        @DisplayName("OR within group, AND between groups - complex scenario")
        void orWithinGroup_andBetweenGroups() {
            Map<Integer, List<Integer>> groupedTypes = new LinkedHashMap<>();
            groupedTypes.put(0, Arrays.asList(10001, 10003));
            groupedTypes.put(1, Collections.singletonList(10004));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.OR.getCode());
            condRelations.put(1, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.AND.getCode(), condRelations);

            ConditionTypeEnum typeNum = ConditionTypeEnum.getEnumByCode(10001);
            ConditionTypeEnum typeDate = ConditionTypeEnum.getEnumByCode(10003);
            ConditionTypeEnum typeColl = ConditionTypeEnum.getEnumByCode(10004);
            ConditionJudge falseJudge = mockJudge(false);
            ConditionJudge trueJudge = mockJudge(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(typeNum.getConditionJudgeCls())).thenReturn(falseJudge);
                mockedStatic.when(() -> SpringBeanUtils.getBean(typeDate.getConditionJudgeCls())).thenReturn(trueJudge);
                mockedStatic.when(() -> SpringBeanUtils.getBean(typeColl.getConditionJudgeCls())).thenReturn(trueJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertTrue(result);
            }
        }

        @Test
        @DisplayName("AND within group, OR between groups - complex scenario")
        void andWithinGroup_orBetweenGroups() {
            Map<Integer, List<Integer>> groupedTypes = new LinkedHashMap<>();
            groupedTypes.put(0, Arrays.asList(10001, 10003));
            groupedTypes.put(1, Collections.singletonList(10004));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());
            condRelations.put(1, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes,
                    ConditionRelationShipEnum.OR.getCode(), condRelations);

            ConditionTypeEnum typeNum = ConditionTypeEnum.getEnumByCode(10001);
            ConditionTypeEnum typeDate = ConditionTypeEnum.getEnumByCode(10003);
            ConditionTypeEnum typeColl = ConditionTypeEnum.getEnumByCode(10004);
            ConditionJudge falseJudge = mockJudge(false);
            ConditionJudge trueJudge = mockJudge(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(typeNum.getConditionJudgeCls())).thenReturn(falseJudge);
                mockedStatic.when(() -> SpringBeanUtils.getBean(typeDate.getConditionJudgeCls())).thenReturn(trueJudge);
                mockedStatic.when(() -> SpringBeanUtils.getBean(typeColl.getConditionJudgeCls())).thenReturn(trueJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertTrue(result);
            }
        }
    }

    @Nested
    @DisplayName("error handling")
    class ErrorHandlingTest {

        @Test
        @DisplayName("should throw AFBizException when condRelation is null for a group")
        void shouldThrowWhenCondRelationNull() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            assertThrows(AFBizException.class,
                    () -> conditionService.checkMatchCondition(nodeVo, conf, startConditions, false));
        }

        @Test
        @DisplayName("should throw AFBizException when condition type code is unknown")
        void shouldThrowWhenConditionTypeUnknown() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(99999));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            assertThrows(AFBizException.class,
                    () -> conditionService.checkMatchCondition(nodeVo, conf, startConditions, false));
        }

        @Test
        @DisplayName("should set result false and break when conditionParamTypeList is empty")
        void shouldReturnFalseWhenConditionParamTypeListEmpty() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.emptyList());
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

            assertFalse(result);
        }

        @Test
        @DisplayName("should rethrow AFBizException from judge")
        void shouldRethrowAFBizExceptionFromJudge() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            ConditionJudge judge = mock(ConditionJudge.class);
            when(judge.judge(any(), any(), any(), anyInt())).thenThrow(new AFBizException("judge error"));

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(judge);

                AFBizException ex = assertThrows(AFBizException.class,
                        () -> conditionService.checkMatchCondition(nodeVo, conf, startConditions, false));
                assertEquals("judge error", ex.getMessage());
            }
        }

        @Test
        @DisplayName("should rethrow generic Exception from judge")
        void shouldRethrowGenericExceptionFromJudge() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            ConditionJudge judge = mock(ConditionJudge.class);
            when(judge.judge(any(), any(), any(), anyInt())).thenThrow(new RuntimeException("runtime error"));

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(judge);

                RuntimeException ex = assertThrows(RuntimeException.class,
                        () -> conditionService.checkMatchCondition(nodeVo, conf, startConditions, false));
                assertEquals("runtime error", ex.getMessage());
            }
        }

        @Test
        @DisplayName("duplicate condition type codes in same group should be deduplicated")
        void duplicateConditionTypesInGroup_shouldBeDeduplicated() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Arrays.asList(10001, 10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            ConditionJudge trueJudge = mockJudge(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(trueJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertTrue(result);
                verify(trueJudge, times(1)).judge(any(), any(), any(), anyInt());
            }
        }
    }

    @Nested
    @DisplayName("migration preview check")
    class MigrationPreviewTest {

        @Test
        @DisplayName("should throw AFBizException when condition changed from false to true during migration preview")
        void shouldThrowWhenConditionChangedFromFalseToTrue() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            startConditions.setIsMigration(true);
            startConditions.setPreview(true);

            ConditionJudge trueJudge = mockJudge(true);

            BpmDynamicConditionChoosen oldRecord = new BpmDynamicConditionChoosen();
            oldRecord.setNodeId("otherNode");

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(trueJudge);
                when(dynamicConditionChoosenMapper.selectList(any(LambdaQueryWrapper.class)))
                        .thenReturn(Collections.singletonList(oldRecord));
                when(dynamicConditionChoosenMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
                when(dynamicConditionChoosenMapper.insert(any(BpmDynamicConditionChoosen.class))).thenReturn(1);

                AFBizException ex = assertThrows(AFBizException.class,
                        () -> conditionService.checkMatchCondition(nodeVo, conf, startConditions, true));
                assertEquals(StringConstants.CONDITION_CHANGED, ex.getCode());
            }
        }

        @Test
        @DisplayName("should throw AFBizException when condition changed from true to false during migration preview")
        void shouldThrowWhenConditionChangedFromTrueToFalse() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            startConditions.setIsMigration(true);
            startConditions.setPreview(true);

            ConditionJudge falseJudge = mockJudge(false);

            BpmDynamicConditionChoosen oldRecord = new BpmDynamicConditionChoosen();
            oldRecord.setNodeId("testNode");

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(falseJudge);
                when(dynamicConditionChoosenMapper.selectList(any(LambdaQueryWrapper.class)))
                        .thenReturn(Collections.singletonList(oldRecord));
                when(dynamicConditionChoosenMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
                when(dynamicConditionChoosenMapper.insert(any(BpmDynamicConditionChoosen.class))).thenReturn(1);

                AFBizException ex = assertThrows(AFBizException.class,
                        () -> conditionService.checkMatchCondition(nodeVo, conf, startConditions, true));
                assertEquals(StringConstants.CONDITION_CHANGED, ex.getCode());
            }
        }

        @Test
        @DisplayName("should not throw when condition unchanged during migration preview")
        void shouldNotThrowWhenConditionUnchanged() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            startConditions.setIsMigration(true);
            startConditions.setPreview(true);

            ConditionJudge trueJudge = mockJudge(true);

            BpmDynamicConditionChoosen oldRecord = new BpmDynamicConditionChoosen();
            oldRecord.setNodeId("testNode");

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(trueJudge);
                when(dynamicConditionChoosenMapper.selectList(any(LambdaQueryWrapper.class)))
                        .thenReturn(Collections.singletonList(oldRecord));

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, true);

                assertTrue(result);
                verify(dynamicConditionChoosenMapper, never()).delete(any(LambdaQueryWrapper.class));
            }
        }

        @Test
        @DisplayName("should not throw when both false and not previously used during migration preview")
        void shouldNotThrowWhenBothFalseAndNotUsed() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            startConditions.setIsMigration(true);
            startConditions.setPreview(true);

            ConditionJudge falseJudge = mockJudge(false);

            BpmDynamicConditionChoosen oldRecord = new BpmDynamicConditionChoosen();
            oldRecord.setNodeId("otherNode");

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(falseJudge);
                when(dynamicConditionChoosenMapper.selectList(any(LambdaQueryWrapper.class)))
                        .thenReturn(Collections.singletonList(oldRecord));

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, true);

                assertFalse(result);
                verify(dynamicConditionChoosenMapper, never()).delete(any(LambdaQueryWrapper.class));
            }
        }

        @Test
        @DisplayName("migration preview should not apply when isDynamicConditionGateway is false")
        void migrationPreview_shouldNotApplyWhenNotDynamic() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            startConditions.setIsMigration(true);
            startConditions.setPreview(true);

            ConditionJudge trueJudge = mockJudge(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(trueJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertTrue(result);
                verify(dynamicConditionChoosenMapper, never()).selectList(any(LambdaQueryWrapper.class));
            }
        }
    }

    @Nested
    @DisplayName("dynamic condition recording")
    class DynamicConditionRecordingTest {

        @Test
        @DisplayName("should insert dynamic condition record when result is true and not preview")
        void shouldInsertRecordWhenTrueAndNotPreview() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            startConditions.setPreview(false);

            ConditionJudge trueJudge = mockJudge(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(trueJudge);
                when(dynamicConditionChoosenMapper.insert(any(BpmDynamicConditionChoosen.class))).thenReturn(1);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, true);

                assertTrue(result);
                verify(dynamicConditionChoosenMapper, times(1)).insert(any(BpmDynamicConditionChoosen.class));
            }
        }

        @Test
        @DisplayName("should not insert record when result is false")
        void shouldNotInsertRecordWhenFalse() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            startConditions.setPreview(false);

            ConditionJudge falseJudge = mockJudge(false);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(falseJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, true);

                assertFalse(result);
                verify(dynamicConditionChoosenMapper, never()).insert(any(BpmDynamicConditionChoosen.class));
            }
        }

        @Test
        @DisplayName("should not insert record when isDynamicConditionGateway is false")
        void shouldNotInsertRecordWhenNotDynamic() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            startConditions.setPreview(false);

            ConditionJudge trueJudge = mockJudge(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(trueJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, false);

                assertTrue(result);
                verify(dynamicConditionChoosenMapper, never()).insert(any(BpmDynamicConditionChoosen.class));
            }
        }

        @Test
        @DisplayName("should not insert record when preview is true")
        void shouldNotInsertRecordWhenPreview() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            startConditions.setPreview(true);

            ConditionJudge trueJudge = mockJudge(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(trueJudge);

                boolean result = conditionService.checkMatchCondition(nodeVo, conf, startConditions, true);

                assertTrue(result);
                verify(dynamicConditionChoosenMapper, never()).insert(any(BpmDynamicConditionChoosen.class));
            }
        }

        @Test
        @DisplayName("inserted record should contain correct processNumber, nodeId and nodeFrom")
        void insertedRecordShouldContainCorrectFields() {
            Map<Integer, List<Integer>> groupedTypes = new HashMap<>();
            groupedTypes.put(0, Collections.singletonList(10001));
            Map<Integer, Integer> condRelations = new HashMap<>();
            condRelations.put(0, ConditionRelationShipEnum.AND.getCode());

            BpmnNodeConditionsConfBaseVo conf = buildConditionsConf(groupedTypes, 0, condRelations);

            startConditions.setPreview(false);
            startConditions.setProcessNum("PROC_002");
            nodeVo.setNodeId("myNode");
            nodeVo.setNodeFrom("myFromNode");

            ConditionJudge trueJudge = mockJudge(true);

            try (MockedStatic<SpringBeanUtils> mockedStatic = mockStatic(SpringBeanUtils.class)) {
                mockedStatic.when(() -> SpringBeanUtils.getBean(any(Class.class))).thenReturn(trueJudge);
                when(dynamicConditionChoosenMapper.insert(any(BpmDynamicConditionChoosen.class))).thenReturn(1);

                conditionService.checkMatchCondition(nodeVo, conf, startConditions, true);

                verify(dynamicConditionChoosenMapper).insert(argThat(record ->
                        "PROC_002".equals(record.getProcessNumber())
                                && "myNode".equals(record.getNodeId())
                                && "myFromNode".equals(record.getNodeFrom())
                ));
            }
        }
    }
}
