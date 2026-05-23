package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class Round17EnumsTest extends BaseTest {

    @Nested
    @DisplayName("NoticeReplaceEnum")
    class NoticeReplaceEnumTest {
        @Test
        @DisplayName("PROCESS_TYPE should have code 1 and filName processType")
        void processType() {
            assertEquals(1, NoticeReplaceEnum.PROCESS_TYPE.getCode());
            assertEquals("流程类型", NoticeReplaceEnum.PROCESS_TYPE.getDesc());
            assertEquals("processType", NoticeReplaceEnum.PROCESS_TYPE.getFilName());
            assertFalse(NoticeReplaceEnum.PROCESS_TYPE.getIsSelectEmpl());
        }

        @Test
        @DisplayName("REJECT_NAME should have isSelectEmpl true and empty filName")
        void rejectName() {
            assertEquals(3, NoticeReplaceEnum.REJECT_NAME.getCode());
            assertEquals("审批不同意者", NoticeReplaceEnum.REJECT_NAME.getDesc());
            assertTrue(NoticeReplaceEnum.REJECT_NAME.getIsSelectEmpl());
            assertEquals("", NoticeReplaceEnum.REJECT_NAME.getFilName());
        }

        @Test
        @DisplayName("OPERATOR should have isSelectEmpl true")
        void operator() {
            assertTrue(NoticeReplaceEnum.OPERATOR.getIsSelectEmpl());
        }

        @Test
        @DisplayName("AFTER_CHANGE_APPROVER should have isSelectEmpl true")
        void afterChangeApprover() {
            assertTrue(NoticeReplaceEnum.AFTER_CHANGE_APPROVER.getIsSelectEmpl());
        }

        @Test
        @DisplayName("ORIGINAL_NODE_APPROVER should have isSelectEmpl true")
        void originalNodeApprover() {
            assertTrue(NoticeReplaceEnum.ORIGINAL_NODE_APPROVER.getIsSelectEmpl());
        }

        @Test
        @DisplayName("PROCESS_ID should have filName processId and isSelectEmpl false")
        void processId() {
            assertEquals("processId", NoticeReplaceEnum.PROCESS_ID.getFilName());
            assertFalse(NoticeReplaceEnum.PROCESS_ID.getIsSelectEmpl());
        }

        @Test
        @DisplayName("should have 7 enum values")
        void valueCount() {
            assertEquals(7, NoticeReplaceEnum.values().length);
        }

        @Test
        @DisplayName("getDescByCode should return correct desc")
        void getDescByCode() {
            assertEquals("流程类型", NoticeReplaceEnum.getDescByCode(1));
            assertEquals("流程名称", NoticeReplaceEnum.getDescByCode(2));
            assertEquals("流程编号", NoticeReplaceEnum.getDescByCode(7));
        }

        @Test
        @DisplayName("getDescByCode should return null for unknown code")
        void getDescByCodeUnknown() {
            assertNull(NoticeReplaceEnum.getDescByCode(99));
        }
    }

    @Nested
    @DisplayName("CallbackTypeEnum")
    class CallbackTypeEnumTest {
        @Test
        @DisplayName("PROC_STARTED should map to PROC_BASE_CALL_BACK beanId")
        void procStartedBeanId() {
            assertEquals("PROC_BASE_CALL_BACK", CallbackTypeEnum.PROC_STARTED_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("PROC_COMMIT should map to PROC_BASE_CALL_BACK beanId")
        void procCommitBeanId() {
            assertEquals("PROC_BASE_CALL_BACK", CallbackTypeEnum.PROC_COMMIT_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("PROC_END should map to PROC_BASE_CALL_BACK beanId")
        void procEndBeanId() {
            assertEquals("PROC_BASE_CALL_BACK", CallbackTypeEnum.PROC_END_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("PROC_FINISH should map to PROC_BASE_CALL_BACK beanId")
        void procFinishBeanId() {
            assertEquals("PROC_BASE_CALL_BACK", CallbackTypeEnum.PROC_FINISH_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("CONF_CONDITION should have unique beanId")
        void confConditionBeanId() {
            assertEquals("CONF_CONDITION_CALL_BACK", CallbackTypeEnum.CONF_CONDITION_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("PROC_CONDITION should have unique beanId")
        void procConditionBeanId() {
            assertEquals("PROC_CONDITION_CALL_BACK", CallbackTypeEnum.PROC_CONDITION_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("PROC_SUBMIT should have unique beanId")
        void procSubmitBeanId() {
            assertEquals("PROC_SUBMIT_CALL_BACK", CallbackTypeEnum.PROC_SUBMIT_CALL_BACK.getBeanId());
        }

        @Test
        @DisplayName("should have 7 enum values")
        void valueCount() {
            assertEquals(7, CallbackTypeEnum.values().length);
        }

        @Test
        @DisplayName("all marks should be unique")
        void uniqueMarks() {
            java.util.Set<String> marks = new java.util.HashSet<>();
            for (CallbackTypeEnum e : CallbackTypeEnum.values()) {
                assertTrue(marks.add(e.getMark()), "Duplicate mark: " + e.getMark());
            }
        }
    }

    @Nested
    @DisplayName("SignTypeEnum")
    class SignTypeEnumTest {
        @Test
        @DisplayName("SIGN_TYPE_SIGN should have code 1")
        void signType() {
            assertEquals(1, SignTypeEnum.SIGN_TYPE_SIGN.getCode());
            assertTrue(SignTypeEnum.SIGN_TYPE_SIGN.getDesc().contains("会签"));
        }

        @Test
        @DisplayName("SIGN_TYPE_OR_SIGN should have code 2")
        void orSignType() {
            assertEquals(2, SignTypeEnum.SIGN_TYPE_OR_SIGN.getCode());
            assertTrue(SignTypeEnum.SIGN_TYPE_OR_SIGN.getDesc().contains("或签"));
        }

        @Test
        @DisplayName("SIGN_TYPE_SIGN_IN_ORDER should have code 3")
        void signInOrderType() {
            assertEquals(3, SignTypeEnum.SIGN_TYPE_SIGN_IN_ORDER.getCode());
            assertTrue(SignTypeEnum.SIGN_TYPE_SIGN_IN_ORDER.getDesc().contains("顺序"));
        }

        @Test
        @DisplayName("should have 3 enum values")
        void valueCount() {
            assertEquals(3, SignTypeEnum.values().length);
        }
    }

    @Nested
    @DisplayName("ElementTypeEnum")
    class ElementTypeEnumTest {
        @Test
        @DisplayName("START_EVENT should have code 1")
        void startEvent() {
            assertEquals(1, ElementTypeEnum.ELEMENT_TYPE_START_EVENT.getCode());
            assertEquals("StartEvent", ElementTypeEnum.ELEMENT_TYPE_START_EVENT.getDesc());
        }

        @Test
        @DisplayName("USER_TASK should have code 2")
        void userTask() {
            assertEquals(2, ElementTypeEnum.ELEMENT_TYPE_USER_TASK.getCode());
            assertEquals("UserTask", ElementTypeEnum.ELEMENT_TYPE_USER_TASK.getDesc());
        }

        @Test
        @DisplayName("GATEWAY should have code 3")
        void gateway() {
            assertEquals(3, ElementTypeEnum.ELEMENT_TYPE_GATEWAY.getCode());
        }

        @Test
        @DisplayName("SEQUENCE_FLOW should have code 4")
        void sequenceFlow() {
            assertEquals(4, ElementTypeEnum.ELEMENT_TYPE_SEQUENCE_FLOW.getCode());
        }

        @Test
        @DisplayName("END_EVENT should have code 5")
        void endEvent() {
            assertEquals(5, ElementTypeEnum.ELEMENT_TYPE_END_EVENT.getCode());
        }

        @Test
        @DisplayName("PARALLEL_GATEWAY should have code 6")
        void parallelGateway() {
            assertEquals(6, ElementTypeEnum.ELEMENT_TYPE_PARALLEL_GATEWAY.getCode());
        }

        @Test
        @DisplayName("should have 6 enum values")
        void valueCount() {
            assertEquals(6, ElementTypeEnum.values().length);
        }
    }

    @Nested
    @DisplayName("ConditionRelationShipEnum")
    class ConditionRelationShipEnumTest {
        @Test
        @DisplayName("AND should have code 0 and value false")
        void andRelation() {
            assertEquals(0, ConditionRelationShipEnum.AND.getCode());
            assertFalse(ConditionRelationShipEnum.AND.getValue());
            assertEquals("and", ConditionRelationShipEnum.AND.getDesc());
        }

        @Test
        @DisplayName("OR should have code 1 and value true")
        void orRelation() {
            assertEquals(1, ConditionRelationShipEnum.OR.getCode());
            assertTrue(ConditionRelationShipEnum.OR.getValue());
            assertEquals("or", ConditionRelationShipEnum.OR.getDesc());
        }

        @Test
        @DisplayName("getCodeByValue should return 0 for false")
        void codeByValueFalse() {
            assertEquals(0, ConditionRelationShipEnum.getCodeByValue(false));
        }

        @Test
        @DisplayName("getCodeByValue should return 1 for true")
        void codeByValueTrue() {
            assertEquals(1, ConditionRelationShipEnum.getCodeByValue(true));
        }

        @Test
        @DisplayName("getCodeByValue should default to 1 (OR) for null")
        void codeByValueNull() {
            assertEquals(1, ConditionRelationShipEnum.getCodeByValue(null));
        }

        @Test
        @DisplayName("getValueByCode should return false for 0")
        void valueByCode0() {
            assertFalse(ConditionRelationShipEnum.getValueByCode(0));
        }

        @Test
        @DisplayName("getValueByCode should return true for 1")
        void valueByCode1() {
            assertTrue(ConditionRelationShipEnum.getValueByCode(1));
        }

        @Test
        @DisplayName("getValueByCode should default to true (OR) for null")
        void valueByCodeNull() {
            assertTrue(ConditionRelationShipEnum.getValueByCode(null));
        }

        @Test
        @DisplayName("getValueByCode should default to true for unknown code")
        void valueByCodeUnknown() {
            assertTrue(ConditionRelationShipEnum.getValueByCode(99));
        }
    }

    @Nested
    @DisplayName("ProcessSubmitStateEnum")
    class ProcessSubmitStateEnumTest {
        @Test
        @DisplayName("PROCESS_SUB_TYPE should have code 1")
        void submitType() {
            assertEquals(1, ProcessSubmitStateEnum.PROCESS_SUB_TYPE.getCode());
            assertEquals("流程提交状态", ProcessSubmitStateEnum.PROCESS_SUB_TYPE.getDesc());
        }

        @Test
        @DisplayName("PROCESS_AGRESS_TYPE should have code 2")
        void agreeType() {
            assertEquals(2, ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode());
        }

        @Test
        @DisplayName("PROCESS_NO_AGRESS_TYPE should have code 3")
        void disagreeType() {
            assertEquals(3, ProcessSubmitStateEnum.PROCESS_NO_AGRESS_TYPE.getCode());
        }

        @Test
        @DisplayName("PROCESS_SIGN_UP should have code 9")
        void signUp() {
            assertEquals(9, ProcessSubmitStateEnum.PROCESS_SIGN_UP.getCode());
            assertEquals("加批", ProcessSubmitStateEnum.PROCESS_SIGN_UP.getDesc());
        }

        @Test
        @DisplayName("should have 9 enum values")
        void valueCount() {
            assertEquals(9, ProcessSubmitStateEnum.values().length);
        }

        @Test
        @DisplayName("getDescByCode should return correct desc")
        void getDescByCode() {
            assertEquals("流程提交状态", ProcessSubmitStateEnum.getDescByCode(1));
            assertEquals("加批", ProcessSubmitStateEnum.getDescByCode(9));
        }

        @Test
        @DisplayName("getDescByCode should return null for unknown code")
        void getDescByCodeUnknown() {
            assertNull(ProcessSubmitStateEnum.getDescByCode(99));
        }
    }

    @Nested
    @DisplayName("BpmnConfFlagsEnum")
    class BpmnConfFlagsEnumTest {
        @Test
        @DisplayName("NOTHING should have code 0")
        void nothing() {
            assertEquals(0, BpmnConfFlagsEnum.NOTHING.getCode());
        }

        @Test
        @DisplayName("HAS_NODE_LABELS should have code 1")
        void nodeLabels() {
            assertEquals(1, BpmnConfFlagsEnum.HAS_NODE_LABELS.getCode());
        }

        @Test
        @DisplayName("HAS_STARTUSER_CHOOSE_MODULES should have code 2")
        void startUserChoose() {
            assertEquals(2, BpmnConfFlagsEnum.HAS_STARTUSER_CHOOSE_MODULES.getCode());
        }

        @Test
        @DisplayName("HAS_DYNAMIC_CONDITIONS should have code 4")
        void dynamicConditions() {
            assertEquals(4, BpmnConfFlagsEnum.HAS_DYNAMIC_CONDITIONS.getCode());
        }

        @Test
        @DisplayName("HAS_COPY should have code 8")
        void copy() {
            assertEquals(8, BpmnConfFlagsEnum.HAS_COPY.getCode());
        }

        @Test
        @DisplayName("binaryOr should combine flags")
        void binaryOr() {
            assertEquals(3, BpmnConfFlagsEnum.binaryOr(1, 2));
            assertEquals(0b111, BpmnConfFlagsEnum.binaryOr(0b11, 0b100));
        }

        @Test
        @DisplayName("binaryOr should treat null as 0")
        void binaryOrNull() {
            assertEquals(1, BpmnConfFlagsEnum.binaryOr(null, 1));
        }

        @Test
        @DisplayName("should have 7 enum values")
        void valueCount() {
            assertEquals(7, BpmnConfFlagsEnum.values().length);
        }
    }

    @Nested
    @DisplayName("MsgProcessEventEnum")
    class MsgProcessEventEnumTest {
        @Test
        @DisplayName("NULL should have code 0")
        void nullEvent() {
            assertEquals(0, MsgProcessEventEnum.NULL.getCode());
        }

        @Test
        @DisplayName("PROCESS_SUBMIT should have code 1")
        void submit() {
            assertEquals(1, MsgProcessEventEnum.PROCESS_SUBMIT.getCode());
        }

        @Test
        @DisplayName("PROCESS_FINISH should have code 20")
        void finish() {
            assertEquals(20, MsgProcessEventEnum.PROCESS_FINISH.getCode());
        }

        @Test
        @DisplayName("ofCode should return correct enum")
        void ofCode() {
            assertEquals(MsgProcessEventEnum.PROCESS_SUBMIT, MsgProcessEventEnum.ofCode(1));
            assertEquals(MsgProcessEventEnum.PROCESS_APPROVE, MsgProcessEventEnum.ofCode(3));
        }

        @Test
        @DisplayName("ofCode should return NULL for unknown code")
        void ofCodeUnknown() {
            assertEquals(MsgProcessEventEnum.NULL, MsgProcessEventEnum.ofCode(999));
        }

        @Test
        @DisplayName("getEnumByCode should return null for null input")
        void getEnumByCodeNull() {
            assertNull(MsgProcessEventEnum.getEnumByCode(null));
        }

        @Test
        @DisplayName("getEnumByCode should return null for unknown code")
        void getEnumByCodeUnknown() {
            assertNull(MsgProcessEventEnum.getEnumByCode(999));
        }

        @Test
        @DisplayName("getDescByCode should return null for null input")
        void getDescByCodeNull() {
            assertNull(MsgProcessEventEnum.getDescByCode(null));
        }

        @Test
        @DisplayName("getDescByCode should return correct desc")
        void getDescByCode() {
            assertEquals("流程提交操作", MsgProcessEventEnum.getDescByCode(1));
            assertEquals("同意", MsgProcessEventEnum.getDescByCode(3));
        }
    }

    @Nested
    @DisplayName("NodePropertyEnum")
    class NodePropertyEnumTest {
        @Test
        @DisplayName("NODE_PROPERTY_LOOP should have code 2")
        void loop() {
            assertEquals(2, NodePropertyEnum.NODE_PROPERTY_LOOP.getCode());
            assertEquals("层层审批", NodePropertyEnum.NODE_PROPERTY_LOOP.getDesc());
        }

        @Test
        @DisplayName("NODE_PROPERTY_PERSONNEL should have code 5")
        void personnel() {
            assertEquals(5, NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getCode());
            assertEquals(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER,
                    NodePropertyEnum.NODE_PROPERTY_PERSONNEL.getParamTypeEnum());
        }

        @Test
        @DisplayName("NODE_PROPERTY_START_USER should have code 12")
        void startUser() {
            assertEquals(12, NodePropertyEnum.NODE_PROPERTY_START_USER.getCode());
            assertEquals(BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_SINGLE,
                    NodePropertyEnum.NODE_PROPERTY_START_USER.getParamTypeEnum());
        }

        @Test
        @DisplayName("getByCode should return correct enum")
        void getByCode() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_LOOP, NodePropertyEnum.getByCode(2));
            assertEquals(NodePropertyEnum.NODE_PROPERTY_PERSONNEL, NodePropertyEnum.getByCode(5));
        }

        @Test
        @DisplayName("getByCode should return null for null input")
        void getByCodeNull() {
            assertNull(NodePropertyEnum.getByCode(null));
        }

        @Test
        @DisplayName("getByCode should return null for unknown code")
        void getByCodeUnknown() {
            assertNull(NodePropertyEnum.getByCode(99));
        }

        @Test
        @DisplayName("getDescByCode should return correct desc")
        void getDescByCode() {
            assertEquals("层层审批", NodePropertyEnum.getDescByCode(2));
            assertEquals("指定人员", NodePropertyEnum.getDescByCode(5));
        }

        @Test
        @DisplayName("getNodePropertyEnumByCode should only return enums with hasPropertyTable=1")
        void getNodePropertyEnumByCode() {
            assertNotNull(NodePropertyEnum.getNodePropertyEnumByCode(2));
            assertEquals("层层审批", NodePropertyEnum.getNodePropertyEnumByCode(2).getDesc());
        }

        @Test
        @DisplayName("all enums should have hasPropertyTable=1")
        void allHavePropertyTable() {
            for (NodePropertyEnum e : NodePropertyEnum.values()) {
                assertEquals(1, e.getHasPropertyTable().intValue());
            }
        }
    }

    @Nested
    @DisplayName("PersonnelEnum")
    class PersonnelEnumTest {
        @Test
        @DisplayName("NODE_LOOP_PERSONNEL should reference NODE_PROPERTY_LOOP")
        void loopPersonnel() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_LOOP, PersonnelEnum.NODE_LOOP_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("USERAPPOINTED_PERSONNEL should reference NODE_PROPERTY_PERSONNEL")
        void appointedPersonnel() {
            assertEquals(NodePropertyEnum.NODE_PROPERTY_PERSONNEL, PersonnelEnum.USERAPPOINTED_PERSONNEL.getNodePropertyEnum());
        }

        @Test
        @DisplayName("fromNodePropertyEnum should return correct PersonnelEnum")
        void fromNodePropertyEnum() {
            assertEquals(PersonnelEnum.NODE_LOOP_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_LOOP));
            assertEquals(PersonnelEnum.ROLE_PERSONNEL,
                    PersonnelEnum.fromNodePropertyEnum(NodePropertyEnum.NODE_PROPERTY_ROLE));
        }

        @Test
        @DisplayName("fromNodePropertyEnum should return null for null input")
        void fromNodePropertyEnumNull() {
            assertNull(PersonnelEnum.fromNodePropertyEnum(null));
        }

        @Test
        @DisplayName("should have 14 enum values")
        void valueCount() {
            assertEquals(14, PersonnelEnum.values().length);
        }
    }
}
