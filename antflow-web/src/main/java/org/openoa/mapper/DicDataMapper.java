package org.openoa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.base.vo.BaseKeyValueStruVo;
import org.openoa.entity.DictData;

import java.util.Collections;
import java.util.List;

@Mapper
public interface DicDataMapper extends BaseMapper<DictData> {
    List<DictData> selectLFActiveFormCodes();

    List<DictData> selectLFActiveFormCodePageList(Page page);
}
