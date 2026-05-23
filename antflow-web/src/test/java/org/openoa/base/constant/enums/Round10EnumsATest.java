package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class Round10EnumsATest extends BaseTest {

    @Nested
    @DisplayName("ProcessButtonEnum")
    class ProcessButtonEnumTest {
        @Test
        @DisplayName("getDescByCode should return first match for duplicate code")
        void shouldReturnFirstMatchForDuplicateCode() {
            assertEquals("不同意", ProcessButtonEnum.getDescByCode(1));
        }

        @Test
        @DisplayName("getCodeByDesc should return code by desc")
        void shouldReturnCodeByDesc() {
            assertEquals(1, ProcessButtonEnum.getCodeByDesc("不同意"));
            assertEquals(2, ProcessButtonEnum.getCodeByDesc("同意"));
            assertEquals(3, ProcessButtonEnum.getCodeByDesc("撤回"));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistentCode() {
            assertNull(ProcessButtonEnum.getDescByCode(999));
        }

        @Test
        @DisplayName("should return null for non-existent desc")
        void shouldReturnNullForNonExistentDesc() {
            assertNull(ProcessButtonEnum.getCodeByDesc("不存在"));
        }

        @Test
        @DisplayName("DISAGREE_TYPE and VIEW_TYPE share code 1")
        void disagreeAndViewShareCode() {
            assertEquals(ProcessButtonEnum.DISAGREE_TYPE.getCode(),
                    ProcessButtonEnum.VIEW_TYPE.getCode());
            assertNotEquals(ProcessButtonEnum.DISAGREE_TYPE.getDesc(),
                    ProcessButtonEnum.VIEW_TYPE.getDesc());
        }

        @Test
        @DisplayName("AGREE_TYPE and DEAL_WITH_TYPE share code 2")
        void agreeAndDealWithShareCode() {
            assertEquals(ProcessButtonEnum.AGREE_TYPE.getCode(),
                    ProcessButtonEnum.DEAL_WITH_TYPE.getCode());
        }
    }

    @Nested
    @DisplayName("ProcessBusinessCallBackTypeEnum")
    class ProcessBusinessCallBackTypeEnumTest {
        @Test
        @DisplayName("should return enum by code")
        void shouldReturnByCode() {
            assertEquals(ProcessBusinessCallBackTypeEnum.Send_MQ_Message,
                    ProcessBusinessCallBackTypeEnum.getEnumByCode(1));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(ProcessBusinessCallBackTypeEnum.getEnumByCode(99));
        }

        @Test
        @DisplayName("should have adaptor class")
        void shouldHaveAdaptorClass() {
            assertNotNull(ProcessBusinessCallBackTypeEnum.Send_MQ_Message.getClsz());
        }
    }

    @Nested
    @DisplayName("VariantFormContainerTypeEnum")
    class VariantFormContainerTypeEnumTest {
        @Test
        @DisplayName("should return by typeName")
        void shouldReturnByTypeName() {
            assertEquals(VariantFormContainerTypeEnum.CARD, VariantFormContainerTypeEnum.getByTypeName("card"));
            assertEquals(VariantFormContainerTypeEnum.TAB, VariantFormContainerTypeEnum.getByTypeName("tab"));
            assertEquals(VariantFormContainerTypeEnum.TABLE, VariantFormContainerTypeEnum.getByTypeName("table"));
            assertEquals(VariantFormContainerTypeEnum.GRID, VariantFormContainerTypeEnum.getByTypeName("grid"));
        }

        @Test
        @DisplayName("should return null for empty typeName")
        void shouldReturnNullForEmpty() {
            assertNull(VariantFormContainerTypeEnum.getByTypeName(""));
        }

        @Test
        @DisplayName("should return null for null typeName")
        void shouldReturnNullForNull() {
            assertNull(VariantFormContainerTypeEnum.getByTypeName(null));
        }

        @Test
        @DisplayName("should return null for unknown typeName")
        void shouldReturnNullForUnknown() {
            assertNull(VariantFormContainerTypeEnum.getByTypeName("unknown"));
        }
    }

    @Nested
    @DisplayName("MessageSendTypeEnum")
    class MessageSendTypeEnumTest {
        @Test
        @DisplayName("should return enum by code")
        void shouldReturnByCode() {
            assertEquals(MessageSendTypeEnum.MAIL, MessageSendTypeEnum.getEnumByCode(1));
            assertEquals(MessageSendTypeEnum.MESSAGE, MessageSendTypeEnum.getEnumByCode(2));
            assertEquals(MessageSendTypeEnum.ALL, MessageSendTypeEnum.getEnumByCode(50));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(MessageSendTypeEnum.getEnumByCode(99));
        }
    }

    @Nested
    @DisplayName("InformEnum")
    class InformEnumTest {
        @Test
        @DisplayName("should return enum by code")
        void shouldReturnByCode() {
            assertEquals(InformEnum.APPLICANT, InformEnum.getEnumByByCode(1));
            assertEquals(InformEnum.CURRENT_APPROVER, InformEnum.getEnumByByCode(3));
        }

        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("申请人", InformEnum.getDescByByCode(1));
            assertEquals("当前节点审批人", InformEnum.getDescByByCode(3));
        }

        @Test
        @DisplayName("should return null desc for null code")
        void shouldReturnNullDescForNull() {
            assertNull(InformEnum.getDescByByCode(null));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(InformEnum.getEnumByByCode(99));
            assertNull(InformEnum.getDescByByCode(99));
        }

        @Test
        @DisplayName("APPLICANT should have filName startUser")
        void applicantFilName() {
            assertEquals("startUser", InformEnum.APPLICANT.getFilName());
        }

        @Test
        @DisplayName("CURRENT_APPROVER should have filName assignee")
        void currentApproverFilName() {
            assertEquals("assignee", InformEnum.CURRENT_APPROVER.getFilName());
        }
    }

    @Nested
    @DisplayName("ButtonPageTypeEnum")
    class ButtonPageTypeEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("发起页", ButtonPageTypeEnum.getDescByCode(1));
            assertEquals("审批页", ButtonPageTypeEnum.getDescByCode(2));
            assertEquals("查看页", ButtonPageTypeEnum.getDescByCode(3));
        }

        @Test
        @DisplayName("should return code by desc")
        void shouldReturnCodeByDesc() {
            assertEquals(1, ButtonPageTypeEnum.getCodeByDesc("发起页"));
            assertEquals(2, ButtonPageTypeEnum.getCodeByDesc("审批页"));
        }

        @Test
        @DisplayName("should return null for non-existent")
        void shouldReturnNullForNonExistent() {
            assertNull(ButtonPageTypeEnum.getDescByCode(99));
            assertNull(ButtonPageTypeEnum.getCodeByDesc("不存在"));
        }

        @Test
        @DisplayName("INITIATE should have name 'initiate'")
        void initiateName() {
            assertEquals("initiate", ButtonPageTypeEnum.INITIATE.getName());
        }
    }
}
