package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.jsonconf.VariableConfigJson.SignUpItem;

import java.util.List;

public interface BpmVariableSignUpBizService {
    Boolean checkNodeIsSignUp(String processNumber, String nodeId);

    List<SignUpItem> getSignUpList(String processNumber);
}
