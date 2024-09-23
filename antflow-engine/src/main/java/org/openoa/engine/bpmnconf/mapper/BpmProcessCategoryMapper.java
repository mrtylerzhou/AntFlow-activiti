package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.engine.bpmnconf.confentity.BpmProcessCategory;
import org.openoa.engine.vo.BpmProcessCategoryVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface BpmProcessCategoryMapper extends BaseMapper<BpmProcessCategory> {
    List<BpmProcessCategoryVo> findProcessCategory(BpmProcessCategoryVo vo);
}
