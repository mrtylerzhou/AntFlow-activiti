package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.DefaultTemplate;
import org.openoa.base.interf.IAFService;
import org.openoa.engine.bpmnconf.mapper.DefaultTemplateMapper;

import java.util.List;

public interface DefaultTemplateService extends IAFService<DefaultTemplateMapper,DefaultTemplate> {
    void insertOrUpdateAllColumnBatch(List<DefaultTemplate> list);
}
