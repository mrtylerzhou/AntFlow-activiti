package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class NodePropertyEnumTest extends BaseTest {

    @Nested
    @DisplayName("getByCode")
    class GetByCodeTest {
        @Test
        @DisplayName("should return enum for valid code")
        void shouldReturnEnumForValidCode() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_LOOP, NodePropertyEnum.getByCode(2));
        }

        @Test
        @DisplayName("should return NODE_PROPERTY_PERSONNEL for code 5")
        void shouldReturnPersonnelForCode5() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_PERSONNEL, NodePropertyEnum.getByCode(5));
        }

        @Test
        @DisplayName("should return null for null code")
        void shouldReturnNullForNull() {
            assertNull(NodePropertyEnum.getByCode(null));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(NodePropertyEnum.getByCode(999));
        }
    }

    @Nested
    @DisplayName("getNodePropertyEnumByCode")
    class GetNodePropertyEnumByCodeTest {
        @Test
        @DisplayName("should return enum with hasPropertyTable=1")
        void shouldReturnEnumWithPropertyTable() {
            NodePropertyEnum result = NodePropertyEnum.getNodePropertyEnumByCode(2);
            assertNotNull(result);
            assertEquals(NodePropertyEnum.NODE_PROPERTY_LOOP, result);
            assertEquals(1, result.getHasPropertyTable());
        }

        @Test
        @DisplayName("should return null for code without property table")
        void shouldReturnNullForNoPropertyTable() {
            assertNull(NodePropertyEnum.getNodePropertyEnumByCode(1));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(NodePropertyEnum.getNodePropertyEnumByCode(999));
        }
    }

    @Nested
    @DisplayName("getDescByCode")
    class GetDescByCodeTest {
        @Test
        @DisplayName("should return desc for valid code")
        void shouldReturnDescForValidCode() {
            assertEquals("层层审批", NodePropertyEnum.getDescByCode(2));
        }

        @Test
        @DisplayName("should return desc for code 5")
        void shouldReturnDescForCode5() {
            assertEquals("指定人员", NodePropertyEnum.getDescByCode(5));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(NodePropertyEnum.getDescByCode(999));
        }
    }

    @Nested
    @DisplayName("enum properties")
    class EnumPropertiesTest {
        @Test
        @DisplayName("NODE_PROPERTY_ROLE should have correct properties")
        void roleProperties() {
            assertEquals(4, NodePropertyEnum.NODE_PROPERTY_ROLE.getCode());
            assertEquals("指定角色", NodePropertyEnum.NODE_PROPERTY_ROLE.getDesc());
            assertEquals(1, NodePropertyEnum.NODE_PROPERTY_ROLE.getHasPropertyTable());
        }

        @Test
        @DisplayName("NODE_PROPERTY_START_USER should have SINGLE param type")
        void startUserParamType() {
            assertEquals(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE,
                    NodePropertyEnum.NODE_PROPERTY_START_USER.getParamTypeEnum());
        }

        @Test
        @DisplayName("NODE_PROPERTY_PERSONNEL should have MULTIPLAYER param type")
        void personnelParamType() {
            assertEquals(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER,
                    NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getParamTypeEnum());
        }
    }
}
