package org.openoa.base.interf;

import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

/**
 * @author AntFlow
 * @since 0.5
 */
public interface ConditionService {

    /**
     * to check whether the conditions are met,and the conditions are connected by AND
     *
     * @param conditionsConf
     * @param bpmnStartConditionsVo
     * @return
     */
    boolean checkMatchCondition(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo);
}
