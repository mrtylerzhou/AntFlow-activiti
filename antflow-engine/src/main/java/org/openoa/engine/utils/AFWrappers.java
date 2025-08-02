package org.openoa.engine.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.interf.TenantField;

public class AFWrappers {
    public static <T extends TenantField> LambdaQueryWrapper<T> lambdaTenantQuery() {
        QueryWrapper<T> tenantFieldQueryWrapper = tenantQuery();
        return tenantFieldQueryWrapper.lambda();
    }
    public static <T extends TenantField> QueryWrapper<T> tenantQuery() {
        QueryWrapper<T> queryWrapper=new QueryWrapper<>();
        String tenantId = MultiTenantUtil.getCurrentTenantId();
        queryWrapper.eq("is_del",0);
        if (MultiTenantUtil.strictTenantMode()&&!StringUtils.isEmpty(tenantId)) {
            queryWrapper.eq("tenant_id",tenantId);
        }
        return queryWrapper;
    }
}
