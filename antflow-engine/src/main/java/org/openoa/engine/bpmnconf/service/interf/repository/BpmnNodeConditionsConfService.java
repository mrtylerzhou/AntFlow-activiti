package org.openoa.engine.bpmnconf.service.interf.repository;

import org.openoa.base.vo.BusinessDataVo;

import java.util.List;

public interface BpmnNodeConditionsConfService {
    List<String> queryConditionParamNameByProcessNumber(BusinessDataVo businessDataVo);
}
