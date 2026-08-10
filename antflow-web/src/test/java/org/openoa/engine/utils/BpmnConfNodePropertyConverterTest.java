package org.openoa.engine.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.ConditionRelationShipEnum;
import org.openoa.base.constant.enums.JudgeOperatorEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BaseKeyValueStruVo;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnNodeConditionsConfVueVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.engine.bpmnconf.constant.enus.ConditionTypeEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BpmnConfNodePropertyConverterTest extends BaseTest {

    @Nested
    @DisplayName("fromVue3Model")
    class FromVue3ModelTest {

        @Test
        @DisplayName("should throw AFBizException when propertysVo is null")
        void shouldThrowWhenPropertysVoIsNull() {
            AFBizException ex = assertThrows(AFBizException.class,
                    () -> BpmnConfNodePropertyConverter.fromVue3Model(null));
            assertEquals("node has no property!", ex.getMessage());
        }

        @Test
        @DisplayName("should throw AFBizException when conditionList is empty and isDefault=0")
        void shouldThrowWhenConditionListEmptyAndNotDefault() {
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(0)
                    .conditionList(new ArrayList<>())
                    .build();
            AFBizException ex = assertThrows(AFBizException.class,
                    () -> BpmnConfNodePropertyConverter.fromVue3Model(vo));
            assertEquals("input nodes is empty", ex.getMessage());
        }

        @Test
        @DisplayName("should create default result when isDefault=1 with empty conditionList")
        void shouldCreateDefaultResultWhenIsDefault1() {
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(1)
                    .sort(1)
                    .conditionList(new ArrayList<>())
                    .build();
            BpmnNodeConditionsConfBaseVo result = BpmnConfNodePropertyConverter.fromVue3Model(vo);
            assertEquals(1, result.getIsDefault());
            assertEquals(1, result.getSort());
            assertNotNull(result.getConditionParamTypes());
            assertTrue(result.getConditionParamTypes().isEmpty());
            assertNotNull(result.getGroupedConditionParamTypes());
            assertTrue(result.getGroupedConditionParamTypes().isEmpty());
        }

        @Test
        @DisplayName("should map groupRelation=false to AND(code=0)")
        void shouldMapGroupRelationFalseToAnd() {
            BpmnNodeConditionsConfVueVo condition = buildTotalMoneyCondition("100", 5);
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(0)
                    .groupRelation(false)
                    .conditionList(Collections.singletonList(Collections.singletonList(condition)))
                    .build();
            BpmnNodeConditionsConfBaseVo result = BpmnConfNodePropertyConverter.fromVue3Model(vo);
            assertEquals(ConditionRelationShipEnum.AND.getCode(), result.getGroupRelation());
            assertEquals(0, result.getGroupRelation());
        }

        @Test
        @DisplayName("should map groupRelation=true to OR(code=1)")
        void shouldMapGroupRelationTrueToOr() {
            BpmnNodeConditionsConfVueVo condition = buildTotalMoneyCondition("100", 5);
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(0)
                    .groupRelation(true)
                    .conditionList(Collections.singletonList(Collections.singletonList(condition)))
                    .build();
            BpmnNodeConditionsConfBaseVo result = BpmnConfNodePropertyConverter.fromVue3Model(vo);
            assertEquals(ConditionRelationShipEnum.OR.getCode(), result.getGroupRelation());
            assertEquals(1, result.getGroupRelation());
        }

        @Test
        @DisplayName("should map groupRelation=null to OR(code=1)")
        void shouldMapGroupRelationNullToOr() {
            BpmnNodeConditionsConfVueVo condition = buildTotalMoneyCondition("100", 5);
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(0)
                    .groupRelation(null)
                    .conditionList(Collections.singletonList(Collections.singletonList(condition)))
                    .build();
            BpmnNodeConditionsConfBaseVo result = BpmnConfNodePropertyConverter.fromVue3Model(vo);
            assertEquals(1, result.getGroupRelation());
        }

        @Test
        @DisplayName("should convert scalar condition (totalMoney with EQ operator)")
        void shouldConvertScalarCondition() {
            BpmnNodeConditionsConfVueVo condition = buildTotalMoneyCondition("100", 5);
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(0)
                    .conditionList(Collections.singletonList(Collections.singletonList(condition)))
                    .build();
            BpmnNodeConditionsConfBaseVo result = BpmnConfNodePropertyConverter.fromVue3Model(vo);
            assertEquals("100", result.getTotalMoney());
            assertEquals(JudgeOperatorEnum.EQ.getCode(), result.getNumberOperator());
        }

        @Test
        @DisplayName("should convert list condition (accountType)")
        void shouldConvertListCondition() {
            String fixedDownBoxValue = JSON.toJSONString(Arrays.asList(
                    BaseKeyValueStruVo.builder().key("1").value("Type1").build(),
                    BaseKeyValueStruVo.builder().key("2").value("Type2").build()
            ));
            BpmnNodeConditionsConfVueVo condition = new BpmnNodeConditionsConfVueVo();
            condition.setColumnId("1");
            condition.setColumnDbname("accountType");
            condition.setZdy1("1,2");
            condition.setFixedDownBoxValue(fixedDownBoxValue);
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(0)
                    .conditionList(Collections.singletonList(Collections.singletonList(condition)))
                    .build();
            BpmnNodeConditionsConfBaseVo result = BpmnConfNodePropertyConverter.fromVue3Model(vo);
            assertNotNull(result.getAccountType());
            assertEquals(Arrays.asList(1, 2), result.getAccountType());
        }

        @Test
        @DisplayName("should handle binary operator (between) with totalMoney")
        void shouldHandleBinaryOperator() {
            BpmnNodeConditionsConfVueVo condition = new BpmnNodeConditionsConfVueVo();
            condition.setColumnId("38");
            condition.setColumnDbname("totalMoney");
            condition.setOptType(9);
            condition.setZdy1("100");
            condition.setZdy2("200");
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(0)
                    .conditionList(Collections.singletonList(Collections.singletonList(condition)))
                    .build();
            BpmnNodeConditionsConfBaseVo result = BpmnConfNodePropertyConverter.fromVue3Model(vo);
            assertEquals("100,200", result.getTotalMoney());
            assertEquals(JudgeOperatorEnum.GTE1LTE2.getCode(), result.getNumberOperator());
        }

        @Test
        @DisplayName("should throw AFBizException when columnId is null")
        void shouldThrowWhenColumnIdIsNull() {
            BpmnNodeConditionsConfVueVo condition = new BpmnNodeConditionsConfVueVo();
            condition.setColumnId(null);
            condition.setColumnDbname("totalMoney");
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(0)
                    .conditionList(Collections.singletonList(Collections.singletonList(condition)))
                    .build();
            AFBizException ex = assertThrows(AFBizException.class,
                    () -> BpmnConfNodePropertyConverter.fromVue3Model(vo));
            assertEquals("each and every node must have a columnId value", ex.getMessage());
        }

        @Test
        @DisplayName("should throw AFBizException when columnId is invalid")
        void shouldThrowWhenColumnIdIsInvalid() {
            BpmnNodeConditionsConfVueVo condition = new BpmnNodeConditionsConfVueVo();
            condition.setColumnId("99999");
            condition.setColumnDbname("unknownField");
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(0)
                    .conditionList(Collections.singletonList(Collections.singletonList(condition)))
                    .build();
            AFBizException ex = assertThrows(AFBizException.class,
                    () -> BpmnConfNodePropertyConverter.fromVue3Model(vo));
            assertTrue(ex.getMessage().contains("columnId of value:99999 is not a valid value"));
        }

        @Test
        @DisplayName("should throw AFBizException when columnDbname doesn't match fieldName")
        void shouldThrowWhenColumnDbnameMismatch() {
            BpmnNodeConditionsConfVueVo condition = new BpmnNodeConditionsConfVueVo();
            condition.setColumnId("38");
            condition.setColumnDbname("wrongName");
            condition.setOptType(5);
            condition.setZdy1("100");
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(0)
                    .conditionList(Collections.singletonList(Collections.singletonList(condition)))
                    .build();
            AFBizException ex = assertThrows(AFBizException.class,
                    () -> BpmnConfNodePropertyConverter.fromVue3Model(vo));
            assertTrue(ex.getMessage().contains("columnDbname:wrongName is not a valid name"));
        }

        @Test
        @DisplayName("should store extJson as serialized conditionList")
        void shouldStoreExtJson() {
            BpmnNodeConditionsConfVueVo condition = buildTotalMoneyCondition("100", 5);
            List<List<BpmnNodeConditionsConfVueVo>> conditionList = Collections.singletonList(
                    Collections.singletonList(condition));
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(0)
                    .conditionList(conditionList)
                    .build();
            BpmnNodeConditionsConfBaseVo result = BpmnConfNodePropertyConverter.fromVue3Model(vo);
            assertNotNull(result.getExtJson());
            List<List<BpmnNodeConditionsConfVueVo>> parsed = JSON.parseObject(
                    result.getExtJson(),
                    new TypeReference<List<List<BpmnNodeConditionsConfVueVo>>>() {});
            assertNotNull(parsed);
            assertEquals(1, parsed.size());
            assertEquals(1, parsed.get(0).size());
            assertEquals("38", parsed.get(0).get(0).getColumnId());
        }

        @Test
        @DisplayName("should store conditionParamTypes with all condition type codes")
        void shouldStoreConditionParamTypes() {
            BpmnNodeConditionsConfVueVo cond1 = buildTotalMoneyCondition("100", 5);
            String fixedDownBoxValue = JSON.toJSONString(Collections.singletonList(
                    BaseKeyValueStruVo.builder().key("1").value("Type1").build()));
            BpmnNodeConditionsConfVueVo cond2 = new BpmnNodeConditionsConfVueVo();
            cond2.setColumnId("1");
            cond2.setColumnDbname("accountType");
            cond2.setZdy1("1");
            cond2.setFixedDownBoxValue(fixedDownBoxValue);
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(0)
                    .conditionList(Collections.singletonList(Arrays.asList(cond1, cond2)))
                    .build();
            BpmnNodeConditionsConfBaseVo result = BpmnConfNodePropertyConverter.fromVue3Model(vo);
            assertNotNull(result.getConditionParamTypes());
            assertEquals(2, result.getConditionParamTypes().size());
            assertTrue(result.getConditionParamTypes().contains(38));
            assertTrue(result.getConditionParamTypes().contains(1));
        }

        @Test
        @DisplayName("should store groupedConditionParamTypes by group index")
        void shouldStoreGroupedConditionParamTypes() {
            BpmnNodeConditionsConfVueVo cond1 = buildTotalMoneyCondition("100", 5);
            BpmnNodeConditionsConfVueVo cond2 = buildTotalMoneyCondition("200", 5);
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .isDefault(0)
                    .conditionList(Arrays.asList(
                            Collections.singletonList(cond1),
                            Collections.singletonList(cond2)))
                    .build();
            BpmnNodeConditionsConfBaseVo result = BpmnConfNodePropertyConverter.fromVue3Model(vo);
            assertNotNull(result.getGroupedConditionParamTypes());
            assertEquals(2, result.getGroupedConditionParamTypes().size());
            assertTrue(result.getGroupedConditionParamTypes().containsKey(1));
            assertTrue(result.getGroupedConditionParamTypes().containsKey(2));
            assertEquals(Collections.singletonList(38), result.getGroupedConditionParamTypes().get(1));
            assertEquals(Collections.singletonList(38), result.getGroupedConditionParamTypes().get(2));
        }

        private BpmnNodeConditionsConfVueVo buildTotalMoneyCondition(String value, int optType) {
            BpmnNodeConditionsConfVueVo condition = new BpmnNodeConditionsConfVueVo();
            condition.setColumnId("38");
            condition.setColumnDbname("totalMoney");
            condition.setOptType(optType);
            condition.setZdy1(value);
            return condition;
        }
    }

    @Nested
    @DisplayName("toVue3Model")
    class ToVue3ModelTest {

        @Test
        @DisplayName("should throw AFBizException when baseVo is null")
        void shouldThrowWhenBaseVoIsNull() {
            AFBizException ex = assertThrows(AFBizException.class,
                    () -> BpmnConfNodePropertyConverter.toVue3Model(null));
            assertEquals("baseVo to convert is null", ex.getMessage());
        }

        @Test
        @DisplayName("should return empty list when isDefault=1")
        void shouldReturnEmptyListWhenIsDefault1() {
            BpmnNodeConditionsConfBaseVo baseVo = BpmnNodeConditionsConfBaseVo.builder()
                    .isDefault(1)
                    .build();
            List<BpmnNodeConditionsConfVueVo> result = BpmnConfNodePropertyConverter.toVue3Model(baseVo);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should reconstruct list-type condition from baseVo (accountType)")
        void shouldReconstructListTypeCondition() {
            List<Integer> accountType = Arrays.asList(1, 2);
            List<BaseIdTranStruVo> accountTypeList = Arrays.asList(
                    BaseIdTranStruVo.builder().id("1").name("Type1").build(),
                    BaseIdTranStruVo.builder().id("2").name("Type2").build());
            Map<Integer, List<Integer>> groupedConditionParamTypes = new HashMap<>();
            groupedConditionParamTypes.put(1, Collections.singletonList(
                    ConditionTypeEnum.CONDITION_THIRD_ACCOUNT_TYPE.getCode()));
            BpmnNodeConditionsConfBaseVo baseVo = BpmnNodeConditionsConfBaseVo.builder()
                    .isDefault(0)
                    .accountType(accountType)
                    .accountTypeList(accountTypeList)
                    .groupedConditionParamTypes(groupedConditionParamTypes)
                    .build();
            List<BpmnNodeConditionsConfVueVo> result = BpmnConfNodePropertyConverter.toVue3Model(baseVo);
            assertNotNull(result);
            assertEquals(1, result.size());
            BpmnNodeConditionsConfVueVo vueVo = result.get(0);
            assertEquals("accountType", vueVo.getColumnDbname());
            assertEquals(ConditionTypeEnum.CONDITION_THIRD_ACCOUNT_TYPE.getDesc(), vueVo.getShowName());
            assertEquals("1,2", vueVo.getZdy1());
            assertNotNull(vueVo.getFixedDownBoxValue());
            List<BaseKeyValueStruVo> parsed = JSON.parseArray(vueVo.getFixedDownBoxValue(),
                    BaseKeyValueStruVo.class);
            assertEquals(2, parsed.size());
            assertEquals("1", parsed.get(0).getKey());
            assertEquals("Type1", parsed.get(0).getValue());
            assertEquals("2", parsed.get(1).getKey());
            assertEquals("Type2", parsed.get(1).getValue());
        }
    }
}
