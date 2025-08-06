package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmProcessName;
import org.openoa.base.entity.BpmnConf;
import org.openoa.engine.bpmnconf.mapper.BpmProcessNameMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNameService;

public interface BpmProcessNameBizService extends BizService<BpmProcessNameMapper, BpmProcessNameService, BpmProcessName>{
    void editProcessName(BpmnConf bpmnConfByCode);

    BpmProcessName getBpmProcessName(String processKey);
}
