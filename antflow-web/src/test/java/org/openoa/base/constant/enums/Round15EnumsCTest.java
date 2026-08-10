package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class Round15EnumsCTest extends BaseTest {

    @Nested
    @DisplayName("WildcardCharacterEnum")
    class WildcardCharacterEnumTest {
        @Test
        @DisplayName("ONE_CHARACTER should have code 1 and correct fields")
        void oneCharacter() {
            assertEquals(1, WildcardCharacterEnum.ONE_CHARACTER.getCode());
            assertEquals("{工作流名称}", WildcardCharacterEnum.ONE_CHARACTER.getDesc());
            assertEquals("bpmnName", WildcardCharacterEnum.ONE_CHARACTER.getFilName());
            assertFalse(WildcardCharacterEnum.ONE_CHARACTER.getIsSearchEmpl());
        }

        @Test
        @DisplayName("THREE_CHARACTER should have isSearchEmpl true")
        void threeCharacter() {
            assertTrue(WildcardCharacterEnum.THREE_CHARACTER.getIsSearchEmpl());
            assertEquals("{申请人}", WildcardCharacterEnum.THREE_CHARACTER.getDesc());
            assertEquals("startUser", WildcardCharacterEnum.THREE_CHARACTER.getFilName());
        }

        @Test
        @DisplayName("SEVEN_CHARACTER should have isSearchEmpl true")
        void sevenCharacter() {
            assertTrue(WildcardCharacterEnum.SEVEN_CHARACTER.getIsSearchEmpl());
            assertEquals("nextNodeApproveds", WildcardCharacterEnum.SEVEN_CHARACTER.getFilName());
        }

        @Test
        @DisplayName("FIVE_CHARACTER should have isSearchEmpl false")
        void fiveCharacter() {
            assertFalse(WildcardCharacterEnum.FIVE_CHARACTER.getIsSearchEmpl());
            assertEquals("applyDate", WildcardCharacterEnum.FIVE_CHARACTER.getFilName());
        }

        @Test
        @DisplayName("should have 9 enum values")
        void valueCount() {
            assertEquals(9, WildcardCharacterEnum.values().length);
        }

        @Test
        @DisplayName("all transfDesc should be valid regex patterns")
        void transfDescShouldBeValidRegex() {
            for (WildcardCharacterEnum e : WildcardCharacterEnum.values()) {
                assertDoesNotThrow(() -> Pattern.compile(e.getTransfDesc()),
                        "transfDesc of " + e.name() + " should be a valid regex: " + e.getTransfDesc());
            }
        }

        @Test
        @DisplayName("person-related wildcards should have isSearchEmpl true")
        void personRelatedFlags() {
            assertTrue(WildcardCharacterEnum.THREE_CHARACTER.getIsSearchEmpl());
            assertTrue(WildcardCharacterEnum.FOUR_CHARACTER.getIsSearchEmpl());
            assertTrue(WildcardCharacterEnum.SEVEN_CHARACTER.getIsSearchEmpl());
            assertTrue(WildcardCharacterEnum.EIGHT_CHARACTER.getIsSearchEmpl());
            assertTrue(WildcardCharacterEnum.NINE_CHARACTER.getIsSearchEmpl());
        }

        @Test
        @DisplayName("non-person wildcards should have isSearchEmpl false")
        void nonPersonFlags() {
            assertFalse(WildcardCharacterEnum.ONE_CHARACTER.getIsSearchEmpl());
            assertFalse(WildcardCharacterEnum.TWO_CHARACTER.getIsSearchEmpl());
            assertFalse(WildcardCharacterEnum.FIVE_CHARACTER.getIsSearchEmpl());
            assertFalse(WildcardCharacterEnum.SIX_CHARACTER.getIsSearchEmpl());
        }
    }

    @Nested
    @DisplayName("AFSpecialAssigneeEnum")
    class AFSpecialAssigneeEnumTest {
        @Test
        @DisplayName("TO_BE_REMOVED should have code 0 and id '0'")
        void toBeRemoved() {
            assertEquals(0, AFSpecialAssigneeEnum.TO_BE_REMOVED.getCode());
            assertEquals("0", AFSpecialAssigneeEnum.TO_BE_REMOVED.getId());
            assertEquals("最终会被去除的人员", AFSpecialAssigneeEnum.TO_BE_REMOVED.getDesc());
        }

        @Test
        @DisplayName("CC_NODE should have code -1 and id '-1'")
        void ccNode() {
            assertEquals(-1, AFSpecialAssigneeEnum.CC_NODE.getCode());
            assertEquals("-1", AFSpecialAssigneeEnum.CC_NODE.getId());
            assertEquals("抄送人", AFSpecialAssigneeEnum.CC_NODE.getDesc());
        }

        @Test
        @DisplayName("SKIP should have code -2 and id '-2'")
        void skip() {
            assertEquals(-2, AFSpecialAssigneeEnum.SKIP.getCode());
            assertEquals("-2", AFSpecialAssigneeEnum.SKIP.getId());
            assertEquals("自动节点自动跳过", AFSpecialAssigneeEnum.SKIP.getDesc());
        }

        @Test
        @DisplayName("buildToBeRemoved should return correct BaseIdTranStruVo")
        void buildToBeRemoved() {
            BaseIdTranStruVo vo = AFSpecialAssigneeEnum.buildToBeRemoved();
            assertNotNull(vo);
            assertEquals("0", vo.getId());
            assertEquals("最终会被去除的人员", vo.getName());
        }

        @Test
        @DisplayName("should have 3 enum values")
        void valueCount() {
            assertEquals(3, AFSpecialAssigneeEnum.values().length);
        }
    }

    @Nested
    @DisplayName("LFFieldTypeEnum")
    class LFFieldTypeEnumTest {
        @Test
        @DisplayName("STRING should have type 1")
        void stringType() {
            assertEquals(1, LFFieldTypeEnum.STRING.getType());
            assertEquals("字符串", LFFieldTypeEnum.STRING.getDesc());
        }

        @Test
        @DisplayName("NUMBER should have type 2")
        void numberType() {
            assertEquals(2, LFFieldTypeEnum.NUMBER.getType());
            assertEquals("数字", LFFieldTypeEnum.NUMBER.getDesc());
        }

        @Test
        @DisplayName("DATE should have type 3")
        void dateType() {
            assertEquals(3, LFFieldTypeEnum.DATE.getType());
            assertEquals("日期", LFFieldTypeEnum.DATE.getDesc());
        }

        @Test
        @DisplayName("BOOLEAN should have type 6")
        void booleanType() {
            assertEquals(6, LFFieldTypeEnum.BOOLEAN.getType());
            assertEquals("布尔", LFFieldTypeEnum.BOOLEAN.getDesc());
        }

        @Test
        @DisplayName("BLOB should have type 7")
        void blobType() {
            assertEquals(7, LFFieldTypeEnum.BLOB.getType());
            assertEquals("二进制", LFFieldTypeEnum.BLOB.getDesc());
        }

        @Test
        @DisplayName("getCode should return type field")
        void getCodeReturnsType() {
            for (LFFieldTypeEnum e : LFFieldTypeEnum.values()) {
                assertEquals(e.getType(), e.getCode());
            }
        }

        @Test
        @DisplayName("getByType should return correct enum")
        void getByType() {
            assertEquals(LFFieldTypeEnum.STRING, LFFieldTypeEnum.getByType(1));
            assertEquals(LFFieldTypeEnum.NUMBER, LFFieldTypeEnum.getByType(2));
            assertEquals(LFFieldTypeEnum.BLOB, LFFieldTypeEnum.getByType(7));
        }

        @Test
        @DisplayName("getByType should return null for null input")
        void getByTypeNull() {
            assertNull(LFFieldTypeEnum.getByType(null));
        }

        @Test
        @DisplayName("getByType should return null for unknown type")
        void getByTypeUnknown() {
            assertNull(LFFieldTypeEnum.getByType(99));
        }

        @Test
        @DisplayName("should have 7 enum values")
        void valueCount() {
            assertEquals(7, LFFieldTypeEnum.values().length);
        }
    }
}
