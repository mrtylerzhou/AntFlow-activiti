package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmnNode;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;

public interface BpmnNodeBizService extends BizService<BpmnNodeMapper, BpmnNodeService, BpmnNode> {
    BpmnNodeVo getBpmnNodeVoByTaskDefKey(String processNumber, String taskDefKey);

    BpmnNodeVo getBpmnNodeVoByNodeId(String processNumber, String nodeId);
}
