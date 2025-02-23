package org.openoa.engine.bpmnconf.activitilistener;

import org.activiti.engine.EngineServices;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.el.FixedValue;
import org.springframework.stereotype.Component;

@Component
public class BpmnFlowListener implements ExecutionListener {
    private FixedValue extraInfo; // 使用 FixedValue 而不是 String

    public void setExtraInfo(FixedValue extraInfo) {
        this.extraInfo = extraInfo;
    }


    @Override
    public void notify(DelegateExecution execution) throws Exception {
        EngineServices engineServices = execution.getEngineServices();

    }
}
