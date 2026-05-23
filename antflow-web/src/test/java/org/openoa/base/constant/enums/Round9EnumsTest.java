package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class Round9EnumsTest extends BaseTest {

    @Nested
    @DisplayName("CallbackTypeEnum")
    class CallbackTypeEnumTest {
        @Test
        @DisplayName("PROC_STARTED should map to PROC_BASE_CALL_BACK beanId")
        void startedMapsToBase() {
            assertEquals("PROC_BASE_CALL_BACK", CallbackTypeEnum.PROC_STARTED_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("PROC_COMMIT should map to PROC_BASE_CALL_BACK beanId")
        void commitMapsToBase() {
            assertEquals("PROC_BASE_CALL_BACK", CallbackTypeEnum.PROC_COMMIT_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("PROC_END should map to PROC_BASE_CALL_BACK beanId")
        void endMapsToBase() {
            assertEquals("PROC_BASE_CALL_BACK", CallbackTypeEnum.PROC_END_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("PROC_FINISH should map to PROC_BASE_CALL_BACK beanId")
        void finishMapsToBase() {
            assertEquals("PROC_BASE_CALL_BACK", CallbackTypeEnum.PROC_FINISH_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("CONF_CONDITION should have unique beanId")
        void confConditionUnique() {
            assertEquals("CONF_CONDITION_CALL_BACK", CallbackTypeEnum.CONF_CONDITION_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("PROC_CONDITION should have unique beanId")
        void procConditionUnique() {
            assertEquals("PROC_CONDITION_CALL_BACK", CallbackTypeEnum.PROC_CONDITION_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("PROC_SUBMIT should have unique beanId")
        void procSubmitUnique() {
            assertEquals("PROC_SUBMIT_CALL_BACK", CallbackTypeEnum.PROC_SUBMIT_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("should have 7 enum values")
        void shouldHave7Values() {
            assertEquals(7, CallbackTypeEnum.values().length);
        }
    }

    @Nested
    @DisplayName("AdminPersonnelTypeEnum")
    class AdminPersonnelTypeEnumTest {
        @Test
        @DisplayName("should return enum by type code")
        void shouldReturnByType() {
            assertEquals(AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_PROCESS,
                    AdminPersonnelTypeEnum.getEnumByType(1));
            assertEquals(AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_APPLICATION,
                    AdminPersonnelTypeEnum.getEnumByType(2));
            assertEquals(AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_INTERFACE,
                    AdminPersonnelTypeEnum.getEnumByType(3));
            assertEquals(AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_TEMPLATE,
                    AdminPersonnelTypeEnum.getEnumByType(4));
        }

        @Test
        @DisplayName("should return null for non-existent type")
        void shouldReturnNullForNonExistent() {
            assertNull(AdminPersonnelTypeEnum.getEnumByType(99));
        }

        @Test
        @DisplayName("should return enum by permCode")
        void shouldReturnByPermCode() {
            assertEquals(AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_PROCESS,
                    AdminPersonnelTypeEnum.getEnumByPermCode("YWFLCGL"));
            assertEquals(AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_APPLICATION,
                    AdminPersonnelTypeEnum.getEnumByPermCode("YWFYYGL"));
        }

        @Test
        @DisplayName("should return null for non-existent permCode")
        void shouldReturnNullForNonExistentPermCode() {
            assertNull(AdminPersonnelTypeEnum.getEnumByPermCode("NONEXISTENT"));
        }

        @Test
        @DisplayName("PROCESS should have correct strField/listField/idsField")
        void processFields() {
            AdminPersonnelTypeEnum e = AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_PROCESS;
            assertEquals("processAdminsStr", e.getStrField());
            assertEquals("processAdmins", e.getListField());
            assertEquals("processAdminIds", e.getIdsField());
        }
    }

    @Nested
    @DisplayName("SignTypeEnum")
    class SignTypeEnumTest {
        @Test
        @DisplayName("SIGN_TYPE_SIGN should have code 1")
        void signCode() {
            assertEquals(1, SignTypeEnum.SIGN_TYPE_SIGN.getCode());
            assertEquals("会签（需所有审批人同意，不限顺序）", SignTypeEnum.SIGN_TYPE_SIGN.getDesc());
        }

        @Test
        @DisplayName("SIGN_TYPE_OR_SIGN should have code 2")
        void orSignCode() {
            assertEquals(2, SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode());
        }

        @Test
        @DisplayName("SIGN_TYPE_SIGN_IN_ORDER should have code 3")
        void signInOrderCode() {
            assertEquals(3, SignTypeEnum.SIGN_TYPE_SIGN_IN_ORDER.getCode());
        }

        @Test
        @DisplayName("should have 3 values")
        void shouldHave3Values() {
            assertEquals(3, SignTypeEnum.values().length);
        }
    }

    @Nested
    @DisplayName("NumJudgeOperatorEnum")
    class NumJudgeOperatorEnumTest {
        @Test
        @DisplayName("GT_OR_EQ should have operatorType 1")
        void gtOrEq() {
            assertEquals(1, NumJudgeOperatorEnum.GT_OR_EQ.getOperatorType());
            assertEquals("大于等于", NumJudgeOperatorEnum.GT_OR_EQ.getDesc());
        }

        @Test
        @DisplayName("getCode should return operatorType")
        void getCodeReturnsOperatorType() {
            assertEquals(2, NumJudgeOperatorEnum.GT.getCode());
            assertEquals(3, NumJudgeOperatorEnum.LT_OR_EQ.getCode());
            assertEquals(4, NumJudgeOperatorEnum.LT.getCode());
            assertEquals(5, NumJudgeOperatorEnum.EQ.getCode());
        }

        @Test
        @DisplayName("should have 5 values")
        void shouldHave5Values() {
            assertEquals(5, NumJudgeOperatorEnum.values().length);
        }
    }

    @Nested
    @DisplayName("ElementTypeEnum")
    class ElementTypeEnumTest {
        @Test
        @DisplayName("should have correct code-desc mapping")
        void shouldHaveCorrectMapping() {
            assertEquals("StartEvent", ElementTypeEnum.ELEMENT_TYPE_START_EVENT.getDesc());
            assertEquals("UserTask", ElementTypeEnum.ELEMENT_TYPE_USER_TASK.getDesc());
            assertEquals("Gateway", ElementTypeEnum.ELEMENT_TYPE_GATEWAY.getDesc());
            assertEquals("SequenceFlow", ElementTypeEnum.ELEMENT_TYPE_SEQUENCE_FLOW.getDesc());
            assertEquals("EndEvent", ElementTypeEnum.ELEMENT_TYPE_END_EVENT.getDesc());
            assertEquals("ParallelGateWay", ElementTypeEnum.ELEMENT_TYPE_PARALLEL_GATEWAY.getDesc());
        }

        @Test
        @DisplayName("should have 6 values")
        void shouldHave6Values() {
            assertEquals(6, ElementTypeEnum.values().length);
        }
    }

    @Nested
    @DisplayName("AFGenericValueTypeEnum")
    class AFGenericValueTypeEnumTest {
        @Test
        @DisplayName("should have non-continuous codes")
        void shouldHaveNonContinuousCodes() {
            assertEquals(0, AFGenericValueTypeEnum.STRING.getCode());
            assertEquals(1, AFGenericValueTypeEnum.NUM.getCode());
            assertEquals(3, AFGenericValueTypeEnum.BASEIDTRANSTRUVO.getCode());
            assertEquals(4, AFGenericValueTypeEnum.ARRAY_OF_IDNAMESTRCT.getCode());
            assertEquals(5, AFGenericValueTypeEnum.ARRAY_OF_STRING.getCode());
            assertEquals(6, AFGenericValueTypeEnum.ARRAY_OF_NUM.getCode());
        }

        @Test
        @DisplayName("should have 6 values")
        void shouldHave6Values() {
            assertEquals(6, AFGenericValueTypeEnum.values().length);
        }
    }

    @Nested
    @DisplayName("FilterDataEnum")
    class FilterDataEnumTest {
        @Test
        @DisplayName("should return service class by code")
        void shouldReturnServiceByCode() {
            assertNotNull(FilterDataEnum.getFilterDataServiceByCode(1));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(FilterDataEnum.getFilterDataServiceByCode(99));
        }
    }

    @Nested
    @DisplayName("WildcardCharacterEnum")
    class WildcardCharacterEnumTest {
        @Test
        @DisplayName("ONE_CHARACTER should have isSearchEmpl=false")
        void oneCharacterNotSearchEmpl() {
            assertFalse(WildcardCharacterEnum.ONE_CHARACTER.getIsSearchEmpl());
        }

        @Test
        @DisplayName("THREE_CHARACTER (申请人) should have isSearchEmpl=true")
        void threeCharacterIsSearchEmpl() {
            assertTrue(WildcardCharacterEnum.THREE_CHARACTER.getIsSearchEmpl());
        }

        @Test
        @DisplayName("FOUR_CHARACTER (被审批人) should have isSearchEmpl=true")
        void fourCharacterIsSearchEmpl() {
            assertTrue(WildcardCharacterEnum.FOUR_CHARACTER.getIsSearchEmpl());
        }

        @Test
        @DisplayName("transfDesc should be regex-escaped")
        void transfDescShouldBeEscaped() {
            assertTrue(WildcardCharacterEnum.ONE_CHARACTER.getTransfDesc().contains("\\{"));
            assertTrue(WildcardCharacterEnum.FIVE_CHARACTER.getTransfDesc().contains("\\("));
        }

        @Test
        @DisplayName("should have 9 values")
        void shouldHave9Values() {
            assertEquals(9, WildcardCharacterEnum.values().length);
        }
    }

    @Nested
    @DisplayName("ProcessKeyEnum")
    class ProcessKeyEnumTest {
        @Test
        @DisplayName("should return null for desc by code when enum is empty")
        void shouldReturnNullForDesc() {
            assertNull(ProcessKeyEnum.getDescByCode(1));
        }

        @Test
        @DisplayName("should return null for code by desc when enum is empty")
        void shouldReturnNullForCode() {
            assertNull(ProcessKeyEnum.getCodeByDesc("test"));
        }
    }
}
