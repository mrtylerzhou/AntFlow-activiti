package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.entity.DetailedUser;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DetailedUserUtilTest extends BaseTest {

    @Nested
    @DisplayName("basicEmployeeInfo")
    class BasicEmployeeInfoTest {
        @Test
        @DisplayName("should map id and name to username")
        void shouldMapIdAndName() {
            BaseIdTranStruVo vo = BaseIdTranStruVo.builder().id("emp001").name("Alice").build();
            DetailedUser user = DetailedUserUtil.basicEmployeeInfo(vo);
            assertEquals("emp001", user.getId());
            assertEquals("Alice", user.getUsername());
        }

        @Test
        @DisplayName("should handle null name")
        void shouldHandleNullName() {
            BaseIdTranStruVo vo = BaseIdTranStruVo.builder().id("emp002").build();
            DetailedUser user = DetailedUserUtil.basicEmployeeInfo(vo);
            assertEquals("emp002", user.getId());
            assertNull(user.getUsername());
        }

        @Test
        @DisplayName("should not set other fields")
        void shouldNotSetOtherFields() {
            BaseIdTranStruVo vo = BaseIdTranStruVo.builder().id("1").name("Bob").build();
            DetailedUser user = DetailedUserUtil.basicEmployeeInfo(vo);
            assertNull(user.getEmail());
            assertNull(user.getMobile());
            assertNull(user.getLeaderId());
        }
    }

    @Nested
    @DisplayName("basicEmployeeInfos")
    class BasicEmployeeInfosTest {
        @Test
        @DisplayName("should convert list of BaseIdTranStruVo to DetailedUser list")
        void shouldConvertList() {
            List<BaseIdTranStruVo> vos = Arrays.asList(
                    BaseIdTranStruVo.builder().id("1").name("Alice").build(),
                    BaseIdTranStruVo.builder().id("2").name("Bob").build()
            );
            List<DetailedUser> users = DetailedUserUtil.basicEmployeeInfos(vos);
            assertEquals(2, users.size());
            assertEquals("1", users.get(0).getId());
            assertEquals("Alice", users.get(0).getUsername());
            assertEquals("2", users.get(1).getId());
            assertEquals("Bob", users.get(1).getUsername());
        }

        @Test
        @DisplayName("should return empty list for null input")
        void shouldReturnEmptyForNull() {
            List<DetailedUser> users = DetailedUserUtil.basicEmployeeInfos(null);
            assertTrue(users.isEmpty());
        }

        @Test
        @DisplayName("should return empty list for empty input")
        void shouldReturnEmptyForEmpty() {
            List<DetailedUser> users = DetailedUserUtil.basicEmployeeInfos(Collections.emptyList());
            assertTrue(users.isEmpty());
        }
    }
}
