package org.openoa.engine.bpmnconf.service.interf.repository;

import org.openoa.base.entity.BpmnConfLfFormdataField;
import org.openoa.base.interf.IAFService;
import org.openoa.engine.bpmnconf.mapper.BpmnConfLfFormdataFieldMapper;

import java.util.Map;

public interface BpmnConfLfFormdataFieldService extends IAFService<BpmnConfLfFormdataFieldMapper, BpmnConfLfFormdataField> {
    Map<String,BpmnConfLfFormdataField> qryFormDataFieldMap(Long confId);

    /**
     * 按表单版本id查询字段配置(外部表单模式使用)
     */
    Map<String,BpmnConfLfFormdataField> qryFieldMapByFormdataId(Long formdataId);
}
