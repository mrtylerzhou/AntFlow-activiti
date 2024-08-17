package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.engine.bpmnconf.confentity.InformationTemplate;
import org.openoa.base.vo.InformationTemplateVo;

import java.util.List;

@Mapper
public interface InformationTemplateMapper extends BaseMapper<InformationTemplate> {


    List<InformationTemplateVo> pageList(Page page, InformationTemplateVo vo);

}
