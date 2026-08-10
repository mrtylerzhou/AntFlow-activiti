package org.openoa.engine.conf.engineconfig;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.util.ThreadLocalContainer;

import static org.junit.jupiter.api.Assertions.*;

class MultiTenantIdHolderTest extends BaseTest {

    private final MultiTenantIdHolder holder = new MultiTenantIdHolder();

    @AfterEach
    void cleanup() {
        holder.clearCurrentTenantId();
    }

    @Nested
    @DisplayName("setCurrentTenantId / getCurrentTenantId")
    class SetAndGetTest {
        @Test
        @DisplayName("should return empty string when no tenant ID is set")
        void noTenantId() {
            assertEquals("", holder.getCurrentTenantId());
        }

        @Test
        @DisplayName("should return tenant ID when set")
        void setTenantId() {
            holder.setCurrentTenantId("tenant123");
            assertEquals("tenant123", holder.getCurrentTenantId());
        }

        @Test
        @DisplayName("should return empty string for DEFAULT_TENANT (case insensitive)")
        void defaultTenant() {
            holder.setCurrentTenantId("default");
            assertEquals("", holder.getCurrentTenantId());
        }

        @Test
        @DisplayName("should return empty string for DEFAULT_TENANT in uppercase")
        void defaultTenantUppercase() {
            holder.setCurrentTenantId("DEFAULT");
            assertEquals("", holder.getCurrentTenantId());
        }

        @Test
        @DisplayName("should return empty string for Default (mixed case)")
        void defaultTenantMixedCase() {
            holder.setCurrentTenantId("Default");
            assertEquals("", holder.getCurrentTenantId());
        }

        @Test
        @DisplayName("should return actual ID for non-default tenant")
        void nonDefaultTenant() {
            holder.setCurrentTenantId("acme-corp");
            assertEquals("acme-corp", holder.getCurrentTenantId());
        }
    }

    @Nested
    @DisplayName("clearCurrentTenantId")
    class ClearTest {
        @Test
        @DisplayName("should clear tenant ID")
        void clear() {
            holder.setCurrentTenantId("tenant456");
            holder.clearCurrentTenantId();
            assertEquals("", holder.getCurrentTenantId());
        }

        @Test
        @DisplayName("should be safe to call clear when no tenant is set")
        void clearWhenEmpty() {
            holder.clearCurrentTenantId();
            assertEquals("", holder.getCurrentTenantId());
        }
    }

    @Nested
    @DisplayName("ThreadLocal isolation")
    class ThreadIsolationTest {
        @Test
        @DisplayName("tenant ID should be isolated between threads")
        void threadIsolation() throws Exception {
            holder.setCurrentTenantId("main-tenant");
            Thread t = new Thread(() -> {
                assertEquals("", holder.getCurrentTenantId());
                holder.setCurrentTenantId("other-tenant");
                assertEquals("other-tenant", holder.getCurrentTenantId());
                holder.clearCurrentTenantId();
            });
            t.start();
            t.join();
            assertEquals("main-tenant", holder.getCurrentTenantId());
        }
    }
}
