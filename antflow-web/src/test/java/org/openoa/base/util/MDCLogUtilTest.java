package org.openoa.base.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class MDCLogUtilTest extends BaseTest {

    @AfterEach
    void cleanup() {
        MDC.clear();
    }

    @Nested
    @DisplayName("getLogIdBydefault")
    class GetLogIdByDefaultTest {
        @Test
        @DisplayName("should return null when ruid not set")
        void shouldReturnNullWhenNotSet() {
            assertNull(MDCLogUtil.getLogIdBydefault());
        }

        @Test
        @DisplayName("should return ruid when set")
        void shouldReturnRuidWhenSet() {
            MDC.put("ruid", "test-ruid-123");
            assertEquals("test-ruid-123", MDCLogUtil.getLogIdBydefault());
        }
    }

    @Nested
    @DisplayName("getLogId")
    class GetLogIdTest {
        @Test
        @DisplayName("should generate new logId when ruid not set")
        void shouldGenerateNewWhenNotSet() {
            String logId = MDCLogUtil.getLogId();
            assertNotNull(logId);
            assertFalse(logId.isEmpty());
        }

        @Test
        @DisplayName("should return existing ruid when set")
        void shouldReturnExistingWhenSet() {
            MDC.put("ruid", "existing-ruid");
            assertEquals("existing-ruid", MDCLogUtil.getLogId());
        }

        @Test
        @DisplayName("should store generated logId in MDC")
        void shouldStoreInMDC() {
            String logId = MDCLogUtil.getLogId();
            assertEquals(logId, MDC.get("ruid"));
        }
    }

    @Nested
    @DisplayName("cleanLogId")
    class CleanLogIdTest {
        @Test
        @DisplayName("should remove ruid from MDC")
        void shouldRemoveRuid() {
            MDC.put("ruid", "to-be-cleaned");
            MDCLogUtil.cleanLogId();
            assertNull(MDC.get("ruid"));
        }

        @Test
        @DisplayName("should not throw when ruid not set")
        void shouldNotThrowWhenNotSet() {
            assertDoesNotThrow(MDCLogUtil::cleanLogId);
        }
    }

    @Nested
    @DisplayName("resetLogId")
    class ResetLogIdTest {
        @Test
        @DisplayName("should replace existing ruid with new one")
        void shouldReplaceWithNew() {
            MDC.put("ruid", "old-ruid");
            String newId = MDCLogUtil.getLogId();
            assertEquals("old-ruid", newId);

            MDCLogUtil.resetLogId();
            String resetId = MDCLogUtil.getLogIdBydefault();
            assertNotNull(resetId);
            assertNotEquals("old-ruid", resetId);
        }
    }
}
