package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.OutSideBpmConditionsTemplate;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmConditionsTemplateMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.OutSideBpmConditionsTemplateService;
import org.openoa.engine.vo.OutSideBpmConditionsTemplateVo;

import java.util.List;

public interface OutSideBpmConditionsTemplateBizService extends BizService<OutSideBpmConditionsTemplateMapper, OutSideBpmConditionsTemplateService, OutSideBpmConditionsTemplate> {
    ResultAndPage<OutSideBpmConditionsTemplateVo> listPage(PageDto pageDto, OutSideBpmConditionsTemplateVo vo);

    List<OutSideBpmConditionsTemplateVo> selectListByPartMark(String businessPartyMark);

    OutSideBpmConditionsTemplateVo detail(Integer id);

    void delete(Integer id);
}
