package org.openoa.base.util;

import org.activiti.engine.impl.cfg.multitenant.TenantIdHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.MockBaseTest;
import org.openoa.base.interf.TenantField;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AFWrappersTest extends MockBaseTest {

    private void setupPropertyUtil(String strictMode, String tenantId) {
        try {
            Field appCtxField = SpringBeanUtils.class.getDeclaredField("applicationContext");
            appCtxField.setAccessible(true);
            ApplicationContext ctx = mock(ApplicationContext.class);
            Environment env = mock(Environment.class);
            when(ctx.getBean(Environment.class)).thenReturn(env);
            when(env.getProperty("antflow.multitenant.strict")).thenReturn(strictMode);
            when(ctx.getBean(TenantIdHolder.class)).thenReturn(new TenantIdHolder() {
                @Override
                public void setCurrentTenantId(String id) {}
                @Override
                public String getCurrentTenantId() { return tenantId; }
                @Override
                public void clearCurrentTenantId() {}
            });
            appCtxField.set(null, ctx);
        } catch (Exception e) {
            fail("Failed to set up mock: " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("tenantQuery")
    class TenantQueryTest {
        @Test
        @DisplayName("should always include is_del=0 filter")
        void isDelFilter() {
            setupPropertyUtil("false", null);
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<?> wrapper = AFWrappers.tenantQuery();
            assertNotNull(wrapper);
            String sql = wrapper.getCustomSqlSegment();
            assertNotNull(sql);
            assertTrue(sql.contains("is_del"));
        }

        @Test
        @DisplayName("should include tenant_id when strict mode and tenant ID present")
        void strictModeWithTenant() {
            setupPropertyUtil("true", "tenant123");
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<?> wrapper = AFWrappers.tenantQuery();
            String sql = wrapper.getCustomSqlSegment();
            assertTrue(sql.contains("tenant_id"));
        }

        @Test
        @DisplayName("should not include tenant_id when not strict mode")
        void nonStrictMode() {
            setupPropertyUtil("false", "tenant123");
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<?> wrapper = AFWrappers.tenantQuery();
            String sql = wrapper.getCustomSqlSegment();
            assertFalse(sql.contains("tenant_id"));
        }

        @Test
        @DisplayName("should not include tenant_id when strict mode but tenant ID is empty")
        void strictModeEmptyTenant() {
            setupPropertyUtil("true", "");
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<?> wrapper = AFWrappers.tenantQuery();
            String sql = wrapper.getCustomSqlSegment();
            assertFalse(sql.contains("tenant_id"));
        }
    }

    @Nested
    @DisplayName("lambdaTenantQuery")
    class LambdaTenantQueryTest {
        @Test
        @DisplayName("should return LambdaQueryWrapper")
        void returnsLambdaWrapper() {
            setupPropertyUtil("false", null);
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<?> wrapper = AFWrappers.lambdaTenantQuery();
            assertNotNull(wrapper);
        }
    }
}
