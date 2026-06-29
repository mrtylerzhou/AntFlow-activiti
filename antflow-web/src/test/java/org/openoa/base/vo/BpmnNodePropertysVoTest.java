package org.openoa.base.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class BpmnNodePropertysVoTest extends BaseTest {

    @Nested
    @DisplayName("default values")
    class DefaultValuesTest {
        @Test
        @DisplayName("groupRelation should default to true")
        void groupRelationDefault() {
            BpmnNodePropertysVo vo = new BpmnNodePropertysVo();
            assertTrue(vo.getGroupRelation());
        }
    }

    @Nested
    @DisplayName("builder")
    class BuilderTest {
        @Test
        @DisplayName("should build with all fields")
        void buildWithAllFields() {
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .signType(1)
                    .isMultiPeople(1)
                    .isDefault(0)
                    .sort(1)
                    .groupRelation(false)
                    .nodeMark("node1")
                    .afterSignUpWay(1)
                    .signUpType(2)
                    .configurationTableType(1)
                    .tableFieldType(2)
                    .hrbpConfType(1)
                    .assignLevelGrade(3)
                    .assignLevelType(1)
                    .functionId(100L)
                    .functionName("testFunc")
                    .formAssigneeProperty(1)
                    .build();
            assertEquals(1, vo.getSignType());
            assertEquals(1, vo.getIsMultiPeople());
            assertFalse(vo.getGroupRelation());
            assertEquals("node1", vo.getNodeMark());
            assertEquals(100L, vo.getFunctionId());
            assertEquals("testFunc", vo.getFunctionName());
        }

        @Test
        @DisplayName("builder should override default groupRelation")
        void builderOverridesDefault() {
            BpmnNodePropertysVo vo = BpmnNodePropertysVo.builder()
                    .groupRelation(false)
                    .build();
            assertFalse(vo.getGroupRelation());
        }
    }

    @Nested
    @DisplayName("setters")
    class SettersTest {
        @Test
        @DisplayName("should set groupRelation to false")
        void setGroupRelationFalse() {
            BpmnNodePropertysVo vo = new BpmnNodePropertysVo();
            vo.setGroupRelation(false);
            assertFalse(vo.getGroupRelation());
        }

        @Test
        @DisplayName("should set signType")
        void setSignType() {
            BpmnNodePropertysVo vo = new BpmnNodePropertysVo();
            vo.setSignType(2);
            assertEquals(2, vo.getSignType());
        }

        @Test
        @DisplayName("should set conditionsConf")
        void setConditionsConf() {
            BpmnNodePropertysVo vo = new BpmnNodePropertysVo();
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setIsDefault(1);
            vo.setConditionsConf(conf);
            assertNotNull(vo.getConditionsConf());
            assertEquals(1, vo.getConditionsConf().getIsDefault());
        }
    }
}
