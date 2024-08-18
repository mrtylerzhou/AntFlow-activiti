package org.openoa.engine.bpmnconf.adp.formatter;

import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

/**
 * @author AntFlow
 * @since 0.5
 */

public interface BpmnStartFormat {

    /**
     * format confvo nodes by start conditions
     * @param bpmnConfVo
     * @param bpmnStartConditions
     * @return
     */
    void formatBpmnConf(BpmnConfVo bpmnConfVo, BpmnStartConditionsVo bpmnStartConditions);

}
