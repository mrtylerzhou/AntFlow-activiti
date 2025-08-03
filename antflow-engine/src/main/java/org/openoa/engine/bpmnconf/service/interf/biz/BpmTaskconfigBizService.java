package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmTaskconfig;
import org.openoa.engine.bpmnconf.mapper.BpmTaskconfigMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmTaskconfigService;

public interface BpmTaskconfigBizService extends BizService<BpmTaskconfigMapper, BpmTaskconfigService, BpmTaskconfig>{
    String findTargeNode(String taskId);
}
