package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmProcessApplicationType;
import org.openoa.engine.bpmnconf.mapper.BpmProcessApplicationTypeMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessApplicationTypeService;
import org.openoa.engine.vo.BpmProcessApplicationTypeVo;

public interface BpmProcessApplicationTypeBizService extends BizService<BpmProcessApplicationTypeMapper, BpmProcessApplicationTypeService, BpmProcessApplicationType>{
    boolean moveUp(Long id);

    boolean asCommonlyUsed(Long processTypeId, Integer id, boolean isCancel, Integer type);

    boolean iconOperation(BpmProcessApplicationTypeVo vo);

    boolean delete(Long id);
}
