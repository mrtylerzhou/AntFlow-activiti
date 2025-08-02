package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.base.entity.BpmProcessCategory;
import org.openoa.engine.vo.BpmProcessCategoryVo;

import java.util.List;

@Mapper
public interface BpmProcessCategoryMapper extends BaseMapper<BpmProcessCategory> {
    List<BpmProcessCategoryVo> findProcessCategory(BpmProcessCategoryVo vo);
}
