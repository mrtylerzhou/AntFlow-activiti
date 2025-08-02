package org.openoa.engine.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.openoa.base.interf.TenantField;

public class AF {
    //无它,仅仅是QueryWrapperUtil.addTenantCondition的包装,因为用到的地方比较多,名字太长影响可读性,WT意为:wrap tenant
    //需要说明的是TenantField扩展了IsDelField,并且方法除了拼租户字段判断,还会自动拼上is_del=0
    public static <T extends TenantField>LambdaQueryWrapper<T> WT(LambdaQueryWrapper<T> wrapper){
        return QueryWrapperUtil.addTenantCondition(wrapper);
    }
}
