package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmVariable;
import org.openoa.base.service.BpmVariableService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMapper;

import java.util.List;

public interface BpmVariableBizService extends BizService<BpmVariableMapper, BpmVariableService, BpmVariable>{
    Boolean checkIsInProcess(String formCode, Integer businessId, Integer loginEmplId, String loginUsername);

    List<BaseIdTranStruVo> getApproversByNodeId(String processNumber, String nodeId);

    List<BaseIdTranStruVo> getApproversByElementId(String processNumber, String elementId);
    void deleteByProcessNumber(String processNumber);
}
