package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class NodeTypeEnumTest extends BaseTest {

    @Nested
    @DisplayName("getNodeTypeEnumByCode")
    class GetNodeTypeEnumByCodeTest {
        @Test
        @DisplayName("should return NODE_TYPE_CONDITIONS for code 3 (hasPropertyTable=1)")
        void shouldReturnConditionsFor3() {
            assertEquals(NodeTypeEnum.NODE_TYPE_CONDITIONS, NodeTypeEnum.getNodeTypeEnumByCode(3));
        }

        @Test
        @DisplayName("should return NODE_TYPE_OUT_SIDE_CONDITIONS for code 5 (hasPropertyTable=1)")
        void shouldReturnOutSideConditionsFor5() {
            assertEquals(NodeTypeEnum.NODE_TYPE_OUT_SIDE_CONDITIONS, NodeTypeEnum.getNodeTypeEnumByCode(5));
        }

        @Test
        @DisplayName("should return NODE_TYPE_COPY for code 6 (hasPropertyTable=1)")
        void shouldReturnCopyFor6() {
            assertEquals(NodeTypeEnum.NODE_TYPE_COPY, NodeTypeEnum.getNodeTypeEnumByCode(6));
        }

        @Test
        @DisplayName("should return null for NODE_TYPE_START code 1 (hasPropertyTable=0)")
        void shouldReturnNullForStart() {
            assertNull(NodeTypeEnum.getNodeTypeEnumByCode(1));
        }

        @Test
        @DisplayName("should return null for NODE_TYPE_GATEWAY code 2 (hasPropertyTable=0)")
        void shouldReturnNullForGateway() {
            assertNull(NodeTypeEnum.getNodeTypeEnumByCode(2));
        }

        @Test
        @DisplayName("should return null for NODE_TYPE_APPROVER code 4 (hasPropertyTable=0)")
        void shouldReturnNullForApprover() {
            assertNull(NodeTypeEnum.getNodeTypeEnumByCode(4));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(NodeTypeEnum.getNodeTypeEnumByCode(999));
        }
    }

    @Nested
    @DisplayName("enum properties")
    class EnumPropertiesTest {
        @Test
        @DisplayName("NODE_TYPE_START should have hasPropertyTable=0")
        void startNoPropertyTable() {
            assertEquals(0, NodeTypeEnum.NODE_TYPE_START.getHasPropertyTable());
        }

        @Test
        @DisplayName("NODE_TYPE_CONDITIONS should have hasPropertyTable=1")
        void conditionsHasPropertyTable() {
            assertEquals(1, NodeTypeEnum.NODE_TYPE_CONDITIONS.getHasPropertyTable());
        }
    }
}
