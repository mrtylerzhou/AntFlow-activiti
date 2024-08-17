package org.openoa.engine.bpmnconf.service.biz;

import org.openoa.base.vo.BpmnConfCommonVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

public interface BpmnInsertVariables {

    /**
     * insert variables
     *
     * @param bpmnConfCommonVo
     * @param bpmnStartConditions
     */
    void insertVariables(BpmnConfCommonVo bpmnConfCommonVo, BpmnStartConditionsVo bpmnStartConditions);
}
