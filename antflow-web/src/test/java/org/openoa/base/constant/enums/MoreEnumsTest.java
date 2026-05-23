package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class MoreEnumsTest extends BaseTest {

    @Nested
    @DisplayName("ConfigFlowButtonSortEnum")
    class ConfigFlowButtonSortEnumTest {
        @Test
        @DisplayName("should return enum by code")
        void shouldReturnEnumByCode() {
            assertEquals(ConfigFlowButtonSortEnum.SUBMIT, ConfigFlowButtonSortEnum.getEnumByCode(1));
            assertEquals(ConfigFlowButtonSortEnum.AGREED, ConfigFlowButtonSortEnum.getEnumByCode(3));
            assertEquals(ConfigFlowButtonSortEnum.PRINT, ConfigFlowButtonSortEnum.getEnumByCode(8));
        }

        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("提交", ConfigFlowButtonSortEnum.getDescByCode(1));
            assertEquals("同意", ConfigFlowButtonSortEnum.getDescByCode(3));
            assertEquals("不同意", ConfigFlowButtonSortEnum.getDescByCode(4));
        }

        @Test
        @DisplayName("should return code by desc")
        void shouldReturnCodeByDesc() {
            assertEquals(1, ConfigFlowButtonSortEnum.getCodeByDesc("提交"));
            assertEquals(3, ConfigFlowButtonSortEnum.getCodeByDesc("同意"));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistentCode() {
            assertNull(ConfigFlowButtonSortEnum.getEnumByCode(999));
            assertNull(ConfigFlowButtonSortEnum.getDescByCode(999));
        }

        @Test
        @DisplayName("should return null for non-existent desc")
        void shouldReturnNullForNonExistentDesc() {
            assertNull(ConfigFlowButtonSortEnum.getCodeByDesc("不存在"));
        }

        @Test
        @DisplayName("SUBMIT should have sort=1")
        void submitSort() {
            assertEquals(1, ConfigFlowButtonSortEnum.SUBMIT.getSort());
        }

        @Test
        @DisplayName("AGREED should have sort=10")
        void agreedSort() {
            assertEquals(10, ConfigFlowButtonSortEnum.AGREED.getSort());
        }
    }

    @Nested
    @DisplayName("MsgNoticeTypeEnum")
    class MsgNoticeTypeEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("工作流流转通知", MsgNoticeTypeEnum.getDescByCode(1));
            assertEquals("工作流完成通知", MsgNoticeTypeEnum.getDescByCode(3));
        }

        @Test
        @DisplayName("should return default value by code")
        void shouldReturnDefaultValueByCode() {
            String defaultVal = MsgNoticeTypeEnum.getDefaultValueByCode(1);
            assertNotNull(defaultVal);
            assertTrue(defaultVal.contains("{流程类型}"));
            assertTrue(defaultVal.contains("{流程名称}"));
        }

        @Test
        @DisplayName("PROCESS_REJECT default should contain {审批不同意者}")
        void rejectDefaultShouldContainDisagreePerson() {
            String defaultVal = MsgNoticeTypeEnum.getDefaultValueByCode(4);
            assertTrue(defaultVal.contains("{审批不同意者}"));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(MsgNoticeTypeEnum.getDescByCode(99));
            assertNull(MsgNoticeTypeEnum.getDefaultValueByCode(99));
        }
    }

    @Nested
    @DisplayName("ProcessOperationEnum")
    class ProcessOperationEnumTest {
        @Test
        @DisplayName("should return enum by code")
        void shouldReturnEnumByCode() {
            assertEquals(ProcessOperationEnum.BUTTON_TYPE_SUBMIT, ProcessOperationEnum.getEnumByCode(1));
            assertEquals(ProcessOperationEnum.BUTTON_TYPE_AGREE, ProcessOperationEnum.getEnumByCode(3));
            assertEquals(ProcessOperationEnum.BUTTON_TYPE_STOP, ProcessOperationEnum.getEnumByCode(12));
        }

        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("流程提交", ProcessOperationEnum.getDescByCode(1));
            assertEquals("同意", ProcessOperationEnum.getDescByCode(3));
            assertEquals("终止", ProcessOperationEnum.getDescByCode(12));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(ProcessOperationEnum.getEnumByCode(999));
            assertNull(ProcessOperationEnum.getDescByCode(999));
        }

        @Test
        @DisplayName("should have outSideMarker constant")
        void shouldHaveOutSideMarker() {
            assertEquals("outSide", ProcessOperationEnum.getOutSideMarker());
            assertEquals("outSideAccess", ProcessOperationEnum.getOutSideAccessmarker());
        }

        @Test
        @DisplayName("should have correct code for all major operations")
        void shouldHaveCorrectCodes() {
            assertEquals(29, ProcessOperationEnum.BUTTON_TYPE_PROCESS_DRAW_BACK.getCode());
            assertEquals(30, ProcessOperationEnum.BUTTON_TYPE_SAVE_DRAFT.getCode());
            assertEquals(33, ProcessOperationEnum.BUTTON_TYPE_PROCESS_MOVE_AHEAD.getCode());
        }
    }
}
