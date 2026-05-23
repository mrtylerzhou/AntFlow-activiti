package org.openoa.engine.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.openoa.MockBaseTest;
import org.openoa.base.util.MultiTenantUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueryWrapperUtilTest extends MockBaseTest {

    @Nested
    @DisplayName("addTenantCondition")
    class AddTenantConditionTest {

        @Test
        @DisplayName("should add tenant condition when strict mode is on and tenantId is not empty")
        @SuppressWarnings("unchecked")
        void shouldAddTenantConditionWhenStrictModeOnAndTenantIdNotEmpty() {
            try (MockedStatic<MultiTenantUtil> mockedMultiTenant = mockStatic(MultiTenantUtil.class)) {
                mockedMultiTenant.when(MultiTenantUtil::strictTenantMode).thenReturn(true);
                mockedMultiTenant.when(MultiTenantUtil::getCurrentTenantId).thenReturn("tenant-001");

                LambdaQueryWrapper<Object> wrapper = mock(LambdaQueryWrapper.class);
                SFunction<Object, ?> column = mock(SFunction.class);
                when(wrapper.eq(column, "tenant-001")).thenReturn(wrapper);

                LambdaQueryWrapper<Object> result = QueryWrapperUtil.addTenantCondition(wrapper, column);

                assertSame(wrapper, result);
                verify(wrapper).eq(column, "tenant-001");
            }
        }

        @Test
        @DisplayName("should not add tenant condition when strict mode is off")
        @SuppressWarnings("unchecked")
        void shouldNotAddTenantConditionWhenStrictModeOff() {
            try (MockedStatic<MultiTenantUtil> mockedMultiTenant = mockStatic(MultiTenantUtil.class)) {
                mockedMultiTenant.when(MultiTenantUtil::strictTenantMode).thenReturn(false);
                mockedMultiTenant.when(MultiTenantUtil::getCurrentTenantId).thenReturn("tenant-001");

                LambdaQueryWrapper<Object> wrapper = mock(LambdaQueryWrapper.class);
                SFunction<Object, ?> column = mock(SFunction.class);

                LambdaQueryWrapper<Object> result = QueryWrapperUtil.addTenantCondition(wrapper, column);

                assertSame(wrapper, result);
                verify(wrapper, never()).eq(any(), any());
            }
        }

        @Test
        @DisplayName("should not add tenant condition when tenantId is empty")
        @SuppressWarnings("unchecked")
        void shouldNotAddTenantConditionWhenTenantIdEmpty() {
            try (MockedStatic<MultiTenantUtil> mockedMultiTenant = mockStatic(MultiTenantUtil.class)) {
                mockedMultiTenant.when(MultiTenantUtil::strictTenantMode).thenReturn(true);
                mockedMultiTenant.when(MultiTenantUtil::getCurrentTenantId).thenReturn("");

                LambdaQueryWrapper<Object> wrapper = mock(LambdaQueryWrapper.class);
                SFunction<Object, ?> column = mock(SFunction.class);

                LambdaQueryWrapper<Object> result = QueryWrapperUtil.addTenantCondition(wrapper, column);

                assertSame(wrapper, result);
                verify(wrapper, never()).eq(any(), any());
            }
        }

        @Test
        @DisplayName("should not add tenant condition when tenantId is null")
        @SuppressWarnings("unchecked")
        void shouldNotAddTenantConditionWhenTenantIdNull() {
            try (MockedStatic<MultiTenantUtil> mockedMultiTenant = mockStatic(MultiTenantUtil.class)) {
                mockedMultiTenant.when(MultiTenantUtil::strictTenantMode).thenReturn(true);
                mockedMultiTenant.when(MultiTenantUtil::getCurrentTenantId).thenReturn(null);

                LambdaQueryWrapper<Object> wrapper = mock(LambdaQueryWrapper.class);
                SFunction<Object, ?> column = mock(SFunction.class);

                LambdaQueryWrapper<Object> result = QueryWrapperUtil.addTenantCondition(wrapper, column);

                assertSame(wrapper, result);
                verify(wrapper, never()).eq(any(), any());
            }
        }

        @Test
        @DisplayName("should return the same wrapper instance")
        @SuppressWarnings("unchecked")
        void shouldReturnSameWrapperInstance() {
            try (MockedStatic<MultiTenantUtil> mockedMultiTenant = mockStatic(MultiTenantUtil.class)) {
                mockedMultiTenant.when(MultiTenantUtil::strictTenantMode).thenReturn(false);
                mockedMultiTenant.when(MultiTenantUtil::getCurrentTenantId).thenReturn(null);

                LambdaQueryWrapper<Object> wrapper = mock(LambdaQueryWrapper.class);
                SFunction<Object, ?> column = mock(SFunction.class);

                LambdaQueryWrapper<Object> result = QueryWrapperUtil.addTenantCondition(wrapper, column);

                assertSame(wrapper, result);
            }
        }
    }

    @Nested
    @DisplayName("buildWithTenant")
    class BuildWithTenantTest {

        @Test
        @DisplayName("should build wrapper without tenant condition when strict mode is off")
        void shouldBuildWrapperWithoutTenantConditionWhenStrictModeOff() {
            try (MockedStatic<MultiTenantUtil> mockedMultiTenant = mockStatic(MultiTenantUtil.class)) {
                mockedMultiTenant.when(MultiTenantUtil::strictTenantMode).thenReturn(false);
                mockedMultiTenant.when(MultiTenantUtil::getCurrentTenantId).thenReturn("tenant-001");

                LambdaQueryWrapper<Object> result = QueryWrapperUtil.buildWithTenant(Object::toString);

                assertNotNull(result);
            }
        }

        @Test
        @DisplayName("should build wrapper without tenant condition when tenantId is empty")
        void shouldBuildWrapperWithoutTenantConditionWhenTenantIdEmpty() {
            try (MockedStatic<MultiTenantUtil> mockedMultiTenant = mockStatic(MultiTenantUtil.class)) {
                mockedMultiTenant.when(MultiTenantUtil::strictTenantMode).thenReturn(true);
                mockedMultiTenant.when(MultiTenantUtil::getCurrentTenantId).thenReturn("");

                LambdaQueryWrapper<Object> result = QueryWrapperUtil.buildWithTenant(Object::toString);

                assertNotNull(result);
            }
        }

        @Test
        @DisplayName("should return a new wrapper instance each call")
        void shouldReturnNewWrapperInstance() {
            try (MockedStatic<MultiTenantUtil> mockedMultiTenant = mockStatic(MultiTenantUtil.class)) {
                mockedMultiTenant.when(MultiTenantUtil::strictTenantMode).thenReturn(false);
                mockedMultiTenant.when(MultiTenantUtil::getCurrentTenantId).thenReturn(null);

                LambdaQueryWrapper<Object> result1 = QueryWrapperUtil.buildWithTenant(Object::toString);
                LambdaQueryWrapper<Object> result2 = QueryWrapperUtil.buildWithTenant(Object::toString);

                assertNotSame(result1, result2);
            }
        }
    }
}
