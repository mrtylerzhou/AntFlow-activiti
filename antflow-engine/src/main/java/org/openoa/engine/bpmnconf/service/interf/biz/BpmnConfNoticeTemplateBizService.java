package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmnConfNoticeTemplate;
import org.openoa.base.entity.BpmnConfNoticeTemplateDetail;
import org.openoa.engine.bpmnconf.mapper.BpmnConfNoticeTemplateMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfNoticeTemplateService;

public interface BpmnConfNoticeTemplateBizService extends  BizService<BpmnConfNoticeTemplateMapper, BpmnConfNoticeTemplateService, BpmnConfNoticeTemplate> {
    BpmnConfNoticeTemplateDetail getDetailByCodeAndType(String bpmnCode, Integer noticeType);

    Integer insert(String bpmnCode);
}
