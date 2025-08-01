package org.openoa.engine.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.interf.TenantField;

public class QueryWrapperUtil {
    public static <T> LambdaQueryWrapper<T> addTenantCondition(LambdaQueryWrapper<T> wrapper, SFunction<T, ?> tenantColumn) {
        String tenantId = MultiTenantUtil.getCurrentTenantId();
        if (MultiTenantUtil.strictTenantMode()&&!StringUtils.isEmpty(tenantId)) {
            wrapper.eq(tenantColumn, tenantId);
        }
        return wrapper;
    }
    public static <T extends TenantField> LambdaQueryWrapper<T> addTenantCondition(LambdaQueryWrapper<T> wrapper,T entity) {
        String tenantId = MultiTenantUtil.getCurrentTenantId();
        if (MultiTenantUtil.strictTenantMode()&&!StringUtils.isEmpty(tenantId)) {
            wrapper.eq(TenantField::getTenantId, tenantId);
        }
        return wrapper;
    }
    public static <T extends TenantField> LambdaQueryWrapper<T> buildWithTenant(T entity){
        LambdaQueryWrapper<T> wrapper=new LambdaQueryWrapper<>();
        String tenantId = MultiTenantUtil.getCurrentTenantId();
        if (MultiTenantUtil.strictTenantMode()&&!StringUtils.isEmpty(tenantId)) {
            wrapper.eq(TenantField::getTenantId, tenantId);
        }
        return wrapper;
    }
    public static <T> LambdaQueryWrapper<T> buildWithTenant( SFunction<T, ?> tenantColumn){
        String tenantId = MultiTenantUtil.getCurrentTenantId();
        LambdaQueryWrapper<T> wrapper=new LambdaQueryWrapper<>();
        if (MultiTenantUtil.strictTenantMode()&&!StringUtils.isEmpty(tenantId)) {
            wrapper.eq(tenantColumn, tenantId);
        }
        return wrapper;
    }
}
