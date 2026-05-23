package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.exception.AFBizException;

import static org.junit.jupiter.api.Assertions.*;

class VersionUtilTest extends BaseTest {

    @Nested
    @DisplayName("checkAppVersion")
    class CheckAppVersionTest {
        @Test
        @DisplayName("should not throw when current version is higher")
        void shouldNotThrowWhenCurrentHigher() {
            assertDoesNotThrow(() -> VersionUtil.checkAppVersion("2.0.0", "1.0.0"));
        }

        @Test
        @DisplayName("should not throw when versions are equal")
        void shouldNotThrowWhenEqual() {
            assertDoesNotThrow(() -> VersionUtil.checkAppVersion("1.0.0", "1.0.0"));
        }

        @Test
        @DisplayName("should throw when current version is lower")
        void shouldThrowWhenCurrentLower() {
            assertThrows(AFBizException.class, () -> VersionUtil.checkAppVersion("1.0.0", "2.0.0"));
        }

        @Test
        @DisplayName("should not throw when nowAppVersion is null")
        void shouldNotThrowWhenNowNull() {
            assertDoesNotThrow(() -> VersionUtil.checkAppVersion(null, "1.0.0"));
        }

        @Test
        @DisplayName("should not throw when lastAppVersion is null")
        void shouldNotThrowWhenLastNull() {
            assertDoesNotThrow(() -> VersionUtil.checkAppVersion("1.0.0", null));
        }

        @Test
        @DisplayName("should not throw when both are null")
        void shouldNotThrowWhenBothNull() {
            assertDoesNotThrow(() -> VersionUtil.checkAppVersion(null, null));
        }

        @Test
        @DisplayName("should not throw when both are empty")
        void shouldNotThrowWhenBothEmpty() {
            assertDoesNotThrow(() -> VersionUtil.checkAppVersion("", ""));
        }

        @Test
        @DisplayName("should compare versions without dots")
        void shouldCompareVersionsWithoutDots() {
            assertDoesNotThrow(() -> VersionUtil.checkAppVersion("200", "100"));
            assertThrows(AFBizException.class, () -> VersionUtil.checkAppVersion("100", "200"));
        }

        @Test
        @DisplayName("should handle single segment versions")
        void shouldHandleSingleSegment() {
            assertDoesNotThrow(() -> VersionUtil.checkAppVersion("2", "1"));
            assertThrows(AFBizException.class, () -> VersionUtil.checkAppVersion("1", "2"));
        }

        @Test
        @DisplayName("should not throw for non-numeric version (silently catches exception)")
        void shouldNotThrowForNonNumeric() {
            assertDoesNotThrow(() -> VersionUtil.checkAppVersion("abc", "1.0.0"));
        }
    }
}
