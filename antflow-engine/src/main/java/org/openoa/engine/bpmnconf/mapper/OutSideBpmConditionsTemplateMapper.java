package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmConditionsTemplate;
import org.openoa.engine.vo.OutSideBpmConditionsTemplateVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutSideBpmConditionsTemplateMapper extends BaseMapper<OutSideBpmConditionsTemplate> {

    List<OutSideBpmConditionsTemplateVo> selectPageList(Page page, OutSideBpmConditionsTemplateVo vo);

}
