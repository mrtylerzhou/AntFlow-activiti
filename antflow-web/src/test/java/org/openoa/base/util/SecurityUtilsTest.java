package org.openoa.base.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BaseIdTranStruVo;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTest extends BaseTest {

    @AfterEach
    void cleanup() {
        ThreadLocalContainer.clean();
    }

    @Nested
    @DisplayName("getLogInEmpInfo")
    class GetLogInEmpInfoTest {
        @Test
        @DisplayName("should return null when not logged in")
        void shouldReturnNullWhenNotLoggedIn() {
            assertNull(SecurityUtils.getLogInEmpInfo());
        }

        @Test
        @DisplayName("should return user info when logged in")
        void shouldReturnUserWhenLoggedIn() {
            BaseIdTranStruVo user = BaseIdTranStruVo.builder().id("123").name("testUser").build();
            ThreadLocalContainer.set("currentuser", user);
            BaseIdTranStruVo result = SecurityUtils.getLogInEmpInfo();
            assertNotNull(result);
            assertEquals("123", result.getId());
            assertEquals("testUser", result.getName());
        }
    }

    @Nested
    @DisplayName("getLogInEmpId")
    class GetLogInEmpIdTest {
        @Test
        @DisplayName("should throw when not logged in")
        void shouldThrowWhenNotLoggedIn() {
            assertThrows(AFBizException.class, SecurityUtils::getLogInEmpId);
        }

        @Test
        @DisplayName("should return emp id when logged in")
        void shouldReturnIdWhenLoggedIn() {
            BaseIdTranStruVo user = BaseIdTranStruVo.builder().id("emp001").name("Alice").build();
            ThreadLocalContainer.set("currentuser", user);
            assertEquals("emp001", SecurityUtils.getLogInEmpId());
        }
    }

    @Nested
    @DisplayName("getLogInEmpName")
    class GetLogInEmpNameTest {
        @Test
        @DisplayName("should throw when not logged in")
        void shouldThrowWhenNotLoggedIn() {
            assertThrows(AFBizException.class, SecurityUtils::getLogInEmpName);
        }

        @Test
        @DisplayName("should return emp name when logged in")
        void shouldReturnNameWhenLoggedIn() {
            BaseIdTranStruVo user = BaseIdTranStruVo.builder().id("1").name("Bob").build();
            ThreadLocalContainer.set("currentuser", user);
            assertEquals("Bob", SecurityUtils.getLogInEmpName());
        }
    }

    @Nested
    @DisplayName("getLogInEmpNameSafe")
    class GetLogInEmpNameSafeTest {
        @Test
        @DisplayName("should return empty string when not logged in")
        void shouldReturnEmptyWhenNotLoggedIn() {
            assertEquals("", SecurityUtils.getLogInEmpNameSafe());
        }

        @Test
        @DisplayName("should return name when logged in")
        void shouldReturnNameWhenLoggedIn() {
            BaseIdTranStruVo user = BaseIdTranStruVo.builder().id("1").name("Charlie").build();
            ThreadLocalContainer.set("currentuser", user);
            assertEquals("Charlie", SecurityUtils.getLogInEmpNameSafe());
        }
    }

    @Nested
    @DisplayName("getLogInEmpIdSafe")
    class GetLogInEmpIdSafeTest {
        @Test
        @DisplayName("should return -999 when not logged in")
        void shouldReturnDefaultWhenNotLoggedIn() {
            assertEquals("-999", SecurityUtils.getLogInEmpIdSafe());
        }

        @Test
        @DisplayName("should return id when logged in")
        void shouldReturnIdWhenLoggedIn() {
            BaseIdTranStruVo user = BaseIdTranStruVo.builder().id("42").name("Dave").build();
            ThreadLocalContainer.set("currentuser", user);
            assertEquals("42", SecurityUtils.getLogInEmpIdSafe());
        }
    }
}
