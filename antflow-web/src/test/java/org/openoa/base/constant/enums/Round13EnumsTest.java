package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class Round13EnumsTest extends BaseTest {

    @Nested
    @DisplayName("OrderNodeTypeEnum")
    class OrderNodeTypeEnumTest {
        @Test
        @DisplayName("should return enum by code")
        void shouldReturnByCode() {
            assertEquals(OrderNodeTypeEnum.TEST_ORDERED_SIGN, OrderNodeTypeEnum.getByCode(1));
            assertEquals(OrderNodeTypeEnum.OUT_SIDE_NODE, OrderNodeTypeEnum.getByCode(2));
            assertEquals(OrderNodeTypeEnum.LOOP_NODE, OrderNodeTypeEnum.getByCode(3));
            assertEquals(OrderNodeTypeEnum.UDF_CHAIN_NODES, OrderNodeTypeEnum.getByCode(4));
        }

        @Test
        @DisplayName("should return null for null code")
        void shouldReturnNullForNull() {
            assertNull(OrderNodeTypeEnum.getByCode(null));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(OrderNodeTypeEnum.getByCode(99));
        }

        @Test
        @DisplayName("should have 4 values")
        void shouldHave4Values() {
            assertEquals(4, OrderNodeTypeEnum.values().length);
        }

        @Test
        @DisplayName("TEST_ORDERED_SIGN should have correct desc")
        void testOrderedSignDesc() {
            assertEquals("示例顺序节点", OrderNodeTypeEnum.TEST_ORDERED_SIGN.getDesc());
        }
    }

    @Nested
    @DisplayName("AppApplicationType")
    class AppApplicationTypeTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("版本应用类型", AppApplicationType.getDescByCode(1));
            assertEquals("版本应用数据", AppApplicationType.getDescByCode(2));
            assertEquals("版本快捷入口数据", AppApplicationType.getDescByCode(3));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(AppApplicationType.getDescByCode(99));
        }
    }

    @Nested
    @DisplayName("ViewPageTypeEnum")
    class ViewPageTypeEnumTest {
        @Test
        @DisplayName("VIEW_PAGE_TYPE_START should have code 1")
        void startCode() {
            assertEquals(1, ViewPageTypeEnum.VIEW_PAGE_TYPE_START.getCode());
            assertEquals("发起人", ViewPageTypeEnum.VIEW_PAGE_TYPE_START.getDesc());
        }

        @Test
        @DisplayName("VIEW_PAGE_TYPE_OTHER should have code 2")
        void otherCode() {
            assertEquals(2, ViewPageTypeEnum.VIEW_PAGE_TYPE_OTHER.getCode());
            assertEquals("其他审批人", ViewPageTypeEnum.VIEW_PAGE_TYPE_OTHER.getDesc());
        }

        @Test
        @DisplayName("setCode and setDesc should work")
        void setterShouldWork() {
            ViewPageTypeEnum e = ViewPageTypeEnum.VIEW_PAGE_TYPE_START;
            Integer origCode = e.getCode();
            String origDesc = e.getDesc();
            e.setCode(99);
            e.setDesc("custom");
            assertEquals(99, e.getCode());
            assertEquals("custom", e.getDesc());
            e.setCode(origCode);
            e.setDesc(origDesc);
        }
    }

    @Nested
    @DisplayName("DeduplicationTypeEnum")
    class DeduplicationTypeEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("不去重", DeduplicationTypeEnum.getDescByCode(1));
            assertTrue(DeduplicationTypeEnum.getDescByCode(2).contains("前去重"));
            assertTrue(DeduplicationTypeEnum.getDescByCode(3).contains("后去重"));
            assertNotNull(DeduplicationTypeEnum.getDescByCode(4));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(DeduplicationTypeEnum.getDescByCode(99));
        }
    }

    @Nested
    @DisplayName("BpmnNodeParamTypeEnum")
    class BpmnNodeParamTypeEnumTest {
        @Test
        @DisplayName("SINGLE should have code 1")
        void singleCode() {
            assertEquals(1, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE.getCode());
            assertEquals("单人", BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE.getDesc());
        }

        @Test
        @DisplayName("MULTIPLAYER should have code 2")
        void multiplayerCode() {
            assertEquals(2, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER.getCode());
            assertEquals("多人", BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER.getDesc());
        }

        @Test
        @DisplayName("MULTIPLAYER_SORT should have code 3")
        void multiplayerSortCode() {
            assertEquals(3, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER_SORT.getCode());
            assertEquals("多人有序", BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER_SORT.getDesc());
        }
    }
}
