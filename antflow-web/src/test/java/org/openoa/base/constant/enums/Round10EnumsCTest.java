package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class Round10EnumsCTest extends BaseTest {

    @Nested
    @DisplayName("ProcessEnum")
    class ProcessEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("委托流程", ProcessEnum.getDescByCode(1));
            assertEquals("传阅流程", ProcessEnum.getDescByCode(2));
            assertEquals("发起流程", ProcessEnum.getDescByCode(3));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(ProcessEnum.getDescByCode(999));
        }

        @Test
        @DisplayName("AGENCY_TYPE and HOST_TYPE share code 1")
        void agencyAndHostShareCode() {
            assertEquals(ProcessEnum.AGENCY_TYPE.getCode(), ProcessEnum.HOST_TYPE.getCode());
        }
    }

    @Nested
    @DisplayName("ProcessSubmitStateEnum")
    class ProcessSubmitStateEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("流程提交状态", ProcessSubmitStateEnum.getDescByCode(1));
            assertEquals("流程同意状态", ProcessSubmitStateEnum.getDescByCode(2));
            assertEquals("流程不同意状态", ProcessSubmitStateEnum.getDescByCode(3));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(ProcessSubmitStateEnum.getDescByCode(99));
        }
    }

    @Nested
    @DisplayName("ProcessTypeEnum")
    class ProcessTypeEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("查看流程", ProcessTypeEnum.getDescByCode(1));
            assertEquals("监控流程", ProcessTypeEnum.getDescByCode(2));
            assertEquals("新建流程", ProcessTypeEnum.getDescByCode(3));
            assertEquals("代办流程", ProcessTypeEnum.getDescByCode(5));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(ProcessTypeEnum.getDescByCode(99));
        }
    }

    @Nested
    @DisplayName("ProcesTypeEnum")
    class ProcesTypeEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("DSFZH", ProcesTypeEnum.getDescByCode(1));
        }

        @Test
        @DisplayName("should return code by desc")
        void shouldReturnCodeByDesc() {
            assertEquals(1, ProcesTypeEnum.getCodeByDesc("DSFZH"));
        }

        @Test
        @DisplayName("should return null for non-existent")
        void shouldReturnNullForNonExistent() {
            assertNull(ProcesTypeEnum.getDescByCode(99));
            assertNull(ProcesTypeEnum.getCodeByDesc("不存在"));
        }
    }

    @Nested
    @DisplayName("ProcessJurisdictionEnum")
    class ProcessJurisdictionEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("查看", ProcessJurisdictionEnum.getDescByCode(1));
            assertEquals("创建", ProcessJurisdictionEnum.getDescByCode(2));
            assertEquals("监控", ProcessJurisdictionEnum.getDescByCode(3));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(ProcessJurisdictionEnum.getDescByCode(99));
        }
    }

    @Nested
    @DisplayName("ProcessNoticeEnum")
    class ProcessNoticeEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("邮件", ProcessNoticeEnum.getDescByCode(1));
            assertEquals("短信", ProcessNoticeEnum.getDescByCode(2));
            assertEquals("app推送", ProcessNoticeEnum.getDescByCode(3));
            assertEquals("企微", ProcessNoticeEnum.getDescByCode(5));
            assertEquals("钉钉", ProcessNoticeEnum.getDescByCode(6));
            assertEquals("飞书", ProcessNoticeEnum.getDescByCode(7));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(ProcessNoticeEnum.getDescByCode(99));
        }
    }

    @Nested
    @DisplayName("NoticeReplaceEnum")
    class NoticeReplaceEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("流程类型", NoticeReplaceEnum.getDescByCode(1));
            assertEquals("流程名称", NoticeReplaceEnum.getDescByCode(2));
            assertEquals("流程编号", NoticeReplaceEnum.getDescByCode(7));
        }

        @Test
        @DisplayName("PROCESS_TYPE should have isSelectEmpl=false")
        void processTypeNotSelectEmpl() {
            assertFalse(NoticeReplaceEnum.PROCESS_TYPE.getIsSelectEmpl());
        }

        @Test
        @DisplayName("REJECT_NAME should have isSelectEmpl=true")
        void rejectNameIsSelectEmpl() {
            assertTrue(NoticeReplaceEnum.REJECT_NAME.getIsSelectEmpl());
        }

        @Test
        @DisplayName("PROCESS_TYPE should have filName processType")
        void processTypeFilName() {
            assertEquals("processType", NoticeReplaceEnum.PROCESS_TYPE.getFilName());
        }

        @Test
        @DisplayName("REJECT_NAME should have empty filName")
        void rejectNameEmptyFilName() {
            assertEquals("", NoticeReplaceEnum.REJECT_NAME.getFilName());
        }
    }

    @Nested
    @DisplayName("ApplyType")
    class ApplyTypeTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("流程", ApplyType.getDescByCode(1));
            assertEquals("应用", ApplyType.getDescByCode(2));
        }

        @Test
        @DisplayName("should return code by desc")
        void shouldReturnCodeByDesc() {
            assertEquals(1, ApplyType.getCodeByDesc("流程"));
            assertEquals(2, ApplyType.getCodeByDesc("应用"));
        }

        @Test
        @DisplayName("should return null for non-existent")
        void shouldReturnNullForNonExistent() {
            assertNull(ApplyType.getDescByCode(99));
            assertNull(ApplyType.getCodeByDesc("不存在"));
        }
    }

    @Nested
    @DisplayName("BusinessPartyTypeEnum")
    class BusinessPartyTypeEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("嵌入式", BusinessPartyTypeEnum.getDescByCode(1));
            assertEquals("调入式", BusinessPartyTypeEnum.getDescByCode(2));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(BusinessPartyTypeEnum.getDescByCode(99));
        }
    }

    @Nested
    @DisplayName("VersionIsForceEnums")
    class VersionIsForceEnumsTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("否", VersionIsForceEnums.getDescByCode(0));
            assertEquals("是", VersionIsForceEnums.getDescByCode(1));
        }

        @Test
        @DisplayName("should return code by desc")
        void shouldReturnCodeByDesc() {
            assertEquals(0, VersionIsForceEnums.getCodeByDesc("否"));
            assertEquals(1, VersionIsForceEnums.getCodeByDesc("是"));
        }

        @Test
        @DisplayName("should return null for non-existent")
        void shouldReturnNullForNonExistent() {
            assertNull(VersionIsForceEnums.getDescByCode(99));
            assertNull(VersionIsForceEnums.getCodeByDesc("不存在"));
        }
    }

    @Nested
    @DisplayName("JumpUrlEnum")
    class JumpUrlEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("流程审批页", JumpUrlEnum.getDescByByCode(1));
            assertEquals("流程查看页", JumpUrlEnum.getDescByByCode(2));
            assertEquals("流程待办页", JumpUrlEnum.getDescByByCode(3));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(JumpUrlEnum.getDescByByCode(99));
        }
    }

    @Nested
    @DisplayName("MessageLimit")
    class MessageLimitTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("每日每人最多收取", MessageLimit.getDescByCode(80));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(MessageLimit.getDescByCode(99));
        }

        @Test
        @DisplayName("should have code 80")
        void shouldHaveCode80() {
            assertEquals(80, MessageLimit.EMAIL_A_DAY.getCode());
        }
    }
}
