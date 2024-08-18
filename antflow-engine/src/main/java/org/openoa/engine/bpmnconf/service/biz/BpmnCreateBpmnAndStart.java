package org.openoa.engine.bpmnconf.service.biz;

import org.openoa.base.vo.BpmnConfCommonVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

public interface BpmnCreateBpmnAndStart {

    /**
     * create and start a process
     *
     * @param bpmnConfCommonVo
     * @param bpmnStartConditions
     */
    void createBpmnAndStart(BpmnConfCommonVo bpmnConfCommonVo, BpmnStartConditionsVo bpmnStartConditions);
}
