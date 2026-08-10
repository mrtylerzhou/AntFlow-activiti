package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class Round8EnumsTest extends BaseTest {

    @Nested
    @DisplayName("EventTypeEnum")
    class EventTypeEnumTest {
        @Test
        @DisplayName("should return enum by code")
        void shouldReturnByCode() {
            assertEquals(EventTypeEnum.PROCESS_INITIATE, EventTypeEnum.getByCode(1));
            assertEquals(EventTypeEnum.PROCESS_FLOW, EventTypeEnum.getByCode(3));
            assertEquals(EventTypeEnum.PROCESS_END, EventTypeEnum.getByCode(9));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(EventTypeEnum.getByCode(999));
        }

        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("流程发起", EventTypeEnum.getDescByByCode(1));
            assertEquals("流程流转至当前节点", EventTypeEnum.getDescByByCode(3));
        }

        @Test
        @DisplayName("should return null desc for non-existent code")
        void shouldReturnNullDescForNonExistent() {
            assertNull(EventTypeEnum.getDescByByCode(999));
        }

        @Test
        @DisplayName("should return enum by operation type")
        void shouldReturnByOperationType() {
            assertEquals(EventTypeEnum.PROCESS_INITIATE,
                    EventTypeEnum.getEnumByOperationType(ProcessOperationEnum.BUTTON_TYPE_SUBMIT.getCode()));
            assertEquals(EventTypeEnum.PROCESS_CONSENT,
                    EventTypeEnum.getEnumByOperationType(ProcessOperationEnum.BUTTON_TYPE_AGREE.getCode()));
        }

        @Test
        @DisplayName("should return null for non-existent operation type")
        void shouldReturnNullForNonExistentOpType() {
            assertNull(EventTypeEnum.getEnumByOperationType(999));
        }

        @Test
        @DisplayName("PROCESS_INITIATE should be inNode=true")
        void initiateIsInNode() {
            assertTrue(EventTypeEnum.PROCESS_INITIATE.getIsInNode());
        }

        @Test
        @DisplayName("PROCESS_END should be inNode=false")
        void endIsNotInNode() {
            assertFalse(EventTypeEnum.PROCESS_END.getIsInNode());
        }
    }

    @Nested
    @DisplayName("LFFieldTypeEnum")
    class LFFieldTypeEnumTest {
        @Test
        @DisplayName("should return enum by type")
        void shouldReturnByType() {
            assertEquals(LFFieldTypeEnum.STRING, LFFieldTypeEnum.getByType(1));
            assertEquals(LFFieldTypeEnum.NUMBER, LFFieldTypeEnum.getByType(2));
            assertEquals(LFFieldTypeEnum.BOOLEAN, LFFieldTypeEnum.getByType(6));
        }

        @Test
        @DisplayName("should return null for null type")
        void shouldReturnNullForNull() {
            assertNull(LFFieldTypeEnum.getByType(null));
        }

        @Test
        @DisplayName("should return null for non-existent type")
        void shouldReturnNullForNonExistent() {
            assertNull(LFFieldTypeEnum.getByType(99));
        }

        @Test
        @DisplayName("should use cached allInstances on second call")
        void shouldUseCache() {
            LFFieldTypeEnum.getByType(1);
            LFFieldTypeEnum result = LFFieldTypeEnum.getByType(3);
            assertEquals(LFFieldTypeEnum.DATE, result);
        }
    }

    @Nested
    @DisplayName("SortTypeEnum")
    class SortTypeEnumTest {
        @Test
        @DisplayName("should return enum by code")
        void shouldReturnByCode() {
            assertEquals(SortTypeEnum.ASC, SortTypeEnum.getSortTypeEnumByCode(1));
            assertEquals(SortTypeEnum.DESC, SortTypeEnum.getSortTypeEnumByCode(2));
            assertEquals(SortTypeEnum.FIELD, SortTypeEnum.getSortTypeEnumByCode(3));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(SortTypeEnum.getSortTypeEnumByCode(99));
        }

        @Test
        @DisplayName("ASC should have isAsc=true and mark ' ASC'")
        void ascProperties() {
            assertTrue(SortTypeEnum.ASC.isAsc());
            assertEquals(" ASC", SortTypeEnum.ASC.getMark());
        }

        @Test
        @DisplayName("DESC should have isAsc=false and mark ' DESC'")
        void descProperties() {
            assertFalse(SortTypeEnum.DESC.isAsc());
            assertEquals(" DESC", SortTypeEnum.DESC.getMark());
        }

        @Test
        @DisplayName("FIELD should have isAsc=false and empty mark")
        void fieldProperties() {
            assertFalse(SortTypeEnum.FIELD.isAsc());
            assertEquals("", SortTypeEnum.FIELD.getMark());
        }
    }

    @Nested
    @DisplayName("ApprovalFormCodeEnum")
    class ApprovalFormCodeEnumTest {
        @Test
        @DisplayName("should return null for any code since enum is empty")
        void shouldReturnNullForEmptyEnum() {
            assertNull(ApprovalFormCodeEnum.getEnumByCode("any_code"));
        }

        @Test
        @DisplayName("should return false for exist since enum is empty")
        void shouldReturnFalseForExist() {
            assertFalse(ApprovalFormCodeEnum.exist("any_code"));
        }
    }
}
