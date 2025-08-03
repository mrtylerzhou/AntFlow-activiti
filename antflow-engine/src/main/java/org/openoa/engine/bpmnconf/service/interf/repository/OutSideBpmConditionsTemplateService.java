package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.OutSideBpmConditionsTemplate;
import org.openoa.engine.vo.OutSideBpmConditionsTemplateVo;

import java.util.List;

public interface OutSideBpmConditionsTemplateService extends IService<OutSideBpmConditionsTemplate> {
    List<OutSideBpmConditionsTemplateVo> selectConditionListByAppId(Integer appId);

    void edit(OutSideBpmConditionsTemplateVo vo);
}
