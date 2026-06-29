package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class DeduplicationTypeEnumTest extends BaseTest {

    @Nested
    @DisplayName("getDescByCode")
    class GetDescByCodeTest {

        @Test
        @DisplayName("code 1 returns '不去重'")
        void code1() {
            assertEquals("不去重", DeduplicationTypeEnum.getDescByCode(1));
        }

        @Test
        @DisplayName("code 2 returns forward deduplication description")
        void code2() {
            assertEquals("当一个审批人重复出现时，只在最后一次审批（前去重）", DeduplicationTypeEnum.getDescByCode(2));
        }

        @Test
        @DisplayName("code 3 returns backward deduplication description")
        void code3() {
            assertEquals("当一个审批人重复出现时，只在第一次审批（后去重）", DeduplicationTypeEnum.getDescByCode(3));
        }

        @Test
        @DisplayName("code 4 returns skip next deduplication description")
        void code4() {
            assertEquals("当一个审批人仅在相邻节点重复出现时,后续自动同意", DeduplicationTypeEnum.getDescByCode(4));
        }

        @Test
        @DisplayName("null returns null")
        void nullReturnsNull() {
            assertNull(DeduplicationTypeEnum.getDescByCode(null));
        }

        @Test
        @DisplayName("999 returns null")
        void unknownCodeReturnsNull() {
            assertNull(DeduplicationTypeEnum.getDescByCode(999));
        }
    }
}
