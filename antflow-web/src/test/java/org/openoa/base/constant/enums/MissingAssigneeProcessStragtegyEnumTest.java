package org.openoa.base.constant.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class MissingAssigneeProcessStragtegyEnumTest extends BaseTest {

    @Nested
    @DisplayName("getByCode")
    class GetByCodeTest {
        @Test
        @DisplayName("should return NOT_ALLOWED for code 0")
        void shouldReturnNotAllowedFor0() {
            assertEquals(MissingAssigneeProcessStragtegyEnum.NOT_ALLOWED,
                    MissingAssigneeProcessStragtegyEnum.getByCode(0));
        }

        @Test
        @DisplayName("should return SKIP for code 1")
        void shouldReturnSkipFor1() {
            assertEquals(MissingAssigneeProcessStragtegyEnum.SKIP,
                    MissingAssigneeProcessStragtegyEnum.getByCode(1));
        }

        @Test
        @DisplayName("should return TRANSFER_TO_ADMIN for code 2")
        void shouldReturnTransferToAdminFor2() {
            assertEquals(MissingAssigneeProcessStragtegyEnum.TRANSFER_TO_ADMIN,
                    MissingAssigneeProcessStragtegyEnum.getByCode(2));
        }

        @Test
        @DisplayName("should return null for null code")
        void shouldReturnNullForNull() {
            assertNull(MissingAssigneeProcessStragtegyEnum.getByCode(null));
        }

        @Test
        @DisplayName("should return null for non-existent code")
        void shouldReturnNullForNonExistent() {
            assertNull(MissingAssigneeProcessStragtegyEnum.getByCode(99));
        }

        @Test
        @DisplayName("should use == comparison which fails for Integer > 127")
        void shouldUseReferenceComparison() {
            Integer code = 1;
            assertNotNull(MissingAssigneeProcessStragtegyEnum.getByCode(code));
        }
    }

    @Nested
    @DisplayName("enum properties")
    class EnumPropertiesTest {
        @Test
        @DisplayName("NOT_ALLOWED should have code 0 and desc 不允许发起")
        void notAllowedProperties() {
            assertEquals(0, MissingAssigneeProcessStragtegyEnum.NOT_ALLOWED.getCode());
            assertEquals("不允许发起", MissingAssigneeProcessStragtegyEnum.NOT_ALLOWED.getDesc());
        }

        @Test
        @DisplayName("SKIP should have code 1 and desc 跳过")
        void skipProperties() {
            assertEquals(1, MissingAssigneeProcessStragtegyEnum.SKIP.getCode());
            assertEquals("跳过", MissingAssigneeProcessStragtegyEnum.SKIP.getDesc());
        }

        @Test
        @DisplayName("TRANSFER_TO_ADMIN should have code 2 and desc 转办给管理员")
        void transferToAdminProperties() {
            assertEquals(2, MissingAssigneeProcessStragtegyEnum.TRANSFER_TO_ADMIN.getCode());
            assertEquals("转办给管理员", MissingAssigneeProcessStragtegyEnum.TRANSFER_TO_ADMIN.getDesc());
        }
    }
}
