package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class Round15EnumsBTest extends BaseTest {

    @Nested
    @DisplayName("ProcessOperationEnum")
    class ProcessOperationEnumTest {
        @Test
        @DisplayName("BUTTON_TYPE_SUBMIT should have code 1")
        void submit() {
            assertEquals(1, ProcessOperationEnum.BUTTON_TYPE_SUBMIT.getCode());
            assertEquals("流程提交", ProcessOperationEnum.BUTTON_TYPE_SUBMIT.getDesc());
        }

        @Test
        @DisplayName("BUTTON_TYPE_AGREE should have code 3")
        void agree() {
            assertEquals(3, ProcessOperationEnum.BUTTON_TYPE_AGREE.getCode());
            assertEquals("同意", ProcessOperationEnum.BUTTON_TYPE_AGREE.getDesc());
        }

        @Test
        @DisplayName("BUTTON_TYPE_DIS_AGREE should have code 4")
        void disagree() {
            assertEquals(4, ProcessOperationEnum.BUTTON_TYPE_DIS_AGREE.getCode());
        }

        @Test
        @DisplayName("BUTTON_TYPE_ADD_ASSIGNEE should have code 25")
        void addAssignee() {
            assertEquals(25, ProcessOperationEnum.BUTTON_TYPE_ADD_ASSIGNEE.getCode());
            assertEquals("加签", ProcessOperationEnum.BUTTON_TYPE_ADD_ASSIGNEE.getDesc());
        }

        @Test
        @DisplayName("BUTTON_TYPE_SAVE_DRAFT should have code 30")
        void saveDraft() {
            assertEquals(30, ProcessOperationEnum.BUTTON_TYPE_SAVE_DRAFT.getCode());
        }

        @Test
        @DisplayName("getEnumByCode should return correct enum")
        void getEnumByCode() {
            assertEquals(ProcessOperationEnum.BUTTON_TYPE_SUBMIT, ProcessOperationEnum.getEnumByCode(1));
            assertEquals(ProcessOperationEnum.BUTTON_TYPE_AGREE, ProcessOperationEnum.getEnumByCode(3));
        }

        @Test
        @DisplayName("getEnumByCode should return null for unknown code")
        void getEnumByCodeUnknown() {
            assertNull(ProcessOperationEnum.getEnumByCode(999));
        }

        @Test
        @DisplayName("getDescByCode should return correct desc")
        void getDescByCode() {
            assertEquals("流程提交", ProcessOperationEnum.getDescByCode(1));
            assertEquals("同意", ProcessOperationEnum.getDescByCode(3));
        }

        @Test
        @DisplayName("getDescByCode should return null for unknown code")
        void getDescByCodeUnknown() {
            assertNull(ProcessOperationEnum.getDescByCode(999));
        }

        @Test
        @DisplayName("outSideMarker should be 'outSide'")
        void outSideMarker() {
            assertEquals("outSide", ProcessOperationEnum.getOutSideMarker());
        }

        @Test
        @DisplayName("outSideAccessmarker should be 'outSideAccess'")
        void outSideAccessmarker() {
            assertEquals("outSideAccess", ProcessOperationEnum.getOutSideAccessmarker());
        }
    }

    @Nested
    @DisplayName("ButtonTypeEnum")
    class ButtonTypeEnumTest {
        @Test
        @DisplayName("BUTTON_TYPE_PREVIEW should have code 0")
        void preview() {
            assertEquals(0, ButtonTypeEnum.BUTTON_TYPE_PREVIEW.getCode());
            assertEquals("预览", ButtonTypeEnum.BUTTON_TYPE_PREVIEW.getDesc());
        }

        @Test
        @DisplayName("BUTTON_TYPE_SUBMIT should have code 1")
        void submit() {
            assertEquals(1, ButtonTypeEnum.BUTTON_TYPE_SUBMIT.getCode());
            assertEquals("提交", ButtonTypeEnum.BUTTON_TYPE_SUBMIT.getDesc());
        }

        @Test
        @DisplayName("BUTTON_TYPE_AGREE should have code 3")
        void agree() {
            assertEquals(3, ButtonTypeEnum.BUTTON_TYPE_AGREE.getCode());
            assertEquals("同意", ButtonTypeEnum.BUTTON_TYPE_AGREE.getDesc());
        }

        @Test
        @DisplayName("BUTTON_TYPE_PROCESS_DRAW_BACK should have code 29")
        void drawBack() {
            assertEquals(29, ButtonTypeEnum.BUTTON_TYPE_PROCESS_DRAW_BACK.getCode());
            assertEquals("流程撤回", ButtonTypeEnum.BUTTON_TYPE_PROCESS_DRAW_BACK.getDesc());
        }

        @Test
        @DisplayName("getDescByCode should return correct desc")
        void getDescByCode() {
            assertEquals("提交", ButtonTypeEnum.getDescByCode(1));
            assertEquals("同意", ButtonTypeEnum.getDescByCode(3));
            assertEquals("不同意", ButtonTypeEnum.getDescByCode(4));
        }

        @Test
        @DisplayName("getDescByCode should return null for unknown code")
        void getDescByCodeUnknown() {
            assertNull(ButtonTypeEnum.getDescByCode(999));
        }
    }

    @Nested
    @DisplayName("NumJudgeOperatorEnum")
    class NumJudgeOperatorEnumTest {
        @Test
        @DisplayName("GT_OR_EQ should have code 1")
        void gtOrEq() {
            assertEquals(1, NumJudgeOperatorEnum.GT_OR_EQ.getCode());
            assertEquals("大于等于", NumJudgeOperatorEnum.GT_OR_EQ.getDesc());
        }

        @Test
        @DisplayName("GT should have code 2")
        void gt() {
            assertEquals(2, NumJudgeOperatorEnum.GT.getCode());
            assertEquals("大于", NumJudgeOperatorEnum.GT.getDesc());
        }

        @Test
        @DisplayName("LT_OR_EQ should have code 3")
        void ltOrEq() {
            assertEquals(3, NumJudgeOperatorEnum.LT_OR_EQ.getCode());
            assertEquals("小于等于", NumJudgeOperatorEnum.LT_OR_EQ.getDesc());
        }

        @Test
        @DisplayName("LT should have code 4")
        void lt() {
            assertEquals(4, NumJudgeOperatorEnum.LT.getCode());
            assertEquals("小于", NumJudgeOperatorEnum.LT.getDesc());
        }

        @Test
        @DisplayName("EQ should have code 5")
        void eq() {
            assertEquals(5, NumJudgeOperatorEnum.EQ.getCode());
            assertEquals("等于", NumJudgeOperatorEnum.EQ.getDesc());
        }

        @Test
        @DisplayName("should have exactly 5 values")
        void valueCount() {
            assertEquals(5, NumJudgeOperatorEnum.values().length);
        }

        @Test
        @DisplayName("getCode should return operatorType")
        void getCodeReturnsOperatorType() {
            for (NumJudgeOperatorEnum e : NumJudgeOperatorEnum.values()) {
                assertNotNull(e.getCode());
                assertNotNull(e.getDesc());
            }
        }
    }

    @Nested
    @DisplayName("ApprovalStandardEnum")
    class ApprovalStandardEnumTest {
        @Test
        @DisplayName("START_USER should have code 1")
        void startUser() {
            assertEquals(1, ApprovalStandardEnum.START_USER.getCode());
            assertEquals("发起人", ApprovalStandardEnum.START_USER.getDesc());
        }

        @Test
        @DisplayName("APPROVAL should have code 2")
        void approval() {
            assertEquals(2, ApprovalStandardEnum.APPROVAL.getCode());
            assertEquals("被审批人", ApprovalStandardEnum.APPROVAL.getDesc());
        }

        @Test
        @DisplayName("FROM_PREV_NODE should have code 3")
        void fromPrevNode() {
            assertEquals(3, ApprovalStandardEnum.FROM_PREV_NODE.getCode());
            assertEquals("上一节点审批人的", ApprovalStandardEnum.FROM_PREV_NODE.getDesc());
        }

        @Test
        @DisplayName("getByCode should return correct enum")
        void getByCode() {
            assertEquals(ApprovalStandardEnum.START_USER, ApprovalStandardEnum.getByCode(1));
            assertEquals(ApprovalStandardEnum.APPROVAL, ApprovalStandardEnum.getByCode(2));
            assertEquals(ApprovalStandardEnum.FROM_PREV_NODE, ApprovalStandardEnum.getByCode(3));
        }

        @Test
        @DisplayName("getByCode should return null for unknown code")
        void getByCodeUnknown() {
            assertNull(ApprovalStandardEnum.getByCode(99));
        }
    }

    @Nested
    @DisplayName("ProcessTypeEnum")
    class ProcessTypeEnumTest {
        @Test
        @DisplayName("VIEW_TYPE should have code 1")
        void viewType() {
            assertEquals(1, ProcessTypeEnum.VIEW_TYPE.getCode());
            assertEquals("查看流程", ProcessTypeEnum.VIEW_TYPE.getDesc());
        }

        @Test
        @DisplayName("LAUNCH_TYPE should have code 3")
        void launchType() {
            assertEquals(3, ProcessTypeEnum.LAUNCH_TYPE.getCode());
            assertEquals("新建流程", ProcessTypeEnum.LAUNCH_TYPE.getDesc());
        }

        @Test
        @DisplayName("ADMIN_TYPE should have code 8")
        void adminType() {
            assertEquals(8, ProcessTypeEnum.ADMIN_TYPE.getCode());
            assertEquals("流程管理", ProcessTypeEnum.ADMIN_TYPE.getDesc());
        }

        @Test
        @DisplayName("getDescByCode should return correct desc")
        void getDescByCode() {
            assertEquals("查看流程", ProcessTypeEnum.getDescByCode(1));
            assertEquals("新建流程", ProcessTypeEnum.getDescByCode(3));
        }

        @Test
        @DisplayName("getDescByCode should return null for unknown code")
        void getDescByCodeUnknown() {
            assertNull(ProcessTypeEnum.getDescByCode(99));
        }
    }
}
