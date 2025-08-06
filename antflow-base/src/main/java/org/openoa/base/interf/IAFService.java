package org.openoa.base.interf;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IAFService<M extends BaseMapper<T>,T> extends IService<T> {
    default M getMapper(){
        return (M)getBaseMapper();
    }
}
