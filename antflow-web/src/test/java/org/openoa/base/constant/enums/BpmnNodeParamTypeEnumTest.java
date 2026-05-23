package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class BpmnNodeParamTypeEnumTest extends BaseTest {

    @Nested
    @DisplayName("BPMN_NODE_PARAM_SINGLE")
    class SingleTest {

        @Test
        @DisplayName("has code 1")
        void hasCode1() {
            assertEquals(1, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE.getCode());
        }

        @Test
        @DisplayName("has desc '单人'")
        void hasDesc() {
            assertEquals("单人", BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE.getDesc());
        }
    }

    @Nested
    @DisplayName("BPMN_NODE_PARAM_MULTIPLAYER")
    class MultiplayerTest {

        @Test
        @DisplayName("has code 2")
        void hasCode2() {
            assertEquals(2, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER.getCode());
        }

        @Test
        @DisplayName("has desc '多人'")
        void hasDesc() {
            assertEquals("多人", BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER.getDesc());
        }
    }

    @Nested
    @DisplayName("BPMN_NODE_PARAM_MULTIPLAYER_SORT")
    class MultiplayerSortTest {

        @Test
        @DisplayName("has code 3")
        void hasCode3() {
            assertEquals(3, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER_SORT.getCode());
        }

        @Test
        @DisplayName("has desc '多人有序'")
        void hasDesc() {
            assertEquals("多人有序", BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER_SORT.getDesc());
        }
    }
}
