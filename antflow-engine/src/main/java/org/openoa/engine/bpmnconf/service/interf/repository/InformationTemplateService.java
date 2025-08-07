package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.InformationTemplate;
import org.openoa.base.vo.InformationTemplateVo;
import org.openoa.base.vo.ResultAndPage;


public interface InformationTemplateService extends IService<InformationTemplate> {
    ResultAndPage<InformationTemplateVo> list(PageDto pageDto, InformationTemplateVo informationTemplateVo);

    InformationTemplateVo getInformationTemplateById(Long templateId);
}
