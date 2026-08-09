package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.exception.AFBizException;

import static org.junit.jupiter.api.Assertions.*;

class Round18EnumsATest extends BaseTest {

    @Nested
    @DisplayName("MsgNoticeTypeEnum")
    class MsgNoticeTypeEnumTest {
        @Test
        @DisplayName("PROCESS_FLOW should have code 1 and defaultValue with placeholders")
        void processFlow() {
            assertEquals(1, MsgNoticeTypeEnum.PROCESS_FLOW.getCode());
            assertEquals("工作流流转通知", MsgNoticeTypeEnum.PROCESS_FLOW.getDesc());
            assertTrue(MsgNoticeTypeEnum.PROCESS_FLOW.getDefaultValue().contains("{流程类型}"));
        }

        @Test
        @DisplayName("PROCESS_REJECT should contain {审批不同意者} placeholder")
        void processReject() {
            assertTrue(MsgNoticeTypeEnum.PROCESS_REJECT.getDefaultValue().contains("{审批不同意者}"));
        }

        @Test
        @DisplayName("PROCESS_STOP should contain {操作者} placeholder")
        void processStop() {
            assertTrue(MsgNoticeTypeEnum.PROCESS_STOP.getDefaultValue().contains("{操作者}"));
        }

        @Test
        @DisplayName("PROCESS_CHANGE_OPERATOR should contain {变更后处理人} placeholder")
        void changeOperator() {
            assertTrue(MsgNoticeTypeEnum.PROCESS_CHANGE_OPERATOR.getDefaultValue().contains("{变更后处理人}"));
        }

        @Test
        @DisplayName("PROCESS_CHANGE_NOW_OPERATOR should contain {原审批节点处理人} placeholder")
        void changeNowOperator() {
            assertTrue(MsgNoticeTypeEnum.PROCESS_CHANGE_NOW_OPERATOR.getDefaultValue().contains("{原审批节点处理人}"));
        }

        @Test
        @DisplayName("should have 10 enum values")
        void valueCount() {
            assertEquals(10, MsgNoticeTypeEnum.values().length);
        }

        @Test
        @DisplayName("getDescByCode should return correct desc")
        void getDescByCode() {
            assertEquals("工作流流转通知", MsgNoticeTypeEnum.getDescByCode(1));
            assertEquals("工作流完成通知", MsgNoticeTypeEnum.getDescByCode(3));
        }

        @Test
        @DisplayName("getDescByCode should return null for unknown code")
        void getDescByCodeUnknown() {
            assertNull(MsgNoticeTypeEnum.getDescByCode(99));
        }

        @Test
        @DisplayName("getDefaultValueByCode should return correct defaultValue")
        void getDefaultValueByCode() {
            assertNotNull(MsgNoticeTypeEnum.getDefaultValueByCode(1));
            assertTrue(MsgNoticeTypeEnum.getDefaultValueByCode(1).contains("{流程编号}"));
        }

        @Test
        @DisplayName("getDefaultValueByCode should return null for unknown code")
        void getDefaultValueByCodeUnknown() {
            assertNull(MsgNoticeTypeEnum.getDefaultValueByCode(99));
        }
    }

    @Nested
    @DisplayName("AdminPersonnelTypeEnum")
    class AdminPersonnelTypeEnumTest {
        @Test
        @DisplayName("PROCESS type should have code 1 and permCode YWFLCGL")
        void processType() {
            assertEquals(1, AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_PROCESS.getCode());
            assertEquals("流程管理员", AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_PROCESS.getDesc());
            assertEquals("YWFLCGL", AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_PROCESS.getPermCode());
        }

        @Test
        @DisplayName("APPLICATION type should have permCode YWFYYGL")
        void applicationType() {
            assertEquals("YWFYYGL", AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_APPLICATION.getPermCode());
        }

        @Test
        @DisplayName("INTERFACE type should have permCode YWFJKGL")
        void interfaceType() {
            assertEquals("YWFJKGL", AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_INTERFACE.getPermCode());
        }

        @Test
        @DisplayName("TEMPLATE type should have permCode YWFMBGL")
        void templateType() {
            assertEquals("YWFMBGL", AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_TEMPLATE.getPermCode());
        }

        @Test
        @DisplayName("all permCodes should be unique")
        void uniquePermCodes() {
            java.util.Set<String> codes = new java.util.HashSet<>();
            for (AdminPersonnelTypeEnum e : AdminPersonnelTypeEnum.values()) {
                assertTrue(codes.add(e.getPermCode()), "Duplicate permCode: " + e.getPermCode());
            }
        }

        @Test
        @DisplayName("getEnumByType should return correct enum")
        void getEnumByType() {
            assertEquals(AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_PROCESS, AdminPersonnelTypeEnum.getEnumByType(1));
            assertEquals(AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_TEMPLATE, AdminPersonnelTypeEnum.getEnumByType(4));
        }

        @Test
        @DisplayName("getEnumByType should return null for unknown code")
        void getEnumByTypeUnknown() {
            assertNull(AdminPersonnelTypeEnum.getEnumByType(99));
        }

        @Test
        @DisplayName("getEnumByPermCode should return correct enum")
        void getEnumByPermCode() {
            assertEquals(AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_PROCESS, AdminPersonnelTypeEnum.getEnumByPermCode("YWFLCGL"));
            assertEquals(AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_TEMPLATE, AdminPersonnelTypeEnum.getEnumByPermCode("YWFMBGL"));
        }

        @Test
        @DisplayName("getEnumByPermCode should return null for unknown permCode")
        void getEnumByPermCodeUnknown() {
            assertNull(AdminPersonnelTypeEnum.getEnumByPermCode("UNKNOWN"));
        }

        @Test
        @DisplayName("strField and listField should follow naming pattern")
        void fieldNamingPattern() {
            for (AdminPersonnelTypeEnum e : AdminPersonnelTypeEnum.values()) {
                assertTrue(e.getStrField().endsWith("Str"), e.name() + " strField should end with 'Str'");
                assertTrue(e.getListField().endsWith("s"), e.name() + " listField should end with 's'");
                assertTrue(e.getIdsField().endsWith("Ids"), e.name() + " idsField should end with 'Ids'");
            }
        }
    }

    @Nested
    @DisplayName("InformEnum")
    class InformEnumTest {
        @Test
        @DisplayName("APPLICANT should have code 1 and filName startUser")
        void applicant() {
            assertEquals(1, InformEnum.APPLICANT.getCode());
            assertEquals("申请人", InformEnum.APPLICANT.getDesc());
            assertEquals("startUser", InformEnum.APPLICANT.getFilName());
        }

        @Test
        @DisplayName("ALL_APPROVER should have filName approveds")
        void allApprover() {
            assertEquals("approveds", InformEnum.ALL_APPROVER.getFilName());
        }

        @Test
        @DisplayName("should have 6 enum values")
        void valueCount() {
            assertEquals(6, InformEnum.values().length);
        }

        @Test
        @DisplayName("getEnumByByCode should return correct enum")
        void getEnumByByCode() {
            assertEquals(InformEnum.APPLICANT, InformEnum.getEnumByByCode(1));
            assertEquals(InformEnum.ASSIGNEED_ROLES, InformEnum.getEnumByByCode(6));
        }

        @Test
        @DisplayName("getEnumByByCode should return null for unknown code")
        void getEnumByByCodeUnknown() {
            assertNull(InformEnum.getEnumByByCode(99));
        }

        @Test
        @DisplayName("getDescByByCode should return correct desc")
        void getDescByByCode() {
            assertEquals("申请人", InformEnum.getDescByByCode(1));
            assertEquals("指定角色", InformEnum.getDescByByCode(6));
        }

        @Test
        @DisplayName("getDescByByCode should return null for null input")
        void getDescByByCodeNull() {
            assertNull(InformEnum.getDescByByCode(null));
        }

        @Test
        @DisplayName("getDescByByCode should return null for unknown code")
        void getDescByByCodeUnknown() {
            assertNull(InformEnum.getDescByByCode(99));
        }
    }

    @Nested
    @DisplayName("SortTypeEnum")
    class SortTypeEnumTest {
        @Test
        @DisplayName("ASC should have code 1 and isAsc true")
        void asc() {
            assertEquals(1, SortTypeEnum.ASC.getCode());
            assertEquals(" ASC", SortTypeEnum.ASC.getMark());
            assertTrue(SortTypeEnum.ASC.isAsc());
        }

        @Test
        @DisplayName("DESC should have code 2 and isAsc false")
        void desc() {
            assertEquals(2, SortTypeEnum.DESC.getCode());
            assertEquals(" DESC", SortTypeEnum.DESC.getMark());
            assertFalse(SortTypeEnum.DESC.isAsc());
        }

        @Test
        @DisplayName("FIELD should have code 3 and empty mark")
        void field() {
            assertEquals(3, SortTypeEnum.FIELD.getCode());
            assertEquals("", SortTypeEnum.FIELD.getMark());
            assertFalse(SortTypeEnum.FIELD.isAsc());
        }

        @Test
        @DisplayName("getSortTypeEnumByCode should return correct enum")
        void getByCode() {
            assertEquals(SortTypeEnum.ASC, SortTypeEnum.getSortTypeEnumByCode(1));
            assertEquals(SortTypeEnum.DESC, SortTypeEnum.getSortTypeEnumByCode(2));
            assertEquals(SortTypeEnum.FIELD, SortTypeEnum.getSortTypeEnumByCode(3));
        }

        @Test
        @DisplayName("getSortTypeEnumByCode should return null for unknown code")
        void getByCodeUnknown() {
            assertNull(SortTypeEnum.getSortTypeEnumByCode(99));
        }
    }

    @Nested
    @DisplayName("ButtonPageTypeEnum")
    class ButtonPageTypeEnumTest {
        @Test
        @DisplayName("INITIATE should have code 1 and name initiate")
        void initiate() {
            assertEquals(1, ButtonPageTypeEnum.INITIATE.getCode());
            assertEquals("initiate", ButtonPageTypeEnum.INITIATE.getName());
            assertEquals("发起页", ButtonPageTypeEnum.INITIATE.getDesc());
        }

        @Test
        @DisplayName("AUDIT should have code 2 and name audit")
        void audit() {
            assertEquals(2, ButtonPageTypeEnum.AUDIT.getCode());
            assertEquals("audit", ButtonPageTypeEnum.AUDIT.getName());
        }

        @Test
        @DisplayName("TO_VIEW should have code 3 and name toView")
        void toView() {
            assertEquals(3, ButtonPageTypeEnum.TO_VIEW.getCode());
            assertEquals("toView", ButtonPageTypeEnum.TO_VIEW.getName());
        }

        @Test
        @DisplayName("getDescByCode should return correct desc")
        void getDescByCode() {
            assertEquals("发起页", ButtonPageTypeEnum.getDescByCode(1));
            assertEquals("审批页", ButtonPageTypeEnum.getDescByCode(2));
            assertEquals("查看页", ButtonPageTypeEnum.getDescByCode(3));
        }

        @Test
        @DisplayName("getCodeByDesc should return correct code")
        void getCodeByDesc() {
            assertEquals(1, ButtonPageTypeEnum.getCodeByDesc("发起页"));
            assertEquals(2, ButtonPageTypeEnum.getCodeByDesc("审批页"));
            assertEquals(3, ButtonPageTypeEnum.getCodeByDesc("查看页"));
        }

        @Test
        @DisplayName("getCodeByDesc should return null for unknown desc")
        void getCodeByDescUnknown() {
            assertNull(ButtonPageTypeEnum.getCodeByDesc("不存在"));
        }
    }

    @Nested
    @DisplayName("VariantFormContainerTypeEnum")
    class VariantFormContainerTypeEnumTest {
        @Test
        @DisplayName("CARD should have typeName card")
        void card() {
            assertEquals("card", VariantFormContainerTypeEnum.CARD.getTypeName());
            assertEquals("卡片", VariantFormContainerTypeEnum.CARD.getDesc());
        }

        @Test
        @DisplayName("TAB should have typeName tab")
        void tab() {
            assertEquals("tab", VariantFormContainerTypeEnum.TAB.getTypeName());
        }

        @Test
        @DisplayName("TABLE should have typeName table")
        void table() {
            assertEquals("table", VariantFormContainerTypeEnum.TABLE.getTypeName());
        }

        @Test
        @DisplayName("GRID should have typeName grid")
        void grid() {
            assertEquals("grid", VariantFormContainerTypeEnum.GRID.getTypeName());
        }

        @Test
        @DisplayName("getByTypeName should return correct enum")
        void getByTypeName() {
            assertEquals(VariantFormContainerTypeEnum.CARD, VariantFormContainerTypeEnum.getByTypeName("card"));
            assertEquals(VariantFormContainerTypeEnum.GRID, VariantFormContainerTypeEnum.getByTypeName("grid"));
        }

        @Test
        @DisplayName("getByTypeName should return null for null input")
        void getByTypeNameNull() {
            assertNull(VariantFormContainerTypeEnum.getByTypeName(null));
        }

        @Test
        @DisplayName("getByTypeName should return null for empty input")
        void getByTypeNameEmpty() {
            assertNull(VariantFormContainerTypeEnum.getByTypeName(""));
        }

        @Test
        @DisplayName("getByTypeName should be case sensitive")
        void caseSensitive() {
            assertNull(VariantFormContainerTypeEnum.getByTypeName("Card"));
            assertNull(VariantFormContainerTypeEnum.getByTypeName("GRID"));
        }
    }

    @Nested
    @DisplayName("HrbpTypeEnum")
    class HrbpTypeEnumTest {
        @Test
        @DisplayName("HRBP should have code 0")
        void hrbp() {
            assertEquals(0, HrbpTypeEnum.HRBP.getCode());
            assertEquals("hrbp", HrbpTypeEnum.HRBP.getDesc());
        }

        @Test
        @DisplayName("HRBP_LEADER should have code 2")
        void hrbpLeader() {
            assertEquals(2, HrbpTypeEnum.HRBP_LEADER.getCode());
            assertEquals("hrbp leader", HrbpTypeEnum.HRBP_LEADER.getDesc());
        }

        @Test
        @DisplayName("should have 2 enum values")
        void valueCount() {
            assertEquals(2, HrbpTypeEnum.values().length);
        }
    }

    @Nested
    @DisplayName("ProcessNoticeEnum")
    class ProcessNoticeEnumTest {
        @Test
        @DisplayName("EMAIL_TYPE should have code 1")
        void email() {
            assertEquals(1, ProcessNoticeEnum.EMAIL_TYPE.getCode());
            assertEquals("邮件", ProcessNoticeEnum.EMAIL_TYPE.getDesc());
        }

        @Test
        @DisplayName("PHONE_TYPE should have code 2")
        void phone() {
            assertEquals(2, ProcessNoticeEnum.PHONE_TYPE.getCode());
        }

        @Test
        @DisplayName("WECHAT_TYPE should have code 5 (no code 4)")
        void wechat() {
            assertEquals(5, ProcessNoticeEnum.WECHAT_TYPE.getCode());
        }

        @Test
        @DisplayName("DING_TALK_TYPE should have code 6")
        void dingTalk() {
            assertEquals(6, ProcessNoticeEnum.DING_TALK_TYPE.getCode());
        }

        @Test
        @DisplayName("FEISHU_TYPE should have code 7")
        void feishu() {
            assertEquals(7, ProcessNoticeEnum.FEISHU_TYPE.getCode());
        }

        @Test
        @DisplayName("getDescByCode should return correct desc")
        void getDescByCode() {
            assertEquals("邮件", ProcessNoticeEnum.getDescByCode(1));
            assertEquals("钉钉", ProcessNoticeEnum.getDescByCode(6));
        }

        @Test
        @DisplayName("getDescByCode should return null for unknown code")
        void getDescByCodeUnknown() {
            assertNull(ProcessNoticeEnum.getDescByCode(99));
        }
    }

    @Nested
    @DisplayName("ProcessJurisdictionEnum")
    class ProcessJurisdictionEnumTest {
        @Test
        @DisplayName("VIEW_TYPE should have code 1")
        void viewType() {
            assertEquals(1, ProcessJurisdictionEnum.VIEW_TYPE.getCode());
            assertEquals("查看", ProcessJurisdictionEnum.VIEW_TYPE.getDesc());
        }

        @Test
        @DisplayName("CREATE_TYPE should have code 2")
        void createType() {
            assertEquals(2, ProcessJurisdictionEnum.CREATE_TYPE.getCode());
            assertEquals("创建", ProcessJurisdictionEnum.CREATE_TYPE.getDesc());
        }

        @Test
        @DisplayName("CONTROL_TYPE should have code 3")
        void controlType() {
            assertEquals(3, ProcessJurisdictionEnum.CONTROL_TYPE.getCode());
            assertEquals("监控", ProcessJurisdictionEnum.CONTROL_TYPE.getDesc());
        }

        @Test
        @DisplayName("getDescByCode should return correct desc")
        void getDescByCode() {
            assertEquals("查看", ProcessJurisdictionEnum.getDescByCode(1));
            assertEquals("监控", ProcessJurisdictionEnum.getDescByCode(3));
        }

        @Test
        @DisplayName("getDescByCode should return null for unknown code")
        void getDescByCodeUnknown() {
            assertNull(ProcessJurisdictionEnum.getDescByCode(99));
        }
    }
}
