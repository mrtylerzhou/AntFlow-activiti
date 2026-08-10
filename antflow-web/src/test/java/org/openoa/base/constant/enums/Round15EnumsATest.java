package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class Round15EnumsATest extends BaseTest {

    @Nested
    @DisplayName("ProcessButtonEnum")
    class ProcessButtonEnumTest {
        @Test
        @DisplayName("DISAGREE_TYPE should have code 1")
        void disagreeType() {
            assertEquals(1, ProcessButtonEnum.DISAGREE_TYPE.getCode());
            assertEquals("不同意", ProcessButtonEnum.DISAGREE_TYPE.getDesc());
        }

        @Test
        @DisplayName("AGREE_TYPE should have code 2")
        void agreeType() {
            assertEquals(2, ProcessButtonEnum.AGREE_TYPE.getCode());
            assertEquals("同意", ProcessButtonEnum.AGREE_TYPE.getDesc());
        }

        @Test
        @DisplayName("WITHDRAW_TYPE should have code 3")
        void withdrawType() {
            assertEquals(3, ProcessButtonEnum.WITHDRAW_TYPE.getCode());
            assertEquals("撤回", ProcessButtonEnum.WITHDRAW_TYPE.getDesc());
        }

        @Test
        @DisplayName("END_TYPE should have code 4")
        void endType() {
            assertEquals(4, ProcessButtonEnum.END_TYPE.getCode());
            assertEquals("终止", ProcessButtonEnum.END_TYPE.getDesc());
        }

        @Test
        @DisplayName("CHANGE_TYPE should have code 6")
        void changeType() {
            assertEquals(6, ProcessButtonEnum.CHANGE_TYPE.getCode());
            assertEquals("变更处理人", ProcessButtonEnum.CHANGE_TYPE.getDesc());
        }

        @Test
        @DisplayName("FORWARD_TYPE should have code 10")
        void forwardType() {
            assertEquals(10, ProcessButtonEnum.FORWARD_TYPE.getCode());
            assertEquals("转发", ProcessButtonEnum.FORWARD_TYPE.getDesc());
        }

        @Test
        @DisplayName("getDescByCode should return desc for known code")
        void getDescByCode() {
            assertEquals("不同意", ProcessButtonEnum.getDescByCode(1));
            assertEquals("撤回", ProcessButtonEnum.getDescByCode(3));
        }

        @Test
        @DisplayName("getDescByCode should return null for unknown code")
        void getDescByCodeUnknown() {
            assertNull(ProcessButtonEnum.getDescByCode(999));
        }

        @Test
        @DisplayName("getCodeByDesc should return code for known desc")
        void getCodeByDesc() {
            assertEquals(3, ProcessButtonEnum.getCodeByDesc("撤回"));
            assertEquals(4, ProcessButtonEnum.getCodeByDesc("终止"));
        }

        @Test
        @DisplayName("getCodeByDesc should return null for unknown desc")
        void getCodeByDescUnknown() {
            assertNull(ProcessButtonEnum.getCodeByDesc("不存在"));
        }
    }

    @Nested
    @DisplayName("ProcessStateEnum")
    class ProcessStateEnumTest {
        @Test
        @DisplayName("HANDLING_STATE should have code 1")
        void handlingState() {
            assertEquals(1, ProcessStateEnum.HANDLING_STATE.getCode());
            assertEquals("审批中", ProcessStateEnum.HANDLING_STATE.getDesc());
        }

        @Test
        @DisplayName("HANDLED_STATE should have code 2")
        void handledState() {
            assertEquals(2, ProcessStateEnum.HANDLED_STATE.getCode());
            assertEquals("审批通过", ProcessStateEnum.HANDLED_STATE.getDesc());
        }

        @Test
        @DisplayName("END_STATE should have code 3")
        void endState() {
            assertEquals(3, ProcessStateEnum.END_STATE.getCode());
            assertEquals("作废", ProcessStateEnum.END_STATE.getDesc());
        }

        @Test
        @DisplayName("REJECT_STATE should have code 6")
        void rejectState() {
            assertEquals(6, ProcessStateEnum.REJECT_STATE.getCode());
            assertEquals("审批拒绝", ProcessStateEnum.REJECT_STATE.getDesc());
        }

        @Test
        @DisplayName("getDescByCode should return correct desc")
        void getDescByCode() {
            assertEquals("审批中", ProcessStateEnum.getDescByCode(1));
            assertEquals("审批通过", ProcessStateEnum.getDescByCode(2));
            assertEquals("作废", ProcessStateEnum.getDescByCode(3));
            assertEquals("审批拒绝", ProcessStateEnum.getDescByCode(6));
        }

        @Test
        @DisplayName("getDescByCode should return null for unknown code")
        void getDescByCodeUnknown() {
            assertNull(ProcessStateEnum.getDescByCode(99));
        }

        @Test
        @DisplayName("getCodeByDesc should return correct code")
        void getCodeByDesc() {
            assertEquals(1, ProcessStateEnum.getCodeByDesc("审批中"));
            assertEquals(6, ProcessStateEnum.getCodeByDesc("审批拒绝"));
        }

        @Test
        @DisplayName("getCodeByDesc should return null for unknown desc")
        void getCodeByDescUnknown() {
            assertNull(ProcessStateEnum.getCodeByDesc("不存在"));
        }
    }

    @Nested
    @DisplayName("ProcessDisagreeTypeEnum")
    class ProcessDisagreeTypeEnumTest {
        @Test
        @DisplayName("ONE_DISAGREE should have code 1")
        void oneDisagree() {
            assertEquals(1, ProcessDisagreeTypeEnum.ONE_DISAGREE.getCode());
            assertEquals("退回上一个节点提交下一个节点", ProcessDisagreeTypeEnum.ONE_DISAGREE.getDesc());
        }

        @Test
        @DisplayName("THREE_DISAGREE should have code 3")
        void threeDisagree() {
            assertEquals(3, ProcessDisagreeTypeEnum.THREE_DISAGREE.getCode());
            assertEquals("退回发起人提交回退节点", ProcessDisagreeTypeEnum.THREE_DISAGREE.getDesc());
        }

        @Test
        @DisplayName("should have 5 enum values")
        void valueCount() {
            assertEquals(5, ProcessDisagreeTypeEnum.values().length);
        }

        @Test
        @DisplayName("getDescByCode should return correct desc")
        void getDescByCode() {
            assertEquals("退回上一个节点提交下一个节点", ProcessDisagreeTypeEnum.getDescByCode(1));
            assertEquals("退回历史节点提交回退节点", ProcessDisagreeTypeEnum.getDescByCode(5));
        }

        @Test
        @DisplayName("getDescByCode should return null for unknown code")
        void getDescByCodeUnknown() {
            assertNull(ProcessDisagreeTypeEnum.getDescByCode(99));
        }

        @Test
        @DisplayName("getByCode should return correct enum")
        void getByCode() {
            assertEquals(ProcessDisagreeTypeEnum.ONE_DISAGREE, ProcessDisagreeTypeEnum.getByCode(1));
            assertEquals(ProcessDisagreeTypeEnum.FIVE_DISAGREE, ProcessDisagreeTypeEnum.getByCode(5));
        }

        @Test
        @DisplayName("getByCode should return null for null input")
        void getByCodeNull() {
            assertNull(ProcessDisagreeTypeEnum.getByCode(null));
        }

        @Test
        @DisplayName("getByCode should return null for unknown code")
        void getByCodeUnknown() {
            assertNull(ProcessDisagreeTypeEnum.getByCode(99));
        }
    }

    @Nested
    @DisplayName("FieldValueTypeEnum")
    class FieldValueTypeEnumTest {
        @Test
        @DisplayName("STRING should have code 1")
        void stringType() {
            assertEquals(1, FieldValueTypeEnum.STRING.getCode());
            assertEquals("String", FieldValueTypeEnum.STRING.getDesc());
        }

        @Test
        @DisplayName("NUMBER should have code 2")
        void numberType() {
            assertEquals(2, FieldValueTypeEnum.NUMBER.getCode());
            assertEquals("Number", FieldValueTypeEnum.NUMBER.getDesc());
        }

        @Test
        @DisplayName("NUMBERCHOICE should have code 5")
        void numberChoiceType() {
            assertEquals(5, FieldValueTypeEnum.NUMBERCHOICE.getCode());
            assertEquals("NumberChoice", FieldValueTypeEnum.NUMBERCHOICE.getDesc());
        }

        @Test
        @DisplayName("STRINGCHOICE should have code 6")
        void stringChoiceType() {
            assertEquals(6, FieldValueTypeEnum.STRINGCHOICE.getCode());
            assertEquals("StringChoice", FieldValueTypeEnum.STRINGCHOICE.getDesc());
        }

        @Test
        @DisplayName("PERSONCHOICE should have code 7")
        void personChoiceType() {
            assertEquals(7, FieldValueTypeEnum.PERSONCHOICE.getCode());
            assertEquals("PersonChoice", FieldValueTypeEnum.PERSONCHOICE.getDesc());
        }

        @Test
        @DisplayName("should have 6 enum values")
        void valueCount() {
            assertEquals(6, FieldValueTypeEnum.values().length);
        }
    }
}
