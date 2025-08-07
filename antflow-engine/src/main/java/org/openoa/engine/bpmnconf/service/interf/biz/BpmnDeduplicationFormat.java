package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

public interface BpmnDeduplicationFormat {

    /**
     * 向前去重
     *
     * @param bpmnConfVo
     * @param bpmnStartConditions
     * @return
     */
    BpmnConfVo forwardDeduplication(BpmnConfVo bpmnConfVo, BpmnStartConditionsVo bpmnStartConditions);

    /**
     * 向后去重
     *
     * @param bpmnConfVo
     * @param bpmnStartConditions
     * @return
     */
    BpmnConfVo backwardDeduplication(BpmnConfVo bpmnConfVo, BpmnStartConditionsVo bpmnStartConditions);

}