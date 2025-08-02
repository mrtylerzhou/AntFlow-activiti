package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.engine.bpmnconf.confentity.BpmnConfNoticeTemplate;
import org.openoa.engine.bpmnconf.confentity.BpmnConfNoticeTemplateDetail;

public interface BpmnConfNoticeTemplateService extends IService<BpmnConfNoticeTemplate> {
    BpmnConfNoticeTemplateDetail getDetailByCodeAndType(String bpmnCode, Integer noticeType);

    Integer insert(String bpmnCode);
}
