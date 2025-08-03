package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmVariableSignUp;
import org.openoa.base.entity.BpmVariableSignUpPersonnel;
import org.openoa.base.service.BpmVariableSignUpPersonnelService;
import org.openoa.engine.bpmnconf.mapper.BpmVariableSignUpMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVariableSignUpService;

import java.util.List;

public interface BpmVariableSignUpBizService extends BizService<BpmVariableSignUpMapper, BpmVariableSignUpService, BpmVariableSignUp>{
    Boolean checkNodeIsSignUp(String processNumber, String nodeId);

    List<BpmVariableSignUp> getSignUpList(String processNumber);
}
