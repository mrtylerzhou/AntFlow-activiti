package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.base.entity.OutSideBpmConditionsTemplate;
import org.openoa.engine.vo.OutSideBpmConditionsTemplateVo;

import java.util.List;

@Mapper
public interface OutSideBpmConditionsTemplateMapper extends BaseMapper<OutSideBpmConditionsTemplate> {

    List<OutSideBpmConditionsTemplateVo> selectPageList(Page page, OutSideBpmConditionsTemplateVo vo);

}
