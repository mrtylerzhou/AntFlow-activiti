package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class NodeFormAssigneePropertyEnumTest extends BaseTest {

    @Nested
    @DisplayName("getByCode")
    class GetByCodeTest {
        @Test
        @DisplayName("should return FORM_ASSIGNEE for code 1")
        void shouldReturnFormAssigneeFor1() {
            assertEquals(NodeFormAssigneePropertyEnum.FORM_ASSIGNEE,
                    NodeFormAssigneePropertyEnum.getByCode(1));
        }

        @Test
        @DisplayName("should return FORM_ROLE for code 2")
        void shouldReturnFormRoleFor2() {
            assertEquals(NodeFormAssigneePropertyEnum.FORM_ROLE,
                    NodeFormAssigneePropertyEnum.getByCode(2));
        }

        @Test
        @DisplayName("should return null for null code")
        void shouldReturnNullForNull() {
            assertNull(NodeFormAssigneePropertyEnum.getByCode(null));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(NodeFormAssigneePropertyEnum.getByCode(99));
        }

        @Test
        @DisplayName("should return FORM_USER_LOOP_LEADER for code 8")
        void shouldReturnLoopLeaderFor8() {
            assertEquals(NodeFormAssigneePropertyEnum.FORM_USER_LOOP_LEADER,
                    NodeFormAssigneePropertyEnum.getByCode(8));
        }
    }

    @Nested
    @DisplayName("getDescByCode")
    class GetDescByCodeTest {
        @Test
        @DisplayName("should return desc for valid code")
        void shouldReturnDescForValidCode() {
            assertEquals("表单中的人员", NodeFormAssigneePropertyEnum.getDescByCode(1));
        }

        @Test
        @DisplayName("should return empty string for null code (not null!)")
        void shouldReturnEmptyForNull() {
            assertEquals("", NodeFormAssigneePropertyEnum.getDescByCode(null));
        }

        @Test
        @DisplayName("should return empty string for non-existent code")
        void shouldReturnEmptyForNonExistent() {
            assertEquals("", NodeFormAssigneePropertyEnum.getDescByCode(99));
        }

        @Test
        @DisplayName("should return correct desc for FORM_ROLE")
        void shouldReturnDescForFormRole() {
            assertEquals("表单中的角色", NodeFormAssigneePropertyEnum.getDescByCode(2));
        }
    }

    @Nested
    @DisplayName("enum properties")
    class EnumPropertiesTest {
        @Test
        @DisplayName("FORM_ASSIGNEE should have code 1")
        void formAssigneeCode() {
            assertEquals(1, NodeFormAssigneePropertyEnum.FORM_ASSIGNEE.getCode());
        }

        @Test
        @DisplayName("should have 8 enum values")
        void shouldHave8Values() {
            assertEquals(8, NodeFormAssigneePropertyEnum.values().length);
        }
    }
}
