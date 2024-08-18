package org.openoa.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openoa.entity.BizDemo;

import static org.openoa.base.constant.StringConstants.DB_NAME_2;

@DS(DB_NAME_2)
public interface BizDemoMapper extends BaseMapper<BizDemo> {
}
