package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.InformationTemplate;
import org.openoa.base.vo.DefaultTemplateVo;
import org.openoa.base.vo.InformationTemplateVo;
import org.openoa.engine.bpmnconf.mapper.InformationTemplateMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.InformationTemplateService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InformationTemplateBizService extends BizService<InformationTemplateMapper, InformationTemplateService, InformationTemplate>{
    @Transactional
    long edit(InformationTemplateVo informationTemplateVo);

    List<DefaultTemplateVo> getList();

    void setList(List<DefaultTemplateVo> vos);
}
