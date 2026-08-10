package org.openoa.base.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BpmnNodeConditionsConfBaseVoTest extends BaseTest {

    @Nested
    @DisplayName("default values")
    class DefaultValuesTest {
        @Test
        @DisplayName("numberOperatorList should default to empty ArrayList")
        void numberOperatorListDefault() {
            BpmnNodeConditionsConfBaseVo vo = new BpmnNodeConditionsConfBaseVo();
            assertNotNull(vo.getNumberOperatorList());
            assertTrue(vo.getNumberOperatorList().isEmpty());
        }

        @Test
        @DisplayName("groupedNumberOperatorListMap should default to empty HashMap")
        void groupedNumberOperatorListMapDefault() {
            BpmnNodeConditionsConfBaseVo vo = new BpmnNodeConditionsConfBaseVo();
            assertNotNull(vo.getGroupedNumberOperatorListMap());
            assertTrue(vo.getGroupedNumberOperatorListMap().isEmpty());
        }

        @Test
        @DisplayName("groupedCondRelations should default to empty HashMap")
        void groupedCondRelationsDefault() {
            BpmnNodeConditionsConfBaseVo vo = new BpmnNodeConditionsConfBaseVo();
            assertNotNull(vo.getGroupedCondRelations());
            assertTrue(vo.getGroupedCondRelations().isEmpty());
        }
    }

    @Nested
    @DisplayName("builder")
    class BuilderTest {
        @Test
        @DisplayName("should build with all fields")
        void buildWithAllFields() {
            BpmnNodeConditionsConfBaseVo vo = BpmnNodeConditionsConfBaseVo.builder()
                    .isDefault(1)
                    .sort(0)
                    .groupRelation(1)
                    .numberOperator(2)
                    .totalMoney("1000.00")
                    .outTotalMoney("500.00")
                    .leaveHour("8.0")
                    .extJson("{\"key\":\"val\"}")
                    .outSideConditionsJson("[{\"cond\":1}]")
                    .outSideConditionsId("COND-001")
                    .outSideConditionsUrl("http://example.com")
                    .outSideMatched(true)
                    .expression("a > b")
                    .inRangeValue("1,10")
                    .build();
            assertEquals(1, vo.getIsDefault());
            assertEquals(0, vo.getSort());
            assertEquals(1, vo.getGroupRelation());
            assertEquals(2, vo.getNumberOperator());
            assertEquals("1000.00", vo.getTotalMoney());
            assertEquals("500.00", vo.getOutTotalMoney());
            assertEquals("8.0", vo.getLeaveHour());
            assertTrue(vo.getOutSideMatched());
            assertEquals("a > b", vo.getExpression());
        }

        @Test
        @DisplayName("builder should not use field defaults")
        void builderNoDefaults() {
            BpmnNodeConditionsConfBaseVo vo = BpmnNodeConditionsConfBaseVo.builder().build();
            assertNull(vo.getNumberOperatorList());
            assertNull(vo.getGroupedNumberOperatorListMap());
            assertNull(vo.getGroupedCondRelations());
        }
    }

    @Nested
    @DisplayName("setters and getters")
    class SetterGetterTest {
        @Test
        @DisplayName("should set and get numberOperatorList")
        void numberOperatorList() {
            BpmnNodeConditionsConfBaseVo vo = new BpmnNodeConditionsConfBaseVo();
            vo.setNumberOperatorList(new ArrayList<>());
            assertNotNull(vo.getNumberOperatorList());
        }

        @Test
        @DisplayName("should set and get groupedNumberOperatorListMap")
        void groupedNumberOperatorListMap() {
            BpmnNodeConditionsConfBaseVo vo = new BpmnNodeConditionsConfBaseVo();
            vo.setGroupedNumberOperatorListMap(new HashMap<>());
            assertNotNull(vo.getGroupedNumberOperatorListMap());
        }

        @Test
        @DisplayName("should set and get lfConditions")
        void lfConditions() {
            BpmnNodeConditionsConfBaseVo vo = new BpmnNodeConditionsConfBaseVo();
            HashMap<String, Object> map = new HashMap<>();
            map.put("key", "value");
            vo.setLfConditions(map);
            assertEquals("value", vo.getLfConditions().get("key"));
        }
    }
}
