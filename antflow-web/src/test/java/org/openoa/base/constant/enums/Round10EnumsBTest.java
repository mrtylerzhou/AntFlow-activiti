package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class Round10EnumsBTest extends BaseTest {

    @Nested
    @DisplayName("DuplicationProcessStrategyEnum")
    class DuplicationProcessStrategyEnumTest {
        @Test
        @DisplayName("REMOVE should have code 1")
        void removeCode() {
            assertEquals(1, DuplicationProcessStrategyEnum.REMOVE.getCode());
            assertEquals("去除", DuplicationProcessStrategyEnum.REMOVE.getDesc());
        }

        @Test
        @DisplayName("SKIP should have code 2")
        void skipCode() {
            assertEquals(2, DuplicationProcessStrategyEnum.SKIP.getCode());
            assertEquals("跳过", DuplicationProcessStrategyEnum.SKIP.getDesc());
        }
    }

    @Nested
    @DisplayName("HrbpTypeEnum")
    class HrbpTypeEnumTest {
        @Test
        @DisplayName("HRBP should have code 0")
        void hrbpCode() {
            assertEquals(0, HrbpTypeEnum.HRBP.getCode());
            assertEquals("hrbp", HrbpTypeEnum.HRBP.getDesc());
        }

        @Test
        @DisplayName("HRBP_LEADER should have code 2 (non-continuous)")
        void hrbpLeaderCode() {
            assertEquals(2, HrbpTypeEnum.HRBP_LEADER.getCode());
        }
    }

    @Nested
    @DisplayName("ErrLevelEnum")
    class ErrLevelEnumTest {
        @Test
        @DisplayName("ERR should have code 1")
        void errCode() {
            assertEquals(1, ErrLevelEnum.ERR_LEVEL_ERR.getCode());
            assertEquals("错误", ErrLevelEnum.ERR_LEVEL_ERR.getDesc());
        }

        @Test
        @DisplayName("WARNING should have code 2")
        void warningCode() {
            assertEquals(2, ErrLevelEnum.ERR_LEVEL_WORNING.getCode());
            assertEquals("提醒", ErrLevelEnum.ERR_LEVEL_WORNING.getDesc());
        }
    }

    @Nested
    @DisplayName("OpLogFlagEnum")
    class OpLogFlagEnumTest {
        @Test
        @DisplayName("SUCCESS should have code 0")
        void successCode() {
            assertEquals(0, OpLogFlagEnum.SUCCESS.getCode());
            assertEquals("成功", OpLogFlagEnum.SUCCESS.getDesc());
        }

        @Test
        @DisplayName("FAILURE should have code 1")
        void failureCode() {
            assertEquals(1, OpLogFlagEnum.FAILURE.getCode());
        }

        @Test
        @DisplayName("BusinessException should have code 2")
        void bizExceptionCode() {
            assertEquals(2, OpLogFlagEnum.BusinessException.getCode());
        }
    }

    @Nested
    @DisplayName("FieldPermEnum")
    class FieldPermEnumTest {
        @Test
        @DisplayName("should have R, E, H values")
        void shouldHaveValues() {
            assertEquals(3, FieldPermEnum.values().length);
            assertNotNull(FieldPermEnum.R);
            assertNotNull(FieldPermEnum.E);
            assertNotNull(FieldPermEnum.H);
        }
    }

    @Nested
    @DisplayName("APPTypeEnum")
    class APPTypeEnumTest {
        @Test
        @DisplayName("ANDROID should have code 1")
        void androidCode() {
            assertEquals(1, APPTypeEnum.ANDROID.getCode());
            assertEquals("android", APPTypeEnum.ANDROID.getName());
            assertEquals("安卓", APPTypeEnum.ANDROID.getDesc());
        }

        @Test
        @DisplayName("IOS should have code 2")
        void iosCode() {
            assertEquals(2, APPTypeEnum.IOS.getCode());
            assertEquals("苹果", APPTypeEnum.IOS.getDesc());
        }

        @Test
        @DisplayName("HARMONY_OS should have code 3")
        void harmonyCode() {
            assertEquals(3, APPTypeEnum.HARMONY_OS.getCode());
            assertEquals("鸿蒙", APPTypeEnum.HARMONY_OS.getDesc());
        }
    }

    @Nested
    @DisplayName("ApprovalStandardEnum")
    class ApprovalStandardEnumTest {
        @Test
        @DisplayName("should return enum by code")
        void shouldReturnByCode() {
            assertEquals(ApprovalStandardEnum.START_USER, ApprovalStandardEnum.getByCode(1));
            assertEquals(ApprovalStandardEnum.APPROVAL, ApprovalStandardEnum.getByCode(2));
            assertEquals(ApprovalStandardEnum.FROM_PREV_NODE, ApprovalStandardEnum.getByCode(3));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(ApprovalStandardEnum.getByCode(99));
        }
    }

    @Nested
    @DisplayName("ButtonTypeEnum")
    class ButtonTypeEnumTest {
        @Test
        @DisplayName("should return desc by code")
        void shouldReturnDescByCode() {
            assertEquals("预览", ButtonTypeEnum.getDescByCode(0));
            assertEquals("提交", ButtonTypeEnum.getDescByCode(1));
            assertEquals("同意", ButtonTypeEnum.getDescByCode(3));
            assertEquals("终止", ButtonTypeEnum.getDescByCode(12));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(ButtonTypeEnum.getDescByCode(99));
        }
    }

    @Nested
    @DisplayName("AFSpecialDictCategoryEnum")
    class AFSpecialDictCategoryEnumTest {
        @Test
        @DisplayName("LOWCODEFLOW and USER_DEFINED_RULE share code 0")
        void shareCode0() {
            assertEquals(AFSpecialDictCategoryEnum.LOWCODEFLOW.getCode(),
                    AFSpecialDictCategoryEnum.USER_DEFINED_RULE_FOR_ASSIGNEE.getCode());
            assertEquals(0, AFSpecialDictCategoryEnum.LOWCODEFLOW.getCode());
        }

        @Test
        @DisplayName("should have different desc")
        void differentDesc() {
            assertEquals("lowcodeflow", AFSpecialDictCategoryEnum.LOWCODEFLOW.getDesc());
            assertEquals("udr", AFSpecialDictCategoryEnum.USER_DEFINED_RULE_FOR_ASSIGNEE.getDesc());
        }
    }

    @Nested
    @DisplayName("FieldValueTypeEnum")
    class FieldValueTypeEnumTest {
        @Test
        @DisplayName("PERSONCHOICE and ROLECHOICE share code 7")
        void shareCode7() {
            assertEquals(FieldValueTypeEnum.PERSONCHOICE.getCode(),
                    FieldValueTypeEnum.ROLECHOICE.getCode());
            assertEquals(7, FieldValueTypeEnum.PERSONCHOICE.getCode());
        }

        @Test
        @DisplayName("STRING should have code 1")
        void stringCode() {
            assertEquals(1, FieldValueTypeEnum.STRING.getCode());
            assertEquals("String", FieldValueTypeEnum.STRING.getDesc());
        }

        @Test
        @DisplayName("NUMBER should have code 2")
        void numberCode() {
            assertEquals(2, FieldValueTypeEnum.NUMBER.getCode());
        }
    }

    @Nested
    @DisplayName("LFControlTypeEnum")
    class LFControlTypeEnumTest {
        @Test
        @DisplayName("SELECT should have code 1, name select, desc 下拉框")
        void selectProperties() {
            assertEquals(1, LFControlTypeEnum.SELECT.getCode());
            assertEquals("select", LFControlTypeEnum.SELECT.getName());
            assertEquals("下拉框", LFControlTypeEnum.SELECT.getDesc());
        }
    }
}
