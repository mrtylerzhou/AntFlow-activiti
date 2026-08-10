package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class MsgProcessEventEnumTest extends BaseTest {

    @Nested
    @DisplayName("ofCode")
    class OfCodeTest {
        @Test
        @DisplayName("should return PROCESS_SUBMIT for code 1")
        void shouldReturnSubmitFor1() {
            assertEquals(MsgProcessEventEnum.PROCESS_SUBMIT, MsgProcessEventEnum.ofCode(1));
        }

        @Test
        @DisplayName("should return PROCESS_APPROVE for code 3")
        void shouldReturnApproveFor3() {
            assertEquals(MsgProcessEventEnum.PROCESS_APPROVE, MsgProcessEventEnum.ofCode(3));
        }

        @Test
        @DisplayName("should return NULL for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertEquals(MsgProcessEventEnum.NULL, MsgProcessEventEnum.ofCode(999));
        }

        @Test
        @DisplayName("should return NULL for null code")
        void shouldReturnNullForNull() {
            assertEquals(MsgProcessEventEnum.NULL, MsgProcessEventEnum.ofCode(null));
        }

        @Test
        @DisplayName("should return PROCESS_FINISH for code 20")
        void shouldReturnFinishFor20() {
            assertEquals(MsgProcessEventEnum.PROCESS_FINISH, MsgProcessEventEnum.ofCode(20));
        }
    }

    @Nested
    @DisplayName("getEnumByCode")
    class GetEnumByCodeTest {
        @Test
        @DisplayName("should return enum for valid code")
        void shouldReturnEnumForValidCode() {
            assertEquals(MsgProcessEventEnum.PROCESS_SUBMIT, MsgProcessEventEnum.getEnumByCode(1));
        }

        @Test
        @DisplayName("should return null for null code")
        void shouldReturnNullForNull() {
            assertNull(MsgProcessEventEnum.getEnumByCode(null));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(MsgProcessEventEnum.getEnumByCode(999));
        }
    }

    @Nested
    @DisplayName("getDescByCode")
    class GetDescByCodeTest {
        @Test
        @DisplayName("should return desc for valid code")
        void shouldReturnDescForValidCode() {
            assertEquals("流程提交操作", MsgProcessEventEnum.getDescByCode(1));
        }

        @Test
        @DisplayName("should return null for null code")
        void shouldReturnNullForNull() {
            assertNull(MsgProcessEventEnum.getDescByCode(null));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(MsgProcessEventEnum.getDescByCode(999));
        }

        @Test
        @DisplayName("should return correct desc for PROCESS_ABORT")
        void shouldReturnDescForAbort() {
            assertEquals("终止", MsgProcessEventEnum.getDescByCode(12));
        }
    }
}
