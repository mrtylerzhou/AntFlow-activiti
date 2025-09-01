package org.openoa.engine.bpmnconf.service.interf.repository;

import org.openoa.base.entity.BpmnConfLfFormdataField;
import org.openoa.base.interf.IAFService;
import org.openoa.engine.bpmnconf.mapper.BpmnConfLfFormdataFieldMapper;

import java.util.Map;

public interface BpmnConfLfFormdataFieldService extends IAFService<BpmnConfLfFormdataFieldMapper, BpmnConfLfFormdataField> {
    Map<String,BpmnConfLfFormdataField> qryFormDataFieldMap(Long confId);
}
