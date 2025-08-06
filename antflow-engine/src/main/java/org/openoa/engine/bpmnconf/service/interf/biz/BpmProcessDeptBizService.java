package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmProcessDept;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.engine.bpmnconf.mapper.BpmProcessDeptMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessDeptService;

import java.util.List;

public interface BpmProcessDeptBizService extends BizService<BpmProcessDeptMapper, BpmProcessDeptService, BpmProcessDept>{
    void editRelevance(BpmnConf bpmnConf);

    List<String> findProcessKey();

    void editProcessConf(BpmProcessDeptVo vo) throws AFBizException;
}
