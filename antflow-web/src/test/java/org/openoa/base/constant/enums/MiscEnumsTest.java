package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.BaseIdTranStruVo;

import static org.junit.jupiter.api.Assertions.*;

class MiscEnumsTest extends BaseTest {

    @Nested
    @DisplayName("ProcessStateEnum")
    class ProcessStateEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("审批中", ProcessStateEnum.getDescByCode(1));
            assertEquals("审批通过", ProcessStateEnum.getDescByCode(2));
            assertEquals("作废", ProcessStateEnum.getDescByCode(3));
            assertEquals("审批拒绝", ProcessStateEnum.getDescByCode(6));
        }

        @Test
        @DisplayName("should return code by desc")
        void shouldReturnCodeByDesc() {
            assertEquals(1, ProcessStateEnum.getCodeByDesc("审批中"));
            assertEquals(2, ProcessStateEnum.getCodeByDesc("审批通过"));
        }

        @Test
        @DisplayName("should return null for non-existent")
        void shouldReturnNullForNonExistent() {
            assertNull(ProcessStateEnum.getDescByCode(99));
            assertNull(ProcessStateEnum.getCodeByDesc("不存在"));
        }
    }

    @Nested
    @DisplayName("ProcessDisagreeTypeEnum")
    class ProcessDisagreeTypeEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("退回上一个节点提交下一个节点", ProcessDisagreeTypeEnum.getDescByCode(1));
            assertEquals("退回发起人提交下一个节点", ProcessDisagreeTypeEnum.getDescByCode(2));
        }

        @Test
        @DisplayName("should return enum by code")
        void shouldReturnEnumByCode() {
            assertEquals(ProcessDisagreeTypeEnum.ONE_DISAGREE, ProcessDisagreeTypeEnum.getByCode(1));
            assertEquals(ProcessDisagreeTypeEnum.FIVE_DISAGREE, ProcessDisagreeTypeEnum.getByCode(5));
        }

        @Test
        @DisplayName("should return null for null code")
        void shouldReturnNullForNull() {
            assertNull(ProcessDisagreeTypeEnum.getByCode(null));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(ProcessDisagreeTypeEnum.getByCode(99));
            assertNull(ProcessDisagreeTypeEnum.getDescByCode(99));
        }
    }

    @Nested
    @DisplayName("AFSpecialAssigneeEnum")
    class AFSpecialAssigneeEnumTest {
        @Test
        @DisplayName("buildToBeRemoved should return correct VO")
        void buildToBeRemoved() {
            BaseIdTranStruVo vo = AFSpecialAssigneeEnum.buildToBeRemoved();
            assertNotNull(vo);
            assertEquals("0", vo.getId());
            assertEquals("最终会被去除的人员", vo.getName());
        }

        @Test
        @DisplayName("TO_BE_REMOVED should have code 0")
        void toBeRemovedCode() {
            assertEquals(0, AFSpecialAssigneeEnum.TO_BE_REMOVED.getCode());
        }

        @Test
        @DisplayName("CC_NODE should have code -1")
        void ccNodeCode() {
            assertEquals(-1, AFSpecialAssigneeEnum.CC_NODE.getCode());
            assertEquals("抄送人", AFSpecialAssigneeEnum.CC_NODE.getDesc());
        }

        @Test
        @DisplayName("SKIP should have code -2")
        void skipCode() {
            assertEquals(-2, AFSpecialAssigneeEnum.SKIP.getCode());
            assertEquals("自动节点自动跳过", AFSpecialAssigneeEnum.SKIP.getDesc());
        }
    }
}
