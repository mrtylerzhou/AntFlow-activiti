package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmBusinessDraft;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.mapper.BpmBusinessDraftMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmBusinessDraftService;

public interface BpmProcessDraftBizService extends BizService<BpmBusinessDraftMapper, BpmBusinessDraftService, BpmBusinessDraft>{
    void saveBusinessDraft(BusinessDataVo businessDataVo);

    BusinessDataVo loadDraft(String formCode, String userId);
}
